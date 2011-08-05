/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - introduced read only elements through new super class
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.IContextRoot;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;

public class NewEnumeratedSet extends AbstractNewActionDelegate<IContextRoot> {

	@Override
	public void runAction(IAction action) {
		final IContextRoot root = editor.getRodinInput();
		EventBEditorUtils.newEnumeratedSet(editor, root);
	}
	
}
