/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.clause;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.IArithmetic;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPPredicate;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Term;
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
	
	private Set<IClause> clauses;
	private IVariableContext variableContext;
	
	private ClauseFactory cf = ClauseFactory.getDefault();
	
	private BooleanEqualityTable bool;
	private LabelManager manager;
	
	private VariableTable variableTable;
	
	/**
	 * Returns the result of this phase.
	 * 
	 * @return the result of this phase
	 */
	public LoaderResult buildClauses(IContext context) {
		debugContext(context);
		
		variableContext = new VariableContext();
		variableTable = new VariableTable();
		bool = new BooleanEqualityTable(context.getNextLiteralIdentifier());

		clauses = new HashSet<IClause>();
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
		buildPredicateTypeInformation(context.getAllPredicateDescriptors());
		
		debug("========================================");
		debug("End of loading phase, clauses:");
		for (IClause clause : clauses) {
			debug(clause.toString());
		}
		return new LoaderResult(clauses);
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
	
	
	
	private void buildPredicateTypeInformation(Collection<PredicateDescriptor> descriptors) {
		for (PredicateDescriptor descriptor : descriptors) {
			List<TermSignature> unifiedTerms = descriptor.getUnifiedResults();
			if (unifiedTerms.size() == 2 && !unifiedTerms.get(1).isConstant()) {
				TermSignature term1 = unifiedTerms.get(0);
				TermSignature term2 = unifiedTerms.get(1);
				IClause clause = createTypeClause(descriptor.getIndex(), term1.getSort(), term2.getSort());
				clauses.add(clause);
			}
		}
	}
	
	private IClause createTypeClause(int index, Sort sort1, Sort sort2) {
		Term term1 = variableContext.getNextVariable(sort1);
		Term term2 = new Constant(sort1.getName(), sort2);
		List<Term> terms = new ArrayList<Term>();
		terms.add(term1);
		terms.add(term2);
		IPredicate literal = new PPPredicate(index, true, terms);
		List<IPredicate> predicates = new ArrayList<IPredicate>();
		predicates.add(literal);
		IClause clause = new PPDisjClause(new TypingOrigin(), predicates, 
				new ArrayList<IEquality>(), new ArrayList<IArithmetic>(), new ArrayList<IEquality>());
		return clause;
	}
	
	
	public IVariableContext getVariableContext() {
		return variableContext;
	}
	
}
