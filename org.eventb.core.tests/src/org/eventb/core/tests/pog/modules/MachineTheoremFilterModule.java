/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPOGFilterModule#accept(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean accept(String poName, IProgressMonitor monitor)
			throws CoreException {
		String[] strings = poName.split("/");
		return !strings[strings.length-1].equals("THM");
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPOGFilterModule#endModule(org.eventb.core.pog.state.IPOGStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void endModule(IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPOGFilterModule#initModule(org.eventb.core.pog.state.IPOGStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void initModule(IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.tool.IModule#getModuleType()
	 */
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
