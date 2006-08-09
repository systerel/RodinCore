/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.prover.ProverUI;

/**
 * @author htson
 *         <p>
 *         This is an implementation of the Proof Tree UI View.
 */
public class ProofTreeUI extends PageBookView implements ISelectionProvider,
		ISelectionChangedListener {

	/**
	 * The identifier of the Proof Tree UI View (value
	 * <code>"org.eventb.ui.views.ProofTreeUI"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.ProofTreeUI";

	// A default text when not available (depends on the current editor).
	private String defaultText = "A proof tree is not available";

	public static Object buffer;
	
	public static boolean DEBUG = false;
	
	public static void debug(String message) {
		if (DEBUG) System.out.println("***ProofTreeUI*** " + message);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#createDefaultPage(org.eclipse.ui.part.PageBook)
	 */
	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();
		initPage(page);
		page.createControl(book);
		page.setMessage(defaultText);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		// Try to get an ProofTree UI Page list page.
		Object obj = part.getAdapter(IProofTreeUIPage.class);
		if (obj instanceof IProofTreeUIPage) {
			IProofTreeUIPage page = (IProofTreeUIPage) obj;
			if (page instanceof IPageBookViewPage)
				initPage((IPageBookViewPage) page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		// There is no content outline
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#doDestroyPage(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.ui.part.PageBookView.PageRec)
	 */
	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		IProofTreeUIPage page = (IProofTreeUIPage) pageRecord.page;
		page.dispose();
		pageRecord.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#getBootstrapPart()
	 */
	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		if (page != null)
			if (page.getActiveEditor() instanceof ProverUI)
				return page.getActiveEditor();

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#isImportant(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		// We only care about Prover UI editors
		return (part instanceof ProverUI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		getSelectionProvider().addSelectionChangedListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		// get the selection from the selection provider
		return getSelectionProvider().getSelection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		getSelectionProvider().removeSelectionChangedListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		getSelectionProvider().setSelection(selection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		getSelectionProvider().selectionChanged(event);
	}

}