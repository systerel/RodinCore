package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ReplayHints;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.sequent.Hypothesis;

public class SinglePredInput implements IReasonerInput{
	
	private Predicate predicate;
	private String error;
	
	public SinglePredInput(String predString, ITypeEnvironment typeEnv){
		
		predicate = Lib.parsePredicate(predString);
		if (predicate == null)
		{
			error = "Parse error for predicate: "+ predString;
			return;
		}
		if (! Lib.typeCheckClosed(predicate,typeEnv)){
			error = "Type check failed for Predicate: "+predicate;
			predicate = null;
			return;
		}
		error = null;
	}
	
	public SinglePredInput(Predicate predicate){
		this.predicate = predicate;
		this.error = null;
	}

	public SinglePredInput(Hypothesis hypothesis) {
		predicate = hypothesis.getPredicate();
	}
	
	public final boolean hasError(){
		return (error != null);
	}
	
	/**
	 * @return Returns the error.
	 */
	public final String getError() {
		return error;
	}

	/**
	 * @return Returns the predicate.
	 */
	public final Predicate getPredicate() {
		return predicate;
	}

	public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		assert ! hasError();
		assert predicate != null;
		reasonerInputSerializer.putPredicate("singlePredicate",predicate);
	}
	
	public SinglePredInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
			this(reasonerInputSerializer.getPredicate("singlePredicate"));
	}

	public void applyHints(ReplayHints hints) {
		predicate = hints.applyHints(predicate);
		
	}

}
