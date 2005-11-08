/**
 * 
 */
package org.eventb.core;

import java.util.ArrayList;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.result.type.TypeEnvironment;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.InternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 * 
 * A sequent is a tuple (NAME, TYPE_ENV, HYP, GOAL, HINTS)
 * 
 * <p>
 * The name (NAME) identifies uniquely a sequent (resp. proof obligation) in a PO file.
 * The type environment (TYPE_ENV) specifies type of identifiers local to the sequent.
 * (The type environment is contained in the sequent in form of POTypeExpressions.)
 * There is one hypothesis (HYP) in the sequent. It is of type POHypothesis.
 * There is one goal (GOAL) in the sequent. It is a POPredicate or a POPredicateForm.
 * Hints (HINTS) are associated with a sequent in form of attributes.
 * </p>
 *
 */
public class POSequent extends InternalElement {

	public static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".posequent";
	
	private TypeEnvironment typeEnvironment = null;
	
	private POHypothesis hypothesis = null;
	
	private POAnyPredicate goal = null;
	
	/**
	 * @param name
	 * @param parent
	 */
	public POSequent(String name, IRodinElement parent) {
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
	
	public String getName() {
		return getElementName();
	}
	
	public TypeEnvironment getTypeEnvironment() throws RodinDBException {
		FormulaFactory factory = ((POFile) getOpenable()).getFormulaFactory();
		if(typeEnvironment == null) {
			typeEnvironment = new TypeEnvironment();
			for(IRodinElement element : getChildrenOfType(POIdentifier.ELEMENT_TYPE)) {
				POIdentifier typeExpression = (POIdentifier) element;
				String name = typeExpression.getElementName();
				Expression expr = factory.parseExpression(typeExpression.getContents()).getParsedExpression();
				
				assert expr != null;
				typeEnvironment.addIdent(factory.makeFreeIdentifier(name, null), expr);
			}
		}
		return typeEnvironment;
	}
	
	public POHypothesis getHypothesis() throws RodinDBException {
		if(hypothesis == null) {
			ArrayList<IRodinElement> list = getChildrenOfType(POHypothesis.ELEMENT_TYPE);
			
			assert list.size() == 1;
			
			hypothesis = (POHypothesis) list.get(0);
		}
		return hypothesis;
	}
	
	public POAnyPredicate getGoal() throws RodinDBException {
		if(goal == null) {
			ArrayList<IRodinElement> list = getChildrenOfType(POPredicate.ELEMENT_TYPE);
			if(list.size() == 0)
				list = getChildrenOfType(POPredicateForm.ELEMENT_TYPE);
			
			assert list.size() == 1;
			
			goal = (POAnyPredicate) list.get(0);
		}
		return goal;
	}
	
	public String getHint(String hintName) {
		// TODO implement hints as attributes
		return null;
	}

}
