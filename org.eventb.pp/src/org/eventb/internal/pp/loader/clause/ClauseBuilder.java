/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.clause;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.ComplexPredicateLiteral;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.tracing.PredicateOrigin;
import org.eventb.internal.pp.core.tracing.TypingOrigin;
import org.eventb.internal.pp.loader.formula.AbstractFormula;
import org.eventb.internal.pp.loader.formula.ILabelizableFormula;
import org.eventb.internal.pp.loader.formula.ISignedFormula;
import org.eventb.internal.pp.loader.formula.ISubFormula;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IContext;
import org.eventb.internal.pp.loader.predicate.INormalizedFormula;
import org.eventb.internal.pp.loader.predicate.PredicateBuilder;

/**
 * This is the builder for the second phase of the loading process. The input
 * is the context filled in by the {@link PredicateBuilder}, which handles the
 * first part of the loading process.
 * 
 * At the end of this phase, no simplifications have been applied. It means
 * that, for instance.
 *
 * @author François Terrier
 *
 */
public class ClauseBuilder {

	/**
	 * Debug flag for <code>LOADER_PHASE2_TRACE</code>
	 */
	public static boolean DEBUG;
	
	public static void debug(String message){
		if (DEBUG)
			System.out.println(prefix+message);
	}
	private static StringBuilder prefix = new StringBuilder("");
	public static void debugEnter(AbstractFormula<?> pred) {
		debug("Entering "+pred+": "+pred.getStringDeps());
		prefix.append("  ");
	}
	public static void debugExit(ISubFormula<?> pred) {
		prefix.deleteCharAt(prefix.length()-1);
		prefix.deleteCharAt(prefix.length()-1);
	}
	
	private Set<Clause> clauses;
	private IVariableContext variableContext;
	
	private ClauseFactory cf = ClauseFactory.getDefault();
	
	private BooleanEqualityTable bool;
	private LabelManager manager;
	
	private VariableTable variableTable;

	public LoaderResult getResult() {
		return new LoaderResult(clauses);
	}
	
	/**
	 * Returns the result of this phase.
	 * 
	 * @return the result of this phase
	 */
	public void buildClauses(IContext context) {
		debugContext(context);
		
		variableContext = new VariableContext();
		variableTable = new VariableTable();
		bool = new BooleanEqualityTable(context.getNextLiteralIdentifier());

		clauses = new LinkedHashSet<Clause>();
		manager = new LabelManager();
		
		for (INormalizedFormula signature : context.getResults()) {
			debug("========================================");
			debug("Getting clauses for original formula: " + signature);
//			manager.setForceLabelize(false);
			buildNormalizedFormulas(signature);
		}
		
		manager.setGettingDefinitions(true);
		getDefinitions();

		// get type informations
//		buildPredicateTypeInformation(context.getAllPredicateDescriptors());
		
		debug("========================================");
		debug("End of loading phase, clauses:");
		for (Clause clause : clauses) {
			debug(clause.toString());
		}
	}
	
	private void getDefinitions() {
		manager.nextLabelizableFormula();
		ILabelizableFormula<?> formula = manager.getNextFormula();
		while (formula != null) {
			debug("========================================");
			debug("Getting definition clauses for label: " + formula + ": " + formula.getStringDeps());
			formula.split();
//			manager.setForceLabelize(false);
			
//			if (manager.isNextEquivalence()) {
//				formula.getFinalClauses(clauses, manager, cf, bool, variableTable, true);
//			}
			if (manager.isNextPositive()) {
				formula.getFinalClauses(clauses, manager, cf, bool, variableTable, variableContext, false);
			}
			else {
				formula.getFinalClauses(clauses, manager, cf, bool, variableTable, variableContext, true);
			}
			
			manager.nextLabelizableFormula();
			formula = manager.getNextFormula();
		}
	}
	
	private void debugContext(IContext context) {
		if (DEBUG) {
			System.out.println("========================================");
			System.out.println("Structure of all normalized formulas");
			for (INormalizedFormula formula : context.getResults()) {
				System.out.println(formula.getSignature().toTreeForm(""));
				System.out.println("----------------");
			}
			System.out.println("Summary of all subformulas (except arithmetic)");
			for (LiteralDescriptor desc : context.getAllDescriptors()) {
				System.out.println(desc.toStringWithInfo());
				System.out.println("----------------");
			}
		}
	}
	
	/**
	 * @param result
	 */
	private void buildNormalizedFormulas(INormalizedFormula result) {
		ISignedFormula sig = result.getSignature();
		sig.split();
		sig.getFinalClauses(clauses, manager, cf, bool, variableTable, variableContext, new PredicateOrigin(result.getOriginalPredicate(), result.isGoal()));
	}
	
	@SuppressWarnings("unused")
	public void buildPredicateTypeInformation(IContext context) {
		for (PredicateDescriptor descriptor : context.getAllPredicateDescriptors()) {
			List<TermSignature> unifiedTerms = descriptor.getUnifiedResults();
			if (unifiedTerms.size() == 2 && !unifiedTerms.get(1).isConstant()) {
				TermSignature term1 = unifiedTerms.get(0);
				TermSignature term2 = unifiedTerms.get(1);
				Clause clause = createTypeClause(descriptor.getIndex(), term1.getSort(), term2.getSort());
				clauses.add(clause);
			}
		}
	}
	
	private Clause createTypeClause(int index, Sort sort1, Sort sort2) {
		SimpleTerm term1 = variableContext.getNextVariable(sort1);
		SimpleTerm term2 = new Constant(sort1.getName(), sort2);
		List<SimpleTerm> terms = new ArrayList<SimpleTerm>();
		terms.add(term1);
		terms.add(term2);
		PredicateLiteral literal = new ComplexPredicateLiteral(new org.eventb.internal.pp.core.elements.PredicateDescriptor(index, true), terms);
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		predicates.add(literal);
		Clause clause = new DisjunctiveClause(new TypingOrigin(), predicates, 
				new ArrayList<EqualityLiteral>(), new ArrayList<ArithmeticLiteral>(), new ArrayList<EqualityLiteral>());
		return clause;
	}
	
	
	public IVariableContext getVariableContext() {
		return variableContext;
	}
	
}
