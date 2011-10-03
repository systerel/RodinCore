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

import fr.systerel.editor.actions.IEventBDialog;
import fr.systerel.editor.actions.OperationUtils;
import fr.systerel.editor.internal.actions.operations.OperationFactory;
import fr.systerel.editor.internal.actions.operations.RodinEditorHistory;
import fr.systerel.editor.internal.dialogs.NewCarrierSetDialog;

/**
 * @author "Thomas Muller"
 * 
 */
public class CarrierSetMaker extends AbstractRodinEditorWizardElementMaker {

	public CarrierSetMaker(IEditorPart editor, IEventBRoot root) {
		super(editor, root);
	}

	@Override
	public void addValues(IEventBDialog dialog) {
		final Collection<String> attributes = ((NewCarrierSetDialog) dialog)
				.getNames();
		final String[] names = attributes
				.toArray(new String[attributes.size()]);
		OperationUtils.executeAtomic(RodinEditorHistory.getInstance(),
				OperationFactory.getRodinFileUndoContext(getRoot()),
				"Create Carrier Set",
				EventBOperationFactory.createCarrierSet(getRoot(), names));
	}

}
