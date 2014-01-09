/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added a constructor with IReasonerDesc
 *     Systerel - added unselected hypotheses
 *     Systerel - added used reasoners to proof dependencies
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.DLib;

// order of stored hypotheses is preserved by using a LinkedHashSet
public class ProofRule extends ReasonerOutput implements IProofRule {
	
	private static final Set<Predicate> NO_HYPS = Collections.emptySet();
	private static final IAntecedent[] NO_ANTECEDENTS = new IAntecedent[0];

	public static class Antecedent implements IAntecedent {
		
		private final FreeIdentifier[] addedFreeIdentifiers;
		private final Set<Predicate> addedHypotheses;
		private final Set<Predicate> unselectedAddedHyps;
		private final List<IHypAction> hypActions;
		private final Predicate goal;
		
		private static final FreeIdentifier[] NO_FREE_IDENTS = new FreeIdentifier[0];
		private static final ArrayList<IHypAction> NO_HYP_ACTIONS = new ArrayList<IHypAction>();
		
		public Antecedent(Predicate goal, Set<Predicate> addedHyps,
				Set<Predicate> unselectedAddedHyps,
				FreeIdentifier[] addedFreeIdents, List<IHypAction> hypAction) {
			this.goal = goal;
			this.addedHypotheses = addedHyps == null ? NO_HYPS : new LinkedHashSet<Predicate>(addedHyps);
			this.unselectedAddedHyps = unselectedAddedHyps == null ? NO_HYPS : new HashSet<Predicate>(unselectedAddedHyps);
			this.addedFreeIdentifiers = addedFreeIdents == null ? NO_FREE_IDENTS : addedFreeIdents.clone();
			this.hypActions = hypAction == null ? NO_HYP_ACTIONS : new ArrayList<IHypAction>(hypAction);
			checkPreconditions();
		}
		
		private void checkPreconditions() {
			if (!addedHypotheses.containsAll(unselectedAddedHyps)) {
				throw new IllegalArgumentException(
						"unselected added hyps should be a subset of added hyps"
								+ "\nadded: " + addedHypotheses
								+ "\nunselected: " + unselectedAddedHyps);
			}
			
		}

		/**
		 * @return Returns the addedFreeIdentifiers.
		 */
		public final FreeIdentifier[] getAddedFreeIdents() {
			return addedFreeIdentifiers;
		}

		/**
		 * @return Returns the hypAction.
		 */
		public final List<IHypAction> getHypActions() {
			return hypActions;
		}

		/**
		 * @return Returns the addedHypotheses.
		 */
		public final Set<Predicate> getAddedHyps() {
			return Collections.unmodifiableSet(addedHypotheses);
		}

		/**
		 * @return Returns the unselectedAddedHyps.
		 */
		public Set<Predicate> getUnselectedAddedHyps() {
			return Collections.unmodifiableSet(unselectedAddedHyps);
		}
		
		/**
		 * @return Returns the goal.
		 */
		public final Predicate getGoal() {
			return goal;
		}
		
		private IProverSequent genSequent(IProverSequent seq, Predicate goalInstantiation){
			
			// newGoal not required
			Predicate newGoal;
			if (goal == null)
			{
				// Check for ill formed rule
				if (goalInstantiation == null) return null;
				newGoal = goalInstantiation;
			}
			else
			{
				newGoal = goal;
			}
			
			IInternalProverSequent result = ((IInternalProverSequent) seq)
					.modify(addedFreeIdentifiers, addedHypotheses,
							unselectedAddedHyps, newGoal);
			// not strictly needed
			if (result == null) return null;
			result = ProofRule.performHypActions(hypActions,result);
			// no change if seq == result
			return result;
			
		}

