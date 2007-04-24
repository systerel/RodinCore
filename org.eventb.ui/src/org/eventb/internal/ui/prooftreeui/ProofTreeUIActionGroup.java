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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that are used in the Proof Tree UI
 *         View.
 */
public class ProofTreeUIActionGroup extends ActionGroup {

	// The associated Proof Tree UI page.
	final ProofTreeUIPage proofTreeUI;

	// Different actions.
	protected Action prevPOAction;

	protected Action nextPOAction;

	protected Action filterAction;

	protected DrillDownAdapter drillDownAdapter;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param proofTreeUI
	 *            the associated Proof Tree UI page.
	 */
	public ProofTreeUIActionGroup(final ProofTreeUIPage proofTreeUI) {
		this.proofTreeUI = proofTreeUI;
		drillDownAdapter = new DrillDownAdapter(proofTreeUI.getViewer());

		filterAction = new Action() {
			@Override
			public void run() {
				ProofTreeUIFiltersDialog dialog = new ProofTreeUIFiltersDialog(
						null, ProofTreeUIActionGroup.this.proofTreeUI);
				dialog.open();
				Object[] results = dialog.getResult();
				if (results != null) {
					ProofTreeUIActionGroup.this.proofTreeUI.setFilters(results);
				}

				ProofTreeUIActionGroup.this.proofTreeUI.refresh();
			}
		};
		filterAction.setText("Filter");
		filterAction.setToolTipText("Filter the rules");
		filterAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJ_ELEMENT));

		nextPOAction = new Action() {
			@Override
			public void run() {
				UIUtils.runWithProgressDialog(
						ProofTreeUIActionGroup.this.proofTreeUI.getControl()
								.getShell(), new IRunnableWithProgress() {

							public void run(IProgressMonitor monitor)
									throws InvocationTargetException,
									InterruptedException {

								try {
									ProofTreeUIActionGroup.this.proofTreeUI
											.getUserSupport()
											.nextUndischargedPO(false, monitor);
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
				});
			}
		};
		nextPOAction.setText("Next PO");
		nextPOAction.setToolTipText("Next Proof Obligation");
		nextPOAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_TOOL_FORWARD));

		prevPOAction = new Action() {
			@Override
			public void run() {
				UIUtils.runWithProgressDialog(
						ProofTreeUIActionGroup.this.proofTreeUI.getControl()
								.getShell(), new IRunnableWithProgress() {

							public void run(IProgressMonitor monitor)
									throws InvocationTargetException,
									InterruptedException {

								try {
									ProofTreeUIActionGroup.this.proofTreeUI
											.getUserSupport()
											.prevUndischargedPO(false, monitor);
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
				});
			}
		};
		prevPOAction.setText("Previous PO");
		prevPOAction.setToolTipText("Previous Proof Obligation");
		prevPOAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_TOOL_BACK));
	}

	/**
	 * Dynamically fill the context menu (depends on the selection).
	 * <p>
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		ISelection sel = getContext().getSelection();
		if (sel instanceof IStructuredSelection) {
			// Other plug-ins can contribute there actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			super.fillContextMenu(menu);
		}
	}

}
