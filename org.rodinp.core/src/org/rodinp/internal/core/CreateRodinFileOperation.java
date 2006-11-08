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
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;
import org.rodinp.internal.core.util.Messages;
import org.rodinp.internal.core.util.Util;

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
 * <li>The name of the file.
 * </ul>
 */
public class CreateRodinFileOperation extends RodinDBOperation {

	/**
	 * The name of the Rodin file being created.
	 */
	protected String name;

	/**
	 * When executed, this operation will create a Rodin file with the
	 * given name. The name should be a valid name for a Rodin file.
	 */
	public CreateRodinFileOperation(IRodinProject project,
			String name, boolean force) {
		super(null, new IRodinElement[] { project }, force);
		this.name = name;
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
			RodinFile rodinFile = getRodinFile();
			worked(1);
			IFile file = rodinFile.getResource();
			if (file.exists()) {
				// update the contents of the existing file if force is true
				if (force) {
					InputStream stream = getInitialInputStream(rodinFile);
					createFile(file, stream, force);
					resultElements = new IRodinElement[] { rodinFile };
					// TODO check why the test below?
					if (rodinFile.getParent().exists()) {
						delta.changed(rodinFile, IRodinElementDelta.F_CONTENT);
						addDelta(delta);
					}
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
		IElementType elementType = rodinFile.getElementType();
		StringBuilder buffer = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		);
		buffer.append("<");
		buffer.append(elementType.getId());
		buffer.append("/>");
		try {
			return new ByteArrayInputStream(buffer.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Util.log(e, "Reverting to default encoding");
			return new ByteArrayInputStream(buffer.toString().getBytes());
		}
	}

	private RodinFile getRodinFile() {
		return ((RodinProject) getParentElement()).getRodinFile(name);
	}

	@Override
	protected ISchedulingRule getSchedulingRule() {
		IResource resource = getRodinFile().getResource();
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
	 * <li>NO_ELEMENTS_TO_PROCESS - the project supplied to the
	 * operation is <code>null</code>.
	 * <li>INVALID_NAME - the file name provided to the operation
	 * is <code>null</code> or has an invalid syntax
	 * </ul>
	 */
	@Override
	public IRodinDBStatus verify() {
		super.verify();
		if (getParentElement() == null) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.NO_ELEMENTS_TO_PROCESS);
		}
		final ElementTypeManager typeManager = ElementTypeManager.getInstance();
		if (! typeManager.isValidFileName(name)) {
			return new RodinDBStatus(IRodinDBStatusConstants.INVALID_NAME,
					name);
		}
		return RodinDBStatus.VERIFIED_OK;
	}
}
