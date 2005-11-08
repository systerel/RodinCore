/**
 * 
 */
package org.eventb.core;

import org.eventb.core.ast.Expression;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.InternalElement;

/**
 * @author halstefa
 * 
 * A type expression is a pair (NAME, EXPR).
 * <p>
 * It defines a type with name NAME and described by expression EXPR.
 * </p>
 *
 */
public class POIdentifier extends InternalElement {

	public static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poidentifier";

	public POIdentifier(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public String getName() {
		return getElementName();
	}
	
	public Expression getType() {
		// TODO parse type expression
		return null;
	}


}
