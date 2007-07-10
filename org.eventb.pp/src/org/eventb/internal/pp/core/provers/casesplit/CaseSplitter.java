package org.eventb.internal.pp.core.provers.casesplit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IDispatcher;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.inferrers.CaseSplitNegationInferrer;

public class CaseSplitter implements IProver {
	
	/**
	 * Debug flag for <code>PROVER_CASESPLIT_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
			String prefix = "";
//			for (int i = 0; i < currentLevel.getHeight(); i++) {
//				prefix += " ";
//			}
			System.out.println(prefix+message);
	}
	
	private Stack<SplitPair> splits = new Stack<SplitPair>();
	private SplitPair nextCase;
	
	private Vector<Clause> candidates;
	private CaseSplitNegationInferrer inferrer;
	
	private IDispatcher dispatcher;
	private ClauseSimplifier simplifier;
	
	public CaseSplitter(IVariableContext context, IDispatcher dispatcher) {
		this.inferrer = new CaseSplitNegationInferrer(context);
		this.dispatcher = dispatcher;
	}
	
	public void initialize(ClauseSimplifier simplifier) {
		this.simplifier = simplifier;
		candidates = new Stack<Clause>();
	}
	
	public void contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies) {
		// contradiction has been found, backtrack
		// main loop on the next case
		backtrack(oldLevel, dependencies);
	}
	
	// this returns the next clause produced by a case split.
	// if the preceding branch was closed, it returns the next case.
	// it it is not the case it does a new case split
	public ProverResult next() {
//		assert splits.size() == dispatcher.getLevel().getHeight();
		Clause result;
		if (nextCase == null) {
			if (candidates.isEmpty()) return null;
			dispatcher.nextLevel();
			result = newCaseSplit();
		}
		else {
			dispatcher.nextLevel();
			result = nextCase();
		}
		if (DEBUG) debug("CaseSplitter, next clause: "+result);
		
		result = simplifier.run(result);
		
//		assert splits.size() == dispatcher.getLevel().getHeight();
		return new ProverResult(result);
	}
	
	private Clause nextCase() {
		if (DEBUG) debug("Following case on "+nextCase.original+", size of split stack: "+splits.size());
		Clause result = nextCase.right;
		splits.push(nextCase);
		nextCase = null;
		return result;
	}
	
	private Clause newCaseSplit() {
		Clause clause = candidates.remove(0);
		
		if (clause == null) throw new IllegalStateException();
		candidates.remove(clause);
		
		assert !dispatcher.getLevel().isAncestorOf(clause.getLevel()):"Splitting on clause: "+clause+", but level: "+dispatcher.getLevel();
		
		splits.push(split(clause));
		if (DEBUG) debug("New case split on "+clause+", size of split stack: "+splits.size()+", remaining candidates: "+candidates.size());
		return splits.peek().left;
	}
	
	private SplitPair split(Clause clause) {
		inferrer.setLevel(dispatcher.getLevel().getParent());
		clause.infer(inferrer);
		return new SplitPair(clause,inferrer.getLeftCase(),inferrer.getRightCase(),dispatcher.getLevel().getParent());
	}
	
	/**
	 * Backtrack from this level up to and inclusive the level specified as a parameter.
	 * 
	 * @param oldLevel the level which must be backtracked
	 */
	private void backtrack(Level oldLevel, Set<Level> dependencies) {
		if (DEBUG) debug("CaseSplitter: Backtracking datastructures, size of split stack: "+splits.size());
		
		Set<Clause> putBackList = new LinkedHashSet<Clause>();
		if (	nextCase != null
			&&	nextCase.original.getLevel().isAncestorOf(dispatcher.getLevel())) {
			// we put the clause as a candidate again
			putBackList.add(nextCase.original);
		}
		
		if (DEBUG) debug("CaseSplitter: Backtracking from: "+oldLevel+", to: "+dispatcher.getLevel());
		Level tmp = oldLevel;

		while (!tmp.getParent().equals(dispatcher.getLevel())) {
			SplitPair pair = splits.pop();
			if (!dependencies.contains(tmp)) { 
				// we put it back in the candidate list
				putBackList.add(pair.original);
			}
			tmp = tmp.getParent();
			
			assert tmp.equals(pair.level);
		}
		if (DEBUG) debug("CaseSplitter: Backtracking done, size of split stack: "+splits.size());
		nextCase = splits.pop();
		
		List<Clause> reversePutBackList = new ArrayList<Clause>(putBackList);
//		Collections.reverse(reversePutBackList);
		for (Clause clause : reversePutBackList) {
			if (dispatcher.contains(clause)) {
				candidates.add(clause);
			}
		}
		
//		assert splits.size() == dispatcher.getLevel().getHeight() : "Splits: "+splits.size();
	}
	
	public void registerDumper(Dumper dumper) {
		dumper.addObject("CaseSplitter", candidates);
	} 
	
	private boolean accepts(Clause clause) {
		return inferrer.canInfer(clause);
	}
	
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		assert !candidates.contains(clause);
		assert !dispatcher.getLevel().isAncestorOf(clause.getLevel());
		
		if (accepts(clause)) candidates.add(clause);
		return null;
	}

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
		Clause left,right;
		Level level;
		
		SplitPair(Clause original, Clause left, Clause right, Level level) {
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
