package org.eventb.internal.pp.core.provers.casesplit;

import java.util.Set;
import java.util.Stack;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IDispatcher;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.datastructure.DefaultChangeListener;
import org.eventb.internal.pp.core.datastructure.IObservable;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.inferrers.CaseSplitNegationInferrer;
import org.eventb.internal.pp.core.search.ConditionIterator;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.search.ResetIterator;

public class CaseSplitter extends DefaultChangeListener implements IProver {
	
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
	
	private IterableHashSet<IClause> candidates;
	private CaseSplitIterator splitIterator;
	private CaseSplitNegationInferrer inferrer;
	
	private IDispatcher dispatcher;
	
	public CaseSplitter(IVariableContext context) {
		this.inferrer = new CaseSplitNegationInferrer(context);
	}
	
	public void initialize(IDispatcher dispatcher, IObservable clauses, ClauseSimplifier simplifier) {
		this.dispatcher = dispatcher;
		candidates = new IterableHashSet<IClause>();
		
		splitIterator = new CaseSplitIterator(candidates.iterator(),inferrer);
		clauses.addChangeListener(this);
	}
	
	public void contradiction(Level oldLevel, Level newLevel, Stack<Level> dependencies) {
		// contradiction has been found, backtrack
		// main loop on the next case
		backtrack(oldLevel, dependencies);
	}
	
	// this returns the next clause produced by a case split.
	// if the preceding branch was closed, it returns the next case.
	// it it is not the case it does a new case split
	public IClause next() {
//		assert splits.size() == dispatcher.getLevel().getHeight()-1;
		
		IClause result;
		if (dispatcher.getLevel().isRightBranch()) {
			result = nextCase();
		}
		else {
			result = newCaseSplit();
		}
		if (result == null) return null;
		debug("Case: "+result);
		
//		assert splits.size() == dispatcher.getLevel().getHeight();
		
		return result;
	}
	
	private IClause nextCase() {
		debug("Following case on "+nextCase.original+", size of split stack: "+splits.size());
		IClause result = nextCase.right;
		splits.push(nextCase);
		nextCase = null;
		return result;
	}
	
	private IClause newCaseSplit() {
		if (!splitIterator.hasNext()) return null;
		
		IClause clause = splitIterator.next();
		if (clause == null) throw new IllegalStateException();
		candidates.remove(clause);
		
		splits.push(split(clause));
		debug("New case split on "+clause+", size of split stack: "+splits.size());
		return splits.peek().left;
	}
	
	private SplitPair split(IClause clause) {
		inferrer.setLevel(dispatcher.getLevel().getParent());
		clause.infer(inferrer);
		return new SplitPair(clause,inferrer.getLeftCase(),inferrer.getRightCase(),dispatcher.getLevel().getParent());
	}
	
	/**
	 * Backtrack from this level up to and inclusive the level specified as a parameter.
	 * 
	 * @param oldLevel the level which must be backtracked
	 */
	private void backtrack(Level oldLevel, Stack<Level> dependencies) {
		debug("CaseSplitter: Backtracking datastructures, size of split stack: "+splits.size());
		
		if (	nextCase != null
			&&	nextCase.original.getLevel().isAncestorOf(dispatcher.getLevel())) {
			// we put the clause as a candidate again
			candidates.appends(nextCase.original);
		}
		
// 		if (proofDone) {
//			debug("CaseSplitter: Clearing data structures");
//			splits.clear();
//			return;
//		}
		debug("CaseSplitter: Backtracking from: "+oldLevel+", to: "+dispatcher.getLevel());
		Level tmp = oldLevel;

		while (!tmp.getParent().equals(dispatcher.getLevel())) {
			SplitPair pair = splits.pop();
			if (!dependencies.contains(tmp)) { 
				// we put it back in the candidate list
				candidates.appends(pair.original);
			}
			
			tmp = tmp.getParent();
			
			assert tmp.equals(pair.level);
		}
		debug("CaseSplitter: Backtracking done, size of split stack: "+splits.size());
		nextCase = splits.pop();
//		assert splits.size() == dispatcher.getLevel().getHeight() : "Splits: "+splits.size();
	}
	
	public void registerDumper(Dumper dumper) {
		dumper.addDataStructure("CaseSplit", candidates.iterator());
	} 
	
	private boolean accepts(IClause clause) {
		return inferrer.canInfer(clause);
	}
	
	@Override
	public void newClause(IClause clause) {
		if (accepts(clause)) candidates.appends(clause);
	}

	@Override
	public void removeClause(IClause clause) {
		if (accepts(clause)) candidates.remove(clause);
	}

	
	private class CaseSplitIterator extends ConditionIterator<IClause> {
		private CaseSplitNegationInferrer inferrer;
		
		public CaseSplitIterator(ResetIterator<IClause> iterator, CaseSplitNegationInferrer inferrer) {
			super(iterator);
			this.inferrer = inferrer;
		}

		@Override
		public boolean isSelected(IClause clause) {
			assert inferrer.canInfer(clause);
			
			return true;
		}
	}

	private static class SplitPair {
		IClause original;
		IClause left,right;
		Level level;
		
		SplitPair(IClause original, IClause left, IClause right, Level level) {
			this.original = original;
			this.left = left;
			this.right = right;
			this.level = level;
		}
	}

	public ResetIterator<IClause> getGeneratedClauses() {
		return null;
	}

	public void clean() {
		// do nothing
	}

	public Set<IClause> getSubsumedClauses() {
		// TODO Auto-generated method stub
		return null;
	}
}
