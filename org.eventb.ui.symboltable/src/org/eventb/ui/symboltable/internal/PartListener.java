/*******************************************************************************
 * Copyright (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Heinrich Heine Universitaet Duesseldorf - initial API and implementation
 *******************************************************************************/

package org.eventb.ui.symboltable.internal;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

public class PartListener implements IPartListener2 {
	private final SymbolViewPart symbolViewPart;

	public PartListener(final SymbolViewPart symbolViewPart) {
		this.symbolViewPart = symbolViewPart;
	}

	public void partActivated(final IWorkbenchPartReference partRef) {
		activate(partRef);
	}

	public void partBroughtToTop(final IWorkbenchPartReference partRef) {
		// enable(partRef);
	}

	public void partClosed(final IWorkbenchPartReference partRef) {
		// disable(partRef);
	}

	public void partDeactivated(final IWorkbenchPartReference partRef) {
		deactivate(partRef);
	}

	public void partHidden(final IWorkbenchPartReference partRef) {
		// disable(partRef);
	}

	private void activate(final IWorkbenchPartReference partRef) {
		// editor becoming active
		if (partRef instanceof IEditorReference) {
			symbolViewPart.setEditor(getEditor(partRef));
		}

		// clear error message
		if (partRef.getPart(false) == symbolViewPart) {
			symbolViewPart.clearError();
		}

	}

	private void deactivate(final IWorkbenchPartReference partRef) {
		if (partRef instanceof IEditorReference) {
			symbolViewPart.setEditor(null);
		}

		if (partRef.getPart(false) == symbolViewPart) {
			symbolViewPart.clearError();
		}
	}

	private IEditorPart getEditor(final IWorkbenchPartReference partRef) {
		final IWorkbenchPart part = partRef.getPart(false);

		if (part instanceof IEditorPart) {
			return (IEditorPart) part;
		}

		return null;
	}

	public void partOpened(final IWorkbenchPartReference partRef) {
		// IGNORE
	}

	public void partInputChanged(final IWorkbenchPartReference partRef) {
		// IGNORE
	}

	public void partVisible(final IWorkbenchPartReference partRef) {
		// IGNORE
	}
}