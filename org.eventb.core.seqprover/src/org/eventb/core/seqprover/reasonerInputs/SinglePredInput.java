package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public class SinglePredInput implements IReasonerInput{
	
	private static final String SERIALIZATION_KEY = "pred";

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
		assert predicate != null;
		this.predicate = predicate;
		this.error = null;
	}

	public SinglePredInput(Hypothesis hypothesis) {
		predicate = hypothesis.getPredicate();
		error = null;
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
		reasonerInputSerializer.putPredicates(SERIALIZATION_KEY, predicate);
	}
	
	public SinglePredInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		final Predicate[] preds =
			reasonerInputSerializer.getPredicates(SERIALIZATION_KEY);
		if (preds.length != 1) {
			throw new SerializeException(new IllegalStateException("Expected exactly one predicate"));
		}
		predicate = preds[0];
	}

	public void applyHints(ReplayHints hints) {
		predicate = hints.applyHints(predicate);
		
	}

}
