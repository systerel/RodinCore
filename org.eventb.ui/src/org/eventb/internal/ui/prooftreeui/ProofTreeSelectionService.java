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
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.core.runtime.ListenerList;
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
 * {@link IProofTreeSelectionListener} and call
 * {@link #addListener(IProofTreeSelectionListener)}.
 * </p>
 * 
 * @author beauger
 * @see IProofTreeSelectionListener
 */
public class ProofTreeSelectionService implements ISelectionListener {

	private ProofTreeSelectionService() {
		// singleton
	}

	private static final ProofTreeSelectionService INSTANCE = new ProofTreeSelectionService();

	public static ProofTreeSelectionService getInstance() {
		return INSTANCE;
	}
	
	static ISelectionService getSelectionService() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
	}

	private static void init() {
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

	private static void uninit() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				getSelectionService().removeSelectionListener(getInstance());
			}
		});
	}

	private final ListenerList listeners = new ListenerList(ListenerList.IDENTITY);
	private IProofTreeNode currentNode;

	public IProofTreeNode getCurrentNode() {
		return currentNode;
	}

	public synchronized void addListener(IProofTreeSelectionListener listener) {
		if (listeners.isEmpty()) {
			init();
		}
		listeners.add(listener);
	}

	public synchronized void removeListener(IProofTreeSelectionListener listener) {
		listeners.remove(listener);
		if (listeners.isEmpty()) {
			uninit();
		}
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = ((IStructuredSelection) selection);
			final Object element = ssel.getFirstElement();
			if (currentNode == element) {
				return;
			}
			if (element instanceof IProofTreeNode) {
				currentNode = (IProofTreeNode) element;
				fireChange();
			}
		}
	}

	private void fireChange() {
		// following code recommendation from ListenerList
		final Object[] listenerArray = listeners.getListeners();
		for (int i = 0; i < listenerArray.length; i++) {
			((IProofTreeSelectionListener) listenerArray[i]).nodeChanged(currentNode);
		}
	}

}
