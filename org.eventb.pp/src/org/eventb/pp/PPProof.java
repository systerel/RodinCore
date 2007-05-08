package org.eventb.pp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.ProofStrategy;
import org.eventb.internal.pp.core.elements.IClause;
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
		if (DEBUG)
			System.out.println(message);
	}
	
	private List<Predicate> hypotheses; 
	private List<Predicate> transformedHypotheses;
	private Predicate goal;
	private Predicate transformedGoal;
	
	private PPResult result;
	
	private PredicateBuilder pBuilder;
	private ClauseBuilder cBuilder;
	private ProofStrategy proofStrategy;
	
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
		
		Predicate newPredicate = Translator.decomposeIdentifiers(predicate, ff);
		newPredicate = Translator.reduceToPredicateCalulus(newPredicate, ff);
		newPredicate = Translator.simplifyPredicate(newPredicate, ff);
		
		debug("Translated: "+predicate+" to: "+newPredicate);
		return newPredicate;
	}
	
	// sequent must be type-checked
	public void prove(long timeout) {
		if (transformedHypotheses==null) transformedHypotheses = hypotheses;
		if (transformedGoal == null) transformedGoal = goal;
		
		initPredicateBuilder();
		initClauseBuilder();
		
		loadHypotheses();
		loadGoal();
		
		if (result!=null) {
			// proof has been found during translation
		}
		else {
			LoaderResult loaderResult = cBuilder.buildClauses(pBuilder.getContext());
			initProver();
			
			debug("==== Original clauses ====");
			for (IClause clause : loaderResult.getClauses()) {
				debug(clause.toString());
			}
			
			proofStrategy.setClauses(loaderResult.getClauses());
			try {
				proofStrategy.mainLoop(timeout);
				result = proofStrategy.getResult();
			}
			catch (InterruptedException e) {
				result = new PPResult(Result.cancel,null);
			}
			
		}
		debugResult();
	}
	
	private void debugResult() {
		if (result.getResult()==Result.valid) {
//			debug("** proof found, traced clauses **");
//			debug(getResult().getTracer().getClauses().toString());
			debug("** proof found **");
			debug("original hypotheses: "+result.getTracer().getOriginalPredicates().toString());
			debug("goal needed: "+result.getTracer().isGoalNeeded());
		}
		else {
			debug("** no proof found **");
		}
	}
	
	private void loadGoal() {
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
	
	private void loadHypotheses() {
		for (int i=0;i<transformedHypotheses.size();i++) {
			Predicate hypothesis = hypotheses.get(i);
			Predicate transformedHypothesis = transformedHypotheses.get(i);
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
	
	private void initPredicateBuilder() {
		pBuilder = new PredicateBuilder();
	}
	
	private void initClauseBuilder() {
		cBuilder = new ClauseBuilder();
	}
	
	private void initProver() {
		proofStrategy = new ProofStrategy();
		
		PredicateProver prover = new PredicateProver(cBuilder.getVariableContext());
		CaseSplitter casesplitter = new CaseSplitter(cBuilder.getVariableContext());
		SeedSearchProver seedsearch = new SeedSearchProver(cBuilder.getVariableContext());
		EqualityProver equalityprover = new EqualityProver(cBuilder.getVariableContext());
		proofStrategy.setPredicateProver(prover);
		proofStrategy.setCaseSplitter(casesplitter);
		proofStrategy.setSeedSearch(seedsearch);
		proofStrategy.setEqualityProver(equalityprover);
		
		OnePointRule onepoint = new OnePointRule();
		ExistentialSimplifier existential = new ExistentialSimplifier();
		LiteralSimplifier literal = new LiteralSimplifier(cBuilder.getVariableContext());
		EqualitySimplifier equality = new EqualitySimplifier(cBuilder.getVariableContext());
		
		proofStrategy.addSimplifier(onepoint);
		proofStrategy.addSimplifier(equality);
		proofStrategy.addSimplifier(existential);
		proofStrategy.addSimplifier(literal);
	}
	
}
