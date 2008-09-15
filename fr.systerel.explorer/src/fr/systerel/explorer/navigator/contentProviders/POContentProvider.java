package fr.systerel.explorer.navigator.contentProviders;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;

import fr.systerel.explorer.poModel.Machine;
import fr.systerel.explorer.poModel.PoModelFactory;
import fr.systerel.explorer.poModel.POContainer;

public class POContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
        if (element instanceof IInvariant) {
			return PoModelFactory.getInvariant((IInvariant) element).getIPSStatuses();
        } 
        if (element instanceof IEvent) {
			return PoModelFactory.getEvent((IEvent) element).getIPSStatuses();
        } 
        if (element instanceof ITheorem) {
			return PoModelFactory.getTheorem((ITheorem) element).getIPSStatuses();
        } 
        if (element instanceof POContainer) {
			return ((POContainer)element).getIPSStatuses();
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
