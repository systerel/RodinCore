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
package org.eventb.ui;

import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IEventBRoot;
import org.eventb.internal.ui.eventbeditor.wizards.EventBCreationWizards;

/**
 * Allows to launch the Event-B element creation wizards.
 * 
 * @author Thomas Muller
 * @since 2.4
 */
public class ElementCreationWizardFacade {

	public static void openNewVariablesWizard(IEventBRoot root, Shell shell) {
		new EventBCreationWizards.NewVariablesWizard().openDialog(root, shell);
		
	}
	
}
