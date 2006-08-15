package org.eventb.core.prover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;

/**
 * Common implementation for running a external automated provers.
 * 
 * @author Laurent Voisin
 * @author Farhad Mehta
 */

// TODO : remove class
public abstract class LegacyProvers implements Reasoner {
	
	
	public static abstract class Input implements ReasonerInput{
		
		public final long timeOutDelay;
		public final IProgressMonitor monitor;
		
		protected static final long DEFAULT_DELAY = 30 * 1000;
		
		public Input() {
			// Defaults to 30 seconds 
			this.timeOutDelay = DEFAULT_DELAY;
			this.monitor = null;
		}

		public Input(long timeOutDelay) {
			this.timeOutDelay  = timeOutDelay;
			this.monitor = null;
		}
		
		public Input(IProgressMonitor monitor) {
			this.timeOutDelay  = DEFAULT_DELAY;
			this.monitor = monitor;
		}

		public Input(long timeOutDelay, IProgressMonitor monitor) {
			this.timeOutDelay  = timeOutDelay;
			this.monitor = monitor;
		}
		
		public Input(SerializableReasonerInput serializableReasonerInput) {
			this.timeOutDelay = Long.parseLong(serializableReasonerInput.getString("timeOutDelay"));
			this.monitor = null;
		}
		
		public SerializableReasonerInput genSerializable(){
			SerializableReasonerInput serializableReasonerInput 
			= new SerializableReasonerInput();
			serializableReasonerInput.putString("timeOutDelay",String.valueOf(this.timeOutDelay));
			return serializableReasonerInput;
		}
		
//		public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
//			reasonerInputSerializer.putString("timeOutDelay",Long.toString(timeOutDelay));		
//		}
//
//		public boolean hasError() {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		public String getError() {
//			// TODO Auto-generated method stub
//			return null;
//		}
	}

}
