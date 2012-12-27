/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCExtendsContext;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B SC extends clause for a context, as an extension of
 * the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCExtendsContext</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public class SCExtendsContext extends EventBElement implements
		ISCExtendsContext {

	/**
	 * Constructor used by the Rodin database.
	 */
	public SCExtendsContext(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISCExtendsContext> getElementType() {
		return ELEMENT_TYPE;
	}

	private IRodinElement getAbstractSCContextHandle() throws RodinDBException {
		return getAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE);
	}

	@Override
	public ISCContextRoot getAbstractSCContext() throws RodinDBException {
		IRodinElement target = getAbstractSCContextHandle();
		if (!(target instanceof ISCContextRoot)) {
			throw Util.newRodinDBException(
					Messages.database_SCExtendsContextTypeFailure, this);
		}
		return (ISCContextRoot) target;
	}

	@Override
	public void setAbstractSCContext(ISCContextRoot abstractSCContext,
			IProgressMonitor monitor) throws RodinDBException {

		setAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE,
				abstractSCContext, monitor);
	}

}
