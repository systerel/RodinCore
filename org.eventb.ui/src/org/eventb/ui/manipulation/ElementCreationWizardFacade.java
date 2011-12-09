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
package org.eventb.ui.manipulation;

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

	/**
	 * Opens the variable creation wizard.
	 * 
	 * @param root
	 * 			the root element to host the new variables
	 * @param shell
	 * 			the parent shell to open the dialog
	 */
	public static void openNewVariablesWizard(IEventBRoot root, Shell shell) {
		new EventBCreationWizards.NewVariablesWizard().openDialog(root, shell);

	}

	/**
	 * Opens the variable creation wizard.
	 * 
	 * @param root
	 * 			the root element to host the new variables
	 * @param shell
	 * 			the parent shell to open the dialog
	 */
	public static void openNewVariantWizard(IEventBRoot root, Shell shell) {
		new EventBCreationWizards.NewVariantWizard().openDialog(root, shell);
	}

	/**
	 * Opens the event creation wizard.
	 * 
	 * @param root
	 * 			the root element to host the new events
	 * @param shell
	 * 			the parent shell to open the dialog
	 */
	public static void openNewEventWizard(IEventBRoot root, Shell shell) {
		new EventBCreationWizards.NewEventsWizard().openDialog(root, shell);
	}

	/**
	 * Opens the invariant creation wizard.
	 * 
	 * @param root
	 * 			the root element to host the new invariants
	 * @param shell
	 * 			the parent shell to open the dialog
	 */
	public static void openNewInvariantWizard(IEventBRoot root, Shell shell) {
		new EventBCreationWizards.NewInvariantsWizard().openDialog(root, shell);
	}

	/**
	 * Opens the carrier set creation wizard.
	 * 
	 * @param root
	 * 			the root element to host the new carrier sets
	 * @param shell
	 * 			the parent shell to open the dialog
	 */
	public static void openNewCarrierSetWizard(IEventBRoot root, Shell shell) {
		new EventBCreationWizards.NewCarrierSetsWizard()
				.openDialog(root, shell);
	}

	/**
	 * Opens the variable creation wizard.
	 * 
	 * @param root
	 * 			the root element to host the new variables
	 * @param shell
	 * 			the parent shell to open the dialog
	 */
	public static void openNewConstantsWizard(IEventBRoot root, Shell shell) {
		new EventBCreationWizards.NewConstantsWizard().openDialog(root, shell);
	}

	/**
	 * Opens the enumerated set creation wizard.
	 * 
	 * @param root
	 * 			the root element to host the new enumerated sets
	 * @param shell
	 * 			the parent shell to open the dialog
	 */
	public static void openNewEnumeratedSetWizard(IEventBRoot root, Shell shell) {
		new EventBCreationWizards.NewEnumeratedSetWizard().openDialog(root,
				shell);
	}

	/**
	 * Opens the axiom creation wizard.
	 * 
	 * @param root
	 * 			the root element to host the new axioms
	 * @param shell
	 * 			the parent shell to open the dialog
	 */
	public static void openNewAxiomWizard(IEventBRoot root, Shell shell) {
		new EventBCreationWizards.NewAxiomsWizard().openDialog(root, shell);
	}

}
