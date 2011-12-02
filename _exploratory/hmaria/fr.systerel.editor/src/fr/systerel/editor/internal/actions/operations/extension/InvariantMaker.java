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
import org.eventb.core.IInvariant;

import fr.systerel.editor.actions.IEventBDialog;
import fr.systerel.editor.internal.actions.operations.OperationUtils;
import fr.systerel.editor.internal.dialogs.NewDerivedPredicateDialog;

public class InvariantMaker extends AbstractRodinEditorWizardElementMaker {

	public InvariantMaker(IEditorPart editor, IEventBRoot root) {
		super(editor, root);
	}

	@Override
	public void addValues(IEventBDialog dialog) {
		assert dialog instanceof NewDerivedPredicateDialog<?>;
		final NewDerivedPredicateDialog<IInvariant> ndp = (NewDerivedPredicateDialog<IInvariant>) dialog;
		final String[] names = ndp.getNewNames();
		final String[] contents = ndp.getNewContents();
		final boolean[] isTheorem = ndp.getIsTheorem();
		OperationUtils.executeAtomic(getHistory(), getUndoContext(),
				"Create Invariant", EventBOperationFactory.createInvariant(
						getRoot(), names, contents, isTheorem));
	}

}
