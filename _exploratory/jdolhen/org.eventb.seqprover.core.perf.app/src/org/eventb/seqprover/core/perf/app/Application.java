/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.seqprover.core.perf.app;

import static org.eclipse.equinox.app.IApplicationContext.APPLICATION_ARGS;
import static org.rodinp.core.RodinCore.getRodinDB;
import static org.rodinp.internal.core.debug.DebugHelpers.disableIndexing;

import java.io.File;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.rodinp.core.IRodinProject;

/**
 * Application for running performance tests of the Atelier B provers and SMT
 * solvers based on full Event-B projects.
 * 
 * @author Yoann Guyot
 * @author Laurent Voisin
 */
public class Application implements IApplication {

	public static boolean DEBUG = true;

	private static final String USAGE = "Usage: rodin"
			+ " -application org.eventb.seqprover.core.perf.app.main"
			+ " <projects-dir>";

	private static class InvalidArgsException extends Exception {

		private static final long serialVersionUID = -8739792968812389976L;

		public InvalidArgsException(String message) {
			super(message);
		}

	}

	private IFileStore projectsStore;
	private IFileStore dirStore;

	@Override
	public Object start(IApplicationContext ctx) throws Exception {
		try {
			parseArgs(ctx);
		} catch (InvalidArgsException e) {
			System.err.println(e.getMessage());
			return EXIT_OK; // Can't set an error exit code
		}
		ctx.applicationRunning();
		prepareWorkspace();
		dirStore = projectsStore.getParent();
		final IRodinProject[] projects = importProjects();
		prepareResults();
		final TestRunner runner = new TestRunner(projects);
		runner.testProjects();
		saveWorkspace();
		return EXIT_OK;
	}

	@Override
	public void stop() {
		// Don't do anything
	}

	private void parseArgs(IApplicationContext ctx) throws InvalidArgsException {
		final Map<?, ?> map = ctx.getArguments();
		final String[] args = (String[]) map.get(APPLICATION_ARGS);
		if (args.length != 1) {
			throw error("Missing directory.");
		}
		final File file = new File(args[0]).getAbsoluteFile();
		projectsStore = EFS.getLocalFileSystem().fromLocalFile(file);
		if (DEBUG) {
			System.out.println("DirStore = " + projectsStore.toURI());
		}
	}

	private static InvalidArgsException error(String message) {
		return new InvalidArgsException(message + "\n" + USAGE);
	}

	private void prepareWorkspace() throws CoreException {
		desactivateAutoBuilder();
		disableIndexing();
		cleanWorkspace();
	}

	private void desactivateAutoBuilder() throws CoreException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}
	}

	private void cleanWorkspace() throws CoreException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IWorkspaceRoot root = workspace.getRoot();
		root.delete(true, true, null);
	}

	private IRodinProject[] importProjects() throws Exception {
		final ProjectImporter importer = new ProjectImporter(projectsStore);
		importer.importProjects();
		return getRodinDB().getRodinProjects();
	}

	private void prepareResults() throws CoreException {
		final IFileStore store = dirStore.getChild("results");
		store.delete(EFS.NONE, null);
		Archivist.resultsStore = store;
	}

	private void saveWorkspace() throws CoreException {
		cleanWorkspace();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.save(true, null);
	}

}
