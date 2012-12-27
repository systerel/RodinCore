/*******************************************************************************
 * Copyright (c) 2008, 2012 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineTheoremFilterModule extends POGFilterTestModule {

	public static final IModuleType<MachineTheoremFilterModule> MODULE_TYPE = 
		POGCore.getModuleType(PLUGIN_ID + ".machineTheoremFilterModule"); //$NON-NLS-1$
	
	@Override
	public boolean accept(String poName, IProgressMonitor monitor)
			throws CoreException {
		String[] strings = poName.split("/");
		return !strings[strings.length-1].equals("THM");
	}

	@Override
	public void endModule(IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// Nothing to do
	}

	@Override
	public void initModule(IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// Nothing to do
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
