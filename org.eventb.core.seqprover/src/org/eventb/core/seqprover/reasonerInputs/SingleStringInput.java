package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * @since 1.0
 */
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

	public SingleStringInput(IReasonerInputReader reader)
			throws SerializeException {

		this(reader.getString("singleString"));
	}
	
	public void serialize(IReasonerInputWriter writer) throws SerializeException {
		assert ! hasError();
		assert string != null;
		writer.putString("singleString",string);
	}

	public void applyHints(ReplayHints hints) {	
	}

}
