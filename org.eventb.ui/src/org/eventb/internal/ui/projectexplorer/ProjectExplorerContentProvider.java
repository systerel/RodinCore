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

package org.eventb.internal.ui.projectexplorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBFile;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson This class provide the content for the tree viewer in the
 *         Project Explorer.
 */
public class ProjectExplorerContentProvider implements
		IStructuredContentProvider, ITreeContentProvider,
		IElementChangedListener {

	// The relationship between RodinFile and their TreeNodes.
	private HashMap<IRodinFile, Object[]> elementsMap;

	// The invisible root of the tree viewer.
	private Object invisibleRoot = null;

	// The Project Explorer.
	ProjectExplorer explorer;

	// List of elements need to be refresh (when processing Delta of changes).
	private ArrayList<Object> toRefresh;

	/**
	 * Constructor.
	 * 
	 * @param explorer
	 *            The Project Explorer
	 */
	public ProjectExplorerContentProvider(ProjectExplorer explorer) {
		this.explorer = explorer;
		elementsMap = new HashMap<IRodinFile, Object[]>();
	}

	/**
	 * This response for the delta changes from the Rodin Database
	 * <p>
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		toRefresh = new ArrayList<Object>();
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
			Object parent;
			if (element instanceof IRodinProject) {
				parent = invisibleRoot;
			} else {
				parent = element.getParent();
			}
			toRefresh.add(parent);
			return;
		}

		if (kind == IRodinElementDelta.REMOVED) {
			Object parent;
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
				toRefresh.add(element);
				return;
			}

			if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
				toRefresh.add(element);
				return;
			}
		}

	}

	/**
	 * Refresh the nodes.
	 * <p>
	 * 
	 * @param toRefresh2
	 *            List of node to refresh
	 * @param updateLabels
	 *            <code>true</code> if the label need to be updated as well
	 */
	private void postRefresh(final ArrayList<Object> toRefresh2, final boolean updateLabels) {
		UIUtils.asyncPostRunnable(new Runnable() {
			public void run() {
				TreeViewer viewer = explorer.getTreeViewer();
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					Object[] objects = viewer.getExpandedElements();
					for (Object elem : toRefresh2) {
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
		invisibleRoot = newInput;

		explorer.setRoot(invisibleRoot);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// Do nothing
	}

	/**
	 * Return the list of elements for a particular parent
	 * <p>
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object child) {
		if (child instanceof TreeNode)
			return ((TreeNode<?>) child).getParent();
		if (child instanceof IRodinElement) {
			IRodinElement element = (IRodinElement) child;
			IRodinElement parent = (element).getParent();
			Object[] objects = elementsMap.get(parent);
			if (objects == null)
				return parent;
			for (Object obj : objects) {
				if (obj instanceof TreeNode<?>) {
					TreeNode<?> node = (TreeNode<?>) obj;
					if (node.getType().equals(element.getElementType())) {
						return node;
					}
				}
			}
			return parent;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parent) {
		if (parent instanceof IMachineFile) {
			IMachineFile mch = (IMachineFile) parent;
			if (elementsMap.containsKey(mch)) {
				return elementsMap.get(mch);
			} else {
				ArrayList<TreeNode<?>> list = new ArrayList<TreeNode<?>>();
				list.add(new TreeNode<IVariable>("Variables", mch,
						IVariable.ELEMENT_TYPE));
				list.add(new TreeNode<IInvariant>("Invariants", mch,
						IInvariant.ELEMENT_TYPE));
				list.add(new TreeNode<ITheorem>("Theorems", mch,
						ITheorem.ELEMENT_TYPE));
				list.add(new TreeNode<IEvent>("Events", mch,
						IEvent.ELEMENT_TYPE));
				elementsMap.put(mch, list.toArray());

				return list.toArray();
			}
		}

		if (parent instanceof IContextFile) {
			IContextFile ctx = (IContextFile) parent;

			if (elementsMap.containsKey(ctx)) {
				return elementsMap.get(ctx);
			} else {
				ArrayList<TreeNode<?>> list = new ArrayList<TreeNode<?>>();
				list.add(new TreeNode<ICarrierSet>("Carrier Sets", ctx,
						ICarrierSet.ELEMENT_TYPE));
				list.add(new TreeNode<IConstant>("Constants", ctx,
						IConstant.ELEMENT_TYPE));
				list.add(new TreeNode<IAxiom>("Axioms", ctx,
						IAxiom.ELEMENT_TYPE));
				list.add(new TreeNode<ITheorem>("Theorems", ctx,
						ITheorem.ELEMENT_TYPE));
				elementsMap.put(ctx, list.toArray());

				return list.toArray();
			}
		}

		if (parent instanceof IRodinProject) {
			IRodinProject prj = (IRodinProject) parent;
			try {
				IMachineFile[] machines = prj
						.getChildrenOfType(IMachineFile.ELEMENT_TYPE);
				IContextFile[] contexts = prj
						.getChildrenOfType(IContextFile.ELEMENT_TYPE);

				try{
					return buildList(machines,contexts);

				}
				catch(RodinDBException e){
					// skip
				}

					final IRodinElement[] results = new IRodinElement[machines.length
							+ contexts.length];
					System.arraycopy(machines, 0, results, 0, machines.length);
					System.arraycopy(contexts, 0, results, machines.length,
							contexts.length);
					
					return results;
			} catch (RodinDBException e) {
				// If it is out of date then prompt the user to refresh
				if (!prj.getResource().isSynchronized(IResource.DEPTH_INFINITE)) {
					MessageDialog
							.openWarning(
									EventBUIPlugin.getActiveWorkbenchShell(),
									"Resource out of date",
									"Project "
											+ ((IRodinProject) parent)
													.getElementName()
											+ " is out of date with the file system and will be refresh.");
					ProjectExplorerActionGroup.refreshAction.refreshAll();
				} else { // Otherwise, there are problems, log an error
					// message
					e.printStackTrace();
					UIUtils.log(e, "Cannot read the Rodin project "
							+ prj.getElementName());
					return new Object[0];
				}
			}
		}

		try {
			if (parent instanceof IParent) {
				return ((IParent) parent).getChildren();
			}
		} catch (RodinDBException e) {
			// If the resource is out of date then prompt the user to refresh
			if (((IRodinElement) parent).getCorrespondingResource()
					.isSynchronized(IResource.DEPTH_INFINITE)) {
				MessageDialog
						.openWarning(
								EventBUIPlugin.getActiveWorkbenchShell(),
								"Resource out of date",
								"Element "
										+ ((IParent) parent).toString()
										+ " is out of date with the file system and will be refresh.");
				ProjectExplorerActionGroup.refreshAction.refreshAll();
			} else { // Otherwise, there are problems, log an error
				// message
				e.printStackTrace();
				UIUtils.log(e, "Cannot read the element "
						+ parent);
			}
		}

		if (parent instanceof TreeNode) {
			return ((TreeNode<?>) parent).getChildren();
		}
		return new Object[0];
	}

	private Object[] buildList(IMachineFile[] machines,
			IContextFile[] contexts) throws RodinDBException {
		ArrayList<IMachineFile> toAdd = new ArrayList<IMachineFile>(machines.length);
		for (IMachineFile m : machines){
			toAdd.add(m);
		}	
		ArrayList<IEventBFile> result = new ArrayList<IEventBFile>(machines.length);
		while (!toAdd.isEmpty()){
			IMachineFile m = toAdd.get(0);
			addRecursively(m,toAdd,result);
		}
		for (IContextFile ctx :contexts){
			result.add(ctx);
		}
		return result.toArray();
	}

	private void addRecursively(IMachineFile m, ArrayList<IMachineFile> toAdd, ArrayList<IEventBFile> result) throws RodinDBException {
		toAdd.remove(m);
		for (IRefinesMachine refM : m.getRefinesClauses()){
			IMachineFile abs = refM.getAbstractMachine();
			if (abs.exists() && toAdd.contains(abs)){
				addRecursively(abs, toAdd, result);
			}
		}
		result.add(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object parent) {
		if (parent instanceof IRodinFile)
			return true;
		try {
			if (parent instanceof IParent)
				return ((IParent) parent).hasChildren();
			if (parent instanceof TreeNode)
				return ((TreeNode<?>) parent).hasChildren();
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return false;
	}

}
