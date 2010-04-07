/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.ExplorerPlugin;
import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelPOContainer;

/**
 *         This class contains some utility static methods that are used in this
 *         Explorer plug-in.
 * @since 1.0
 *
 */
public class ExplorerUtils {

	/**
	 * Retrieves the current explorer selection.
	 * <p>
	 * Must be run in the UI thread.
	 * </p>
	 * 
	 * @author Nicolas Beauger
	 */
	static class SelectionRetriever implements Runnable {
		private static Object[] NO_OBJECT = new Object[0];
		
		private Object[] result = NO_OBJECT;

		public void run() {
			final IWorkbenchWindow activeWindow = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (activeWindow == null) {
				return;
			}
			final IWorkbenchPage activePage = activeWindow.getActivePage();
			if (activePage == null) {
				return;
			}
			final ISelection explSelection = activePage
					.getSelection(ExplorerPlugin.NAVIGATOR_ID);
			if (! (explSelection instanceof IStructuredSelection)) {
				return;
			}
			result = ((IStructuredSelection) explSelection).toArray();
		}
		
		public Object[] getResult() {
			return result;
		}
	}


	public static boolean DEBUG;

	public static final String DEBUG_PREFIX = "*** Event-B Explorer *** ";

	public static IMachineRoot[] getMachineRootChildren(IRodinProject project)
			throws RodinDBException {
		return project.getRootElementsOfType(IMachineRoot.ELEMENT_TYPE);
	}
	
	
	public static IContextRoot[] getContextRootChildren(IRodinProject project)
			throws RodinDBException {
		return project.getRootElementsOfType(IContextRoot.ELEMENT_TYPE);
	}
	
	
	public static void refreshViewer(final CommonViewer viewer) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					Object[] expanded = viewer.getExpandedElements();
					viewer.refresh(false);
					viewer.setExpandedElements(expanded);
				}
			}
		});
	}
	
	public static void debug(String message) {
		System.out.println(DEBUG_PREFIX + message);
	}

	/**
	 * Runs the given operation in a progress dialog.
	 * 
	 * @param op
	 *            a runnable operation
	 * @since 1.3
	 */
	public static void runWithProgress(IRunnableWithProgress op) {
		final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell) {
			@Override
			protected boolean isResizable() {
				return true;
			}
		};
		try {
			dialog.run(true, true, op);
		} catch (InterruptedException exception) {
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			realException.printStackTrace();
			final String message = realException.getMessage();
			MessageDialog.openError(shell, "Unexpected Error", message);
			return;
		}
	}

	/**
	 * Runs the given operation in a progress dialog.
	 * 
	 * @param op
	 *            a runnable operation
	 * @since 1.3
	 */
	public static void runWithProgress(final IWorkspaceRunnable op) {
		final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell) {
			@Override
			protected boolean isResizable() {
				return true;
			}
		};

		final IRunnableWithProgress wrap = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					op.run(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
		try {
			dialog.run(true, true, wrap);
		} catch (InterruptedException exception) {
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			realException.printStackTrace();
			final String message = realException.getMessage();
			MessageDialog.openError(shell, "Unexpected Error", message);
			return;
		}
	}

	// Adds statuses located under the given object in the explorer to the given set.
	// Expects obj to be a rodin project or element, or an element in the explorer model.
	// At project level, statuses are fetched through the Rodin database, which
	// avoids depending on the model controller (updated only if project has been expanded).
	// At under-project levels, statuses are fetched either through the database
	// or through the model controller, which is necessarily updated
	// if objects have been selected within.
	private static void addObjectStatuses(Object obj, Set<IPSStatus> statuses,
			boolean pendingOnly, SubMonitor subMonitor)
			throws InterruptedException {
		if (obj instanceof IProject) {
			final IRodinProject prj = RodinCore.valueOf((IProject) obj);
			if (prj != null) {
				addStatuses(prj, statuses, pendingOnly, subMonitor.newChild(1));
			}
		} else if (obj instanceof IRodinProject) {
			final IRodinProject prj = (IRodinProject) obj;
			addStatuses(prj, statuses, pendingOnly, subMonitor.newChild(1));
		} else if (obj instanceof IEventBRoot) {
			addStatuses((IEventBRoot) obj, statuses, pendingOnly, subMonitor.newChild(1));
		} else if (obj instanceof IPSStatus) {
			addStatus((IPSStatus) obj, statuses, pendingOnly, subMonitor.newChild(1));
		} else if (obj instanceof IElementNode) {
			addStatuses((IElementNode) obj, statuses, pendingOnly, subMonitor.newChild(1));
		} else if (obj instanceof IRodinElement) {
			// invariants, events, theorems, axioms
			final IModelElement element = ModelController.getModelElement(obj);
			addStatuses(element, statuses, pendingOnly, subMonitor.newChild(1));
		}
	}



	private static void addStatuses(IRodinProject project,
			Set<IPSStatus> statuses, boolean pendingOnly,
			IProgressMonitor monitor) throws InterruptedException {
		checkCancel(monitor);
		if (!project.exists()) {
			return;
		}
		try {
			final IPSRoot[] psRoots = project
					.getRootElementsOfType(IPSRoot.ELEMENT_TYPE);
			final SubMonitor subMonitor = SubMonitor.convert(monitor,
					psRoots.length);
			for (IPSRoot root : psRoots) {
				checkCancel(subMonitor);
				addStatuses(root, statuses, pendingOnly, subMonitor.newChild(1));
			}
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetChildrenException(e,
					UserAwareness.INFORM);
		}
	}

	private static void addStatuses(IEventBRoot root, Set<IPSStatus> statuses,
			boolean pendingOnly, IProgressMonitor monitor)
			throws InterruptedException {
		checkCancel(monitor);
		final IPSRoot psRoot = root.getPSRoot();
		if (!psRoot.exists()) {
			return;
		}
		try {
			final IPSStatus[] rootStatuses = psRoot.getStatuses();
			final SubMonitor subMonitor = SubMonitor.convert(monitor,
					rootStatuses.length);
			for (IPSStatus status : rootStatuses) {
				addStatus(status, statuses, pendingOnly, subMonitor.newChild(1));
			}
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetChildrenException(e,
					UserAwareness.INFORM);
		}
	}

	// 'Proof Obligations', 'Axioms', ...
	private static void addStatuses(IElementNode node, Set<IPSStatus> statuses,
			boolean pendingOnly, IProgressMonitor monitor)
			throws InterruptedException {
		checkCancel(monitor);
		final IModelElement modelElem = ModelController.getModelElement(node);
		if (modelElem == null) {
			return;
		}

		final IInternalElementType<?> childrenType = node.getChildrenType();
		final Object[] children = modelElem.getChildren(childrenType, false);
		final SubMonitor subMonitor = SubMonitor.convert(monitor,
				children.length);

		if (childrenType == IPSStatus.ELEMENT_TYPE) { // Proof Obligations Node
			for (IPSStatus status : (IPSStatus[]) children) {
				checkCancel(subMonitor);
				addStatus(status, statuses, pendingOnly, subMonitor.newChild(1));
			}
			return;
		}
		for (Object child : children) {
			checkCancel(subMonitor);
			final IModelElement modelElement = ModelController
					.getModelElement(child);
			if (modelElement != null) {
				addStatuses(modelElement, statuses, pendingOnly, subMonitor
						.newChild(1));
			}
		}
	}

	private static void addStatuses(IModelElement element,
			Set<IPSStatus> statuses, boolean pendingOnly,
			IProgressMonitor monitor) throws InterruptedException {
		checkCancel(monitor);
		if (element instanceof ModelPOContainer) {
			final ModelPOContainer modelPOContainer = (ModelPOContainer) element;
			final IPSStatus[] modelStatuses = modelPOContainer.getIPSStatuses();
			final SubMonitor subMonitor = SubMonitor.convert(monitor,
					modelStatuses.length);
			for (IPSStatus status : modelStatuses) {
				checkCancel(subMonitor);
				addStatus(status, statuses, pendingOnly, subMonitor.newChild(1));
			}
		}
	}

	private static void addStatus(IPSStatus status, Set<IPSStatus> statuses,
			boolean pendingOnly, IProgressMonitor monitor)
			throws InterruptedException {
		checkCancel(monitor);
		try {
			if (!status.exists()) {
				return;
			}
			if (pendingOnly && !isBrokenOrPending(status)) {
				return;
			}
			statuses.add(status);
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleRodinException(e,
					UserAwareness.INFORM);
		}
	}
	
	private static boolean isBrokenOrPending(IPSStatus status)
			throws RodinDBException {
		return status.isBroken()
				|| status.getConfidence() <= IConfidence.PENDING;
	}


	/**
	 * Returns the current selection of the explorer.
	 * 
	 * @return an array of selected objects
	 * @since 1.3
	 */
	public static Object[] getExplorerSelection() {
		final SelectionRetriever selRetriever = new SelectionRetriever();
		PlatformUI.getWorkbench().getDisplay().syncExec(selRetriever);

		return selRetriever.getResult();
	}
	
	/**
	 * @since 1.3
	 */
	public static void checkCancel(IProgressMonitor monitor)
			throws InterruptedException {
		if (monitor != null && monitor.isCanceled()) {
			throw new InterruptedException();
		}
	}

	/**
	 * Returns the status elements located under the given objects in the
	 * explorer.
	 * <p>
	 * This method is intended to be used with the results of
	 * {@link #getExplorerSelection()} as objects argument.
	 * </p>
	 * 
	 * @param objects
	 *            an array of objects
	 * @param pendingOnly
	 *            get only statuses with confidence {@link IConfidence#PENDING}
	 *            or less, or which are broken ({@link IPSStatus#isBroken()})
	 * @param monitor
	 *            the progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts <code>null</code>, indicating that no
	 *            progress should be reported and that the operation cannot be
	 *            cancelled.
	 * @return a set of statuses
	 * @throws InterruptedException
	 * @since 1.3
	 */
	public static Set<IPSStatus> getStatuses(Object[] objects,
			boolean pendingOnly, IProgressMonitor monitor)
			throws InterruptedException {
		final SubMonitor subMonitor = SubMonitor.convert(monitor,
			objects.length);
		final Set<IPSStatus> statuses = new LinkedHashSet<IPSStatus>();
		for (Object obj : objects) {
			checkCancel(monitor);
			addObjectStatuses(obj, statuses, pendingOnly, subMonitor
					.newChild(1));
		}
		return statuses;
	}

}
