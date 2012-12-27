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
public class CPOTool extends POTool {

	private static final String CPO = "CPO";
	// Id of this tool
	private static String SC_ID = PLUGIN_ID + ".testCPO";
	
	public void clean(IFile source, IFile file, IProgressMonitor monitor) throws CoreException {
		clean(file, monitor, CPO);
	}

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		extract(file, graph, CPO, SC_ID, monitor);
	}

	public boolean run(IFile source, IFile file, IProgressMonitor monitor) throws CoreException {
		run(source, file, monitor, CPO);
		return true;
	}

}
