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
import org.eventb.core.pog.state.IMachineInvariantTable;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.state.IStateRepository;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineInvariantModule extends PredicateModule {

	@Override
	protected IHypothesisManager getHypothesisManager(IStateRepository<IStatePOG> repository) 
	throws CoreException {
		return (IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
	}

	@Override
	protected IPredicateTable getPredicateTable(IStateRepository<IStatePOG> repository) 
	throws CoreException {
		return (IMachineInvariantTable) repository.getState(IMachineInvariantTable.STATE_TYPE);
	}
	
	@Override
	protected String getWDProofObligationSourceRole() {
		return "invariant";
	}

	@Override
	protected String getWDProofObligationDescription() {
		return "Well-definedness of Invariant";
	}

	@Override
	protected String getWDProofObligationName(String elementLabel) {
		return elementLabel + "/WD";
	}

}
