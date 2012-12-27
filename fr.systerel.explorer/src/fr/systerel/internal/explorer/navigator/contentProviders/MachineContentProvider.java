/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.contentProviders;

import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * The simple content provider for Machine elements.
 *
 */
public class MachineContentProvider extends AbstractRootContentProvider {

	public MachineContentProvider() {
		super(IMachineRoot.ELEMENT_TYPE);
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IMachineRoot) {
			return ((IMachineRoot) element).getParent().getParent();
		}
		return null;
	}

	@Override
	protected IEventBRoot[] getRootChildren(IRodinProject project)
			throws RodinDBException {
		return ExplorerUtils.getMachineRootChildren(project);
	}

}
