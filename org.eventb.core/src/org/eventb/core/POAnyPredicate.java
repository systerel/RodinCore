/**
 * 
 */
package org.eventb.core;

import org.rodinp.core.RodinElement;
import org.rodinp.core.UnnamedInternalElement;

/**
 * @author halstefa
 * 
 * Abstract class to represent POPredicates and POPredicateForms uniformly
 *
 */
public abstract class POAnyPredicate extends UnnamedInternalElement {

	public POAnyPredicate(String type, RodinElement parent) {
		super(type, parent);
	}

}
