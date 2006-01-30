/**
 * 
 */
package org.eventb.internal.core.protosc;

import org.rodinp.core.IInternalElement;

/**
 * @author halstefa
 *
 */
public interface IContextRule {
	
	public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList);

}