		@Override
		public IAntecedent translate(FormulaFactory factory) {
			final Predicate trGoal = goal == null ? null : goal.translate(factory);
			
			final FreeIdentifier[] trAddedFreeIdentifiers = new FreeIdentifier[addedFreeIdentifiers.length];
			for (int i = 0; i < addedFreeIdentifiers.length; i++) {
				trAddedFreeIdentifiers[i] = (FreeIdentifier) addedFreeIdentifiers[i].translate(factory);
			}
			
			final Set<Predicate> trAddedHypotheses = new LinkedHashSet<Predicate>(addedHypotheses.size());
			for (Predicate addedHyp : addedHypotheses) {
				trAddedHypotheses.add(addedHyp.translate(factory));
			}
			
			final Set<Predicate> trUnselectedAddedHyps = new LinkedHashSet<Predicate>(unselectedAddedHyps.size());
			for (Predicate unselectedAddedHyp : unselectedAddedHyps) {
				trUnselectedAddedHyps.add(unselectedAddedHyp.translate(factory));
			}
			
			final List<IHypAction> trHypActions = new ArrayList<IHypAction>(hypActions.size());
			for (IHypAction hypAction : hypActions) {
				trHypActions.add(hypAction.translate(factory));
			}
			
			return new Antecedent(trGoal, trAddedHypotheses,
					trUnselectedAddedHyps, trAddedFreeIdentifiers, trHypActions);
		}
		
	}
	
	private static FormulaFactory findFormulaFactory(
			Collection<? extends Formula<?>> formulas) {
		if (formulas != null && !formulas.isEmpty()) {
			return formulas.iterator().next().getFactory();
		}
		return null;
	}
	
	private static FormulaFactory findFormulaFactory(Predicate goal,
			Set<Predicate> neededHyps, IAntecedent[] antecedents) {
		if (goal != null) {
			return goal.getFactory();
		}
		
		FormulaFactory factory;
		factory = findFormulaFactory(neededHyps);
		if (factory != null) return factory;
		
		for (IAntecedent antecedent : antecedents) {
			final Predicate anteGoal = antecedent.getGoal();
			if (anteGoal != null) {
				return anteGoal.getFactory();
			}
			factory = findFormulaFactory(antecedent.getAddedHyps());
			if (factory != null) return factory;
			
			factory = findFormulaFactory(antecedent.getUnselectedAddedHyps());
			if (factory != null) return factory;
			
			factory = findFormulaFactory(asList(antecedent.getAddedFreeIdents()));
			if (factory != null) return factory;
			
			for (IHypAction hypAction : antecedent.getHypActions()) {
				if (hypAction instanceof ISelectionHypAction) {
					final ISelectionHypAction select = (ISelectionHypAction) hypAction;
					factory = findFormulaFactory(select.getHyps());
					if (factory != null) return factory;
				} else if (hypAction instanceof IForwardInfHypAction) {
					final IForwardInfHypAction fwd = (IForwardInfHypAction) hypAction;
					factory = findFormulaFactory(fwd.getHyps());
					if (factory != null) return factory;

					factory = findFormulaFactory(fwd.getInferredHyps());
					if (factory != null) return factory;

					factory = findFormulaFactory(asList(fwd.getAddedFreeIdents()));
					if (factory != null) return factory;
				}
			}
		}
		throw new IllegalArgumentException(
				"Formula factory not found in proof rule.");
	}
	
	private final FormulaFactory factory;
	private final String display;
	private final IAntecedent[] antecedents;
	private final Set<Predicate> neededHypotheses;
	private final Predicate goal;
	private final int reasonerConfidence;
	
	public ProofRule(IReasoner generatedBy, IReasonerInput generatedUsing, Predicate goal, Set<Predicate> neededHyps, Integer confidence, String display, IAntecedent[] antecedents) {
		super(generatedBy,generatedUsing);
		
		this.factory = findFormulaFactory(goal, neededHyps, antecedents);
		this.goal = goal;
		this.antecedents = antecedents == null ? NO_ANTECEDENTS : antecedents.clone();
		this.neededHypotheses = neededHyps == null ? NO_HYPS : new LinkedHashSet<Predicate>(neededHyps);
		this.reasonerConfidence = confidence == null ? IConfidence.DISCHARGED_MAX : confidence;
		this.display = display == null ? generatedBy.getReasonerID() : display;		
	}

