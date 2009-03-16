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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Master part of the MasterDetailsBlock for the proof skeleton viewer.
 * 
 * @author Nicolas Beauger
 * 
 */
public class PrfSklMasterPart implements IFormPart {

	private static final Font EVENTB_FONT = JFaceResources
			.getFont(PreferenceConstants.EVENTB_MATH_FONT);

	private TreeViewer viewer;
	private IManagedForm managedForm;

	// listener to the tree selection
	private final ISelectionChangedListener treeListener = new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {
			fireSelectionChanged(event);
		}

	};

	void fireSelectionChanged(SelectionChangedEvent event) {
		managedForm.fireSelectionChanged(PrfSklMasterPart.this, event
				.getSelection());
	}

	private final IElementChangedListener statusListener = new IElementChangedListener() {

		public void elementChanged(ElementChangedEvent event) {
			reloadIfInputChanged(event.getDelta());
		}

	};

	void reloadIfInputChanged(IRodinElementDelta delta) {
		final Object currentInput = managedForm.getInput();
		if (currentInput instanceof IPSStatus) {
			final IPSStatus status = (IPSStatus) currentInput;
			if (contains(delta, status)) {
				setViewerInput(status);
			}
		}
	}

	private static boolean contains(IRodinElementDelta delta, IPSStatus status) {
		final IRodinElement element = delta.getElement();
		if (element instanceof IEventBRoot && !(element instanceof IPSRoot)) {
			return false;
		}
		if (status.equals(element)) {
			return true;
		} else {
			final IRodinElementDelta[] childred = delta.getAffectedChildren();
			for (IRodinElementDelta child : childred) {
				if (contains(child, status)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent Composite.
	 */
	public PrfSklMasterPart(Composite parent) {
		this.viewer = new TreeViewer(parent);
		viewer.getControl().setFont(EVENTB_FONT);
		viewer.setContentProvider(new PrfSklContentProvider());
		viewer.setLabelProvider(new PrfSklLabelProvider());
		viewer.addSelectionChangedListener(treeListener);
	}

	public boolean setFormInput(Object input) {
		if (input == null) {
			return false;
		}
		if (input instanceof IPSStatus) {
			setViewerInput((IPSStatus) input);
			return true;
		} else  if (input instanceof IViewerInput) {
			setViewerInput((IViewerInput) input);
			return true;
		}
		return false;
	}

	void setViewerInput(IViewerInput input) {
		if (input instanceof ProofTreeInput) {
			RodinCore.addElementChangedListener(statusListener);
		} else {
			RodinCore.removeElementChangedListener(statusListener);
		}
		if (viewer != null) {
			viewer.setInput(input);
			viewer.getTree().setSelection(viewer.getTree().getItem(0));
			treeListener.selectionChanged(new SelectionChangedEvent(viewer,
					viewer.getSelection()));
		}
	}

	private void setViewerInput(IPSStatus status) {
		final IPRProof proof = status.getProof();
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				try {
					final IProofTree prTree = proof.getProofTree(null);
					if (prTree != null) {
						setViewerInput(new ProofTreeInput(prTree));
					} else {
						setViewerInput(new ProofErrorInput(proof,
								"Failed to build the proof tree"));
					}
				} catch (RodinDBException e) {
					setViewerInput(new ProofErrorInput(proof, e
							.getLocalizedMessage()));
				}
			}
		});
	}

	public void setFocus() {
		// Do nothing
	}

	public void refresh() {
		viewer.refresh();
	}

	public boolean isStale() {
		return false;
	}

	public boolean isDirty() {
		return false;
	}

	public void initialize(IManagedForm form) {
		this.managedForm = form;
	}

	public void dispose() {
		RodinCore.removeElementChangedListener(statusListener);
		viewer.removeSelectionChangedListener(treeListener);
		viewer.getTree().dispose();
		viewer.getControl().dispose();
	}

	public void commit(boolean onSave) {
		// Do nothing
	}

	/**
	 * Get the TreeViewer.
	 * 
	 * @return the TreeViewer.
	 */
	public TreeViewer getViewer() {
		return viewer;
	}
}