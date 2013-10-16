/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation.updaters;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.rodinp.core.emf.api.itf.ILFile;

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * An adapter to update the presentation when the implicit children are
 * recalculated after the user "saves" a model. It iterates on all opened
 * RodinEditors and asks them to recalculate their presentation.
 * 
 * @author "Thomas Muller"
 */
public class ImplicitPresentationUpdater extends AdapterImpl {

	private final RodinEditor editor;

	public ImplicitPresentationUpdater(RodinEditor editor) {
		this.editor = editor;
	}

	@Override
	public void notifyChanged(Notification notification) {
		final ILFile root = editor.getResource();
		if (this.target != null && this.target.equals(root)) {
			editor.resync(null, true);
		}
	}
}
