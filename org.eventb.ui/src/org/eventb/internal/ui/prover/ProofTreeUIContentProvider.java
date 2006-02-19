package org.eventb.internal.ui.prover;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.pm.IProofTreeChangeEvent;
import org.eventb.core.pm.IProofTreeChangedListener;
import org.eventb.core.pm.ProofState;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.rules.ProofTreeNode;
import org.rodinp.core.IRodinElement;

public class ProofTreeUIContentProvider
	implements	ITreeContentProvider,
				IProofTreeChangedListener
{

	ProofTreeUIPage page;
	
	public ProofTreeUIContentProvider(ProofTreeUIPage page) {
		this.page = page;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// Do nothing
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (oldInput != null)
			page.getEditor().getUserSupport().removeProofTreeChangedListener(this);
		if (newInput != null)
			page.getEditor().getUserSupport().addProofTreeChangedListener(this);

		page.setInvisibleRoot(null);
		page.setRoot(null);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofTreeChangedListener#proofTreeChanged(org.eventb.core.pm.IProofTreeChangeEvent)
	 */
	public void proofTreeChanged(IProofTreeChangeEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Tree changed");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		ProofState invisibleRoot = page.getInvisibleRoot();
		IProofTreeNode root = page.getRoot();
		if (parentElement.equals(invisibleRoot)) {
			if (root == null) {
				root = invisibleRoot.getProofTree();
				page.setRoot(root);
			}
			Object [] result = {root};
			return result;
		}
		if (parentElement instanceof ProofTreeNode) {
			IProofTreeNode pt = (IProofTreeNode) parentElement;
			// TODO enquire effect of new contract for pt.getChildren()
			if (pt.hasChildren()) return getChildrenOfList(pt.getChildren());
			else return new Object[0];
		}
		
		return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof IRodinElement) {
			return ((IRodinElement) element).getParent();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if (element.equals(page.getInvisibleRoot())) return true;
		
		if (element instanceof ProofTreeNode) {
			return ((IProofTreeNode) element).hasChildren();
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ProofState) {
			if (page.getInvisibleRoot() == null) page.setInvisibleRoot((ProofState) inputElement);
			return getChildren(page.getInvisibleRoot());
		}
		return getChildren(inputElement);
	}
	
	private Object [] getChildrenOfList(IProofTreeNode [] parents) {
		// TODO Should do it more efficiently using different data structure
		ArrayList<Object> children = new ArrayList<Object>();
		Object [] filters = page.getFilters();
		for (int i = 0; i < parents.length; i++) {
			IProofTreeNode pt = parents[i];
			if (!pt.isOpen()) {
				int j;
				for (j = 0; j < filters.length; j++) {
					if (filters[j].equals(pt.getRule().getName())) {
						// TODO enquire effect of new contract for pt.getChildren()
						Object [] list = getChildrenOfList(pt.getChildren()); 
						for (int k = 0; k < list.length; k++) children.add(list[k]);
						break;
					}
				}
				if (j == filters.length) children.add(pt);
			}
			else children.add(pt);
		}
		return children.toArray();
	}
	
}
