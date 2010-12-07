/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added cancellation tests
 *     Systerel - added sequent normalization
 *******************************************************************************/
package org.eventb.pp;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.CancellationChecker;
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
import org.eventb.internal.pp.sequent.InputPredicate;
import org.eventb.internal.pp.sequent.InputSequent;
import org.eventb.internal.pp.sequent.SimpleTracer;
import org.eventb.pp.PPResult.Result;

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

	private static final FormulaFactory ff = FormulaFactory.getDefault();

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

	private final CancellationChecker cancellation;

	private final InputSequent sequent;
	
	private VariableContext context;
	private PredicateTable table;
	private List<Clause> clauses;
	
	private PPResult result;
	
	private ClauseDispatcher proofStrategy;
	
	public PPProof(Predicate[] hypotheses, Predicate goal, IPPMonitor monitor) {
		this.cancellation = CancellationChecker.newChecker(monitor);
		this.sequent = new InputSequent(hypotheses, goal, ff, cancellation);
	}

	public PPProof(Set<Predicate> hypotheses, Predicate goal, IPPMonitor monitor) {
		this.cancellation = CancellationChecker.newChecker(monitor);
		this.sequent = new InputSequent(hypotheses, goal, ff, cancellation);
	}

	public PPProof(Iterable<Predicate> hypotheses, Predicate goal,
			IPPMonitor monitor) {
		this.cancellation = CancellationChecker.newChecker(monitor);
		this.sequent = new InputSequent(hypotheses, goal, ff, cancellation);
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
	
	@Deprecated
	public List<Predicate> getTranslatedHypotheses() {
		return sequent.getTranslatedHypotheses();
	}
	
	@Deprecated
	public Predicate getTranslatedGoal() {
		return sequent.getTranslatedGoal();
	}
	
	/**
	 * Invokes the translator. Translates the original hypotheses
	 * and goal to predicate calculus.
	 */
	public void translate() {
		sequent.translate();
		cancellation.check();
		runHook(translateHook);
	}
	
	/**
	 * Invokes the loader. Transforms the set of hypotheses and the goal
	 * into the CNF required as an input to the prover.
	 */
	public void load() {
		final AbstractContext loadContext = new AbstractContext();
		load(loadContext);
		cancellation.check();
		runHook(loadHook);
	}
	
	private void runHook(Runnable hook) {
		if (hook != null) {
			hook.run();
		}
	}

	protected void load(AbstractContext loadContext){
		for (InputPredicate predicate : sequent.getPredicates()) {
			if (predicate.loadPhaseOne(loadContext)) {
				proofFound(predicate);
				debugResult();
				return;
			}
			cancellation.check();
		}

		final ClauseBuilder cBuilder = new ClauseBuilder(cancellation);
		cBuilder.loadClausesFromContext(loadContext);
		cancellation.check();
		cBuilder.buildPredicateTypeInformation(loadContext);
		cancellation.check();

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
		
		initProver();
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
		if (DEBUG) {
			if (result.getResult() == Result.valid) {
				// debug("** proof found, traced clauses **");
				// debug(getResult().getTracer().getClauses().toString());
				debug("** proof found **");
				if (result.getTracer() instanceof org.eventb.internal.pp.core.Tracer)
					debug("closing clauses: " + ((org.eventb.internal.pp.core.Tracer) result.getTracer()).getClosingOrigins());
				debug("original hypotheses: " + result.getTracer().getNeededHypotheses().toString());
				debug("goal needed: " + result.getTracer().isGoalNeeded());
			} else {
				debug("** no proof found **");
			}
		}
	}
	
	private void initProver() {
		proofStrategy = new ClauseDispatcher(cancellation);
		
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
