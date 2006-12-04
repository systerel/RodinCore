/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSource;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.eventb.core.pog.state.IMachineVariantInfo;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventVariantModule extends MachineEventRefinementModule {

	public void process(IRodinElement element, IPOFile target,
			IStateRepository<IStatePOG> repository, IProgressMonitor monitor)
			throws CoreException {
		
		if (convergence == IConvergenceElement.Convergence.ORDINARY)
			return;
		
		Expression varExpression = machineVariantInfo.getExpression();
		List<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
		if (concreteEventActionTable.getDeltaPrime() != null)
			substitution.add(concreteEventActionTable.getDeltaPrime());
		Expression nextVarExpression = varExpression.applyAssignments(substitution, factory);
		substitution.clear();
		substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());	
		nextVarExpression = nextVarExpression.applyAssignments(substitution, factory);

		boolean isIntVariant = varExpression.getType().equals(factory.makeIntegerType());
		Predicate varPredicate = getVarPredicate(nextVarExpression, varExpression, isIntVariant);
		
		POGSource[] sources = sources(
				new POGSource(IPOSource.DEFAULT_ROLE, machineVariantInfo.getVariant()),
				new POGSource(IPOSource.DEFAULT_ROLE, concreteEvent));
		
		createPO(
				target, 
				concreteEventLabel + "/" + "VAR", 
				"Variant of event", 
				eventHypothesisManager.getFullHypothesis(target), 
				makeActionHypothesis(), 
				new POGPredicate(machineVariantInfo.getVariant(), varPredicate), 
				sources, 
				emptyHints, 
				monitor);
		
		if (isIntVariant) {
			Predicate natPredicate = 
				factory.makeRelationalPredicate(
						Formula.IN, 
						varExpression, 
						factory.makeAtomicExpression(Formula.NATURAL, null), 
						null);
			createPO(
					target, 
					concreteEventLabel + "/" + "NAT", 
					"Natural number variant of event", 
					eventHypothesisManager.getFullHypothesis(target), 
					makeActionHypothesis(), 
					new POGPredicate(machineVariantInfo.getVariant(), natPredicate), 
					sources, 
					emptyHints, 
					monitor);
		}
	}
	
	private Predicate getVarPredicate(
			Expression nextVarExpression, 
			Expression varExpression, 
			boolean isIntVariant) {
		int tag;
		if (convergence == IConvergenceElement.Convergence.ANTICIPATED)
			if (isIntVariant)
				tag = Formula.LE;
			else
				tag = Formula.SUBSETEQ;
		else
			if (isIntVariant)
				tag = Formula.LT;
			else
				tag = Formula.SUBSET;
		
		Predicate varPredicate = 
			factory.makeRelationalPredicate(tag, nextVarExpression, varExpression, null);
		return varPredicate;
	}

	protected IConvergenceElement.Convergence convergence;
	protected IMachineVariantInfo machineVariantInfo;
	
	@Override
	public void initModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		convergence = concreteEvent.getConvergence();
		machineVariantInfo = 
			(IMachineVariantInfo) repository.getState(IMachineVariantInfo.STATE_TYPE);
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		machineVariantInfo = null;
		super.endModule(element, target, repository, monitor);
	}
	
}
