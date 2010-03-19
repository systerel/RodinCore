/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored cancellation tests
 *******************************************************************************/
package org.eventb.internal.pp.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.CancellationChecker;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.search.RandomAccessList;
import org.eventb.internal.pp.core.search.ResetIterator;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.pp.ITracer;
import org.eventb.pp.PPResult;
import org.eventb.pp.PPResult.Result;

/**
 * This class decides in which order prover modules are invoked and 
 * manages derived and subsumed clauses.
 * <p>
 * The clause dispatcher maintains two data structures, one containing the 
 * clauses that have already been handled and dispatched to the modules
 * ({@link #alreadyDispatchedClauses}), and one containing clauses that
 * have been derived by a prover module but not yet dispatched to the
 * other modules ({@link #nonDispatchedClauses}).
 * <p>
 * The algorithm works step-by-step. At each step, the dispatcher does the
 * following.
 * <ul>
 * <li>It dispatches all non dispatched clauses to the prover modules using
 * {@link IProverModule#addClauseAndDetectContradiction(Clause)}.</li>
 * <li>It gets the next clause from one of the prover module using 
 * {@link IProverModule#next(boolean)}.</li>
 * </ul>
 * Between those two, it checks whether a contradiction has been derived and
 * if it is the case, it first call {@link IProverModule#contradiction(Level, Level, Set)}
 * on all prover modules and then it backtracks its data structures (the already
 * dispatched clauses and the non-dispatched clauses). Doing so, it calls 
 * {@link IProverModule#removeClause(Clause)} for each backtracked clause on each
 * prover module.
 * Before each steps, it checks whether the maximal number of steps has been 
 * reached or whether the user has canceled the proof, in which case it stops.
 * <p>
 * The prover modules never have in their internal state clauses that are not
 * in the {@link #alreadyDispatchedClauses} list of the clause dispatcher.
 * <p>
 * The clause dispatcher never has two clauses that are the same but with different
 * levels in its state. It always removes the clause with the lowest level without
 * adding the new one. It is therefore the same for prover modules. If it already
 * exists, a clause is never added without first having been removed. This means
 * that when the {@link ClauseDispatcher} calls {@link IProverModule#addClauseAndDetectContradiction(Clause)},
 * either the given clause is new (has never been generated before), or it has
 * already been generated but first removed using {@link IProverModule#removeClause(Clause)}.
 *
 * @author François Terrier
 *
 */
public final class ClauseDispatcher  {
	
	/**
	 * Debug flag for <code>PROVER_TRACE</code>
	 */
	public static boolean DEBUG = false;
	public static void debug(String message){
		System.out.println(message);
	}
	
	private final RandomAccessList<Clause> alreadyDispatchedClauses;
	private final ResetIterator<Clause> alreadyDispatchedBacktrackClausesIterator;

	private final RandomAccessList<Clause> nonDispatchedClauses;
	private final ResetIterator<Clause> nonDispatchedClausesIterator;
	private final ResetIterator<Clause> nonDispatchedBacktrackClausesIterator;
	
	private final Collection<Clause> originalClauses;
	private final List<IProverModule> provers;
	private final ClauseSimplifier simplifier;
	private final CancellationChecker cancellation;
	private final Tracer tracer;
	private final Dumper dumper;
	
	private PPResult result;
	private int counter = 0;
	private boolean terminated = false;

	
	public ClauseDispatcher(CancellationChecker cancellation) {
		alreadyDispatchedClauses = new RandomAccessList<Clause>();
		alreadyDispatchedBacktrackClausesIterator = alreadyDispatchedClauses.iterator();
		nonDispatchedClauses = new RandomAccessList<Clause>();
		nonDispatchedClausesIterator = nonDispatchedClauses.iterator();
		nonDispatchedBacktrackClausesIterator = nonDispatchedClauses.iterator();
		
		this.cancellation = cancellation;
		
		dumper = new Dumper();
		tracer = new Tracer();
		
		simplifier = new ClauseSimplifier();
		provers = new ArrayList<IProverModule>();
		originalClauses = new ArrayList<Clause>();
	}
	
	/**
	 * Adds a prover module to the clause dispatcher.
	 * <p>
	 * Prover module are invoked one after the other in the order
	 * they were added to derive new clauses out of the dispatched
	 * clauses.
	 * 
	 * @param prover the prover module to add
	 */
	public void addProverModule(IProverModule prover) {
		prover.registerDumper(dumper);
		provers.add(prover);
	}
	
	/**
	 * Sets the original clauses for this proof
	 * 
	 * @param clauses the original clauses
	 */
	public void setClauses(Collection<Clause> clauses) {
		this.originalClauses.addAll(clauses);
	}
	
	/**
	 * Adds a simplifier.
	 * <p>
	 * After a clause is derived by a prover module and before it
	 * is added to the list of clauses, simplifiers added using this
	 * method are called on the clause. They are called in the order
	 * in which they were added. 
	 * 
	 * @param simplifier the simplifier to add
	 */
	public void addSimplifier(ISimplifier simplifier) {
		this.simplifier.addSimplifier(simplifier);
	}
	
