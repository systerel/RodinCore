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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 *         This class contains some utility static methods that are used in this
 *         Explorer plug-in.
 * @since 1.0
 *
 */
public class ExplorerUtils {

	public static boolean DEBUG;

	public static final String DEBUG_PREFIX = "*** Event-B Explorer *** ";

	/**
	 * @since 1.3
	 */
	public static final FormulaFactory factory = FormulaFactory.getDefault();

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

}
