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

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.handlers.context.ChildCreationInfo;
import fr.systerel.editor.internal.operations.AtomicOperation;
import fr.systerel.editor.internal.operations.History;
import fr.systerel.editor.internal.operations.OperationFactory;

/**
 * An abstract implementation sharing the method to create children. Clients
 * MUST implement the <code>handleSelection()</code>method or its parent
 * <code>execute()</code>.
 * 
 * @author "Thomas Muller"
 */
public abstract class AbstractAddChildHandler extends AbstractEditionHandler {

	protected void createChildAndRefresh(final RodinEditor editor,
			final ChildCreationInfo childInfo,
			final IInternalElementType<?> type) {
		final ILElement nextSibling = childInfo.getNextSibling();
		final ILElement childParent = childInfo.getParent();
		final IInternalElement localNextSibling = (nextSibling == null || nextSibling
				.getElementType() != type) ? null : nextSibling.getElement();
		final IInternalElement rootElement = (childParent == null) ? editor
				.getDocumentMapper().getRoot().getElement() : childParent
				.getRoot().getElement();
		final IInternalElement localParent;
		if (childParent.getElement().equals(rootElement)) {
			localParent = rootElement;
		} else {
			localParent = childParent.getElement();
		}
		final AtomicOperation op = OperationFactory.createElementGeneric(
				localParent, type, localNextSibling);
		History.getInstance().addOperation(op);
		editor.resync(null, false);
	}

	@Override
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		final ChildCreationInfo possibility = editor.getDocumentMapper()
				.getChildCreationPossibility(caretOffset);
		if (possibility == null) {
			return false;
		}
		if (possibility.getPossibleChildTypes().isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Subclasses MUST override this method, or its parent
	 * <code>execute()</code>.
	 */
	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		// do nothing
		return null;
	}

}
