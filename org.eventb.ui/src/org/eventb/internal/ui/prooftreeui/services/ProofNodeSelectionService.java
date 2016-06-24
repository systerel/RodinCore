/*******************************************************************************
 * Copyright (c) 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui.services;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * Service that maintains a current selection of proof tree node.
 * <p>
 * Classes that want to be notified shall implement
 * {@link IProofNodeSelectionListener} and call
 * {@link #addListener(IProofNodeSelectionListener)}.
 * </p>
 * 
 * @author beauger
 * @see IProofNodeSelectionListener
 */
public class ProofNodeSelectionService extends AbstractSelectionService<IProofTreeNode, IProofNodeSelectionListener>
		implements ISelectionListener {

	private ProofNodeSelectionService() {
		// singleton
	}

	private static final ProofNodeSelectionService INSTANCE = new ProofNodeSelectionService();

	public static ProofNodeSelectionService getInstance() {
		return INSTANCE;
	}
	
	static ISelectionService getSelectionService() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
	}

	@Override
	protected void startListening() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				final ISelectionService selectionService = getSelectionService();
				selectionService.addSelectionListener(getInstance());
				final ISelection selection = selectionService.getSelection();
				if (selection != null) {
					getInstance().selectionChanged(null, selection);
				}
			}
		});
	}

	@Override
	protected void stopListening() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				getSelectionService().removeSelectionListener(getInstance());
			}
		});
	}

	@Override
	protected void notifyChange(IProofNodeSelectionListener listener, IProofTreeNode newNode) {
		listener.nodeChanged(newNode);
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = ((IStructuredSelection) selection);
			final Object element = ssel.getFirstElement();
			if (element instanceof IProofTreeNode) {
				currentChanged((IProofTreeNode) element);
			}
		}
	}

}
