/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
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
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ISCContextRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B context extensions as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IExtendsContext</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ExtendsContext extends EventBElement implements IExtendsContext {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public ExtendsContext(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IExtendsContext> getElementType() {
		return ELEMENT_TYPE;
	}
	
	@Override
	public boolean hasAbstractContextName() 
	throws RodinDBException {
		return hasAttribute(EventBAttributes.TARGET_ATTRIBUTE);
	}

	@Override
	public String getAbstractContextName() 
	throws RodinDBException {
		return getAttributeValue(EventBAttributes.TARGET_ATTRIBUTE);
	}

	@Override
	public ISCContextRoot getAbstractSCContext() throws RodinDBException {
		final String bareName = getAbstractContextName();
		return getEventBProject().getSCContextRoot(bareName);
	}

	@Override
	public IContextRoot getAbstractContextRoot() throws RodinDBException {
		final String bareName = getAbstractContextName();
		return getEventBProject().getContextRoot(bareName);
	}

	@Override
	public void setAbstractContextName(String name, IProgressMonitor monitor) 
	throws RodinDBException {
		setAttributeValue(EventBAttributes.TARGET_ATTRIBUTE, name, monitor);
	}

}
