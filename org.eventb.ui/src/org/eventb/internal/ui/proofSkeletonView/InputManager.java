/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.internal.ui.utils.Messages;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author Nicolas Beauger
 * 
 */
public class InputManager implements IPartListener2, ISelectionListener {

	private static abstract class InputMaker<T> {
		protected final InputManager manager;
		protected final T selection;
		
		public InputMaker(InputManager manager, T selection) {
			this.manager = manager;
			this.selection = selection;
		}

		public T getSelection() {
			return selection;
		}
		
		public abstract IViewerInput makeInput();
		
		public abstract void addInputChangedListener();
		
		public abstract void removeInputChangedListener();
	}

	private static class DefaultInputMaker extends InputMaker<Object> {
		
		public DefaultInputMaker() {
			super(null, new Object());
		}

		@Override
		public IViewerInput makeInput() {
			return DefaultInput.getDefault();
		}

		@Override
		public void addInputChangedListener() {
			// nothing to do
		}

		@Override
		public void removeInputChangedListener() {
			// nothing to do
		}
	}
	
	private static class StatusInputMaker extends InputMaker<IPSStatus> {
	
		public StatusInputMaker(InputManager manager, IPSStatus status) {
			super(manager, status);
		}
		
		private final IElementChangedListener statusListener = new IElementChangedListener() {
			public void elementChanged(ElementChangedEvent event) {
				reloadIfInputChanged(event.getDelta());
			}
		};
		
		void reloadIfInputChanged(IRodinElementDelta delta) {
			final IRodinElementDelta statusDelta = getStatusDelta(delta, selection);
			if (statusDelta == null) {
				// not concerned by delta
				return;
			}
			if (statusDelta.getKind() == IRodinElementDelta.REMOVED) {
				// the status has been removed
				manager.setViewInput(new DefaultInputMaker());
			} else {
				// the status has been updated
				manager.setViewInput(this);
			}
		}
		
		// returns the child delta that concerns the given status
		// or null if not found
		private static IRodinElementDelta getStatusDelta(
				IRodinElementDelta delta, IPSStatus status) {
			final IRodinElement element = delta.getElement();
			if (element.isRoot()
					&& element.getElementType() != IPSRoot.ELEMENT_TYPE) {
				return null;
			}
			if (status.equals(element)) {
				return delta;
			} else {
				final IRodinElementDelta[] childred = delta
						.getAffectedChildren();
				for (IRodinElementDelta child : childred) {
					final IRodinElementDelta found = getStatusDelta(child,
							status);
					if (found != null) {
						return found;
					}
				}
				return null;
			}
		}

		@Override
		public IViewerInput makeInput() {
			final IPRProof proof = selection.getProof();
			if (!proof.exists()) {
				return new ProofErrorInput(proof,
						Messages.proofskeleton_proofdoesnotexist);
			}
			try {
				final IProofTree prTree = proof.getProofTree(null);
				if (prTree == null) {
					return new ProofErrorInput(proof,
							Messages.proofskeleton_buildfailed);
				}
				final String tooltip = proof.getElementName();
				return new ProofTreeInput(prTree, tooltip);
			} catch (RodinDBException e) {
				return new ProofErrorInput(proof, e
						.getLocalizedMessage());
			}
		}

		@Override
		public void addInputChangedListener() {
			RodinCore.addElementChangedListener(statusListener);
		}

		@Override
		public void removeInputChangedListener() {
			RodinCore.removeElementChangedListener(statusListener);
		}
	}

	final ProofSkeletonView view;
	private final String partId;
	private final IWorkbenchWindow workbenchWindow;
	private final ISelectionService selectionService;
	volatile InputMaker<?> currentInputMaker;


	public InputManager(ProofSkeletonView view) {
		this.view = view;
		this.partId = view.getSite().getId();
		this.workbenchWindow = view.getSite().getWorkbenchWindow();
		this.selectionService = workbenchWindow.getSelectionService();
	}

	public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
		filterAndProcessNewSelection(sourcepart, selection);
	}

	private void filterAndProcessNewSelection(IWorkbenchPart sourcepart,
			ISelection selection) {

		if (sourcepart == view) {
			return;
		}
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection)
					.getFirstElement();
			if (element == null) {
				return;
			}
			if (element instanceof IPSStatus) {
				final InputMaker<?> newInputMaker = new StatusInputMaker(this,
						(IPSStatus) element);
				if (inputChanged(newInputMaker)) {
					setViewInput(newInputMaker);
				}
			}
		}
	}

	private boolean inputChanged(InputMaker<?> newInputMaker) {
		if (currentInputMaker == null) {
			return true;
		}
		final Object currentSelection = currentInputMaker.getSelection();
		final Object newSelection = newInputMaker.getSelection();
		return !newSelection.equals(currentSelection);
	}
	
	void setViewInput(final InputMaker<?> maker) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				if (currentInputMaker != null) {
					currentInputMaker.removeInputChangedListener();
				}
				final IViewerInput input = maker.makeInput();
				maker.addInputChangedListener();
				currentInputMaker = maker;
				view.setInput(input);
			}
		});
	}

	public void partVisible(IWorkbenchPartReference partRef) {
		if (partRef.getId().equals(partId)) {
			selectionService.addSelectionListener(this);
			fireCurrentSelection();
		}
	}

	public void partHidden(IWorkbenchPartReference partRef) {
		if (partRef.getId().equals(partId)) {
			selectionService.removeSelectionListener(this);
		}
	}

	public void partActivated(IWorkbenchPartReference partRef) {
		// do nothing
	}

	public void partDeactivated(IWorkbenchPartReference partRef) {
		// do nothing
	}

	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		// do nothing
	}

	public void partClosed(IWorkbenchPartReference partRef) {
		if (partRef.getId().equals(partId) && currentInputMaker != null) {
			currentInputMaker.removeInputChangedListener();
		}
	}

	public void partInputChanged(IWorkbenchPartReference partRef) {
		// do nothing
	}

	public void partOpened(IWorkbenchPartReference partRef) {
		// do nothing
	}

	private void fireCurrentSelection() {
		final ISelection selection = selectionService.getSelection();
		if (selection != null) {
			final IWorkbenchPage activePage = workbenchWindow.getActivePage();
			final IWorkbenchPart activePart = activePage == null ? null
					: activePage.getActivePart();
			this.selectionChanged(activePart, selection);
		}
	}

}
