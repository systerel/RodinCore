/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.core.tests.builder;

import java.util.ArrayList;

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
	
	public static String getTrace() {
		StringBuilder builder = new StringBuilder();
		for (String call : traces) {
			builder.append(call);
			builder.append('\n');
		}
		// Remove trailing '\n'
		final int length = builder.length();
		if (length != 0)
			builder.deleteCharAt(length - 1);
		return builder.toString();
	}
	
	public static void addTrace(String tool, String method, IFile file) {
		traces.add(tool + " " + method + " " + file.getFullPath());
	}

}
