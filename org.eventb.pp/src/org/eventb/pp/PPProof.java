package org.eventb.pp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.ClauseDispatcher;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;
import org.eventb.internal.pp.core.provers.equality.EqualityProver;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.eventb.internal.pp.core.provers.seedsearch.SeedSearchProver;
import org.eventb.internal.pp.core.simplifiers.EqualitySimplifier;
import org.eventb.internal.pp.core.simplifiers.ExistentialSimplifier;
import org.eventb.internal.pp.core.simplifiers.LiteralSimplifier;
import org.eventb.internal.pp.core.simplifiers.OnePointRule;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LoaderResult;
import org.eventb.internal.pp.loader.clause.VariableContext;
import org.eventb.internal.pp.loader.predicate.PredicateBuilder;
import org.eventb.pp.PPResult.Result;
import org.eventb.pptrans.Translator;

// applies the necessary pass one after the other
public class PPProof {

	/**
	 * Debug flag for <code>PROVER_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
		System.out.println(message);
	}
	
	private List<Predicate> hypotheses; 
	private List<Predicate> transformedHypotheses;
	private Predicate goal;
	private Predicate transformedGoal;
	
	private Set<Clause> clauses;
	
	private PPResult result;
	
//	private PredicateBuilder pBuilder;
//	private ClauseBuilder cBuilder;
	private ClauseDispatcher proofStrategy;
	
	public PPProof(Predicate[] hypotheses, Predicate goal) {
		this.hypotheses = Arrays.asList(hypotheses);
		this.goal = goal;
	}
	
	public PPProof(Set<Predicate> hypotheses, Predicate goal) {
		this.hypotheses = new ArrayList<Predicate>(hypotheses);
		this.goal = goal;
	}

	public PPProof(Iterable<Predicate> hypotheses, Predicate goal) {
		this.hypotheses = new ArrayList<Predicate>();
		for (Predicate predicate : hypotheses) {
			this.hypotheses.add(predicate);
		}
		this.goal = goal;
	}
	
	public PPProof(Set<Clause> clauses) {
		this.clauses = clauses;
	}
	
	public PPResult getResult() {
		return result;
	}
	
	public void translate() {
		transformedHypotheses = new ArrayList<Predicate>();
		for (Predicate hypothesis : hypotheses) {
			transformedHypotheses.add(translate(hypothesis));
		}
		this.transformedGoal = translate(goal);
	}
	
	private static FormulaFactory ff = FormulaFactory.getDefault();
	private Predicate translate(Predicate predicate) {
		assert predicate.isTypeChecked();
		
		Predicate newPredicate;
		
		newPredicate = Translator.decomposeIdentifiers(predicate, ff);
		newPredicate = Translator.reduceToPredicateCalulus(newPredicate, ff);
		newPredicate = Translator.simplifyPredicate(newPredicate, ff);

		if (DEBUG) debug("Translated: "+predicate+" to: "+newPredicate);
		return newPredicate;
	}
	
	// sequent must be type-checked
	public void prove(long timeout) {
		if (transformedHypotheses==null) transformedHypotheses = hypotheses;
		if (transformedGoal == null) transformedGoal = goal;
		
		IVariableContext context;
		if (clauses == null) {
			PredicateBuilder pBuilder = new PredicateBuilder();
			ClauseBuilder cBuilder = new ClauseBuilder();
		
			loadHypotheses(pBuilder);
			loadGoal(pBuilder);
		
			if (result!=null) {
				// proof has been found during translation
				debugResult();
				return;
			}
			cBuilder.buildClauses(pBuilder.getContext());
			cBuilder.buildPredicateTypeInformation(pBuilder.getContext());
			LoaderResult loaderResult = cBuilder.getResult();
			
			clauses = loaderResult.getClauses();
			context = cBuilder.getVariableContext();
		}
		else {
			context = new VariableContext();
		}
		
		initProver(context);
		if (DEBUG) debug("==== Original clauses ====");
		for (Clause clause : clauses) {
			if (DEBUG) debug(clause.toString());
		}

		proofStrategy.setClauses(clauses);
		try {
			proofStrategy.mainLoop(timeout);
			result = proofStrategy.getResult();
		}
		catch (InterruptedException e) {
			result = new PPResult(Result.cancel,null);
		}
		debugResult();
	}
	
	private void debugResult() {
		if (result.getResult()==Result.valid) {
//			if (DEBUG) debug("** proof found, traced clauses **");
//			if (DEBUG) debug(getResult().getTracer().getClauses().toString());
			if (DEBUG) debug("** proof found **");
			if (result.getTracer() instanceof org.eventb.internal.pp.core.tracing.Tracer) if (DEBUG) debug("closing clauses: "+((org.eventb.internal.pp.core.tracing.Tracer)result.getTracer()).getClosingOrigins());
			if (DEBUG) debug("original hypotheses: "+result.getTracer().getOriginalPredicates().toString());
			if (DEBUG) debug("goal needed: "+result.getTracer().isGoalNeeded());
		}
		else {
			if (DEBUG) debug("** no proof found **");
		}
	}
	
	private void loadGoal(PredicateBuilder pBuilder) {
		transformedGoal = transformedGoal.flatten(ff);
		if (transformedGoal.getTag() == Formula.BTRUE) {
			// proof done
			result = new PPResult(Result.valid,new Tracer(true)); 
		}
		else if (transformedGoal.getTag() == Formula.BFALSE) {
			// ignore
		}
		else {
			pBuilder.build(transformedGoal, goal, true);
		}
	}
	
	
	
	private void loadHypotheses(PredicateBuilder pBuilder) {
		for (int i=0;i<transformedHypotheses.size();i++) {
			Predicate hypothesis = hypotheses.get(i);
			Predicate transformedHypothesis = transformedHypotheses.get(i).flatten(ff);
			if (transformedHypothesis.getTag() == Formula.BTRUE) {
				// ignore
			}
			else if (transformedHypothesis.getTag() == Formula.BFALSE) {
				// proof done
				result = new PPResult(Result.valid,new Tracer(hypothesis, false)); 
			}
			else {
				pBuilder.build(transformedHypothesis, hypothesis, false);
			}
		}
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

		public List<Predicate> getOriginalPredicates() {
			return originalPredicates;
		}

		public boolean isGoalNeeded() {
			return goalNeeded;
		}
	}
	
	
	private void initProver(IVariableContext context) {
		proofStrategy = new ClauseDispatcher();
		
		PredicateProver prover = new PredicateProver(context);
		CaseSplitter casesplitter = new CaseSplitter(context, proofStrategy);
		SeedSearchProver seedsearch = new SeedSearchProver(context);
		EqualityProver equalityprover = new EqualityProver(context);
		proofStrategy.setPredicateProver(prover);
		proofStrategy.setCaseSplitter(casesplitter);
		proofStrategy.setSeedSearch(seedsearch);
		proofStrategy.setEqualityProver(equalityprover);
		
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
