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
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;

import fr.systerel.explorer.model.ModelAxiom;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelEvent;
import fr.systerel.explorer.model.ModelInvariant;
import fr.systerel.explorer.model.ModelPOContainer;
import fr.systerel.explorer.model.ModelTheorem;

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
	
	    if (element instanceof IMachineFile) {
	    	Object [] result = new Object[1];
	    	if (ModelController.getMachine((IMachineFile)element) != null) {
		    	result[0] =(ModelController.getMachine((IMachineFile)element));
		    	return result;
	    	}
	    } 

	    if (element instanceof IContextFile) {
	    	Object [] result = new Object[1];
	    	if (ModelController.getContext((IContextFile)element) != null) {
		    	result[0] =(ModelController.getContext((IContextFile)element));
		    	return result;
	    	}
	    } 
	    
	    if (element instanceof ModelPOContainer) {
			return ((ModelPOContainer)element).getIPSStatuses();
	    } 
        return new Object[0];
	}

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
