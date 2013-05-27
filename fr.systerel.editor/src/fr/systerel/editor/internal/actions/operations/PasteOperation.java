/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.actions.operations;

import static fr.systerel.editor.internal.editors.RodinEditorUtils.debug;
import static fr.systerel.editor.internal.editors.RodinEditorUtils.log;
import static fr.systerel.editor.internal.editors.RodinEditorUtils.showInfo;

import org.eventb.ui.manipulation.ElementManipulationFacade;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.editor.EditorPlugin;

/**
 * Implements a paste operation with internal elements. We first check that the
 * operation can be carried out, then perform it if possible.
 * 
 * @author Laurent Voisin
 */
public class PasteOperation {

	private final IInternalElement target;
	private final IRodinElement[] elems;

	private IInternalElement parent;
	private IInternalElement sibling;

	public PasteOperation(IRodinElement[] elems, IInternalElement target) {
		this.elems = elems;
		this.target = target;
	}

	public void execute() {
		if (!verify()) {
			showInfo("Cannot paste here", //
					"Cannot paste clipboard contents at the selected location.");
			return;
		}
		ElementManipulationFacade.copyElements(elems, parent, sibling);
		if (EditorPlugin.DEBUG) {
			debug("PASTED SUCCESSFULLY");
		}
	}

	/*
	 * Determines the actual target of the paste operation, which can be either
	 * the given target or its parent. Returns false if neither can work.
	 */
	private boolean verify() {
		parent = target;
		sibling = null;
		if (canCopy()) {
			return true;
		}
		final IRodinElement p = target.getParent();
		if (!(p instanceof IInternalElement)) {
			// target is a root element
			return false;
		}
		parent = (IInternalElement) p;
		try {
			sibling = target.getNextSibling();
		} catch (RodinDBException e) {
			log(e.getStatus());
			return false;
		}
		return canCopy();
	}

	private boolean canCopy() {
		final IInternalElementType<?> parentType = parent.getElementType();
		for (final IRodinElement elem : elems) {
			final IElementType<?> elementType = elem.getElementType();
			if (!(elementType instanceof IInternalElementType<?>)) {
				return false;
			}
			if (!parentType.canParent((IInternalElementType<?>) elementType)) {
				return false;
			}
		}
		return true;
	}

}
