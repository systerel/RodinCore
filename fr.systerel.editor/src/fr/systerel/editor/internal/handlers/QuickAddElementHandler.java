/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.rodinp.core.IInternalElementType;

import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.handlers.context.ChildCreationInfo;

/**
 * This abstract class provides the short version of the Add"xxx" menus
 * displayed via the keyboard shortcut.
 * 
 * @author "Thomas Muller"
 */
public abstract class QuickAddElementHandler extends AbstractAddElementHandler {

	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		final ChildCreationInfo possibility = getCreationPossibility(editor,
				offset);
		if (possibility != null
				&& !possibility.getPossibleChildTypes().isEmpty()) {
			showTipMenu(editor, offset, possibility, editor.getStyledText());
			return null;
		}
		return "No possible element creation";
	}

	protected abstract ChildCreationInfo getCreationPossibility(RodinEditor editor, int offset);
	
	
	/**
	 * The quick version of the menu which is displayed after the user pressed
	 * the keyboard shortcut.
	 */
	private void showTipMenu(final RodinEditor editor, final int offset,
			final ChildCreationInfo childInfo, StyledText parent) {
		final Menu tipMenu = new Menu(parent);
		final Set<IInternalElementType<?>> pChildTypes = childInfo
				.getPossibleChildTypes();
		if (pChildTypes.size() == 1) {
			final IInternalElementType<?>[] a = pChildTypes
					.toArray(new IInternalElementType<?>[1]);
			createElementAndRefresh(editor, childInfo, a[0]);
			return;
		}
		for (final IInternalElementType<?> type : pChildTypes) {
			final MenuItem item = new MenuItem(tipMenu, SWT.PUSH);
			item.setText(getLabel(type));
			item.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent se) {
					widgetSelected(se);
				}

				@Override
				public void widgetSelected(SelectionEvent se) {
					createElementAndRefresh(editor, childInfo, type);
				}

			});
		}
		final Point loc = parent.getLocationAtOffset(offset);
		final Point mapped = parent.getDisplay().map(parent, null, loc);
		tipMenu.setLocation(mapped);
		tipMenu.setVisible(true);
	}
	
	private String getLabel(IInternalElementType<?> t) {
		final StringBuilder b = new StringBuilder();
		b.append("Add ");
		b.append(t.getName().replaceFirst("Event-B", ""));
		return b.toString();
	}
	
	public static class QuickAddChildHandler extends QuickAddElementHandler {

		@Override
		protected ChildCreationInfo getCreationPossibility(RodinEditor editor,
				int offset) {
			return editor.getDocumentMapper().getChildCreationPossibility(offset);
		}
		
	}
	
	public static class QuickAddSiblingHandler extends QuickAddElementHandler {

		@Override
		protected ChildCreationInfo getCreationPossibility(RodinEditor editor,
				int offset) {
			return editor.getDocumentMapper().getSiblingCreationPossibility(offset);
		}
		
	}

}