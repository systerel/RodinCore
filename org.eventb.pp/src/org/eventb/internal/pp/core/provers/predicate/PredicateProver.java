package org.eventb.internal.pp.core.provers.predicate;


import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.datastructure.DataStructureWrapper;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.inferrers.InferrenceResult;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.search.ResetIterator;
import org.eventb.internal.pp.core.tracing.AbstractInferrenceOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class PredicateProver implements IProver {

	/**
	 * Debug flag for <code>PROVER_CASESPLIT_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
		System.out.println(message);
	}
	
	private DataStructureWrapper unitClausesWrapper;
	private IterableHashSet<Clause> unitClauses;
	private ResetIterator<Clause> unitClausesIterator;
	private IterableHashSet<Clause> nonUnitClauses;
	
	private ResolutionInferrer inferrer;
	private ResolutionResolver nonUnitResolver;
	private ResolutionResolver unitResolver;
	private ReverseResolutionResolver conditionResolver;
	
	private ClauseSimplifier simplifier;
	private UnitMatcher unitMatcher;
	
	public PredicateProver(IVariableContext context) {
		this.inferrer = new ResolutionInferrer(context);
		
		unitClauses = new IterableHashSet<Clause>();
		// TODO remove
		unitClausesWrapper = new DataStructureWrapper(unitClauses);
		nonUnitClauses = new IterableHashSet<Clause>();
		
		unitMatcher = new UnitMatcher(unitClausesWrapper);
		
		nonUnitResolver = new ResolutionResolver(inferrer, new IteratorMatchIterator(nonUnitClauses.iterator()));
		unitResolver = new ResolutionResolver(inferrer, new UnitMatchIterator(unitMatcher));
		conditionResolver = new ReverseResolutionResolver(inferrer, new UnitMatchIterator(unitMatcher));
		
		unitClausesIterator = unitClauses.iterator();
	}
	
	public void initialize(ClauseSimplifier simplifier) {
		this.simplifier = simplifier;
	}
	
//	private Clause blockedClause = null;
	
//	public boolean isBlocked() {
//		return blockedClause != null;
//	}
	
	public ProverResult next(boolean force) {
		// TODO refactor this 
		
		if (simplifier == null) throw new IllegalStateException();
		
		ProverResult result = null;
//		if (isBlocked()) {
//			if (DEBUG) debug("Unblocking clause: "+blockedClause);
//			result = new ProverResult(blockedClause);
//			blockedClause = null;
//		}
//		else {
			if (!nonUnitResolver.isInitialized()) {
				Clause unit = nextUnit();
				if (unit == null) return ProverResult.EMPTY_RESULT;
				nonUnitResolver.initialize(unit);
			}
			InferrenceResult nextClause = nonUnitResolver.next(force);
			while (nextClause == null) {
				Clause unit = nextUnit();
				if (unit == null) return ProverResult.EMPTY_RESULT;
				else newClause(unit, unitResolver);
				nonUnitResolver.initialize(unit);
				nextClause = nonUnitResolver.next(force);
			}
			if (nextClause != null) {
				if (nextClause.isBlocked()) return ProverResult.EMPTY_RESULT;
				
				Clause clause = nextClause.getClause();
				clause = simplifier.run(clause);
//				if (clause.isFalse()) {
//					result = new ProverResult(clause.getOrigin());
//				}
//				else if (clause.checkIsBlockedOnInstantiationsAndUnblock()) blockedClause = clause;
				result = new ProverResult(clause, nextClause.getSubsumedClauses());
			}
//		}
		if (DEBUG) debug("PredicateProver, next clause: "+result);
		return result;
	}
	
	private Clause nextUnit() {
		if (unitClausesIterator.hasNext()) return unitClausesIterator.next();
		return null;
	}
	
	public ProverResult newClause(Clause clause, IResolver resolver) {
		Set<Clause> generatedClauses = new HashSet<Clause>();
		Set<Clause> subsumedClauses = new HashSet<Clause>();
		
		resolver.initialize(clause);
		InferrenceResult result = resolver.next(true);
		while (result != null) {
			assert !result.isBlocked();
			
			subsumedClauses.addAll(result.getSubsumedClauses());
			
			Clause inferredClause = result.getClause();
			inferredClause = simplifier.run(inferredClause);
//			if (inferredClause.isFalse()) {
//				// we can stop here because all subsequent clauses will be lost
//				return new ProverResult(inferredClause.getOrigin(), subsumedClauses);
//			}
//			if (!inferredClause.isTrue()) {
				generatedClauses.add(inferredClause);
//			}
			
			result = resolver.next(true);
		}
		return new ProverResult(generatedClauses, subsumedClauses);
	}
	
	
	private boolean isAcceptedUnitClause(Clause clause) {
		return clause.isUnit() && clause.getPredicateLiterals().size() > 0
		&& !clause.hasQuantifiedLiteral();
	}
	
	private boolean isAcceptedNonUnitClause(Clause clause) {
		 return	clause.getPredicateLiterals().size()>0 
				&& !clause.isUnit()
				&& !clause.isBlockedOnConditions();
//				&& !clause.hasQuantifiedLiteral();
	}
	
	
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		if (simplifier == null) throw new IllegalStateException();
		
		if (isAcceptedUnitClause(clause)) {
			unitClausesWrapper.add(clause);
			// we generate the clauses
			return newClause(clause, unitResolver);
		}
		else if (isAcceptedNonUnitClause(clause)) {
			nonUnitClauses.appends(clause);
			if (hadConditions(clause) /* || hadQuantifier(clause) */) return newClause(clause, conditionResolver);
		}
		return ProverResult.EMPTY_RESULT;
	}

	public void removeClause(Clause clause) {
		if (isAcceptedUnitClause(clause)) {
			unitClausesWrapper.remove(clause);
		}
		else if (isAcceptedNonUnitClause(clause)) {
			nonUnitClauses.remove(clause);
		}
		nonUnitResolver.remove(clause);
		unitResolver.remove(clause);
	}
	
