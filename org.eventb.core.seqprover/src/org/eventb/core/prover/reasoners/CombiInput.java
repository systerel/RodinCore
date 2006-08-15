/**
 * 
 */
package org.eventb.core.prover.reasoners;

import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReplayHints;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;

public class CombiInput implements ReasonerInput{
	
	ReasonerInput[] reasonerInputs;

	public CombiInput(ReasonerInput ...reasonerInputs){
		assert reasonerInputs != null;
		this.reasonerInputs = reasonerInputs;
	}
	

	/**
	 * @return Returns the reasonerInputs.
	 */
	public final ReasonerInput[] getReasonerInputs() {
		return reasonerInputs;
	}

	public boolean hasError() {
		for (ReasonerInput reasonerInput : reasonerInputs){
			if (reasonerInput.hasError()) return true;
		}
		return false;
	}

	public String getError() {
		StringBuilder str = new StringBuilder();
		for (ReasonerInput reasonerInput : reasonerInputs){
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