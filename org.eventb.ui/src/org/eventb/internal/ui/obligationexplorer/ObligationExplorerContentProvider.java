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

package org.eventb.internal.ui.obligationexplorer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.IContext;
import org.eventb.core.IMachine;
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
public class ObligationExplorerContentProvider
	implements	IStructuredContentProvider, 
				ITreeContentProvider,
				IElementChangedListener 
{

	// The invisible root of the tree viewer.
	private IRodinElement invisibleRoot = null;
	
	// The Project Explorer.
	private ObligationExplorer explorer;
	
	// List of elements need to be refresh (when processing Delta of changes).
	private List<IRodinElement> toRefresh;
	

	/**
	 * Constructor. 
	 * @param explorer The Project Explorer
	 */
	public ObligationExplorerContentProvider(ObligationExplorer explorer) {
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
		
		if (child instanceof IRodinElement) return ((IRodinElement) child).getParent();
		return null;
	}
	
	
	/**
	 * Return the list of children for a particular parent.
	 */
	public Object [] getChildren(Object parent) {
		if (parent instanceof IRodinProject) {
			IRodinProject prj = (IRodinProject) parent;
			try {
				// TODO Need to get children of Type PR files instead
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
				ObligationExplorerActionGroup.refreshAction.refreshAll();
			}
		}
		
		
		// TODO Delete
		if (parent instanceof IRodinFile) {
			return ObligationContentExample.getObligations(parent);
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
			ObligationExplorerActionGroup.refreshAction.refreshAll();
			e.printStackTrace();
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
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

}
