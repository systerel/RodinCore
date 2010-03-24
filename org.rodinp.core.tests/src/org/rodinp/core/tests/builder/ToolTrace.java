/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used list of string in Tool Trace
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * Static class for registering traces of tool execution.
 * 
 * @author Laurent Voisin
 */
public abstract class ToolTrace {

	private static ArrayList<String> traces = new ArrayList<String>();
	
	public static void flush() {
		traces.clear();
	}
	
	public static List<String> getTraces() {
		return new ArrayList<String>(traces);
	}
	
	public static void addTrace(String tool, String method, IFile file) {
		traces.add(tool + " " + method + " " + file.getFullPath());
	}

}
