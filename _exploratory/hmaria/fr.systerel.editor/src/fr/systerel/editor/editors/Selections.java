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
import java.util.List;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eventb.internal.ui.EventBSharedColor;
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
		
		private void setSelectionBackgroundColor(Position position) {
			final Color background = getSelectionBackgroundColorPreference();
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
			for (SimpleSelection sel : selected) {
				if (sel.contains(offset)) {
					return true;
				}
			}
			return false;
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

		public ILElement[] getElements() {
			final ILElement[] result = new ILElement[selected.size()];
			for (int i = 0; i < selected.size(); i++) {
				final SimpleSelection sel = selected.get(i);
				result[i] = sel.element;
			}
			return result;
		}
		
		public void toggle(ILElement element, Position position) {
			final int index = indexOf(element);
			if (index < 0) {
				selected.add(new SimpleSelection(element, position));
				effect.select(position);
			} else {
				// FIXME position should match that of the selection 
				selected.remove(index);
				effect.unselect(position);
			}
		}
		
		public void clear() {
			for (SimpleSelection sel : selected) {
				effect.unselect(sel.position);
			}
			selected.clear();
		}
	}

}
