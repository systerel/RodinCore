/*******************************************************************************
 * Copyright (c) 2011, 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.wizards;

import java.util.Collection;

import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.basis.ContextRoot;
import org.eventb.core.basis.MachineRoot;
import org.eventb.internal.ui.eventbeditor.Triplet;
import org.eventb.internal.ui.eventbeditor.dialogs.EventBDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewCarrierSetDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewConstantDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewDerivedPredicateDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewEnumeratedSetDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewEventDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewVariableDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewVariantDialog;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;

/**
 * Utility methods to support the creation of EventB elements using wizards.
 */
public class EventBCreationWizards {

	public static class NewAxiomsWizard extends AbstractEventBCreationWizard {

		@Override
		public EventBDialog createDialog(IEventBRoot root, Shell shell) {
			return new NewDerivedPredicateDialog<IAxiom>(root, shell,
					"New Axioms", IAxiom.ELEMENT_TYPE);
		}

		@Override
		public AtomicOperation getCreationOperation(IEventBRoot root,
				EventBDialog dialog) {
			if (!(dialog instanceof NewDerivedPredicateDialog<?>))
				return null;
			final NewDerivedPredicateDialog<?> axiomDialog = (NewDerivedPredicateDialog<?>) dialog;
			final String[] names = axiomDialog.getNewNames();
			final String[] contents = axiomDialog.getNewContents();
			final boolean[] isTheorem = axiomDialog.getIsTheorem();
			return OperationFactory.createAxiomWizard((IContextRoot) root,
					names, contents, isTheorem);
		}

	}

	public static class NewConstantsWizard extends AbstractEventBCreationWizard {

		@Override
		public EventBDialog createDialog(IEventBRoot root, Shell shell) {
			return new NewConstantDialog(this, (IContextRoot) root, shell,
					"New Constant");
		}

		@Override
		public AtomicOperation getCreationOperation(IEventBRoot root,
				EventBDialog dialog) {
			final NewConstantDialog constantDialog = (NewConstantDialog) dialog;
			final String identifier = constantDialog.getIdentifier();
			final String[] axmNames = constantDialog.getAxiomNames();
			final String[] axmSubs = constantDialog.getAxiomPredicates();
			final boolean[] axmIsThm = constantDialog.getAxiomIsTheorem();
			return OperationFactory.createConstantWizard((IContextRoot) root,
					identifier, axmNames, axmSubs, axmIsThm);
		}

	}

	public static class NewEnumeratedSetWizard extends
			AbstractEventBCreationWizard {

		@Override
		protected EventBDialog createDialog(IEventBRoot root, Shell shell) {
			return new NewEnumeratedSetDialog((IContextRoot) root, shell,
					"New Enumerated Set");
		}

		@Override
		public AtomicOperation getCreationOperation(IEventBRoot root,
				EventBDialog dialog) {
			final NewEnumeratedSetDialog enumDialod = (NewEnumeratedSetDialog) dialog;
			final String name = enumDialod.getName();
			final String[] elements = enumDialod.getElements();
			if (name == null)
				return null;
			return OperationFactory.createEnumeratedSetWizard(
					(IContextRoot) root, name, elements);
		}

	}

	public static class NewCarrierSetsWizard extends
			AbstractEventBCreationWizard {

		@Override
		protected EventBDialog createDialog(IEventBRoot root, Shell shell) {
			return new NewCarrierSetDialog((ContextRoot) root, shell,
					"New Carrier Sets", "Identifier");
		}

		@Override
		public AtomicOperation getCreationOperation(IEventBRoot root,
				EventBDialog dialog) {
			final NewCarrierSetDialog csDialog = (NewCarrierSetDialog) dialog;
			final Collection<String> attributes = csDialog.getNames();
			final String[] names = attributes.toArray(new String[attributes
					.size()]);
			return OperationFactory.createCarrierSetWizard((IContextRoot) root,
					names);
		}

	}

	public static class NewVariantWizard extends AbstractEventBCreationWizard {

		@Override
		protected EventBDialog createDialog(IEventBRoot root, Shell shell) {
			return new NewVariantDialog((IMachineRoot) root, shell, "New Variant");
		}

		@Override
		public AtomicOperation getCreationOperation(IEventBRoot root,
				EventBDialog dialog) {
			final NewVariantDialog variantDialog = (NewVariantDialog) dialog;
			final String[] labels = variantDialog.getNewLabels();
			final String[] expressions = variantDialog.getNewExpressions();
			return OperationFactory.createVariantsWizard((IMachineRoot) root,
					labels, expressions);
		}

	}

	public static class NewInvariantsWizard extends
			AbstractEventBCreationWizard {

		@Override
		protected EventBDialog createDialog(IEventBRoot root, Shell shell) {
			return new NewDerivedPredicateDialog<IInvariant>(root, shell,
					"New Invariants", IInvariant.ELEMENT_TYPE);
		}

		@Override
		public AtomicOperation getCreationOperation(IEventBRoot root,
				EventBDialog dialog) {
			final NewDerivedPredicateDialog<?> iDialog = (NewDerivedPredicateDialog<?>) dialog;
			final String[] names = iDialog.getNewNames();
			final String[] contents = iDialog.getNewContents();
			final boolean[] isTheorem = iDialog.getIsTheorem();
			return OperationFactory.createInvariantWizard((MachineRoot) root,
					names, contents, isTheorem);
		}

	}

	public static class NewVariablesWizard extends AbstractEventBCreationWizard {

		@Override
		protected EventBDialog createDialog(IEventBRoot root, Shell shell) {
			return new NewVariableDialog(this, (IMachineRoot) root, shell,
					"New Variable");
		}

		@Override
		public AtomicOperation getCreationOperation(IEventBRoot root,
				EventBDialog dialog) {
			final NewVariableDialog vDialog = (NewVariableDialog) dialog;
			final String varName = vDialog.getName();
			final Collection<Triplet<String, String, Boolean>> invariant = vDialog
					.getInvariants();
			final String actName = vDialog.getInitActionName();
			final String actSub = vDialog.getInitActionSubstitution();
			return OperationFactory.createVariableWizard((IMachineRoot) root,
					varName, invariant, actName, actSub);
		}

	}

	public static class NewEventsWizard extends AbstractEventBCreationWizard {

		@Override
		protected EventBDialog createDialog(IEventBRoot root, Shell shell) {
			return new NewEventDialog(this, (IMachineRoot) root, shell,
					"New Events");
		}

		@Override
		public AtomicOperation getCreationOperation(IEventBRoot root,
				EventBDialog dialog) {
			final NewEventDialog eDialog = (NewEventDialog) dialog;
			final String name = eDialog.getLabel();
			final String[] paramNames = eDialog.getParameters();
			final String[] grdNames = eDialog.getGrdLabels();
			final String[] grdPredicates = eDialog.getGrdPredicates();
			final boolean[] grdIsTheorem = eDialog.getGrdIsTheorem();
			final String[] actNames = eDialog.getActLabels();
			final String[] actSubstitutions = eDialog.getActSubstitutions();
			return OperationFactory.createEvent((IMachineRoot) root, name,
					paramNames, grdNames, grdPredicates, grdIsTheorem,
					actNames, actSubstitutions);

		}

	}

}
