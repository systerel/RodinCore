/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prooftreeui;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeChangedListener;
import org.eventb.core.seqprover.IProofTreeDelta;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * @author htson
 *         <p>
 *         This is the Content Provider class for Proof Tree UI TreeViewer.
 */
public class ProofTreeUIContentProvider implements ITreeContentProvider,
		IProofTreeChangedListener {

	ProofTreeUIPage page;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param page
	 *            the associated Proof Tree UI page.
	 */
	public ProofTreeUIContentProvider(ProofTreeUIPage page) {
		this.page = page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// Do nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (oldInput != null && oldInput instanceof IProofTree) {
			((IProofTree) oldInput).removeChangeListener(this);
		}
		if (newInput != null && newInput instanceof IProofTree) {
			((IProofTree) newInput).addChangeListener(this);
		}
		page.setInvisibleRoot(null);
		page.setRoot(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		IProofTree invisibleRoot = page.getInvisibleRoot();
		IProofTreeNode root = page.getRoot();
		if (parentElement.equals(invisibleRoot)) {
			if (root == null) {
				root = invisibleRoot.getRoot();
				page.setRoot(root);
			}
			Object[] result = { root };
			return result;
		}
		if (parentElement instanceof IProofTreeNode) {
			IProofTreeNode pt = (IProofTreeNode) parentElement;
			// TODO enquire effect of new contract for pt.getChildren()
			if (pt.hasChildren())
				return getChildrenOfList(pt.getChildNodes());
			else
				return new Object[0];
		}

		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof IProofTreeNode) {
			return ((IProofTreeNode) element).getParent();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return (getChildren(element).length != 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IProofTree) {
			if (page.getInvisibleRoot() == null)
				page.setInvisibleRoot((IProofTree) inputElement);
			return getChildren(page.getInvisibleRoot());
		}
		return getChildren(inputElement);
	}

	/**
	 * Get the children of list of parents.
	 * <p>
	 * 
	 * @param parents
	 *            the list of Proof Tree Nodes
	 * @return the array of children of the set of parents
	 */
	private Object[] getChildrenOfList(IProofTreeNode[] parents) {
		// TODO Should do it more efficiently using different data structure
		ArrayList<Object> children = new ArrayList<Object>();
		Object[] filters = page.getFilters();
		for (int i = 0; i < parents.length; i++) {
			IProofTreeNode pt = parents[i];
			if (!pt.isOpen()) {
				int j;
				for (j = 0; j < filters.length; j++) {
					if (filters[j] instanceof ViewerFilter) { 
						ViewerFilter filter = (ViewerFilter) filters[j];
						if (!filter.select(page.getViewer(), pt.getParent(), pt)) {
							Object[] list = getChildrenOfList(pt.getChildNodes());
							for (int k = 0; k < list.length; k++)
								children.add(list[k]);
							break;
						}
					}
				}
				if (j == filters.length)
					children.add(pt);
			} else
				children.add(pt);
		}
		return children.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.prover.IProofTreeChangedListener#proofTreeChanged(org.eventb.core.prover.IProofTreeDelta)
	 */
	public void proofTreeChanged(IProofTreeDelta delta) {
		// TODO Auto-generated method stub
//		ProofTreeUI.debug("Proof Tree Changed");
	}

}
