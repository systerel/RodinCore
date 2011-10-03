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

import static org.rodinp.core.emf.api.itf.ILUtils.getNextSibling;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.actions.operations.AtomicOperation;
import fr.systerel.editor.internal.actions.operations.RodinEditorHistory;
import fr.systerel.editor.internal.actions.operations.OperationFactory;
import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.RodinEditor;

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
				info.getParent(), info.getElementType(),
				info.getSibling());
		RodinEditorHistory.getInstance().addOperation(op);
		return "Added Sibling";
	}
	
	private static SiblingCreationInfo getSiblingCreationInfo(
			RodinEditor editor, int offset) {
		final SiblingCreationInfo info = new SiblingCreationInfo();
		final DocumentMapper mapper = editor.getDocumentMapper();
		final Interval interAfter = mapper
				.findEditableKindOfIntervalAfter(offset);
		if (interAfter == null) {
			return null;
		}
		final ILElement element = interAfter.getElement();
		if (element == null) {
			return null;
		}
		final ILElement parent = element.getParent();
		if (parent == null) { // we are next to an implicit element;
			return getInfoForImplicitKindOfSibling(offset, info, mapper,
					element);
		}
		info.setParent(parent.getElement());
		info.setElementType(element.getElementType());
		try {
			final IInternalElement nextSibling;
			nextSibling = getNextSibling(parent, element.getElement());
			info.setSibling(nextSibling);
			return info;
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static SiblingCreationInfo getInfoForImplicitKindOfSibling(
			int offset, final SiblingCreationInfo info,
			final DocumentMapper mapper, final ILElement element) {
		if (!element.isImplicit()) {
			return null;
		}
		final Interval iBF = mapper.findEditableIntervalBefore(offset);
		if (iBF == null) {
			return null;
		}
		final ILElement aSiblingBefore = iBF.getElement();
		if (aSiblingBefore == null) {
			return null;
		}
		final ILElement pBF = aSiblingBefore.getParent();
		if (pBF == null) {
			return null;
		}
		info.setParent(pBF.getElement());
		info.setElementType(element.getElementType());
		return info;
	}
	
	
	@Override
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		return getSiblingCreationInfo(editor, caretOffset) != null;
	}
	
	protected static class SiblingCreationInfo {
		
		private IInternalElement parent;
		private IInternalElementType<?> elementType;
		private IInternalElement sibling;
		
		public void setParent(IInternalElement parent) {
			this.parent = parent;
		}

		public IInternalElement getParent() {
			return parent;
		}

		public void setSibling(IInternalElement sibling) {
			this.sibling = sibling;
		}

		public IInternalElement getSibling() {
			return sibling;
		}
		
		public void setElementType(IInternalElementType<?> elementType) {
			this.elementType = elementType;
		}
		
		public IInternalElementType<?> getElementType() {
			return elementType;
		}
		
	}

}
