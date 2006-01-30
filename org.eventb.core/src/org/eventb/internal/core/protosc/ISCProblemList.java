package org.eventb.internal.core.protosc;

import org.rodinp.core.IInternalElement;

public interface ISCProblemList {

	public void addProblem(SCProblem problem);
	
	public void addProblem(IInternalElement element, String message, int severity);
	
}
