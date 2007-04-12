/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.internal.pp.loader.formula.descriptor.DisjunctiveClauseDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.EqualityDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.EquivalenceClauseDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.QuantifiedDescriptor;
import org.eventb.internal.pp.loader.formula.key.SymbolTable;

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
	
	// we use different tables
	SymbolTable<PredicateDescriptor> ptable = new SymbolTable<PredicateDescriptor>();
	SymbolTable<DisjunctiveClauseDescriptor> cTable = new SymbolTable<DisjunctiveClauseDescriptor>();
	SymbolTable<EquivalenceClauseDescriptor> eTable = new SymbolTable<EquivalenceClauseDescriptor>();
	SymbolTable<EqualityDescriptor> equalityTable = new SymbolTable<EqualityDescriptor>();
//	SymbolTable aTable = new SymbolTable();
	SymbolTable<QuantifiedDescriptor> qTable = new SymbolTable<QuantifiedDescriptor>();
	
	List<INormalizedFormula> results = new ArrayList<INormalizedFormula>();
	
	int quantOffset = 0;
	int numberOfVariables = 0;
	
	public void decrementQuantifierOffset(int value) {
		quantOffset -= value;
	}

	public int getQuantifierOffset() {
		return quantOffset;
	}

	public SymbolTable<PredicateDescriptor> getLiteralTable() {
		return ptable;
	}

	public void incrementQuantifierOffset(int value) {
		quantOffset += value;
	}
	
	public void incrementNumberOfVariables(int value) {
		numberOfVariables += value;
	}
	
	public void addResult(INormalizedFormula signature) {
		results.add(signature);
	}

	public SymbolTable<DisjunctiveClauseDescriptor> getDisjClauseTable() {
		return cTable;
	}

	public SymbolTable<QuantifiedDescriptor> getQuantifiedTable() {
		return qTable;
	}

	public SymbolTable<EquivalenceClauseDescriptor> getEqClauseTable() {
		return eTable;
	}
	
	public SymbolTable<EqualityDescriptor> getEqualityTable() {
		return equalityTable;
	}

	public List<INormalizedFormula> getResults() {
		return results;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractContext) {
			AbstractContext temp = (AbstractContext) obj;
			return cTable.equals(temp.cTable) && ptable.equals(temp.ptable)
				&& qTable.equals(temp.qTable) && results.equals(temp.results);
		}
		return false;
	}

	public Collection<LiteralDescriptor> getAllDescriptors() {
		Collection<LiteralDescriptor> result = new ArrayList<LiteralDescriptor>();
		result.addAll(ptable.getAllLiterals());
		result.addAll(cTable.getAllLiterals());
		result.addAll(eTable.getAllLiterals());
		result.addAll(equalityTable.getAllLiterals());
		result.addAll(qTable.getAllLiterals());
		return result;
	}

	private int nextIdentifier = 0;
	
	public int getNextLiteralIdentifier() {
		return nextIdentifier++;
	}

	public int getNumberOfVariables() {
		return numberOfVariables;
	}
	
	public int getFreshVariable() {
		return numberOfVariables++;
	}
	
}
