/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IMachineTheoremTable;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.pog.state.IPOGState;
import org.eventb.core.tool.state.IStateRepository;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineTheoremModule extends TheoremModule {

	@Override
	protected IHypothesisManager getHypothesisManager(IStateRepository<IPOGState> repository) 
	throws CoreException {
		return (IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
	}

	@Override
	protected IPredicateTable getPredicateTable(IStateRepository<IPOGState> repository) 
	throws CoreException {
		return (IMachineTheoremTable) repository.getState(IMachineTheoremTable.STATE_TYPE);
	}

}
