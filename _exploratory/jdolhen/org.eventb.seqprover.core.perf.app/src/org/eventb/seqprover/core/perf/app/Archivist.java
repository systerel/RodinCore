/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.seqprover.core.perf.app;

import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProject;

/**
 * Implementation of archival of prover run results (proof and proof status
 * files).
 * 
 * @author Laurent Voisin
 */
public class Archivist {

	public static IFileStore resultsStore;

	/**
	 * Archives the proof files of the given project into the
	 * <code>results</code> directory of this plug-in.
	 * 
	 * @param rodinProject
	 *            name of the Rodin project from which to copy
	 * @param pathElements
	 *            components of the sub-path below <code>results</code>,
	 *            typically an SMT configuration name
	 * @throws Exception
	 */
	public static void archiveResults(IRodinProject rodinProject,
			String... pathElements) throws Exception {
		new Archivist(rodinProject, pathElements).archiveResults();
	}

	private final IRodinProject rodinProject;
	private final String[] pathElements;
	private IFileStore destDir;

	private Archivist(IRodinProject rodinProject, String[] pathElements) {
		this.rodinProject = rodinProject;
		this.pathElements = pathElements;
	}

	private void archiveResults() throws Exception {
		makeDestinationDir();
		archiveRootsOfType(IPRRoot.ELEMENT_TYPE);
		archiveRootsOfType(IPSRoot.ELEMENT_TYPE);
	}

	/**
	 * Creates a destination directory built as
	 * 
	 * <pre>
	 *  {test directory}/results/{project name}/{pathElements[0]}/{pathElements[1]}/...
	 * </pre>
	 */
	private void makeDestinationDir() throws Exception {
		final String projectName = rodinProject.getElementName();
		IPath path = new Path(projectName);
		for (final String pathElement : pathElements) {
			path = path.append(pathElement);
		}
		destDir = resultsStore.getFileStore(path);
		if (Application.DEBUG) {
			System.out.println("results URI = " + destDir.toURI());
		}
		destDir.mkdir(EFS.NONE, null);
	}

	private <T extends IEventBRoot> void archiveRootsOfType(
			IInternalElementType<T> type) throws CoreException {
		final T[] roots = rodinProject.getRootElementsOfType(type);
		for (final IInternalElement root : roots) {
			archiveRoot(root);
		}
	}

	private void archiveRoot(IInternalElement root) throws CoreException {
		final IFile file = root.getUnderlyingResource();
		if (Application.DEBUG) {
			System.out.println("\tCopying " + file);
		}
		final URI uri = file.getLocationURI();
		final IFileStore src = EFS.getStore(uri);
		final IFileStore dst = destDir.getChild(src.getName());
		src.copy(dst, EFS.OVERWRITE, null);
	}

}
