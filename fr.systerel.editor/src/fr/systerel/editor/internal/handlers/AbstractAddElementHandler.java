/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;

import org.eventb.ui.manipulation.ElementManipulationFacade;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.handlers.context.ChildCreationInfo;

/**
 * An abstract implementation sharing the method to create children. Clients
 * MUST implement the <code>handleSelection()</code>method or its parent
 * <code>execute()</code>.
 * 
 * @author "Thomas Muller"
 */
public abstract class AbstractAddElementHandler extends AbstractEditionHandler {

	protected static final String TYPE_ID = "typeID";

	
	protected void createElementAndRefresh(final RodinEditor editor,
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
		ElementManipulationFacade.createElementGeneric(
				localParent, type, localNextSibling);
	}

	@Override
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		final ChildCreationInfo possibility = getCreationPossibility(editor,
				caretOffset);
		return possibility != null
				&& !possibility.getPossibleChildTypes().isEmpty();
	}
	
	protected abstract ChildCreationInfo getCreationPossibility(RodinEditor editor,
			int caretOffset);

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
