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

package org.eventb.ui.internal.editors;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IAxiom;
import org.eventb.core.IContext;
import org.eventb.ui.Utils.ElementLabelProvider;
import org.eventb.ui.editors.EventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Table part with buttons
 * for displaying axioms (used as master section in Master-Detail block).
 */
public class AxiomMasterSection
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
					// Return the set of axioms belong to this context
					return ((IContext) parent).getAxioms();
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
	public AxiomMasterSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBMasterDetailsBlock block) {
		super(managedForm, parent, toolkit, style, block);
		try {
			counter = ((IContext) rodinFile).getAxioms().length;
		}
		catch (RodinDBException e) {
			// TODO Exception handle
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Handle the adding (new Axiom) action
	 */
	protected void handleAdd() {
		try {
			IInternalElement axiom = rodinFile.createInternalElement(IAxiom.ELEMENT_TYPE, "axiom"+ counter++, null, null);
			this.getViewer().setInput(rodinFile);
			this.getViewer().setSelection(new StructuredSelection(axiom));
			((EventBFormPage) block.getPage()).notifyChangeListeners();
			this.markDirty();
			updateButtons();
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
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