	/**
	 * Returns the tracer of this proof.
	 * 
	 * @return the tracer of this proof
	 */
	public ITracer getTracer() {
		return tracer;
	}
	
	/**
	 * Returns the level controller.
	 * 
	 * @return the level controller
	 */
	public ILevelController getLevelController() {
		return tracer;
	}
	
	/**
	 * Returns the result of this proof.
	 * 
	 * @return the result of this proof
	 */
	public PPResult getResult() {
		return result;
	}
	
	/**
	 * Starts the clause dispatcher on the specified clauses.
	 * <p>
	 * If a result is not found after the specified number of steps,
	 * the clause dispatcher stops.
	 * 
	 * @param nofSteps the number of steps after which the clause dispatcher
	 * stops
	 */
	public void mainLoop(long nofSteps) {
		if (DEBUG) debug("=== ClauseDispatcher. Starting ===");
		addOriginalClauses();
		if (DEBUG) dumpOriginalClauses();
		
		boolean force = false;
		while (!terminated) {
			cancellation.check();
			if (!updateCounterAndCheckTermination(nofSteps)) {
				// first phase, treat non dispatched clauses
				if (!treatNondispatchedClausesAndCheckContradiction()) {
					// second phase, all clauses have been treated
					force = getNextClauseFromProvers(force);
				}
			}
		}
	}
	
	private void dumpOriginalClauses() {
		debug("= Clauses =");
		ResetIterator<Clause> iterator = nonDispatchedClauses.iterator();
		while (iterator.hasNext()) {
			debug(iterator.next().toString());
		}
		iterator.invalidate();
	}
	
	private void addOriginalClauses() {
		for (Clause clause : originalClauses) {
			clause = simplifier.run(clause);
			if (clause.isFalse()) internalContradiction(clause.getOrigin());
			else if (!clause.isTrue()) addNonDispatchedClause(clause);
		}
	}
	
	private boolean getNextClauseFromProvers(boolean force) {
		if (DEBUG) debug("== Getting next clause from provers ==");
		ProverResult nextResult = null;
		for (IProverModule prover : provers) {
			nextResult = prover.next(force);
			if (!nextResult.isEmpty()) {
				if (DEBUG) debug("= Got result from "+prover.toString()+": "+nextResult.toString()+" =");
				break;
			}
		}
		if (nextResult.isEmpty()) {
			if (DEBUG) debug("= Got no result this time =");
			// proof done
			if (force) terminate(Result.invalid);
			return true;
		}
		else {
			Set<IOrigin> contradictions = new HashSet<IOrigin>();
			treatProverResultAndCheckContradiction(nextResult, contradictions);
			if (!contradictions.isEmpty()) handleContradictions(contradictions);
			return false;
		}
	}
	
	private boolean treatNondispatchedClausesAndCheckContradiction() {
		if (DEBUG) debug("== Treating non dispatched clauses ==");
		nonDispatchedClausesIterator.reset();
		while (nonDispatchedClausesIterator.hasNext()) {
			cancellation.check();
			Clause clause = nonDispatchedClausesIterator.next();
			if (DEBUG) debug("== Next clause: "+clause+" ==");
			
			assert tracer.getCurrentLevel().compareTo(clause.getLevel()) >= 0;

			Set<IOrigin> contradictions = new HashSet<IOrigin>();
			for (IProverModule prover : provers) {
				cancellation.check();
				ProverResult result = prover.addClauseAndDetectContradiction(clause);
				if (DEBUG) debug("= Got result from "+prover.toString()+": "+result.toString()+" =");
				treatProverResultAndCheckContradiction(result, contradictions);
			}
			alreadyDispatchedClauses.add(clause);
			nonDispatchedClauses.remove(clause);
			
			if (!contradictions.isEmpty()) {
				handleContradictions(contradictions);
				return true;
			}
		}
		return false;
	}
	
	private boolean updateCounterAndCheckTermination(long nofSteps) {
		counter++;
		if (nofSteps > 0 && counter >= nofSteps) {
			terminate(Result.timeout);
			return true;
		}
		if (DEBUG) debug("=== ClauseDispatcher. Step "+counter+". Level "+tracer.getCurrentLevel()+" ===");
		dumper.dump();
		return false;
	}
	
	private void treatProverResultAndCheckContradiction(ProverResult result, Set<IOrigin> contradictions) {
		removeClauses(result.getSubsumedClauses());
		Set<Clause> generatedClauses = result.getGeneratedClauses();
		simplifyClauses(generatedClauses);
		checkGeneratedClauseLevel(generatedClauses);
		splitResultAndGetContradiction(generatedClauses, contradictions);
		addNonDispatchedClauses(generatedClauses);
	}
	
	private void simplifyClauses(Set<Clause> generatedClauses) {
		simplifier.run(generatedClauses);
	}

	private void splitResultAndGetContradiction(Set<Clause> generatedClauses, Set<IOrigin> contradictions) {
		for (Iterator<Clause> iter = generatedClauses.iterator(); iter.hasNext();) {
			Clause clause = iter.next();
			if (clause.isFalse()) {
				iter.remove();
				contradictions.add(clause.getOrigin());
			}
			if (clause.isTrue()) iter.remove();
		}
	}
	
