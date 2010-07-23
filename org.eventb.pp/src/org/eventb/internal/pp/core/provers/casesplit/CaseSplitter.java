/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.casesplit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.eventb.internal.pp.core.ClauseDispatcher;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.ILevelController;
import org.eventb.internal.pp.core.IProverModule;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.inferrers.CaseSplitInferrer;

/**
 * The prover module responsible for the case split rule.
 * <p>
 * This module maintains a stack of splits which is updated each time
 * the {@link ClauseDispatcher} backtracks or each time a new split is done.
 * All this module does is keeping this list of splits up-to-date and consistent
 * with the current level of the prover. Furthermore, it chooses the right candidate
 * for the next split.
 *
 * @author François Terrier
 *
 */
public class CaseSplitter implements IProverModule {
	
	/**
	 * Debug flag for <code>PROVER_CASESPLIT_TRACE</code>
	 */
	public static boolean DEBUG = false;
	public static void debug(String message){
		System.out.println(message);
	}
	
	private Stack<SplitPair> splits;
	private SplitPair nextCase;
	private Vector<Clause> candidates;
	private CaseSplitInferrer inferrer;
	private ILevelController dispatcher;
	
	public CaseSplitter(VariableContext context, ILevelController dispatcher) {
		this.inferrer = new CaseSplitInferrer(context);
		this.dispatcher = dispatcher;
		this.candidates = new Stack<Clause>();
		this.splits = new Stack<SplitPair>();
	}
	
