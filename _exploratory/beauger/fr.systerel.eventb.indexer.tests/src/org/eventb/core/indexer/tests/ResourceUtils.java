/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.indexer.tests;

import static junit.framework.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;

/**
 * @author Nicolas Beauger
 * 
 */
public class ResourceUtils {

	private static void setContents(IFile file, String contents)
			throws Exception {
		final InputStream input = new ByteArrayInputStream(contents
				.getBytes("utf-8"));
		file.setContents(input, IResource.NONE, null);
	}

	private static IRodinFile createRodinFile(IRodinProject project,
			String fileName) throws RodinDBException {
		IRodinFile file = project.getRodinFile(fileName);
		file.create(true, null);
		return file;
	}

	private static void initFile(IRodinFile rodinFile, String contents)
			throws Exception {
		final IFile resource = rodinFile.getResource();
		setContents(resource, contents);
	}

	public static IContextFile createContext(IRodinProject project,
			String fileName, String contents) throws Exception {
		final IRodinFile rFile = createRodinFile(project, fileName);
		initFile(rFile, contents);
		return adaptContext(rFile);
	}

	private static IContextFile adaptContext(final IRodinFile rFile) {
		final IContextFile context = (IContextFile) rFile
				.getAdapter(IContextFile.class);
		assertNotNull("could not get adapter to IContextFile", context);

		return context;
	}

	public static IMachineFile createMachine(IRodinProject project,
			String fileName, String contents) throws Exception {
		final IRodinFile rFile = createRodinFile(project, fileName);
		initFile(rFile, contents);
		return adaptMachine(rFile);
	}

	private static IMachineFile adaptMachine(final IRodinFile rFile) {
		final IMachineFile machine = (IMachineFile) rFile
				.getAdapter(IMachineFile.class);
		assertNotNull("could not get adapter to IMachineFile", machine);

		return machine;
	}

	public static final String MCH_FILE_NAME = "file.bum";
	public static final List<IDeclaration> EMPTY_DECL = Collections
	.emptyList();
	public static final String INTERNAL_ELEMENT1 = "internal_element1";
	public static final String CTX_FILE_NAME = "file.buc";

}
