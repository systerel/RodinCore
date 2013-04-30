/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import static fr.systerel.editor.internal.editors.RodinEditorUtils.convertEventToKeystroke;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.HANDLE_TYPE;
import static org.eclipse.jface.bindings.keys.KeyStroke.NO_KEY;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.bindings.keys.KeyLookupFactory;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISelectionService;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.Selections.MultipleSelection;
import fr.systerel.editor.internal.editors.Selections.SelectionEffect;

/**
 * Controls the selection in the RodinEditor and decides when it should be
 * editable. It is not possible to have just parts of the text editable. So the
 * whole styled text widget is set to editable or not according to the current
 * carret position. Since this class directly accesses the styled text widget,
 * coordinates may be in need of transformation, since the editor implements
 * folding and the <code>DocumentMapper</code> works with model coordinates and
 * not widget coordinates.
 */
public class SelectionController implements MouseListener, VerifyListener,
		VerifyKeyListener, ISelectionProvider {

	private static final Integer CR = KeyLookupFactory.getDefault()
			.formalKeyLookupInteger(IKeyLookup.CR_NAME);


	// TODO tracing
	public static boolean DEBUG;
	

	private StyledText styledText;
	private DocumentMapper mapper;
	private ProjectionViewer viewer;
	private OverlayEditor overlayEditor;
	private final MultipleSelection selection;


	private final ListenerList postSelectionChangedListeners;
	
	/**
	 * Last selected offset
	 */
	private int clickedOffset;

	public SelectionController(StyledText styledText, DocumentMapper mapper,
			ProjectionViewer viewer, OverlayEditor overlayEditor) {
		super();
		this.styledText = styledText;
		this.viewer = viewer;
		this.mapper = mapper;
		this.overlayEditor = overlayEditor;
		this.postSelectionChangedListeners = new ListenerList();
		selection = new MultipleSelection(new SelectionEffect(styledText));
	}

	public boolean hasSelectedElements() {
		return !selection.isEmpty();
	}
	
	/**
	 * @return the selectedElement
	 */
	public ILElement[] getSelectedElements() {
		return selection.getElements();
	}
	
	@Override
	public void mouseDoubleClick(MouseEvent e) {
		if (DEBUG)
			System.out.println("double click " + e);
		final int offset = getOffset(e);
		if (offset < 0) return;
		toggleSelection(offset);
		// clear the native and cumbersome selection
		// that occurs after double click
		styledText.setSelection(offset);
		overlayEditor.quitEdition(false);
	}
	
	public EditPos toggleSelection(int offset) {
		final EditorElement editElem = mapper.findEditorElementAt(offset);
		if (editElem == null) return null;
		return toggleSelection(editElem);
	}

	public EditPos toggleSelection(ILElement element) {
		final EditorElement editElem = mapper.findEditorElement(element);
		if (editElem == null) return null;
		return toggleSelection(editElem);
	}
	
	public void toggleSelection(EditorElement[] editElems) {
		for (EditorElement elem : editElems) {
			toggleSelection(elem);
		}
	}
	
	private EditPos toggleSelection(EditorElement editElem) {
		final ILElement element = editElem.getLightElement();
		if (element.isImplicit()) return null;
		final EditPos editElemPos = editElem.getPos();
		if (editElemPos == null) return null;
		
		// TODO position is only useful if element is not selected
		selection.toggle(element, editElemPos);
		firePostSelectionChanged(new SelectionChangedEvent(this, getSelection()));
		if (DEBUG)
			System.out.println("selected " + element.getElement() + " in "
					+ editElemPos);
		return editElemPos;
	}

	private ISelection adaptCurrentSelection() {
		return new StructuredSelection(selection.getElements());
	}

	public ILElement getSelectionAt(int offset) {
		return selection.getSelectionAt(offset);
	}
	
	public boolean isSelected(ILElement element) {
		return selection.contains(element); // FIXME or contains a parent ?
	}

	private int getModelCaretOffset() {
		return viewer.widgetOffset2ModelOffset(styledText
				.getCaretOffset());
	}
	
	private int getOffset(MouseEvent e) {
		return getOffset(new Point(e.x, e.y));
	}

	public int getOffset(Point p) {
		try {
			return styledText.getOffsetAtLocation(p);
		} catch (IllegalArgumentException exc) {
			// not over a character
			final int lineIndex = styledText.getLineIndex(p.y);
			final int offset;
			if (lineIndex < styledText.getLineCount() - 1) {
				offset = styledText.getOffsetAtLine(lineIndex + 1);
				return offset - 1;
			}
			return styledText.getOffsetAtLine(lineIndex);
		}
	}
	
	private boolean handleHandleSelection(final int offset) {
		final Interval inter = mapper.findInterval(offset);
		if (inter != null && inter.getContentType().equals(HANDLE_TYPE)) {
			toggleSelection(offset);
			return true;
		}
		return false;
	}

	private EditorElement[] keepTopLevelElements(EditorElement[] editElems) {
		final List<ILElement> parents = new ArrayList<ILElement>();
		for (EditorElement elem : editElems) {
			parents.add(elem.getLightElement());
		}
		final List<EditorElement> result = new ArrayList<EditorElement>();
		for (EditorElement elem : editElems) {
			if (!parents.contains(elem.getLightElement().getParent())) {
				result.add(elem);
			}
		}
		return result.toArray(new EditorElement[result.size()]);
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (DEBUG)
			System.out.println("mouse down " + e);

		final int offset = getOffset(e);
		if (offset < 0)
			return;
		// Button 3 is the right button
		// Filtering event to display the context menu
		if (e.button == 3) {
			return;
		}
		if (selection.contains(offset)) {
			if (styledText.dragDetect(e))
				return;
		}
		if ((e.stateMask & SWT.MOD1) != 0) {
			toggleSelection(offset);
			return;
		}
		if ((e.stateMask & SWT.MOD2) != 0) {
			selectZone(clickedOffset, offset);
			return;
		}
		clickedOffset = offset;
		clearSelection();
		if (overlayEditor.isActive()) {
			// the user clicked outside the overlay editor
			// as this listener is on the main text therefore
			// we quit overlay edition
			overlayEditor.saveAndExit(false);
		}
		if (handleHandleSelection(offset)) {
			return;
		} else {
			overlayEditor.showAtOffset(offset);
		}
		resetSelection(offset);
	}

	private void resetSelection(int offset) {
		selection.clear();
		firePostSelectionChanged(new SelectionChangedEvent(this, getSelection()));
	}
	
	public void selectItems(ILElement[] selected) {
		selection.clear();
		for (ILElement e : selected) {
			final EditorElement editorElem = mapper.findEditorElement(e);
			if (editorElem == null)
				continue;
			final ILElement element = editorElem.getLightElement();
			if (element.isImplicit())
				return;
			final EditPos enclosingPos = mapper.getItemPosition(editorElem);
			if (enclosingPos == null)
				return;
			selection.add(element, enclosingPos);
			// styledText.setSelection(start);
			if (DEBUG)
				System.out.println("selected " + element.getElement() + " in "
						+ enclosingPos);
		}
		firePostSelectionChanged(new SelectionChangedEvent(this, getSelection()));
	}
	
	/** Removes all selections */
	public void clearSelection() {
		selection.clear();
	}

	public void selectZone(int offset1, int offset2) {
		clearSelection();
		final int minOffset = Math.min(offset1, offset2);
		final int maxOffset = Math.max(offset1, offset2);
		final int firstLine = styledText.getLineAtOffset(minOffset);
		final int lastLine = styledText.getLineAtOffset(maxOffset);
		final int startOffset = styledText.getOffsetAtLine(firstLine);
		final int lastLineOffset = styledText.getOffsetAtLine(lastLine);
		final int lastLineLength = styledText.getLine(lastLine).length();
		final int endOffset = lastLineOffset + lastLineLength;
		final EditorElement[] editElems = mapper.findEditorElementsBetween(
				startOffset, endOffset);
		if (editElems.length == 0)
			return;
		// clear the native and cumbersome selection
		// that occurs after double click
		styledText.setSelection(minOffset);
		overlayEditor.quitEdition(false);
		toggleSelection(keepTopLevelElements(editElems));
	}
	
	@Override
	public void mouseUp(MouseEvent e) {
		if (DEBUG)
			System.out.println("mouse up " + e);
		// nothing to do
	}

	@Override
	public void verifyText(VerifyEvent e) {
		int start = viewer.widgetOffset2ModelOffset(e.start);

		Interval editable = mapper.findEditableInterval(start);
		// if there is no editable interval in the region, cancel.
		// this includes cases where there is a deletion right before an
		// editable interval
		if (editable == null) {
			// System.out.println("no editable here");
			e.doit = false;
			return;
		}
		// do not delete after the editable has ended
		int end = viewer.widgetOffset2ModelOffset(e.end);
		if (editable.getLastIndex() < end
				&& e.text.length() == 0) {
			// System.out.println("can not delete after editable has ended");
			e.doit = false;
			return;
		}
	}
	
	@Override
	public void verifyKey(VerifyEvent event) {
		final KeyStroke keystroke = convertEventToKeystroke(event);
		if (keystroke.getNaturalKey() == CR
				&& keystroke.getModifierKeys() == NO_KEY) {
			final int offset = styledText.getCaretOffset();
			if (offset >= 0 && handleHandleSelection(offset)) {
				return;
			}
			overlayEditor.showAtOffset(offset);
		}
	}

	public void goToNextEditRegion() {
		final int offset = getModelCaretOffset();
		final Interval next = mapper.findEditableIntervalAfter(offset);
		if (next != null) {
			final int nextOffset = next.getOffset();
			final int newOffset = viewer.modelOffset2WidgetOffset(nextOffset);
			styledText.setSelection(newOffset);
			if (next.getAttributeManipulation() == null)
				overlayEditor.showAtOffset(newOffset);
		}
	}

	public void goToPreviousEditRegion() {
		final int offset = getModelCaretOffset();
		final Interval previous = mapper.findEditableIntervalBefore(offset);
		if (previous != null) {
			final int prevOffset = previous.getOffset();
			final int newOffset = viewer.modelOffset2WidgetOffset(prevOffset);
			styledText.setSelection(newOffset);
			if (previous.getAttributeManipulation() == null)
				overlayEditor.showAtOffset(newOffset);
		}
	}

	/**
	 * Returns the selection in terms of a structured selection.
	 */
	@Override
	public ISelection getSelection() {
		return adaptCurrentSelection();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		postSelectionChangedListeners.add(listener);
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		postSelectionChangedListeners.remove(listener);
	}

	/**
	 * Sets the ILElements contained by the selection iteratively if the given
	 * selection is an instance of StructuredSelection.
	 * 
	 * This method comes from {@link ISelectionProvider} and is kept for
	 * backward compatibility, but the clients should use the
	 * {@link ISelectionService} instead.
	 */
	@Override
	public void setSelection(ISelection selection) {
		if (selection instanceof StructuredSelection) {
			final List<ILElement> elements = new ArrayList<ILElement>();
			final Iterator<?> itr = ((StructuredSelection) selection)
					.iterator();
			while (itr.hasNext()) {
				final Object sel = itr.next();
				if (sel instanceof ILElement) {
					elements.add((ILElement) sel);
				}
			}
			selectItems(elements.toArray(new ILElement[elements.size()]));
			firePostSelectionChanged(new SelectionChangedEvent(viewer,
					new StructuredSelection(elements)));
		}
	}
	
	/**
	 * Notifies any post selection listeners that a post selection event has
	 * been received. Only listeners registered at the time this method is
	 * called are notified.
	 * 
	 * @param event
	 *            a selection changed event
	 * 
	 * @see #addPostSelectionChangedListener(ISelectionChangedListener)
	 */
	protected void firePostSelectionChanged(final SelectionChangedEvent event) {
		final Object[] listeners = postSelectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}

}
