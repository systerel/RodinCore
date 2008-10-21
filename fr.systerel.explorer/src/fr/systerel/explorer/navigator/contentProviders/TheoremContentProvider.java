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
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ITheorem;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelContext;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelMachine;
import fr.systerel.explorer.navigator.IElementNode;


/**
 * The content provider for Theorem elements
 * @author Maria Husmann
 *
 */
public class TheoremContentProvider implements ITreeContentProvider {
	public Object[] getChildren(Object element) {
		if (element instanceof IMachineRoot) {
			Object[] results = new Object[1];
			//get the intermediary node for theorems
			results[0] = ModelController.getMachine((IMachineRoot) element).theorem_node;
			return results;
		}
		if (element instanceof IContextRoot) {
			Object[] results = new Object[1];
			//get the intermediary node for theorems
			results[0] = ModelController.getContext((IContextRoot) element).theorem_node;
			return results;
		}
		if (element instanceof IElementNode){
			IElementNode node = (IElementNode) element;
			IInternalElementType<?> type = node.getChildrenType();
			if (type.equals(ITheorem.ELEMENT_TYPE)) {
				try {
					if (node.getParent() instanceof IMachineRoot) {
						IMachineRoot machine = (IMachineRoot) node.getParent();
						return machine.getTheorems();
					}
					if (node.getParent() instanceof IContextRoot) {
						IContextRoot context = (IContextRoot) node.getParent();
						return context.getTheorems();
					}
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return new Object[0];
	}
	public Object getParent(Object element) {
    	if (element instanceof ITheorem) {
    		ITheorem thm =  (ITheorem) element;
    		if ( thm.getRodinFile().getRoot() instanceof IMachineRoot) {
        		IMachineRoot mach = (IMachineRoot) thm.getRodinFile().getRoot();
         		ModelMachine machine = ModelController.getMachine(mach);
         		if (machine != null) {
        			return machine.theorem_node;
         		}
    		}
    		if ( thm.getRodinFile().getRoot() instanceof IContextRoot) {
        		IContextRoot ctxt = (IContextRoot) thm.getRodinFile().getRoot();
         		ModelContext context = ModelController.getContext(ctxt);
         		if (context != null) {
        			return context.theorem_node;
         		}
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
