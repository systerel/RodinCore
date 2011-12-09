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
import org.eventb.ui.eventbeditor.IRodinHistory;
import org.eventb.ui.manipulation.ElementCreationWizardFacade;
import org.eventb.ui.manipulation.ElementManipulationFacade;

import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.handlers.AbstractEditorHandler;

/**
 * Handlers to create and open element creation wizards.
 * 
 * @author "Thomas Muller"
 */
public class WizardHandlers {

	public static final IRodinHistory history = ElementManipulationFacade
			.getHistory();

	public static class NewVariableWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			ElementCreationWizardFacade.openNewVariablesWizard(
					editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}

	public static class NewVariantWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			ElementCreationWizardFacade.openNewVariantWizard(
					editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}

	public static class NewEventWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			ElementCreationWizardFacade.openNewEventWizard(
					editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}

	public static class NewInvariantWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			ElementCreationWizardFacade.openNewInvariantWizard(
					editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}

	public static class NewCarrierSetWizardHandler extends
			AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			ElementCreationWizardFacade.openNewCarrierSetWizard(
					editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}

	public static class NewConstantWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			ElementCreationWizardFacade.openNewConstantsWizard(
					editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}

	public static class NewEnumeratedSetWizardHandler extends
			AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			ElementCreationWizardFacade.openNewEnumeratedSetWizard(
					editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}

	public static class NewAxiomWizardHandler extends AbstractEditorHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			final RodinEditor editor = getActiveRodinEditor();
			ElementCreationWizardFacade.openNewAxiomWizard(
					editor.getInputRoot(), editor.getSite().getShell());
			return null;
		}

	}

}
