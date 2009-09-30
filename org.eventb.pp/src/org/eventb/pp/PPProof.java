/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added cancellation tests
 *******************************************************************************/
package org.eventb.pp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.PPCore;
import org.eventb.internal.pp.core.ClauseDispatcher;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateTable;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
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
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.pp.PPResult.Result;
import org.eventb.pptrans.Translator;

/**
 * This class is responsible for running PP on a given set
 * of hypotheses and a goal.
 * <p>
 * Methods in this class must be called in a certain order. If the
 * original set of hypotheses and the goal are in set theory, {@link #translate()}
 * must first be invoked to translate them into predicate calculus. Then,
 * the loader must be invoked using {@link #load()} and then the prover is
 * invoked using {@link #prove(long)}. 
 * Once the prover is done, the result of the proof can be retrieved by using
 * {@link #getResult()}. To cancel a proof, method {@link #cancel()}.
 *
 * @author François Terrier
 *
 * @since 0.2
 */
public class PPProof {

	/**
	 * Debug flag for <code>PROVER_TRACE</code>
	 */
	public static boolean DEBUG = false;
	public static void debug(String message){
		System.out.println(message);
	}
	
	/**
	 * Hook codes to be run at the end of each phase.
	 * 
	 * Reserved for testing purpose only.
	 */
	public static Runnable translateHook = null;
	public static Runnable loadHook = null;
	
	private final IPPMonitor monitor;
	
	private List<InputPredicate> hypotheses = new ArrayList<InputPredicate>();
	private InputPredicate goal;
	
	private VariableContext context;
	private PredicateTable table;
	private List<Clause> clauses;
	
	private PPResult result;
	
	private ClauseDispatcher proofStrategy;
	
	public PPProof(Predicate[] hypotheses, Predicate goal, IPPMonitor monitor) {
		setHypotheses(Arrays.asList(hypotheses));
		this.goal = new InputPredicate(goal,true);
		this.monitor = monitor;
	}

	public PPProof(Set<Predicate> hypotheses, Predicate goal, IPPMonitor monitor) {
		setHypotheses(hypotheses);
		this.goal = new InputPredicate(goal,true);
		this.monitor = monitor;
	}

	public PPProof(Iterable<Predicate> hypotheses, Predicate goal,
			IPPMonitor monitor) {
		setHypotheses(hypotheses);
		this.goal = new InputPredicate(goal,true);
		this.monitor = monitor;
	}
	
	private void setHypotheses(Iterable<Predicate> predicates) {
		for (Predicate predicate : predicates) {
			this.hypotheses.add(new InputPredicate(predicate,false));
		}
	}
	
	private void checkCancellation() {
		if (monitor != null && monitor.isCanceled()) {
			throw new CancellationException();
		}
	}
		
	/**
	 * Returns the result of this proof.
	 * 
	 * @return the result of this proof
	 */
	public PPResult getResult() {
		return result;
	}
	
	private void proofFound(InputPredicate predicate) {
		final SimpleTracer tracer = new SimpleTracer(predicate);
		result = new PPResult(Result.valid, tracer);
	}
	
	public Collection<Clause> getClauses() {
		return clauses;
	}
	
	public List<Predicate> getTranslatedHypotheses() {
		List<Predicate> result = new ArrayList<Predicate>();
		for (InputPredicate hypothesis : hypotheses) {
			result.add(hypothesis.getTranslatedPredicate());
		}
		return result;
	}
	
	public Predicate getTranslatedGoal() {
		return goal.getTranslatedPredicate();
	}
	
	/**
	 * Invokes the translator. Translates the original hypotheses
	 * and goal to predicate calculus.
	 */
	public void translate() {
		for (InputPredicate predicate : hypotheses) {
			predicate.translate();
			checkCancellation();
		}
		goal.translate();
		checkCancellation();
		runHook(translateHook);
	}
	
	/**
	 * Invokes the loader. Transforms the set of hypotheses and the goal
	 * into the CNF required as an input to the prover.
	 */
	public void load() {
		final AbstractContext loadContext = new AbstractContext();
		load(loadContext);
		checkCancellation();
		runHook(loadHook);
	}
	
	private void runHook(Runnable hook) {
		if (hook != null) {
			hook.run();
		}
	}

	protected void load(AbstractContext loadContext){
		for (InputPredicate predicate : hypotheses) {
			if (predicate.loadPhaseOne(loadContext)) {
				proofFound(predicate);
				debugResult();
				return;
			}
			checkCancellation();
		}
		if (goal.loadPhaseOne(loadContext)) {
			proofFound(goal);
			debugResult();
			return;
		}
		checkCancellation();

		final ClauseBuilder cBuilder = new ClauseBuilder(monitor);
		cBuilder.loadClausesFromContext(loadContext);
		checkCancellation();
		cBuilder.buildPredicateTypeInformation(loadContext);
		checkCancellation();

		clauses = cBuilder.getClauses();
		context = cBuilder.getVariableContext();
		table = cBuilder.getPredicateTable();
	}

