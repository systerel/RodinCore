/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/
package fr.systerel.explorer.navigator.contentProviders;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;

import fr.systerel.explorer.model.ModelAxiom;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelEvent;
import fr.systerel.explorer.model.ModelInvariant;
import fr.systerel.explorer.model.ModelTheorem;
import fr.systerel.explorer.navigator.IElementNode;

/**
 * 
 * The content provider for proof obligations
 *
 */
public class POContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
	    if (element instanceof IInvariant) {
	    	ModelInvariant inv = ModelController.getInvariant((IInvariant) element);
	    	if (inv != null) {
	    		return inv.getIPSStatuses();
	    	}
	    } 
	    if (element instanceof IEvent) {
	    	ModelEvent evt = ModelController.getEvent((IEvent) element);
	    	if (evt != null) {
	    		return evt.getIPSStatuses();
	    	}
	    } 
	    if (element instanceof ITheorem) {
	    	ModelTheorem thm = ModelController.getTheorem((ITheorem) element);
	    	if (thm != null) {
	    		return thm.getIPSStatuses();
	    	}
	    } 
	
	    if (element instanceof IAxiom) {
	    	ModelAxiom axm = ModelController.getAxiom((IAxiom) element);
	    	if (axm != null) {
	    		return axm.getIPSStatuses();
	    	}
	    } 
	
	    if (element instanceof IMachineRoot) {
	    	Object [] result = new Object[1];
	    	if (ModelController.getMachine((IMachineRoot)element) != null) {
		    	result[0] =(ModelController.getMachine((IMachineRoot)element).po_node);
		    	//build the model
		    	(ModelController.getMachine((IMachineRoot)element)).processPORoot();
		    	(ModelController.getMachine((IMachineRoot)element)).processPSRoot();
		    	return result;
	    	}
	    } 

	    if (element instanceof IContextRoot) {
	    	Object [] result = new Object[1];
	    	if (ModelController.getContext((IContextRoot)element) != null) {
		    	result[0] =(ModelController.getContext((IContextRoot)element).po_node);
		    	//build the model
		    	(ModelController.getContext((IContextRoot)element)).processPORoot();
		    	(ModelController.getContext((IContextRoot)element)).processPSRoot();
		    	return result;
	    	}
	    } 
	    
	    if (element instanceof IElementNode) {
	    	IElementNode node = (IElementNode) element;
	    	if (node.getChildrenType().equals(IPSStatus.ELEMENT_TYPE)) {
		    	if (node.getParent() instanceof IMachineRoot) {
		    		return ModelController.getMachine((IMachineRoot)node.getParent()).getIPSStatuses();
		    	}
		    	if (node.getParent() instanceof IContextRoot) {
		    		return ModelController.getContext((IContextRoot)node.getParent()).getIPSStatuses();

		    	}
	    	}
	    } 
        return new Object[0];
	}

	// proof obligations can have multiple parents. return none at all.
	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return (getChildren(element).length > 0);
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		// do nothing

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing

	}

}
