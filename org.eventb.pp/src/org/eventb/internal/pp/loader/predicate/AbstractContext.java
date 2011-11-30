/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - adapted to XProver v2 API
 *******************************************************************************/

package org.eventb.internal.pp.loader.predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.internal.pp.loader.formula.descriptor.ArithmeticDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.DisjunctiveClauseDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.EqualityDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.EquivalenceClauseDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.QuantifiedDescriptor;
import org.eventb.internal.pp.loader.formula.key.SymbolTable;

/**
 * This class represents a context. Information stored in a context are
 * information that are needed to parse a sequent and that are completed by the
 * builder for each sequent.
 * 
 * @author François Terrier
 * 
 */
public class AbstractContext implements IContext {

	public static void setDebugFlag(boolean value) {
		PredicateLoader.DEBUG = value;
	}

	// TODO maybe use only one table with a good HASH algorithm
	// it will factor out lots of code in PredicateBuilder (eg. in method
	// exitIN and exitEqualityLiteral ...)

	SymbolTable<PredicateDescriptor> predicateTable = new SymbolTable<PredicateDescriptor>();
	SymbolTable<DisjunctiveClauseDescriptor> disjunctionTable = new SymbolTable<DisjunctiveClauseDescriptor>();
	SymbolTable<EquivalenceClauseDescriptor> equivalenceTable = new SymbolTable<EquivalenceClauseDescriptor>();
	SymbolTable<EqualityDescriptor> equalityTable = new SymbolTable<EqualityDescriptor>();
	SymbolTable<ArithmeticDescriptor> arithmeticTable = new SymbolTable<ArithmeticDescriptor>();
	SymbolTable<QuantifiedDescriptor> quantifierTable = new SymbolTable<QuantifiedDescriptor>();

	protected final List<INormalizedFormula> results = new ArrayList<INormalizedFormula>();

	@Override
	public void load(ISimpleSequent sequent) {
		for (ITrackedPredicate predicate : sequent.getPredicates()) {
			load(predicate.getPredicate(), predicate.getOriginal(),
					!predicate.isHypothesis());
		}
	}
	// TODO clean up these load methods that are used only in tests
	@Override
	public void load(Predicate predicate, Predicate originalPredicate,
			boolean isGoal) {
		final PredicateLoader loader = getPredicateLoader(predicate,
				originalPredicate, isGoal);
		loader.load();
		results.add(loader.getResult());
	}

	protected PredicateLoader getPredicateLoader(Predicate predicate,
			Predicate originalPredicate, boolean isGoal) {
		return new PredicateLoader(this, predicate, originalPredicate, isGoal);
	}

	@Override
	public void load(Predicate predicate, boolean isGoal) {
		load(predicate, predicate, isGoal);
	}

	public SymbolTable<PredicateDescriptor> getLiteralTable() {
		return predicateTable;
	}

	SymbolTable<DisjunctiveClauseDescriptor> getDisjClauseTable() {
		return disjunctionTable;
	}

	SymbolTable<QuantifiedDescriptor> getQuantifiedTable() {
		return quantifierTable;
	}

	SymbolTable<EquivalenceClauseDescriptor> getEqClauseTable() {
		return equivalenceTable;
	}

	SymbolTable<EqualityDescriptor> getEqualityTable() {
		return equalityTable;
	}

	SymbolTable<ArithmeticDescriptor> getArithmeticTable() {
		return arithmeticTable;
	}

	@Override
	public List<INormalizedFormula> getResults() {
		return results;
	}

	@Override
	public INormalizedFormula getLastResult() {
		final int size = results.size();
		assert 0 < size;
		return results.get(size - 1);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractContext) {
			AbstractContext temp = (AbstractContext) obj;
			return disjunctionTable.equals(temp.disjunctionTable)
					&& predicateTable.equals(temp.predicateTable)
					&& quantifierTable.equals(temp.quantifierTable)
					&& results.equals(temp.results);
		}
		return false;
	}

	@Override
	public Collection<LiteralDescriptor> getAllDescriptors() {
		Collection<LiteralDescriptor> result = new ArrayList<LiteralDescriptor>();
		result.addAll(predicateTable.getAllLiterals());
		result.addAll(disjunctionTable.getAllLiterals());
		result.addAll(equivalenceTable.getAllLiterals());
		result.addAll(equalityTable.getAllLiterals());
		result.addAll(quantifierTable.getAllLiterals());
		return result;
	}

	@Override
	public Collection<PredicateDescriptor> getAllPredicateDescriptors() {
		return predicateTable.getAllLiterals();
	}

	private int nextIdentifier = 0;

	@Override
	public int getNextLiteralIdentifier() {
		return nextIdentifier++;
	}

	// Number of variables used in this context so far
	private int numberOfVariables = 0;

	@Override
	public int getFreshVariableIndex() {
		return numberOfVariables++;
	}

}
