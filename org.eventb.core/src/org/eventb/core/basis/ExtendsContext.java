/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ISCContextFile;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

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
 */
public class ExtendsContext extends InternalElement implements IExtendsContext {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public ExtendsContext(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IExtendsContext#getAbstractContextName()
	 */
	public String getAbstractContextName() throws RodinDBException {
		return getContents();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IExtendsContext#getAbstractSCContext()
	 */
	public ISCContextFile getAbstractSCContext() throws RodinDBException {
		final String bareName = getAbstractContextName();
		final String scName = EventBPlugin.getSCContextFileName(bareName);
		final IRodinProject project = getRodinProject();
		return (ISCContextFile) project.getRodinFile(scName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IExtendsContext#setAbstractContextName(java.lang.String)
	 */
	public void setAbstractContextName(String name) throws RodinDBException {
		setContents(name);
	}
}
