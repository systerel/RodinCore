package org.eventb.core.prover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;

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
		
	}

}
