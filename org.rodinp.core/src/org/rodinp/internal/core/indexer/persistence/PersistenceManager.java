/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer.persistence;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.indexer.DeltaQueuer;
import org.rodinp.internal.core.indexer.IndexManager;
import org.rodinp.internal.core.indexer.PerProjectPIM;
import org.rodinp.internal.core.indexer.persistence.xml.XMLPersistor;

/**
 * @author Nicolas Beauger
 * 
 */
public class PersistenceManager implements ISaveParticipant {

	public static final IPath INDEX_SAVE_PATH = new Path("index-save");

	private static final Plugin plugin = RodinCore.getPlugin();
	private static PersistenceManager instance;

	private PersistenceManager() {
		// Singleton: private constructor
	}

	public static PersistenceManager getDefault() {
		if (instance == null) {
			instance = new PersistenceManager();
		}
		return instance;
	}

	@Override
	public void doneSaving(ISaveContext context) {
		// nothing to do
	}

	@Override
	public void prepareToSave(ISaveContext context) throws CoreException {
		// nothing to do
	}

	@Override
	public void rollback(ISaveContext context) {
		// nothing to do
	}

	@Override
	public void saving(ISaveContext context) throws CoreException {

		final IPath stateLocation = plugin.getStateLocation();

		switch (context.getKind()) {

		case ISaveContext.FULL_SAVE: {
			// avoid workspace deadlock: stop indexing definitely
			IndexManager.getDefault().stop();
			
			final IPath[] previousFiles = context.getFiles();
			final int saveNumber = context.getSaveNumber();
			final IPath saveFilePath = getFullSaveName(saveNumber);
			final File file = stateLocation.append(saveFilePath).toFile();

			final PersistentIndexManager data =
					IndexManager.getDefault().getPersistentData();

			final IPersistor ps = chooseStrategy();
			final boolean success = ps.save(data, file);

			if (success) {
				context.map(INDEX_SAVE_PATH, saveFilePath);
			} else {
				file.delete();
			}
			deleteFiles(previousFiles);
			context.needSaveNumber();
			context.needDelta();
			break;
		}
		case ISaveContext.PROJECT_SAVE:
			final IProject project = context.getProject();
			if (project != null) {
				final IRodinProject rodinProject = RodinCore.valueOf(project);
				final PersistentPIM pim =
						IndexManager.getDefault().getPersistentPIM(rodinProject);

				if (pim != null) {
					saveProject(rodinProject, pim, stateLocation);
				}
			}
			break;
		}
	}

	private void saveProject(IRodinProject rodinProject,
			PersistentPIM pim, IPath stateLocation) {
		final File file = getProjectSaveFile(stateLocation, rodinProject);

		final IPersistor ps = chooseStrategy();
		final boolean success = ps.saveProject(pim, file);
		if (!success) {
			file.delete();
		}
	}

	private void deleteFiles(IPath[] previousFiles) {
		for (IPath path : previousFiles) {
			path.toFile().delete();
		}
	}

	private File getProjectSaveFile(final IPath stateLocation,
			final IRodinProject rodinProject) {
		final String saveProjectName = getProjectSaveName(rodinProject);
		final Path projectDir = new Path("projects");
		stateLocation.append(projectDir).toFile().mkdir();
		final IPath projectRelPath =
				projectDir.addTrailingSeparator().append(saveProjectName);
		final IPath projectFilePath = stateLocation.append(projectRelPath);
		return projectFilePath.toFile();
	}

	private static String getProjectSaveName(IRodinProject rodinProject) {
		return "save-" + rodinProject.getElementName();
	}

	private static IPath getFullSaveName(int saveNumber) {
		return new Path("save-" + Integer.toString(saveNumber));
	}

	public boolean restore(ISavedState savedState, PersistentIndexManager persistIM,
			final DeltaQueuer queuer) {
		if (savedState == null) {
			// activate for very first time
			return false;
		}
		final File saveFile = getFullSaveFile(savedState);

		if (saveFile == null || !saveFile.exists()) {
			return false;
		}
		final IPersistor ps = chooseStrategy();

		final boolean success = ps.restore(saveFile, persistIM);
		if (!success) {
			return false;
		}
		queuer.restore(persistIM.getDeltas());
		
		return true;
	}

	public boolean restoreProject(IRodinProject project, PerProjectPIM pppim) {
		final IPath stateLocation = plugin.getStateLocation();

		final File file = getProjectSaveFile(stateLocation, project);

		if (file.exists()) {
			final IPersistor ps = chooseStrategy();

			return ps.restoreProject(file, pppim);
		}
		return false;
	}

	private File getFullSaveFile(ISavedState ss) {
		final IPath saveFilePath = ss
		.lookup(INDEX_SAVE_PATH);

		if (saveFilePath == null) {
			return null;
		}
		final File file = plugin.getStateLocation().append(saveFilePath).toFile();
		if (!file.exists()) {
			return null;
		}
		return file;
	}

	private IPersistor chooseStrategy() {
		return new XMLPersistor();
	}

	public void deleteProject(IRodinProject project) {

		final IPath stateLocation = plugin.getStateLocation();
		final File file = getProjectSaveFile(stateLocation, project);
		if (file.exists()) {
			file.delete();
		}
	}

}