	public ProofRule(IReasonerDesc generatedBy, IReasonerInput generatedUsing,
			Predicate goal, Set<Predicate> neededHyps, Integer confidence,
			String display, IAntecedent[] antecedents) {
		super(generatedBy,generatedUsing);
		
		this.factory = findFormulaFactory(goal, neededHyps, antecedents);
		this.goal = goal;
		this.antecedents = antecedents == null ? NO_ANTECEDENTS : antecedents.clone();
		this.neededHypotheses = neededHyps == null ? NO_HYPS : new LinkedHashSet<Predicate>(neededHyps);
		this.reasonerConfidence = confidence == null ? IConfidence.DISCHARGED_MAX : confidence;
		this.display = display == null ? generatedBy.getId() : display;	
	}

	public FormulaFactory getFormulaFactory() {
		return factory;
	}
	
	public String getDisplayName() {
		return display;
	}

	public String getRuleID() {
		return getReasonerDesc().getId();
	}

	public int getConfidence() {
		return reasonerConfidence;
	}

	public IProverSequent[] apply(IProverSequent seq) {
		// Check if all the needed hyps are there
		if (! seq.containsHypotheses(neededHypotheses))
			return null;
		// Check if the goal null, or identical to the sequent.
		if ( goal!=null && ! goal.equals(seq.goal()) ) return null;
		
		// in case the goal is null, keep track of the sequent goal.
		Predicate goalInstantiation = null;
		if (goal == null)
			goalInstantiation = seq.goal();
		
		// Generate new antecedents
		IProverSequent[] anticidents 
			= new IProverSequent[antecedents.length];
		for (int i = 0; i < anticidents.length; i++) {
			anticidents[i] = ((Antecedent) antecedents[i]).genSequent(seq, goalInstantiation);
			if (anticidents[i] == null)
				// most probably a name clash occured
				// or the rule is ill formed
				// or an invalid type env.
				// add renaming/refactoring code here
				return null;
		}
		
		return anticidents;
	}

	public Set<Predicate> getNeededHyps() {
		return neededHypotheses;
	}

	public Predicate getGoal() {
		return goal;
	}

	public IAntecedent[] getAntecedents() {
		return antecedents;
	}

	
	public ProofDependenciesBuilder processDeps(ProofDependenciesBuilder[] subProofsDeps){
		assert antecedents.length == subProofsDeps.length;

		ProofDependenciesBuilder proofDeps = new ProofDependenciesBuilder();
		
		// the singular goal dependency
		Predicate depGoal = null;
		
		// process each antecedent
		for (int i = 0; i < antecedents.length; i++) {

			final IAntecedent antecedent = antecedents[i];
			final ProofDependenciesBuilder subProofDeps = subProofsDeps[i];
			
			// Process the antecedent
			processHypActionDeps(antecedent.getHypActions(), subProofDeps);
			
			subProofDeps.getUsedHypotheses().removeAll(antecedent.getAddedHyps());
			if (antecedent.getGoal()!=null)
				subProofDeps.getUsedFreeIdents().addAll(Arrays.asList(antecedent.getGoal().getFreeIdentifiers()));
			for (Predicate hyp : antecedent.getAddedHyps())
				subProofDeps.getUsedFreeIdents().addAll(Arrays.asList(hyp.getFreeIdentifiers()));
			for (FreeIdentifier freeIdent : antecedent.getAddedFreeIdents()){
				subProofDeps.getUsedFreeIdents().remove(freeIdent);
				subProofDeps.getIntroducedFreeIdents().add(freeIdent.getName());			
			}
						
			// Combine this information
			proofDeps.getUsedHypotheses().addAll(subProofDeps.getUsedHypotheses());
			proofDeps.getUsedFreeIdents().addAll(subProofDeps.getUsedFreeIdents());
			proofDeps.getIntroducedFreeIdents().addAll(subProofDeps.getIntroducedFreeIdents());
			
			// update depGoal
			// in case the antecedent is the variable goal, and the proof above it has
			// an instantiation for it, make it the new depGoal
			if (antecedent.getGoal() == null && subProofDeps.getGoal() != null){
				// Check for non-equal instantiations of the goal
				assert (depGoal == null || depGoal.equals(subProofDeps.getGoal()));
				depGoal = subProofDeps.getGoal();
			}
			
			// add used reasoners
			proofDeps.getUsedReasoners().addAll(
					subProofDeps.getUsedReasoners());
		}
		
		if (goal != null){	
			// goal is explicitly stated
			depGoal = goal;
		}
			
		proofDeps.setGoal(depGoal);
		proofDeps.getUsedHypotheses().addAll(neededHypotheses);	
		if (depGoal!=null) proofDeps.getUsedFreeIdents().addAll(Arrays.asList(depGoal.getFreeIdentifiers()));
		for (Predicate hyp : neededHypotheses)
			proofDeps.getUsedFreeIdents().addAll(Arrays.asList(hyp.getFreeIdentifiers()));
		
		proofDeps.getUsedReasoners().add(reasonerDesc);
		return proofDeps;
	}
	
	
	private static IInternalProverSequent performHypActions(List<IHypAction> hypActions,IInternalProverSequent seq){
		if (hypActions == null) return seq;
		IInternalProverSequent result = seq;
		for(IHypAction action : hypActions){
			result = ((IInternalHypAction) action).perform(result);
			if (result == null)
				return null;
		}
		return result;
	}
	
