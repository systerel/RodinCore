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
import fr.systerel.editor.internal.dialogs.NewEventDialog;

public class EventMaker extends AbstractRodinEditorWizardElementMaker {

	public EventMaker(IEditorPart editor, IEventBRoot root) {
		super(editor, root);
	}

	@Override
	public void addValues(IEventBDialog dialog) {
		assert dialog instanceof NewEventDialog;
		final NewEventDialog ndp = (NewEventDialog) dialog;
		final String[] grdNames = ndp.getGrdLabels();
		final String[] lGrdPredicates = ndp.getGrdPredicates();
		final boolean[] grdIsTheorem = ndp.getGrdIsTheorem();

		final String[] actNames = ndp.getActLabels();
		final String[] lActSub = ndp.getActSubstitutions();

		final Collection<String> parseResult = ndp.getParseResult();
		final String[] paramNames = parseResult.toArray(new String[parseResult
				.size()]);
		OperationUtils.executeAtomic(getHistory(), getUndoContext(),
				"Create Event", EventBOperationFactory.createEvent(getRoot(),
						ndp.getLabelResult(), paramNames, grdNames,
						lGrdPredicates, grdIsTheorem, actNames, lActSub));
	}

}
