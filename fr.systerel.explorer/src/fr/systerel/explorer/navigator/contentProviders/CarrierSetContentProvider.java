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
import org.eventb.core.ICarrierSet;
import org.eventb.core.IContextFile;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelContext;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.navigator.IElementNode;

/**
 * The content provider for CarrierSet elements
 * @author Maria Husmann
 *
 */
public class CarrierSetContentProvider implements ITreeContentProvider {
	public Object[] getChildren(Object element) {
		if (element instanceof IContextFile) {
			Object[] results = new Object[1];
			//get the intermediary node for carrier sets
			results[0] = ModelController.getContext((IContextFile) element).nodes[0];
			return results;
		}
		if (element instanceof IElementNode){
			IInternalElementType<?> type = ((IElementNode) element).getChildrenType();
			if (type.equals(ICarrierSet.ELEMENT_TYPE)) {
				IContextFile context = (IContextFile) ((IElementNode) element).getParent();
				try {
					return context.getCarrierSets();
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return new Object[0];
	}
	public Object getParent(Object element) {
    	if (element instanceof ICarrierSet) {
    		ICarrierSet carr =  (ICarrierSet) element;
    		IContextFile ctx = (IContextFile) carr.getRodinFile();
     		ModelContext context = ModelController.getContext(ctx);
     		if (context != null) {
    			return context.nodes[0];
     		}
		}
    	if (element instanceof IElementNode) {
    		return ((IElementNode) element).getParent();
    	}
      return null;
	}

	public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
	public void dispose() {
    	// Do nothing

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    	// Do nothing
		 
	}

}
