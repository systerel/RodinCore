/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.IEventBSharedImages;

/**
 * @author htson
 *         <p>
 *         This is an implementation of the Proof Tree UI View.
 */
public class ProofTreeUI extends PageBookView implements ISelectionProvider,
		ISelectionChangedListener {

	// A default text when not available (depends on the current editor).
	private String defaultText = "A proof tree is not available";

	public static Object buffer;

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
			initPage(page);
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
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		getSelectionProvider().addSelectionChangedListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		// get the selection from the selection provider
		return getSelectionProvider().getSelection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		getSelectionProvider().removeSelectionChangedListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		getSelectionProvider().setSelection(selection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		getSelectionProvider().selectionChanged(event);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		createActions();
		createMenu();
		createToolbar();
		hookGlobalActions();

		restoreState();
	}

	private void restoreState() {
		if (memento == null)
			return;
		memento = memento.getChild("showGoal");
		if (memento != null) { // Only if there is a saved state before
			showGoal = memento.getID().equalsIgnoreCase("true");
			showGoalAction.setChecked(showGoal);
			memento = null;
		}
	}

	@Override
	public void saveState(IMemento mem) {
		mem = mem.createChild("showGoal", showGoal ? "true" : "false");
	}

	private IMemento memento;

	@Override
	public void init(IViewSite site, IMemento mem) throws PartInitException {
		init(site);
		this.memento = mem;
	}

	private IAction showGoalAction;

	static boolean showGoal;

	private void createActions() {
		showGoalAction = new Action("Show Goal", Action.AS_CHECK_BOX) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				showGoal = this.isChecked();
				// Update all the pages
				IPage currentPage = ProofTreeUI.this.getCurrentPage();
				((ProofTreeUIPage) currentPage).refresh();
			}
		};
		showGoalAction.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_SHOW_GOAL_PATH));
	}

	/**
	 * Create menu.
	 */
	private void createMenu() {
		// This is a temporary code
		// IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
		// mgr.add(someAction);
	}

	/**
	 * Create toolbar.
	 */
	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(showGoalAction);
	}

	private void hookGlobalActions() {
		// This is a temporary code
		// IActionBars bars = getViewSite().getActionBars();
		// bars.setGlobalActionHandler(IWorkbenchActionConstants.SELECT_ALL,
		// selectAllAction);
		// bars.setGlobalActionHandler(IWorkbenchActionConstants.DELETE,
		// deleteItemAction);
		// viewer.getControl().addKeyListener(new KeyAdapter() {
		// public void keyPressed(KeyEvent event) {
		// if (event.character == SWT.DEL &&
		// event.stateMask == 0 &&
		// deleteItemAction.isEnabled())
		// {
		// deleteItemAction.run();
		// }
		// }
		// });
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		super.partActivated(part);
		IPage currentPage = this.getCurrentPage();
		if (currentPage instanceof ProofTreeUIPage) {
			((ProofTreeUIPage) currentPage).refresh();
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		partActivated(part);
	}
	
	

}