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

package org.eventb.internal.ui.obligationexplorer;

import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.basis.PSStatus;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.proofcontrol.ProofControl;
import org.eventb.internal.ui.prooftreeui.ProofTreeUI;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.ElementSorter;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         The view shows data obtained from the model. The view gets a list of
 *         models and obligations from the RodinDB. The view is connected to the
 *         model using a content provider.
 */
public class ObligationExplorer extends ViewPart implements
		ISelectionChangedListener, IUserSupportManagerChangedListener {

	// Action when double clicking.
	Action doubleClickAction;

	/**
	 * The plug-in identifier of the Obligation Explorer (value
	 * <code>"org.eventb.ui.views.ObligationExplorer"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.ObligationExplorer";

	// The tree viewer to display the structure of projects, components, etc.
	TreeViewer fViewer;

	// Group of action that is used.
	ObligationExplorerActionGroup groupActionSet;

	ToolItem exclude;

	ToolItem discharge;

	Text filterText;

	ProofStatusToolTip handler;
	
	// Proof states
	static int UNKNOWN = -1;
	static int DISCHARGED = 0;
	static int PENDING_BROKEN = 2;
	static int PENDING = 3;
	static int REVIEWED_BROKEN = 4;
	static int REVIEWED = 5;
	static int DISCHARGED_BROKEN = 6;

	/**
	 * Implements filtering based on proof obligation names.
	 */
	class ObligationTextFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {

			if (element instanceof PSStatus) {
				final String filterString = filterText.getText();
				final boolean excluding = exclude.getSelection();
				if (filterString.length() == 0) {
					// This filter always match the PO name
					return !excluding;
				}
				final PSStatus sequent = (PSStatus) element;
				if (sequent.getElementName().contains(filterString))
					return !excluding;
				else
					return excluding;
			}
			return true;
		}

	}

	/**
	 * Implements filtering of discharged proof obligations.
	 */
	class DischargedFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			
			if (element instanceof IPSStatus) {
				if (! discharge.getSelection()) {
					// No filtering on discharged POs
					return true;
				}

				try {
					return getStatus((IPSStatus) element) != DISCHARGED;
				} catch (RodinDBException e) {
					// Ignore case where database is not up to date
				}
			}
			return true;
		}

	}

	int getStatus(IPSStatus status) throws RodinDBException {
		// Try to synchronize with the proof tree in memory
		Collection<IUserSupport> userSupports = EventBPlugin.getDefault().getUserSupportManager()
				.getUserSupports();
		final boolean proofBroken = status.isBroken();
		for (IUserSupport userSupport : userSupports) {
			IProofState [] proofStates = userSupport.getPOs();
			for (IProofState proofState : proofStates) {
				if (proofState.getPSStatus().equals(status)) {
					IProofTree tree = proofState.getProofTree();
					if (!proofState.isDirty() || tree == null)
						break;

					int confidence = tree.getConfidence();

					if (confidence == IConfidence.PENDING) {
						if (false && proofBroken)
							return PENDING_BROKEN;
						else
							return PENDING;
					}
					if (confidence <= IConfidence.REVIEWED_MAX) {
						if (false && proofBroken)
							return REVIEWED_BROKEN;
						else
							return REVIEWED;
					}
					if (confidence <= IConfidence.DISCHARGED_MAX) {
						if (false && proofBroken)
							return DISCHARGED_BROKEN;
						else
							return DISCHARGED;
					}
					return UNKNOWN; // Should not happen
				}
			}
		}

		// Otherwise, setting the label accordingly.

		final IPRProof prProof = status.getProof();

		// TODO : confidence now expresses unattempted as well
		if ((!prProof.exists()) || (prProof.getConfidence() <= IConfidence.UNATTEMPTED))
			return PENDING;

		int confidence = prProof.getConfidence();
		if (proofBroken) {

			if (confidence == IConfidence.PENDING)
				return PENDING_BROKEN;
			if (confidence <= IConfidence.REVIEWED_MAX)
				return REVIEWED_BROKEN;
			if (confidence <= IConfidence.DISCHARGED_MAX)
				return DISCHARGED_BROKEN;

		} else {

			if (confidence == IConfidence.PENDING)
				return PENDING;
			if (confidence <= IConfidence.REVIEWED_MAX)
				return REVIEWED;
			if (confidence <= IConfidence.DISCHARGED_MAX)
				return DISCHARGED;

		}

		return UNKNOWN;
	}

	/**
	 * Get the contained tree viewer
	 * <p>
	 * 
	 * @return a tree viewer
	 */
	public TreeViewer getTreeViewer() {
		return fViewer;
	}

	CoolItem createToolItem(CoolBar coolBar) {
		ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
		discharge = new ToolItem(toolBar, SWT.CHECK);
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		discharge.setImage(registry.get(IEventBSharedImages.IMG_DISCHARGED));
		discharge.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (ObligationExplorerUtils.DEBUG) {
					ObligationExplorerUtils.debug("Event " + e);
					ObligationExplorerUtils.debug("Status "
							+ exclude.getSelection());
				}
				fViewer.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		exclude = new ToolItem(toolBar, SWT.CHECK);
		exclude.setText("ex");
		exclude.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (ObligationExplorerUtils.DEBUG) {
					ObligationExplorerUtils.debug("Event " + e);
					ObligationExplorerUtils.debug("Status "
							+ exclude.getSelection());
				}
				fViewer.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});

		toolBar.pack();
		Point size = toolBar.getSize();
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setControl(toolBar);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return item;
	}

	CoolItem createText(CoolBar coolBar) {
		filterText = new Text(coolBar, SWT.SINGLE | SWT.BORDER);
		new TimerText(filterText, 1000) {

			@Override
			protected void response() {
				fViewer.refresh();
			}

		};
		filterText.pack();
		Point size = filterText.getSize();
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setControl(filterText);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return item;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * <p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		FormLayout layout = new FormLayout();
		parent.setLayout(layout);
		CoolBar coolBar = new CoolBar(parent, SWT.FLAT);
		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		coolBar.setLayoutData(coolData);

		createToolItem(coolBar);
		createText(coolBar);

		fViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		fViewer.setContentProvider(new ObligationExplorerContentProvider(
				fViewer));
		fViewer.setLabelProvider(new ObligationExplorerLabelProvider(fViewer));
		fViewer.setSorter(new ElementSorter());
		fViewer.addFilter(new ObligationTextFilter());
		fViewer.addFilter(new DischargedFilter());
		FormData textData = new FormData();
		textData.left = new FormAttachment(0);
		textData.right = new FormAttachment(100);
		textData.top = new FormAttachment(coolBar);
		textData.bottom = new FormAttachment(100);
		fViewer.getControl().setLayoutData(textData);
		fViewer.setInput(EventBUIPlugin.getRodinDatabase());

		// Sync with the current active ProverUI
		IWorkbenchPage activePage = EventBUIPlugin.getActivePage();
		if (activePage != null) {
			IEditorPart editor = activePage.getActiveEditor();
			if (editor instanceof ProverUI) {
				IPSStatus prSequent = ((ProverUI) editor)
						.getCurrentProverSequent();
				if (prSequent != null) {
					fViewer.setSelection(new StructuredSelection(prSequent));
					fViewer.reveal(prSequent);
				} else {
					IRodinFile prFile = ((ProverUI) editor).getRodinInput();
					fViewer.setSelection(new StructuredSelection(prFile));
					fViewer.reveal(prFile);
				}
			}
		}
		fViewer.addSelectionChangedListener(this);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		EventBPlugin.getDefault().getUserSupportManager().addChangeListener(this);
		handler = new ProofStatusToolTip(fViewer.getControl().getShell());
		handler.activateHoverHelp(fViewer.getControl());

	}

	/**
	 * Hook the actions to the context menu.
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				groupActionSet.setContext(new ActionContext(fViewer
						.getSelection()));
				groupActionSet.fillContextMenu(manager);
				groupActionSet.setContext(null);
			}
		});
		Menu menu = menuMgr.createContextMenu(fViewer.getControl());
		fViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, fViewer);
	}

	/**
	 * Put the actions to to the pull down menu and toolbar.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Fill the local pull down menu with actions.
	 * <p>
	 * 
	 * @param manager
	 *            a menu manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		MenuManager newMenu = new MenuManager("&New");
		manager.add(newMenu);
		manager.add(new Separator());
		manager.add(ObligationExplorerActionGroup.refreshAction);
	}

	/**
	 * Fill the toolbar with actions.
	 * <p>
	 * 
	 * @param manager
	 *            a menu manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new Separator());
		ObligationExplorerActionGroup.drillDownAdapter
				.addNavigationActions(manager);
	}

	/**
	 * Creat the actions.
	 */
	private void makeActions() {
		groupActionSet = new ObligationExplorerActionGroup(this);
		// Double click to link with editor
		doubleClickAction = new Action() {
			@Override
			public void run() {
				ISelection selection = fViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IRodinFile) {
					UIUtils.linkToEventBEditor(obj);
				}
				else if (obj instanceof IPSStatus) {
					selectPO((IPSStatus) obj);
				}
			}
		};

	}

	/**
	 * Associate the double click action.
	 */
	private void hookDoubleClickAction() {
		fViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 * <p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		fViewer.getControl().setFocus();
	}

	/**
	 * Refresh the view by refreshing the tree viewer.
	 */
	public void refresh() {
		fViewer.refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		// Do nothing
		// TODO See if will be used. Otherwise, should be removed.
	}

	void selectPO(IPSStatus ps) {
		UIUtils.linkToProverUI(ps);
		UIUtils.activateView(ProofControl.VIEW_ID);
		UIUtils.activateView(ProofTreeUI.VIEW_ID);
	}

	/**
	 * External object need to call this methods to set the selection in the
	 * viewer.
	 * <p>
	 * 
	 * @param obj
	 *            the object will be selected
	 */
	public void externalSetSelection(Object obj) {
//		byExternal = true;
		if (!((IStructuredSelection) fViewer.getSelection()).toList().contains(
				obj)) {
			if (ObligationExplorerUtils.DEBUG)
				ObligationExplorerUtils.debug("Set new Selection");
			fViewer.getControl().setRedraw(false);
			fViewer.setSelection(new StructuredSelection(obj));
			fViewer.getControl().setRedraw(true);
		}
//		byExternal = false;
	}

	public void userSupportManagerChanged(final IUserSupportManagerDelta delta) {

		if (ObligationExplorerUtils.DEBUG)
			ObligationExplorerUtils.debug("Begin User Support Manager Changed");

		final Control control = fViewer.getControl();
		
		// Do nothing if the control is already disposed.
		if (control.isDisposed())
			return;

		Display display = control.getDisplay();

		display.syncExec(new Runnable() {
			public void run() {
		
				// Do nothing if the control is already disposed.
				if (control.isDisposed())
					return;

				IUserSupportDelta[] affectedUserSupports = delta.getAffectedUserSupports();
				for (IUserSupportDelta affectedUserSupport : affectedUserSupports) {
					IPSFile psFile = affectedUserSupport.getUserSupport().getInput();
					if (psFile != null) {
						IMachineFile machineFile = psFile.getMachineFile();
						if (machineFile.exists()) {
							fViewer.refresh(machineFile);
						} else {
							IContextFile contextFile = psFile.getContextFile();
							if (contextFile.exists()) {
								fViewer.refresh(contextFile);
							}
						}
					}
					int kind = affectedUserSupport.getKind();
					if (kind == IUserSupportDelta.CHANGED) {
						int flags = affectedUserSupport.getFlags();
						if ((flags | IUserSupportDelta.F_CURRENT) != 0) {
							IProofState ps = affectedUserSupport.getUserSupport().getCurrentPO();
							if (ps != null) {
								IPSStatus prSequent = ps.getPSStatus();
								externalSetSelection(prSequent);
							} else { // Empty selection
								clearSelection();
							}
						}
								
					}
				}
			}
		});

		if (ObligationExplorerUtils.DEBUG)
			ObligationExplorerUtils.debug("End User Support Manager Changed");
	}

	@Override
	public void dispose() {
		if (fViewer == null)
			return;
		EventBPlugin.getDefault().getUserSupportManager().removeChangeListener(this);
		fViewer.removeSelectionChangedListener(this);
		super.dispose();
	}

	void clearSelection() {
		// TODO LV checks with Son why redraw change, is it for revealing the new selection? 
		fViewer.getControl().setRedraw(false);
		fViewer.setSelection(new StructuredSelection());
		fViewer.getControl().setRedraw(true);
	}

}