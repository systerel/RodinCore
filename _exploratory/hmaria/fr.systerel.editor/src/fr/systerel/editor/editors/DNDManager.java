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
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.lightcore.LightElement;
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

	private static class Move {
		private final ILElement targetParent;
		private final ILElement nextSibling;
		
		public Move(ILElement parent, ILElement nextSibling) {
			this.targetParent = parent;
			this.nextSibling = nextSibling;
		}
		
		public void perform(ILElement[] elements) {
			for (ILElement element : elements) {
				if(targetParent.equals(element.getParent())) {
					final int oldPos = targetParent.getChildPosition(element);
					final int newPos;
					if (nextSibling == null) {
						newPos = targetParent.getChildren().size() - 1;
					} else {
						final int siblingPos = targetParent.getChildPosition(nextSibling);
						newPos = siblingPos;
					}
					targetParent.moveChild(newPos, oldPos);
				} else {
					// FIXME API missing in ILElement or TODO in addChild
					((LightElement) element).setEParent((LightElement) targetParent);
					targetParent.addChild(element, nextSibling);
				}
			}
		}
	}
	
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
			final int offset = getOffset(e);
			final IRodinElement[] elements = (IRodinElement[]) e.data;
			if (DEBUG) {
				System.out.println("drop " + e);
				System.out.println(Arrays.asList(elements));
				System.out.println("at " + offset);
			}
			processDrop(elements, offset);
			styledText.setSelection(offset);
		}

		private int getOffset(DropTargetEvent e) {
			final Point loc = styledText.toControl(e.x, e.y);
			final int offset = controller.getOffset(loc);
			return offset;
		}

		private void processDrop(IRodinElement[] elements, int offset) {
			if (elements.length == 0) return;
			final IElementType<?> siblingType = checkAndGetSameType(elements);
			if (siblingType == null)
				return;
			final IElementType<?> parentType = elements[0].getParent().getElementType();
			final Move move = findMove(offset, siblingType, parentType);
			if (move == null)
				return;
			final ILElement[] elems = toLElements(elements);
			if (elems == null)
				return;
			move.perform(elems);
			try {
				documentProvider.doSynchronize(mapper.getRoot(), null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private ILElement[] toLElements(IRodinElement[] elements) {
			final ILElement[] result = new ILElement[elements.length];
			for (int i = 0; i < elements.length; i++) {
				final IRodinElement element = elements[i];
				final ILElement lElement = SynchroUtils.findElement(element,
						mapper.getRoot());
				if (lElement == null)
					return null;
				result[i] = lElement;
			}
			return result;
		}

		private Move findMove(int offset, IElementType<?> type,
				IElementType<?> parentType) {
			// try sibling after
			final ILElement after = findElementAfter(offset, type);
			if (after != null) {
				final ILElement parent = after.getParent();
				return new Move(parent, after);
			}
			// try parent before, insert at the end
			final ILElement parent = findElementBefore(offset, parentType);
			if (parent != null) {
				return new Move(parent, null);
			}
			return null;
		}

		private ILElement findElementBefore(int offset, IElementType<?> type) {
			final Interval intervalBefore = mapper
					.findEditableIntervalBefore(offset);
			if (intervalBefore == null)
				return null;
			return findElementAt(intervalBefore.getOffset(), type);
		}

		private ILElement findElementAfter(int offset, IElementType<?> type) {
			final Interval intervalAfter = mapper
					.findEditableIntervalAfter(offset);
			if (intervalAfter == null)
				return null;
			return findElementAt(intervalAfter.getLastIndex(), type);
		}

		private ILElement findElementAt(int offset, IElementType<?> type) {
			final EditorElement item = mapper.findItemContaining(offset);
			if (item == null)
				return null;
			final ILElement element = findAncestorOftype(
					item.getLightElement(), type);
			return element;
		}


	}
	
	private static IElementType<?> checkAndGetSameType(IRodinElement[] elements) {
		if (elements.length == 0)
			return null;
		final IElementType<?> type = elements[0].getElementType();
		for (IRodinElement element : elements) {
			if (element.getElementType() != type)
				return null;
		}
		return type;
	}

	private static ILElement findAncestorOftype(ILElement descendant,
			IElementType<?> type) {
		if (descendant.getElementType() == type) return descendant;
		final ILElement descParent = descendant.getParent();
		if (descParent == null) { // parent of root
			return null;
		}
		return findAncestorOftype(descParent, type);
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
