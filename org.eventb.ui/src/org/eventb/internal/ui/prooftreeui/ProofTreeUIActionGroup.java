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

import static org.eclipse.ui.ISharedImages.IMG_OBJ_ELEMENT;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that are used in the Proof Tree UI
 *         View.
 */
public class ProofTreeUIActionGroup extends ActionGroup {

	private static final class FilterAction extends Action {

		private final ProofTreeUIPage proofTreeUI;

		FilterAction(ProofTreeUIPage proofTreeUI) {
			this.proofTreeUI = proofTreeUI;
		}

		@Override
		public void run() {
			final ProofTreeUIFiltersDialog dialog = new ProofTreeUIFiltersDialog(
					null, proofTreeUI);
			dialog.open();
			final Object[] results = dialog.getResult();
			if (results != null) {
				proofTreeUI.setFilters(results);
			}
			proofTreeUI.refresh();
		}
	}

	private static final class NextPrevPOAction extends Action {

		private final ProofTreeUIPage proofTreeUI;

		final boolean nextAction;

		NextPrevPOAction(ProofTreeUIPage proofTreeUI, boolean nextAction) {
			this.proofTreeUI = proofTreeUI;
			this.nextAction = nextAction;
		}

		@Override
		public void run() {
			final IUserSupport us = proofTreeUI.getUserSupport();
			final Shell shell = proofTreeUI.getControl().getShell();
			UIUtils.runWithProgressDialog(shell, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException {
					try {
						if (nextAction) {
							us.nextUndischargedPO(false, monitor);
						} else {
							us.prevUndischargedPO(false, monitor);
						}
					} catch (RodinDBException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		}
	}

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
	public ProofTreeUIActionGroup(ProofTreeUIPage proofTreeUI) {
		drillDownAdapter = new DrillDownAdapter(proofTreeUI.getViewer());

		filterAction = new FilterAction(proofTreeUI);
		filterAction.setText("Filter");
		filterAction.setToolTipText("Filter the rules");
		filterAction.setImageDescriptor(getSharedImageDesc(IMG_OBJ_ELEMENT));

		nextPOAction = new NextPrevPOAction(proofTreeUI, true);
		nextPOAction.setText("Next PO");
		nextPOAction.setToolTipText("Next Proof Obligation");
		nextPOAction.setImageDescriptor(getSharedImageDesc(IMG_TOOL_FORWARD));

		prevPOAction = new NextPrevPOAction(proofTreeUI, false);
		prevPOAction.setText("Previous PO");
		prevPOAction.setToolTipText("Previous Proof Obligation");
		prevPOAction.setImageDescriptor(getSharedImageDesc(IMG_TOOL_BACK));
	}

	private static ImageDescriptor getSharedImageDesc(String symbolicName) {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				symbolicName);
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
			// Other plug-ins can contribute their actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			super.fillContextMenu(menu);
		}
	}

}
