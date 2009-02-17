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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOStampedElement;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.internal.core.EventBProject;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Common implementation for event-B files.
 * 
 * @author Stefan Hallerstede
 * @author Laurent Voisin
 */
public abstract class EventBRoot extends InternalElement implements IEventBRoot,
		IPOStampedElement {

	protected EventBRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	public final String getComponentName() {
		return getElementName();
	}

	public IEventBProject getEventBProject() {
		return new EventBProject(getRodinProject());
	}

	public final IContextRoot getContextRoot() {
		if (this instanceof IContextRoot) {
			return (IContextRoot) this.getMutableCopy();
		}
		final String name = EventBPlugin.getContextFileName(getComponentName());
		return (IContextRoot) getRodinProject().getRodinFile(name).getRoot();
	}

	public final IMachineRoot getMachineRoot() {
		if (this instanceof IMachineRoot) {
			return (IMachineRoot) this.getMutableCopy();
		}
		final String name = EventBPlugin.getMachineFileName(getComponentName());
		return (IMachineRoot) getRodinProject().getRodinFile(name).getRoot();
	}

	public final IPRRoot getPRRoot() {
		if (this instanceof IPRRoot) {
			return (IPRRoot) this.getMutableCopy();
		}
		final String name = EventBPlugin.getPRFileName(getComponentName());
		return (IPRRoot) getRodinProject().getRodinFile(name).getRoot();
	}

	public final ISCContextRoot getSCContextRoot() {
		// Do not optimize here due to temporary files.
		final String name = EventBPlugin.getSCContextFileName(getComponentName());
		return (ISCContextRoot) getRodinProject().getRodinFile(name).getRoot();
	}

	public final ISCMachineRoot getSCMachineRoot() {
		// Do not optimize here due to temporary files.
		final String name = EventBPlugin.getSCMachineFileName(getComponentName());
		return (ISCMachineRoot) getRodinProject().getRodinFile(name).getRoot();
	}

	public final IPORoot getPORoot() {
		// Do not optimize here due to temporary files.
		final String name = EventBPlugin.getPOFileName(getComponentName());
		return (IPORoot) getRodinProject().getRodinFile(name).getRoot();
	}

	public final IPSRoot getPSRoot() {
		if (this instanceof IPSRoot) {
			return (IPSRoot) this.getMutableCopy();
		}
		final String name = EventBPlugin.getPSFileName(getComponentName());
		return (IPSRoot) getRodinProject().getRodinFile(name).getRoot();
	}
	
	public boolean hasPOStamp() throws RodinDBException {
		return hasAttribute(EventBAttributes.POSTAMP_ATTRIBUTE);
	}
	
	public long getPOStamp() throws RodinDBException {
		return getAttributeValue(EventBAttributes.POSTAMP_ATTRIBUTE);
	}
	
	public void setPOStamp(long stamp, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.POSTAMP_ATTRIBUTE, stamp, monitor);
	}

	public boolean isAccurate() throws RodinDBException {
		return getAttributeValue(EventBAttributes.ACCURACY_ATTRIBUTE);
	}
	
	public void setAccuracy(boolean accurate, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.ACCURACY_ATTRIBUTE, accurate, monitor);
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
	
	@Deprecated
	protected final <T extends IRodinElement> T getSingletonChild(
			IElementType<T> elementType, String message) throws RodinDBException {

		return EventBUtil.getSingletonChild(this, elementType, message);
	}

}
