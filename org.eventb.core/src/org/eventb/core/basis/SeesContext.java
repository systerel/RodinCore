/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 ******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IContextRoot;
import org.eventb.core.ISeesContext;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B sees relationship as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ISeesContext</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public class SeesContext extends EventBElement implements ISeesContext {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SeesContext(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISeesContext> getElementType() {
		return ELEMENT_TYPE;
	}

	public IRodinFile getSeenSCContext() throws RodinDBException {
		final String bareName = getSeenContextName();
		return getEventBProject().getSCContextFile(bareName);
	}
	
	public IContextRoot getSeenContextRoot() throws RodinDBException {
		final String bareName = getSeenContextName();
		return getEventBProject().getContextRoot(bareName);
	}
	
	public boolean hasSeenContextName() throws RodinDBException {
		return hasAttribute(EventBAttributes.TARGET_ATTRIBUTE);
	}

	public String getSeenContextName() throws RodinDBException {
		return getAttributeValue(EventBAttributes.TARGET_ATTRIBUTE);
	}

	public void setSeenContextName(String name, IProgressMonitor monitor) 
	throws RodinDBException {
		setAttributeValue(EventBAttributes.TARGET_ATTRIBUTE, name, monitor);
	}

	@Deprecated
	public void setSeenContextName(String name) throws RodinDBException {
		setSeenContextName(name, null);
	}

}
