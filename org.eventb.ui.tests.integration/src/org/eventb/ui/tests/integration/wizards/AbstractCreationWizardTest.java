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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.IEventBRoot;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.internal.ui.eventbeditor.wizards.AbstractEventBCreationWizard;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.tests.integration.EventBUIIntegrationUtils;

/**
 * Abstract class for tests to check the integration of element creation wizards
 * associated to the EventB editor.
 * 
 * @author Thomas Muller
 */
public abstract class AbstractCreationWizardTest extends AbstractUIIntegrationTest {

	/**
	 * Returns <code>true</code> if the test shall be skipped.
	 */
	protected boolean prepareTestAndOpenContextEditor(String title,
			String message, Display display) {
		return prepareTestAndOpenEditor(eventBContextEditorID, ctx, title, message, display);
	}

	/**
	 * Returns <code>true</code> if the test shall be skipped.
	 */
	protected boolean prepareTestAndOpenMachineEditor(String title,
			String message, Display display) {
		return prepareTestAndOpenEditor(eventBMachineEditorID, mch, title, message, display);
	}
	
	private boolean prepareTestAndOpenEditor(String editorID, IEventBRoot root, String title,
			String message, Display display) {
		final IWorkbenchPage page = ww.getActivePage();
		final boolean confirm = EventBUIIntegrationUtils.askForTestExecution(display, title, message);
		if (confirm)
			EventBUIIntegrationUtils.openEditor(display, page, editorID, root);
		return !confirm;
	}
	
	
	protected IEventBEditor<?> getAndCheckEditor(IEventBRoot expectedRoot) {
		final IEditorPart activeEditor = ww.getActivePage().getActiveEditor();
		assertTrue(activeEditor instanceof IEventBEditor<?>);
		// We check that the active editor is the one we want
		final IEditorInput editorInput = activeEditor.getEditorInput();
		assertTrue((editorInput instanceof FileEditorInput));
		assertEquals(((FileEditorInput) editorInput).getFile(), expectedRoot
				.getRodinFile().getResource());
		return (IEventBEditor<?>) activeEditor;
	}

	protected void checkWizard(Display display, final IEventBEditor<?> editor,
			final AbstractEventBCreationWizard wizard) {

		display.syncExec(new Runnable() {

			@Override
			public void run() {
				// Launch the dialog for interactive test.
				final AtomicOperation op = wizard.openDialog(editor);
				// We check that the element is added in the editor
				if (op != null) {
					assertTrue(
							"The element has not been added to the EventB editor",
							editor.isNewElement(op.getCreatedElement()));
					// We check that the operation has been added to the history
					assertTrue(
							" The element has not been added to the History",
							History.getInstance().isUndo(
									OperationFactory.getContext(mch)));
				}
			}

		});
	}
	
}
