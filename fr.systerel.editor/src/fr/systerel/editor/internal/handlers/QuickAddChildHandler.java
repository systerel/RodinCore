/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
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
 * This is the short version of the AddChild menu that is displayed via the
 * keyboard shortcut.
 * 
 * @author "Thomas Muller"
 */
public class QuickAddChildHandler extends AbstractAddChildHandler {

	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		final ChildCreationInfo possibility = editor.getDocumentMapper()
				.getChildCreationPossibility(offset);
		if (possibility != null) {
			showTipMenu(editor, offset, possibility, editor.getStyledText());
			return null;
		}
		return "No possible Child Creation";
	}

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
			createChildAndRefresh(editor, childInfo, a[0]);
			return;
		}
		for (final IInternalElementType<?> type : pChildTypes) {
			final MenuItem item = new MenuItem(tipMenu, SWT.PUSH);
			item.setText(type.getName());
			item.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent se) {
					widgetSelected(se);
				}

				@Override
				public void widgetSelected(SelectionEvent se) {
					createChildAndRefresh(editor, childInfo, type);
				}

			});
		}
		final Point loc = parent.getLocationAtOffset(offset);
		final Point mapped = parent.getDisplay().map(parent, null, loc);
		tipMenu.setLocation(mapped);
		tipMenu.setVisible(true);
	}

}