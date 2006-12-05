/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.state.IMachineInvariantTable;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class MachineEventInvariantModule extends MachineEventRefinementModule {
	
	protected ISCEvent abstractEvent;
	protected IMachineInvariantTable invariantTable;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOFile target,
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {

		if (isApplicable())
			processInvariants(
				target, 
				monitor);
					
	}
	
	protected abstract boolean isApplicable();
	
	private void processInvariants(
			IPOFile target, 
			IProgressMonitor monitor) throws RodinDBException {
		List<ISCPredicateElement> invariants = invariantTable.getElements();
		List<Predicate> invPredicates = invariantTable.getPredicates();
		
		for (int i=0; i<invariants.size(); i++) {
			
			String invariantLabel = ((ISCInvariant) invariants.get(i)).getLabel();
			
			Predicate predicate = invPredicates.get(i);
			
			if (goalIsTrivial(predicate))
				continue;
			
			FreeIdentifier[] freeIdentifiers = predicate.getFreeIdentifiers();
			HashSet<FreeIdentifier> freeIdents = 
				new HashSet<FreeIdentifier>(freeIdentifiers.length * 4 / 3 + 1);
			boolean commonIdents = false; // common identifiers?
			for(FreeIdentifier identifier : freeIdentifiers) {
				freeIdents.add(identifier);
				if(!commonIdents && concreteEventActionTable.getAssignedVariables().contains(identifier))
					commonIdents = true;
			}
				
			if (commonIdents || isInitialisation) {
				
				ArrayList<POGPredicate> hyp = makeActionHypothesis(freeIdents);
				
				createInvariantProofObligation(
						target, 
						(ISCInvariant) invariants.get(i), 
						invariantLabel, 
						predicate, 
						hyp, 
						freeIdents,
						monitor);
		
			}
			
		}
	}
	
	protected abstract void createInvariantProofObligation(
			IPOFile target, 
			ISCInvariant invariant, 
			String invariantLabel, 
			Predicate invPredicate, 
			ArrayList<POGPredicate> hyp, 
			Set<FreeIdentifier> freeIdents,
			IProgressMonitor monitor) throws RodinDBException;

	@Override
	public void initModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		abstractEvent = eventHypothesisManager.getFirstAbstractEvent();	
		invariantTable =
			(IMachineInvariantTable) repository.getState(IMachineInvariantTable.STATE_TYPE);
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEvent = null;
		invariantTable = null;
		super.endModule(element, target, repository, monitor);
	}

}
