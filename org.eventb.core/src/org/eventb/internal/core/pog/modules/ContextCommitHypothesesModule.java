/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.pog.state.IContextHypothesisManager;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IState;
import org.eventb.core.tool.state.IToolStateRepository;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextCommitHypothesesModule extends CommitHypothesesModule {

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pog.modules.CommitHypothesesModule#getHypothesisManager()
	 */
	@Override
	protected IHypothesisManager getHypothesisManager(IToolStateRepository<IState> repository) 
	throws CoreException {
		return (IHypothesisManager) repository.getState(IContextHypothesisManager.STATE_TYPE);
	}

}
