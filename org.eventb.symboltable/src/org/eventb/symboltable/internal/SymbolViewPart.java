/*******************************************************************************
 * Copyright (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Heinrich Heine Universitaet Duesseldorf - initial API and implementation
 *     Systerel - used Eclipse Command Framework
 *******************************************************************************/

package org.eventb.symboltable.internal;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;

public class SymbolViewPart extends ViewPart {
	private final IPartListener2 editorListener;
	private IEditorPart editor = null;
	private final SymbolProvider contentProvider;
	private final ClickListener clickListener;
	private IStatusLineManager statusLineManager;

	private IViewProvider viewProvider;

	public SymbolViewPart() {
		editorListener = new PartListener(this);
		contentProvider = new SymbolProvider();
		clickListener = new ClickListener(this);
	}

	@Override
	public void createPartControl(final Composite parent) {
		setTitleToolTip("Symbol table to quickly insert mathematical Event-B symbols into text editor");

		viewProvider = new UncategorizedViewProvider(getSite()
				.getWorkbenchWindow().getWorkbench().getDisplay(),
				contentProvider, clickListener);
		viewProvider.createPartControl(parent);

		registerPartListener();

		final IActionBars bars = getViewSite().getActionBars();
		statusLineManager = bars.getStatusLineManager();
	}

	public void reportError(final String message) {
		statusLineManager.setErrorMessage(message);
	}

	public void clearError() {
		statusLineManager.setErrorMessage(null);
	}

	@Override
	public void setFocus() {
		// let the focus on the editor
	}

	@Override
	public void dispose() {
		unregisterEditorListener();

		if (viewProvider != null) {
			viewProvider.dispose();
		}

		super.dispose();
	}

	public void setEditor(final IEditorPart editor) {
		final boolean changed = this.editor != editor;
		this.editor = editor;

		if (changed) {
			clickListener.setEditor(editor);
			final boolean enabled = editor != null;
			if (viewProvider != null) {
				viewProvider.setEnabled(enabled);
			}
		}
	}

	private void registerPartListener() {
		final IWorkbenchPartSite site = getSite();
		if (site != null) {
			final IWorkbenchPage page = site.getPage();
			if (page != null) {
				page.addPartListener(editorListener);
			}
		}
	}

	private void unregisterEditorListener() {
		final IWorkbenchPartSite site = getSite();
		if (site != null) {
			final IWorkbenchPage page = site.getPage();
			if (page != null) {
				page.removePartListener(editorListener);
			}
		}
	}
}
