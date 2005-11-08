/**
 * 
 */
package org.eventb.core;

import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinElement;
import org.rodinp.core.UnnamedInternalElement;

/**
 * @author halstefa
 * 
 * A Hypothesis is a pair (GOBAL_HYP, LOCAL_HYP)
 * <p>
 * The contents of the hypothesis is a reference to a predicate set (GLOBAL_HYP).
 * The children of the hypothesis are the local hypotheses (LOCAL_HYP).
 * The children are of either of type POPredicate or POPredicateForm.
 * </p>
 */
public class POHypothesis extends UnnamedInternalElement {

	public static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".pohypothesis";
	
	private POPredicateSet gobalHypothesis = null;
	
	private POAnyPredicate[] localHypothesis = null;
	
	/**
	 * @param type
	 * @param parent
	 */
	public POHypothesis(String type, RodinElement parent) {
		super(type, parent);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public POPredicateSet getGlobalHypothesis() throws RodinDBException {
		
		if(gobalHypothesis == null) {
			gobalHypothesis = ((POFile) getOpenable()).getPredicateSet(getContents());
		
			assert gobalHypothesis != null;
		}
		
		return gobalHypothesis;
	}
	
	public POAnyPredicate[] getLocalHypothesis() throws RodinDBException {
		if(localHypothesis == null) {
			localHypothesis = (POAnyPredicate[]) getChildren();
		}
		return localHypothesis;
	}

}
