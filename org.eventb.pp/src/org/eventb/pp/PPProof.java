/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.pp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.PPCore;
import org.eventb.internal.pp.core.ClauseDispatcher;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.PredicateTable;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;
import org.eventb.internal.pp.core.provers.equality.EqualityProver;
import org.eventb.internal.pp.core.provers.extensionality.ExtensionalityProver;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.eventb.internal.pp.core.provers.seedsearch.SeedSearchProver;
import org.eventb.internal.pp.core.simplifiers.EqualitySimplifier;
import org.eventb.internal.pp.core.simplifiers.ExistentialSimplifier;
import org.eventb.internal.pp.core.simplifiers.LiteralSimplifier;
import org.eventb.internal.pp.core.simplifiers.OnePointRule;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.predicate.PredicateBuilder;
import org.eventb.pp.PPResult.Result;
import org.eventb.pptrans.Translator;

// applies the necessary pass one after the other
public class PPProof {

	/**
	 * Debug flag for <code>PROVER_TRACE</code>
	 */
	public static boolean DEBUG = false;
	public static void debug(String message){
		System.out.println(message);
	}
	
	private List<InputPredicate> hypotheses = new ArrayList<InputPredicate>();
	private InputPredicate goal;
	
	private IVariableContext context;
	private PredicateTable table;
	private List<Clause> clauses;
	
	private PPResult result;
	
	private ClauseDispatcher proofStrategy;
	
	public PPProof(Predicate[] hypotheses, Predicate goal) {
		setHypotheses(Arrays.asList(hypotheses));
		this.goal = new InputPredicate(goal,true);
	}
	
	public PPProof(Set<Predicate> hypotheses, Predicate goal) {
		setHypotheses(hypotheses);
		this.goal = new InputPredicate(goal,true);
	}

	public PPProof(Iterable<Predicate> hypotheses, Predicate goal) {
		setHypotheses(hypotheses);
		this.goal = new InputPredicate(goal,true);
	}
	
	private void setHypotheses(Iterable<Predicate> predicates) {
		for (Predicate predicate : predicates) {
			this.hypotheses.add(new InputPredicate(predicate,false));
		}
	}
	
//	public PPProof(List<Clause> clauses) {
//		this.clauses = clauses;
//	}
		
	public PPResult getResult() {
		return result;
	}
	
	private void proofFound(InputPredicate predicate) {
		Tracer tracer;
		if (predicate.isGoal) tracer = new Tracer(true); 
		else tracer = new Tracer(predicate.originalPredicate, false);
		result = new PPResult(Result.valid, tracer);
	}
	
	public Collection<Clause> getClauses() {
		return clauses;
	}
	
	public List<Predicate> getTranslatedHypotheses() {
		List<Predicate> result = new ArrayList<Predicate>();
		for (InputPredicate hypothesis : hypotheses) {
			result.addAll(hypothesis.translatedPredicates);
		}
		return result;
	}
	
	public List<Predicate> getTranslatedGoal() {
		return new ArrayList<Predicate>(goal.translatedPredicates);
	}
	
	public void translate() {
		for (InputPredicate predicate : hypotheses) {
			predicate.deriveUsefulPredicates();
			predicate.translate();
		}
		goal.deriveUsefulPredicates();
		goal.translate();
	}
	
	public void load() {
		PredicateBuilder pBuilder = new PredicateBuilder();
		ClauseBuilder cBuilder = new ClauseBuilder();

		for (InputPredicate predicate : hypotheses) {
			if (predicate.loadPhaseOne(pBuilder)) {
				proofFound(predicate);
				debugResult();
				return;
			}
		}
		if (goal.loadPhaseOne(pBuilder)) {
			proofFound(goal);
			debugResult();
			return;
		}

		cBuilder.loadClausesFromContext(pBuilder.getContext());
		cBuilder.buildPredicateTypeInformation(pBuilder.getContext());

		clauses = cBuilder.getClauses();
		context = cBuilder.getVariableContext();
		table = cBuilder.getPredicateTable();
	}
	
