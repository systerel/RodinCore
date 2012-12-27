/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Universitaet Duesseldorf - added theorem attribute
 *     Systerel - added PO nature
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCInvariant;
import org.eventb.core.pog.IPOGNature;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IMachineInvariantTable;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineInvariantModule extends PredicateModule<ISCInvariant> {

	public static final IModuleType<FwdMachineInvariantModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineInvariantModule"); //$NON-NLS-1$
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected IHypothesisManager getHypothesisManager(IPOGStateRepository repository) 
	throws CoreException {
		return (IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
	}

	@Override
	protected IPredicateTable<ISCInvariant> getPredicateTable(IPOGStateRepository repository) 
	throws CoreException {
		return (IMachineInvariantTable) repository.getState(IMachineInvariantTable.STATE_TYPE);
	}

	@Override
	protected IPOGNature getWDProofObligationNature(boolean isTheorem) {
		if (isTheorem) {
			return IPOGNature.THEOREM_WELL_DEFINEDNESS;
		} else {
			return IPOGNature.INVARIANT_WELL_DEFINEDNESS;
		}
	}

	@Override
	protected boolean isAccurate() {
		return ((IMachineHypothesisManager) hypothesisManager).machineIsAccurate();
	}

	@Override
	protected String getProofObligationPrefix(ISCInvariant predicateElement)
			throws RodinDBException {
		return predicateElement.getLabel();
	}

}
