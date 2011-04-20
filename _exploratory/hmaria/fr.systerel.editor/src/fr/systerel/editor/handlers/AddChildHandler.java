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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.documentModel.RodinDocumentProvider;
import fr.systerel.editor.editors.RodinEditor;
import fr.systerel.editor.handlers.context.ChildCreationInfo;

public class AddChildHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IEditorPart activeEditor = EditorPlugin.getActivePage()
				.getActiveEditor();
		if (!(activeEditor instanceof RodinEditor)) {
			return null;
		}
		final RodinEditor rEditor = (RodinEditor) activeEditor;
		final ISelection currentSelection = HandlerUtil
				.getActiveMenuSelection(event);
		if (currentSelection instanceof TextSelection) {
			final int offset = ((TextSelection) currentSelection).getOffset();
			final ChildCreationInfo possibility = rEditor.getDocumentMapper()
					.getChildCreationPossibility(offset);
			if (possibility != null)
				showTipMenu(rEditor, offset, possibility,
						(StyledText) rEditor.getTextComposite());
		}
		return null;
	}

	public int getOffsetFromSelection(ISelection selection) {
		if (selection instanceof StructuredSelection) {

		}
		return -1;
	}

	private void showTipMenu(final RodinEditor editor, int offset,
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
