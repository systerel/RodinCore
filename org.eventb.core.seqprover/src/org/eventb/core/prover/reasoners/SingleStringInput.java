package org.eventb.core.prover.reasoners;

import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReplayHints;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;

public class SingleStringInput implements ReasonerInput{
	
	private String string;
	private String error;
	
	public SingleStringInput(String string){
		this.string = string;
		this.error = null;
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
	 * @return Returns the string.
	 */
	public final String getString() {
		return string;
	}

	public SingleStringInput(SerializableReasonerInput serializableReasonerInput) {
		string = serializableReasonerInput.getString("string");
		if (string == null){
			error = "Invalid or nonexistent String";
			return;
		}
	}

	public SerializableReasonerInput genSerializable(){
		SerializableReasonerInput serializableReasonerInput 
		= new SerializableReasonerInput();
		assert string != null;
		serializableReasonerInput.putString("string",string);
		return serializableReasonerInput;
	}

	public SingleStringInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		new SingleStringInput(reasonerInputSerializer.getString("singleString"));
	}
	
	public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		assert ! hasError();
		reasonerInputSerializer.putString("singleString",string);
	}

	public void applyHints(ReplayHints hints) {
		// TODO Auto-generated method stub
		
	}

}
