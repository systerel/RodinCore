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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.internal.ui.eventbeditor.wizards.EventBCreationWizards;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.tests.integration.EventBUIIntegrationUtils;
import org.junit.Test;

/**
 * Tests to check the integration of element creation wizards associated to the
 * EventB editor.
 * 
 * @author Thomas Muller
 */
public class EventBCreationWizardTest extends AbstractUIIntegrationTest {

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
		final IWorkbenchPage page = ww.getActivePage();
		final Display display = workbench.getDisplay();

		final boolean confirm = askForTestExecution(display,
				"New Axioms Wizard Test",
				"Enter a valid axiom or more and press OK.\n"
						+ "Press Cancel to skip the test.");
		if (!confirm)
			return;

		EventBUIIntegrationUtils.openEditor(display, page,
				eventBContextEditorID, ctx);
		final IEditorPart activeEditor = page.getActiveEditor();
		assertTrue(activeEditor instanceof IEventBEditor<?>);
		final IEventBEditor<?> editor = (IEventBEditor<?>) activeEditor;
		// We check that the active editor is the one we want
		final IEditorInput editorInput = activeEditor.getEditorInput();
		assertTrue((editorInput instanceof FileEditorInput));
		assertEquals(((FileEditorInput) editorInput).getFile(), ctx
				.getRodinFile().getResource());
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				// Launch the dialog for interactive test.
				final EventBCreationWizards.NewAxiomsWizard wizard = new EventBCreationWizards.NewAxiomsWizard();
				final AtomicOperation op = wizard
						.openDialog((IEventBEditor<?>) activeEditor);
				// We check that the element is added in the editor
				if (op != null) {
					assertTrue(
							"The element has not been added to the EventB editor",
							editor.isNewElement(op.getCreatedElement()));
					// We check that the operation has been added to the history
					assertTrue(History.getInstance().isUndo(
							OperationFactory.getContext(ctx)));
				}
			}

		});

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
		final IWorkbenchPage page = ww.getActivePage();
		final Display display = workbench.getDisplay();

		final boolean confirm = askForTestExecution(display,
				"New Constants Wizard Test",
				"Enter a valid constant or more and press OK.\n"
						+ "Press Cancel to skip the test.");
		if (!confirm)
			return;

		EventBUIIntegrationUtils.openEditor(display, page,
				eventBContextEditorID, ctx);

		final IEditorPart activeEditor = page.getActiveEditor();
		assertTrue(activeEditor instanceof IEventBEditor<?>);
		final IEventBEditor<?> editor = (IEventBEditor<?>) activeEditor;

		// We check that the active editor is the one we want
		final IEditorInput editorInput = activeEditor.getEditorInput();
		assertTrue((editorInput instanceof FileEditorInput));
		assertEquals(((FileEditorInput) editorInput).getFile(), ctx
				.getRodinFile().getResource());
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				// Launch the dialog for interactive test.
				final EventBCreationWizards.NewConstantsWizard wizard = new EventBCreationWizards.NewConstantsWizard();
				final AtomicOperation op = wizard
						.openDialog((IEventBEditor<?>) activeEditor);
				// We check that the element is added in the editor
				if (op != null) {
					assertTrue(
							"The element has not been added to the EventB editor",
							editor.isNewElement(op.getCreatedElement()));
					// We check that the operation has been added to the history
					assertTrue(History.getInstance().isUndo(
							OperationFactory.getContext(ctx)));
				}
			}

		});
	}

	private static boolean askForTestExecution(final Display display,
			final String testTitle, final String message) {
		final RunnableWithResult r = new RunnableWithResult() {

			private boolean result;

			@Override
			public void run() {
				result = MessageDialog.openConfirm(display.getActiveShell(),
						testTitle, message);
			}

			@Override
			public Object getResult() {
				return result;
			}

		};
		display.syncExec(r); // open the dialog
		return (Boolean) r.getResult();

	}

	private interface RunnableWithResult extends Runnable {

		Object getResult();

	}

}
