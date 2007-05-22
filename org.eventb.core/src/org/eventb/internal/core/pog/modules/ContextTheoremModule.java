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
import org.eventb.core.ISCTheorem;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IContextHypothesisManager;
import org.eventb.core.pog.state.IContextTheoremTable;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.tool.IModuleType;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextTheoremModule extends TheoremModule {

	public static final IModuleType<ContextTheoremModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".contextTheoremModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected IHypothesisManager getHypothesisManager(IPOGStateRepository repository) 
	throws CoreException {
		return (IContextHypothesisManager) repository.getState(IContextHypothesisManager.STATE_TYPE);
	}

	@Override
	protected IPredicateTable<ISCTheorem> getPredicateTable(IPOGStateRepository repository) 
	throws CoreException {
		return (IContextTheoremTable) repository.getState(IContextTheoremTable.STATE_TYPE);
	}

	@Override
	protected boolean isAccurate() {
		return ((IContextHypothesisManager) hypothesisManager).contextIsAccurate();
	}

}
