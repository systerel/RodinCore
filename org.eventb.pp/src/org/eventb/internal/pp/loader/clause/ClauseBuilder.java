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

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.tracing.PredicateOrigin;
import org.eventb.internal.pp.loader.formula.AbstractFormula;
import org.eventb.internal.pp.loader.formula.ILabelizableFormula;
import org.eventb.internal.pp.loader.formula.ISignedFormula;
import org.eventb.internal.pp.loader.formula.ISubFormula;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
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
	
	private Collection<IClause> clauses;
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
		variableTable = new VariableTable(variableContext);
		bool = new BooleanEqualityTable(context.getNextLiteralIdentifier());

		clauses = new ArrayList<IClause>();
		manager = new LabelManager();
		
		for (INormalizedFormula signature : context.getResults()) {
			debug("========================================");
			debug("Getting clauses for original formula: " + signature);
//			manager.setForceLabelize(false);
			buildNormalizedFormulas(signature);
		}
		
		manager.setGettingDefinitions(true);
		getDefinitions();

		
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
				formula.getFinalClauses(clauses, manager, cf, bool, variableTable, false);
			}
			else {
				formula.getFinalClauses(clauses, manager, cf, bool, variableTable, true);
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
		sig.getFinalClauses(clauses, manager, cf, bool, variableTable, new PredicateOrigin(result.getOriginalPredicate(), result.isGoal()));
	}
	
	public IVariableContext getVariableContext() {
		return variableContext;
	}
	
//	private void addClause(List<ILiteral> clause) {
//		if (clause.size() == 1) {
//			unitClauses.add(cf.newDisjClause(clause));
//		}
//		else {
//			// TODO really a disjunctive clause here ?
//			// clauses should not be created here, they should be
//			// only created inside DisjunctiveClause and
//			// EquivalenceClause
//			clauses.add(cf.newDisjClause(clause));
//		}
//	}
	
}
