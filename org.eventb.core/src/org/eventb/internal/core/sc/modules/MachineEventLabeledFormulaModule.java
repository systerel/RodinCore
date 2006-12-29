/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.state.IEventLabelSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.tool.state.IToolStateRepository;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class MachineEventLabeledFormulaModule extends LabeledFormulaModule {
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledElementModule#getLabelSymbolTableFromRepository(org.eventb.core.sc.IStateRepository)
	 */
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			IToolStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IEventLabelSymbolTable.STATE_TYPE);
	}

}
