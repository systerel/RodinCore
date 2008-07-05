/*******************************************************************************
 * Copyright (c) 2008 Heinrich Heine Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.ui.projectexplorer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author Jens Bendisposto / Dennis Winter
 * 
 */
public class ProjectExplorerContributionProxy {

	private final IConfigurationElement configurationElement;
	private ITreeContentProvider instance;

	public ProjectExplorerContributionProxy(
			final IConfigurationElement configurationElement) {
		this.configurationElement = configurationElement;
	}

	public ITreeContentProvider getProvider() {
		if (instance == null) {
			initializeProvider();
		}
		return instance;
	}

	private void initializeProvider() {
		try {
			instance = (ITreeContentProvider) configurationElement
					.createExecutableExtension("class");
		} catch (ClassCastException e) {
			final String errorMsg = "Error instantiating class "
					+ configurationElement.getAttribute("class")
					+ ". Wrong type. Expected ITreeContentProvider";
			EventBUIPlugin.getDefault().getLog().log(createStatus(e, errorMsg));

		} catch (CoreException e) {
			final String errorMsg = "Error instantiating class "
					+ configurationElement.getAttribute("class")
					+ " for project explorer ";
			EventBUIPlugin.getDefault().getLog().log(createStatus(e, errorMsg));

		}
	}

	private Status createStatus(final Exception e, final String errorMsg) {
		return new Status(IStatus.ERROR, configurationElement
				.getDeclaringExtension().getSimpleIdentifier(), errorMsg, e);
	}

	public void dispose() {
		if (instance != null) {
			instance.dispose();
		}
	}

}
