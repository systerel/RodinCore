/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for Event-B SC witnesses.
 * <p>
 * An SC witness is a witness that has been statically checked. An SC witness
 * has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}. It contains an
 * expression that is accessed and manipulated via
 * {@link org.eventb.core.ISCExpressionElement} and an identifier that is accessed and
 * manipulated by the contributed methods <code>getIdentifier()</code> and 
 * <code>setIdentifier()</code>. 
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * @see org.eventb.core.ISCExpressionElement#getExpression(FormulaFactory,
 *      ITypeEnvironment)
 * @see org.eventb.core.ISCExpressionElement#setExpression(Expression)
 * 
 * @author Stefan Hallerstede
 */
public interface ISCWitness extends ITraceableElement, ILabeledElement, ISCPredicateElement {

	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scWitness"); //$NON-NLS-1$

}
