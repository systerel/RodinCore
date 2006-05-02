package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.rodinp.core.IRodinElement;

public class RodinElementSorter extends ViewerSorter {
	
	public int compare(Viewer viewer, Object e1, Object e2) {
        int cat1 = category(e1);
        int cat2 = category(e2);
        return cat1 - cat2;
	}
	
	public int category(Object obj) {
		IRodinElement rodinElement = ((Leaf) obj).getElement();
		if (rodinElement instanceof IVariable) return 1;
		if (rodinElement instanceof IInvariant) return 2;
		if (rodinElement instanceof ITheorem) return 4;
		if (rodinElement instanceof IEvent) return 5;
		if (rodinElement instanceof IGuard) return 2;
		if (rodinElement instanceof IAction) return 3;
		if (rodinElement instanceof ICarrierSet) return 1;
		if (rodinElement instanceof IConstant) return 2;
		if (rodinElement instanceof IAxiom) return 3;
		return 0;
	}
	
}
