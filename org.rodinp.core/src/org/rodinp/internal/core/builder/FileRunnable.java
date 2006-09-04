/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.builder.IAutomaticTool;

/**
 * @author Stefan Hallerstede
 *
 */
public class FileRunnable implements IWorkspaceRunnable {

	private final IAutomaticTool tool;
	
	private final IFile file;
	
	private boolean changed;
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws CoreException {
		changed = tool.run(file, monitor);
	}

	public FileRunnable(IAutomaticTool tool, IFile file) {
		this.tool = tool;
		this.file = file;
	}
	
	public boolean targetHasChanged() {
		return changed;
	}

}
