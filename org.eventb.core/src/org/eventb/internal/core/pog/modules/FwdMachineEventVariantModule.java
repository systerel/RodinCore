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

import static java.util.Arrays.asList;
import static org.eventb.core.IConvergenceElement.Convergence.ANTICIPATED;
import static org.eventb.core.IConvergenceElement.Convergence.CONVERGENT;
import static org.eventb.core.IConvergenceElement.Convergence.ORDINARY;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LT;
import static org.eventb.core.ast.Formula.NATURAL;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
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
	@SuppressWarnings("synthetic-access")
	private class Info {
		// Index in machineVariantInfo
		public final int index;

		// Variant source object
		public final IRodinElement source;

		// Before expression
		public final Expression expression;

		// Is this an integer variant
		public final boolean isNatural;

		// After expression
		public final Expression nextExpression;

		public Info(int index) throws RodinDBException {
			this.index = index;
			this.source = machineVariantInfo.getVariant(index).getSource();
			this.expression = machineVariantInfo.getExpression(index);
			this.isNatural = expression.getType() instanceof IntegerType;
			this.nextExpression = getAfterExpression(expression);
		}

		public boolean isUnchanged() {
			return nextExpression.equals(expression);
		}

		public IPOGPredicate getVarGoal(boolean strict) {
			final int tag;
			if (isNatural) {
				tag = strict ? LT : LE;
			} else {
				tag = strict ? SUBSET : SUBSETEQ;
			}
			
			final Predicate pred;
			pred = factory.makeRelationalPredicate(tag, nextExpression, expression, null);
			return makePredicate(pred, source);
		}

		public IPOGPredicate getNatGoal() {
			final Expression nat = factory.makeAtomicExpression(NATURAL, null);
			final Predicate pred;
			pred = factory.makeRelationalPredicate(IN, expression, nat, null);
			return makePredicate(pred, source);
		}

		public IPOGPredicate getEqHyp() {
			final Predicate pred;
			pred = factory.makeRelationalPredicate(EQUAL, nextExpression, expression, null);
			return makePredicate(pred, source);
		}
	}

	/**
	 * Allows to accumulate local hypotheses that are shared between several variant
	 * proof obligations.
	 * 
	 * We could have alternatively used a POPredicateSet but this would generate
	 * twice as many sets as variants and I am not sure we would have more compact
	 * PO files, given that we do not expect to have that many local hypotheses.
	 */
	private class HypAccumulator {

		// Hyps accumulated so far
		private final List<IPOGPredicate> hyps;

		// Free identifier for which before-after predicates have already been added
		private final Set<FreeIdentifier> idents;

		public HypAccumulator() {
			this.hyps = new ArrayList<>();
			this.idents = new HashSet<>();
		}

		// Returns the hypotheses accumulated so far.
		public List<IPOGPredicate> getHyps() {
			return hyps;
		}

		// Adds the before-after predicates for the primed variables of the given
		// predicate.
		public void addBAPredicates(Predicate target) throws RodinDBException {
			final FreeIdentifier[] targetIdents = target.getFreeIdentifiers();
			final Set<FreeIdentifier> newIdents = new HashSet<>(asList(targetIdents));

			// Add only the BA predicates for the new primed variables.
			newIdents.removeAll(idents);
			makeActionHypothesis(hyps, newIdents);
			idents.addAll(newIdents);
		}

		public void addEqHyp(Info info) {
			hyps.add(info.getEqHyp());
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
		final boolean isConvergent = concreteConvergence == CONVERGENT;

		final List<Info> infos = new LinkedList<>();
		infos.add(new Info(0));

		final List<IPOGSource> sourceList = new ArrayList<>();
		sourceList.add(makeSource(IPOSource.DEFAULT_ROLE, concreteEvent.getSource()));

		final HypAccumulator hypAccumulator = new HypAccumulator();

		final Iterator<Info> iter = infos.iterator();
		while (iter.hasNext()) {
			final Info info = iter.next();

			// Remove the variant if it is not modified by this event, except the
			// last one for a convergent event.
			if (info.isUnchanged() && (!isConvergent || iter.hasNext())) {
				iter.remove();
				continue;
			}
			
			sourceList.add(makeSource(IPOSource.DEFAULT_ROLE, info.source));
			final IPOGSource[] sources = new IPOGSource[sourceList.size()];
			sourceList.toArray(sources);
			
			if (info.isNatural && isConvergent) {
				String sequentNameNAT = machineVariantInfo.getPOName(info.index, concreteEventLabel, "NAT");
				createPO(
						target, 
						sequentNameNAT, 
						IPOGNature.EVENT_VARIANT_IN_NAT, 
						eventHypothesisManager.getFullHypothesis(), 
						hypAccumulator.getHyps(), 
						info.getNatGoal(), 
						sources, 
						new IPOGHint[] {
								getLocalHypothesisSelectionHint(target, sequentNameNAT)
							}, 
						accurate,
						monitor);
			}

			IPOGPredicate varGoal = info.getVarGoal(isConvergent);
			hypAccumulator.addBAPredicates(varGoal.getPredicate());
			String sequentNameVAR = machineVariantInfo.getPOName(info.index, concreteEventLabel, "VAR");
			createPO(
					target, 
					sequentNameVAR, 
					IPOGNature.EVENT_VARIANT, 
					eventHypothesisManager.getFullHypothesis(), 
					hypAccumulator.getHyps(), 
					varGoal, 
					sources, 
					new IPOGHint[] {
							getLocalHypothesisSelectionHint(target, sequentNameVAR)
						}, 
					accurate,
					monitor);

			if (iter.hasNext()) {
				hypAccumulator.addEqHyp(info);
			}
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
