/*******************************************************************************
 * Copyright (c) 2006, 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.loader.formula.descriptor.ArithmeticDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.DisjunctiveClauseDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.EqualityDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.EquivalenceClauseDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.QuantifiedDescriptor;
import org.eventb.internal.pp.loader.formula.key.SymbolTable;
import org.eventb.internal.pp.loader.formula.terms.VariableSignature;

/**
 * This class represents a context. Information stored in a context are information that
 * are needed to parse a sequent and that are completed by the builder for each sequent.
 *
 * @author François Terrier
 *
 */
public class AbstractContext implements IContext {

	// TODO maybe use only one table with a good HASH algorithm
	// it will factor out lots of code in PredicateBuilder (eg. in method
	// exitIN and exitEqualityLiteral ...)
	
	SymbolTable<PredicateDescriptor> predicateTable = new SymbolTable<PredicateDescriptor>();
	SymbolTable<DisjunctiveClauseDescriptor> disjunctionTable = new SymbolTable<DisjunctiveClauseDescriptor>();
	SymbolTable<EquivalenceClauseDescriptor> equivalenceTable = new SymbolTable<EquivalenceClauseDescriptor>();
	SymbolTable<EqualityDescriptor> equalityTable = new SymbolTable<EqualityDescriptor>();
	SymbolTable<ArithmeticDescriptor> arithmeticTable = new SymbolTable<ArithmeticDescriptor>();
	SymbolTable<QuantifiedDescriptor> quantifierTable = new SymbolTable<QuantifiedDescriptor>();
	
	List<INormalizedFormula> results = new ArrayList<INormalizedFormula>();
	
	int numberOfVariables = 0;
	
	final Vector<VariableSignature> boundVars = new Vector<VariableSignature>();

	public void addDecls(BoundIdentDecl[] decls) {
		int revIndex = boundVars.size(); // induction variable for next loop
		for (BoundIdentDecl decl : decls) {
			final Sort sort = new Sort(decl.getType());
			final int varIndex = numberOfVariables++;
			boundVars.add(new VariableSignature(varIndex, revIndex++, sort));
		}
	}

	public void removeDecls(BoundIdentDecl[] decls) {
		boundVars.setSize(boundVars.size() - decls.length);
	}

	public int getQuantifierOffset() {
		return boundVars.size();
	}

	public VariableSignature getVariableSignature(int boundIndex) {
		final int length = boundVars.size();
		assert 0 <= boundIndex && boundIndex < length;
		return boundVars.get(length - boundIndex - 1);
	}

	public SymbolTable<PredicateDescriptor> getLiteralTable() {
		return predicateTable;
	}

	public void addResult(INormalizedFormula signature) {
		results.add(signature);
	}

	public SymbolTable<DisjunctiveClauseDescriptor> getDisjClauseTable() {
		return disjunctionTable;
	}

	public SymbolTable<QuantifiedDescriptor> getQuantifiedTable() {
		return quantifierTable;
	}

	public SymbolTable<EquivalenceClauseDescriptor> getEqClauseTable() {
		return equivalenceTable;
	}
	
	public SymbolTable<EqualityDescriptor> getEqualityTable() {
		return equalityTable;
	}

	public SymbolTable<ArithmeticDescriptor> getArithmeticTable() {
		return arithmeticTable;
	}
	
	public List<INormalizedFormula> getResults() {
		return results;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractContext) {
			AbstractContext temp = (AbstractContext) obj;
			return disjunctionTable.equals(temp.disjunctionTable) && predicateTable.equals(temp.predicateTable)
				&& quantifierTable.equals(temp.quantifierTable) && results.equals(temp.results);
		}
		return false;
	}

	public Collection<LiteralDescriptor> getAllDescriptors() {
		Collection<LiteralDescriptor> result = new ArrayList<LiteralDescriptor>();
		result.addAll(predicateTable.getAllLiterals());
		result.addAll(disjunctionTable.getAllLiterals());
		result.addAll(equivalenceTable.getAllLiterals());
		result.addAll(equalityTable.getAllLiterals());
		result.addAll(quantifierTable.getAllLiterals());
		return result;
	}
	
	public Collection<PredicateDescriptor> getAllPredicateDescriptors() {
		return predicateTable.getAllLiterals();
	}

	private int nextIdentifier = 0;
	
	public int getNextLiteralIdentifier() {
		return nextIdentifier++;
	}

	public int getFreshVariableIndex() {
		return numberOfVariables++;
	}

}
