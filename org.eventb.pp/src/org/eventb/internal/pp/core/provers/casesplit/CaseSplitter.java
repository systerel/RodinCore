package org.eventb.internal.pp.core.provers.casesplit;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IDispatcher;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.inferrers.CaseSplitNegationInferrer;
import org.eventb.internal.pp.core.search.ConditionIterator;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.search.ResetIterator;

public class CaseSplitter implements IProver {
	
	/**
	 * Debug flag for <code>PROVER_CASESPLIT_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
		if (DEBUG) {
			String prefix = "";
//			for (int i = 0; i < currentLevel.getHeight(); i++) {
//				prefix += " ";
//			}
			System.out.println(prefix+message);
		}
	}
	
	private Stack<SplitPair> splits = new Stack<SplitPair>();
	private SplitPair nextCase;
	
	private IterableHashSet<Clause> candidates;
	private CaseSplitIterator splitIterator;
	private CaseSplitNegationInferrer inferrer;
	
	private IDispatcher dispatcher;
	private ClauseSimplifier simplifier;
	
	public CaseSplitter(IVariableContext context, IDispatcher dispatcher) {
		this.inferrer = new CaseSplitNegationInferrer(context);
		this.dispatcher = dispatcher;
	}
	
	public void initialize(ClauseSimplifier simplifier) {
		this.simplifier = simplifier;
		candidates = new IterableHashSet<Clause>();
		
		splitIterator = new CaseSplitIterator(candidates.iterator(),inferrer);
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
			if (!splitIterator.hasNext()) return null;
			dispatcher.nextLevel();
			result = newCaseSplit();
		}
		else {
			dispatcher.nextLevel();
			result = nextCase();
		}
		debug("CaseSplitter, next clause: "+result);
		
		result = simplifier.run(result);
		
//		assert splits.size() == dispatcher.getLevel().getHeight();
		return new ProverResult(result);
	}
	
	private Clause nextCase() {
		debug("Following case on "+nextCase.original+", size of split stack: "+splits.size());
		Clause result = nextCase.right;
		splits.push(nextCase);
		nextCase = null;
		return result;
	}
	
	private Clause newCaseSplit() {
		Clause clause = splitIterator.next();
		
		if (clause == null) throw new IllegalStateException();
		candidates.remove(clause);
		
		assert !dispatcher.getLevel().isAncestorOf(clause.getLevel()):"Splitting on clause: "+clause+", but level: "+dispatcher.getLevel();
		
		splits.push(split(clause));
		debug("New case split on "+clause+", size of split stack: "+splits.size());
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
		debug("CaseSplitter: Backtracking datastructures, size of split stack: "+splits.size());
		
		Set<Clause> putBackList = new LinkedHashSet<Clause>();
		if (	nextCase != null
			&&	nextCase.original.getLevel().isAncestorOf(dispatcher.getLevel())) {
			// we put the clause as a candidate again
			putBackList.add(nextCase.original);
		}
		
		debug("CaseSplitter: Backtracking from: "+oldLevel+", to: "+dispatcher.getLevel());
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
		debug("CaseSplitter: Backtracking done, size of split stack: "+splits.size());
		nextCase = splits.pop();
		
		List<Clause> reversePutBackList = new ArrayList<Clause>(putBackList);
//		Collections.reverse(reversePutBackList);
		for (Clause clause : reversePutBackList) {
			if (dispatcher.contains(clause)) {
				candidates.appends(clause);
			}
		}
		
//		assert splits.size() == dispatcher.getLevel().getHeight() : "Splits: "+splits.size();
	}
	
	public void registerDumper(Dumper dumper) {
		dumper.addDataStructure("CaseSplit", candidates.iterator());
	} 
	
	private boolean accepts(Clause clause) {
		return inferrer.canInfer(clause);
	}
	
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		assert !candidates.contains(clause);
		assert !dispatcher.getLevel().isAncestorOf(clause.getLevel());
		
		if (accepts(clause)) candidates.appends(clause);
		return null;
	}

	public void removeClause(Clause clause) {
		candidates.remove(clause);
	}
	
	private class CaseSplitIterator extends ConditionIterator<Clause> {
		private CaseSplitNegationInferrer inferrer;
		
		public CaseSplitIterator(ResetIterator<Clause> iterator, CaseSplitNegationInferrer inferrer) {
			super(iterator);
			this.inferrer = inferrer;
		}

		@Override
		public boolean isSelected(Clause clause) {
			assert inferrer.canInfer(clause);
			
			return true;
		}
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

	public boolean isSubsumed(Clause clause) {
		return false;
	}
	
	@Override
	public String toString() {
		return "CaseSplitter";
	}
}
