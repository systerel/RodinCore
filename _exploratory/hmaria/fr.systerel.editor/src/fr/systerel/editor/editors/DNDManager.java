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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.RodinDocumentProvider;
import fr.systerel.editor.internal.documentModel.ModelOperations.ModelPosition;
import fr.systerel.editor.internal.documentModel.ModelOperations.Move;

/**
 * @author Nicolas Beauger
 * 
 */
@SuppressWarnings("restriction")
public class DNDManager {

	public static boolean DEBUG;

	private class Dragger extends DragSourceAdapter {
		public void dragStart(DragSourceEvent e) {
			e.doit = controller.hasSelectedElements();
			if (DEBUG)
				System.out.println("drag start " + e.doit);
		}

		public void dragSetData(DragSourceEvent e) {
			final ILElement[] elements = controller
					.getSelectedElements();
			e.data = toRElements(elements);
			if (DEBUG)
				System.out.println("set data " + e.data);
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

			// works only because style ranges are totally recomputed
			// upon drop completion (editor resynchronisation)
			controller.resetSelectionNoEffect(offset);
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
			final ModelPosition pos = mapper.findModelPosition(offset,
					siblingType, parentType);
			if (pos == null)
				return;
			final List<ILElement> elems = toLElements(elements);
			if (elems == null)
				return;
			new Move(pos).perform(elems);
			documentProvider.doSynchronize(mapper.getRoot(), null);
		}

		private List<ILElement> toLElements(IRodinElement[] elements) {
			final List<ILElement> result = new ArrayList<ILElement>(elements.length);
			for (IRodinElement element : elements) {
				final ILElement lElement = SynchroUtils.findElement(element,
						mapper.getRoot());
				if (lElement == null)
					return null;
				result.add(lElement);
			}
			return result;
		}

	}
	
	private static IRodinElement[] toRElements(ILElement[] elements) {
		final IRodinElement[] result = new IRodinElement[elements.length];
		for (int i = 0; i < elements.length; i++) {
			final ILElement element = elements[i];
			result[i] = element.getElement();
		}
		return result;
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
