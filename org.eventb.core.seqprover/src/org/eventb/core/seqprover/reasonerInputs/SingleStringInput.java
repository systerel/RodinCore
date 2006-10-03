package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public class SingleStringInput implements IReasonerInput{
	
	private String string;
	private String error;
	
	public SingleStringInput(String string){
		assert string != null;
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
		this(reasonerInputSerializer.getString("singleString"));
	}
	
	public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		assert ! hasError();
		assert string != null;
		reasonerInputSerializer.putString("singleString",string);
	}

	public void applyHints(ReplayHints hints) {
		// TODO Auto-generated method stub
		
	}

}
