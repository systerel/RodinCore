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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IContextFile;
import org.eventb.core.IEventBFile;
import org.eventb.core.IEventBProject;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOStampedElement;
import org.eventb.core.IPRFile;
import org.eventb.core.IPSFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCMachineFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;

/**
 * Common implementation for event-B files.
 * 
 * @author Stefan Hallerstede
 * @author Laurent Voisin
 */
@Deprecated
public abstract class EventBFile extends RodinFile implements IEventBFile,
		IPOStampedElement {

	protected EventBFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	public final String getComponentName() {
		return getRoot().getElementName();
	}

	public IEventBProject getEventBProject() {
		IEventBRoot root = (IEventBRoot) getRoot();
		return root.getEventBProject();
	}

	public final IContextFile getContextFile() {
		IEventBRoot root = (IEventBRoot) getRoot();
		return (IContextFile) root.getContextRoot().getRodinFile();
	}

	public final IMachineFile getMachineFile() {
		IEventBRoot root = (IEventBRoot) getRoot();
		return (IMachineFile) root.getMachineRoot().getRodinFile();
	}

	public final IPRFile getPRFile() {
		IEventBRoot root = (IEventBRoot) getRoot();
		return (IPRFile) root.getPRRoot().getRodinFile();
	}

	public final ISCContextFile getSCContextFile() {
		IEventBRoot root = (IEventBRoot) getRoot();
		return (ISCContextFile) root.getSCContextRoot().getRodinFile();
	}

	public final ISCMachineFile getSCMachineFile() {
		IEventBRoot root = (IEventBRoot) getRoot();
		return (ISCMachineFile) root.getSCMachineRoot().getRodinFile();
	}

	public final IPOFile getPOFile() {
		IEventBRoot root = (IEventBRoot) getRoot();
		return (IPOFile) root.getPORoot().getRodinFile();
	}

	public final IPSFile getPSFile() {
		IEventBRoot root = (IEventBRoot) getRoot();
		return (IPSFile) root.getPSRoot().getRodinFile();
	}
	
	public boolean hasPOStamp() throws RodinDBException {
		EventBRoot root = (EventBRoot) getRoot();
		return root.hasPOStamp();
	}
	
	public long getPOStamp() throws RodinDBException {
		EventBRoot root = (EventBRoot) getRoot();
		return root.getPOStamp();
	}
	
	public void setPOStamp(long stamp, IProgressMonitor monitor) throws RodinDBException {
		EventBRoot root = (EventBRoot) getRoot();
		root.setPOStamp(stamp, monitor);
	}

	public boolean isAccurate() throws RodinDBException {
		EventBRoot root = (EventBRoot) getRoot();
		return root.isAccurate();
	}
	
	public void setAccuracy(boolean accurate, IProgressMonitor monitor) throws RodinDBException {
		EventBRoot root = (EventBRoot) getRoot();
		root.setAccuracy(accurate, monitor);
	}
	
	public void setConfiguration(String configuration, IProgressMonitor monitor) throws RodinDBException {
		EventBRoot root = (EventBRoot) getRoot();
		root.setConfiguration(configuration, monitor);
	}
	
	public String getConfiguration() throws RodinDBException {
		EventBRoot root = (EventBRoot) getRoot();
		return root.getConfiguration();
	}
	
	public boolean hasConfiguration() throws RodinDBException {
		EventBRoot root = (EventBRoot) getRoot();
		return root.hasConfiguration();
	}
	
}
