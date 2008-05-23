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
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.IContextFile;
import org.eventb.core.IEventBFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.projectexplorer.ProjectExplorerActionGroup;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
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

	// The tree viewer of the Obligation Explorer.
	TreeViewer viewer;

	/**
	 * Constructor.
	 * 
	 * @param viewer
	 *            The tree viewer of the Obligation Explorer.
	 */
	public ObligationExplorerContentProvider(TreeViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * This response for the delta changes from the Rodin Database
	 * <p>
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		final ArrayList<Runnable> runnables = new ArrayList<Runnable>();
		IRodinElementDelta delta = event.getDelta();
		if (ObligationExplorerUtils.DEBUG)
			ObligationExplorerUtils.debug(delta.toString());
		processDelta(delta, runnables);
		executeRunnables(runnables);
	}

	void executeRunnables(final Collection<Runnable> runnables) {

		// now post all collected runnables
		Control ctrl= viewer.getControl();
		if (ctrl != null && !ctrl.isDisposed()) {
			//Are we in the UIThread? If so spin it until we are done
			if (ctrl.getDisplay().getThread() == Thread.currentThread()) {
				runUpdates(runnables);
			} else {
				synchronized (this) {
					if (fPendingUpdates == null) {
						fPendingUpdates = runnables;
					} else {
						fPendingUpdates.addAll(runnables);
					}
				}
				ctrl.getDisplay().asyncExec(new Runnable(){
					public void run() {
						runPendingUpdates();
					}
				});
			}
		}
	}
	
	private Collection<Runnable> fPendingUpdates;

	private void runUpdates(Collection<Runnable> runnables) {
		Iterator<Runnable> runnableIterator = runnables.iterator();
		while (runnableIterator.hasNext()){
			runnableIterator.next().run();
		}
	}

	/**
	 * Run all of the runnables that are the widget updates. Must be called in the display thread.
	 */
	public void runPendingUpdates() {
		Collection<Runnable> pendingUpdates;
		synchronized (this) {
			pendingUpdates= fPendingUpdates;
			fPendingUpdates= null;
		}
		if (pendingUpdates != null && viewer != null) {
			Control control = viewer.getControl();
			if (control != null && !control.isDisposed()) {
				runUpdates(pendingUpdates);
			}
		}
	}

	/**
	 * Process the delta recursively and depend on the kind of the delta.
	 * <p>
	 * 
	 * @param delta
	 *            The Delta from the Rodin Database.
	 * @param runnables
	 *            the resulting view changes as runnables ({@link Runnable}.
	 */
	private boolean processDelta(IRodinElementDelta delta,
			Collection<Runnable> runnables) {
		int kind = delta.getKind();
		IRodinElement element = delta.getElement();
		
		if (kind == IRodinElementDelta.ADDED) {
			// Convert from IPSFile to IMachineFile or IContextFile the add to
			// the view.
			if (element instanceof IPSFile) {
				IPSFile psFile = (IPSFile) element;
				// ASSUMPTION: psFile must correspond to either machineFile or
				// contextFile
				IMachineFile machineFile = psFile.getMachineFile();
				if (machineFile.exists())
					element = machineFile;
				else
					element = psFile.getContextFile();
				Object parent = psFile.getRodinProject();
				postAdd(parent, element, runnables);
				return false;
			}
			
			if (element instanceof IPSStatus
					|| element instanceof IRodinProject) {
				Object parent = getParent(element);
				if (parent != null) {
					postAdd(parent, element, runnables);
					if (parent instanceof IRodinFile)
						postUpdateLabel(parent, runnables);
				}
				return false;
			}
			return false;
		}

		if (kind == IRodinElementDelta.REMOVED) {
			if (element instanceof IRodinProject || element instanceof IPSStatus) {
				postRemove(new Object [] {element}, runnables);
				Object parent = getParent(element);
				if (parent != null && parent instanceof IRodinFile) {
					postUpdateLabel(parent, runnables);
				}
			}
			if (element instanceof IPSFile) {
				IPSFile psFile = (IPSFile) element;
				// ASSUMPTION: psFile must correspond to either machineFile or
				// contextFile
				IMachineFile machineFile = psFile.getMachineFile();
				IContextFile contextFile = psFile.getContextFile();
				postRemove(new Object[] { machineFile, contextFile }, runnables);
			}
			return false;
		}

		if (kind == IRodinElementDelta.CHANGED) {

			// The label for IMachineFile or IContextFile might change, refresh
			// the label only.
			if (element instanceof IMachineFile
					|| element instanceof IContextFile) {
				postUpdateLabel(element, runnables);
				return false;
			}

			// Ignore changes to irrelevant elements.
			if (!(element instanceof IRodinDB
					|| element instanceof IRodinProject
					|| element instanceof IPSFile || element instanceof IPSStatus)) {
				return false;
			}
			
			int flags = delta.getFlags();

			if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				IRodinElementDelta[] deltas = delta.getAffectedChildren();
				for (int i = 0; i < deltas.length; i++) {
					if (processDelta(deltas[i], runnables))
						return false;
				}
			}

			if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
				Object parent = this.getParent(element);
				if (parent != null) {
					postRefresh(parent, runnables);
					return true;
				}
			}

			if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
				postRefresh(element, runnables);
				Object parent = this.getParent(element);
				if (parent != null && parent instanceof IRodinFile)
					postUpdateLabel(parent, runnables);
				return false;
			}
		}

		return false; 
	}

	private void postUpdateLabel(final Object element,
			Collection<Runnable> runnables) {
		if (ObligationExplorerUtils.DEBUG)
			ObligationExplorerUtils.debug("Update label: " + element);
		runnables.add(new Runnable() {
			public void run() {
				viewer.update(element, new String[] {"content"});
			}
		});
	}

	private void postRefresh(final Object element,
			Collection<Runnable> runnables) {
		if (ObligationExplorerUtils.DEBUG)
			ObligationExplorerUtils.debug("Refresh: " + element);
		runnables.add(new Runnable() {
			public void run() {
				viewer.refresh(element, true);
			}
		});
	}

	private void postRemove(final Object [] elements,
			Collection<Runnable> runnables) {
		if (ObligationExplorerUtils.DEBUG) {
			for (Object obj : elements)
				ObligationExplorerUtils.debug("Remove: " + obj);
		}
		runnables.add(new Runnable() {
			public void run() {
				viewer.remove(elements);
			}
		});
	}

	private void postAdd(final Object parent,
			final IRodinElement element, Collection<Runnable> runnables) {
		if (ObligationExplorerUtils.DEBUG)
			ObligationExplorerUtils.debug("Add: " + element + " to "
					+ parent);

		runnables.add(new Runnable() {
			public void run() {
				if (viewer.testFindItem(element) == null)
					viewer.add(parent, element);
			}
		});
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
		if (!(child instanceof IRodinElement))
			return null;
		if (child instanceof IRodinProject)
			return invisibleRoot;
		
		IRodinElement element = (IRodinElement) child;
		IRodinElement parent = element.getParent();
		if (parent instanceof IPSFile) {
			IPSFile psFile = (IPSFile) parent;
			IMachineFile machineFile = psFile.getMachineFile();
			if (machineFile.exists()) {
				return machineFile;
			}
			IContextFile contextFile = psFile.getContextFile();
			if (contextFile.exists()) {
				return contextFile;
			}
		}
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parent) {
		if (parent instanceof IRodinProject) {
			IRodinProject prj = (IRodinProject) parent;
			try {
				IRodinElement[] machines = prj
						.getChildrenOfType(IMachineFile.ELEMENT_TYPE);
				IRodinElement[] contexts = prj
						.getChildrenOfType(IContextFile.ELEMENT_TYPE);

				IRodinElement[] results = new IRodinElement[machines.length
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
			if (parent instanceof IEventBFile) {
				IPSFile psFile = ((IEventBFile) parent).getPSFile();
				if (psFile.exists())
					return psFile.getStatuses();
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