	/**
	 * Invokes the prover. Tries to prove the current sequent
	 * in maximum maxSteps steps. Also, the prover will stop when the 
	 * monitor indicates that it has been canceled.
	 * 
	 * @param maxSteps
	 *            maximal number of steps, or <code>-1</code> to denote an
	 *            infinite number
	 */
	public void prove(long maxSteps) {
		if (result != null) return;
		if (context == null) throw new IllegalStateException("Loader must be preliminary invoked");
		
		initProver(monitor);
		if (DEBUG) {
			debug("==== Original clauses ====");
			for (Clause clause : clauses) {
				debug(clause.toString());
			}
		}

		proofStrategy.setClauses(clauses);
		proofStrategy.mainLoop(maxSteps);
		result = proofStrategy.getResult();
		
		debugResult();
	}
	
	private void debugResult() {
		if (result.getResult()==Result.valid) {
//			if (DEBUG) debug("** proof found, traced clauses **");
//			if (DEBUG) debug(getResult().getTracer().getClauses().toString());
			if (DEBUG) debug("** proof found **");
			if (result.getTracer() instanceof org.eventb.internal.pp.core.Tracer) if (DEBUG) debug("closing clauses: "+((org.eventb.internal.pp.core.Tracer)result.getTracer()).getClosingOrigins());
			if (DEBUG) debug("original hypotheses: "+result.getTracer().getNeededHypotheses().toString());
			if (DEBUG) debug("goal needed: "+result.getTracer().isGoalNeeded());
		}
		else {
			if (DEBUG) debug("** no proof found **");
		}
	}
	
	private static class InputPredicate {

		private static final FormulaFactory ff = FormulaFactory.getDefault();

		private static Predicate translate(Predicate predicate) {
			assert predicate.isTypeChecked();

			Predicate newPredicate;
			newPredicate = Translator.decomposeIdentifiers(predicate, ff);
			newPredicate = Translator
					.reduceToPredicateCalulus(newPredicate, ff);
			newPredicate = Translator.simplifyPredicate(newPredicate, ff);
			newPredicate = newPredicate.flatten(ff);

			if (newPredicate.isTypeChecked()) {
				if (DEBUG) {
					debug("Translated: " + predicate + " to: " + newPredicate);
				}
				return newPredicate;
			}
			if (DEBUG) {
				debug("Translator generated untype-checked predicate: "
						+ newPredicate);
			}
			PPCore.log("Translator generetad untyped predicate "
							+ newPredicate);
			return null;
		}

		final boolean isGoal;
		final Predicate originalPredicate;
		private Predicate translatedPredicate;

		public InputPredicate(Predicate originalPredicate, boolean isGoal) {
			this.originalPredicate = originalPredicate;
			this.isGoal = isGoal;
		}

		public boolean loadPhaseOne(AbstractContext loadContext) {
			if (translatedPredicate == null) {
				throw new IllegalStateException(
						"Translator should be invoked first");
			}
			assert translatedPredicate.isTypeChecked();

			if (translatedPredicate.getTag() == Predicate.BTRUE) {
				return isGoal;
			}
			if (translatedPredicate.getTag() == Predicate.BFALSE) {
				return !isGoal;
			}
			loadContext.load(translatedPredicate, originalPredicate, isGoal);
			return false;
		}

		public void translate() {
			translatedPredicate = translate(originalPredicate);
		}

		public Predicate getTranslatedPredicate() {
			return translatedPredicate;
		}
	}
	
	private static final class SimpleTracer implements ITracer {
		private final Predicate hypothesis;
		private final boolean goalNeeded;
		
		public SimpleTracer(InputPredicate ip) {
			if (ip.isGoal) {
				this.hypothesis = null;
				this.goalNeeded = true;
			} else {
				this.hypothesis = ip.originalPredicate;
				this.goalNeeded = false;
			}
		}

		public List<Predicate> getNeededHypotheses() {
			if (hypothesis != null) {
				return Collections.singletonList(hypothesis);
			}
			return Collections.emptyList();
		}

		public boolean isGoalNeeded() {
			return goalNeeded;
		}
	}
	
	private void initProver(IPPMonitor monitor) {
		proofStrategy = new ClauseDispatcher(monitor);
		
		PredicateProver prover = new PredicateProver(context);
		CaseSplitter casesplitter = new CaseSplitter(context, proofStrategy.getLevelController());
		SeedSearchProver seedsearch = new SeedSearchProver(context, proofStrategy.getLevelController());
		EqualityProver equalityprover = new EqualityProver(context);
		ExtensionalityProver extensionalityProver = new ExtensionalityProver(table, context);
		proofStrategy.addProverModule(prover);
		proofStrategy.addProverModule(casesplitter);
		proofStrategy.addProverModule(seedsearch);
		proofStrategy.addProverModule(equalityprover);
		proofStrategy.addProverModule(extensionalityProver);
		
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
