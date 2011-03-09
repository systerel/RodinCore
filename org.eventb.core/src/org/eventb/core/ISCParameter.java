/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for event parameters in Event-B statically checked (SC) machines.
 * <p>
 * An SC variable is a variable that has been statically checked. An SC parameter
 * has a name that is returned by
 * {@link IRodinElement#getElementName()} and contains a type
 * that is accessed and manipulated via
 * {@link ISCIdentifierElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IRodinElement#getElementName()
 * @see ISCIdentifierElement#getType(FormulaFactory)
 * @see ISCIdentifierElement#setType(Type, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface ISCParameter extends ITraceableElement, ISCIdentifierElement {
	
	IInternalElementType<ISCParameter> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scParameter"); //$NON-NLS-1$


}
