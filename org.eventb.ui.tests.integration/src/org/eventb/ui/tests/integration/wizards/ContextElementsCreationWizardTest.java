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
package org.eventb.ui.tests.integration.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eventb.internal.ui.eventbeditor.wizards.EventBCreationWizards;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.junit.Test;

/**
 * Tests to check the integration of element creation wizards associated to the
 * Context EventB editor.
 * 
 * @author Thomas Muller
 */
public class ContextElementsCreationWizardTest extends AbstractCreationWizardTest {

	/**
	 * This is an interactive test. This test runs the "NewAxiomsWizard" on an
	 * empty context. It ensures that when a correct input has been given, and
	 * the OK button is pressed, then the created element is a new element for
	 * the EventB editor, and the operation has been added to the undo-redo
	 * history.
	 * 
	 * @throws CoreException
	 */
	@Test
	public void testNewAxiomWizard() throws CoreException {
		final Display display = workbench.getDisplay();
		final boolean skip = prepareTestAndOpenContextEditor(
				"New Axiom Wizard Test",
				"Enter a valid axiom or more and press OK.\n"
						+ "Press Cancel to skip the test.", display);
		if (skip)
			return;
		final IEventBEditor<?> activeEditor = (IEventBEditor<?>) getAndCheckEditor(ctx);
		checkWizard(display, activeEditor,
				new EventBCreationWizards.NewAxiomsWizard());
	}

	/**
	 * This is an interactive test. This test runs the "NewConstantsWizard" on
	 * an empty context. It ensures that when a correct input has been given,
	 * and the OK button is pressed, then the created element is a new element
	 * for the EventB editor, and the operation has been added to the undo-redo
	 * history.
	 * 
	 * @throws CoreException
	 */
	@Test
	public void testNewConstantsWizard() throws CoreException {
		final Display display = workbench.getDisplay();
		final boolean skip = prepareTestAndOpenContextEditor(
				"New Constant Wizard Test",
				"Enter a valid constant or add more and finally press OK.\n"
						+ "Press Cancel to skip the test.", display);
		if (skip)
			return;
		final IEventBEditor<?> activeEditor = (IEventBEditor<?>) getAndCheckEditor(ctx);
		checkWizard(display, activeEditor,
				new EventBCreationWizards.NewConstantsWizard());
	}

	/**
	 * This is an interactive test. This test runs the "NewEnumeratedSetWizard"
	 * on an empty context. It ensures that when a correct input has been given,
	 * and the OK button is pressed, then the created element is a new element
	 * for the EventB editor, and the operation has been added to the undo-redo
	 * history.
	 * 
	 * @throws CoreException
	 */
	@Test
	public void testNewEnumeratedSetWizard() throws CoreException {
		final Display display = workbench.getDisplay();
		final boolean skip = prepareTestAndOpenContextEditor(
				"New Enumerated Set Wizard Test",
				"Enter a valid enumerated set and press OK.\n"
						+ "Press Cancel to skip the test.", display);
		if (skip)
			return;
		final IEventBEditor<?> activeEditor = (IEventBEditor<?>) getAndCheckEditor(ctx);
		checkWizard(display, activeEditor,
				new EventBCreationWizards.NewEnumeratedSetWizard());
	}

	/**
	 * This is an interactive test. This test runs the "NewCarrierSetsWizard" on
	 * an empty context. It ensures that when a correct input has been given,
	 * and the OK button is pressed, then the created element is a new element
	 * for the EventB editor, and the operation has been added to the undo-redo
	 * history.
	 * 
	 * @throws CoreException
	 */
	@Test
	public void testNewCarrierSetsWizard() throws CoreException {
		final Display display = workbench.getDisplay();
		final boolean skip = prepareTestAndOpenContextEditor(
				"New Carrier Set Wizard Test",
				"Enter a valid carrier set or more and press OK.\n"
						+ "Press Cancel to skip the test.", display);
		if (skip)
			return;
		final IEventBEditor<?> activeEditor = (IEventBEditor<?>) getAndCheckEditor(ctx);
		checkWizard(display, activeEditor,
				new EventBCreationWizards.NewCarrierSetsWizard());
	}

	

}
