/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.JavaProject
 *     Systerel - removed deprecated methods and occurrence count
 *     Systerel - separation of file and root element
 *     Systerel - now using Token objects
 *******************************************************************************/
package org.rodinp.internal.core;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.internal.core.RodinDBManager.OpenableMap;
import org.rodinp.internal.core.util.MementoTokenizer;
import org.rodinp.internal.core.util.MementoTokenizer.Token;

/**
 * Handle for a Rodin Project.
 *
 * @see IRodinProject
 */
public class RodinProject extends Openable implements IRodinProject {
	
	/**
	 * Whether the underlying file system is case sensitive.
	 */
	protected static final boolean IS_CASE_SENSITIVE =
		! new File("Temp").equals(new File("temp")); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The platform project this <code>IRodinProject</code> is based on
	 */
	protected IProject project;
	
	/**
	 * Returns true if the given project is accessible and it has
	 * a java nature, otherwise false.
	 * @param project IProject
	 * @return boolean
	 */
	public static boolean hasRodinNature(IProject project) { 
		try {
			return project.hasNature(RodinCore.NATURE_ID);
		} catch (CoreException e) {
			// project does not exist or is not open
		}
		return false;
	}

	/**
	 * Constructor needed for <code>IProject.getNature()</code> and <code>IProject.addNature()</code>.
	 *
	 * @see #setProject(IProject)
	 */
	public RodinProject() {
		super(null);
	}

	public RodinProject(IProject project, RodinElement parent) {
		super(parent);
		this.project = project;
	}

	/**
	 * @see Openable
	 */
	@Override
	protected boolean buildStructure(OpenableElementInfo info,
			IProgressMonitor pm, OpenableMap newElements,
			IResource underlyingResource) throws RodinDBException {
	
		// check whether the java project can be opened
		if (! underlyingResource.isAccessible()) {
			throw newNotPresentException();
		}
		
		// compute the children
		((RodinProjectElementInfo) info).computeChildren(this);
		
		return true;
	}

	@Override
	public void closing(OpenableElementInfo info) {
		super.closing(info);
	}
	/**
	 * Computes the collection of Rodin files and set it on the given info.
	 * @param info RodinProjectElementInfo
	 */
	public void computeChildren(RodinProjectElementInfo info) {
		info.setNonRodinResources(null);
		info.computeChildren(this);		
	}

//	/**
//	 * Compute the file name to use for a given shared property
//	 * @param qName QualifiedName
//	 * @return String
//	 */
//	public String computeSharedPropertyFileName(QualifiedName qName) {
//
//		return '.' + qName.getLocalName();
//	}
	
	/*
	 * Returns whether the given resource is accessible directly
	 * through the children or the non-Rodin resources of this project.
	 * Returns true if the resource is directly accessible in the project.
	 * Assumes that the resource is a folder or a file.
	 */
	public boolean contains(IResource resource) {
		// All direct children of this project are accessible
		return resource.getParent() == this.project;
	}

	/**
	 * Returns a new element info for this element.
	 */
	@Override
	protected RodinProjectElementInfo createElementInfo() {
		return new RodinProjectElementInfo();
	}

	/**
	 * Returns true if this handle represents the same Rodin project
	 * as the given handle. Two handles represent the same
	 * project if they are identical or if they represent a project with 
	 * the same underlying resource.
	 *
	 * @see RodinElement#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
	
		if (this == o)
			return true;
	
		if (! (o instanceof RodinProject))
			return false;
	
		RodinProject other = (RodinProject) o;
		return this.project.equals(other.project);
	}

	@Override
	public String getElementName() {
		return this.project.getName();
	}
	
	@Override
	public IElementType<IRodinProject> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public IRodinElement getHandleFromMemento(Token token, MementoTokenizer memento) {
		if (token == Token.EXTERNAL) {
				if (!memento.hasMoreTokens())
					return this;
				String fileName = memento.nextToken().getRepresentation();
				RodinElement file = getRodinFile(fileName);
				if (file == null) {
					return null;
				}
				return file.getHandleFromMemento(memento);
		}
		return null;
	}

	/**
	 * Returns the <code>char</code> that marks the start of this handles
	 * contribution to a memento.
	 */
	@Override
	protected char getHandleMementoDelimiter() {
		return REM_EXTERNAL;
	}

	/**
	 * Convenience method that returns the specific type of info for a Rodin project.
	 */
	protected RodinProjectElementInfo getRodinProjectElementInfo()
		throws RodinDBException {

		return (RodinProjectElementInfo) getElementInfo();
	}

