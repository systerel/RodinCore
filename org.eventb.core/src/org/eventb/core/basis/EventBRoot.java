/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
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
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOStampedElement;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCMachineRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Common implementation for event-B root elements.
 * 
 * @author Stefan Hallerstede
 * @author Laurent Voisin
 * @since 1.0
 */
public abstract class EventBRoot extends EventBElement implements IEventBRoot,
		IPOStampedElement {

	protected EventBRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	public final String getComponentName() {
		return getElementName();
	}

	public final IContextRoot getContextRoot() {
		if (this instanceof IContextRoot) {
			return (IContextRoot) this.getMutableCopy();
		}
		return getEventBProject().getContextRoot(getElementName());
	}

	public final IMachineRoot getMachineRoot() {
		if (this instanceof IMachineRoot) {
			return (IMachineRoot) this.getMutableCopy();
		}
		return getEventBProject().getMachineRoot(getElementName());
	}

	public final IPRRoot getPRRoot() {
		if (this instanceof IPRRoot) {
			return (IPRRoot) this.getMutableCopy();
		}
		return getEventBProject().getPRRoot(getElementName());
	}

	public final ISCContextRoot getSCContextRoot() {
		// Do not optimize here due to temporary files.
		return getEventBProject().getSCContextRoot(getElementName());
	}

	public final ISCMachineRoot getSCMachineRoot() {
		// Do not optimize here due to temporary files.
		return getEventBProject().getSCMachineRoot(getElementName());
	}

	public final IPORoot getPORoot() {
		// Do not optimize here due to temporary files.
		return getEventBProject().getPORoot(getElementName());
	}

	public final IPSRoot getPSRoot() {
		if (this instanceof IPSRoot) {
			return (IPSRoot) this.getMutableCopy();
		}
		return getEventBProject().getPSRoot(getElementName());
	}
	
	public void setConfiguration(String configuration, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.CONFIGURATION_ATTRIBUTE, configuration, monitor);
	}
	
	public String getConfiguration() throws RodinDBException {
		return getAttributeValue(EventBAttributes.CONFIGURATION_ATTRIBUTE);
	}
	
	public boolean hasConfiguration() throws RodinDBException {
		return hasAttribute(EventBAttributes.CONFIGURATION_ATTRIBUTE);
	}

}
