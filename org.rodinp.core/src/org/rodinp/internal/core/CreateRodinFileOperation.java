/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.CreateCompilationUnitOperation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.util.Messages;
import org.rodinp.internal.core.util.Util;
import org.rodinp.internal.core.version.VersionManager;

/**
 * <p>
 * This operation creates a Rodin file. If the file doesn't exist yet, a new
 * empty file will be created. Otherwise the operation will empty the contents
 * of an existing file.
 * 
 * <p>
 * Required Attributes:
 * <ul>
 * <li>The project in which to create the file.
 * <li>The rodinFile of the file.
 * </ul>
 */
public class CreateRodinFileOperation extends RodinDBOperation {

	/**
	 * When executed, this operation will create a Rodin file with the
	 * given rodinFile. The rodinFile should be a valid rodinFile for a Rodin file.
	 */
	public CreateRodinFileOperation(RodinFile rodinFile, boolean force) {
		super(new IRodinElement[] { rodinFile }, force);
	}

	/**
	 * Creates a compilation unit.
	 * 
	 * @exception RodinDBException
	 *                if unable to create the compilation unit.
	 */
	@Override
	protected void executeOperation() throws RodinDBException {
		try {
			beginTask(Messages.operation_createFileProgress, 2);
			RodinElementDelta delta = newRodinElementDelta();
			RodinFile rodinFile = (RodinFile) getElementToProcess();
			worked(1);
			IFile file = rodinFile.getResource();
			if (file.exists()) {
				// update the contents of the existing file if force is true
				if (force) {
					rodinFile.revert();
					InputStream stream = getInitialInputStream(rodinFile);
					createFile(file, stream, force);
					resultElements = new IRodinElement[] { rodinFile };
					delta.changed(rodinFile, IRodinElementDelta.F_CONTENT);
					addDelta(delta);
				} else {
					throw new RodinDBException(new RodinDBStatus(
							IRodinDBStatusConstants.NAME_COLLISION,
							Messages.bind(Messages.status_nameCollision,
									file.getFullPath().toString()))
					);
				}
			} else {
				InputStream stream = getInitialInputStream(rodinFile);
				createFile(file, stream, force);
				resultElements = new IRodinElement[] { rodinFile };
				if (rodinFile.getParent().exists()) {
					delta.added(rodinFile);
					addDelta(delta);
				}
			}
			worked(1);
		} finally {
			done();
		}
	}

	private ByteArrayInputStream getInitialInputStream(RodinFile rodinFile) {
		IElementType<?> elementType = rodinFile.getRoot().getElementType();
		long version = VersionManager.getInstance().getVersion(elementType);
		StringBuilder buffer = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		);
		buffer.append("<");
		buffer.append(elementType.getId());
		if (version > 0) {
			buffer.append(" version=\"");
			buffer.append(version);
			buffer.append("\"");
		}
		buffer.append("/>");
		try {
			return new ByteArrayInputStream(buffer.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Util.log(e, "Reverting to default encoding");
			return new ByteArrayInputStream(buffer.toString().getBytes());
		}
	}

	@Override
	protected ISchedulingRule getSchedulingRule() {
		IResource resource = getElementToProcess().getResource();
		IWorkspace workspace = resource.getWorkspace();
		if (resource.exists()) {
			return workspace.getRuleFactory().modifyRule(resource);
		} else {
			return workspace.getRuleFactory().createRule(resource);
		}
	}


	@Override
	public boolean modifiesResources() {
		return true;
	}

	/**
	 * Possible failures:
	 * <ul>
	 * <li>NO_ELEMENTS_TO_PROCESS - the file supplied to the
	 * operation is <code>null</code>.
	 * <li>ELEMENT_DOES_NOT_EXIST - the parent project doesn't exist 
	 * <li>NAME_COLLISION - the file provided to the operation
	 * exists and force is <code>false</code>
	 * </ul>
	 */
	@Override
	public IRodinDBStatus verify() {
		super.verify();
		RodinFile rf = (RodinFile) getElementToProcess();
		RodinProject rp = (RodinProject) rf.getParent();
		if (! rp.exists()) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST, rp);
		}
		if (rf.exists() && ! force) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.NAME_COLLISION,
					Messages.bind(Messages.status_nameCollision,
							rf.getPath().toString()));
		}
		return RodinDBStatus.VERIFIED_OK;
	}
}
