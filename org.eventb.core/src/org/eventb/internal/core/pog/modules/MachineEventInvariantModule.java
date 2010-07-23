/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.state.IMachineInvariantTable;
import org.eventb.core.pog.state.IPOGStateRepository;
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
	@Override
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		if (isApplicable())
			processInvariants(
				repository.getTarget(), 
				monitor);
					
	}
	
	protected abstract boolean isApplicable();
	
	private void processInvariants(
			IPORoot target, 
			IProgressMonitor monitor) throws CoreException {
		List<ISCInvariant> invariants = invariantTable.getElements();
		List<Predicate> invPredicates = invariantTable.getPredicates();
		
		for (int i=0; i<invariants.size(); i++) {
			
			ISCInvariant invariant = invariants.get(i);
			if (invariant.isTheorem()) {
				continue;
			}
			String invariantLabel = invariant.getLabel();
			
			Predicate predicate = invPredicates.get(i);
			
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
				if(!commonIdents)
					if (concreteEventActionTable.getAssignedVariables().contains(identifier))
						commonIdents = true;
					else if (abstractEventActionTable.getAssignedVariables().contains(identifier))
						commonIdents = true;
			}
				
			if (commonIdents || isInitialisation) {
				
				createInvariantProofObligation(
						target, 
						invariant, 
						invariantLabel, 
						predicate, 
						freeIdents,
						monitor);
		
			}
			
		}
	}
	
	protected IPOGHint getInvariantPredicateSelectionHint(
			IPORoot file, ISCInvariant invariant) throws CoreException {
		return makePredicateSelectionHint(machineHypothesisManager.getPredicate(invariant));
	}
	
	protected abstract void createInvariantProofObligation(
			IPORoot target, 
			ISCInvariant invariant, 
			String invariantLabel, 
			Predicate invPredicate, 
			Set<FreeIdentifier> freeIdents,
			IProgressMonitor monitor) throws CoreException;

	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		abstractEvent = abstractEventGuardList.getFirstAbstractEvent();	
		invariantTable =
			(IMachineInvariantTable) repository.getState(IMachineInvariantTable.STATE_TYPE);
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEvent = null;
		invariantTable = null;
		super.endModule(element, repository, monitor);
	}

}
