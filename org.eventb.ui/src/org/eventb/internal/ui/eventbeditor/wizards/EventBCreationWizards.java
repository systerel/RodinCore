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
package org.eventb.internal.ui.eventbeditor.wizards;

import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.internal.ui.eventbeditor.dialogs.EventBDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewConstantDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewDerivedPredicateDialog;
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

}
