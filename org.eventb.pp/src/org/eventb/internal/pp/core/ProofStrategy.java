package org.eventb.internal.pp.core;

import java.util.Collection;
import java.util.Stack;

import org.eventb.internal.pp.core.datastructure.DataStructureWrapper;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.core.tracing.Tracer;
import org.eventb.pp.ITracer;
import org.eventb.pp.PPResult;
import org.eventb.pp.PPResult.Result;

public class ProofStrategy implements IDispatcher {
	
	private Level level = Level.base;
	
	/**
	 * Debug flag for <code>PROVER_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
		if (DEBUG)
			System.out.println(message);
	}
	
	// the tracer
	private Tracer tracer;
	// the dumper
	private Dumper dumper;
	
	// for now, managed here
	private DataStructureWrapper dsWrapper;
	
	// these are provers in some sense
	private IProver casesplitter;
	private IProver prover;
	private IProver seedsearch;
	private IProver equality;
	
	private ClauseSimplifier simplifier;
	
	private boolean stop, proofFound;
	
	public ProofStrategy() {
		// TODO change
		Constant.uniqueIdentifier = 0;
		level = Level.base;
		
		dsWrapper = new DataStructureWrapper(new IterableHashSet<IClause>());
		
		dumper = new Dumper();
		tracer = new Tracer();
//		dumper.addDataStructure("Non unit clauses", clauses.iterator());
		
		simplifier = new ClauseSimplifier();
	}
	
	private Collection<IClause> originalClauses;

	public void setPredicateProver(IProver prover) {
		prover.initialize(this, dsWrapper, simplifier);
		prover.registerDumper(dumper);
		this.prover = prover;
	}
	
	public void setCaseSplitter(IProver casesplitter) {
		casesplitter.initialize(this, dsWrapper, simplifier);
		casesplitter.registerDumper(dumper);
		this.casesplitter = casesplitter;
	}
	
	public void setSeedSearch(IProver seedsearch) {
		seedsearch.initialize(this, dsWrapper, simplifier);
		seedsearch.registerDumper(dumper);
		this.seedsearch = seedsearch;
	}
	
	public void setEqualityProver(IProver equality) {
		equality.initialize(this, dsWrapper, simplifier);
		equality.registerDumper(dumper);
		this.equality = equality;
	}
	
	public void setClauses(Collection<IClause> clauses) {
		this.originalClauses = clauses;
	}
	
	public void addSimplifier(ISimplifier simplifier) {
		this.simplifier.addSimplifier(simplifier);
	}
	
	public void contradiction(IOrigin origin) {
		debug("Contradiction found on: "+origin);
		tracer.addClosingClause(origin);
		
		Level oldLevel = level;
		
		// contradiction has been found, backtrack
		Stack<Level> dependencies = new Stack<Level>();
		origin.getDependencies(dependencies);
		
		adjustLevel(dependencies);
		if (proofFound) return;
		
		debug("Old level was: "+oldLevel+", new level is: "+level);
		
		debug("Dispatching contradiction to subprovers");
		// tell the predicate prover
		prover.contradiction(oldLevel, level, proofFound, dependencies);
		// tell the case splitter
		// TODO not stop
		casesplitter.contradiction(oldLevel, level, proofFound, dependencies);
		// TODO tell the arithmetic prover
		equality.contradiction(oldLevel, level, proofFound, dependencies);
		
		// we backtrack our own datastructure
		debug("ProofStrategy: Backtracking datastructures");
		dsWrapper.backtrack(level);
	}
	
	private boolean isSubsumed(IClause clause) {
		// TODO cleaner ! check for subsumption, this is a HACK !
		// @see{newClause(IClause clause)}
		// TODO we should check here that the clause does not exist yet.
		// If it does, we must compare the levels, if the level of the new clause
		// is higher than the existing one, we do nothing, if not, the existing one
		// must be replaced by the new one
		if (dsWrapper.contains(clause)) {
			IClause existingClause = dsWrapper.get(clause);
			if (	!existingClause.getLevel().equals(clause.getLevel()) &&
					Level.getHighest(existingClause.getLevel(), clause.getLevel()).equals(existingClause.getLevel())) {
				// we replace the clause by the new one
				dsWrapper.remove(clause);
				return false;
			}
			return true;
		}
		return false;
	}
	
	// new clause :
	// 1 simplify
	// 2 dispatch to provers
	public void newClause(IClause clause) {
		assert clause.getLevel().isAncestorOf(level) || clause.getLevel().equals(level);
		
		// simplify
		clause = simplifier.run(clause);
		
		if (clause.isTrue()) return;
		
		// dispatch to corresponding prover
		if (clause.isFalse()) {
			contradiction(clause.getOrigin());
			return;
		}
		
		// all clauses are put here
		// this should add the clause in all datastructures of all provers
		if (isSubsumed(clause)) return;
		dsWrapper.add(clause);
		
		// TODO forward subsumption
		// TODO this code calls back into the mainloop - think of another way
		// TODO do not do this, let the predicate prover do the instantiations
		// and call it back like the equality
		// give to predicate prover
		
//		if (prover.accepts(clause)) {
//			prover.addOwnClause(clause);
//		}

	}
	
	public ITracer getTracer() {
		return tracer;
	}
	
	private int counter;
	public void mainLoop(long nofSteps) throws InterruptedException {
		debug("=== ProofStrategy. Starting ===");
		
		for (IClause clause : originalClauses) {
			newClause(clause);
		}
		
		boolean nextStep = true;
		counter = 0;
		while (!stop) {
			if (Thread.interrupted()) throw new InterruptedException();
			
			counter++;
			if (nofSteps > 0 && counter >= nofSteps) noProofFound();
			
			debug("=== ProofStrategy. Step "+counter+". Level "+level+" ===");
			dumper.dump();
			
			// add equality clauses
			for (IClause clause : equality.getGeneratedClauses()) {
				if (!isSubsumed(clause)) {
					dsWrapper.add(clause);
				}
			}
			// add prover clauses
			for (IClause clause : prover.getGeneratedClauses()) {
				if (!isSubsumed(clause)) {
					dsWrapper.add(clause);
				}
			}
			
			IClause clause = prover.next();
			
			if (clause == null) {
				debug("== Nothing found, trying seedsearch ==");
				clause = seedsearch.next();
			}
			if (clause == null) {
				debug("== Nothing found, trying casesplit ==");
				// handle no more clauses
				// for now : case split
				Level oldLevel = level;
				nextLevel();
				clause = casesplitter.next();
				
				// TODO this is ugly
				if (clause == null) {
					level = oldLevel;
				}
				else {
					debug("Splitting, new level: "+level.toString());
				}
			}
			if (clause == null) {
				// no case split available
				if (nextStep)
					nextStep = false;
				else
					noProofFound();
			}
			else {
				nextStep = true;
				newClause(clause);
			}
		}
		
	}
	
	public Level getLevel() {
		return level;
	}
	
	private void nextLevel() {
		if (level.getLeftBranch().equals(lastClosedBranch)) level = level.getRightBranch();
		else level = level.getLeftBranch();
	}
	
	private Level lastClosedBranch;
	
	private void adjustLevel(Stack<Level> dependencies) {
		
		Level highestOddLevel = getHighestOdd(dependencies);
		if (highestOddLevel.equals(Level.base)) {
			// proof is done !
			proofFound();
			level = Level.base;
		}
		else {
			// main loop on the next case
			lastClosedBranch = highestOddLevel;
			level = highestOddLevel.getParent();
		}
	}
	
	private Level getHighestOdd(Stack<Level> dependencies) {
		for (Level level : dependencies) {
			if (level.isLeftBranch()) return level;
		}
		return Level.base;
	}
	
	private PPResult result;
	
	public PPResult getResult() {
		return result;
	}

	public void noProofFound() {
		stop = true;
		result = new PPResult(Result.invalid, null);
//		System.out.println("no proof found, number of steps: "+counter);
	}
	
	public void proofFound() {
		// TODO contradiction has been found, stop the nonUnitProver
		stop = true;
		proofFound = true;
		result = new PPResult(Result.valid, tracer);
//		System.out.println("proof found, number of steps: "+counter);
//		debug("** proof found, traced clauses **");
//		debug(tracer.getClauses().toString());
//		debug("** original hypotheses **");
//		debug(tracer.getOriginalPredicates().toString());
	}

	public boolean hasStopped() {
		return stop;
	}

	public void removeClause(IClause clause) {
		dsWrapper.remove(clause);
	}


}
