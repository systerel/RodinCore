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
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;

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

	private final ISelectionChangedListener listener = 
		new ISelectionChangedListener() {

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
	 * @param parent the parent Composite.
	 */
	public PrfSklMasterPart(Composite parent) {
		this.viewer = new TreeViewer(parent);
		viewer.getControl().setFont(EVENTB_FONT);
		viewer.setContentProvider(new PrfSklContentProvider());
		viewer.setLabelProvider(new PrfSklLabelProvider());
		viewer.addSelectionChangedListener(listener);
	}

	public boolean setFormInput(Object input) {
		if (input == null) {
			return false;
		}
		
		if (input instanceof IProofTree) {
			if (viewer != null) {
				viewer.setInput(input);
				viewer.getTree().setSelection(viewer.getTree().getItem(0));
				listener.selectionChanged(new SelectionChangedEvent(viewer,
						viewer.getSelection()));
			}
		} else if (input instanceof IPrfSklInput) {
			viewer.setInput(input);
		} else {
			return false;
		}
		return true;
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
		// TODO check if useful
		viewer.removeSelectionChangedListener(listener);
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
