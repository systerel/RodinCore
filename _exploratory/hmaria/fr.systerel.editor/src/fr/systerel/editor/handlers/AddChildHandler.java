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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.documentModel.RodinDocumentProvider;
import fr.systerel.editor.editors.RodinEditor;
import fr.systerel.editor.handlers.context.ChildCreationInfo;

public class AddChildHandler extends AbstractEditorHandler {

	@Override
	protected void handleSelection(RodinEditor editor, int offset) {
		final ChildCreationInfo possibility = editor.getDocumentMapper()
		.getChildCreationPossibility(offset);
		if (possibility != null)
			showTipMenu(editor, offset, possibility,
					(StyledText) editor.getTextComposite());
	}

	//TODO make this menu dynamic
	private void showTipMenu(final RodinEditor editor,final int offset,
			final ChildCreationInfo childInfo, StyledText parent) {
		final Menu tipMenu = new Menu(parent);
		for (final IInternalElementType<?> type : childInfo
				.getPossibleChildTypes()) {
			final MenuItem item = new MenuItem(tipMenu, SWT.PUSH);
			item.setText(type.getName());
			item.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent se) {
					widgetSelected(se);
				}

				@Override
				public void widgetSelected(SelectionEvent se) {
					final ILElement nextSibling = childInfo.getNextSibling();
					final ILElement childParent = childInfo.getParent();
					final IInternalElement localNextSibling = (nextSibling == null || nextSibling
							.getElementType() != type) ? null : nextSibling
							.getElement();
					try {
						final IInternalElement rootElement = (childParent == null) ? editor
								.getDocumentMapper().getRoot().getElement()
								: childParent.getRoot().getElement();
						final IInternalElement localParent;
						if (childParent.getElement().equals(rootElement)) {
							localParent = rootElement;
						} else {
							localParent = childParent.getElement();
						}
						ElementDescRegistry.getInstance().createElement(
								rootElement, localParent, type,
								localNextSibling);
						((RodinDocumentProvider) editor.getDocumentProvider())
								.doSynchronize(rootElement, null);
						editor.selectAndReveal(offset, 0);
					} catch (RodinDBException e1) {
						e1.printStackTrace();
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			});
		}
		final Point loc = parent.getLocationAtOffset(offset);
		final Point mapped = parent.getDisplay().map(parent, null, loc);
		tipMenu.setLocation(mapped);
		tipMenu.setVisible(true);
	}

}
