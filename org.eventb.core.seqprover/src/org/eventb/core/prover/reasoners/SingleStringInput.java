package org.eventb.core.prover.reasoners;

import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.IReasonerInput;
import org.eventb.core.prover.ReplayHints;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;

public class SingleStringInput implements IReasonerInput{
	
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
