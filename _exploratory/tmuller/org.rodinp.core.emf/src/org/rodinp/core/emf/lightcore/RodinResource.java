/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Southampton - Initial API and implementation
 *     Systerel - Adaptation from the persistence class RodinResource
 *******************************************************************************/
package org.rodinp.core.emf.lightcore;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILFile;
import org.rodinp.core.emf.lightcore.sync.SynchroManager;

/**
 * This is the serialisation of Event-B models from EMF into the Rodin database.
 * 
 * @author cfs/ff
 */
public class RodinResource extends ResourceImpl implements ILFile {

	private IRodinFile rodinFile;
	private IRodinProject rodinProject;
	private IFile file;
	private IProject project;

	@Override
	public void setURI(final URI uri) {
		String projectName;
		super.setURI(uri);
		final int segmentCount = uri.segmentCount();
		if ("platform".equals(uri.scheme())) {
			projectName = URI.decode(uri.segment(segmentCount - 2));
			final String fileName = URI.decode(uri.segment(segmentCount - 1));
			final String localfilename = fileName.replace(".ebum", ".bum")
					.replace(".ebuc", ".buc");
			rodinProject = RodinCore.getRodinDB().getRodinProject(projectName);
			project = (IProject) rodinProject.getCorrespondingResource();
			rodinFile = rodinProject.getRodinFile(localfilename);
			file = rodinFile.getResource();
		} else if (null == uri.scheme()) {
			projectName = null;
			final String fileName = URI.decode(uri.segment(segmentCount - 1));
			rodinFile = rodinProject.getRodinFile(fileName);
			file = rodinFile.getResource();
		}

	}

	@Override
	public void load(final Map<?, ?> options) throws IOException {
		try {
			isLoading = true;
			// does file already exist? -> load
			if (exists()) {
				try {
					this.getContents().add(
							SynchroManager.getDefault().getModelForRoot(
									rodinFile.getRoot()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				// success
				setTimeStamp(System.currentTimeMillis());
				setLoaded(true);
			}
			// otherwise throw exception
			else {
				throw new IOException("Resource does not exist");
			}
		} finally {
			isLoading = false;
		}
	}

	public void save() {
		try {
			saveAsRodin(Collections.emptyMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void save(final Map<?, ?> options) throws IOException {
		saveAsRodin(options);
	}

	private void saveAsRodin(final Map<?, ?> options) throws IOException {
		if (!isLoaded || isLoading) // || !isModified )
			return;
		try {

			if (!exists()) {
				// create new RodinFile
				try {
					rodinFile.create(true, null);
					// success
					setTimeStamp(System.currentTimeMillis());

				} catch (final RodinDBException e) {
					throw new IOException("Error while creating rodin file: "
							+ e.getLocalizedMessage());
				}
			}

			try {
				RodinCore.run(new IWorkspaceRunnable() {
					public void run(final IProgressMonitor monitor)
							throws CoreException {
						rodinFile.save(null, true);
					}
				}, null);

			} catch (RodinDBException e) {
				throw new IOException("Error while saving rodin file: "
						+ e.getLocalizedMessage());
			}
			// success
			setTimeStamp(System.currentTimeMillis());
			isModified = false;

		} finally {
		}
	}

	public IRodinFile getRodinFile() {
		return rodinFile;
	}

	public IResource getUnderlyingResource() {
		if (file == null) {
			return project;
		} else
			return file;
	}

	/**
	 * Returns whether this resource exists.
	 * 
	 * @exception IOException
	 *                if the resource is not properly defined.
	 */
	private boolean exists() throws IOException {
		// valid project?
		if (rodinProject == null && project == null) {
			throw new IOException("Invalid project name: "
					+ uri.segment(uri.segmentCount() - 2));
		}
		// valid file for RodinFile?
		if (rodinFile == null && file == null) {
			throw new IOException("Invalid file name: "
					+ uri.segment(uri.segmentCount() - 1));
		}
		// does file exist?
		return rodinFile == null ? file.exists() : rodinFile.exists();
	}

	@Override
	public ILElement getRoot() {
		return (ILElement) getContents().get(0);
	}

	@Override
	public boolean isEmpty() {
		return getContents().isEmpty();
	}

	public void unloadResource() {
		unloadRoot();
		try {
			// Make the associated RodinFile consistent if it is has some
			// unsaved change
			if (rodinFile.hasUnsavedChanges())
				rodinFile.makeConsistent(new NullProgressMonitor());
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	private void unloadRoot() {
		final LightElement root = (LightElement) getRoot();
		final TreeIterator<EObject> iter = root.eAllContents();
		while(iter.hasNext()) {
			final EObject el = iter.next();
			el.eSetDeliver(false);
			el.eAdapters().clear();
		}
		root.eSetDeliver(false);
		root.delete();
	}
}
