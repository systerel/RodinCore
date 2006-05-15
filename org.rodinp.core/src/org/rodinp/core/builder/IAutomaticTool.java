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
 * An automatic tool is the actual tool to be run, whereas the extractor specifies when it is to be run.
 * A tool must react to cancellation requests from a progress monitor and to interrupt requests.
 * The builder may or may not accept a completed operation in either case, but it should be assumed
 * that the entire work of the tool will be discarded.
 * A producer supplies to operations:
 * <ul>
 * <li> "run": the tool produces some target file "file" </li>
 * <li> "clean": the tool deletes some target file "file" </li>
 * </ul>
 * 
 * @see org.rodinp.core.builder.IExtractor
 */
public interface IAutomaticTool {
	
	/**
	 * run a tool that updates a file (or a set of files)
	 * @param file The file to be updated
	 * @param monitor The progress monitor
	 * @return True if file has changed, false otherwise
	 * @throws CoreException If some internal problem occured
	 */
	public boolean run(IFile file, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * a tool responsible for creating a file is also responsible for cleaning it
	 * @param file to be cleaned
	 * @param monitor The progress monitor
	 * @throws CoreException If some internal problem occured
	 */
	public void clean(IFile file, IProgressMonitor monitor) throws CoreException;

}
