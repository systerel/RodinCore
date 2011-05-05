/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.editors;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eventb.internal.ui.RodinHandleTransfer;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.EditorElement;
import fr.systerel.editor.documentModel.Interval;
import fr.systerel.editor.documentModel.RodinDocumentProvider;

/**
 * @author Nicolas Beauger
 * 
 */
@SuppressWarnings("restriction")
public class DNDManager {

	public static boolean DEBUG;

	private class Dragger extends DragSourceAdapter {
		public void dragStart(DragSourceEvent e) {
			e.doit = controller.getSelectedElement() != null;
			if (DEBUG)
				System.out.println("drag start " + e.doit);
		}

		public void dragSetData(DragSourceEvent e) {
			final IInternalElement element = controller
					.getSelectedElement().getElement();
			e.data = new IRodinElement[] { element };
			if (DEBUG)
				System.out.println("set data " + element);
		}

		@Override
		public void dragFinished(DragSourceEvent event) {
			if (DEBUG)
				System.out.println("drag finished");
		}

	}
	
	private class Dropper extends DropTargetAdapter {
		public void dragEnter(DropTargetEvent e) {
			if (DEBUG)
				System.out.println("drag enter" + e);
			if (e.detail == DND.DROP_DEFAULT)
				e.detail = DND.DROP_COPY;
		}

		public void dragOperationChanged(DropTargetEvent e) {
			if (DEBUG)
				System.out.println("drag operation changed " + e);
			if (e.detail == DND.DROP_DEFAULT)
				e.detail = DND.DROP_COPY;
		}

		public void drop(DropTargetEvent e) {
			final Point loc = styledText.toControl(e.x, e.y);
			final int offset = controller.getOffset(loc);
			final IRodinElement[] elements = (IRodinElement[]) e.data;
			if (DEBUG) {
				System.out.println("drop " + e);
				System.out.println(Arrays.asList(elements));
				System.out.println("at " + offset);
			}
			processDrop(elements, offset);
		}

		private void processDrop(IRodinElement[] elements, int offset) {
			for (IRodinElement element : elements) {
				final ILElement lElement = SynchroUtils.findElement(
						element, mapper.getRoot());
				final ILElement selectionParent = lElement.getParent();
				if (selectionParent == null)
					return; // cannot move root
				final int oldPos = selectionParent
						.getChildPosition(lElement);
				final int newPos = findInsertPos(offset, lElement, oldPos);
				if (newPos == INVALID_POS)
					return;
				assert oldPos >= 0;
				selectionParent.moveChild(newPos, oldPos);
			}
			try {
				documentProvider.doSynchronize(mapper.getRoot(), null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private static final int INVALID_POS = -1;

		private int findInsertPos(int offset, ILElement selection,
				int oldPos) {
			final ILElement selectionParent = selection.getParent();
			final ILElement before = findSiblingBefore(offset, selection);
			if (before != null) {
				final int posBefore = selectionParent
						.getChildPosition(before);
				assert posBefore >= 0;
				if (posBefore < oldPos) {
					return posBefore + 1;
				} else {
					return posBefore;
				}
			}
			// try sibling after
			final ILElement after = findSiblingAfter(offset, selection);
			if (after != null) {
				final int posAfter = selectionParent
						.getChildPosition(after);
				assert posAfter >= 0;
				if (oldPos < posAfter) {
					return posAfter - 1;
				} else {
					return posAfter;
				}
			}
			return INVALID_POS;
		}

		private ILElement findSiblingBefore(int offset, ILElement selection) {
			final Interval intervalBefore = mapper
					.findEditableIntervalBefore(offset);
			if (intervalBefore == null)
				return null;
			return findSiblingAt(intervalBefore.getOffset(), selection);
		}

		private ILElement findSiblingAfter(int offset, ILElement selection) {
			final Interval intervalAfter = mapper
					.findEditableIntervalAfter(offset);
			if (intervalAfter == null)
				return null;
			return findSiblingAt(intervalAfter.getLastIndex(), selection);
		}

		private ILElement findSiblingAt(int offset, ILElement selection) {
			final EditorElement item = mapper.findItemContaining(offset);
			if (item == null)
				return null;
			final ILElement sibling = findDirectChild(
					item.getLightElement(), selection.getParent());
			if (sibling == null)
				return null;
			if (sameType(sibling, selection)) {
				return sibling;
			} else {
				return null;
			}
		}


	}
	
	private static boolean sameType(ILElement el1, ILElement el2) {
		return el1.getElementType() == el2.getElementType();
	}
	
	private static ILElement findDirectChild(ILElement descendant,
			ILElement parent) {
		final ILElement descParent = descendant.getParent();
		if (descParent == null) { // parent of root
			return null;
		}
		if (descParent.equals(parent)) {
			return descendant;
		}
		return findDirectChild(descParent, parent);
	}
	
	private final SelectionController controller;
	private final StyledText styledText;
	private final DocumentMapper mapper;
	private final RodinDocumentProvider documentProvider;

	public DNDManager(SelectionController controller, StyledText styledText,
			DocumentMapper mapper, RodinDocumentProvider documentProvider) {
		this.controller = controller;
		this.styledText = styledText;
		this.mapper = mapper;
		this.documentProvider = documentProvider;
	}

	public void install() {
		styledText.setDragDetect(false);
		// remove standard DND
		styledText.setData(DND.DRAG_SOURCE_KEY, null);
		styledText.setData(DND.DROP_TARGET_KEY, null);

		final DragSource source = new DragSource(styledText, DND.DROP_COPY
				| DND.DROP_MOVE);
		source.setTransfer(new Transfer[] { RodinHandleTransfer.getInstance() });
		source.addDragListener(new Dragger());
		
		final DropTarget target = new DropTarget(styledText, DND.DROP_DEFAULT
				| DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
		target.setTransfer(new Transfer[] { RodinHandleTransfer.getInstance() });
		target.addDropListener(new Dropper());
		
		// TODO customize DragSourceEffect, DropTargetEffect
	}

}
