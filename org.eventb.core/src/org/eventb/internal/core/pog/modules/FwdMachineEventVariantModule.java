/*******************************************************************************
 * Copyright (c) 2006, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added PO nature
 *     Systerel - Simplify PO for anticipated event (FR326)
 *     Systerel - lexicographic variants
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import static org.eventb.core.IConvergenceElement.Convergence.ANTICIPATED;
import static org.eventb.core.IConvergenceElement.Convergence.CONVERGENT;
import static org.eventb.core.IConvergenceElement.Convergence.ORDINARY;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LT;
import static org.eventb.core.ast.Formula.NATURAL;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IntegerType;
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

	/**
	 * Stores information about variants for which POs should be generated.
	 */
	private class Info {
		// Index in machineVariantInfo
		public final int index;

		// Before expression
		public final Expression expression;

		// Is this an integer variant
		public final boolean isNatural;

		// After expression
		public final Expression nextExpression;

		public Info(int index) {
			this.index = index;
			this.expression = machineVariantInfo.getExpression(index);
			this.isNatural = expression.getType() instanceof IntegerType;
			this.nextExpression = getAfterExpression(expression);
		}

		public boolean isUnchanged() {
			return nextExpression.equals(expression);
		}

		public Predicate getVarPredicate(boolean strict) {
			final int tag;
			if (isNatural) {
				tag = strict ? LT : LE;
			} else {
				tag = strict ? SUBSET : SUBSETEQ;
			}
			
			return factory.makeRelationalPredicate(tag, nextExpression, expression, null);
		}

		public Predicate getNatPredicate() {
			return factory.makeRelationalPredicate(
					IN, 
					expression,
					factory.makeAtomicExpression(NATURAL, null), 
					null);

		}
	}

	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		// no PO for ordinary events
		if (concreteConvergence == ORDINARY)
			return;
		
		// no PO for convergent events if the abstract event was convergent
		if (concreteConvergence == CONVERGENT
				&& abstractConvergence == CONVERGENT)
			return;
		
		// no PO for anticipated events if there is no variant
		if (concreteConvergence == ANTICIPATED && !machineVariantInfo.machineHasVariant())
			return;
		
		IPORoot target = repository.getTarget();

		Info info = new Info(0);

		if (concreteConvergence == ANTICIPATED && info.isUnchanged()) {
			// The variant is not modified by this anticipated event,
			// do not generate any proof obligation.
			return;
		}
		
		Predicate varPredicate = info.getVarPredicate(concreteConvergence == CONVERGENT);
		
		IRodinElement variantSource = machineVariantInfo.getVariant(info.index).getSource();
		IPOGSource[] sources = new IPOGSource[] {
				makeSource(IPOSource.DEFAULT_ROLE, variantSource),
				makeSource(IPOSource.DEFAULT_ROLE, concreteEvent.getSource())
		};
		
		ArrayList<IPOGPredicate> hyp =  makeActionHypothesis(varPredicate);
		
		String sequentNameVAR = machineVariantInfo.getPOName(info.index, concreteEventLabel, "VAR");
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
		
		if (info.isNatural && concreteConvergence != ANTICIPATED) {
			Predicate natPredicate = info.getNatPredicate();
			String sequentNameNAT = machineVariantInfo.getPOName(info.index, concreteEventLabel, "NAT");
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

	// Returns the value of the variant after this event has executed.
	Expression getAfterExpression(Expression varExpression) {
		List<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
		if (concreteEventActionTable.getDeltaPrime() != null)
			substitution.add(concreteEventActionTable.getDeltaPrime());
		Expression nextVarExpression = varExpression.applyAssignments(substitution);
		substitution.clear();
		substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());	
		nextVarExpression = nextVarExpression.applyAssignments(substitution);
		return nextVarExpression;
	}
	
	protected Convergence concreteConvergence;
	protected Convergence abstractConvergence;
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
			repository.getState(IMachineVariantInfo.STATE_TYPE);
	}

	private void getAbstractConvergence(IPOGStateRepository repository) throws CoreException, RodinDBException {
		IAbstractEventGuardList abstractEventGuardList = 
			repository.getState(IAbstractEventGuardList.STATE_TYPE);
		List<ISCEvent> abstractEvents = abstractEventGuardList.getAbstractEvents();
		if (abstractEvents.size() == 0) {
			abstractConvergence = null;
			return;
		}
		
		List<Convergence> convergences = new ArrayList<Convergence>(
				abstractEvents.size());
		
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