	/**
	 * Returns an array of non-Rodin resources contained in the receiver.
	 */
	@Override
	public IResource[] getNonRodinResources() throws RodinDBException {

		return ((RodinProjectElementInfo) getElementInfo()).getNonRodinResources(this);
	}

//	/**
//	 * @see org.eclipse.jdt.core.IRodinProject#getOption(String, boolean)
//	 */	
//	public String getOption(String optionName, boolean inheritRodinCoreOptions) {
//		
//		String propertyName = optionName;
//		if (RodinDBManager.getRodinDBManager().optionNames.contains(propertyName)){
//			IEclipsePreferences projectPreferences = getEclipsePreferences();
//			String javaCoreDefault = inheritRodinCoreOptions ? RodinCore.getOption(propertyName) : null;
//			if (projectPreferences == null) return javaCoreDefault;
//			String value = projectPreferences.get(propertyName, javaCoreDefault);
//			return value == null ? null : value.trim();
//		}
//		return null;
//	}
//	
//	/**
//	 * @see org.eclipse.jdt.core.IRodinProject#getOptions(boolean)
//	 */
//	public Map getOptions(boolean inheritRodinCoreOptions) {
//
//		// initialize to the defaults from RodinCore options pool
//		Map options = inheritRodinCoreOptions ? RodinCore.getOptions() : new Hashtable(5);
//
//		// Get project specific options
//		RodinDBManager.PerProjectInfo perProjectInfo = null;
//		Hashtable projectOptions = null;
//		HashSet optionNames = RodinDBManager.getRodinDBManager().optionNames;
//		try {
//			perProjectInfo = getPerProjectInfo();
//			projectOptions = perProjectInfo.options;
//			if (projectOptions == null) {
//				// get eclipse preferences
//				IEclipsePreferences projectPreferences= getEclipsePreferences();
//				if (projectPreferences == null) return options; // cannot do better (non-Rodin project)
//				// create project options
//				String[] propertyNames = projectPreferences.keys();
//				projectOptions = new Hashtable(propertyNames.length);
//				for (int i = 0; i < propertyNames.length; i++){
//					String propertyName = propertyNames[i];
//					String value = projectPreferences.get(propertyName, null);
//					if (value != null && optionNames.contains(propertyName)){
//						projectOptions.put(propertyName, value.trim());
//					}
//				}		
//				// cache project options
//				perProjectInfo.options = projectOptions;
//			}
//		} catch (RodinDBException jme) {
//			projectOptions = new Hashtable();
//		} catch (BackingStoreException e) {
//			projectOptions = new Hashtable();
//		}
//
//		// Inherit from RodinCore options if specified
//		if (inheritRodinCoreOptions) {
//			Iterator propertyNames = projectOptions.keySet().iterator();
//			while (propertyNames.hasNext()) {
//				String propertyName = (String) propertyNames.next();
//				String propertyValue = (String) projectOptions.get(propertyName);
//				if (propertyValue != null && optionNames.contains(propertyName)){
//					options.put(propertyName, propertyValue.trim());
//				}
//			}
//			return options;
//		}
//		return projectOptions;
//	}

	@Override
	public IPath getPath() {
		return this.project.getFullPath();
	}
	
	public RodinDBManager.PerProjectInfo getPerProjectInfo() throws RodinDBException {
		return RodinDBManager.getRodinDBManager().getPerProjectInfoCheckExistence(this.project);
	}

//	private IPath getPluginWorkingLocation() {
//		return this.project.getWorkingLocation(RodinCore.PLUGIN_ID);
//	}	

	/*
	 * @see IRodinProject#getProject()
	 */
	@Override
	public IProject getProject() {
		return this.project;
	}

	/*
	 * @see IRodinElement
	 */
	@Override
	public IResource getResource() {
		return this.project;
	}

	/*
	 * @see IRodinElement
	 */
	@Override
	public IResource getUnderlyingResource() {
		return this.project;
	}

	/**
	 * @see IRodinProject
	 */
	@Override
	public boolean hasBuildState() {

		return RodinDBManager.getRodinDBManager().getLastBuiltState(this.project, null) != null;
	}

	@Override
	public int hashCode() {
		return this.project.hashCode();
	}
	
	/*
	 * Resets this project's caches
	 */
	public void resetCaches() {
		// No cache yet in RodinProjectElementInfo
//		RodinProjectElementInfo info = (RodinProjectElementInfo) RodinDBManager.getRodinDBManager().peekAtInfo(this);
//		if (info != null){
//			info.resetCaches();
//		}
	}

	/**
	 * Sets the underlying kernel project of this Rodin project,
	 * and fills in its parent and name.
	 * Called by IProject.getNature().
	 *
	 * @see IProjectNature#setProject(IProject)
	 */
	public void setProject(IProject project) {

		this.project = project;
		this.parent = RodinDBManager.getRodinDBManager().getRodinDB();
	}

	/**
	 * @see IRodinProject
	 */
	@Override
	public RodinFile getRodinFile(String fileName) {
		final IFile file = project.getProject().getFile(fileName);
		final ElementTypeManager manager = ElementTypeManager.getInstance();
		final FileAssociation association = manager.getFileAssociation(file);
		if (association == null) {
			return null;		// Not a Rodin file.
		}
		return new RodinFile(file, this);
	}

	@Override
	public IRodinFile[] getRodinFiles() throws RodinDBException {
		IRodinElement[] children = getChildren();
		IRodinFile[] result = new IRodinFile[children.length];
		System.arraycopy(children, 0, result, 0, children.length);
		return result;
	}
	
	/**
	 * Recomputes the lists of children - only if opened.
	 */
	public void updateChildren() {
		if (this.isOpen()) {
			try {
				RodinProjectElementInfo info = getRodinProjectElementInfo();
				computeChildren(info);
			} catch (RodinDBException e) {
				try {
					close(); // could not do better
				} catch (RodinDBException ex) {
					// ignore
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IInternalElement> T[] getRootElementsOfType(
			IInternalElementType<T> type) throws RodinDBException {
		final IRodinFile[] files = getRodinFiles();
		final ArrayList<T> list = new ArrayList<T>(files.length);
		for (IRodinFile file: files) {
			if (type == file.getRootElementType()) {
				list.add((T) file.getRoot());
			}
		}
		return ((ElementType<T>) type).toArray(list);
	}
}
