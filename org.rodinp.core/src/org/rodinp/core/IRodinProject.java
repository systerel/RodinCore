/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation as
 *     		org.eclipse.jdt.core.IJavaProject
 *     ETH Zurich - adaptation from JDT to Rodin
 *     Systerel - removed deprecated methods
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.internal.core.ElementTypeManager;

/**
 * A Rodin project represents a view of a project resource in terms of Rodin
 * elements. A project may contain several Rodin files.
 * <p>
 * Rodin project elements need to be opened before they can be navigated or
 * manipulated. The children of a Rodin project are the Rodin files that are
 * contained in this project.
 * </p>
 * <p>
 * Note that a resource project which is closed in the workspace (using for
 * instance {@link IProject#close(IProgressMonitor)}) doesn't correspond
 * anymore to a Rodin project, even if it has the Rodin nature. It is therefore
 * considered as a non-Rodin resource project and not part of the database.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients. An instance of
 * one of these handles can be created via
 * <code>RodinCore.create(project)</code>.
 * </p>
 * 
 * @see RodinCore#valueOf(IProject)
 * @since 1.0
 */
public interface IRodinProject extends IParent, IRodinElement, IOpenable {

	/**
	 * The element type of all Rodin projects.
	 */
	IElementType<IRodinProject> ELEMENT_TYPE = ElementTypeManager.getInstance()
			.getProjectElementType();

	/**
	 * Returns an array of non-Rodin resources directly contained in this
	 * project. It does not transitively answer non-Rodin resources contained in
	 * folders; these would have to be explicitly iterated over.
	 * <p>
	 * Non-Rodin resources includes other files and folders located in the
	 * project.
	 * </p>
	 * 
	 * @return an array of non-Rodin resources (<code>IFile</code>s and/or
	 *         <code>IFolder</code>s) directly contained in this project
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 */
	IResource[] getNonRodinResources() throws RodinDBException;

//	/**
//	 * Helper method for returning one option value only. Equivalent to
//	 * <code>(String)this.getOptions(inheritRodinCoreOptions).get(optionName)</code>
//	 * Note that it may answer <code>null</code> if this option does not
//	 * exist, or if there is no custom value for it.
//	 * <p>
//	 * For a complete description of the configurable options, see
//	 * <code>RodinCore#getDefaultOptions</code>.
//	 * </p>
//	 * 
//	 * @param optionName
//	 *            the name of an option
//	 * @param inheritRodinCoreOptions -
//	 *            boolean indicating whether RodinCore options should be
//	 *            inherited as well
//	 * @return the String value of a given option
//	 * @see RodinCore#getDefaultOptions()
//	 */
//	String getOption(String optionName, boolean inheritRodinCoreOptions);
//
//	/**
//	 * Returns the table of the current custom options for this project.
//	 * Projects remember their custom options, in other words, only the options
//	 * different from the the RodinCore global options for the workspace. A
//	 * boolean argument allows to directly merge the project options with global
//	 * ones from <code>RodinCore</code>.
//	 * <p>
//	 * For a complete description of the configurable options, see
//	 * <code>RodinCore#getDefaultOptions</code>.
//	 * </p>
//	 * 
//	 * @param inheritRodinCoreOptions -
//	 *            boolean indicating whether RodinCore options should be
//	 *            inherited as well
//	 * @return table of current settings of all options (key type:
//	 *         <code>String</code>; value type: <code>String</code>)
//	 * @see RodinCore#getDefaultOptions()
//	 */
//	Map getOptions(boolean inheritRodinCoreOptions);

	/**
	 * Returns the <code>IProject</code> on which this
	 * <code>IRodinProject</code> was created. This is a handle-only method.
	 * 
	 * @return the <code>IProject</code> on which this
	 *         <code>IRodinProject</code> was created
	 */
	IProject getProject();

	/**
	 * Returns a handle to the primary copy of the Rodin file with the specified
	 * name in this project (for example, <code>"toto.mdl"</code>). The name
	 * must be a valid Rodin file name, or <code>null</code> will be returned.
	 * <p>
	 * This is a handle-only method. The Rodin file may or may not be present.
	 * </p>
	 * 
	 * @param fileName
	 *            the name of the Rodin file
	 * @return the Rodin file with the specified name in this project or
	 *         <code>null</code> if the given file name doesn't correspond to
	 *         a Rodin file name (for instance, wrong extension).
	 */
	IRodinFile getRodinFile(String fileName);
	
	/**
	 * Returns whether this project has been built at least once and thus
	 * whether it has a build state.
	 * 
	 * @return <code>true</code> if this project has been built at least once,
	 *         <code>false</code> otherwise
	 */
	boolean hasBuildState();

//	/**
//	 * Helper method for setting one option value only. Equivalent to
//	 * <code>Map options = this.getOptions(false); map.put(optionName, optionValue); this.setOptions(map)</code>
//	 * <p>
//	 * For a complete description of the configurable options, see
//	 * <code>RodinCore#getDefaultOptions</code>.
//	 * </p>
//	 * 
//	 * @param optionName
//	 *            the name of an option
//	 * @param optionValue
//	 *            the value of the option to set
//	 * @see RodinCore#getDefaultOptions()
//	 */
//	void setOption(String optionName, String optionValue);
//
//	/**
//	 * Sets the project custom options. All and only the options explicitly
//	 * included in the given table are remembered; all previous option settings
//	 * are forgotten, including ones not explicitly mentioned.
//	 * <p>
//	 * For a complete description of the configurable options, see
//	 * <code>RodinCore#getDefaultOptions</code>.
//	 * </p>
//	 * 
//	 * @param newOptions
//	 *            the new options (key type: <code>String</code>; value type:
//	 *            <code>String</code>), or <code>null</code> to flush all
//	 *            custom options (clients will automatically get the global
//	 *            RodinCore options).
//	 * @see RodinCore#getDefaultOptions()
//	 */
//	void setOptions(Map newOptions);

	
	/**
	 * Returns all the Rodin files in this project.
	 *
	 * @exception RodinDBException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return all of the Rodin files in this project
	 */
	IRodinFile[] getRodinFiles() throws RodinDBException;
	
	/**
	 * Returns the root elements of the Rodin files of this project that are of
	 * the given element type.
	 * 
	 * @param type
	 *            type of the root elements to retrieve
	 * @return the root elements of this project that are of the given type
	 * @exception RodinDBException
	 *                if this project does not exist, or if an exception occurs
	 *                while accessing its children files
	 */
	<T extends IInternalElement> T[] getRootElementsOfType(
			IInternalElementType<T> type) throws RodinDBException;

}
