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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IContext;
import org.eventb.core.IMachine;
import org.eventb.core.ITheorem;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils.ElementLabelProvider;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Table part with buttons
 * for displaying theorems (used as master section in Master-Detail block).
 */
public class TheoremMasterSection 
	extends EventBTablePartWithButtons
{

	/**
	 * The content provider class. 
	 */
	class MasterContentProvider
	implements IStructuredContentProvider {
		public Object[] getElements(Object parent) {
			if (parent instanceof IMachine)
				try {
					return ((IMachine) parent).getTheorems();
				}
				catch (RodinDBException e) {
					// TODO Exception handle
					e.printStackTrace();
				}
				if (parent instanceof IContext)
					try {
						return ((IContext) parent).getTheorems();
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
	public TheoremMasterSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBMasterDetailsBlock block) {
		super(managedForm, parent, toolkit, style, block);
		if (rodinFile instanceof IMachine)
			try {
				counter = ((IMachine) rodinFile).getTheorems().length;
			}
			catch (RodinDBException e) {
				// TODO Exception handle
				e.printStackTrace();
			}
		else if (rodinFile instanceof IContext)
			try {
				counter = ((IContext) rodinFile).getTheorems().length;
			}
			catch (RodinDBException e) {
				// TODO Exception handle
				e.printStackTrace();
			}
	}
	
	
	/**
	 * Handle the adding (new Theorem) action.
	 */
	protected void handleAdd() {
		try {
			IInternalElement theorem = rodinFile.createInternalElement(ITheorem.ELEMENT_TYPE, "thm" + counter++, null, null);
			theorem.setContents(EventBUIPlugin.THM_DEFAULT);
			this.getViewer().setInput(rodinFile);
			this.getViewer().setSelection(new StructuredSelection(theorem));
			this.markDirty();
			((EventBFormPage) block.getPage()).notifyChangeListeners();
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
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

}
