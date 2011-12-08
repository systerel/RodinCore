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
package fr.systerel.editor.internal.dialogs.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eventb.internal.ui.eventbeditor.wizards.EventBCreationWizards;
import org.eventb.ui.ElementOperationDirector;
import org.eventb.ui.eventbeditor.IRodinHistory;

import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.handlers.AbstractEditorHandler;

/**
 * Handlers to create and open element creation wizards.
 * 
 * @author "Thomas Muller"
 */
public class WizardHandlers {

	public static final IRodinHistory history = ElementOperationDirector.getHistory();
	
	
	public static class NewVariableWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			new EventBCreationWizards.NewVariablesWizard().openDialog(editor.getInputRoot(), editor.getEditorSite().getShell());
			return null;
		}
	
	}

	public static class NewVariantWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			new EventBCreationWizards.NewVariantWizard().openDialog(editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}
		
	}

	public static class NewEventWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			new EventBCreationWizards.NewEventsWizard().openDialog(editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}

	public static class NewInvariantWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			new EventBCreationWizards.NewInvariantsWizard().openDialog(editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}

	public static class NewCarrierSetWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			new EventBCreationWizards.NewCarrierSetsWizard().openDialog(editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}

	public static class NewConstantWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			new EventBCreationWizards.NewConstantsWizard().openDialog(editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}

	public static class NewEnumeratedSetWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			new EventBCreationWizards.NewEnumeratedSetWizard().openDialog(editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}


	}

	public static class NewAxiomWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			new EventBCreationWizards.NewAxiomsWizard().openDialog(editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}
	
	
}
