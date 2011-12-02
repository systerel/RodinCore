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
package fr.systerel.editor.internal.actions.operations.extension;

import org.eclipse.ui.IEditorPart;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IInternalElement;

import fr.systerel.editor.actions.IEventBDialog;
import fr.systerel.editor.internal.actions.operations.OperationTree;
import fr.systerel.editor.internal.actions.operations.OperationUtils;
import fr.systerel.editor.internal.dialogs.NewConstantDialog;

public class ConstantMaker extends AbstractRodinEditorWizardElementMaker {

	public ConstantMaker(IEditorPart editor, IEventBRoot root) {
		super(editor, root);
	}

	@Override
	public void addValues(IEventBDialog dialog) {
		assert (dialog instanceof NewConstantDialog);
		final NewConstantDialog ncsDialog = (NewConstantDialog) dialog;
		OperationUtils.executeAtomic(getHistory(), getUndoContext(),
				"Create Constant", createConstantOperation(getRoot(),
								ncsDialog.getIdentifier(),
								ncsDialog.getAxiomNames(),
								ncsDialog.getAxiomPredicates(),
								ncsDialog.getAxiomIsTheorem()));
	}
	
	public static OperationTree[] createConstantOperation(
			IInternalElement root, String identifier, String[] labels,
			String[] predicates, boolean[] isTheorem) {
		final OperationTree[] cmd = {
				EventBOperationFactory.createConstant(root, identifier),
				EventBOperationFactory.createAxiom(root, labels, predicates,
						isTheorem) };
		return cmd;
	}

}
