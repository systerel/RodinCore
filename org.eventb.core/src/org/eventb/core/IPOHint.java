/**
 * 
 */
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * A hint for a proof of a proof obligation.
 * 
 * @author halstefa
 *
 */
public interface IPOHint extends IInternalElement {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poHint"; //$NON-NLS-1$
	
	String getName();
	void setValue(String value, IProgressMonitor monitor) throws RodinDBException;
	String getValue(IProgressMonitor monitor) throws RodinDBException;
}
