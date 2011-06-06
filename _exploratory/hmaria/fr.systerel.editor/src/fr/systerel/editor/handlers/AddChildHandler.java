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
package fr.systerel.editor.handlers;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.editors.RodinEditor;
import fr.systerel.editor.handlers.context.ChildCreationInfo;
import fr.systerel.editor.operations.AtomicOperation;
import fr.systerel.editor.operations.History;
import fr.systerel.editor.operations.OperationFactory;

public class AddChildHandler extends AbstractEditionHandler {

	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		final ChildCreationInfo possibility = editor.getDocumentMapper()
				.getChildCreationPossibility(offset);
		if (possibility != null) {
			showTipMenu(editor, offset, possibility,
					(StyledText) editor.getTextComposite());
			return null;
		}
		return "No possible Child Creation";
	}

	// TODO make this menu dynamic
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

	private void createChildAndRefresh(final RodinEditor editor,
			final ChildCreationInfo childInfo,
			final IInternalElementType<?> type) {
		final ILElement nextSibling = childInfo.getNextSibling();
		final ILElement childParent = childInfo.getParent();
		final IInternalElement localNextSibling = (nextSibling == null || nextSibling
				.getElementType() != type) ? null : nextSibling.getElement();
		final IInternalElement rootElement = (childParent == null) ? editor
				.getDocumentMapper().getRoot().getElement() : childParent
				.getRoot().getElement();
		final IInternalElement localParent;
		if (childParent.getElement().equals(rootElement)) {
			localParent = rootElement;
		} else {
			localParent = childParent.getElement();
		}
		final AtomicOperation op = OperationFactory.createElementGeneric(
				localParent, type, localNextSibling);
		History.getInstance().addOperation(op);
		editor.resync(null);
	}
	
	@Override
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		final ChildCreationInfo possibility = editor.getDocumentMapper()
				.getChildCreationPossibility(caretOffset);
		if (possibility == null) {
			return false;
		}
		if (possibility.getPossibleChildTypes().isEmpty()) {
			return false;
		}
		return true;
	}

}
