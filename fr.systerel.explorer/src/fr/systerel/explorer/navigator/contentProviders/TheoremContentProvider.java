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
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.ITheorem;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelContext;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelElementNode;
import fr.systerel.explorer.model.ModelMachine;


/**
 * The content provider for Theorem elements
 * @author Maria Husmann
 *
 */
public class TheoremContentProvider implements ITreeContentProvider {
	public Object[] getChildren(Object element) {
		if (element instanceof IMachineFile) {
			Object[] results = new Object[1];
			results[0] = ModelController.getMachine((IMachineFile) element).nodes[2];
			return results;
		}
		if (element instanceof IContextFile) {
			Object[] results = new Object[1];
			results[0] = ModelController.getContext((IContextFile) element).nodes[3];
			return results;
		}
		if (element instanceof ModelElementNode){
			IInternalElementType<?> type = ((ModelElementNode) element).getType();
			if (type.equals(ITheorem.ELEMENT_TYPE)) {
				try {
					if (((ModelElementNode) element).getParent() instanceof ModelMachine) {
						ModelMachine machine = (ModelMachine) ((ModelElementNode) element).getParent();
						return machine.getInternalMachine().getTheorems();
					}
					if (((ModelElementNode) element).getParent() instanceof ModelContext) {
						ModelContext machine = (ModelContext) ((ModelElementNode) element).getParent();
						return machine.getInternalContext().getTheorems();
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
    		if ( thm.getRodinFile() instanceof IMachineFile) {
        		IMachineFile mach = (IMachineFile) thm.getRodinFile();
         		ModelMachine machine = ModelController.getMachine(mach);
         		if (machine != null) {
        			return machine.nodes[2];
         		}
    		}
    		if ( thm.getRodinFile() instanceof IContextFile) {
        		IContextFile ctxt = (IContextFile) thm.getRodinFile();
         		ModelContext context = ModelController.getContext(ctxt);
         		if (context != null) {
        			return context.nodes[3];
         		}
    		}
		}
    	if (element instanceof ModelElementNode) {
    		return ((ModelElementNode) element).getParent();
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
