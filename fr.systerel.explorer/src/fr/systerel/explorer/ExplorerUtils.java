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

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 *         This class contains some utility static methods that are used in this
 *         Explorer plug-in.
 *
 */
public class ExplorerUtils {

	public static IMachineRoot[] getMachineRootChildren(IRodinProject project)
			throws RodinDBException {
		ArrayList<IMachineRoot> result = new ArrayList<IMachineRoot>();
		for (IRodinElement element : project.getChildren()) {
			if (element instanceof IRodinFile) {
				IInternalElement root = ((IRodinFile) element)
						.getRoot();
				if (root instanceof IMachineRoot)
					result.add((IMachineRoot) root);
			}
		}
		return result.toArray(new IMachineRoot[result.size()]);
	}
	
	
	public static IContextRoot[] getContextRootChildren(IRodinProject project)
			throws RodinDBException {
		ArrayList<IContextRoot> result = new ArrayList<IContextRoot>();
		for (IRodinElement element : project.getChildren()) {
			if (element instanceof IRodinFile) {
				IInternalElement root = ((IRodinFile) element)
						.getRoot();
				if (root instanceof IContextRoot)
					result.add((IContextRoot) root);
			}
		}
		return result.toArray(new IContextRoot[result.size()]);
	}
	
	
	public static IRodinProject getRodinProject(IProject project) {
		return RodinCore.getRodinDB().getRodinProject(project.getName());
	}
}
