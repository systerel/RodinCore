/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 *         This class contains some utility static methods that are used in this
 *         Explorer plug-in.
 *
 */
public class ExplorerUtils {

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
}
