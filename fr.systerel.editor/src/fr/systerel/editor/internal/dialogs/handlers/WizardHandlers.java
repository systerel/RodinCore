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
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IInvariant;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.preferences.PreferenceUtils;

import fr.systerel.editor.internal.actions.operations.extension.AxiomMaker;
import fr.systerel.editor.internal.actions.operations.extension.CarrierSetMaker;
import fr.systerel.editor.internal.actions.operations.extension.ConstantMaker;
import fr.systerel.editor.internal.actions.operations.extension.EnumeratedSetMaker;
import fr.systerel.editor.internal.actions.operations.extension.EventMaker;
import fr.systerel.editor.internal.actions.operations.extension.InvariantMaker;
import fr.systerel.editor.internal.actions.operations.extension.VariableMaker;
import fr.systerel.editor.internal.actions.operations.extension.VariantMaker;
import fr.systerel.editor.internal.dialogs.NewCarrierSetDialog;
import fr.systerel.editor.internal.dialogs.NewConstantDialog;
import fr.systerel.editor.internal.dialogs.NewDerivedPredicateDialog;
import fr.systerel.editor.internal.dialogs.NewEnumeratedSetDialog;
import fr.systerel.editor.internal.dialogs.NewEventDialog;
import fr.systerel.editor.internal.dialogs.NewVariableDialog;
import fr.systerel.editor.internal.dialogs.NewVariantDialog;
import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * Handlers to create and open element creation wizards.
 * 
 * @author "Thomas Muller"
 */
public class WizardHandlers {

	public static class NewVariableWizardHandler extends
			RodinEditorWizards<NewVariableDialog> {

		/**
		 * Handlers for machine related wizards.
		 */

		@Override
		public void createDialog(ExecutionEvent event, RodinEditor editor) {
			final IEventBRoot inputRoot = (IEventBRoot) editor.getInputRoot();
			final VariableMaker variableMaker = new VariableMaker(editor,
					inputRoot);

			final String prefix = PreferenceUtils.getAutoNamePrefix(inputRoot,
					IInvariant.ELEMENT_TYPE);

			this.dialog = new NewVariableDialog(variableMaker, "New Variable",
					prefix);
		}
	}

	public static class NewVariantWizardHandler extends
			RodinEditorWizards<NewVariantDialog> {

		@Override
		public void createDialog(ExecutionEvent event, RodinEditor editor) {
			final IEventBRoot inputRoot = (IEventBRoot) editor.getInputRoot();
			final VariantMaker variantMaker = new VariantMaker(editor,
					inputRoot);
			this.dialog = new NewVariantDialog(variantMaker, "New variant",
					"Expression");
		}

	}

	public static class NewEventWizardHandler extends
			RodinEditorWizards<NewEventDialog> {

		@Override
		public void createDialog(ExecutionEvent event, RodinEditor editor) {

			final IEventBRoot inputRoot = (IEventBRoot) editor.getInputRoot();
			final EventMaker eventMaker = new EventMaker(editor, inputRoot);
			this.dialog = new NewEventDialog(eventMaker, "New Event");
		}

	}

	public static class NewInvariantWizardHandler extends
			RodinEditorWizards<NewDerivedPredicateDialog<IInvariant>> {

		@Override
		public void createDialog(ExecutionEvent event, RodinEditor editor) {

			final IEventBRoot inputRoot = (IEventBRoot) editor.getInputRoot();
			final InvariantMaker invMaker = new InvariantMaker(editor,
					inputRoot);
			this.dialog = new NewDerivedPredicateDialog<IInvariant>(invMaker,
					"New Invariants", IInvariant.ELEMENT_TYPE);
		}

	}

	/**
	 * Handlers for context related handlers.
	 */

	public static class NewCarrierSetWizardHandler extends
			RodinEditorWizards<NewCarrierSetDialog> {

		@Override
		public void createDialog(ExecutionEvent event, RodinEditor editor) {
			final IEventBRoot inputRoot = (IEventBRoot) editor.getInputRoot();
			final CarrierSetMaker maker = new CarrierSetMaker(editor, inputRoot);
			final String identifier = UIUtils.getFreeElementIdentifier(
					inputRoot, ICarrierSet.ELEMENT_TYPE);
			this.dialog = new NewCarrierSetDialog(maker, "New Carrier Sets",
					"Identifier", identifier);

		}

	}

	public static class NewConstantWizardHandler extends
			RodinEditorWizards<NewConstantDialog> {

		@Override
		public void createDialog(ExecutionEvent event, RodinEditor editor) {
			final IEventBRoot root = (IEventBRoot) editor.getInputRoot();
			final ConstantMaker maker = new ConstantMaker(editor, root);
			this.dialog = new NewConstantDialog(maker, "New Constant");
		}

	}

	public static class NewEnumeratedSetWizardHandler extends
			RodinEditorWizards<NewEnumeratedSetDialog> {

		@Override
		public void createDialog(ExecutionEvent event, RodinEditor editor) {
			final IEventBRoot root = (IEventBRoot) editor.getInputRoot();
			final String defaultName = UIUtils.getFreeElementIdentifier(root,
					ICarrierSet.ELEMENT_TYPE);
			final EnumeratedSetMaker maker = new EnumeratedSetMaker(editor,
					root);
			this.dialog = new NewEnumeratedSetDialog(maker,
					"New enumerated set", defaultName);
		}

	}

	public static class NewAxiomWizardHandler extends
			RodinEditorWizards<NewDerivedPredicateDialog<IAxiom>> {

		@Override
		public void createDialog(ExecutionEvent event, RodinEditor editor) {
			final IEventBRoot root = (IEventBRoot) editor.getInputRoot();
			final AxiomMaker maker = new AxiomMaker(editor, root);
			this.dialog = new NewDerivedPredicateDialog<IAxiom>(maker,
					"New axioms", IAxiom.ELEMENT_TYPE);
		}

	}
	
	
}
