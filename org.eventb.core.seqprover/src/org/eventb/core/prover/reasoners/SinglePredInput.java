package org.eventb.core.prover.reasoners;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.sequent.Hypothesis;

public class SinglePredInput implements ReasonerInput{
	
	private Predicate predicate;
	private String error;
	
	public SinglePredInput(String predString, ITypeEnvironment typeEnv){
		
		predicate = Lib.parsePredicate(predString);
		if (predString == null)
		{
			error = "Parse error for predicate: "+ predString;
			return;
		}
		if (! Lib.typeCheckClosed(predicate,typeEnv)){
			error = "Type check failed for Predicate: "+predicate;
			predicate = null;
			return;
		}		
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

	public SinglePredInput(SerializableReasonerInput serializableReasonerInput) {
		predicate = serializableReasonerInput.getPredicate("predicate");
		if (predicate == null){
			error = "Invalid or nonexistent Predicate";
			return;
		}
	}

	public SerializableReasonerInput genSerializable(){
		SerializableReasonerInput serializableReasonerInput 
		= new SerializableReasonerInput();
		assert predicate != null;
		serializableReasonerInput.putPredicate("predicate",predicate);
		return serializableReasonerInput;
	}

}