	// sequent must be type-checked
	public void prove(long timeout) {
		if (result != null) return;
		if (context == null) throw new IllegalStateException("Loader must be preliminary invoked");
		
		initProver();
		if (DEBUG) debug("==== Original clauses ====");
		for (Clause clause : clauses) {
			if (DEBUG) debug(clause.toString());
		}

		proofStrategy.setClauses(clauses);
		proofStrategy.mainLoop(timeout);
		result = proofStrategy.getResult();
		
		debugResult();
	}
	
	public void cancel() {
		proofStrategy.cancel();
	}
	
	private void debugResult() {
		if (result.getResult()==Result.valid) {
//			if (DEBUG) debug("** proof found, traced clauses **");
//			if (DEBUG) debug(getResult().getTracer().getClauses().toString());
			if (DEBUG) debug("** proof found **");
			if (result.getTracer() instanceof org.eventb.internal.pp.core.tracing.Tracer) if (DEBUG) debug("closing clauses: "+((org.eventb.internal.pp.core.tracing.Tracer)result.getTracer()).getClosingOrigins());
			if (DEBUG) debug("original hypotheses: "+result.getTracer().getNeededHypotheses().toString());
			if (DEBUG) debug("goal needed: "+result.getTracer().isGoalNeeded());
		}
		else {
			if (DEBUG) debug("** no proof found **");
		}
	}
	
	private static class InputPredicate {
		private static FormulaFactory ff = FormulaFactory.getDefault();

		boolean isGoal;
		Predicate originalPredicate;
		List<Predicate> derivedPredicates;
		List<Predicate> translatedPredicates;
		
		public InputPredicate(Predicate originalPredicate, boolean isGoal) {
			this.originalPredicate = originalPredicate;
			this.isGoal = isGoal;
		}
		
		public boolean loadPhaseOne(PredicateBuilder builder) {
			if (translatedPredicates == null) throw new IllegalStateException("Translator should be invoked first");
			
			for (Predicate translatedPredicate : translatedPredicates) {
				assert translatedPredicate.isTypeChecked();
				
				if (translatedPredicate.getTag() == Formula.BTRUE && isGoal) {
					return true;
				}
				if (translatedPredicate.getTag() == Formula.BFALSE && !isGoal) {
					return true;
				}
				if (translatedPredicate.getTag() == Formula.BTRUE && !isGoal) {
					return false;
				}
				if (translatedPredicate.getTag() == Formula.BFALSE && isGoal) {
					return false;
				}
				builder.build(translatedPredicate, originalPredicate, isGoal);
			}
			return false;
		}
		
		public void translate() {
			this.translatedPredicates = new ArrayList<Predicate>();
			for (Predicate derivedPredicate : derivedPredicates) {
				translate(derivedPredicate);
			}
		}
		
		private void translate(Predicate predicate) {
			assert predicate.isTypeChecked();
			
			Predicate newPredicate;
			newPredicate = Translator.decomposeIdentifiers(predicate, ff);
			newPredicate = Translator.reduceToPredicateCalulus(newPredicate, ff);
			newPredicate = Translator.simplifyPredicate(newPredicate, ff);
			newPredicate = newPredicate.flatten(ff);
			
//			if (!typeCheck(newPredicate)) ProverPlugin.log("Could not type check generated predicate "+newPredicate);
//			else 
			if (newPredicate.isTypeChecked()) {
				translatedPredicates.add(newPredicate);
				if (DEBUG) debug("Translated: "+predicate+" to: "+newPredicate);
			} else {
				PPCore.log("Translator generetad untyped predicate " + newPredicate);
				if (DEBUG) debug("Translator generated untype-checked predicate: "+ newPredicate);
			}
		}
		
//		private boolean typeCheck(Predicate predicate) {
//			ITypeEnvironment env = ff.makeTypeEnvironment();
//			for (FreeIdentifier ident : originalPredicate.getFreeIdentifiers()) {
//				env.add(ident);
//			}
//			ITypeCheckResult result = predicate.typeCheck(env);
//			if (!result.isSuccess()) {
////				System.out.println(result);
//				return false;
//			}
//			return true;
//		}
	
