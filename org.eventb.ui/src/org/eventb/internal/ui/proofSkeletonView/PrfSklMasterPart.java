/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     ETH Zurich - adapted to org.rodinp.keyboard
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.rodinp.keyboard.preferences.PreferenceConstants;

/**
 * Master part of the MasterDetailsBlock for the proof skeleton viewer.
 * 
 * @author Nicolas Beauger
 * 
 */
public class PrfSklMasterPart implements IFormPart {

	private static final Font EVENTB_FONT = JFaceResources
			.getFont(PreferenceConstants.RODIN_MATH_FONT);

	private TreeViewer viewer;
	private IManagedForm managedForm;

	// listener to the tree selection
	private final ISelectionChangedListener treeListener = new ISelectionChangedListener() {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			fireSelectionChanged(event);
		}

	};

	void fireSelectionChanged(SelectionChangedEvent event) {
		managedForm.fireSelectionChanged(PrfSklMasterPart.this, event
				.getSelection());
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

	@Override
	public boolean setFormInput(Object input) {
		if (input == null) {
			return false;
		}
		if (input instanceof IViewerInput) {
			setViewerInput((IViewerInput) input);
			return true;
		}
		return false;
	}

	void setViewerInput(IViewerInput input) {
		if (viewer != null) {
			viewer.setInput(input);
			viewer.getTree().setSelection(viewer.getTree().getItem(0));
			treeListener.selectionChanged(new SelectionChangedEvent(viewer,
					viewer.getSelection()));
		}
	}

	@Override
	public void setFocus() {
		// Do nothing
	}

	@Override
	public void refresh() {
		viewer.refresh();
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void initialize(IManagedForm form) {
		this.managedForm = form;
	}

	@Override
	public void dispose() {
		viewer.removeSelectionChangedListener(treeListener);
		viewer.getTree().dispose();
		viewer.getControl().dispose();
	}

	@Override
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