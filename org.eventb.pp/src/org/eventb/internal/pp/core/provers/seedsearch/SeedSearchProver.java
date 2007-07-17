package org.eventb.internal.pp.core.provers.seedsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.inferrers.InstantiationInferrer;

public class SeedSearchProver implements IProver {

	/**
	 * Debug flag for <code>PROVER_SEEDSEARCH_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
		System.out.println(message);
	}

	private static final int ARBITRARY_SEARCH = 2;
	
	private double currentNumberOfArbitrary = 0;
	private double currentCounter = ARBITRARY_SEARCH;

	private SeedSearchManager manager = new SeedSearchManager();
	private ClauseSimplifier simplifier;
	private InstantiationInferrer inferrer;
	private IVariableContext context;
	
	public SeedSearchProver(IVariableContext context) {
		this.context = context;
		this.inferrer = new InstantiationInferrer(context);
	}
	
	private Vector<Set<Clause>> generatedClausesStack = new Vector<Set<Clause>>();
	
	
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		List<SeedSearchResult> results = addArbitraryClause(clause);
		Set<Clause> instantiatedClauses = new HashSet<Clause>();
		for (SeedSearchResult result : results) {
			Clause instantiatedClause = doInstantiation(result);
			if (instantiatedClause != null) {
				instantiatedClauses.add(instantiatedClause);
			}
		}
		if (!instantiatedClauses.isEmpty()) generatedClausesStack.add(instantiatedClauses);
		return null;
	}
	
	private Clause simplify(Clause clause){
		// we run the simplifier, since it is an instantiation, it is not possible
		// to get a smaller clause than the original, given the fact that the
		// original clause has been simplified
		return simplifier.run(clause);
	}
	
	private Map<Clause, Map<Variable, Set<Constant>>> instantiationMaps = new HashMap<Clause, Map<Variable,Set<Constant>>>();
	
	private boolean checkAndAddInstantiation(Clause clause, Variable variable, Constant constant) {
		Map<Variable, Set<Constant>> instantiationMap = instantiationMaps.get(clause);
		if (instantiationMap == null) {
			instantiationMap = new HashMap<Variable, Set<Constant>>();
			instantiationMaps.put(clause, instantiationMap);
		}
		Set<Constant> constants = instantiationMap.get(variable);
		if (constants == null) {
			constants = new HashSet<Constant>();
			instantiationMap.put(variable, constants);
		}
		if (constants.contains(constant)) return true;
		else {
			constants.add(constant);
			return false;
		}
	}
	
	private Clause doInstantiation(SeedSearchResult result) {
		PredicateLiteral literal = result.getInstantiableClause().getPredicateLiterals().get(result.getPredicatePosition());
		Variable variable = (Variable)literal.getTerms().get(result.getPosition());
		if (checkAndAddInstantiation(result.getInstantiableClause(), variable, result.getConstant())) return null;
		inferrer.addInstantiation(variable, result.getConstant());
		result.getInstantiableClause().infer(inferrer);
		return simplify(inferrer.getResult());
	}

	private List<SeedSearchResult> addArbitraryClause(Clause clause) {
		// TODO optimize
		List<SeedSearchResult> result = new ArrayList<SeedSearchResult>();
		if (clause.isBlockedOnConditions()) return result;
		for (int i = 0; i < clause.getPredicateLiterals().size(); i++) {
			PredicateLiteral literal1 = clause.getPredicateLiterals().get(i);

			// equivalence clauses for constants
			if (clause.isEquivalence()) { 
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
		
		for (Iterator<Entry<Clause,Map<Variable, Set<Constant>>>> iter = instantiationMaps.entrySet().iterator(); iter.hasNext();) {
			Entry<Clause,?> element = iter.next();
			if (newLevel.isAncestorOf(element.getKey().getLevel())) iter.remove();
		}
	}

	public void initialize(ClauseSimplifier simplifier) {
		this.simplifier = simplifier;
	}

	public boolean isSubsumed(Clause clause) {
		return false;
	}

	private void resetCounter() {
		this.currentCounter = ARBITRARY_SEARCH * Math.pow(2, currentNumberOfArbitrary);
	}
	
	private boolean checkAndUpdateCounter() {
		currentCounter--;
		if (currentCounter == 0) {
			currentNumberOfArbitrary++;
			resetCounter();
			return true;
		}
		return false;
	}
	
	private Clause nextArbitraryInstantiation() {
		Clause nextClause = null;
		if (checkAndUpdateCounter()) {
			SeedSearchResult result = manager.getArbitraryInstantiation(context);
			if (result == null) return null;
			nextClause = doInstantiation(result);
		}
		return nextClause;
	}
	
	public ProverResult next() {
		Set<Clause> nextClauses = new HashSet<Clause>();
		while (nextClauses.isEmpty()) {
			if (generatedClausesStack.isEmpty()) {
				Clause nextClause = nextArbitraryInstantiation();
				if (nextClause == null) return null;
				else nextClauses.add(nextClause);
			}
			else {
				nextClauses.addAll(generatedClausesStack.remove(0));
			}
		}
		ProverResult result = new ProverResult(nextClauses,new HashSet<Clause>());
		if (DEBUG) debug("SeedSearchProver, next clauses: "+nextClauses+", remaining clauses: "+generatedClausesStack.size());
		return result;
	}

	public void registerDumper(Dumper dumper) {
		dumper.addObject("SeedSearch table", manager);
	}

	public void removeClause(Clause clause) {
		manager.removeClause(clause);
		
		if (instantiationMaps.containsKey(clause)) {
			for (Iterator<Entry<Clause,Map<Variable, Set<Constant>>>> iter = instantiationMaps.entrySet().iterator(); iter.hasNext();) {
				Entry<Clause,?> element = iter.next();
				if (element.getKey().equalsWithLevel(clause)) iter.remove();
			}
		}
	}
	
	@Override
	public String toString() {
		return "SeedSearchProver";
	}

}
