/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGNature;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IMachineVariantInfo;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineEventVariantModule extends MachineEventActionUtilityModule {

	public static final IModuleType<FwdMachineEventVariantModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineEventVariantModule"); //$NON-NLS-1$
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		// no PO for ordinary events
		if (concreteConvergence == IConvergenceElement.Convergence.ORDINARY)
			return;
		
		// no PO for convergent events if the abstract event was convergent
		if (concreteConvergence == IConvergenceElement.Convergence.CONVERGENT
				&& abstractConvergence == IConvergenceElement.Convergence.CONVERGENT)
			return;
		
		// no PO for anticipated events if there is no variant
		Expression varExpression = machineVariantInfo.getExpression();
		if (concreteConvergence == IConvergenceElement.Convergence.ANTICIPATED
				&& varExpression == null)
			return;
		
		IPORoot target = repository.getTarget();
		
		List<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
		if (concreteEventActionTable.getDeltaPrime() != null)
			substitution.add(concreteEventActionTable.getDeltaPrime());
		Expression nextVarExpression = varExpression.applyAssignments(substitution);
		substitution.clear();
		substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());	
		nextVarExpression = nextVarExpression.applyAssignments(substitution);

		boolean isIntVariant = varExpression.getType().equals(factory.makeIntegerType());
		Predicate varPredicate = getVarPredicate(nextVarExpression, varExpression, isIntVariant);
		
		IRodinElement variantSource = machineVariantInfo.getVariant().getSource();
		IPOGSource[] sources = new IPOGSource[] {
				makeSource(IPOSource.DEFAULT_ROLE, variantSource),
				makeSource(IPOSource.DEFAULT_ROLE, concreteEvent.getSource())
		};
		
		ArrayList<IPOGPredicate> hyp =  makeActionHypothesis(varPredicate);
		
		String sequentNameVAR = concreteEventLabel + "/VAR";
		createPO(
				target, 
				sequentNameVAR, 
				IPOGNature.EVENT_VARIANT, 
				eventHypothesisManager.getFullHypothesis(), 
				hyp, 
				makePredicate(varPredicate, variantSource), 
				sources, 
				new IPOGHint[] {
						getLocalHypothesisSelectionHint(target, sequentNameVAR)
					}, 
				accurate,
				monitor);
		
		if (isIntVariant) {
			Predicate natPredicate = 
				factory.makeRelationalPredicate(
						Formula.IN, 
						varExpression, 
						factory.makeAtomicExpression(Formula.NATURAL, null), 
						null);
			String sequentNameNAT = concreteEventLabel + "/NAT";
			createPO(
					target, 
					sequentNameNAT, 
					IPOGNature.EVENT_VARIANT_IN_NAT, 
					eventHypothesisManager.getFullHypothesis(), 
					hyp, 
					makePredicate(natPredicate, variantSource), 
					sources, 
					new IPOGHint[] {
							getLocalHypothesisSelectionHint(target, sequentNameNAT)
						}, 
					accurate,
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
		getAbstractConvergence(repository);
		machineVariantInfo = 
			(IMachineVariantInfo) repository.getState(IMachineVariantInfo.STATE_TYPE);
	}

	private void getAbstractConvergence(IPOGStateRepository repository) throws CoreException, RodinDBException {
		IAbstractEventGuardList abstractEventGuardList = 
			(IAbstractEventGuardList) repository.getState(IAbstractEventGuardList.STATE_TYPE);
		List<ISCEvent> abstractEvents = abstractEventGuardList.getAbstractEvents();
		if (abstractEvents.size() == 0) {
			abstractConvergence = null;
			return;
		}
		
		List<IConvergenceElement.Convergence> convergences = 
			new ArrayList<IConvergenceElement.Convergence>(abstractEvents.size());
		
		for(ISCEvent abstractEvent : abstractEvents) {
			convergences.add(abstractEvent.getConvergence());
		}
		abstractConvergence = Collections.min(convergences);
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
