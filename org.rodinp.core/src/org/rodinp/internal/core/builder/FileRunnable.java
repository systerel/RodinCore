/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added builder performance trace
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import static org.rodinp.internal.core.builder.RodinBuilder.DEBUG_PERF;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Stefan Hallerstede
 * 
 */
public class FileRunnable implements IWorkspaceRunnable {

	private final ToolDescription toolDesc;

	private final IFile source;
	private final IFile target;

	private boolean changed;

	public void run(IProgressMonitor monitor) throws CoreException {
		final long start = DEBUG_PERF ? System.currentTimeMillis() : 0;
		changed = toolDesc.getTool().run(source, target, monitor);
		if (DEBUG_PERF) {
			final long end = System.currentTimeMillis();
			final String run = toolDesc.getId() + "(" + source.getName()
					+ ") -> " + target.getName();
			System.out.println(run + " took " + (end - start) + " ms");
		}
	}

	public FileRunnable(ToolDescription tool, IFile source, IFile target) {
		this.toolDesc = tool;
		this.source = source;
		this.target = target;
	}

	public boolean targetHasChanged() {
		return changed;
	}

}
