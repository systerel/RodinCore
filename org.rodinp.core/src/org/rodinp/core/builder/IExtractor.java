/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


/**
 * @author Stefan Hallerstede
 *
 * A tool must register an extractor <code>IExtractor</code> and a tool <code>IAutomaticTool</code>.
 * The extractor links the file to be produced ("the target") to its sources, i.e. the files on which it depends.
 * An extractor can work in two ways. A link from a source to the target is called a dependency.
 * <ul>
 * <li>A dependency from the source "file" to the target can be added. 
 * These are usually so-called tool-dependencies (see below). </li>
 * <li> A dependency from another source source to the target can be added. 
 * These are usually user-dependencies (see below). </li>
 * </ul>
 * Dependencies are distinguished by whether they have been in some way be provided by the user
 * or they are required by some installed tool (i.e. once the tool is installed the user cannot
 * control presence or absence of these dependencies. The interface <code>IGraph</code> 
 * enforces this distinction. At runtime it is important to achieve a better error analysis
 * in case there are cycles in the dependency graph. The main aim is to avoid bogus error 
 * messages to the user in situations where the user cannot correct a cyclicity problem.
 * 
 * @see IGraph
 * @see IAutomaticTool
 * @since 1.0
 */
public interface IExtractor {
	
	/**
	 * Extracts dependency information from a file.
	 * @param source the file to be extracted
	 * @param graph the dependency graph to update
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem
	 */
	public void extract(IFile source, IGraph graph, IProgressMonitor monitor) throws CoreException;

}
