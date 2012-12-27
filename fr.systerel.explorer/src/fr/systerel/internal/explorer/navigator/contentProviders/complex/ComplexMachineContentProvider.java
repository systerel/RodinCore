/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.contentProviders.complex;

import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelProject;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * The content provider for machines
 *
 */
public class ComplexMachineContentProvider extends AbstractComplexContentProvider {

	public ComplexMachineContentProvider() {
		super(IMachineRoot.ELEMENT_TYPE);
	}

	@Override
	protected IMachineRoot[] convertToElementType(ModelProject project) {
		return ModelController.convertToIMachine(project.getRootMachines());
	}

	@Override
	protected IMachineRoot[] getRootChildren(IRodinProject project)
			throws RodinDBException {
		return ExplorerUtils.getMachineRootChildren(project);
	}
	
}

