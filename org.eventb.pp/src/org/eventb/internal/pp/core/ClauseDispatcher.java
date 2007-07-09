package org.eventb.internal.pp.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.search.ResetIterator;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.core.tracing.Tracer;
import org.eventb.pp.ITracer;
import org.eventb.pp.PPResult;
import org.eventb.pp.PPResult.Result;

public class ClauseDispatcher implements IDispatcher {
	
	private Level level = Level.base;
	
	/**
	 * Debug flag for <code>PROVER_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
		System.out.println(message);
	}
	
	// the tracer
	private Tracer tracer;
	// the dumper
	private Dumper dumper;
	
	// the provers
	private IProver casesplitter;
	private IProver predicateprover;
	private IProver seedsearch;
	private IProver equality;
	
	private ClauseSimplifier simplifier;
	
	public ClauseDispatcher() {
		level = Level.base;
		
//		dsWrapper = new DataStructureWrapper(new IterableHashSet<Clause>());
		alreadyDispatchedClauses = new IterableHashSet<Clause>();
		alreadyDispatchedBacktrackClausesIterator = alreadyDispatchedClauses.iterator();
		nonDispatchedClauses = new IterableHashSet<Clause>();
		nonDispatchedClausesIterator = nonDispatchedClauses.iterator();
		nonDispatchedBacktrackClausesIterator = nonDispatchedClauses.iterator();
		
		dumper = new Dumper();
		tracer = new Tracer();
//		dumper.addDataStructure("Non unit clauses", clauses.iterator());
		
		simplifier = new ClauseSimplifier();
	}
	
	private Collection<Clause> originalClauses;

	public void setPredicateProver(IProver predicateprover) {
		predicateprover.initialize(simplifier);
		predicateprover.registerDumper(dumper);
		this.predicateprover = predicateprover;
	}
	
	public void setCaseSplitter(IProver casesplitter) {
		casesplitter.initialize(simplifier);
		casesplitter.registerDumper(dumper);
		this.casesplitter = casesplitter;
	}
	
	public void setSeedSearch(IProver seedsearch) {
		seedsearch.initialize(simplifier);
		seedsearch.registerDumper(dumper);
		this.seedsearch = seedsearch;
	}
	
	public void setEqualityProver(IProver equality) {
		equality.initialize(simplifier);
		equality.registerDumper(dumper);
		this.equality = equality;
	}
	
	public void setClauses(Collection<Clause> clauses) {
		this.originalClauses = clauses;
	}
	
	public void addSimplifier(ISimplifier simplifier) {
		this.simplifier.addSimplifier(simplifier);
	}
	
//	private IOrigin externalContradictionOrigin = null;
//	public void contradiction(IOrigin origin) {
//		externalContradictionOrigin = origin;
//	}
	
	
	
//	// new clause :
//	// 1 simplify
//	// 2 dispatch to provers
//	public void newClause(Clause clause) {
//		assert clause.getLevel().isAncestorOf(level) || clause.getLevel().equals(level);
//		
//		// simplify
//		clause = simplifier.run(clause);
//		
//		if (clause.isTrue()) return;
//		
//		// dispatch to corresponding prover
//		if (clause.isFalse()) {
//			internalContradiction(clause.getOrigin());
//			return;
//		}
//		
//		// all clauses are put here
//		// this should add the clause in all datastructures of all provers
//		addClauseAndCheckContradiction(clause);
//		// TODO forward subsumption
//		// TODO this code calls back into the mainloop - think of another way
//		// TODO do not do this, let the predicate prover do the instantiations
//		// and call it back like the equality
//		// give to predicate prover
//		
////		if (prover.accepts(clause)) {
////			prover.addOwnClause(clause);
////		}
//
//	}
	
//	private void addClauseAndCheckContradiction(Clause clause) {
//		if (!isSubsumed(clause)) {
//			dsWrapper.add(clause);
//			if (externalContradictionOrigin != null) {
//				internalContradiction(externalContradictionOrigin);
//				externalContradictionOrigin = null;
//			}
//		}
//	}
	
	public ITracer getTracer() {
		return tracer;
	}
	
	private List<IProver> provers;
	
	private IterableHashSet<Clause> alreadyDispatchedClauses;
	private ResetIterator<Clause> alreadyDispatchedBacktrackClausesIterator;
	
	private IterableHashSet<Clause> nonDispatchedClauses;
	private ResetIterator<Clause> nonDispatchedClausesIterator;
	private ResetIterator<Clause> nonDispatchedBacktrackClausesIterator;
	
	public void mainLoop(long nofSteps) throws InterruptedException {
		boolean tryAgain = true;
		provers = new ArrayList<IProver>();
		provers.add(predicateprover);
		provers.add(casesplitter);
		provers.add(seedsearch);
		provers.add(equality);
		counter = 0;
		
		if (DEBUG) debug("=== ClauseDispatcher. Starting ===");
		addOriginalClauses();
		
		while (!terminated) {
			if (Thread.interrupted()) throw new InterruptedException();
			if (!updateCounterAndCheckTermination(nofSteps)) {
				// first phase, treat non dispatched clauses
				if (!treatNondispatchedClausesAndCheckContradiction()) {
					// second phase, all clauses have been treated
					tryAgain = getNextClauseFromProvers(tryAgain);
				}
			}
		}
	}
	
	private void addOriginalClauses() {
		for (Clause clause : originalClauses) {
			clause = simplifier.run(clause);
			if (clause.isTrue()) continue;
			if (clause.isFalse()) internalContradiction(clause.getOrigin());
			else addNonDispatchedClause(clause);
		}
	}
	
	private boolean getNextClauseFromProvers(boolean tryAgain) {
		if (DEBUG) debug("== Getting next clause from provers ==");
		ProverResult nextResult = null;
		for (IProver prover : provers) {
			nextResult = prover.next();
			if (nextResult != null) {
				if (DEBUG) debug("= Got result from "+prover.toString()+": "+nextResult.toString()+" =");
				break;
			}
		}
		if (nextResult == null) {
			if (DEBUG) debug("= Got no result this time =");
			// proof done
			if (!tryAgain) noProofFound();
			return false;
		}
		else {
			removeClauses(nextResult.getSubsumedClauses());
			if (nextResult.isContradiction()) {
				internalContradiction(nextResult.getContradictionOrigin());
			}
			else {
				addNonDispatchedClauses(nextResult.getGeneratedClauses());
			}
			return true;
		}
	}
	
	private boolean treatNondispatchedClausesAndCheckContradiction() {
		if (DEBUG) debug("== Treating non dispatched clauses ==");
		nonDispatchedClausesIterator.reset();
		while (nonDispatchedClausesIterator.hasNext()) {
			Clause clause = nonDispatchedClausesIterator.next();
			assert getLevel().compareTo(clause.getLevel()) >= 0;
			nonDispatchedClauses.remove(clause);
			// necessary ?
			if (!isSubsumedByProvers(clause)) {
				alreadyDispatchedClauses.appends(clause);
				for (IProver prover : provers) {
					if (checkContradictionAndAddNondispatchedClause(clause, prover)) return true;
				}
			}
		}
		return false;
	}
	
	private boolean updateCounterAndCheckTermination(long nofSteps) {
		counter++;
		if (nofSteps > 0 && counter >= nofSteps) {
			noProofFound();
			return true;
		}
		if (DEBUG) debug("=== ClauseDispatcher. Step "+counter+". Level "+level+" ===");
		dumper.dump();
		return false;
	}
	
	private boolean isSubsumedByProvers(Clause clause) {
		for (IProver prover : provers) {
			if (prover.isSubsumed(clause)) return true;
		}
		return false;
	}
	
	private boolean checkContradictionAndAddNondispatchedClause(Clause clause, IProver prover) {
		ProverResult result = prover.addClauseAndDetectContradiction(clause);
		if (result == null) return false;
		if (DEBUG) debug("= Got result from "+prover.toString()+": "+result.toString()+" =");
		removeClauses(result.getSubsumedClauses());
		if (result.isContradiction()) {
			internalContradiction(result.getContradictionOrigin());
			return true;
		}
		addNonDispatchedClauses(result.getGeneratedClauses());
		return false;
	}
	
	private void addNonDispatchedClauses(Set<Clause> clauses) {
		for (Clause generatedClause : clauses) {
			assert getLevel().compareTo(generatedClause.getLevel()) >= 0;
			addNonDispatchedClause(generatedClause);
		}
	}
	
	private void addNonDispatchedClause(Clause clause) {
		if (!clause.isTrue() && !checkAndRemoveAlreadyExistingClause(clause)) {
			// we check if it is there
			if (nonDispatchedClauses.contains(clause)) {
				Clause existingClause = nonDispatchedClauses.get(clause);
				if (clause.getLevel().isAncestorOf(existingClause.getLevel())) {
					// we replace the clause by the new one
					nonDispatchedClauses.remove(clause);
				}
				else return; // clause had a lower level, we forget the new clause
			}
			nonDispatchedClauses.appends(clause);
		}
	}
	
	private boolean checkAndRemoveAlreadyExistingClause(Clause clause) {
		// if clause already exists we compare the levels, if the level of the new clause
		// is higher than the existing one, we do nothing, if not, the existing one
		// must be replaced by the new one
		// this is very important since all prover modules assumes that when a new
		// clause is added, all equal clauses with a higher level have been preliminarily removed
		if (alreadyDispatchedClauses.contains(clause)) {
			Clause existingClause = alreadyDispatchedClauses.get(clause);
			if (clause.getLevel().isAncestorOf(existingClause.getLevel())) {
				// we replace the clause by the new one
				alreadyDispatchedClauses.remove(clause);
				removeClauseFromProvers(clause);
				return false;
			}
			return true;
		}
		return false;
	}
	
	
	private void internalContradiction(IOrigin origin) {
		if (DEBUG) debug("= Contradiction found on: "+origin+" =");
		Level oldLevel = level;
		
		// contradiction has been found, backtrack
		Set<Level> dependencies = new HashSet<Level>();
		origin.getDependencies(dependencies);
		if (DEBUG) debug("= Level dependencies: "+dependencies+" =");
		
		adjustLevel(origin);
		if (terminated) return;
		
		if (DEBUG) debug("= Closing level: "+tracer.getLastClosedLevel()+", old level was: "+oldLevel+", new level is: "+level+" =");
		
		if (DEBUG) debug("= Dispatching contradiction to subprovers =");
		for (IProver prover : provers) {
			prover.contradiction(oldLevel, level, dependencies);
		}	
		
		// we backtrack our own datastructure
		if (DEBUG) debug("= Done dispatching, backtracking datastructures =");
		
		backtrack(level, alreadyDispatchedBacktrackClausesIterator, alreadyDispatchedClauses);
		backtrack(level, nonDispatchedBacktrackClausesIterator, nonDispatchedClauses);
	}
	
	private void backtrack(Level level, ResetIterator<Clause> iterator, 
			IterableHashSet<Clause> iterable) {
		iterator.reset();
		while (iterator.hasNext()) {
			Clause clause = iterator.next();
			if (level.isAncestorOf(clause.getLevel())) {
				iterable.remove(clause);
				removeClauseFromProvers(clause);
			}
		}
	}
	
	private void removeClauses(Set<Clause> subsumedClauses) {
		if (subsumedClauses == null) return;
		for (Clause subsumedClause : subsumedClauses) {
			alreadyDispatchedClauses.remove(subsumedClause);
			removeClauseFromProvers(subsumedClause);
		}
	}
	
	private void removeClauseFromProvers(Clause clause) {
		for (IProver prover : provers) {
			prover.removeClause(clause);
		}
	}
	
	private int counter;
	
	public Level getLevel() {
		return level;
	}
	
	public void nextLevel() {
		if (level.getLeftBranch().equals(tracer.getLastClosedLevel())) level = level.getRightBranch();
		else level = level.getLeftBranch();
	}
	
	private void adjustLevel(IOrigin origin) {
		tracer.addClosingClause(origin);
		if ( tracer.getLastClosedLevel().equals(Level.base)) {
			// proof is done !
			proofFound();
			level = Level.base;
		}
		else {
			// main loop on the next case
			level =  tracer.getLastClosedLevel().getParent();
		}
	}
	
	private boolean terminated = false;
	private PPResult result;
	
	public PPResult getResult() {
		return result;
	}

	private void noProofFound() {
		result = new PPResult(Result.invalid, null);
		terminated = true;
	}
	
	private void proofFound() {
		result = new PPResult(Result.valid, tracer);
		terminated = true;
	}

	public boolean contains(Clause clause) {
		if (nonDispatchedClauses.contains(clause)) {
			Clause existingClause = nonDispatchedClauses.get(clause);
			if (existingClause.equalsWithLevel(clause)) return true;
		}
		return false;
	}

}
