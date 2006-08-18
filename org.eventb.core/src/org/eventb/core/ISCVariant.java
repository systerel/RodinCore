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

/**
 * Common protocol for Event-B SC variants.
 * <p>
 * An SC variant is a variant that has been statically checked. An SC variant
 * has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}. It contains an
 * expression that is accessed and manipulated via
 * {@link org.eventb.core.ISCExpressionElement}. This interface itself does not
 * contribute any method.
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
 * 
 */
public interface ISCVariant extends ITraceableElement, ISCExpressionElement {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scVariant"; //$NON-NLS-1$

}
