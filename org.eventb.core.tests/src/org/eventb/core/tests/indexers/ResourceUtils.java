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
package org.eventb.core.tests.indexers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;

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
		assert file != null;
		file.create(true, null);
		return file;
	}

	private static void initFile(IRodinFile rodinFile, String contents)
			throws Exception {
		final IFile resource = rodinFile.getResource();
		setContents(resource, contents);
	}

	public static IContextRoot createContext(IRodinProject project,
			String bareName, String contents) throws Exception {
		final String contextName = EventBPlugin.getContextFileName(bareName);
		final IRodinFile rFile = createRodinFile(project, contextName);
		initFile(rFile, contents);
		return (IContextRoot) rFile.getRoot();
	}

	public static IMachineRoot createMachine(IRodinProject project,
			String bareName, String contents) throws Exception {
		final String machineName = EventBPlugin.getMachineFileName(bareName);
		final IRodinFile rFile = createRodinFile(project, machineName);
		initFile(rFile, contents);
		return (IMachineRoot) rFile.getRoot();
	}
	
	public static final String MCH_BARE_NAME = "machine";
	public static final List<IDeclaration> EMPTY_DECL = Collections.emptyList();
	public static final String INTERNAL_ELEMENT1 = "internal_element1";
	public static final String INTERNAL_ELEMENT2 = "internal_element2";
	public static final String CTX_BARE_NAME = "context";

}
