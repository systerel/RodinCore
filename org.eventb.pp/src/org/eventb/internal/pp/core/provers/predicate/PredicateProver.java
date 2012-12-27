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
package org.eventb.internal.pp.core.provers.predicate;


import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IProverModule;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;
import org.eventb.internal.pp.core.provers.predicate.iterators.IteratorMatchIterable;
import org.eventb.internal.pp.core.provers.predicate.iterators.UnitMatchIterable;
import org.eventb.internal.pp.core.provers.predicate.iterators.UnitMatcher;
import org.eventb.internal.pp.core.search.RandomAccessList;
import org.eventb.internal.pp.core.search.ResetIterator;
import org.eventb.internal.pp.core.tracing.AbstractInferrenceOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;

/**
 * This prover module is responsible for applying the unit resolution rule.
 * <p>
 * Each call to {@link #addClauseAndDetectContradiction(Clause)} tries to detect
 * a contradiction using all unit clauses.
 * Each call to {@link #next(boolean)} then returns one clause that is issued
 * from the match between a unit and a non-unit clause.
 *
 * @author François Terrier
 *
 */
public class PredicateProver implements IProverModule {

	/**
	 * Debug flag for <code>PROVER_CASESPLIT_TRACE</code>
	 */
	public static boolean DEBUG = false;
	public static void debug(String message){
		System.out.println(message);
	}
	
	private RandomAccessList<Clause> unitClauses;
	private ResetIterator<Clause> unitClausesIterator;
	private RandomAccessList<Clause> nonUnitClauses;
	
	private ResolutionInferrer inferrer;
	private ResolutionResolver nonUnitResolver;
	private ResolutionResolver unitResolver;
	private ReverseResolutionResolver conditionResolver;
	
	private UnitMatcher unitMatcher;
	
	public PredicateProver(VariableContext context) {
		this.inferrer = new ResolutionInferrer(context);
		
		unitClauses = new RandomAccessList<Clause>();
		nonUnitClauses = new RandomAccessList<Clause>();
		
		unitMatcher = new UnitMatcher();
		
		nonUnitResolver = new ResolutionResolver(inferrer, new IteratorMatchIterable(nonUnitClauses.iterator()));
		unitResolver = new ResolutionResolver(inferrer, new UnitMatchIterable(unitMatcher));
		conditionResolver = new ReverseResolutionResolver(inferrer, new UnitMatchIterable(unitMatcher));
		
		unitClausesIterator = unitClauses.iterator();
	}
	
	private int counter = 0;
	private boolean isNextAvailable() {
		if (counter > 0) {
			counter--;
			return true;
		}
		else {
			counter = 50;
			return false;
		}
	}
	
	private boolean initializeNonUnitResolver() {
		if (!nonUnitResolver.isInitialized()) {
			Clause unit = nextUnit();
			if (unit == null) return false;
			nonUnitResolver.initialize(unit);
		}
		return true;
	}
	
	private ResolutionResult getNextNonUnitResolverResult() {
		ResolutionResult nextClause = nonUnitResolver.next();
		while (nextClause == null) {
			Clause unit = nextUnit();
			if (unit == null) return null;
			else newClause(unit, unitResolver);
			nonUnitResolver.initialize(unit);
			nextClause = nonUnitResolver.next();
		}
		return nextClause;
	}
	
	@Override
	public ProverResult next(boolean force) {
		if (!force && !isNextAvailable()) return ProverResult.EMPTY_RESULT; 
		if (!initializeNonUnitResolver()) return ProverResult.EMPTY_RESULT;
		
		ResolutionResult nextClause = getNextNonUnitResolverResult();
		ProverResult result = ProverResult.EMPTY_RESULT;
		if (nextClause != null) {
			Clause clause = nextClause.getDerivedClause();
			if (nextClause.getSubsumedClause() != null) result = new ProverResult(clause, nextClause.getSubsumedClause());
			else result = new ProverResult(clause);
		}
		if (DEBUG) debug("PredicateProver["+counter+"], next clause: "+result);
		return result;
	}
	
	private Clause nextUnit() {
		if (unitClausesIterator.hasNext()) return unitClausesIterator.next();
		return null;
	}
	
	public ProverResult newClause(Clause clause, IResolver resolver) {
		Set<Clause> generatedClauses = new HashSet<Clause>();
		Set<Clause> subsumedClauses = new HashSet<Clause>();
		
		resolver.initialize(clause);
		ResolutionResult result = resolver.next();
		while (result != null) {
			if (result.getSubsumedClause() != null) subsumedClauses.add(result.getSubsumedClause());
			
			Clause inferredClause = result.getDerivedClause();
			generatedClauses.add(inferredClause);
			
			result = resolver.next();
		}
		return new ProverResult(generatedClauses, subsumedClauses);
	}
	
	
	private boolean isAcceptedUnitClause(Clause clause) {
		return 	clause.isUnit() 
				&& clause.getPredicateLiteralsSize() > 0;
	}
	
	private boolean isAcceptedNonUnitClause(Clause clause) {
		 return	clause.getPredicateLiteralsSize()>0 
				&& !clause.isUnit()
				&& !clause.hasConditions();
	}
	
	@Override
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		if (isAcceptedUnitClause(clause)) {
			unitClauses.add(clause);
			unitMatcher.newClause(clause);
			// we generate the clauses
			if (!clause.hasQuantifiedLiteral()) return newClause(clause, unitResolver);
		}
		else if (isAcceptedNonUnitClause(clause)) {
			nonUnitClauses.add(clause);
			if (hadConditions(clause)) return newClause(clause, conditionResolver);
		}
		return ProverResult.EMPTY_RESULT;
	}

	@Override
	public void removeClause(Clause clause) {
		if (isAcceptedUnitClause(clause)) {
			unitClauses.remove(clause);
			unitMatcher.removeClause(clause);
		}
		else if (isAcceptedNonUnitClause(clause)) {
			nonUnitClauses.remove(clause);
		}
		nonUnitResolver.remove(clause);
		unitResolver.remove(clause);
	}
	
//	private boolean hadQuantifier(Clause clause) {
//		IOrigin origin = clause.getOrigin();
//		if (origin instanceof AbstractInferrenceOrigin) {
//			AbstractInferrenceOrigin tmp = (AbstractInferrenceOrigin)origin;
//			for (Clause parent : tmp.getClauses()) {
//				if (parent.hasQuantifiedLiteral()) return true;
//			}
//		}
//		return false;
//	}

	// TODO change this dirty code
	private boolean hadConditions(Clause clause) {
		IOrigin origin = clause.getOrigin();
		if (origin instanceof AbstractInferrenceOrigin) {
			AbstractInferrenceOrigin tmp = (AbstractInferrenceOrigin)origin;
			for (Clause parent : tmp.getClauses()) {
				if (parent.hasConditions()) return true;
			}
		}
		return false;
	}

	@Override
	public ProverResult contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies) {
		// do nothing
		return ProverResult.EMPTY_RESULT;
	}

	@Override
	public void registerDumper(Dumper dumper) {
		dumper.addDataStructure("PredicateFormula unit clauses", unitClauses.iterator());
//		dumper.addObject("Current unit clause", new Object(){
//			@Override
//			@SuppressWarnings("synthetic-access")
//			public String toString() {
//				return unitClausesIterator.current()==null?"no current unit clause":unitClausesIterator.current().toString();
//			}
//		});
		dumper.addDataStructure("PredicateFormula non-unit clauses", nonUnitClauses.iterator());
	}
	
	@Override
	public String toString() {
		return "PredicateProver";
	}
	
}


