/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class MPOTool extends POTool {
	
	private static final String MPO = "MPO";
	// Id of this tool
	private static String SC_ID = PLUGIN_ID + ".testMPO";
	
	public void clean(IFile source, IFile file, IProgressMonitor monitor) throws CoreException {
		clean(file, monitor, MPO);
	}

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		extract(file, graph, MPO, SC_ID, monitor);
	}

	public boolean run(IFile source, IFile file, IProgressMonitor monitor) throws CoreException {
		run(source, file, monitor, MPO);
		return true;
	}

}
