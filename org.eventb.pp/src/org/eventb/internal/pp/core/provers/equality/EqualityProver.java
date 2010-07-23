/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.equality;

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
import org.eventb.internal.pp.core.IProverModule;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.inferrers.EqualityInferrer;
import org.eventb.internal.pp.core.inferrers.EqualityInstantiationInferrer;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;


/**
 * This module is responsible for handling equality predicates.
 * <p>
 * It does the following :
 * <ul>
 * <li>From constant unit equality clauses - also called facts - it derives contradictions. As
 * soon as a contradiction is found, it is returned.</li>
 * <li>From constant equality literals in non-unit clauses - called queries - it decides whether
 * the equality literal is true, false or unsolved. As soon as a query is solved, the 
 * corresponding clause is genereted.
 * <li>From non-constant equality literals, it tries to find values suitable for
 * instantiation.</li> 
 * </ul>
 *
 * @author François Terrier
 *
 */
public final class EqualityProver implements IProverModule {

	/**
	 * Debug flag for <code>PROVER_EQUALITY_TRACE</code>
	 */
	public static boolean DEBUG = false;
	public static void debug(String message){
		if (DEBUG)
			System.out.println(message);
	}
	
	private final IEquivalenceManager manager = new EquivalenceManager();
	private final EqualityInferrer inferrer;
	private final EqualityInstantiationInferrer instantiationInferrer;
	private final Vector<Set<Clause>> nonDispatchedClauses;
	
	public EqualityProver(VariableContext context) {
		this.inferrer = new EqualityInferrer(context);
		this.instantiationInferrer = new EqualityInstantiationInferrer(context);
		this.nonDispatchedClauses = new Vector<Set<Clause>>();
	}
	