//	private boolean hadQuantifier(Clause clause) {
//		IOrigin origin = clause.getOrigin();
//		if (origin instanceof AbstractInferrenceOrigin) {
//			AbstractInferrenceOrigin tmp = (AbstractInferrenceOrigin)origin;
//			for (Clause parent : tmp.getClauses()) {
//				if (parent.hasQuantifiedLiteral()) return true;
//			}
//		}
//		return false;
//	}

	// TODO dirty
	private boolean hadConditions(Clause clause) {
		IOrigin origin = clause.getOrigin();
		if (origin instanceof AbstractInferrenceOrigin) {
			AbstractInferrenceOrigin tmp = (AbstractInferrenceOrigin)origin;
			for (Clause parent : tmp.getClauses()) {
				if (parent.isBlockedOnConditions()) return true;
			}
		}
		return false;
	}

	public void contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies) {
		// the blocked clauses are not in the search space of the main prover, so
		// it is important to verify here that they can still exist
//		if (blockedClause != null && newLevel.isAncestorOf(blockedClause.getLevel())) {
//			blockedClause = null;
//		}
		
//		// TODO check if necessary
//		backtrackIterator.reset();
//		while (backtrackIterator.hasNext()) {
//			Clause clause = backtrackIterator.next();
//			if (dispatcher.getLevel().isAncestorOf(clause.getLevel())) generatedClauses.remove(clause);
//		}
	}

	public void registerDumper(Dumper dumper) {
		dumper.addDataStructure("PredicateFormula unit clauses", unitClauses.iterator());
		dumper.addObject("Current unit clause", new Object(){
			@SuppressWarnings("synthetic-access")
			@Override
			public String toString() {
				return unitClausesIterator.current()==null?"no current unit clause":unitClausesIterator.current().toString();
			}
		});
		dumper.addDataStructure("PredicateFormula non-unit clauses", nonUnitClauses.iterator());
	}
	
	@Override
	public String toString() {
		return "PredicateProver";
	}
	
	
}


