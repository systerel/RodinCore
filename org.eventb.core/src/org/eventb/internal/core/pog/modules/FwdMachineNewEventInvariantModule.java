/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added PO nature
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGNature;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineNewEventInvariantModule extends MachineEventInvariantModule {

	public static final IModuleType<FwdMachineNewEventInvariantModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineNewEventInvariantModule"); //$NON-NLS-1$
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pog.modules.MachineEventInvariantModule#isApplicable()
	 */
	@Override
	protected boolean isApplicable() {
//		 this POG module applies to events of initial models and new events 
		return abstractEvent == null;
	}
	
	@Override
	protected void createInvariantProofObligation(
			IPORoot target, 
			ISCInvariant invariant, 
			String invariantLabel, 
			Predicate invPredicate, 
			Set<FreeIdentifier> freeIdents,
			IProgressMonitor monitor) throws CoreException {
		
		LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
		
		if (concreteEventActionTable.getDeltaPrime() != null)
			substitution.add(concreteEventActionTable.getDeltaPrime());
		Predicate predicate = invPredicate.applyAssignments(substitution, factory);
		substitution.clear();
		substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());	
		predicate = predicate.applyAssignments(substitution, factory);
	
		ArrayList<IPOGPredicate> hyp = makeActionHypothesis(predicate);
		
		String sequentName = concreteEventLabel + "/" + invariantLabel + "/INV";
		IRodinElement invariantSource = invariant.getSource();
		final IPOGNature nature = isInitialisation ? IPOGNature.INVARIANT_ESTABLISHMENT
				: IPOGNature.INVARIANT_PRESERVATION;
		createPO(
				target, 
				sequentName, 
				nature,
				fullHypothesis,
				hyp,
				makePredicate(predicate, invariantSource),
				new IPOGSource[] {
						makeSource(IPOSource.DEFAULT_ROLE, concreteEvent.getSource()), 
						makeSource(IPOSource.DEFAULT_ROLE, invariantSource)
				},
				new IPOGHint[] {
						getLocalHypothesisSelectionHint(target, sequentName),
						getInvariantPredicateSelectionHint(target, invariant)
				},
				accurate,
				monitor);
	}

}
