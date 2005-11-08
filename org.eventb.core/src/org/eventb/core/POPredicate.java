/**
 * 
 */
package org.eventb.core;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.result.type.TypeEnvironment;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinElement;

/**
 * @author halstefa
 *
 * A predicate has a name associated as its attribute 
 * and the "predicate value" in the contents. 
 */
public class POPredicate extends POAnyPredicate {

	public POPredicate(RodinElement parent) {
		super(ELEMENT_TYPE, parent);
		// TODO Auto-generated constructor stub
	}

	public static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".popredicate";
	
	private Predicate predicate = null;
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public Predicate getPredicate(TypeEnvironment environment) throws RodinDBException {
		if(predicate == null) {
			FormulaFactory factory = ((POFile) getOpenable()).getFormulaFactory();
			predicate = factory.parsePredicate(getContents()).getParsedPredicate();
			boolean correctlyTyped = predicate.isCorrectlyTyped(environment, factory).isCorrectlyTyped();
			
			assert correctlyTyped;
		}
		return predicate;
	}
	
	public String getName() {
		return null;
	}

}
