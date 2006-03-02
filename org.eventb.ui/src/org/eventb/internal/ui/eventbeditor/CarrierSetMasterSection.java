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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IContext;
import org.eventb.internal.ui.UIUtils.ElementLabelProvider;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Table part with buttons
 * for displaying carrier sets (used as master section in Master-Detail block).
 */
public class CarrierSetMasterSection
	extends EventBTablePartWithButtons
{

	/**
	 * The content provider class. 
	 */
	class MasterContentProvider
	implements IStructuredContentProvider {
		public Object[] getElements(Object parent) {
			if (parent instanceof IContext)
				try {
					return ((IContext) parent).getCarrierSets();
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
	public CarrierSetMasterSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBMasterDetailsBlock block) {
		super(managedForm, parent, toolkit, style, block);
		try {
			counter = ((IContext) rodinFile).getConstants().length;
		}
		catch (RodinDBException e) {
			// TODO Exception handle
			e.printStackTrace();
		}
	}
	

	/**
	 * Handle the adding (new Carrier Set) action
	 */
	protected void handleAdd() {
		ElementInputDialog dialog = new ElementInputDialog(this.getSection().getShell(), this.getManagedForm().getToolkit(), "New Carrier Sets", "Name of the new carrier set", "set" + (counter + 1));
		dialog.open();
		Collection<String> names = dialog.getNames();
		try {
			for (Iterator<String> it = names.iterator(); it.hasNext();) {
				String name = it.next();
				rodinFile.createInternalElement(ICarrierSet.ELEMENT_TYPE, name, null, null);
				counter++;
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
		this.getViewer().setInput(rodinFile);
		this.markDirty();
		((EventBFormPage) block.getPage()).notifyChangeListeners();
		updateButtons();
	}
	

	/**
	 * Setting the (tree) viewer input of this master section 
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
