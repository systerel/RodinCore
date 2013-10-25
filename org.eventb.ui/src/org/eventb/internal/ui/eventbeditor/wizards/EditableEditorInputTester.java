/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.wizards;

import static org.eventb.internal.ui.eventbeditor.wizards.AbstractEventBCreationWizard.isCreationAllowed;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

/**
 * Property tester which allows to check that a given file editor input
 * corresponds to an editable Rodin root.
 *
 * @author Thomas Muller
 */
public class EditableEditorInputTester extends PropertyTester {

	@Override
	public boolean test(Object toTest, String property, Object[] args,
			Object expectedValue) {
		if (!(toTest instanceof FileEditorInput)) {
			return false;
		}
		final IFile file = ((FileEditorInput) toTest).getFile();
		final IRodinFile rodinFile = RodinCore.valueOf(file);
		if (rodinFile == null) {
			return false;
		}
		final IEventBRoot root = (IEventBRoot) rodinFile.getRoot();
		return root != null && !root.isReadOnly() && isCreationAllowed(root);
	}

}
