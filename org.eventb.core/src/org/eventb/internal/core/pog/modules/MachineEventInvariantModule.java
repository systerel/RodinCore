/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IMachineInvariantTable;
import org.eventb.core.pog.state.IStateRepository;
import org.eventb.core.pog.util.POGHint;
import org.eventb.core.pog.util.POGPredicateSelectionHint;
import org.rodinp.core.IRodinElement;

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
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		if (isApplicable())
			processInvariants(
				repository.getTarget(), 
				monitor);
					
	}
	
	protected abstract boolean isApplicable();
	
	private void processInvariants(
			IPOFile target, 
			IProgressMonitor monitor) throws CoreException {
		ISCInvariant[] invariants = invariantTable.getElements();
		Predicate[] invPredicates = invariantTable.getPredicates();
		
		for (int i=0; i<invariants.length; i++) {
			
			String invariantLabel = invariants[i].getLabel();
			
			Predicate predicate = invPredicates[i];
			
			if (goalIsTrivial(predicate)) {
				if (DEBUG_TRIVIAL)
					debugTraceTrivial(concreteEventLabel + "/" + invariantLabel + "/INV");
				continue;
			}
			
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
				
				createInvariantProofObligation(
						target, 
						invariants[i], 
						invariantLabel, 
						predicate, 
						freeIdents,
						monitor);
		
			}
			
		}
	}
	
	protected POGHint getInvariantPredicateSelectionHint(
			IPOFile file, ISCInvariant invariant) throws CoreException {
		return new POGPredicateSelectionHint(machineHypothesisManager.getPredicate(invariant));
	}
	
	protected abstract void createInvariantProofObligation(
			IPOFile target, 
			ISCInvariant invariant, 
			String invariantLabel, 
			Predicate invPredicate, 
			Set<FreeIdentifier> freeIdents,
			IProgressMonitor monitor) throws CoreException;

	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		abstractEvent = abstractEventGuardList.getFirstAbstractEvent();	
		invariantTable =
			(IMachineInvariantTable) repository.getState(IMachineInvariantTable.STATE_TYPE);
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEvent = null;
		invariantTable = null;
		super.endModule(element, repository, monitor);
	}

}
