/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.wizards;

import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.Path;
import org.eventb.core.IEventBProject;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * Validates an event-B project name. When the project is valid, allows to
 * retrieve the corresponding event-B project.
 * 
 * @author Laurent Voisin
 */
public class EventBProjectValidator {

	public static final String EMPTY_NAME = "Project name must be specified";
	public static final String INVALID_NAME = "Project name must be valid";
	public static final String INEXISTENT = "Project must exist";
	public static final String CLOSED = "Project must be open";
	public static final String NOT_RODIN = "Project must be a Rodin project";
	public static final String READ_ONLY = "Project must be writable";

	private static final IWorkspaceRoot ROOT = getWorkspace().getRoot();

	private String errorMessage;

	private IEventBProject evbProject;

	/**
	 * Returns the error message computed by the last call to
	 * {@link #validate(String)}.
	 * 
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Returns the event-B project named with the name passed to the last call
	 * to {@link #validate(String)}.
	 * 
	 * @return the event-B project or <code>null</code> in case of error
	 */
	public IEventBProject getEventBProject() {
		return evbProject;
	}

	/**
	 * Returns whether the last call to {@link #validate(String)} produced an
	 * error.
	 * 
	 * @return whether an error was detected
	 */
	public boolean hasError() {
		return errorMessage != null;
	}

	private void setErrorMessage(String msg) {
		assert msg != null;
		errorMessage = msg;
		evbProject = null;
	}

	/**
	 * Attempt to validate the given name as an event-B project name that
	 * exists, is open and writable.
	 * 
	 * @param name
	 *            project name to check
	 */
	public void validate(String name) {
		if (name.length() == 0) {
			setErrorMessage(EMPTY_NAME);
			return;
		}
		if (!Path.EMPTY.isValidSegment(name)) {
			setErrorMessage(INVALID_NAME);
			return;
		}

		final IProject project = ROOT.getProject(name);
		if (!project.exists()) {
			setErrorMessage(INEXISTENT);
			return;
		}
		if (!project.isOpen()) {
			setErrorMessage(CLOSED);
			return;
		}

		final IRodinProject rodinProject = RodinCore.valueOf(project);
		if (!rodinProject.exists()) {
			setErrorMessage(NOT_RODIN);
			return;
		}
		if (rodinProject.isReadOnly()) {
			setErrorMessage(READ_ONLY);
			return;
		}
		evbProject = (IEventBProject) rodinProject
				.getAdapter(IEventBProject.class);
		errorMessage = null;
	}

}