	private void handleContradictions(Set<IOrigin> contradictions) {
		if (contradictions.size() > 1) if (DEBUG) debug(" Several contradictions detected: " +contradictions);
		
		IOrigin oldOrigin = null;
		for (IOrigin origin : contradictions) {
			if (oldOrigin == null) oldOrigin = origin;
			else {
				oldOrigin = getLowerLevelOrigin(oldOrigin, origin);
			}
		}
		internalContradiction(oldOrigin);
	}
	
	private static IOrigin getLowerLevelOrigin(IOrigin origin1, IOrigin origin2) {
		if (origin1 == null) return origin2;
		if (origin2 == null) return origin1;
		return origin1.getLevel().isAncestorOf(origin2.getLevel())?origin1:origin2;
	}
	
	
	private void addNonDispatchedClauses(Set<Clause> clauses) {
		for (Clause generatedClause : clauses) {
			assert !generatedClause.isFalse();
			assert !generatedClause.isTrue();
			addNonDispatchedClause(generatedClause);
		}
	}
	
	private void checkGeneratedClauseLevel(Set<Clause> clauses) throws IllegalStateException {
		for (Clause clause : clauses) {
			if (clause.getLevel().equals(tracer.getCurrentLevel())) continue;
			if (clause.getLevel().isAncestorInSameTree(tracer.getCurrentLevel())) continue;
			else throw new IllegalStateException();
		}
	}
	
	private void addNonDispatchedClause(Clause clause) {
		assert !clause.isFalse();
		
		if (!clause.isTrue() && !checkAndRemoveAlreadyExistingClause(clause)) {
			assert !alreadyDispatchedClauses.contains(clause);
			
			// we check if it is there
			if (nonDispatchedClauses.contains(clause)) {
				Clause existingClause = nonDispatchedClauses.get(clause);
				if (	clause.getLevel().isAncestorOf(existingClause.getLevel())
						|| clause.getOrigin().dependsOnGoal()) {
					// we replace the clause by the new one
					nonDispatchedClauses.remove(existingClause);
				}
				else return; // clause had a lower level, we forget the new clause
			}
			assert !nonDispatchedClauses.contains(clause);
			nonDispatchedClauses.add(clause);
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
				alreadyDispatchedClauses.remove(existingClause);
				removeClauseFromProvers(existingClause);
				return false;
			}
			return true;
		}
		return false;
	}
	
	
	private void internalContradiction(IOrigin origin) {
		assert !tracer.getCurrentLevel().isAncestorOf(origin.getLevel());
		if (DEBUG) debug("= Contradiction found on: "+origin+" =");
		Level oldLevel = tracer.getCurrentLevel();
		Set<Level> dependencies = new HashSet<Level>();
		origin.addDependenciesTo(dependencies);
		if (DEBUG) debug("= Level dependencies: "+dependencies+" =");
		adjustLevel(origin);
		if (terminated) return;
		if (DEBUG) debug("= Closing level: "+tracer.getLastClosedLevel()+", old level was: "+oldLevel+", new level is: "+tracer.getCurrentLevel()+" =");
		dispatchContradictionToProvers(oldLevel, dependencies);
		// we backtrack our own datastructure
		if (DEBUG) debug("= Done dispatching, backtracking datastructures =");
		backtrack(tracer.getCurrentLevel(), alreadyDispatchedBacktrackClausesIterator, alreadyDispatchedClauses);
		backtrack(tracer.getCurrentLevel(), nonDispatchedBacktrackClausesIterator, nonDispatchedClauses);
	}
	
	private void dispatchContradictionToProvers(Level oldLevel, Set<Level> dependencies) {
		if (DEBUG) debug("= Dispatching contradiction to subprovers =");
		Set<IOrigin> contradictions = new HashSet<IOrigin>();
		for (IProverModule prover : provers) {
			ProverResult result = prover.contradiction(oldLevel, tracer.getCurrentLevel(), dependencies);
			treatProverResultAndCheckContradiction(result, contradictions);
		}	
		assert contradictions.isEmpty();
	}
	
	private void backtrack(Level level, ResetIterator<Clause> iterator, 
			RandomAccessList<Clause> iterable) {
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
		for (Clause subsumedClause : subsumedClauses) {
			alreadyDispatchedClauses.remove(subsumedClause);
			removeClauseFromProvers(subsumedClause);
		}
	}
	
	private void removeClauseFromProvers(Clause clause) {
		for (IProverModule prover : provers) {
			prover.removeClause(clause);
		}
	}
	
	private void adjustLevel(IOrigin origin) {
		tracer.addClosingClauseAndUpdateLevel(origin);
		if (tracer.getLastClosedLevel().equals(Level.BASE)) {
			terminate(Result.valid);
		}
	}
	
	private void terminate(Result result1) {
		if (result1 != Result.valid) result = new PPResult(result1, null);
		else result = new PPResult(result1, tracer);
		terminated = true;
	}

}
