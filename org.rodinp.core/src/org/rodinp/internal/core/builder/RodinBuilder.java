/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;

public class RodinBuilder extends IncrementalProjectBuilder {

	public static boolean DEBUG = false;

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) { // throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public static Object readState(IProject project, DataInputStream in) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void writeState(Object savedState, DataOutputStream out) {
		// TODO Auto-generated method stub
	}

	public static void buildStarting() {
		// TODO Auto-generated method stub
	}

	public static void buildFinished() {
		// TODO Auto-generated method stub
	}

}
