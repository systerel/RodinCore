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
package org.rodinp.internal.core.index.persistence;

import java.io.File;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.persistence.xml.XMLPersistor;

/**
 * @author Nicolas Beauger
 * 
 */
public class PersistenceManager implements ISaveParticipant {

	/**
	 * 
	 */
	private static final String INDEX_SAVE = "index-save";
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

	public void doneSaving(ISaveContext context) {
		// TODO Auto-generated method stub

	}

	public void prepareToSave(ISaveContext context) throws CoreException {
		// TODO Auto-generated method stub

	}

	public void rollback(ISaveContext context) {
		// TODO Auto-generated method stub

	}

	public void saving(ISaveContext context) throws CoreException {
		final Plugin plugin = RodinIndexer.getDefault();
		final int saveNumber = context.getSaveNumber();
		String saveFileName = "save-" + Integer.toString(saveNumber);
		File file = plugin.getStateLocation().append(saveFileName).toFile();
		
		
		final IPersistor ps = chooseStrategy();
		ps.save(IndexManager.getDefault().getPerProjectPIM(), file);

		context.map(new Path(INDEX_SAVE), new Path(saveFileName));
		context.needSaveNumber();
		context.needDelta(); // optional
	}

	public void restore() {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			final Plugin plugin = RodinIndexer.getDefault();
			final ISavedState ss = workspace.addSaveParticipant(RodinIndexer
					.getDefault(), PersistenceManager.this);
			if (ss == null) {
				// activate for very first time
				// plugin.buildState();
			} else {
				final IPath saveFilePath = ss.lookup(new Path(INDEX_SAVE));

				final File file = plugin.getStateLocation().append(saveFilePath)
						.toFile();
				
				final IPersistor ps = chooseStrategy();
				
				ps.restore(file, IndexManager.getDefault().getPerProjectPIM());
				
				// plugin.readImportantState(f);
				IResourceChangeListener listener = new IResourceChangeListener() {

					public void resourceChanged(IResourceChangeEvent event) {
						IResourceDelta delta = event.getDelta();
						if (delta != null) {
							// TODO send to RDBCL
							// fast reactivation using delta
							// plugin.updateState(delta);
						} else {
							// slower reactivation without benefit
							// of delta
							// plugin.rebuildState();
						}
					}
				};
				ss.processResourceChangeEvents(listener);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private IPersistor chooseStrategy() {
		return new XMLPersistor();
	}
	

}
