/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;

/**
 * Common protocol for variables in Event-B statically checked (SC) machines.
 * <p>
 * An SC variable is a variable that has been statically checked. An SC variable
 * has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()} and contains a type
 * that is accessed and manipulated via
 * {@link org.eventb.core.ISCIdentifierElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * @see org.eventb.core.ISCIdentifierElement#getType(FormulaFactory)
 * @see org.eventb.core.ISCIdentifierElement#setType(Type)
 * 
 * @author Stefan Hallerstede
 * 
 */
public interface ISCVariable extends ISCIdentifierElement {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scVariable"; //$NON-NLS-1$

	// No additional method

}
