/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineCommitHypothesesModule extends CommitHypothesesModule {

	public static final IModuleType<FwdMachineCommitHypothesesModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineCommitHypothesesModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pog.modules.CommitHypothesesModule#getHypothesisManager()
	 */
	@Override
	protected IHypothesisManager getHypothesisManager(IPOGStateRepository repository) 
	throws CoreException {
		return (IHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
	}

}
