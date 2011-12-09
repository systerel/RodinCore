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
package fr.systerel.editor.internal.editors;

import static fr.systerel.editor.internal.editors.RodinEditorUtils.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eventb.ui.manipulation.ElementManipulationFacade;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.ModelOperations.ModelPosition;
import fr.systerel.editor.internal.documentModel.ModelOperations.Move;
import fr.systerel.editor.internal.documentModel.RodinDocumentProvider;

/**
 * @author Nicolas Beauger
 * 
 */
public class DNDManager {

	public static boolean DEBUG;

	private static class Dragger extends DragSourceAdapter {
		
		private final SelectionController controller;
		
		public Dragger(SelectionController controller) {
			this.controller = controller;
		}
		
		@Override
		public void dragStart(DragSourceEvent e) {
			e.doit = controller.hasSelectedElements();
			if (DEBUG)
				System.out.println("drag start " + e.doit);
		}

		@Override
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
	
	private static class Dropper extends DropTargetAdapter {
		
		private final SelectionController controller;
		private final DocumentMapper mapper;
		private final StyledText styledText;
		
		
		public Dropper(StyledText styledText, SelectionController controller, DocumentMapper mapper) {
			this.controller = controller;
			this.mapper = mapper;
			this.styledText = styledText;
		}
		
		@Override
		public void dragEnter(DropTargetEvent e) {
			if (DEBUG)
				System.out.println("drag enter" + e);
			if (e.detail == DND.DROP_DEFAULT)
				e.detail = DND.DROP_MOVE;
		}

		@Override
		public void dragOperationChanged(DropTargetEvent e) {
			if (DEBUG)
				System.out.println("drag operation changed " + e);
			if (e.detail == DND.DROP_DEFAULT)
				e.detail = DND.DROP_MOVE;
		}
		
		@Override
		public void dragOver(DropTargetEvent event) {
			// Provide visual feedback
			event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
		}

		@Override
		public void drop(DropTargetEvent e) {
			final int offset = getOffset(e);
			final IRodinElement[] elements = (IRodinElement[]) e.data;
			if (DEBUG) {
				System.out.println("drop " + e);
				System.out.println(Arrays.asList(elements));
				System.out.println("at " + offset);
			}
			processDrop(elements, offset);
			releaseSelection();
		}

		/**
		 * Frees the cumbersome selection after drop, that occurs on Mac and
		 * Windows platform which seem to absorb the mouse down/up event. This
		 * is a patch as it was impossible to locate the exact cause of such
		 * behavior.
		 */
		private void releaseSelection() {
			final Event event = new Event();
			event.button = SWT.BUTTON1;
			styledText.notifyListeners(SWT.MouseUp, event);
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

	public DNDManager(SelectionController controller, StyledText styledText,
			DocumentMapper mapper, RodinDocumentProvider documentProvider) {
		this.controller = controller;
		this.styledText = styledText;
		this.mapper = mapper;
	}

	public void install() {
		DragSource source = null;
		DropTarget target = null;
		try {
			source = createDragSource(styledText, controller);
			target = createDropTarget(styledText, controller, mapper);
		} catch (SWTError e) {
			// could happen on specific OS platforms
			log(null,
					"Drag-and-drop has been disabled in Rodin Editor because:\n"
							+ e.getMessage());
			// not logging SWT error to avoid popup about workbench instability
			// recover
			if (source != null) {
				for (DragSourceListener listener : source.getDragListeners()) {
					source.removeDragListener(listener);
				}
				source.dispose();
			}
			if (target != null) {
				for (DropTargetListener listener : target.getDropListeners()) {
					target.removeDropListener(listener);
				}
				target.dispose();
			}
		}
		// TODO customize DragSourceEffect, DropTargetEffect
	}
	
	private static DragSource createDragSource(final StyledText styledText,
			SelectionController controller) {
		final DragSource source = new DragSource(styledText, DND.DROP_MOVE);
		final Transfer[] types = new Transfer[] { ElementManipulationFacade
				.getRodinHandleTransfer() };
		source.setTransfer(types);
		source.addDragListener(new Dragger(controller));
		return source;
	}
	
	private static DropTarget createDropTarget(final StyledText styledText,
			SelectionController controller, DocumentMapper mapper) {
		final DropTarget target = new DropTarget(styledText, DND.DROP_DEFAULT
				| DND.DROP_MOVE);
		final Transfer[] types = new Transfer[] { ElementManipulationFacade
				.getRodinHandleTransfer() };
		target.setTransfer(types);
		target.addDropListener(new Dropper(styledText, controller, mapper));
		return target;
	}
	
}
