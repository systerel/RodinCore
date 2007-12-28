/*******************************************************************************
 * Copyright (c) 2006, 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.clause;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.ComplexPredicateLiteral;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.PredicateTable;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.elements.terms.VariableTable;
import org.eventb.internal.pp.core.tracing.DefinitionOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.core.tracing.TypingOrigin;
import org.eventb.internal.pp.loader.formula.AbstractFormula;
import org.eventb.internal.pp.loader.formula.AbstractLabelizableFormula;
import org.eventb.internal.pp.loader.formula.ClauseResult;
import org.eventb.internal.pp.loader.formula.SignedFormula;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IContext;
import org.eventb.internal.pp.loader.predicate.INormalizedFormula;
import org.eventb.internal.pp.loader.predicate.PredicateLoader;

/**
 * This is the builder for the second phase of the loading process. The input
 * is the context filled in by the {@link PredicateLoader}, which handles the
 * first part of the loading process.
 * 
 * At the end of this phase, no simplifications have been applied.
 *
 * @author François Terrier
 *
 */
public final class ClauseBuilder {

	/**
	 * Debug flag for <code>LOADER_PHASE2_TRACE</code>
	 */
	public static boolean DEBUG = false;
	
	public static void debug(String message){
		System.out.println(prefix+message);
	}
	private static StringBuilder prefix = new StringBuilder("");
	public static void debugEnter(AbstractFormula<?> pred) {
		if (DEBUG) debug("Entering "+pred+": "+pred.getStringDeps());
		prefix.append("  ");
	}
	public static void debugExit(AbstractFormula<?> pred) {
		prefix.setLength(prefix.length() - 2);
	}
	
	private List<Clause> clauses;
	private VariableContext variableContext;
	private LabelManager manager;
	private BooleanEqualityTable bool;
	private PredicateTable predicateTable;
	private VariableTable variableTable;

	public void loadClausesFromContext(IContext context) {
		loadClausesFromContext(context, null);
	}
	
	public void loadClausesFromContext(IContext context, VariableTable table) {
		debugContext(context);
		initialize(context, table);
		
		for (INormalizedFormula signature : context.getResults()) {
			if (DEBUG){
				debug("========================================");
				debug("Getting clauses for original formula: " + signature);
			}
			buildNormalizedFormulas(signature);
		}
		getDefinitions();
		if (DEBUG) {
			debug("========================================");
			debug("End of loading phase, clauses:");
			for (Clause clause : clauses) {
				debug(clause.toString());
			}
		}
	}
	
	private void initialize(IContext context, VariableTable variableTable) {
		variableContext = new VariableContext();
		if (variableTable == null) this.variableTable = new VariableTable(variableContext);
		else this.variableTable = variableTable;
		predicateTable = new PredicateTable();
		bool = new BooleanEqualityTable(context.getNextLiteralIdentifier());
		clauses = new ArrayList<Clause>();
		manager = new LabelManager();
	}	
	
	private void getDefinitions() {
		manager.nextLabelizableFormula();
		AbstractLabelizableFormula<?> formula = manager.getNextFormula();
		while (formula != null) {
			if (DEBUG) debug("========================================");
			if (DEBUG) debug("Getting definition clauses for label: " + formula + ": " + formula.getStringDeps());
			
			ClauseResult result;
			if (manager.isNextPositive()) {
				result = formula.getFinalClauses(manager, bool, variableTable, predicateTable, true);
			}
			else {
				result = formula.getFinalClauses(manager, bool, variableTable, predicateTable, false);
			}
			clauses.addAll(result.getClauses(new DefinitionOrigin(), variableContext));
			
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
	
	private void buildNormalizedFormulas(INormalizedFormula result) {
		SignedFormula<?> sig = result.getSignature();
		ClauseResult clauseResult = sig.getFinalClauses(manager, bool, variableTable, predicateTable);
		IOrigin origin = result.getOrigin();
		clauses.addAll(clauseResult.getClauses(origin,variableContext));
	}
	
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
		SimpleTerm term2 = variableTable.getConstant(sort1.getName(), sort2);
		List<SimpleTerm> terms = new ArrayList<SimpleTerm>();
		terms.add(term1);
		terms.add(term2);
		PredicateLiteral literal = new ComplexPredicateLiteral(predicateTable.getDescriptor(index),true, terms);
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		predicates.add(literal);
		Clause clause = ClauseFactory.getDefault().makeDisjunctiveClause(new TypingOrigin(), predicates, 
				new ArrayList<EqualityLiteral>(), new ArrayList<ArithmeticLiteral>(), new ArrayList<EqualityLiteral>());
		return clause;
	}
	
	public VariableContext getVariableContext() {
		return variableContext;
	}
	
	public PredicateTable getPredicateTable() {
		return predicateTable;
	}
	
	public List<Clause> getClauses() {
		return clauses;
	}
}
