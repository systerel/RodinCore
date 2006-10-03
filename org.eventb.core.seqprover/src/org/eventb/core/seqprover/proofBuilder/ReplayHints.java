package org.eventb.core.seqprover.proofBuilder;

import java.util.HashMap;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IProofRule.IAnticident;

public class ReplayHints {
	
	private static final FormulaFactory FF = FormulaFactory.getDefault();
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
		
	public void addHints(IAnticident old,IAnticident current){

		if (old.getAddedFreeIdents().length == 0) return;
		
		for (int i = 0; i < old.getAddedFreeIdents().length; i++) {
			if
			((i < current.getAddedFreeIdents().length) &&
			(! old.getAddedFreeIdents()[i].equals(current.getAddedFreeIdents()[i])))
			{
				this.freeVarRename.put(old.getAddedFreeIdents()[i],current.getAddedFreeIdents()[i]);
			}
		}	
	}
	

	public void applyHints(IReasonerInput reasonerInput) {
		reasonerInput.applyHints(this);
		
	}
	
	public Predicate applyHints(Predicate predicate) {
		return predicate.substituteFreeIdents(freeVarRename,FF);
	}

	public Expression applyHints(Expression expression) {
		return expression.substituteFreeIdents(freeVarRename,FF);
	}
	
	

}
