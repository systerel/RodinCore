/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IRefinesEvent;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B event refinement as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IRefinesEvent</code>.
 * </p>
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public class RefinesEvent extends InternalElement implements IRefinesEvent {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public RefinesEvent(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType<IRefinesEvent> getElementType() {
		return ELEMENT_TYPE;
	}
	
	@Override
	public boolean hasAbstractEventLabel() throws RodinDBException {
		return hasAttribute(EventBAttributes.TARGET_ATTRIBUTE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IRefinesEvent#getAbstractMachineName()
	 */
	@Override
	public String getAbstractEventLabel() throws RodinDBException {
		return getAttributeValue(EventBAttributes.TARGET_ATTRIBUTE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IRefinesEvent#setAbstractMachineName(java.lang.String)
	 */
	@Override
	public void setAbstractEventLabel(String label, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.TARGET_ATTRIBUTE, label, monitor);
	}

}