	private static void processHypActionDeps(List<IHypAction> hypActions,ProofDependenciesBuilder proofDeps){
		int length = hypActions.size();
		for (int i = length-1; i >= 0; i--) {
			((IInternalHypAction)hypActions.get(i)).processDependencies(proofDeps);
		}
	}

	@Override
	public IProofRule translate(FormulaFactory factory) {
		final Predicate trGoal = goal == null ? null : goal.translate(factory);
		final Set<Predicate> trNeededHyps = new LinkedHashSet<Predicate>();
		for(Predicate neededHyp : neededHypotheses) {
			trNeededHyps.add(neededHyp.translate(factory));
		}
		final IAntecedent[] trAntecedents = new IAntecedent[antecedents.length];
		for (int i = 0; i < antecedents.length; i++) {
			trAntecedents[i] = antecedents[i].translate(factory);
		}
		
		final IReasonerInput trGeneratedUsing;
		if (generatedUsing instanceof ITranslatableReasonerInput) {
			trGeneratedUsing = ((ITranslatableReasonerInput) generatedUsing)
					.translate(factory);
		} else {
			trGeneratedUsing = generatedUsing;
		}
		
		return new ProofRule(reasonerDesc, trGeneratedUsing, trGoal,
				trNeededHyps, reasonerConfidence, display, trAntecedents);
	}

	/*
	 * Returns the hypotheses that are acted upon by some antecedent of this
	 * rule. In other terms, the hypotheses returned are the ones that are
	 * needed for the given rule to apply fully, although they are not required
	 * to apply this rule.
	 */
	private Set<Predicate> actedHyps() {
		final Set<Predicate> result = new LinkedHashSet<Predicate>();
		for (final IAntecedent antecedent : getAntecedents()) {
			for (final IHypAction action : antecedent.getHypActions()) {
				final IInternalHypAction act = (IInternalHypAction) action;
				result.addAll(act.getHyps());
			}
		}
		return result;
	}
	
	private Predicate makeGoal() {
		return goal == null ? DLib.False(factory) : goal;
	}

	private ITypeEnvironment getTypeEnvironment() {
		final ITypeEnvironmentBuilder typeEnv = factory.makeTypeEnvironment();
		if (goal != null) {
			typeEnv.addAll(goal.getFreeIdentifiers());
		}
		for (Predicate hyp : neededHypotheses) {
			typeEnv.addAll(hyp.getFreeIdentifiers());
		}
		if (generatedUsing instanceof ITranslatableReasonerInput) {
			typeEnv.addAll(((ITranslatableReasonerInput) generatedUsing)
					.getTypeEnvironment(factory));
		}
		return typeEnv.makeSnapshot();
	}
	
	public IProverSequent makeSequent() {
		final ITypeEnvironment typenv = getTypeEnvironment();
		final Predicate goal = makeGoal();
		final Set<Predicate> hyps = new LinkedHashSet<Predicate>();
		hyps.addAll(getNeededHyps());
		hyps.addAll(actedHyps());
		return ProverFactory.makeSequent(typenv, hyps, null, hyps, goal);

	}
}
