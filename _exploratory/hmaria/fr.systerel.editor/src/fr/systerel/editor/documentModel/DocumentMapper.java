/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.documentModel;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.LightElement;

import fr.systerel.editor.editors.RodinConfiguration;

/**
 * Maps <code>Intervals</code> to a document.
 * 
 * The following rules applies for Intervals:
 * <ul>
 * <li>an editable is never next to another editable interval.</li>
 * <li>for each offset there can be at most three intervals at that position.</li>
 * <li>only editable intervals can be zero length.</li>
 * </ul>
 */
public class DocumentMapper {

	private ArrayList<Interval> intervals = new ArrayList<Interval>();
	private LightElement root;
	private Interval previous;
	private IDocument document;
	private RodinDocumentProvider documentProvider;

	private HashMap<LightElement, EditorItem> editorElements = new HashMap<LightElement, EditorItem>();
	private HashMap<IInternalElementType<?>, EditorItem> sections = new HashMap<IInternalElementType<?>, EditorItem>();

	/**
	 * Adds an interval to the document mapper at the end of the list. The
	 * intervals must be added in the order they appear in the text!
	 * 
	 * @param interval
	 * @throws Exception
	 */
	public void addInterval(Interval interval) throws Exception {
		if (intervals.size() > 0) {
			if (intervals.get(intervals.size() - 1).compareTo(interval) > 0) {
				throw new Exception("Insertion must be sorted");
			}
		}
		intervals.add(interval);

	}

