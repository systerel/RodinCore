package org.eventb.internal.pp.core.provers.casesplit;

import java.util.Stack;

import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IDispatcher;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.datastructure.IObservable;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.inferrers.CaseSplitInferrer;
import org.eventb.internal.pp.core.search.ConditionIterator;
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
	
	private CaseSplitIterator splitIterator;
	private CaseSplitInferrer inferrer;
	
	private IDispatcher dispatcher;
	
	public CaseSplitter(IVariableContext context) {
		this.inferrer = new CaseSplitInferrer(context);
	}
	
	public void initialize(IDispatcher dispatcher, IObservable clauses) {
		this.dispatcher = dispatcher;
		
		splitIterator = new CaseSplitIterator(clauses.iterator());
	}
	
	public void contradiction(Level oldLevel, Level newLevel, boolean proofFound) {
		// contradiction has been found, backtrack
		// main loop on the next case
		backtrack(oldLevel, proofFound);
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
			
		splits.push(split(clause));
		debug("New case split on "+clause+", size of split stack: "+splits.size());
		return splits.peek().left;
	}
	
	private SplitPair split(IClause clause) {
		inferrer.setLevel(dispatcher.getLevel().getParent());
		clause.infer(inferrer);
		return new SplitPair(clause,inferrer.getLeftCase(),inferrer.getRightCase());
	}
	
	/**
	 * Backtrack from this level up to and inclusive the level specified as a parameter.
	 * 
	 * @param level the level which must be backtracked
	 */
	private void backtrack(Level oldLevel, boolean proofDone) {
		debug("CaseSplitter: Backtracking datastructures, size of split stack: "+splits.size());
		if (proofDone) {
			debug("CaseSplitter: Clearing data structures");
			splits.clear();
			return;
		}
		debug("CaseSplitter: Backtracking from: "+oldLevel+", to: "+dispatcher.getLevel());
		Level tmp = oldLevel;
		int i = 0;
		while (!tmp.getParent().equals(dispatcher.getLevel())) {
			tmp = tmp.getParent();
			i++;
		}
		debug("CaseSplitter: Backtracking "+i+" levels");
		for (int j = 0; j < i; j++) {
			splits.pop();			
		}
		debug("CaseSplitter: Backtracking done, size of split stack: "+splits.size());
		nextCase = splits.pop();
//		assert splits.size() == dispatcher.getLevel().getHeight() : "Splits: "+splits.size();
	}
	
	public void registerDumper(Dumper dumper) {
	}
	
	private class CaseSplitIterator extends ConditionIterator<IClause> {
		
		public CaseSplitIterator(ResetIterator<IClause> nonUnitClauses) {
			super(nonUnitClauses);
		}

		public boolean isSelected(IClause clause) {
			return inferrer.canInfer(clause);
		}

	}

	private static class SplitPair {
		private IClause original;
		private IClause left,right;
		
		private SplitPair(IClause original, IClause left, IClause right) {
			this.original = original;
			this.left = left;
			this.right = right;
		}
	}

}
