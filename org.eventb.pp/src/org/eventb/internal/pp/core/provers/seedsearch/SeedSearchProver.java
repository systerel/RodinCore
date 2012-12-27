/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.provers.seedsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.ILevelController;
import org.eventb.internal.pp.core.IProverModule;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.inferrers.InstantiationInferrer;

public class SeedSearchProver implements IProverModule {

	/**
	 * Debug flag for <code>PROVER_SEEDSEARCH_TRACE</code>
	 */
	public static boolean DEBUG = false;
	public static void debug(String message){
		System.out.println(message);
	}

	private SeedSearchManager manager = new SeedSearchManager();
	private InstantiationInferrer inferrer;
	private VariableContext context;
	private ILevelController levelController;
	
	public SeedSearchProver(VariableContext context, ILevelController levelController) {
		this.context = context;
		this.levelController = levelController;
		this.inferrer = new InstantiationInferrer(context);
	}
	
	private Vector<Set<Clause>> generatedClausesStack = new Vector<Set<Clause>>();
	
	@Override
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		if (accept(clause)) {
			List<SeedSearchResult> results = addArbitraryClause(clause);
			
			// TODO testing 
//			results.addAll(addEqualityClause(clause));
			
			Set<Clause> instantiatedClauses = new HashSet<Clause>();
			for (SeedSearchResult result : results) {
				Clause instantiatedClause = doInstantiation(result);
				if (instantiatedClause != null) {
					instantiatedClauses.add(instantiatedClause);
				}
			}
			if (!instantiatedClauses.isEmpty()) generatedClausesStack.add(instantiatedClauses);
		}
		return ProverResult.EMPTY_RESULT;
	}
	
	private boolean accept(Clause clause) {
		if (clause.hasConditions()) return false;
		return true;
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
		PredicateLiteral literal = result.getInstantiableClause().getPredicateLiteral(result.getPredicatePosition());
		Variable variable = (Variable)literal.getTerm(result.getPosition());
		if (checkAndAddInstantiation(result.getInstantiableClause(), variable, result.getConstant())) return null;
		inferrer.addInstantiation(variable, result.getConstant());
		result.getInstantiableClause().infer(inferrer);
		Clause clause = inferrer.getResult();
		assert clause.getLevel().isAncestorInSameTree(levelController.getCurrentLevel()) || clause.getLevel().equals(levelController.getCurrentLevel());
		return clause;
	}
	
	private void addConstants(Clause clause, PredicateLiteral literal1, List<SeedSearchResult> result) {
		// equivalence clauses for constants
		if (clause.isEquivalence()) { 
			PredicateLiteral inverse = literal1.getInverse();
			result.addAll(manager.addConstant(inverse.getDescriptor(), inverse.isPositive(), inverse.getTerms(), clause));
			result.addAll(manager.addConstant(literal1.getDescriptor(), literal1.isPositive(), literal1.getTerms(), clause));
		}
		else {
			result.addAll(manager.addConstant(literal1.getDescriptor(), literal1.isPositive(), literal1.getTerms(), clause));
		}
	}
	
	private void addInstantiable(Clause clause, PredicateLiteral literal1, int position, List<SeedSearchResult> result) {
		// we do not instantiate definitions with the seed search module
		if (clause.getOrigin().isDefinition()) return;
		
		if (literal1.isQuantified()/* && clause.isUnit() */) { 
			for (int i = 0; i < literal1.getTermsSize(); i++) {
				SimpleTerm term = literal1.getTerm(i);
				if (!term.isConstant()) {
					if (clause.isEquivalence()) {
						result.addAll(manager.addInstantiable(literal1.getDescriptor(), literal1.isPositive(), position, literal1.getTerms(), i, clause));
						PredicateLiteral inverse = literal1.getInverse();
						result.addAll(manager.addInstantiable(inverse.getDescriptor(), inverse.isPositive(), position, inverse.getTerms(), i, clause));
					}
					else {
						result.addAll(manager.addInstantiable(literal1.getDescriptor(), literal1.isPositive(), position, literal1.getTerms(), i, clause));
					}
				}
			}
		}
	}
	
	private void addVariableLink(Clause clause, PredicateLiteral literal1, PredicateLiteral literal2, List<SeedSearchResult> result) {
		PredicateLiteral inverse1 = literal1.getInverse();
		PredicateLiteral inverse2 = literal2.getInverse();
		if (clause.isEquivalence() && clause.sizeWithoutConditions()==2) {
			result.addAll(manager.addVariableLink(
					literal1.getDescriptor(), literal1.isPositive(),
					inverse2.getDescriptor(), inverse2.isPositive(), 
					literal1.getTerms(), inverse2.getTerms(), clause));
			result.addAll(manager.addVariableLink(inverse1.getDescriptor(), inverse1.isPositive(), 
					literal2.getDescriptor(), literal2.isPositive(),
					inverse1.getTerms(), literal2.getTerms(), clause));
		}
		else if (clause.isEquivalence()) {
			result.addAll(manager.addVariableLink(literal1.getDescriptor(), literal1.isPositive(),
					inverse2.getDescriptor(), inverse2.isPositive(),
					literal1.getTerms(), inverse2.getTerms(), clause));
			result.addAll(manager.addVariableLink(inverse1.getDescriptor(), inverse1.isPositive(), 
					literal2.getDescriptor(), literal2.isPositive(),
					literal1.getInverse().getTerms(), literal2.getTerms(), clause));
			result.addAll(manager.addVariableLink(literal1.getDescriptor(), literal1.isPositive(), 
					literal2.getDescriptor(), literal2.isPositive(),
					literal1.getTerms(), literal2.getTerms(), clause));
			result.addAll(manager.addVariableLink(inverse1.getDescriptor(), inverse1.isPositive(), 
					inverse2.getDescriptor(), inverse2.isPositive(), 
					inverse1.getTerms(), inverse2.getTerms(), clause));
		}
		else {
			result.addAll(manager.addVariableLink(literal1.getDescriptor(), literal1.isPositive(), 
					literal2.getDescriptor(), literal2.isPositive(),
					literal1.getTerms(), literal2.getTerms(), clause));
		}
	}

	private List<SeedSearchResult> addArbitraryClause(Clause clause) {
		// TODO optimize
		List<SeedSearchResult> result = new ArrayList<SeedSearchResult>();
		if (clause.hasConditions()) return result;
		for (int i = 0; i < clause.getPredicateLiteralsSize(); i++) {
			PredicateLiteral literal1 = clause.getPredicateLiteral(i);

			addConstants(clause, literal1, result);
			addInstantiable(clause, literal1, i, result);
			
			for (int j = i+1; j < clause.getPredicateLiteralsSize(); j++) {
				PredicateLiteral literal2 = clause.getPredicateLiteral(j);
				addVariableLink(clause, literal1, literal2, result);
			}
		}
		return result;
	}
	
	@SuppressWarnings("unused")
	private List<SeedSearchResult> addEqualityClause(Clause clause) {
		List<SeedSearchResult> result = new ArrayList<SeedSearchResult>();
		for (EqualityLiteral equality : clause.getEqualityLiterals()) {
			if (!equality.getTerm1().isConstant() && !equality.getTerm2().isConstant()) {
				Variable variable1 = (Variable)equality.getTerm1();
				Variable variable2 = (Variable)equality.getTerm2();
				for (int i = 0; i<clause.getPredicateLiteralsSize();i++) {
					PredicateLiteral predicate = clause.getPredicateLiteral(i);
					if (predicate.getTerms().contains(variable1) || predicate.getTerms().contains(variable2)) {
						for (int j = 0; j < predicate.getTermsSize(); j++) {
							SimpleTerm term = predicate.getTerm(j);
							if (term == variable1 || term == variable2) {
								result.addAll(manager.addInstantiable(predicate.getDescriptor(), predicate.isPositive(), i, predicate.getTerms(), j, clause));
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public ProverResult contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies) {
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
		
		return ProverResult.EMPTY_RESULT;
	}

	private static final int ARBITRARY_SEARCH = 1;
	private double currentNumberOfArbitrary = 0;
	private double currentCounter = ARBITRARY_SEARCH;
	
	private void resetCounter() {
		this.currentCounter = ARBITRARY_SEARCH * Math.pow(2, currentNumberOfArbitrary);
	}
	
	private boolean checkAndUpdateCounter() {
		currentCounter--;
		if (currentCounter > 0) {
			return false;
		}
		else {
			currentNumberOfArbitrary++;
			resetCounter();
			return true;
		}
	}
	
	private List<Clause> nextArbitraryInstantiation() {
		List<Clause> result = new ArrayList<Clause>();
		List<SeedSearchResult> seedSearchResults = manager.getArbitraryInstantiation(context);
		for (SeedSearchResult seedSearchResult : seedSearchResults) {
			Clause nextClause = doInstantiation(seedSearchResult);
			result.add(nextClause);
		}
		return result;
	}
	
	private static final int INIT = 10;
	private int counter = INIT;
	private boolean isNextAvailable() {
		if (counter > 0) {
			counter--;
			return false;
		}
		else {
			counter = INIT;
			return true;
		}
	}
	
	@Override
	public ProverResult next(boolean force) {
		if (!force && !isNextAvailable()) return ProverResult.EMPTY_RESULT; 
		
		Set<Clause> nextClauses = new HashSet<Clause>();
		if (!generatedClausesStack.isEmpty()) nextClauses.addAll(generatedClausesStack.remove(0));
		if (checkAndUpdateCounter() || force) {
			List<Clause> clauses = nextArbitraryInstantiation();
			nextClauses.addAll(clauses);
			if (DEBUG) debug("SeedSearchProver, arbitrary instantiation: "+clauses);
		}
		
		ProverResult result = new ProverResult(nextClauses,new HashSet<Clause>());
		if (DEBUG) debug("SeedSearchProver, next clauses: "+nextClauses+", remaining clauses: "+generatedClausesStack.size());
		return result;
	}

	@Override
	public void registerDumper(Dumper dumper) {
		dumper.addObject("SeedSearch table", manager);
	}

	@Override
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
