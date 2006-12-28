/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.pog.POGIntervalSelectionHint;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.state.ITypingState;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventWitnessModule extends MachineEventRefinementModule {

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.state.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOFile target,
			IPOGStateRepository repository, 
			IProgressMonitor monitor)
			throws CoreException {
		
		ISCEvent event = (ISCEvent) element;
		
		ISCWitness[] witnesses = event.getSCWitnesses();
		
		if (witnesses.length == 0)
			return;
					
		for (ISCWitness witness : witnesses) {
			Predicate predicate = witness.getPredicate(factory, typeEnvironment);
			
			if (goalIsTrivial(predicate)) // only trivial POs can be derived from this witness
				continue;
			
			Predicate wdPredicate = predicate.getWDPredicate(factory);
			String witnessLabel = witness.getLabel();
			FreeIdentifier witnessIdentifier = factory.makeFreeIdentifier(witnessLabel, null);
			witnessIdentifier.typeCheck(typeEnvironment);
			
			assert witnessIdentifier.isTypeChecked();
			
			createProofObligation(
					target, 
					witness, 
					wdPredicate, 
					witnessLabel, 
					witnessIdentifier, 
					"WWD",
					"Well-definedness of witness",
					monitor);
			
			boolean deterministic = isDeterministic(predicate, witnessIdentifier);
			if (!deterministic) {
				Predicate fisPredicate = predicate;
				FreeIdentifier[] freeIdentifiers = predicate.getFreeIdentifiers();
				if (containsIdent(witnessIdentifier, freeIdentifiers)) {
					BoundIdentDecl boundIdent = witnessIdentifier.asDecl(factory);
					ArrayList<FreeIdentifier> toBind = new ArrayList<FreeIdentifier>(1);
					toBind.add(witnessIdentifier);
					fisPredicate = fisPredicate.bindTheseIdents(toBind, factory);
					fisPredicate = factory.makeQuantifiedPredicate(
							Formula.EXISTS, 
							new BoundIdentDecl[] { boundIdent }, 
							fisPredicate, null);
				}
				createProofObligation(
						target, 
						witness, 
						fisPredicate, 
						witnessLabel, 
						witnessIdentifier, 
						"WFIS",
						"Feasibility of witness",
						monitor);			
			}
		}
		
	}

	private void createProofObligation(
			IPOFile target, 
			ISCWitness witness, 
			Predicate goal, 
			String witnessLabel, 
			FreeIdentifier witnessIdentifier, 
			String suffix,
			String desc,
			IProgressMonitor monitor) throws RodinDBException {
		if (!goalIsTrivial(goal)) {
			String sequentName = concreteEventLabel + "/" + witnessLabel + "/" + suffix;
			
			ArrayList<POGPredicate> actionHyp = makeActionHypothesis();
			boolean primed = witnessIdentifier.isPrimed();
			List<POGPredicate> localHyp = primed ? actionHyp : emptyPredicates;
			
			if (primed) { // global witness
				goal = applyDetAssignments(goal);
			}
			
			createPO(
					target, 
					sequentName, 
					desc, 
					fullHypothesis, 
					localHyp,
					new POGPredicate(witness, goal),
					sources(new POGSource(IPOSource.DEFAULT_ROLE, witness)),
					hints(
							new POGIntervalSelectionHint(
									eventHypothesisManager.getRootHypothesis(target),
									getSequentHypothesis(target, sequentName)
							)
					),
					monitor);
		}
	}

	private Predicate applyDetAssignments(Predicate wdPredicate) {
		LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
		if (concreteEventActionTable.getXiUnprime() != null)
			substitution.add(concreteEventActionTable.getXiUnprime());
		substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
		wdPredicate = wdPredicate.applyAssignments(substitution, factory);
		return wdPredicate;
	}

	private boolean isDeterministic(Predicate predicate, FreeIdentifier identifier) {
		boolean deterministic = false;
		if (predicate instanceof RelationalPredicate) {
			RelationalPredicate relPredicate = (RelationalPredicate) predicate;
			if (relPredicate.getTag() == Formula.EQUAL) {
				Expression lhs = relPredicate.getLeft();
				if (lhs instanceof FreeIdentifier) {
					FreeIdentifier wIdentifier = (FreeIdentifier) lhs;
					if (wIdentifier.equals(identifier)) {
						Expression rhs = relPredicate.getRight();
						FreeIdentifier[] freeIdentifiers = rhs.getFreeIdentifiers();
						boolean found = containsIdent(wIdentifier, freeIdentifiers);
						if (!found)
							deterministic = true;
					}
				}
			}
		}
		return deterministic;
	}

	private boolean containsIdent(FreeIdentifier ident, FreeIdentifier[] idents) {
		boolean found = false;
		for (FreeIdentifier identifier : idents)
			if (ident.equals(identifier)) {
				found = true;
				break;
			}
		return found;
	}
	
	protected ITypeEnvironment typeEnvironment;

	@Override
	public void initModule(
			IRodinElement element, 
			IPOFile target, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		ITypingState typingState = (ITypingState) repository.getState(ITypingState.STATE_TYPE);
		typeEnvironment = typingState.getTypeEnvironment();
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOFile target, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		typeEnvironment = null;
		super.endModule(element, target, repository, monitor);
	}

}
