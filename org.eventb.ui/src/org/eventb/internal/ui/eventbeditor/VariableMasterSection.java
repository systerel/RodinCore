/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IMachine;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.UIUtils.ElementLabelProvider;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Table part with buttons
 * for displaying variables (used as master section in Master-Detail block).
 */
public class VariableMasterSection 
	extends EventBTablePartWithButtons
	implements IElementChangedListener
{
	
	/**
	 * The content provider class. 
	 */
	class MasterContentProvider
	implements IStructuredContentProvider {
		public Object[] getElements(Object parent) {
			if (parent instanceof IMachine)
				try {
					return ((IMachine) parent).getVariables();
				}
				catch (RodinDBException e) {
					// TODO Exception handle
					e.printStackTrace();
				}
			return new Object[0];
		}
    	
    	public void dispose() {return;}

    	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    		return;
    	}
    }
	
	
	/**
	 * Contructor.
	 * <p>
	 * @param managedForm The form to create this master section
	 * @param parent The composite parent
	 * @param toolkit The Form Toolkit used to create this master section
	 * @param style The style
	 * @param block The master detail block which this master section belong to
	 */
	public VariableMasterSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBMasterDetailsBlock block) {
		super(managedForm, parent, toolkit, style, block);
		try {
			counter = ((IMachine) rodinFile).getVariables().length;
		}
		catch (RodinDBException e) {
			// TODO Exception handle
			e.printStackTrace();
		}
		((EventBEditor) this.getBlock().getPage().getEditor()).addElementChangedListener(this);
	}
	

	/**
	 * Handle the adding (new Variable) action.
	 */
	protected void handleAdd() {
		UIUtils.newVariables(rodinFile);
	}
	
	/**
	 * Setting the (tree) viewer input of this master section.
	 */
	protected void setViewerInput() {
		// TODO Move this to the super class
		TableViewer viewer = this.getViewer();
		viewer.setContentProvider(new MasterContentProvider());
		viewer.setLabelProvider(new ElementLabelProvider());
		rodinFile = ((EventBEditor) this.getBlock().getPage().getEditor()).getRodinInput();
		viewer.setInput(rodinFile);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		processDelta(delta);
	}
	
	private void processDelta(IRodinElementDelta delta) {
		IRodinElement element= delta.getElement();
		if (element instanceof IRodinFile) {
			IRodinElementDelta [] deltas = delta.getAffectedChildren();
			for (int i = 0; i < deltas.length; i++) {
				processDelta(deltas[i]);
			}

			return;
		}
		if (element instanceof IVariable) {
			UIUtils.postRunnable(new Runnable() {
				public void run() {
					getViewer().setInput(rodinFile);
					markDirty();
					updateButtons();
				}
			}, this.getSection().getClient());
		}
		else {
			return;
		}
	}

}
