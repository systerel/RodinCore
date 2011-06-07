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
package fr.systerel.editor.internal.handlers;

import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.editors.RodinEditor;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.operations.AtomicOperation;
import fr.systerel.editor.operations.History;
import fr.systerel.editor.operations.OperationFactory;

/**
 * @author Thomas Muller & Nicolas Beauger
 */
public class DeleteHandler extends AbstractEditionHandler {

	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		final Interval inter = editor.getDocumentMapper()
				.findFirstElementIntervalAfter(offset);
		if (inter == null)
			return "No element to delete from here.";
		final ILElement element = inter.getElement();
		if (element == null || element.isImplicit()) {
			return "No element to deleted or non deletable element";
		}
		final AtomicOperation op = OperationFactory.deleteElement(element
				.getElement());
		History.getInstance().addOperation(op);
		editor.resync(null);
		return "Element deleted.";
	}
}
