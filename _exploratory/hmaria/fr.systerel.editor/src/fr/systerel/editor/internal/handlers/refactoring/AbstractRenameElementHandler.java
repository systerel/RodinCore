/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers.refactoring;

import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IAttributeDesc;
import org.eventb.internal.ui.preferences.PreferenceUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

import fr.systerel.editor.internal.actions.operations.RodinEditorHistory;
import fr.systerel.editor.internal.actions.operations.OperationFactory;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.handlers.AbstractEditionHandler;

@SuppressWarnings("restriction")
public abstract class AbstractRenameElementHandler extends
		AbstractEditionHandler {

	protected IInternalElementType<?> type;

	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		final IInternalElement root = editor.getInputRoot();

		final String prefix = PreferenceUtils.getAutoNamePrefix(root, type);
		final IAttributeDesc desc = ElementDescRegistry.getInstance()
				.getElementDesc(type).getAutoNameAttribute();

		RodinEditorHistory.getInstance().addOperation(
				OperationFactory.renameElements(root, type,
						desc.getManipulation(), prefix));
		editor.resync(null, false);
		return null;
	}

}
