package org.eventb.internal.pp.core;

import java.util.Collection;
import java.util.Stack;

import org.eventb.internal.pp.core.datastructure.DataStructureWrapper;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.eventb.internal.pp.core.provers.seedsearch.SeedSearchProver;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
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
	private CaseSplitter casesplitter;
	private PredicateProver prover;
	private SeedSearchProver seedsearch;
	
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

	public void setPredicateProver(PredicateProver prover) {
		prover.initialize(this, dsWrapper);
		prover.registerDumper(dumper);
		this.prover = prover;
	}
	
	public void setCaseSplitter(CaseSplitter casesplitter) {
		casesplitter.initialize(this, dsWrapper);
		casesplitter.registerDumper(dumper);
		this.casesplitter = casesplitter;
	}
	
	public void setSeedSearch(SeedSearchProver seedsearch) {
		seedsearch.initialize(this, dsWrapper);
		seedsearch.registerDumper(dumper);
		this.seedsearch = seedsearch;
	}
	
	public void setClauses(Collection<IClause> clauses) {
		this.originalClauses = clauses;
	}
	
	public void addSimplifier(ISimplifier simplifier) {
		this.simplifier.addSimplifier(simplifier);
	}
	
	private void contradiction(IClause clause) {
		debug("Contradiction found on: "+clause);
		tracer.addClosingClause(clause);
		
		Level oldLevel = level;
		adjustLevel(clause);
		debug("Old level was: "+oldLevel+", new level is: "+level);
		
		// we backtrack our own datastructure
		debug("ProofStrategy: Backtracking datastructures");
		dsWrapper.backtrack(level);
		
		debug("Dispatching contradiction to subprovers");
		// tell the predicate prover
		prover.contradiction(oldLevel, level, proofFound);
		// tell the case splitter
		// TODO not stop
		casesplitter.contradiction(oldLevel, level, proofFound);
		// TODO tell the arithmetic prover
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
		
		if (clause == null) return;
		
		// dispatch to corresponding prover
		if (clause.isEmpty()) {
			contradiction(clause);
			return;
		}
		
		// all clauses are put here
		// this should add the clause in all datastructures of all provers
		if (isSubsumed(clause)) return;
		
		dsWrapper.add(clause);
		
		// TODO forward subsumption
		// TODO this code calls back into the mainloop - think of another way
		// give to predicate prover
		if (prover.accepts(clause)) {
			prover.addOwnClause(clause);
		}

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
	
	private void adjustLevel(IClause clause) {
		// contradiction has been found, backtrack
		Stack<Level> dependencies = new Stack<Level>();
		clause.getDependencies(dependencies);
		
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


}
