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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eventb.internal.ui.EventBSharedColor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ILElement;

/**
 * @author Nicolas Beauger
 *
 */
public class Selections {
	
	public static class SelectionEffect {
		private final StyledText styledText;

		public SelectionEffect(StyledText styledText) {
			this.styledText = styledText;
		}
		
		public void select(Position position) {
			setSelectionBackgroundColor(position);
		}
		
		public void unselect(Position position) {
			resetBackgroundColor(position);
		}
		
		public void badSelection(Position position) {
			setBadSelectionBackgroundColor(position);
		}
		
		private void setSelectionBackgroundColor(Position position) {
			final Color background = getSelectionBackgroundColorPreference();
			setBackgroundColor(position, background);
		}
		
		private void setBadSelectionBackgroundColor(Position position) {
			final Color background = getBadSelectionBackgroundColorPreference();
			setBackgroundColor(position, background);
		}
		
		private void resetBackgroundColor(Position position) {
			setBackgroundColor(position, null);
		}
		
		private void setBackgroundColor(Position position, Color color) {
			final int start = position.getOffset();
			final int length = position.getLength();
			final StyleRange[] styleRanges = styledText.getStyleRanges(start,
					length);
			setBackgroundColor(styleRanges, color);
			styledText.replaceStyleRanges(start, length, styleRanges);
		}

		private static void setBackgroundColor(StyleRange[] styleRanges, Color color) {
			for (StyleRange styleRange : styleRanges) {
				styleRange.background = color;
			}

		}
		
		// TODO make a preference
		private static Color getSelectionBackgroundColorPreference() {
			return EventBSharedColor.getSystemColor(SWT.COLOR_GREEN);
		}
		
		// TODO make a preference
		private static Color getBadSelectionBackgroundColorPreference() {
			return EventBSharedColor.getSystemColor(SWT.COLOR_RED);
		}
	}
	
	private static class SimpleSelection {
		public final ILElement element;
		public final Position position;
		
		public SimpleSelection(ILElement element, Position position) {
			this.element = element;
			this.position = position;
		}
		
		public boolean contains(int offset) {
			return position.includes(offset);
		}
	}

	public static class MultipleSelection {
		private final List<SimpleSelection> selected = new ArrayList<SimpleSelection>();
		private final SelectionEffect effect;
		
		public MultipleSelection(SelectionEffect effect) {
			this.effect = effect;
		}

		public boolean isEmpty() {
			return selected.isEmpty();
		}
		
		public boolean contains(ILElement element) {
			return indexOf(element) >= 0;
		}

		public boolean contains(int offset) {
			return indexOf(offset) >= 0;
		}
		
		public ILElement getSelectionAt(int offset) {
			final int index = indexOf(offset);
			if (index < 0) return null;
			return selected.get(index).element;
		}
		
		private int indexOf(ILElement element) {
			for (int i = 0; i < selected.size(); i++) {
				final SimpleSelection sel = selected.get(i);
				if (sel.element.equals(element)) {
					return i;
				}
			}
			return -1;
		}
		
		private int indexOf(int offset) {
			for (int i = 0; i < selected.size(); i++) {
				final SimpleSelection sel = selected.get(i);
				if (sel.contains(offset)) {
					return i;
				}
			}
			return -1;
		}

		public ILElement[] getElements() {
			final ILElement[] result = new ILElement[selected.size()];
			for (int i = 0; i < selected.size(); i++) {
				final SimpleSelection sel = selected.get(i);
				result[i] = sel.element;
			}
			return result;
		}
		
		public void toggle(ILElement element, Position position) {
			int index = indexOf(element);
			if (index < 0) {
				// a descendant of an already selected element unselects it
				index = indexOf(position.getOffset());
			}
			if (index < 0) {
				add(element, position);
			} else {
				remove(index);
			}
		}

		private void remove(int index) {
			final boolean wasValid = isValidSelection(selected);
			final SimpleSelection removed = selected.remove(index);
			effect.unselect(removed.position);
			if (!wasValid && isValidSelection(selected)) {
				applySelectEffect();
			}
		}

		public void add(ILElement element, Position position) {
			// an ancestor of an already selected element replaces it
			removeContainedIn(position);
			final int index = findInsertionIndex(position);
			selected.add(index, new SimpleSelection(element, position));
			effect.select(position);
			if (!isValidSelection(selected)) {
				applyBadSelectionEffect();
			}
		}
		
		private int findInsertionIndex(Position position) {
			int i = 0;
			for (; i < selected.size(); i++) {
				final SimpleSelection sel = selected.get(i);
				if (sel.position.offset > position.offset) {
					break;
				}
			}
			return i;
		}

		private void removeContainedIn(Position position) {
			// not applying unselect effects because those of the containing
			// position will apply
			final Iterator<SimpleSelection> iter = selected.iterator();
			while(iter.hasNext()) {
				final SimpleSelection sel = iter.next();
				if (position.includes(sel.position.offset)) {
					iter.remove();
				}
			}
		}

		private void applySelectEffect() {
			for (SimpleSelection sel : selected) {
				effect.select(sel.position);
			}
		}
		
		private void applyBadSelectionEffect() {
			for (SimpleSelection sel : selected) {
				effect.badSelection(sel.position);
			}
		}

		private static boolean isValidSelection(List<SimpleSelection> selection) {
			if (selection.isEmpty()) return true;
			final SimpleSelection first = selection.get(0);
			final IInternalElementType<? extends IInternalElement> type = first.element
					.getElementType();
			for (int i = 1; i < selection.size(); i++) {
				final SimpleSelection sel = selection.get(i);
				if (sel.element.getElementType() != type) {
					return false;
				}
			}
			return true;
		}

		public void clear() {
			for (SimpleSelection sel : selected) {
				effect.unselect(sel.position);
			}
			selected.clear();
		}
		
		public void clearNoEffect() {
			selected.clear();
		}
	}

}
