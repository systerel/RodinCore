/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.RodinCore;

/**
 * Implementation of the Rodin nature for projects.
 * 
 * @author Laurent Voisin
 */
public class RodinProjectNature implements IProjectNature {
	
	private IProject project;

	/**
	 * Adds the builder with the given id to the build spec of this project, if
	 * not already present.
	 */
	private void addToBuildSpec(String builderID) throws CoreException {
		final IProjectDescription description = this.project.getDescription();
		final ICommand[] buildSpec = description.getBuildSpec();
		final int length = buildSpec.length;
		for (int i = 0; i < length; ++i) {
			if (buildSpec[i].getBuilderName().equals(builderID)) {
				return;
			}
		}
		
		// Not found, add a new builder command to the beginning of the build
		// spec
		final ICommand command = description.newCommand();
		command.setBuilderName(builderID);
		final ICommand[] newBuildSpec = new ICommand[length + 1];
		newBuildSpec[0] = command;
		System.arraycopy(buildSpec, 0, newBuildSpec, 1, length);
		description.setBuildSpec(newBuildSpec);
		this.project.setDescription(description, null);
	}

	/**
	 * Configure the project with Rodin nature.
	 */
	@Override
	public void configure() throws CoreException {
		// register Rodin builder
		addToBuildSpec(RodinCore.BUILDER_ID);
	}

	/**
	 * Removes the Rodin nature from the project.
	 */
	@Override
	public void deconfigure() throws CoreException {
		// deregister Rodin builder
		removeFromBuildSpec(RodinCore.BUILDER_ID);
	}

	@Override
	public IProject getProject() {
		return this.project;
	}

	/**
	 * Removes the builder with the given id from the build spec of this
	 * project.
	 */
	private void removeFromBuildSpec(String builderID) throws CoreException {
		final IProjectDescription description = this.project.getDescription();
		final ICommand[] buildSpec = description.getBuildSpec();
		final int length = buildSpec.length;
		for (int i = 0; i < length; ++i) {
			if (buildSpec[i].getBuilderName().equals(builderID)) {
				final ICommand[] newBuildSpec = new ICommand[length-1];
				System.arraycopy(buildSpec, 0, newBuildSpec, 0, i);
				System.arraycopy(buildSpec, i+1, newBuildSpec, i, length-i-1);
				description.setBuildSpec(newBuildSpec);
				this.project.setDescription(description, null);
				return;
			}
		}
	}
	
	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

}
