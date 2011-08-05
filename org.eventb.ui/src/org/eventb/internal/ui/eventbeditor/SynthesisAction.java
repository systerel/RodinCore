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
package org.eventb.internal.ui.eventbeditor;

import static org.eventb.internal.ui.eventbeditor.EventBEditorUtils.checkAndShowReadOnly;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.rodinp.core.IInternalElement;

public abstract class SynthesisAction extends Action {

	protected static boolean checkReadOnly(IInternalElement... elements) {
		for (IInternalElement element : elements) {
			if (checkAndShowReadOnly(element)) {
				return true;
			}
		}
		return false;
	}

	protected static boolean checkReadOnly(TreeViewer viewer) {
		final IStructuredSelection ssel = (IStructuredSelection) viewer
				.getSelection();
		final IInternalElement[] elements = new IInternalElement[ssel.size()];
		int i = 0;
		for (Iterator<?> it = ssel.iterator(); it.hasNext(); i++) {
			elements[i] = (IInternalElement) it.next();
		}
		return checkReadOnly(elements);
	}

}
