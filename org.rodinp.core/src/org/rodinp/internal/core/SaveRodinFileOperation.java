/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.CreateCompilationUnitOperation.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.util.Messages;

/**
 * <p>
 * This operation saves the contents of a Rodin file to its underlying resource.
 * 
 * <p>
 * Required Attributes:
 * <ul>
 * <li>The Rodin file
 * </ul>
 */
public class SaveRodinFileOperation extends RodinDBOperation {

	private final boolean keepHistory;
	
	/**
	 * When executed, this operation will save the given Rodin file.
	 */
	public SaveRodinFileOperation(IRodinFile rodinFile, boolean force) {
		super(rodinFile, force);
		this.keepHistory = false;
	}

	public SaveRodinFileOperation(IRodinFile rodinFile, boolean force,
			boolean keepHistory) {
		super(rodinFile, force);
		this.keepHistory = keepHistory;
	}

	/**
	 * Saves the RodinFile
	 * 
	 * @exception RodinDBException
	 *                if unable to create the compilation unit.
	 */
	@Override
	protected void executeOperation() throws RodinDBException {
		try {
			beginTask(Messages.operation_saveFileProgress, 2);
			RodinFile file = (RodinFile) getElementToProcess();
			RodinFileElementInfo fileInfo = (RodinFileElementInfo)
					file.getElementInfo(getSubProgressMonitor(1));
			fileInfo.saveToFile(file, 
					force,
					keepHistory,
					getSchedulingRule(), 
					getSubProgressMonitor(1)
			);
			// No delta to produce
			setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
		} finally {
			done();
		}
	}

	@Override
	protected ISchedulingRule getSchedulingRule() {
		final IResource resource = getElementToProcess().getResource();
		final IWorkspace workspace = resource.getWorkspace();
		return workspace.getRuleFactory().modifyRule(resource);
	}


	@Override
	public boolean modifiesResources() {
		return true;
	}

	/**
	 * Possible failures:
	 * <ul>
	 * <li>NO_ELEMENTS_TO_PROCESS - the project supplied to the
	 * operation is <code>null</code>.
	 * <li>INVALID_NAME - the file name provided to the operation
	 * is <code>null</code> or has an invalid syntax
	 * </ul>
	 */
	@Override
	public IRodinDBStatus verify() {
		super.verify();
		final IRodinElement element = getElementToProcess();
		if (! element.exists()) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST,
					element);
		}
		if (element.isReadOnly()) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.READ_ONLY,
					element);
		}
		return RodinDBStatus.VERIFIED_OK;
	}
}
