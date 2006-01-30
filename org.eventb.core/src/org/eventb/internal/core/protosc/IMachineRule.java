/**
 * 
 */
package org.eventb.internal.core.protosc;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public interface IMachineRule {

	public boolean verify(IInternalElement element, MachineCache cache, ISCProblemList problemList) throws RodinDBException;
	
}