		public void deriveUsefulPredicates() {
			this.derivedPredicates = new ArrayList<Predicate>();
			
//			Predicate setEquivalencePredicate = getSetMembershipEquivalenceForEquality();
//			if (setEquivalencePredicate != null) {
//				if (typeCheck(setEquivalencePredicate)) {
//					derivedPredicates.add(setEquivalencePredicate);
//					if (DEBUG) debug("Adding derived predicate "+setEquivalencePredicate+" for "+originalPredicate);
//				} else {
//					if (DEBUG) debug("Could not type check derived predicate "+setEquivalencePredicate);
//					PPCore.log("Could not type check generated predicate "+setEquivalencePredicate);
//				}
//			}

			if (DEBUG) debug("Keeping original predicate "+originalPredicate);
			derivedPredicates.add(originalPredicate);
		}
		
//		private Predicate getSetMembershipEquivalenceForEquality() {
//			if (isSetEquality(originalPredicate)) {
//				Expression left = getEquality(originalPredicate).getLeft();
//				Expression right = getEquality(originalPredicate).getRight();
//				
//				return 
//				ff.makeQuantifiedPredicate(Formula.FORALL, new BoundIdentDecl[]{ff.makeBoundIdentDecl("x", null)},
//					ff.makeBinaryPredicate(Formula.LEQV, 
//						ff.makeRelationalPredicate(Formula.IN, ff.makeBoundIdentifier(0, null), left, null), 
//						ff.makeRelationalPredicate(Formula.IN, ff.makeBoundIdentifier(0, null), right, null), 
//					null),
//				null);
//			}
//			return null;
//		}
		
//		private RelationalPredicate getEquality(Predicate predicate) {
//			return (RelationalPredicate)predicate;
//		}
		
//		private boolean isSetEquality(Predicate predicate) {
//			if (predicate.getTag() == Formula.EQUAL) {
//				RelationalPredicate equality = getEquality(predicate);
//				if (((equality.getLeft()).getType().toExpression(ff).getTag() == Formula.POW)) {
//					return true;
//				}
//			}
//			return false;
//		}
	}
	
	private static class Tracer implements ITracer {
		private List<Predicate> originalPredicates = new ArrayList<Predicate>();
		private boolean goalNeeded;
		
		public Tracer(Predicate originalPredicate, boolean goalNeeded) {
			this.originalPredicates.add(originalPredicate);
			this.goalNeeded = goalNeeded;
		}
		
		public Tracer(boolean goalNeeded) {
			this.goalNeeded = goalNeeded;
		}

		public List<Predicate> getNeededHypotheses() {
			return originalPredicates;
		}

		public boolean isGoalNeeded() {
			return goalNeeded;
		}
	}
	
	private void initProver() {
		proofStrategy = new ClauseDispatcher();
		
		PredicateProver prover = new PredicateProver(context);
		CaseSplitter casesplitter = new CaseSplitter(context, proofStrategy);
		SeedSearchProver seedsearch = new SeedSearchProver(context);
		EqualityProver equalityprover = new EqualityProver(context);
		ExtensionalityProver extensionalityProver = new ExtensionalityProver(table, context);
		proofStrategy.addProver(prover);
		proofStrategy.addProver(casesplitter);
		proofStrategy.addProver(seedsearch);
		proofStrategy.addProver(equalityprover);
		proofStrategy.addProver(extensionalityProver);
		
		OnePointRule onepoint = new OnePointRule();
		ExistentialSimplifier existential = new ExistentialSimplifier(context);
		LiteralSimplifier literal = new LiteralSimplifier(context);
		EqualitySimplifier equality = new EqualitySimplifier(context);
		proofStrategy.addSimplifier(onepoint);
		proofStrategy.addSimplifier(equality);
		proofStrategy.addSimplifier(existential);
		proofStrategy.addSimplifier(literal);
	}
	
}
