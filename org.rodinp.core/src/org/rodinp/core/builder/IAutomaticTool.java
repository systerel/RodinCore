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
 * A tool must react to cancelation requests from a progress monitor. As specified for
 * progress monitors, on a cancelation request the tool must throw an <code>OperationCanceledException</code>.
 * The builder may or may not accept a completed operation in either case, but it should be assumed
 * that the entire work of the tool will be discarded.
 * A producer supplies to operations:
 * <ul>
 * <li> <code>run()</code>: the tool produces some target file "file" </li>
 * <li> <code>clean()</code>: the tool deletes some target file "file" </li>
 * </ul>
 * 
 * @see org.rodinp.core.builder.IExtractor
 * @since 1.0
 */
public interface IAutomaticTool {
	
	/**
	 * Runs the tool that updates a file (or a set of files).
	 * The tool must delete all markers from the input file(s) from which
	 * <code>file</code> is (directly) derived.
	 * @param source The source node from which the target node was created by an extractor,
	 * 				or <code>null</code> if this is unknown
	 * @param target The file to be updated
	 * @param monitor The progress monitor
	 * @return True if file has changed, false otherwise
	 * @throws CoreException If some internal problem occurred
	 */
	public boolean run(IFile source, IFile target, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * A tool responsible for creating a file is also responsible for cleaning it.
	 * Cleaning occurs on two occasions:
	 * <ul>
	 * <li> when a project is explicitly selected for cleaning (e.g. in the Eclipse project menu)</li>
	 * <li> when a file was deleted all files that can be reached by tool dependencies are cleaned</li>
	 * </ul>
	 * @param source The source node from which the target node was created by an extractor,
	 * 				or <code>null</code> if this is unknown
	 * @param target to be cleaned
	 * @param monitor The progress monitor
	 * @throws CoreException If some internal problem occurred
	 */
	public void clean(IFile source, IFile target, IProgressMonitor monitor) throws CoreException;

}
