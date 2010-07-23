/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCVariable;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B SC variable as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ISCVariable</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public class SCVariable extends SCIdentifierElement implements ISCVariable {
	
	public SCVariable(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public IInternalElementType<ISCVariable> getElementType() {
		return ISCVariable.ELEMENT_TYPE;
	}

	@Override
	public boolean isAbstract() throws RodinDBException {
		return getAttributeValue(EventBAttributes.ABSTRACT_ATTRIBUTE);
	}

	@Override
	public boolean isConcrete() throws RodinDBException {
		return getAttributeValue(EventBAttributes.CONCRETE_ATTRIBUTE);
	}

	@Override
	public void setAbstract(boolean value, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(EventBAttributes.ABSTRACT_ATTRIBUTE, value, monitor);
	}

	@Override
	public void setConcrete(boolean value, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(EventBAttributes.CONCRETE_ATTRIBUTE, value, monitor);
	}

	@Override
	@Deprecated
	public void setForbidden(boolean value, IProgressMonitor monitor) throws RodinDBException {
		setConcrete(!value, monitor);
	}

	@Override
	@Deprecated
	public boolean isForbidden() throws RodinDBException {
		return !isConcrete();
	}

	@Override
	@Deprecated
	public void setPreserved(boolean value, IProgressMonitor monitor) throws RodinDBException {
		setAbstract(value, monitor);
		setConcrete(value, null);
	}

	@Override
	@Deprecated
	public boolean isPreserved() throws RodinDBException {
		return isAbstract() && isConcrete();
	}

}
