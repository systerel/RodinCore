package fr.systerel.explorer.navigator.contentProviders;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;

import fr.systerel.explorer.poModel.Machine;
import fr.systerel.explorer.poModel.ModelFactory;
import fr.systerel.explorer.poModel.POContainer;

public class POContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
        if (element instanceof IInvariant) {
			return ModelFactory.getInvariant((IInvariant) element).getProofObligations();
        } 
        if (element instanceof IEvent) {
			return ModelFactory.getEvent((IEvent) element).getProofObligations();
        } 
        if (element instanceof ITheorem) {
			return ModelFactory.getTheorem((ITheorem) element).getProofObligations();
        } 
        if (element instanceof POContainer) {
			return ((POContainer)element).getProofObligations();
        } 
        return new Object[0];
	}

	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren(Object element) {
		return (getChildren(element).length > 0);
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
