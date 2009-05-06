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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IEventWitnessTable;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineEventWitnessModule extends MachineEventActionUtilityModule {

	public static final IModuleType<FwdMachineEventWitnessModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineEventWitnessModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.state.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		List<ISCWitness> witnesses = witnessTable.getWitnesses();
		List<Predicate> predicates = witnessTable.getPredicates();
		
		int size = witnesses.size();
		if (size == 0)
			return;
		
		IPORoot target = repository.getTarget();
					
		for (int i=0; i<size; i++) {
			ISCWitness witness = witnesses.get(i);
			Predicate predicate = predicates.get(i);
			
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
			IPORoot target, 
			ISCWitness witness, 
			Predicate goal, 
			String witnessLabel, 
			FreeIdentifier witnessIdentifier, 
			String suffix,
			String desc,
			IProgressMonitor monitor) throws CoreException {
		String sequentName = concreteEventLabel + "/" + witnessLabel + "/" + suffix;
		
		if (goalIsTrivial(goal)) {
			if (DEBUG_TRIVIAL)
				debugTraceTrivial(sequentName);
			return;
		}
		
		goal = applyDetAssignments(goal);
		
		ArrayList<IPOGPredicate> hyp = makeActionHypothesis(goal);
					
		IRodinElement witnessSource = witness.getSource();
		createPO(
				target, 
				sequentName, 
				desc, 
				fullHypothesis, 
				hyp,
				makePredicate(goal, witnessSource),
				new IPOGSource[] {
					makeSource(IPOSource.DEFAULT_ROLE, witnessSource)
				},
				new IPOGHint[] {
						makeIntervalSelectionHint(
								eventHypothesisManager.getRootHypothesis(),
								getSequentHypothesis(target, sequentName)
						)
				},
				accurate,
				monitor);
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
	
	IEventWitnessTable witnessTable;
	protected ITypeEnvironment typeEnvironment;

	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		typeEnvironment = repository.getTypeEnvironment();
		witnessTable = (IEventWitnessTable) repository.getState(IEventWitnessTable.STATE_TYPE);
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		typeEnvironment = null;
		witnessTable = null;
		super.endModule(element, repository, monitor);
	}

}
