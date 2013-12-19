/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
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

import static org.eventb.internal.core.Util.newCoreException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCSeesContext;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B SC sees clause for a machine, as an extension of
 * the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCSeesContext</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class SCSeesContext extends EventBElement implements
		ISCSeesContext {

	/**
	 * Constructor used by the Rodin database.
	 */
	public SCSeesContext(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISCSeesContext> getElementType() {
		return ELEMENT_TYPE;
	}

	private IRodinElement getSeenSCContextHandle() throws RodinDBException {
		return getAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE);
	}

	@Override
	public ISCContextRoot getSeenSCContext() throws CoreException {
		IRodinElement target = getSeenSCContextHandle();
		if (target instanceof IRodinFile) {
			IRodinFile rf = (IRodinFile) target;
			if(rf.getRoot() instanceof ISCContextRoot){
				return (ISCContextRoot) rf.getRoot();
			}
		}
		throw newCoreException(
				Messages.database_SCSeesContextTypeFailure, this);
	}

	@Override
	public void setSeenSCContext(IRodinFile seenSCContext,
			IProgressMonitor monitor) throws RodinDBException {

		setAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE,
				seenSCContext, monitor);
	}

}
