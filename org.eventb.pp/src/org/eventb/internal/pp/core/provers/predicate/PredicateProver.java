package org.eventb.internal.pp.core.provers.predicate;


import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IDispatcher;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.datastructure.DefaultChangeListener;
import org.eventb.internal.pp.core.datastructure.IObservable;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.inferrers.InferrenceResult;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.search.ResetIterator;

public class PredicateProver extends DefaultChangeListener implements IProver {

	/**
	 * Debug flag for <code>PROVER_CASESPLIT_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
		if (DEBUG)
			System.out.println(message);
	}
	
	private IterableHashSet<IClause> unitClauses;
	private IterableHashSet<IClause> nonUnitClauses;
	
	private ResolutionInferrer inferrer;
	private ResolutionResolver resolver;
	
	private IDispatcher dispatcher;
	private UnitInferenceIterator unitProver;
	private NonUnitInferenceIterator nonUnitProver;
	
	public PredicateProver(IVariableContext context) {
		this.inferrer = new ResolutionInferrer(context);
		this.resolver = new ResolutionResolver(inferrer);
		
		unitClauses = new IterableHashSet<IClause>();
		nonUnitClauses = new IterableHashSet<IClause>();
		
		unitProver = new UnitInferenceIterator();
		nonUnitProver = new NonUnitInferenceIterator(unitClauses.iterator(),nonUnitClauses.iterator());
	
		unitClausesIterator = unitClauses.iterator();
		nonUnitClausesIterator = nonUnitClauses.iterator();
	}
	
	public void initialize(IDispatcher dispatcher, IObservable clauses) {
		this.dispatcher = dispatcher;
		clauses.addChangeListener(this);
	}
	
	private IClause blockedClause = null;
	
	public boolean isBlocked() {
		return blockedClause != null;
	}
	
	public IClause next() {
		IClause result = null;
		if (isBlocked()) {
			debug("Unblocking clause: "+blockedClause);
			result = blockedClause;
			blockedClause = null;
			unblock();
		}
		else {
			// TODO clean here
			InferrenceResult nextClause = resolver.next();
			if (nextClause == null) {
				if (nonUnitProver.next()) {
					resolver.initialize(nonUnitProver.getMatchingUnit(), nonUnitProver.getMatchingNonUnit());
					nextClause = resolver.next();
				}
			}
			if (nextClause == null) {}
			else {
				PredicateProver.debug("Inferred clause: "+nonUnitProver.getMatchingUnit()+" + "+nonUnitProver.getMatchingNonUnit()+" -> "+nextClause.getClause());
				if (handleBlocking(nextClause)) {
					debug("Clause blocked, returning null");
				}
				else {
					result = nextClause.getClause();
				}
			}
		}
		return result;
	}
	
	private boolean handleBlocking(InferrenceResult newClause) {
		if (newClause.isBlocked()) {
			blockedClause = newClause.getClause();
			return true;
		}
		return false;
	}
	
	private ResetIterator<IClause> unitClausesIterator;
	private ResetIterator<IClause> nonUnitClausesIterator;
	private void unblock() {
		unitClausesIterator.reset();
		while (unitClausesIterator.hasNext()) {
			unitClausesIterator.next().reset();
		}
		nonUnitClausesIterator.reset();
		while (nonUnitClausesIterator.hasNext()) {
			nonUnitClausesIterator.next().reset();
		}
	}
	
	public void newUnitClause(IClause clause) {
		unitProver.initialize(clause);
		IClause newClause;
		
		// TODO the main prover might have backtracked on a contradiction coming from here
		// check if it is the case + document
		while (	(newClause = unitProver.next()) != null
				&& !dispatcher.hasStopped() && (Level.getHighest(dispatcher.getLevel(), clause.getLevel()).equals(dispatcher.getLevel()))) {
			inferrer.setUnitClause(newClause);
			inferrer.setPosition(0);
			if (inferrer.canInfer(clause)) {
				clause.infer(inferrer);
				InferrenceResult result = inferrer.getResult();
				// TODO can we block on unit clause inferrences ?

				PredicateProver.debug("Unit-unit inference: "+clause+" + "+newClause+" -> "+result.getClause());
				dispatcher.newClause(result.getClause());
			}
		}
	}
	
	public boolean accepts(IClause clause) {
		return clause.isUnit() && clause.getPredicateLiterals().size() > 0
		&& !clause.getPredicateLiterals().get(0).isQuantified();
	}
	
	public void addOwnClause(IClause clause) {
		assert dispatcher != null;
		
		newUnitClause(clause);
	}

	@Override
	public void newClause(IClause clause) {
		if (accepts(clause)) {
			unitClauses.appends(clause);
			unitProver.newClause(clause);
			nonUnitProver.newClause(clause);
		}
		else if (clause.getPredicateLiterals().size()>0 && !clause.isUnit()) {
			nonUnitClauses.appends(clause);
			nonUnitProver.newClause(clause);
		}
	}

	@Override
	public void removeClause(IClause clause) {
		if (blockedClause == clause) blockedClause = null;
		
		if (accepts(clause)) {
			unitClauses.remove(clause);
			unitProver.removeClause(clause);
			nonUnitProver.removeClause(clause);
		}
		else if (clause.getPredicateLiterals().size()>0 && !clause.isUnit()) {
			nonUnitClauses.remove(clause);
			nonUnitProver.removeClause(clause);
		}
		
		removeFromResolver(clause);
	}
	
	private void removeFromResolver(IClause clause) {
		resolver.removeClause(clause);
	}
	
	public void contradiction(Level oldLevel, Level newLevel, boolean proofFound) {
		// the blocked clauses are not in the search space of the main prover, so
		// it is important to verify here that they can still exist
		if (blockedClause != null && !Level.getHighest(blockedClause.getLevel(), newLevel).equals(newLevel)) {
			blockedClause = null;
		}
	}

	public void registerDumper(Dumper dumper) {
		dumper.addDataStructure("Predicate unit clauses", unitClauses.iterator());
		dumper.addDataStructure("Predicate non-unit clauses", nonUnitClauses.iterator());
	}

}


