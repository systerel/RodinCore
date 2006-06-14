package org.eventb.internal.core.pom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.HypothesesManagement;
import org.eventb.core.prover.sequent.Hypothesis;

public class ReplayHints {
	
	private HashMap<FreeIdentifier,Expression> freeVarRename;
	
	public ReplayHints(){
		this.freeVarRename = new HashMap<FreeIdentifier,Expression>();
	}
	
	public ReplayHints(ReplayHints replayHints){
		this.freeVarRename = new HashMap<FreeIdentifier,Expression>(replayHints.freeVarRename);
	}
	
	@Override
	public ReplayHints clone(){
		return new ReplayHints(this);
	}
	
	public boolean isEmpty(){
		return this.freeVarRename.isEmpty();
	}
	
//	private void addRename(FreeIdentifier from,FreeIdentifier to){
//		assert (! this.freeVarRename.containsKey(from));
//		this.freeVarRename.put(from,to);
//	}
	
	public void addHints(Anticident old,Anticident current){

		if (old.addedFreeIdentifiers.length == 0) return;
		
		for (int i = 0; i < old.addedFreeIdentifiers.length; i++) {
			if
			((i < current.addedFreeIdentifiers.length) &&
			(! old.addedFreeIdentifiers[i].equals(current.addedFreeIdentifiers[i])))
			{
				this.freeVarRename.put(old.addedFreeIdentifiers[i],current.addedFreeIdentifiers[i]);
			}
		}	
	}
	
	public void applyHints (SerializableReasonerInput input){
		for (Map.Entry<String,Predicate> entry : input.predicates.entrySet()) {
			Predicate newPred = entry.getValue().substituteFreeIdents(
					this.freeVarRename,FormulaFactory.getDefault());
			input.predicates.put(entry.getKey(),newPred);
		}
		
		for (Map.Entry<String,Expression> entry : input.expressions.entrySet()) {
			Expression newExpr = entry.getValue().substituteFreeIdents(
					this.freeVarRename,FormulaFactory.getDefault());
			input.expressions.put(entry.getKey(),newExpr);
		}
		if (input.hypAction != null) {
			Set<Hypothesis> newHyps = new HashSet<Hypothesis>();
			for (Hypothesis hyp : input.hypAction.getHyps()) {
				newHyps.add(
					new Hypothesis(hyp.getPredicate().substituteFreeIdents(
						this.freeVarRename,FormulaFactory.getDefault()))
						);
			}
			input.hypAction = 
				new HypothesesManagement.Action(input.hypAction.getType(),newHyps);
		}
	}

}