	@Override
	public ProverResult contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies) {
		return backtrack(oldLevel, newLevel, dependencies);
	}
	
	private static final int INIT = 3;
	private int counter = 0;
	private boolean isNextAvailable() {
		if (counter > 0) {
			counter--;
			return false;
		}
		else {
			counter = INIT;
			return true;
		}
	}
	
	// this returns the next clause produced by a case split.
	// if the preceding branch was closed, it returns the next case.
	// it it is not the case it does a new case split
	@Override
	public ProverResult next(boolean force) {
//		assert splits.size() == dispatcher.getCurrentLevel().getHeight();
		if (!force && !isNextAvailable()) return ProverResult.EMPTY_RESULT;
		if (nextCase == null && candidates.isEmpty()) return ProverResult.EMPTY_RESULT;
		
		Level oldLevel = dispatcher.getCurrentLevel();
		dispatcher.nextLevel();
		Set<Clause> result;
		if (nextCase == null) result = newCaseSplit(oldLevel);
		else result = nextCase();
		assertCorrectResult(result);
		
		if (DEBUG) debug("CaseSplitter["+counter+"]: "+result);
//		assert splits.size() == dispatcher.getCurrentLevel().getHeight();
		return new ProverResult(result, new HashSet<Clause>());
	}
	
	private void assertCorrectResult(Set<Clause> clauses) {
		for (Clause clause : clauses) {
			assert clause.getLevel().equals(dispatcher.getCurrentLevel());
		}
	}
	
	private Set<Clause> nextCase() {
		Set<Clause> result = nextCase.right;
		splits.push(nextCase);
		if (DEBUG) debug("Following case on "+nextCase.original+", size of split stack: "+splits.size());
		nextCase = null;
		return result;
	}
	
	private Set<Clause> newCaseSplit(Level oldLevel) {
		Clause clause = nextCandidate();
		candidates.remove(clause);
		splits.push(split(clause, oldLevel));
		if (DEBUG) debug("New case split on "+clause+", size of split stack: "+splits.size()+", remaining candidates: "+candidates.size());
		return splits.peek().left;
	}
	
	/**
	 * Returns the next candidate.
	 * <p>
	 * Candidates are chosen in this order :
	 * <ul>
	 * <li>clause depends on the goal, if several clauses depend on the goal, the
	 * one with the smallest depth is taken</li>
	 * <li>clause does not depend on the goal, the one with the smallest depth is 
	 * taken first</li>
	 * </ul>
	 * 
	 * @return
	 */
	private Clause nextCandidate() {
		List<Clause> restrictedCandidates = getCandidatesDependingOnGoal();
		if (restrictedCandidates.isEmpty()) restrictedCandidates = candidates;
		int depth = -1;
		Clause currentClause = null;
		for (Clause clause : restrictedCandidates) {
			if (depth == -1 || clause.getOrigin().getDepth() < depth) {
				depth = clause.getOrigin().getDepth();
				currentClause = clause;
			}
		}
		return currentClause;
	}
	
	private List<Clause> getCandidatesDependingOnGoal() {
		List<Clause> result = new ArrayList<Clause>();
		for (Clause clause : candidates) {
			if (clause.getOrigin().dependsOnGoal()) result.add(clause);
		}
		return result;
	}

	private SplitPair split(Clause clause, Level level) {
		inferrer.setLevel(level);
		clause.infer(inferrer);
		return new SplitPair(clause,inferrer.getLeftCase(),inferrer.getRightCase(),level);
	}
	
	/**
	 * Backtrack from this level up to and inclusive the level specified as a parameter.
	 * <p>
	 * Splits that are eliminated and not used are returned to the {@link ClauseDispatcher}
	 * as they could lead to a proof. 
	 * 
	 * @param oldLevel the level which must be backtracked
	 */
	private ProverResult backtrack(Level oldLevel, Level newLevel, Set<Level> dependencies) {
		if (DEBUG) debug("CaseSplitter: Backtracking datastructures, size of split stack: "+splits.size());
		
		Set<Clause> putBackList = new LinkedHashSet<Clause>();
		if (	nextCase != null
			&&	!newLevel.isAncestorOf(nextCase.original.getLevel())) {
			// we put the clause as a candidate again
			putBackList.add(nextCase.original);
		}
		
		if (DEBUG) debug("CaseSplitter: Backtracking from: "+oldLevel+", to: "+newLevel);
		Level tmp = oldLevel;

		while (!tmp.getParent().equals(newLevel)) {
			SplitPair pair = splits.pop();
			if (	!dependencies.contains(tmp)
					&& !newLevel.isAncestorOf(pair.original.getLevel())) { 
				// we put it back in the candidate list
				putBackList.add(pair.original);
			}
			tmp = tmp.getParent();
			
			assert tmp.equals(pair.level);
		}
		if (DEBUG) debug("CaseSplitter: Backtracking done, size of split stack: "+splits.size());
		nextCase = splits.pop();
		
//		assert splits.size() == dispatcher.getLevel().getHeight() : "Splits: "+splits.size();
		return new ProverResult(putBackList, new HashSet<Clause>());
	}
	
	@Override
	public void registerDumper(Dumper dumper) {
		dumper.addObject("CaseSplitter", candidates);
	} 
	
	private boolean accepts(Clause clause) {
		return inferrer.canInfer(clause);
	}

	@Override
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		assert !candidates.contains(clause);
		assert !dispatcher.getCurrentLevel().isAncestorOf(clause.getLevel());
		
		if (accepts(clause)) candidates.add(clause);
		return ProverResult.EMPTY_RESULT;
	}

	@Override
	public void removeClause(Clause clause) {
		for (Iterator<Clause> iter = candidates.iterator(); iter.hasNext();) {
			Clause existingClause = iter.next();
			if (existingClause.equals(clause)) {
				assert existingClause.equalsWithLevel(clause);
				iter.remove();
			}
		}
	}

	public boolean isSubsumed(Clause clause) {
		return false;
	}
	
	private static class SplitPair {
		Clause original;
		Set<Clause> left,right;
		Level level;
		
		SplitPair(Clause original, Set<Clause> left, Set<Clause> right, Level level) {
			this.original = original;
			this.left = left;
			this.right = right;
			this.level = level;
		}
	}

	@Override
	public String toString() {
		return "CaseSplitter";
	}
}
