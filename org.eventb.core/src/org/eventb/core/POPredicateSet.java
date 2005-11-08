/**
 * 
 */
package org.eventb.core;

import java.util.ArrayList;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.InternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 * 
 * A predicate set consists of other predicate sets and some predicates.
 * Note, that predicates can be identified by their NAME attributes.
 *
 */
public class POPredicateSet extends InternalElement {

	public static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".popredicateset";
	
	private POPredicateSet[] predicateSets = null;
	private POPredicate[] predicates = null;
	
	/**
	 * @param name
	 * @param parent
	 */
	public POPredicateSet(String name, IRodinElement parent) {
		super(name, parent);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public POPredicate[] getPredicates() throws RodinDBException {
		if(predicates == null) {
			ArrayList<IRodinElement> list = getChildrenOfType(POPredicate.ELEMENT_TYPE);
			predicates = new POPredicate[list.size()];
			list.toArray(predicates);
		}
		return predicates;
	}
	
	public POPredicateSet[] getPredicateSets() throws RodinDBException {
		if(predicateSets == null) {
			ArrayList<IRodinElement> list = getChildrenOfType(POPredicateSet.ELEMENT_TYPE);
			predicateSets = new POPredicateSet[list.size()];
			list.toArray(predicateSets);
		}
		return predicateSets;
	}

}
