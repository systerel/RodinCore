/**
 * 
 */
package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.ReplayHints;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;

public class CombiInput implements IReasonerInput{
	
	IReasonerInput[] reasonerInputs;

	public CombiInput(IReasonerInput ...reasonerInputs){
		assert reasonerInputs != null;
		this.reasonerInputs = reasonerInputs;
	}
	

	/**
	 * @return Returns the reasonerInputs.
	 */
	public final IReasonerInput[] getReasonerInputs() {
		return reasonerInputs;
	}

	public boolean hasError() {
		for (IReasonerInput reasonerInput : reasonerInputs){
			if (reasonerInput.hasError()) return true;
		}
		return false;
	}

	public String getError() {
		StringBuilder str = new StringBuilder();
		for (IReasonerInput reasonerInput : reasonerInputs){
			if (reasonerInput.getError()!= null)
				str.append(reasonerInput.getError() + " ");
		}
		if (str.length() == 0) return null;
		return str.toString();
	}

	public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		IReasonerInputSerializer[] children = reasonerInputSerializer.makeSubInputSerializers(reasonerInputs.length);
		for (int i = 0; i < reasonerInputs.length; i++) {
			reasonerInputs[i].serialize(children[i]);
		}
	}

	public void applyHints(ReplayHints hints) {
		for (int i = 0; i < reasonerInputs.length; i++) {
			reasonerInputs[i].applyHints(hints);
		}
	}

	
}