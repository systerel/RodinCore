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
package org.eventb.ui.tests.integration.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eventb.internal.ui.eventbeditor.wizards.EventBCreationWizards;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.junit.Test;

/**
 * Tests to check the integration of element creation wizards associated to the
 * Machine EventB editor.
 * 
 * @author Thomas Muller
 */
public class MachineElementsCreationWizardTest extends
		AbstractCreationWizardTest {

	/**
	 * This is an interactive test. This test runs the "NewVariantWizard" on an
	 * empty machine. It ensures that when a correct input has been given, and
	 * the OK button is pressed, then the created element is a new element for
	 * the EventB editor, and the operation has been added to the undo-redo
	 * history.
	 * 
	 * @throws CoreException
	 */
	@Test
	public void testNewVariantWizard() throws CoreException {
		final Display display = workbench.getDisplay();
		final boolean skip = prepareTestAndOpenMachineEditor(
				"New Variant Wizard Test",
				"Enter a valid variant and press OK.\n"
						+ "Press Cancel to skip the test.", display);
		if (skip)
			return;
		final IEventBEditor<?> activeEditor = (IEventBEditor<?>) getAndCheckEditor(mch);
		checkWizard(display, activeEditor,
				new EventBCreationWizards.NewVariantWizard());
	}
	
	/**
	 * This is an interactive test. This test runs the "NewInvariantsWizard" on
	 * an empty machine. It ensures that when a correct input has been given,
	 * and the OK button is pressed, then the created element is a new element
	 * for the EventB editor, and the operation has been added to the undo-redo
	 * history.
	 * 
	 * @throws CoreException
	 */
	@Test
	public void testNewInvariantsWizard() throws CoreException {
		final Display display = workbench.getDisplay();
		final boolean skip = prepareTestAndOpenMachineEditor(
				"New Invariants Wizard Test",
				"Enter a valid invariant and press OK.\n"
						+ "Press Cancel to skip the test.", display);
		if (skip)
			return;
		final IEventBEditor<?> activeEditor = (IEventBEditor<?>) getAndCheckEditor(mch);
		checkWizard(display, activeEditor,
				new EventBCreationWizards.NewInvariantsWizard());
	}
	
	/**
	 * This is an interactive test. This test runs the "NewVariablesWizard" on
	 * an empty machine. It ensures that when a correct input has been given,
	 * and the OK button is pressed, then the created element is a new element
	 * for the EventB editor, and the operation has been added to the undo-redo
	 * history.
	 * 
	 * @throws CoreException
	 */
	@Test
	public void testNewVariablesWizard() throws CoreException {
		final Display display = workbench.getDisplay();
		final boolean skip = prepareTestAndOpenMachineEditor(
				"New Variables Wizard Test",
				"Enter a valid variable and press OK.\n"
						+ "Press Cancel to skip the test.", display);
		if (skip)
			return;
		final IEventBEditor<?> activeEditor = (IEventBEditor<?>) getAndCheckEditor(mch);
		checkWizard(display, activeEditor,
				new EventBCreationWizards.NewVariablesWizard());
	}
	
}
