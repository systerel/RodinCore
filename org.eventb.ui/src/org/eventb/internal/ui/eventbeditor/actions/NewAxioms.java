/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - introduced read only elements through new super class
 *     Systerel - now using wizard class NewAxiomsWizard
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.IContextRoot;
import org.eventb.internal.ui.eventbeditor.wizards.EventBCreationWizards;

public class NewAxioms extends AbstractNewActionDelegate<IContextRoot> {

	@Override
	public void runAction(IAction action) {
		new EventBCreationWizards.NewAxiomsWizard().openDialog(editor);
	}

}
