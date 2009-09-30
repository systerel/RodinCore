package org.eventb.core.seqprover.proofBuilder;

import java.util.HashMap;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IProofRule.IAntecedent;



/**
 * This is a class that stores information about free identifier renaming
 * that can be used to rename reasoner inputs in order to support refactoring
 * of free identifier names in proofs.
 * 
 * 
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public class ReplayHints {
//	 TODO : Rename to freeIdentRename
//	 TODO : Make interface & cleanup
	
	
	private static final FormulaFactory factory = FormulaFactory.getDefault();
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
		
	public void addHints(IAntecedent old,IAntecedent current){

		if (old.getAddedFreeIdents().length == 0) return;
		
		for (int i = 0; i < old.getAddedFreeIdents().length; i++) {
			if
			((i < current.getAddedFreeIdents().length) &&
			(! old.getAddedFreeIdents()[i].equals(current.getAddedFreeIdents()[i])) &&
			( old.getAddedFreeIdents()[i].getType().equals(current.getAddedFreeIdents()[i].getType())))
			{
				this.freeVarRename.put(old.getAddedFreeIdents()[i],current.getAddedFreeIdents()[i]);
			}
		}	
	}
	

	public void applyHints(IReasonerInput reasonerInput) {
		reasonerInput.applyHints(this);
		
	}
	
	public Predicate applyHints(Predicate predicate) {
		if (predicate == null) return null;
		return predicate.substituteFreeIdents(freeVarRename,factory);
	}

	public Expression applyHints(Expression expression) {
		if (expression == null) return null;
		return expression.substituteFreeIdents(freeVarRename,factory);
	}
	
	

}
