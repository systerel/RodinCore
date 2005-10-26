/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.JavaProject.java which is
 * 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.Openable;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinElement;
import org.rodinp.core.RodinFile;
import org.rodinp.internal.core.util.MementoTokenizer;

/**
 * Handle for a Rodin Project.
 *
 * <p>A Rodin Project internally maintains a devpath that corresponds
 * to the project's classpath. The classpath may include source folders
 * from the current project; jars in the current project, other projects,
 * and the local file system; and binary folders (output location) of other
 * projects. The Rodin Model presents source elements corresponding to output
 * .class files in other projects, and thus uses the devpath rather than
 * the classpath (which is really a compilation path). The devpath mimics
 * the classpath, except has source folder entries in place of output
 * locations in external projects.
 *
 * <p>Each RodinProject has a NameLookup facility that locates elements
 * on by name, based on the devpath.
 *
 * @see IRodinProject
 */
public class RodinProject 
	extends Openable
	implements IRodinProject, IProjectNature {
	
	/**
	 * Whether the underlying file system is case sensitive.
	 */
	protected static final boolean IS_CASE_SENSITIVE = !new File("Temp").equals(new File("temp")); //$NON-NLS-1$ //$NON-NLS-2$

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
	 * Adds a builder to the build spec for the given project.
	 */
	protected void addToBuildSpec(String builderID) throws CoreException {

		IProjectDescription description = this.project.getDescription();
		int rodinCommandIndex = getRodinCommandIndex(description.getBuildSpec());

		if (rodinCommandIndex == -1) {

			// Add a Rodin command to the build spec
			ICommand command = description.newCommand();
			command.setBuilderName(builderID);
			setRodinCommand(description, command);
		}
	}

	/**
	 * @see Openable
	 */
	@Override
	protected boolean buildStructure(OpenableElementInfo info,
			IProgressMonitor pm,
			Map<IRodinElement, RodinElementInfo> newElements,
			IResource underlyingResource) throws RodinDBException {
	
		// check whether the java project can be opened
		if (!underlyingResource.isAccessible()) {
			throw newNotPresentException();
		}
		
		// compute the children
		((RodinProjectElementInfo) info).computeChildren(this);
		
		return true;
	}

	@Override
	public void closing(RodinElementInfo info) {
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

	/**
	 * Compute the file name to use for a given shared property
	 * @param qName QualifiedName
	 * @return String
	 */
	public String computeSharedPropertyFileName(QualifiedName qName) {

		return '.' + qName.getLocalName();
	}
	
	/**
	 * Configure the project with Rodin nature.
	 */
	public void configure() throws CoreException {
		// register Rodin builder
		addToBuildSpec(RodinCore.BUILDER_ID);
	}
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
	/**
	 * Removes the Rodin nature from the project.
	 */
	public void deconfigure() throws CoreException {
		// deregister Rodin builder
		removeFromBuildSpec(RodinCore.BUILDER_ID);
	}

	/**
	 * Returns true if this handle represents the same Rodin project
	 * as the given handle. Two handles represent the same
	 * project if they are identical or if they represent a project with 
	 * the same underlying resource and occurrence counts.
	 *
	 * @see RodinElement#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
	
		if (this == o)
			return true;
	
		if (!(o instanceof RodinProject))
			return false;
	
		RodinProject other = (RodinProject) o;
		return this.project.equals(other.getProject());
	}

	@Override
	public boolean exists() {
		return hasRodinNature(this.project);
	}	

	/**
	 * @see IRodinProject
	 */
	public IRodinElement findElement(IPath path) throws RodinDBException {
		if (path == null || path.isAbsolute()) {
			throw new RodinDBException(
				new RodinDBStatus(IRodinDBStatusConstants.INVALID_PATH, path));
		}
		
		// No folders in projects yet.
		if (path.segmentCount() != 1) {
			return null;
		}
		try {
			String fileName = path.lastSegment();
			for (IRodinElement child: getChildren()) {
				if (child.getElementName().equals(fileName)) {
					return child;
				}
			}
			return null;
		} catch (RodinDBException e) {
			if (e.getStatus().getCode()
				== IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST) {
				return null;
			} else {
				throw e;
			}
		}
	}

//	/**
//	 * Returns the project custom preference pool.
//	 * Project preferences may include custom encoding.
//	 * @return IEclipsePreferences
//	 */
//	public IEclipsePreferences getEclipsePreferences(){
//		if (!RodinProject.hasRodinNature(this.project)) return null;
//		// Get cached preferences if exist
//		RodinDBManager.PerProjectInfo perProjectInfo = RodinDBManager.getRodinDBManager().getPerProjectInfo(this.project, true);
//		if (perProjectInfo.preferences != null) return perProjectInfo.preferences;
//		// Init project preferences
//		IScopeContext context = new ProjectScope(getProject());
//		final IEclipsePreferences eclipsePreferences = context.getNode(RodinCore.PLUGIN_ID);
//		updatePreferences(eclipsePreferences);
//		perProjectInfo.preferences = eclipsePreferences;
//
//		// Listen to node removal from parent in order to reset cache (see bug 68993)
//		IEclipsePreferences.INodeChangeListener nodeListener = new IEclipsePreferences.INodeChangeListener() {
//			public void added(IEclipsePreferences.NodeChangeEvent event) {
//				// do nothing
//			}
//			public void removed(IEclipsePreferences.NodeChangeEvent event) {
//				if (event.getChild() == eclipsePreferences) {
//					RodinDBManager.getRodinDBManager().resetProjectPreferences(RodinProject.this);
//				}
//			}
//		};
//		((IEclipsePreferences) eclipsePreferences.parent()).addNodeChangeListener(nodeListener);
//
//		// Listen to preference changes
//		IEclipsePreferences.IPreferenceChangeListener preferenceListener = new IEclipsePreferences.IPreferenceChangeListener() {
//			public void preferenceChange(IEclipsePreferences.PreferenceChangeEvent event) {
//				RodinDBManager.getRodinDBManager().resetProjectOptions(RodinProject.this);
//			}
//		};
//		eclipsePreferences.addPreferenceChangeListener(preferenceListener);
//		return eclipsePreferences;
//	}

	@Override
	public String getElementName() {
		return this.project.getName();
	}
	
	/**
	 * @see IRodinElement
	 */
	public String getElementType() {
		return RODIN_PROJECT;
	}

	/*
	 * @see RodinElement
	 */
	@Override
	public IRodinElement getHandleFromMemento(String token, MementoTokenizer memento) {
		switch (token.charAt(0)) {
			case REM_EXTERNAL:
				if (!memento.hasMoreTokens())
					return this;
				String fileName = memento.nextToken();
				RodinElement file = getRodinFile(fileName);
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
	 * Find the specific Rodin command amongst the given build spec
	 * and return its index or -1 if not found.
	 */
	private int getRodinCommandIndex(ICommand[] buildSpec) {
		for (int i = 0; i < buildSpec.length; ++i) {
			if (buildSpec[i].getBuilderName().equals(RodinCore.BUILDER_ID)) {
				return i;
			}
		}
		return -1;
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

	/**
	 * @see IRodinElement
	 */
	public IPath getPath() {
		return this.project.getFullPath();
	}
	
	public RodinDBManager.PerProjectInfo getPerProjectInfo() throws RodinDBException {
		return RodinDBManager.getRodinDBManager().getPerProjectInfoCheckExistence(this.project);
	}

//	private IPath getPluginWorkingLocation() {
//		return this.project.getWorkingLocation(RodinCore.PLUGIN_ID);
//	}	

	/**
	 * @see IRodinProject#getProject()
	 */
	public IProject getProject() {
		return this.project;
	}

	/**
	 * @see IRodinElement
	 */
	public IResource getResource() {
		return this.project;
	}

	/**
	 * @see IRodinElement
	 */
	@Override
	public IResource getUnderlyingResource() throws RodinDBException {
		if (!exists()) throw newNotPresentException();
		return this.project;
	}

	/**
	 * @see IRodinProject
	 */
	public boolean hasBuildState() {

		return RodinDBManager.getRodinDBManager().getLastBuiltState(this.project, null) != null;
	}

	@Override
	public int hashCode() {
		return this.project.hashCode();
	}
	
	/**
	 * Removes the given builder from the build spec for the given project.
	 */
	protected void removeFromBuildSpec(String builderID) throws CoreException {

		IProjectDescription description = this.project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
				description.setBuildSpec(newCommands);
				this.project.setDescription(description, null);
				return;
			}
		}
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
	 * Update the Rodin command in the build spec (replace existing one if present,
	 * add one first if none).
	 */
	private void setRodinCommand(
		IProjectDescription description,
		ICommand newCommand)
		throws CoreException {

		ICommand[] oldBuildSpec = description.getBuildSpec();
		int oldRodinCommandIndex = getRodinCommandIndex(oldBuildSpec);
		ICommand[] newCommands;

		if (oldRodinCommandIndex == -1) {
			// Add a Rodin build spec before other builders (1FWJK7I)
			newCommands = new ICommand[oldBuildSpec.length + 1];
			System.arraycopy(oldBuildSpec, 0, newCommands, 1, oldBuildSpec.length);
			newCommands[0] = newCommand;
		} else {
		    oldBuildSpec[oldRodinCommandIndex] = newCommand;
			newCommands = oldBuildSpec;
		}

		// Commit the spec change into the project
		description.setBuildSpec(newCommands);
		this.project.setDescription(description, null);
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
	public RodinFile getRodinFile(String fileName) {
		return ElementTypeManager.getElementTypeManager().createRodinFileHandle(this, fileName);
	}

	public RodinFile createRodinFile(String name, boolean force, IProgressMonitor monitor) throws RodinDBException {
		CreateRodinFileOperation op = new CreateRodinFileOperation(this, name, force);
		op.runOperation(monitor);
		return getRodinFile(name);
	}

	public IRodinElement[] getRodinFiles() throws RodinDBException {
		return getChildren();
	}
	
	/**
	 * Reset the collection of package fragment roots (local ones) - only if opened.
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
}
