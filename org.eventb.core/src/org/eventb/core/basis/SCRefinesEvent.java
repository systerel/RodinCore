/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.internal.core.Util.newCoreException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B SC refines clauses for events, as an extension of
 * the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCRefinesEvent</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class SCRefinesEvent extends EventBElement implements ISCRefinesEvent {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCRefinesEvent(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISCRefinesEvent> getElementType() {
		return ELEMENT_TYPE;
	}
	
	private IRodinElement getAbstractSCEventHandle() throws RodinDBException {
		return getAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE);
	}

	@Override
	public ISCEvent getAbstractSCEvent() throws CoreException {
		IRodinElement target = getAbstractSCEventHandle();
		if (!(target instanceof ISCEvent)) {
			throw newCoreException(Messages.database_SCRefinesEventTypeFailure,
					this);
		}
		return (ISCEvent) target;
	}

	@Override
	public void setAbstractSCEvent(ISCEvent abstractSCEvent, IProgressMonitor monitor) 
	throws RodinDBException {
		setAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE, abstractSCEvent, monitor);
	}

}
