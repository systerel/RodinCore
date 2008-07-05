/*******************************************************************************
 * Copyright (c) 2008 Heinrich Heine Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.ui.projectexplorer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Default implementation for specific Content Providers for the project
 * Explorer The project explorer only delegatates the getChildren method, thus
 * one should only override this method
 * 
 * All other methods remain empty
 * 
 * @author Jens Bendisposto
 * 
 */
public abstract class AbstractRodinContentProvider implements
		ITreeContentProvider {

	public final Object getParent(final Object element) {
		Assert.isTrue(false, "This implementation must not be used.");
		return null;
	}

	public final boolean hasChildren(final Object element) {
		Assert.isTrue(false, "This implementation must not be used.");
		return false;
	}

	public final Object[] getElements(final Object inputElement) {
		Assert.isTrue(false, "This implementation must not be used.");
		return null;
	}

	public void dispose() {
	}

	public final void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
		Assert.isTrue(false, "This implementation must not be used.");
	}

	public abstract Object[] getChildren(Object parentElement);

}
