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

import java.util.Collection;

import org.eclipse.ui.IEditorPart;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.eventbeditor.Triplet;

import fr.systerel.editor.actions.IEventBDialog;
import fr.systerel.editor.internal.actions.operations.OperationUtils;
import fr.systerel.editor.internal.dialogs.NewVariableDialog;

public class VariableMaker extends AbstractRodinEditorWizardElementMaker {

	public VariableMaker(IEditorPart editor, IEventBRoot root) {
		super(editor, root);
	}

	@Override
	public void addValues(IEventBDialog dialog) {
		assert dialog instanceof NewVariableDialog;
		final NewVariableDialog ndp = (NewVariableDialog) dialog;
		final String varName = ndp.getName();
		final Collection<Triplet<String, String, Boolean>> invariant = ndp
				.getInvariants();
		final String actName = ndp.getInitActionName();
		final String actSub = ndp.getInitActionSubstitution();

		OperationUtils.executeAtomic(getHistory(), getUndoContext(),
				"Create Variable", EventBOperationFactory.createVariable(
						(IMachineRoot) getRoot(), varName, invariant, actName,
						actSub));
	}

}
