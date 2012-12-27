/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.internal.core.RodinDBManager;

/**
 * State of the incremental builder for a project 
 * 
 * @author Laurent Voisin
 */
public class BuildState {

	// Version used for serialization
	private static int VERSION = 1;
	
	private String projectName;
	Graph graph;
	
	private BuildState() {
		// Empty constructor
	}

	public BuildState(IProject project) {
		this.projectName = project.getName();
		this.graph = new Graph();
	}

	public static BuildState getBuildState(IProject project, IProgressMonitor monitor) {
		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		BuildState state = manager.getLastBuiltState(project, monitor);
		if (state == null) {
			state = new BuildState(project);
			manager.setLastBuiltState(project, state);
		}
		return state;
	}
	
	/**
	 * Reads the build state for the given project from the given stream
	 * 
	 * @param project
	 *            Project for which a build state is asked
	 * @param in
	 *            stream from which to read the serialized state
	 * @return the build state of that project or <code>null</code> in case of
	 *         error
	 * @throws IOException
	 */
	public static BuildState read(IProject project, DataInputStream in) throws IOException {
		if (RodinBuilder.DEBUG)
			System.out.println("About to read builder state for " + project.getName()); //$NON-NLS-1$
		
		// Check version
		if (VERSION != in.readByte()) {
			if (RodinBuilder.DEBUG)
				System.out.println("Found non-compatible state version... answered null for " + project.getName()); //$NON-NLS-1$
			return null;
		}

		// Check project name
		BuildState newState = new BuildState();
		newState.projectName = in.readUTF();
		if (! project.getName().equals(newState.projectName)) {
			if (RodinBuilder.DEBUG)
				System.out.println("Project's name does not match... answered null"); //$NON-NLS-1$
			return null;
		}
		
		// Read dependency graph
		ObjectInputStream ois = new ObjectInputStream(in);
		try {
			newState.graph = (Graph) ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException("Couldn't read builder dependency graph: ClassNotFoundException");
		}
		newState.graph.initCaches();
		
		if (RodinBuilder.DEBUG)
			System.out.println("Successfully read builder state for " + newState.projectName); //$NON-NLS-1$
		return newState;
	}
	
	@Override
	public String toString() {
		return "Builder state for project " + projectName;
	}

	public void write(DataOutputStream out) throws IOException {
		if (RodinBuilder.DEBUG)
			System.out.println("About to write builder state for " + projectName); //$NON-NLS-1$
		
		// Write version
		out.writeByte(VERSION);

		// Write project name
		out.writeUTF(projectName);
		
		// Write dependency graph
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(graph);

		if (RodinBuilder.DEBUG)
			System.out.println("Successfully written builder state for " + projectName); //$NON-NLS-1$
	}
	
}
