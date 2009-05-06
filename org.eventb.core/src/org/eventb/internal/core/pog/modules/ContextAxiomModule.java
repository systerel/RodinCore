/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCAxiom;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IContextAxiomTable;
import org.eventb.core.pog.state.IContextHypothesisManager;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.tool.IModuleType;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextAxiomModule extends PredicateModule<ISCAxiom> {
	
	public static final IModuleType<ContextAxiomModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".contextAxiomModule"); //$NON-NLS-1$
	
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

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pog.modules.PredicateModule#getWDProofObligationDescription()
	 */
	@Override
	protected String getWDProofObligationDescription(boolean isTheorem) {
		if (isTheorem) {
			return "Well-definedness of Theorem";
		} else {
			return "Well-definedness of Axiom";
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pog.modules.PredicateModule#getWDProofObligationName(java.lang.String)
	 */
	@Override
	protected String getWDProofObligationName(String elementLabel, boolean isTheorem) {
		return elementLabel + "/WD";
	}

	@Override
	protected boolean isAccurate() {
		return ((IContextHypothesisManager) hypothesisManager).contextIsAccurate();
	}

}
