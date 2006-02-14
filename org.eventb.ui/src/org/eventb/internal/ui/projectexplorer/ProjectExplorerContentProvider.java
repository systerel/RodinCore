/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.projectexplorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContext;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachine;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBUIPlugin;
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
 * @author htson
 * This class provide the content for the tree viewer in the Project Explorer.
 */
public class ProjectExplorerContentProvider
	implements	IStructuredContentProvider, 
				ITreeContentProvider,
				IElementChangedListener 
{

	// The relationship between RodinFile and their TreeNodes.  
	private HashMap<IRodinFile, Object []> elementsMap = new HashMap<IRodinFile, Object []>();
	
	// The invisible root of the tree viewer.
	private IRodinElement invisibleRoot = null;
	
	// The Project Explorer.
	private ProjectExplorer explorer;
	
	// List of elements need to be refresh (when processing Delta of changes).
	private List<IRodinElement> toRefresh;
	

	/**
	 * Constructor. 
	 * @param explorer The Project Explorer
	 */
	public ProjectExplorerContentProvider(ProjectExplorer explorer) {
		this.explorer = explorer;
	}
	
	
	
	/**
	 * This response for the delta changes from the Rodin Database
	 * <p>
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		toRefresh = new ArrayList<IRodinElement> ();
		processDelta(event.getDelta());
		postRefresh(toRefresh, true);
	}
	
	
	/*
	 * Process the delta recursively and depend on the kind of the delta.
	 * <p> 
	 * @param delta The Delta from the Rodin Database
	 */
	private void processDelta(IRodinElementDelta delta) {
		int kind= delta.getKind();
		IRodinElement element= delta.getElement();
		if (kind == IRodinElementDelta.ADDED) {
			IRodinElement parent; 
			if (element instanceof IRodinProject) {
				parent = invisibleRoot;
			}
			else {
				parent = element.getParent();
			}
			toRefresh.add(parent);
			return;
		}
		
		if (kind == IRodinElementDelta.REMOVED) {
			IRodinElement parent; 
			if (element instanceof IRodinProject) {
				parent = invisibleRoot;
			}
			else {
				parent = element.getParent();
			}
			toRefresh.add(parent);
			return;
		}
		
		if (kind == IRodinElementDelta.CHANGED) {
			int flags = delta.getFlags();
			
			if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				IRodinElementDelta [] deltas = delta.getAffectedChildren();
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
		}

	}


	/**
	 * Refresh the nodes.
	 * <p>
	 * @param toRefresh List of node to refresh
	 * @param updateLabels <code>true</code> if the label need to be updated as well
	 */
	private void postRefresh(final List toRefresh, final boolean updateLabels) {
		postRunnable(new Runnable() {
			public void run() {
				TreeViewer viewer = explorer.getTreeViewer();
				Control ctrl= viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					Object [] objects = viewer.getExpandedElements();
					for (Iterator iter= toRefresh.iterator(); iter.hasNext();) {
						viewer.refresh(iter.next(), updateLabels);
					}
					viewer.setExpandedElements(objects);
				}
			}
		});
	}
	
	private void postRunnable(final Runnable r) {
		Control ctrl= explorer.getTreeViewer().getControl();
		final Runnable trackedRunnable= new Runnable() {
			public void run() {
				try {
					r.run();
				} finally {
					//removePendingChange();
					//System.out.println("Runned");
				}
			}
		};
		if (ctrl != null && !ctrl.isDisposed()) {
			try {
				ctrl.getDisplay().asyncExec(trackedRunnable); 
			} catch (RuntimeException e) {
				throw e;
			} catch (Error e) {
				throw e; 
			}
		}
	}
	
	
	/**
	 * Register/De-register to the Rodin Core when the input is change 
	 */
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		//System.out.println("Input change, redisplay the tree");
		if (oldInput == null && newInput != null)
			RodinCore.addElementChangedListener(this);
		else if (oldInput != null && newInput == null)
			RodinCore.removeElementChangedListener(this);
		invisibleRoot = (IRodinElement) newInput;
	}
	
	
	/**
	 * The tree is dispose
	 */
	public void dispose() {
		System.out.println("******* DISPOSE ********");
	}
	
	
	/**
	 * Return the list of elements for a particular parent
	 */
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}
	
	
	/**
	 * Return the parent for an element.
	 */
	public Object getParent(Object child) {
		// TODO need to get the right parent for internal elements
		
		if (child instanceof TreeNode) return ((TreeNode) child).getParent();
		if (child instanceof IRodinElement) return ((IRodinElement) child).getParent();
		return null;
	}
	
	
	/**
	 * Return the list of children for a particular parent.
	 */
	public Object [] getChildren(Object parent) {
		if (parent instanceof IMachine) {
			IMachine mch = (IMachine) parent;
			if (elementsMap.containsKey(mch)) {
				//System.out.println("Already explored");
				return (Object []) elementsMap.get(mch);
			}
			else {
				ArrayList<TreeNode> list = new ArrayList<TreeNode>();
				list.add(new TreeNode("Variables", mch, IVariable.ELEMENT_TYPE));
				list.add(new TreeNode("Invariants", mch, IInvariant.ELEMENT_TYPE));
				list.add(new TreeNode("Theorems", mch, ITheorem.ELEMENT_TYPE));
				list.add(new TreeNode("Events", mch, IEvent.ELEMENT_TYPE));
				elementsMap.put(mch, list.toArray());

				return list.toArray();
			}
		}
			
		if (parent instanceof IContext) {
			IContext ctx = (IContext) parent;
			
			if (elementsMap.containsKey(ctx)) {
				// System.out.println("Already explored");
				return (Object []) elementsMap.get(ctx);
			}
			else {
				ArrayList<TreeNode> list = new ArrayList<TreeNode>();
				list.add(new TreeNode("Carrier Sets", ctx, ICarrierSet.ELEMENT_TYPE));
				list.add(new TreeNode("Constants", ctx, IConstant.ELEMENT_TYPE));
				list.add(new TreeNode("Axioms", ctx, IAxiom.ELEMENT_TYPE));
				list.add(new TreeNode("Theorems", ctx, ITheorem.ELEMENT_TYPE));
				elementsMap.put(ctx, list.toArray());

				return list.toArray();
			}
		}
							
		if (parent instanceof IRodinProject) {
			IRodinProject prj = (IRodinProject) parent;
			try {
				IRodinElement [] machines = prj.getChildrenOfType(IMachine.ELEMENT_TYPE);
				IRodinElement [] contexts = prj.getChildrenOfType(IContext.ELEMENT_TYPE);
				
				IRodinElement [] results = new IRodinElement[machines.length + contexts.length];
				System.arraycopy(machines, 0, results, 0, machines.length);
				System.arraycopy(contexts, 0, results, machines.length, contexts.length);

				return results;
			}
			catch (RodinDBException e) {
				e.printStackTrace();
				MessageDialog.openWarning(EventBUIPlugin.getActiveWorkbenchShell(), "Resource out of date", "Project " + ((IRodinProject) parent).getElementName() + " is out of date with the file system and will be refresh.");
				ProjectExplorerActionGroup.refreshAction.refreshAll();
			}
		}
		
		try {
			if (parent instanceof IParent) {
				return ((IParent) parent).getChildren();
			}
		}
		catch (RodinDBException e) {
			// TODO Handle Exception
			MessageDialog.openWarning(EventBUIPlugin.getActiveWorkbenchShell(),
					"Resource out of date", 
					"Element " + ((IParent) parent).toString() + " is out of date with the file system and will be refresh.");
			ProjectExplorerActionGroup.refreshAction.refreshAll();
			e.printStackTrace();
		}

		if (parent instanceof TreeNode) {
			return ((TreeNode) parent).getChildren();
		}
		return new Object[0];
	}

	
	/**
	 * Check if the object has any children.
	 */
	public boolean hasChildren(Object parent) {
		if (parent instanceof IRodinFile) return true;
		try {			
			if (parent instanceof IParent)	return ((IParent) parent).hasChildren();
			if (parent instanceof TreeNode) return ((TreeNode) parent).hasChildren();
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

}
