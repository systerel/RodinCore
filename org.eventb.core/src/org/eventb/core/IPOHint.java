/**
 * 
 */
package org.eventb.core;

import org.rodinp.core.IInternalElement;

/**
 * A hint for a proof of a proof obligation.
 * 
 * @author halstefa
 *
 */
public interface IPOHint extends IInternalElement {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poHint"; //$NON-NLS-1$
	
	String getName();
}
