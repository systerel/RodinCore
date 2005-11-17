/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPOIdentifier;
import org.eventb.core.ast.Expression;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;

/**
 * @author halstefa
 * 
 * A type expression is a pair (NAME, EXPR).
 * <p>
 * It defines a type with name NAME and described by expression EXPR.
 * </p>
 *
 */
public class POIdentifier extends InternalElement implements IPOIdentifier {

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
