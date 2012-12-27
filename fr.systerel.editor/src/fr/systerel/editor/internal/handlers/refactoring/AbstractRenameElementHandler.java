/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers.refactoring;

import org.eventb.core.IEventBRoot;
import org.eventb.ui.manipulation.ElementManipulationFacade;
import org.rodinp.core.IInternalElementType;

import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.handlers.AbstractEditionHandler;

public abstract class AbstractRenameElementHandler extends
		AbstractEditionHandler {

	protected IInternalElementType<?> type;

	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		final IEventBRoot root = editor.getInputRoot();
		ElementManipulationFacade.autoRenameElements(root, type);
		editor.resync(null, false);
		return null;
	}

}
