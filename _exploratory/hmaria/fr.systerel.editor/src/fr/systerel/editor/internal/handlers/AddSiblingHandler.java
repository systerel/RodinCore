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
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILUtils;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.operations.AtomicOperation;
import fr.systerel.editor.internal.operations.History;
import fr.systerel.editor.internal.operations.OperationFactory;

/**
 * Handler to add siblings.
 */
public class AddSiblingHandler extends AbstractEditionHandler {

	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		final SiblingCreationInfo info = getSiblingCreationInfo(editor, offset);
		if (info == null) {
			return "No possible Sibling creation";
		}
		final AtomicOperation op = OperationFactory.createElementGeneric(
				info.getParent(), info.getElement().getElementType(),
				info.getSibling());
		History.getInstance().addOperation(op);
		editor.resync(null, false);
		return "Added Sibling";
	}
	
	private static SiblingCreationInfo getSiblingCreationInfo(
			RodinEditor editor, int offset) {
		final SiblingCreationInfo info = new SiblingCreationInfo();
		final DocumentMapper mapper = editor.getDocumentMapper();
		final EditorElement item = mapper.findItemContaining(offset);
		if (item == null) {
			final Interval inter = mapper.findEditableIntervalBefore(offset);
			if (inter == null)
				info.setElement(null);
			else info.setElement(inter.getElement());
			info.setFoundBefore(true);
		} else {
			info.setElement(item.getLightElement());
		}
		if (info.getElement() == null) {
			return null;
		}
		final ILElement parent = info.getElement().getParent();
		if (parent == null)
			return null;
		if (info.getElement().isImplicit() || info.isFoundBefore()) {
			info.setSibling(null);
		} else {
			try {
				// we search to insert the sibling after the current element
				final IInternalElement siblingAfter = ILUtils.getNextSibling(
						parent, info.getElement().getElement());
				info.setSibling(siblingAfter);
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
		}
		return info;
	}
	
	
	@Override
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		return getSiblingCreationInfo(editor, caretOffset) != null;
	}
	
	protected static class SiblingCreationInfo {
		
		private boolean foundBefore = false;
		private ILElement element;
		private IInternalElement sibling;
		
		public void setElement(ILElement element) {
			this.element = element;
		}
		
		public ILElement getElement() {
			return element;
		}

		public void setFoundBefore(boolean foundBefore) {
			this.foundBefore = foundBefore;
		}

		public boolean isFoundBefore() {
			return foundBefore;
		}

		public void setSibling(IInternalElement sibling) {
			this.sibling = sibling;
		}

		public IInternalElement getSibling() {
			return sibling;
		}
		
		public IInternalElement getParent() {
			if (element.getParent() == null)
				return null;
			return element.getParent().getElement();
		}
		
	}

}
