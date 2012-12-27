/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <p>
 * Version numbers can be associated with root element types by means of the
 * extension point <code>org.rodinp.core.fileElementVersions</code>. By means
 * of the version numbers declared in corresponding extensions the Rodin
 * platform supports automatic conversions from older to newer versions of file
 * elements. Conversions are supplied by means to the extension point
 * <code>org.rodinp.core.conversions</code>.
 * </p>
 * <p>
 * Version management is almost completely hidden inside the Rodin core plug-in.
 * Conversions, i.e. transformations between versions are declared exclusively in
 * extensions. There is no need to implement any line of code in Java.
 * </p>
 * <p>
 * From inside the Rodin platform conversions can be carried out per project
 * using method
 * 
 * <pre>
 * IConversionResult RodinCore.convert(IRodinProject project, boolean force, IProgressMonitor monitor)
 * </pre>
 * 
 * The parameter <code>force</code> is used to express whether synchrony with
 * the file system is wanted or not. See
 * <code>org.eclipse.core.resources.IFile</code>.
 * </p>
 * <p>
 * Method <code>RodinCore.convert()</code> does not modify files. It converts
 * all files in memory and produces an <code>IConversionResult</code>. The
 * conversion result can be queried to find out about the status of the
 * conversion of an entire project. Only if the conversion result is accepted
 * 
 * <pre>
 * void accept(boolean force, boolean keepHistory, IProgressMonitor monitor)
 * </pre>
 * 
 * the files of the project a manipulated, i.e., the updated files are written
 * to disk.
 * </p>
 * <p>
 * The actual conversions to be carried out are specified per file using
 * extensions points as described above. <b>Supported conversions</b>:
 * <ul>
 * <li>Simple conversion</li>
 * <li>Sorted conversion</li>
 * <li>Source conversion</li>
 * </ul>
 * A sorted conversion is a simple conversion that sorts the elements of a file
 * by element types before carrying out the conversions on (internal) elements
 * and attributes. Three operations are available: renaming an element, renaming
 * an attribute, adding an attribute.
 * </p>
 * <p>
 * <b>The following restrictions apply to conversions:</b>
 * <nl>
 * <ul>
 * <li>A plug-in can only manipulate elements contributed by it.</li>
 * <li>File element nodes cannot be renamed.</li>
 * <li>If a plug-in contributes a conversion for some root element type, then
 * the plug-in must declare a version for that root element type.</li>
 * <li>For every version and root element type there can be at most one
 * contributed conversion.</li>
 * <li>In a simple/sorted conversion the operations are declared relative to file element
 * paths.
 * <ul>
 * <li>These paths must be unique in each conversion.</li>
 * <li>They must be absolute, beginning with the root element type.</li>
 * <li>They must refer to elements, not to attributes.</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * <p>
 * A source conversion is based on a fragment on an XSL transform sheet. The fragment 
 * is complemented with some XSL templates to handle versions properly and a template
 * for copying nodes not matched by any template in the fragment.
 * TODO add more information on source conversions (and XSL transforms maybe)
 * </p>
 * </p>
 * <p>
 * <b>The following restrictions apply to file element versions:</b>
 * <nl>
 * <ul>
 * <li>If any plug-in declares a version for a root element type, then so must
 * the plug-in that contributes the root element type.</li>
 * <li>All contributed file element versions for the same root element type
 * must coincide.</li>
 * </ul>
 * </p>
 * <p>
 * These restrictions are verified by the Rodin core plug-in at runtime and
 * parts of or all of the database may refuse to work if some restrictions are
 * violated.
 * </p>
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 */
public interface IConversionResult {

	/**
	 * A conversion converts each file of a project as necessary.
	 * An entry describes the status of each file.
	 *
	 */
	interface IEntry {
		
		/**
		 * Returns the file described by this entry.
		 * 
		 * @return the file described by this entry
		 */
		IRodinFile getFile();
		/**
		 * Returns whether the file has been successfully converted.
		 * 
		 * @return whether the file has been successfully converted
		 */
		boolean success();
		/**
		 * Returns the version number of the file before it was converted.
		 * 
		 * @return the version number of the file before it was converted
		 */
		long getSourceVersion();
		/**
		 * Returns the version number of the file after it has been converted.
		 * 
		 * @return the version number of the file after it has been converted
		 */
		long getTargetVersion();
		/**
		 * Returns a human readable message describing the status of this file.
		 * 
		 * @return a human readable message describing the status of this file
		 */
		String getMessage();
	}
	
	/**
	 * Returns the project of this conversion.
	 * 
	 * @return the project of this conversion
	 */
	IRodinProject getProject();
	
	/**
	 * Returns the entries of this conversion describing the status of each file.
	 * 
	 * @return the entries of this conversion describing the status of each file
	 */
	IEntry[] getEntries();
		
	/**
	 * Accept the conversion of the project. This method changes the corresponding files of the project.
	 * However, only files with a successful conversion are modified.
	 * 
	 * @param force whether or not files are expected to be in synchrony
	 * @param keepHistory whether the old files are to be saved in the history
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws RodinDBException if there was a problem carrying out the conversion
	 */
	void accept(boolean force, boolean keepHistory, IProgressMonitor monitor) throws RodinDBException;
}