	/**
	 * Adds an interval to the document mapper just after a given interval. If
	 * the given previous interval is not found in the list, the new interval is
	 * added at the end of the list. The intervals must be added in the order
	 * they appear in the text!
	 * 
	 * @param interval
	 * @param previous
	 * @throws Exception
	 */
	public void addIntervalAfter(Interval interval, Interval previous) {
		int index = intervals.indexOf(previous);
		if (index >= 0 && index < intervals.size()) {
			intervals.add(index + 1, interval);
		} else {
			try {
				addInterval(interval);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * returns all intervals that are contained in the range starting from
	 * offset
	 * 
	 * @param offset
	 *            The offset of the range.
	 * @param length
	 *            The length of the range
	 * @return All intervals that are found in the range.
	 */
	public Interval[] findIntervals(int offset, int length) {
		int index = findFirstIntervalIndex(offset);
		if (index >= 0) {
			ArrayList<Interval> results = new ArrayList<Interval>();
			for (int i = index; i < intervals.size(); i++) {
				if (intervals.get(i).getOffset() <= offset + length) {
					results.add(intervals.get(i));
				}

			}
			return results.toArray(new Interval[results.size()]);
		} else if (intervals.size() > 0 && index < intervals.get(0).getOffset()) {
			ArrayList<Interval> results = new ArrayList<Interval>();
			for (Interval interval : intervals) {
				if (interval.getOffset() <= offset + length) {
					results.add(interval);
				}

			}
			return results.toArray(new Interval[results.size()]);

		}

		return new Interval[0];
	}

	/**
	 * Binary search to find an interval that contains an offset
	 * 
	 * @param offset
	 * @return
	 */
	private int findIntervalIndex(int offset) {
		int low = 0;
		int high = intervals.size() - 1;
		int mid;
		while (low <= high) {
			mid = (low + high) / 2;

			if (intervals.get(mid).getOffset() > offset) {
				high = mid - 1;
			} else if (intervals.get(mid).getOffset() < offset
					&& (intervals.get(mid).getOffset()
							+ intervals.get(mid).getLength() < offset)) {
				low = mid + 1;
			} else
				return mid;
		}
		return -1;
	}

	/**
	 * Finds the first interval that contains an offset (includes intervals
	 * ending at that position)
	 * 
	 * @param offset
	 * @return
	 */
	protected int findFirstIntervalIndex(int offset) {
		int result = findIntervalIndex(offset);
		// check the two previous intervals.
		if (result > 0) {
			Interval previous = intervals.get(result - 1);
			if (previous.getOffset() + previous.getLength() >= offset) {
				result = result - 1;
			}
		}
		if (result > 0) {
			Interval previous = intervals.get(result - 1);
			if (previous.getOffset() + previous.getLength() >= offset) {
				result = result - 1;
			}
		}

		return result;
	}

	/**
	 * Finds an editable interval for a given offset
	 * 
	 * @param offset
	 * @return the editable interval at the given offset or <code>null</code> if
	 *         none exists.
	 */
	public Interval findEditableInterval(int offset) {
		int index = findEditableIntervalIndex(offset);
		if (index >= 0) {
			return intervals.get(index);
		}
		return null;
	}

	/**
	 * Finds the first editable interval that starts after a given offset.
	 * 
	 * @param offset
	 * @return the first editable interval after the given offset or
	 *         <code>null</code> if none exists.
	 */
	public Interval findEditableIntervalAfter(int offset) {
		for (Interval interval : intervals) {
			if (interval.getOffset() > offset && interval.isEditable()) {
				return interval;
			}
		}
		return null;
	}

	/**
	 * Finds the index of the first interval that starts after a given offset.
	 * 
	 * @param offset
	 * @return the index of the first interval after the given offset or
	 *         <code>-1</code> if none exists.
	 */
	public int findIntervalIndexAfter(int offset) {
		for (Interval interval : intervals) {
			if (interval.getOffset() > offset) {
				return intervals.indexOf(interval);
			}
		}
		return -1;
	}

	/**
	 * Finds the last editable interval that ends before a given offset.
	 * 
	 * @param offset
	 * @return the editable interval before given offset or <code>null</code> if
	 *         none exists.
	 */
	public Interval findEditableIntervalBefore(int offset) {
		Interval previous = null;
		for (Interval interval : intervals) {
			if (interval.getOffset() + interval.getLength() >= offset) {
				return previous;
			}
			if (interval.isEditable()) {
				previous = interval;
			}
		}
		return null;
	}

	/**
	 * Finds the index of an interval that contains a given offset and is
	 * editable.
	 * 
	 * @param offset
	 * @return The index of the resulting interval or -1 if none was found.
	 */
	protected int findEditableIntervalIndex(int offset) {
		// an editable is never next to another editable interval (or in the
		// same position)
		// for each offset there can be at most three intervals at that position
		// only editable intervals can be zero length.
		int index = findFirstIntervalIndex(offset);
		if (index >= 0 && index < intervals.size()) {
			Interval interval = intervals.get(index);
			if (interval.isEditable()) {
				return index;
			}
			// try the next one
			if (index + 1 < intervals.size()) {
				interval = intervals.get(index + 1);
				if (offset >= interval.getOffset()
						&& offset <= interval.getOffset()
								+ interval.getLength()) {
					if (interval.isEditable()) {
						return index + 1;
					}
				}

			}
		}
		return -1;
	}

	protected Interval findFirstInterval(int offset) {
		int index = findFirstIntervalIndex(offset);
		if (index >= 0 && index < intervals.size()) {
			return intervals.get(index);
		}
		return null;
	}

	/**
	 * Processes an interval. Creates and adds a new interval, if there exists
	 * none yet. Otherwise updates length and offset. It is expected that this
	 * method is called in the order the intervals appear in the document.
	 * 
	 * @param offset
	 * @param length
	 * @param element
	 * @param contentType
	 */
	public void processInterval(int offset, int length, LightElement element,
			String contentType) {
		Interval inter;
		if (Interval.isEditableType(contentType)) {
			inter = findInterval(element, contentType);
			if (inter != null) {
				inter.setLength(length);
				inter.setOffset(offset);
			} else {
				inter = new Interval(offset, length, element, contentType);
				try {
					addIntervalAfter(inter, previous);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			if (intervals.indexOf(previous) < intervals.size() - 1) {
				inter = intervals.get(intervals.indexOf(previous) + 1);
				inter.setLength(length);
				inter.setOffset(offset);
			} else {
				inter = new Interval(offset, length, element, contentType);
				try {
					addInterval(inter);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		previous = inter;
		if (element != null) {
			EditorItem el = editorElements.get(element);
			if (el == null) {
				el = new EditorElement(element);
			}
			el.addInterval(inter);
			editorElements.put(element, el);
		}

	}

	/**
	 * Finds the first interval that belongs to the given element
	 * 
	 * @param element
	 * @return the first interval that belongs to the given element
	 */
	public Interval findInterval(IRodinElement element) {
		// TODO: adapt this method to editorElements. will be faster?
		for (Interval interval : intervals) {
			if (element.equals(interval.getElement())) {
				return interval;
			}
		}
		return null;
	}

	/**
	 * Finds the first interval that belongs to the given element and has a
	 * given contentType.
	 * 
	 * @param element
	 * @param contentType
	 * @return the first interval that belongs to the given element
	 */
	public Interval findInterval(LightElement element, String contentType) {
		// TODO: adapt this method to editorElements. will be faster?
		for (Interval interval : intervals) {
			if (element.equals(interval.getElement())
					&& interval.getContentType().equals(contentType)) {
				return interval;
			}
		}
		return null;
	}

	public ArrayList<Interval> getIntervals() {
		return intervals;
	}

	public LightElement getRoot() {
		return root;
	}

	public void setRoot(LightElement root) {
		this.root = root;
	}

	public void resetPrevious() {
		previous = null;
	}

	public EditorItem[] getEditorElements() {
		return editorElements.values().toArray(
				new EditorItem[editorElements.size()]);
	}

	public EditorItem[] getEditorElementsWithType() {
		return sections.values().toArray(
				new EditorItem[editorElements.size()]);
	}

	public EditorItem getEditorElement(LightElement key) {
		return editorElements.get(key);
	}

	public EditorItem getEditorSection(LightElement key) {
		return sections.get(key);
	}

	public void addEditorElement(LightElement key, EditorItem value) {
		editorElements.put(key, value);
	}

	public void addEditorSection(IInternalElementType<?> key,
			EditorSection value) {
		sections.put(key, value);
	}

	public void addEditorSection(IInternalElementType<?> type,
			int folding_start, int folding_length) {
		EditorItem el = sections.get(type);
		if (el == null) {
			el = new EditorSection(type);
			sections.put(type, el);
		}
		el.setFoldingPosition(folding_start, folding_length);
	}

	public Position[] getFoldingPositions() {
		ArrayList<Position> result = new ArrayList<Position>();
		for (EditorItem el : editorElements.values()) {
			if (el.getFoldingPosition() != null) {
				result.add(el.getFoldingPosition());
			}
		}
		for (EditorItem el : sections.values()) {
			if (el.getFoldingPosition() != null) {
				result.add(el.getFoldingPosition());
			}
		}
		return result.toArray(new Position[result.size()]);
	}

	public ProjectionAnnotation[] getFoldingAnnotations() {
		ArrayList<ProjectionAnnotation> result = new ArrayList<ProjectionAnnotation>();
		for (EditorItem el : editorElements.values()) {
			if (el.getFoldingAnnotation() != null) {
				result.add(el.getFoldingAnnotation());
			}
		}
		for (EditorItem el : sections.values()) {
			if (el.getFoldingAnnotation() != null) {
				result.add(el.getFoldingAnnotation());
			}
		}
		return result.toArray(new ProjectionAnnotation[result.size()]);
	}

	public void elementChanged(IRodinElement element) {
		EditorItem el = editorElements.get(element);
		if (el != null) {
			for (Interval interval : el.getIntervals()) {
				try {
					if (element instanceof IIdentifierElement
							&& interval.getContentType().equals(
									RodinConfiguration.IDENTIFIER_TYPE)) {
						checkIdentifier((IIdentifierElement) element, interval);
					} else if (element instanceof ILabeledElement
							&& interval.getContentType().equals(
									RodinConfiguration.IDENTIFIER_TYPE)) {
						checkLabeled((ILabeledElement) element, interval);
					} else if (element instanceof IAssignmentElement
							&& interval.getContentType().equals(
									RodinConfiguration.CONTENT_TYPE)) {
						checkAssignment((IAssignmentElement) element, interval);
					} else if (element instanceof IPredicateElement
							&& interval.getContentType().equals(
									RodinConfiguration.CONTENT_TYPE)) {
						checkPredicate((IPredicateElement) element, interval);
					} else if (element instanceof ICommentedElement
							&& interval.getContentType().equals(
									RodinConfiguration.COMMENT_TYPE)) {
						checkCommented((ICommentedElement) element, interval);
					}
				} catch (RodinDBException e) {
					e.printStackTrace();

				}
			}
		}
	}

	public void elementRemoved(IRodinElement element) {
		System.out.println("element removed: " + element);
		EditorItem editorElement = editorElements.get(element);
		if (editorElement != null) {
			int offset = editorElement.getOffset();
			int length = editorElement.getLength();
			removeElements(offset, length);
			removeIntervals(offset, length);
			adaptFoldingPositions(offset, -length);

			documentProvider.replaceTextInDocument(offset, length, "");
		}
	}

	protected void removeElements(int offset, int length) {
		ArrayList<EditorItem> toRemove = new ArrayList<EditorItem>();
		for (EditorItem element : editorElements.values()) {
			if (element.getOffset() >= offset
					&& element.getOffset() + element.getLength() <= offset
							+ length) {
				toRemove.add(element);
				element.dispose();
			}
		}
		editorElements.values().removeAll(toRemove);
		toRemove.clear();
		for (EditorItem element : sections.values()) {
			toRemove.add(element);
		}
		sections.values().removeAll(toRemove);
	}

	protected void removeIntervals(int offset, int length) {
		ArrayList<Interval> toRemove = new ArrayList<Interval>();
		for (Interval interval : intervals) {
			int end = interval.getOffset() + interval.getLength();
			if (interval.getOffset() >= offset && end <= offset + length) {
				toRemove.add(interval);
			}
			if (interval.getOffset() > offset) {
				break;
			}
		}
		intervals.removeAll(toRemove);
		adaptIntervalOffsetsFrom(findIntervalIndexAfter(offset), -length);

	}

	private void checkLabeled(ILabeledElement element, Interval interval)
			throws RodinDBException {
		if (element.hasLabel()) {
			String text = element.getLabel();
			synchronizeInterval(interval, text);
		}
	}

	private void checkIdentifier(IIdentifierElement element, Interval interval)
			throws RodinDBException {
		if (element.hasIdentifierString()) {
			String text = element.getIdentifierString();
			synchronizeInterval(interval, text);
		}
	}

	private void checkAssignment(IAssignmentElement element, Interval interval)
			throws RodinDBException {
		if (element.hasAssignmentString()) {
			String text = element.getAssignmentString();
			synchronizeInterval(interval, text);
		}
	}

	private void checkPredicate(IPredicateElement element, Interval interval)
			throws RodinDBException {
		if (element.hasPredicateString()) {
			String text = element.getPredicateString();
			synchronizeInterval(interval, text);
		}
	}

	private void checkCommented(ICommentedElement element, Interval interval)
			throws RodinDBException {
		if (element.hasComment()) {
			String text = element.getComment();
			synchronizeInterval(interval, text);
		}
	}

	public IDocument getDocument() {
		return document;
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}

	/**
	 * Adapts the offset of the intervals starting from a given index in the
	 * list.
	 * 
	 * @param index
	 *            The first interval to adapt
	 * @param delta
	 *            The delta of change to the offset.
	 */
	public void adaptIntervalOffsetsFrom(int index, int delta) {
		if (index > 0 && delta != 0) {
			for (int i = index; i < intervals.size(); i++) {
				Interval interval = intervals.get(i);
				interval.setOffset(interval.getOffset() + delta);
			}
		}
	}

	/**
	 * Adapts the folding positions starting from a given offset to a delta of
	 * change in position.
	 * 
	 * @param offset
	 *            The offset where the change happened
	 * @param delta
	 *            The delta of the change
	 */
	public void adaptFoldingPositions(int offset, int delta) {
		ArrayList<EditorItem> elements = new ArrayList<EditorItem>(
				editorElements.values());
		elements.addAll(sections.values());
		for (EditorItem el : elements) {
			Position pos = el.getFoldingPosition();
			if (pos != null) {
				// change happened inside position
				if (pos.getOffset() <= offset
						&& pos.getOffset() + pos.getLength() >= offset) {
					pos.setLength(pos.getLength() + delta);
					// change happened ahead of position
				} else if (pos.getOffset() > offset) {
					pos.setOffset(pos.getOffset() + delta);
				}
			}
		}
	}

	/**
	 * Gets the text that is bound by the given interval from the underlying
	 * document.
	 * 
	 * @param interval
	 * @return The text to be found in the document within the interval bounds,
	 *         or <code>null</code> if the bounds of the interval do not conform
	 *         with the document.
	 */
	protected String getTextFromDocument(Interval interval) {
		if (document != null) {
			try {
				return document.get(interval.getOffset(), interval.getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Synchronizes a given interval with its representation in the text. I.e.
	 * the text area in the document represented by the document is replaced by
	 * the new_text and the interval length is adapted accordingly. The folding
	 * positions are adapted to the changes and the interval offsets of the
	 * following intervals, too.
	 * 
	 * @param interval
	 *            The interval where the change happened
	 * @param new_Text
	 *            The new text to be set into that interval
	 */
	protected void synchronizeInterval(Interval interval, String new_Text) {
		String old_text = getTextFromDocument(interval);
		if (!old_text.equals(new_Text)) {
			adaptIntervalOffsetsFrom(intervals.indexOf(interval) + 1,
					new_Text.length() - old_text.length());
			adaptFoldingPositions(interval.getOffset(), new_Text.length()
					- old_text.length());
			documentProvider.replaceTextInDocument(interval, new_Text);
			interval.setLength(new_Text.length());
		}
	}

	public void setDocumentProvider(RodinDocumentProvider documentProvider) {
		this.documentProvider = documentProvider;
	}

	public EditorItem findEditorElement(int offset, int length) {
		for (EditorItem element : editorElements.values()) {
			if (element.getOffset() == offset && element.getLength() == length) {
				return element;
			}
		}
		return null;
	}

}
