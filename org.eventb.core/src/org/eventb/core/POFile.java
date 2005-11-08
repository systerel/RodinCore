/**
 * 
 */
package org.eventb.core;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.result.type.TypeEnvironment;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.InternalElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinFile;

/**
 * @author halstefa
 *
 */
public class POFile extends RodinFile {

	public static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".pofile";
	
	private FormulaFactory factory = null;
	
	private TypeEnvironment typeEnvironment = null;
	
	private POSequent[] sequents = null;

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public POFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public POPredicateSet getPredicateSet(String name) throws RodinDBException {
		InternalElement element = getInternalElement(POPredicateSet.ELEMENT_TYPE, name);
		if(element.exists())
			return (POPredicateSet) element;
		else
			return null;
	}
	
	public TypeEnvironment getTypeEnvironment() throws RodinDBException {
		getFormulaFactory();
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

	public POSequent[] getSequents() throws RodinDBException {
		if(sequents == null) {
			ArrayList<IRodinElement> list = getChildrenOfType(POSequent.ELEMENT_TYPE);
			sequents = new POSequent[list.size()];
			list.toArray(sequents);
		}
		return sequents;
	}

	/**
	 * @return Returns the formula factory.
	 */
	public FormulaFactory getFormulaFactory() {
		if(factory == null)
			factory = new FormulaFactory();
		return factory;
	}

}
