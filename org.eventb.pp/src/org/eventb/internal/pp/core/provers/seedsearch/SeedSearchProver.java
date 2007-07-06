package org.eventb.internal.pp.core.provers.seedsearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.inferrers.InstantiationInferrer;

public class SeedSearchProver implements IProver {

	/**
	 * Debug flag for <code>PROVER_SEEDSEARCH_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
		if (DEBUG)
			System.out.println(message);
	}

	private SeedSearchManager manager = new SeedSearchManager();
	private ClauseSimplifier simplifier;
	private InstantiationInferrer inferrer;
	
	public SeedSearchProver(IVariableContext context) {
		this.inferrer = new InstantiationInferrer(context);
	}
	
	private Stack<Set<Clause>> generatedClausesStack = new Stack<Set<Clause>>();
	
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		List<SeedSearchResult> results = addArbitraryClause(clause);
		Set<Clause> instantiatedClauses = new HashSet<Clause>();
		for (SeedSearchResult result : results) {
			Clause instantiatedClause = doInstantiation(result);
			// we run the simplifier, since it is an instantiation, it is not possible
			// to get a smaller clause than the original, given the fact that the
			// original clause has been simplified
			instantiatedClause = simplifier.run(instantiatedClause);
			instantiatedClauses.add(instantiatedClause);
		}
		generatedClausesStack.add(instantiatedClauses);
		return null;
	}
	
	private Clause doInstantiation(SeedSearchResult result) {
		PredicateLiteral literal = result.getInstantiableClause().getPredicateLiterals().get(result.getPredicatePosition());
		Variable variable = (Variable)literal.getTerms().get(result.getPosition());
		inferrer.addInstantiation(variable, result.getConstant());
		result.getInstantiableClause().infer(inferrer);
		return inferrer.getResult();
	}

	private List<SeedSearchResult> addArbitraryClause(Clause clause) {
		// TODO optimize
		List<SeedSearchResult> result = new ArrayList<SeedSearchResult>();
		if (clause.isBlockedOnConditions()) return result;
		for (int i = 0; i < clause.getPredicateLiterals().size(); i++) {
			PredicateLiteral literal1 = clause.getPredicateLiterals().get(i);

			// equivalence clauses for constants
			if (clause.isEquivalence() && !clause.isUnit()) { 
				result.addAll(manager.addConstant(literal1.getInverse().getDescriptor(), literal1.getInverse().getTerms(), clause));
				result.addAll(manager.addConstant(literal1.getDescriptor(), literal1.getTerms(), clause));
			}
			else {
				result.addAll(manager.addConstant(literal1.getDescriptor(), literal1.getTerms(), clause));
			}
			
			if (literal1.isQuantified()/* && clause.isUnit() */) { 
				if (clause.isEquivalence()) {
					result.addAll(manager.addInstantiable(literal1.getDescriptor(), literal1.getTerms(), i, clause));
					result.addAll(manager.addInstantiable(literal1.getInverse().getDescriptor(), literal1.getInverse().getTerms(), i, clause));
				}
				else {
					result.addAll(manager.addInstantiable(literal1.getDescriptor(), literal1.getTerms(), i, clause));
				}
			}
			
			for (int j = i+1; j < clause.getPredicateLiterals().size(); j++) {
				PredicateLiteral literal2 = clause.getPredicateLiterals().get(j);
				if (clause.isEquivalence() && clause.sizeWithoutConditions()==2) {
					result.addAll(manager.addVariableLink(literal1.getDescriptor(), literal2.getInverse().getDescriptor(), 
							literal1.getTerms(), literal2.getInverse().getTerms(), clause));
					result.addAll(manager.addVariableLink(literal1.getInverse().getDescriptor(), literal2.getDescriptor(),
							literal1.getInverse().getTerms(), literal2.getTerms(), clause));
				}
				else if (clause.isEquivalence()) {
					result.addAll(manager.addVariableLink(literal1.getDescriptor(), literal2.getInverse().getDescriptor(), 
							literal1.getTerms(), literal2.getInverse().getTerms(), clause));
					result.addAll(manager.addVariableLink(literal1.getInverse().getDescriptor(), literal2.getDescriptor(),
							literal1.getInverse().getTerms(), literal2.getTerms(), clause));
					result.addAll(manager.addVariableLink(literal1.getDescriptor(), literal2.getDescriptor(),
							literal1.getTerms(), literal2.getTerms(), clause));
					result.addAll(manager.addVariableLink(literal1.getInverse().getDescriptor(), literal2.getInverse().getDescriptor(),
							literal1.getInverse().getTerms(), literal2.getInverse().getTerms(), clause));
				}
				else {
					result.addAll(manager.addVariableLink(literal1.getDescriptor(), literal2.getDescriptor(),
							literal1.getTerms(), literal2.getTerms(), clause));
				}
			}
		}
		return result;
	}
	
	public void contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies) {
		// do nothing, we let the removeClause() do the job
		for (Iterator<Set<Clause>> iter = generatedClausesStack.iterator(); iter.hasNext();) {
			Set<Clause> clauses = iter.next();
			for (Iterator<Clause> iter2 = clauses.iterator(); iter2.hasNext();) {
				Clause clause = iter2.next();
				if (newLevel.isAncestorOf(clause.getLevel())) {
					iter2.remove();
					if (clauses.isEmpty()) iter.remove();
				}
			}
		}
	}

	public void initialize(ClauseSimplifier simplifier) {
		this.simplifier = simplifier;
	}

	public boolean isSubsumed(Clause clause) {
		return false;
	}

	public ProverResult next() {
		if (generatedClausesStack.isEmpty()) return null;
		Set<Clause> nextClauses = generatedClausesStack.pop();
		ProverResult result = new ProverResult(nextClauses,new HashSet<Clause>());
		debug("SeedSearchProver, next clauses: "+nextClauses+", remaining clauses: "+generatedClausesStack.size());
		return result;
	}

	public void registerDumper(Dumper dumper) {
		dumper.addObject("SeedSearch table", manager);
	}

	public void removeClause(Clause clause) {
		manager.removeClause(clause);
	}
	
	@Override
	public String toString() {
		return "SeedSearchProver";
	}

}
