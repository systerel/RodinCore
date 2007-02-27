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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IMachineVariantInfo;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.util.POGPredicate;
import org.eventb.core.pog.util.POGSource;
import org.eventb.core.pog.util.POGTraceablePredicate;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineEventVariantModule extends MachineEventActionUtilityModule {

	public static final IModuleType<FwdMachineEventVariantModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineEventVariantModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		if (concreteConvergence == IConvergenceElement.Convergence.ORDINARY)
			return;
		if (concreteConvergence == IConvergenceElement.Convergence.CONVERGENT
				&& abstractConvergence == IConvergenceElement.Convergence.CONVERGENT)
			return;
		
		IPOFile target = repository.getTarget();
		
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
		
		ArrayList<POGPredicate> hyp =  makeActionHypothesis(varPredicate);
		
		createPO(
				target, 
				concreteEventLabel + "/" + "VAR", 
				"Variant of event", 
				eventHypothesisManager.getFullHypothesis(), 
				hyp, 
				new POGTraceablePredicate(varPredicate, machineVariantInfo.getVariant()), 
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
					eventHypothesisManager.getFullHypothesis(), 
					hyp, 
					new POGTraceablePredicate(natPredicate, machineVariantInfo.getVariant()), 
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
		if (concreteConvergence == IConvergenceElement.Convergence.ANTICIPATED)
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

	protected IConvergenceElement.Convergence concreteConvergence;
	protected IConvergenceElement.Convergence abstractConvergence;
	protected IMachineVariantInfo machineVariantInfo;
	
	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		concreteConvergence = concreteEvent.getConvergence();
		IAbstractEventGuardList abstractEventGuardList = 
			(IAbstractEventGuardList) repository.getState(IAbstractEventGuardList.STATE_TYPE);
		ISCEvent abstractEvent = abstractEventGuardList.getFirstAbstractEvent();
		abstractConvergence = abstractEvent == null ? null : abstractEvent.getConvergence();
		machineVariantInfo = 
			(IMachineVariantInfo) repository.getState(IMachineVariantInfo.STATE_TYPE);
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		machineVariantInfo = null;
		super.endModule(element, repository, monitor);
	}
	
}
