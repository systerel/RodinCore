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

package org.eventb.internal.ui.obligationexplorer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class provide the content for the tree viewer in the Obligation
 *         Explorer.
 */
public class ObligationExplorerContentProvider implements
		IStructuredContentProvider, ITreeContentProvider,
		IElementChangedListener {

	// The invisible root of the tree viewer.
	private IRodinElement invisibleRoot = null;

	// The Project Explorer.
	ObligationExplorer explorer;

	// List of elements need to be refresh (when processing Delta of changes).
	private List<IRodinElement> toRefresh;

	/**
	 * Constructor.
	 * 
	 * @param explorer
	 *            The Project Explorer
	 */
	public ObligationExplorerContentProvider(ObligationExplorer explorer) {
		this.explorer = explorer;
	}

	/**
	 * This response for the delta changes from the Rodin Database
	 * <p>
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		toRefresh = new ArrayList<IRodinElement>();
		processDelta(event.getDelta());
		postRefresh(toRefresh, true);
	}

	/**
	 * Process the delta recursively and depend on the kind of the delta.
	 * <p>
	 * 
	 * @param delta
	 *            The Delta from the Rodin Database
	 */
	private void processDelta(IRodinElementDelta delta) {
		int kind = delta.getKind();
		IRodinElement element = delta.getElement();
		if (kind == IRodinElementDelta.ADDED) {
			IRodinElement parent;
			if (element instanceof IRodinProject) {
				parent = invisibleRoot;
			} else {
				parent = element.getParent();
			}
			toRefresh.add(parent);
			return;
		}

		if (kind == IRodinElementDelta.REMOVED) {
			IRodinElement parent;
			if (element instanceof IRodinProject) {
				parent = invisibleRoot;
			} else {
				parent = element.getParent();
			}
			toRefresh.add(parent);
			return;
		}

		if (kind == IRodinElementDelta.CHANGED) {
			int flags = delta.getFlags();

			if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				IRodinElementDelta[] deltas = delta.getAffectedChildren();
				for (int i = 0; i < deltas.length; i++) {
					processDelta(deltas[i]);
				}
				return;
			}

			if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
				toRefresh.add(element.getParent());
				return;
			}

			if ((flags & IRodinElementDelta.F_CONTENT) != 0) {
				if (element instanceof IPRProof) {
					toRefresh.add(element.getParent());
				} else
					toRefresh.add(element);
				return;
			}
		}

	}

	/**
	 * Refresh the nodes.
	 * <p>
	 * 
	 * @param refreshes
	 *            List of node to refresh
	 * @param updateLabels
	 *            <code>true</code> if the label need to be updated as well
	 */
	private void postRefresh(final List<IRodinElement> refreshes, final boolean updateLabels) {
		UIUtils.syncPostRunnable(new Runnable() {
			public void run() {
				TreeViewer viewer = explorer.getTreeViewer();
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					Object[] objects = viewer.getExpandedElements();
					for (IRodinElement elem : refreshes) {
						viewer.refresh(elem, updateLabels);
					}
					viewer.setExpandedElements(objects);
				}
			}
		}, explorer.getTreeViewer().getControl());
	}

	/**
	 * Register/De-register to the Rodin Core when the input is change
	 * <p>
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		if (oldInput == null && newInput != null)
			RodinCore.addElementChangedListener(this);
		else if (oldInput != null && newInput == null)
			RodinCore.removeElementChangedListener(this);
		invisibleRoot = (IRodinElement) newInput;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// Do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object child) {
		if (child instanceof IRodinElement)
			return ((IRodinElement) child).getParent();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parent) {
		if (parent instanceof IRodinProject) {
			IRodinProject prj = (IRodinProject) parent;
			try {
				return prj.getChildrenOfType(IPSFile.ELEMENT_TYPE);
			} catch (RodinDBException e) {
				e.printStackTrace();
				MessageDialog
						.openWarning(
								EventBUIPlugin.getActiveWorkbenchShell(),
								"Resource out of date",
								"Project "
										+ ((IRodinProject) parent)
												.getElementName()
										+ " is out of date with the file system and will be refresh.");
				ObligationExplorerActionGroup.refreshAction.refreshAll();
			}
		}

		try {
			if (parent instanceof IPSFile) {
				IPSFile psFile = (IPSFile) parent;
				if (psFile.exists()) return psFile.getStatuses();
			}
			if (parent instanceof IRodinDB) {
				return ((IRodinDB) parent).getChildren();
			}
		} catch (RodinDBException e) {
			// TODO Handle Exception
			MessageDialog
					.openWarning(
							EventBUIPlugin.getActiveWorkbenchShell(),
							"Resource out of date",
							"Element "
									+ ((IParent) parent).toString()
									+ " is out of date with the file system and will be refresh.");
			ObligationExplorerActionGroup.refreshAction.refreshAll();
			e.printStackTrace();
		}

		return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object parent) {
		if (parent instanceof IParent) {
			try {
				return ((IParent) parent).hasChildren();
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return getChildren(parent).length != 0;
	}

}
