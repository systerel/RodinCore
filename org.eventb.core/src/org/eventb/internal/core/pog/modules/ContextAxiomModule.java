/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     University of Dusseldorf - added theorem attribute
 *     Systerel - added PO nature
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCAxiom;
import org.eventb.core.pog.IPOGNature;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IContextAxiomTable;
import org.eventb.core.pog.state.IContextHypothesisManager;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextAxiomModule extends PredicateModule<ISCAxiom> {
	
	public static final IModuleType<ContextAxiomModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".contextAxiomModule"); //$NON-NLS-1$
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pog.modules.PredicateModule#getHypothesisManager(org.eventb.core.state.IStateRepository)
	 */
	@Override
	protected IHypothesisManager getHypothesisManager(
			IPOGStateRepository repository) throws CoreException {
		return (IContextHypothesisManager) repository.getState(IContextHypothesisManager.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pog.modules.PredicateModule#getPredicateTable(org.eventb.core.state.IStateRepository)
	 */
	@Override
	protected IPredicateTable<ISCAxiom> getPredicateTable(
			IPOGStateRepository repository) throws CoreException {
		return (IContextAxiomTable) repository.getState(IContextAxiomTable.STATE_TYPE);
	}

	@Override
	protected IPOGNature getWDProofObligationNature(boolean isTheorem) {
		if (isTheorem) {
			return IPOGNature.THEOREM_WELL_DEFINEDNESS;
		} else {
			return IPOGNature.AXIOM_WELL_DEFINEDNESS;
		}
	}

	@Override
	protected boolean isAccurate() {
		return ((IContextHypothesisManager) hypothesisManager).contextIsAccurate();
	}

	@Override
	protected String getProofObligationPrefix(ISCAxiom predicateElement)
			throws RodinDBException {
		return predicateElement.getLabel();
	}

}
