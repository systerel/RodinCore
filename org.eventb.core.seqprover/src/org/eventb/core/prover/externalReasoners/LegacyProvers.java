package org.eventb.core.prover.externalReasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExternalReasoner;

/**
 * Common implementation for running an external prover provided by B4free.
 * 
 * @author Laurent Voisin
 */
public abstract class LegacyProvers implements IExternalReasoner {
	
	public static abstract class Input implements IExtReasonerInput{
		
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
