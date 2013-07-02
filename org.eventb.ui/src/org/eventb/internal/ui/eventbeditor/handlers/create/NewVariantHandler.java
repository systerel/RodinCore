/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - introduced read only elements through new super class
 *     Systerel - redirected dialog opening and externalized strings
 *     Systerel - refactored using NewVariantWizard
 *     Systerel - refactored using AbstractCreationWizardHandler
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.handlers.create;

import static org.eventb.internal.ui.utils.Messages.dialogs_variantAlreadyExists;
import static org.eventb.internal.ui.utils.Messages.title_variantExists;

import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.wizards.EventBCreationWizards;
import org.rodinp.core.RodinDBException;

public class NewVariantHandler extends AbstractCreationWizardHandler {

	@Override
	protected void openCreationWizard() {
		final IMachineRoot root = (IMachineRoot) editor.getRodinInput();
		final int length;
		try {
			length = root.getChildrenOfType(IVariant.ELEMENT_TYPE).length;
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetChildrenException(e);
			return;
		}
		if (length != 0) {
			UIUtils
			.showError(title_variantExists,
					dialogs_variantAlreadyExists);
			return;
		}
		new EventBCreationWizards.NewVariantWizard().openDialog(editor);
	}

}