	@Override
	public ProverResult contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies) {
		manager.backtrack(newLevel);
		backtrackClauses(newLevel);
		
		return ProverResult.EMPTY_RESULT;
	}

	private void backtrackClauses(Level newLevel) {
		for (Iterator<Set<Clause>> iter = nonDispatchedClauses.iterator(); iter.hasNext();) {
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
	
	private static final int INIT = 20;
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
		// return equality instantiations here, if not, it loops
		if (nonDispatchedClauses.isEmpty()) return ProverResult.EMPTY_RESULT;
		Set<Clause> result = nonDispatchedClauses.remove(0);
		if (DEBUG) debug("EqualityProver :"+result);
		return new ProverResult(result, new HashSet<Clause>());
	}

	@Override
	public void registerDumper(Dumper dumper) {
		dumper.addObject("EqualityFormula table", manager);
	}

	/**
	 * The prover result contains:
	 * <ul>
	 * <li>all generated unit clauses</li>
	 * <li>all false or true clauses</li>
	 * </ul>
	 * 
	 * @see org.eventb.internal.pp.core.IProverModule#addClauseAndDetectContradiction(org.eventb.internal.pp.core.elements.Clause)
	 */
	@Override
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		Set<Clause> generatedClauses = new HashSet<Clause>();
		Set<Clause> subsumedClauses = new HashSet<Clause>();
		Set<Clause> instantiationClauses = new HashSet<Clause>();
		
		addClause(clause, generatedClauses, subsumedClauses, instantiationClauses);
		splitClauses(generatedClauses, instantiationClauses);
		
		return new ProverResult(generatedClauses, subsumedClauses);
	}
	
	private void splitClauses(Set<Clause> generatedClauses, Set<Clause> instantiationClauses) {
		Set<Clause> clauses = new HashSet<Clause>();
		for (Iterator<Clause> iterator = generatedClauses.iterator(); iterator.hasNext();) {
			Clause clause = iterator.next();
			if (clause.isFalse()) continue;
			if (clause.isTrue()) continue;
			if (clause.isUnit()) continue;
			else {
				iterator.remove();
				clauses.add(clause);
			}
		}
		clauses.addAll(instantiationClauses);
		if (!clauses.isEmpty()) nonDispatchedClauses.add(clauses);
	}
	
	@Override
	public void removeClause(Clause clause) {
		if (clause.isUnit()) return;
		
		for (EqualityLiteral equality : clause.getEqualityLiterals()) {
			if (equality.isConstant()) manager.removeQueryEquality(equality, clause);
			else if (isInstantiationCandidate(equality)) manager.removeInstantiation(equality, clause);
		}
		for (EqualityLiteral equality : clause.getConditions()) {
			if (equality.isConstant()) manager.removeQueryEquality(equality, clause);
			else if (isInstantiationCandidate(equality)) manager.removeInstantiation(equality, clause);
		}
	}
	
	private boolean isInstantiationCandidate(EqualityLiteral equality) {
		if ((equality.getTerm(0) instanceof Variable && equality.getTerm(1) instanceof Constant)
				||	(equality.getTerm(1) instanceof Variable && equality.getTerm(0) instanceof Constant))
			return true;
		return false;
	}
	
	private void addClause(Clause clause, Set<Clause> generatedClauses,
			Set<Clause> subsumedClauses, Set<Clause> instantiationClauses) {
		if (clause.isUnit() && (clause.getEqualityLiteralsSize()>0 || clause.getConditionsSize()>0)) {
			EqualityLiteral equality = null;
			if (clause.getConditionsSize()==1) equality = clause.getCondition(0);
			else equality = clause.getEqualityLiteral(0);
			
			if (!equality.isConstant()) {
				// TODO handle this case, x = a or x = y
				return;
			}
			
			IFactResult result = manager.addFactEquality(equality, clause);
			handleFactResult(result, generatedClauses, subsumedClauses, instantiationClauses);
		}
		else if (clause.getEqualityLiteralsSize()>0 || clause.getConditionsSize()>0) {
			ArrayList<IQueryResult> queryResult = new ArrayList<IQueryResult>();
			ArrayList<IInstantiationResult> instantiationResult = new ArrayList<IInstantiationResult>();

			// if equivalence, then we do the standard instantiations
			// x=a -> x/a, x\=a -> x/a
			if (clause.isEquivalence())
				doTrivialInstantiations(clause, generatedClauses, subsumedClauses);
			
			handleEqualityList(clause.getEqualityLiterals(), clause,
					queryResult, instantiationResult, !clause.isEquivalence());
			handleEqualityList(clause.getConditions(), clause,
					queryResult, instantiationResult, true);
			handleQueryResult(queryResult, generatedClauses, subsumedClauses);
			handleInstantiationResult(instantiationResult, instantiationClauses);
		}
	}

	private void handleEqualityList(List<EqualityLiteral> equalityList, Clause clause,
			List<IQueryResult> queryResult, List<IInstantiationResult> instantiationResult,
			boolean handleOnlyPositives) {
		for (EqualityLiteral equality : equalityList) {
			if (equality.isConstant()) {
				IQueryResult result = manager.addQueryEquality(equality, clause);
				if (result != null) queryResult.add(result);
			}
			else if (handleOnlyPositives?equality.isPositive():true) {
				if (isInstantiationCandidate(equality)) {
					List<? extends IInstantiationResult> result = manager.addInstantiationEquality(equality, clause);
					if (result != null) instantiationResult.addAll(result);
					
				}
			}
			// TODO handle other cases x = a or x = y or #x.x=y etc ...
		}
	}
	
	private void doTrivialInstantiations(Clause clause,
			Set<Clause> generatedClauses, Set<Clause> subsumedClauses) {
		for (EqualityLiteral equality : clause.getEqualityLiterals()) {
			if (isInstantiationCandidate(equality)) {
				Constant constant = null;
				if (equality.getTerm(0) instanceof Constant) constant = (Constant)equality.getTerm(0);
				else if (equality.getTerm(1) instanceof Constant) constant = (Constant)equality.getTerm(1);
				instantiationInferrer.addEqualityEqual(equality, constant);
				
				clause.infer(instantiationInferrer);
				Clause inferredClause = instantiationInferrer.getResult();
				
				generatedClauses.add(inferredClause);
			}
		}
	}

	private void handleFactResult(IFactResult result,
			Set<Clause> generatedClauses, Set<Clause> subsumedClauses,
			Set<Clause> instantiationClauses) {
		if (result == null) return;
		if (result.hasContradiction()) {
			List<Clause> contradictionOrigin = result.getContradictionOrigin();
			IOrigin origin = new ClauseOrigin(contradictionOrigin);
			generatedClauses.add(ClauseFactory.getDefault().makeFALSE(origin));
		}
		else {
			if (result.getSolvedQueries() != null) handleQueryResult(result.getSolvedQueries(), generatedClauses, subsumedClauses);
			if (result.getSolvedInstantiations() != null) handleInstantiationResult(result.getSolvedInstantiations(), instantiationClauses);
		}
	}
	
	private <T> void addToList(Map<Clause, Set<T>> values, Clause clause, T equality) {
		if (!values.containsKey(clause)) {
			Set<T> equalities = new HashSet<T>();
			values.put(clause, equalities);
		}
		values.get(clause).add(equality);
	}
	
	private void handleInstantiationResult(List<? extends IInstantiationResult> result,
			Set<Clause> generatedClauses) {
		if (result == null) return;
		for (IInstantiationResult insRes : result) {
			for (Clause clause : insRes.getSolvedClauses()) {
				instantiationInferrer.addEqualityUnequal(insRes.getEquality(), insRes.getInstantiationValue());
				instantiationInferrer.addParentClauses(new ArrayList<Clause>(insRes.getSolvedValueOrigin()));
				clause.infer(instantiationInferrer);
				Clause inferredClause = instantiationInferrer.getResult();
				
				generatedClauses.add(inferredClause);
			}
		}
	}
	
	private void handleQueryResult(List<? extends IQueryResult> result,
			Set<Clause> generatedClauses, Set<Clause> subsumedClauses) {
		if (result == null) return;
		Map<Clause, Set<EqualityLiteral>> trueValues = new HashMap<Clause, Set<EqualityLiteral>>();
		Map<Clause, Set<EqualityLiteral>> falseValues = new HashMap<Clause, Set<EqualityLiteral>>();
		Map<Clause, Set<Clause>> clauses = new HashMap<Clause, Set<Clause>>();
		
		// take into account the level of the clause
		// -> done by the prover
		for (IQueryResult queryResult : result) {
			Map<Clause, Set<EqualityLiteral>> map = queryResult.getValue()?trueValues:falseValues;
			for (Clause clause : queryResult.getSolvedClauses()) {
				for (Clause originClause : queryResult.getSolvedValueOrigin()) {
					addToList(clauses, clause, originClause);
				}
				addToList(map, clause, queryResult.getEquality());
			}
		}
		
		for (Entry<Clause, Set<Clause>> entry : clauses.entrySet()) {
			if (trueValues.containsKey(entry.getKey())) {
				for (EqualityLiteral equality : trueValues.get(entry.getKey())) {
					inferrer.addEquality(equality, true);
				}
			}
			if (falseValues.containsKey(entry.getKey())) {
				for (EqualityLiteral equality : falseValues.get(entry.getKey())) {
					inferrer.addEquality(equality, false);
				}
			}
			inferrer.addParentClauses(new ArrayList<Clause>(entry.getValue()));
			entry.getKey().infer(inferrer);
			Clause inferredClause = inferrer.getResult();
			generatedClauses.add(inferredClause);
			if (inferredClause.getLevel().compareTo(entry.getKey().getLevel()) <= 0) 
				subsumedClauses.add(entry.getKey());
		}
	}

	@Override
	public String toString() {
		return "EqualityProver";
	}

}
