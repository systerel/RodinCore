/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.equality;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.provers.equality.unionfind.Equality;
import org.eventb.internal.pp.core.provers.equality.unionfind.EqualitySolver;
import org.eventb.internal.pp.core.provers.equality.unionfind.FactResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Instantiation;
import org.eventb.internal.pp.core.provers.equality.unionfind.InstantiationResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Node;
import org.eventb.internal.pp.core.provers.equality.unionfind.QueryResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;
import org.eventb.internal.pp.core.provers.equality.unionfind.SourceTable;

/**
 * Implementation of {@link IEquivalenceManager}.
 * <p>
 * This class handles the mapping from the literals given by the prover
 * to the internal classes of the {@link EqualitySolver}. It handles 
 * backtracking/adding/removing equalities.
 *
 * @author François Terrier
 *
 */
public final class EquivalenceManager implements IEquivalenceManager {

	private final SourceTable table;
	private final EqualitySolver solver;

	// maps Constant -> Node
	// 		EqualityFormula -> EqualityFormula
	//		EqualityFormula -> Instantiation
	private final Hashtable<Constant, Node> constantTable = new Hashtable<Constant, Node>();
	private final Hashtable<EqualityLiteral, Equality<FactSource>> factEqualityTable = new Hashtable<EqualityLiteral, Equality<FactSource>>();
	private final Hashtable<EqualityLiteral, Equality<QuerySource>> queryEqualityTable = new Hashtable<EqualityLiteral, Equality<QuerySource>>();
	private final Hashtable<EqualityLiteral, Instantiation> instantiationTable = new Hashtable<EqualityLiteral, Instantiation>();
	
	// for backtrack
	private Set<Equality<FactSource>> equalities = new HashSet<Equality<FactSource>>();
 
	public EquivalenceManager() {
		this.table = new SourceTable();
		this.solver = new EqualitySolver(table);
	}

	public void removeInstantiation(EqualityLiteral equality, Clause clause) {
		assert !clause.isUnit() && !clause.isFalse();
		
		Instantiation instantiation = instantiationTable.get(equality);
		if (instantiation == null) return;
		
		QuerySource source = instantiation.getSource();
		source.removeClause(clause);
		if (!source.isValid()) {
			instantiationTable.remove(equality);
			Node node = instantiation.getNode();
			node.removeInstantiation(instantiation);
		}
	}
	
	public void removeQueryEquality(EqualityLiteral equality, Clause clause) {
		// for queries only, facts are handled differently
		// facts are never removed without being put back with a different level
		assert !clause.isUnit() && !clause.isFalse();
		
		Equality<QuerySource> nodeEquality = queryEqualityTable.get(equality);
		if (nodeEquality == null) return;
		
		QuerySource source = nodeEquality.getSource();
		source.removeClause(clause);
		if (!source.isValid()) {
			queryEqualityTable.remove(equality);
			removeEqualityFromNodes(nodeEquality, equality.isPositive());
		}
	}
	
	private void removeEqualityFromNodes(Equality<QuerySource> nodeEquality, boolean isPositive) {
		if (isPositive) {
			nodeEquality.getLeft().removeQueryEquality(nodeEquality);
			nodeEquality.getRight().removeQueryEquality(nodeEquality);
		}
		else {
			nodeEquality.getLeft().removeQueryInequality(nodeEquality);
			nodeEquality.getRight().removeQueryInequality(nodeEquality);
		}
	}
	
	public FactResult addFactEquality(EqualityLiteral equality, Clause clause) {
		assert equality.getTerm(0) instanceof Constant && equality.getTerm(1) instanceof Constant;
		
		Node node1 = getNodeAndAddConstant((Constant)equality.getTerm1());
		Node node2 = getNodeAndAddConstant((Constant)equality.getTerm2());
		
		Equality<FactSource> nodeEquality = factEqualityTable.get(equality);
		boolean alreadyExists = true;
		if (nodeEquality == null) {
			alreadyExists = false;
			nodeEquality = addEquality(equality, node1, node2, factEqualityTable, true);
		}
		FactSource source = nodeEquality.getSource();
		if (alreadyExists) {
			if (clause.getLevel().isAncestorOf(source.getLevel())) {
				source.setClause(clause);
			}
			return null;
		}
		
		if (equality.isPositive()) equalities.add(nodeEquality);
		else {
			node1.addFactInequality(nodeEquality);
			node2.addFactInequality(nodeEquality);
		}
		source.setClause(clause);
		
		
		FactResult result = equality.isPositive()?solver.addFactEquality(nodeEquality):
			solver.addFactInequality(nodeEquality);
			
		return result;
	}
	
	public QueryResult addQueryEquality(EqualityLiteral equality, Clause clause) {
		assert equality.getTerm(0) instanceof Constant && equality.getTerm(1) instanceof Constant;
		
		Node node1 = getNodeAndAddConstant((Constant)equality.getTerm1());
		Node node2 = getNodeAndAddConstant((Constant)equality.getTerm2());
		
		Equality<QuerySource> nodeEquality = queryEqualityTable.get(equality);
		boolean alreadyExists = true;
		if (nodeEquality == null) {
			alreadyExists = false;
			nodeEquality = addEquality(equality, node1, node2, queryEqualityTable, false);
		}
		QuerySource source = nodeEquality.getSource();
		source.addClause(clause);
		
		if (alreadyExists) return null;
		
		if (equality.isPositive()) {
			node1.addQueryEquality(nodeEquality);
			node2.addQueryEquality(nodeEquality);
		}
		else {
			node1.addQueryInequality(nodeEquality);
			node2.addQueryInequality(nodeEquality);
		}
		
		QueryResult result = solver.addQuery(nodeEquality, equality.isPositive());
		
		return result;
	}
	
	
	public List<InstantiationResult> addInstantiationEquality(EqualityLiteral equality, Clause clause) {
		assert !equality.isPositive()?clause.isEquivalence():true;
		assert !clause.isUnit();
		assert (equality.getTerm(0) instanceof Variable || equality.getTerm(1) instanceof Variable);
		assert (equality.getTerm(0) instanceof Constant || equality.getTerm(1) instanceof Constant);
		
		Node node = null;
		if (equality.getTerm(0) instanceof Constant) {
			node = getNodeAndAddConstant((Constant)equality.getTerm(0));
		}
		else if (equality.getTerm(1) instanceof Constant) {
			node = getNodeAndAddConstant((Constant)equality.getTerm(1));
		}
		else assert false;
		
		Instantiation instantiation = instantiationTable.get(equality);
		QuerySource source;
		if (instantiation == null) {
			source = new Source.QuerySource(equality);
			instantiation = new Instantiation(node, source);
			instantiationTable.put(equality, instantiation);
			source.addClause(clause);
		}
		else {
			source = instantiation.getSource();
			source.addClause(clause);
			return null;
		}
		
		node.addInstantiation(instantiation);
		
		List<InstantiationResult> result = solver.addInstantiation(instantiation);
		return result;
	}

	
	public void backtrack(Level level) {
		// TODO lazy backtrack
		
		// 1 backtrack sources
		for (Iterator<Entry<EqualityLiteral, Equality<FactSource>>> iter = factEqualityTable.entrySet().iterator(); iter.hasNext();) {
			Entry<EqualityLiteral, Equality<FactSource>> entry = iter.next();
			FactSource source = entry.getValue().getSource();
			source.backtrack(level);
			if (!source.isValid()) iter.remove();
		}
		for (Iterator<Entry<EqualityLiteral, Equality<QuerySource>>> iter = queryEqualityTable.entrySet().iterator(); iter.hasNext();) {
			Entry<EqualityLiteral, Equality<QuerySource>> entry = iter.next();
			QuerySource source = entry.getValue().getSource();
			source.backtrack(level);
			if (!source.isValid()) iter.remove();
		}
		// instantiations
		for (Iterator<Entry<EqualityLiteral, Instantiation>> iter = instantiationTable.entrySet().iterator(); iter.hasNext();) {
			Entry<EqualityLiteral, Instantiation> entry = iter.next();
			QuerySource source = entry.getValue().getSource();
			source.backtrack(level);
			if (source.isValid())
				entry.getValue().backtrack(level);
			else iter.remove();
		}

		// 2 backtrack nodes
		for (Node node : constantTable.values()) {
			node.backtrack();
		}
		
		// 4 backtrack sourcetable
		table.clear();
		
		// 5 add equalities
		for (Equality<FactSource> equality : equalities) {
			if (equality.getSource().isValid()) {
				solver.addFactEquality(equality);
			}
		}
	}
	
	private Node getNodeAndAddConstant(Constant constant) {
		if (constantTable.containsKey(constant)) return constantTable.get(constant);
		else {
			Node node = new Node(constant);
			constantTable.put(constant, node);
			return node;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Source> Equality<T> addEquality(EqualityLiteral equality, Node node1, Node node2, Hashtable<EqualityLiteral, Equality<T>> table, boolean fact) {
		assert !table.containsKey(equality);
		
		Source source = fact?new Source.FactSource(equality):new Source.QuerySource(equality);
		Equality<T> nodeEquality = new Equality(node1, node2, source);
		table.put(equality, nodeEquality);
		return nodeEquality;
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Entry<Node,Set<Node>> equivalenceClass : getEquivalenceClasses().entrySet()) {
			builder.append(equivalenceClass.getValue().toString());
			if (equivalenceClass.getKey().getRootFactsInequalities().size()>0) {
				builder.append(", F≠");
				builder.append(equivalenceClass.getKey().getRootFactsInequalities());
			}
			if (equivalenceClass.getKey().getRootQueryEqualities().size()>0) {
				builder.append(", Q=");
				builder.append(equivalenceClass.getKey().getRootQueryEqualities());
			}
			if (equivalenceClass.getKey().getRootQueryInequalities().size()>0) {
				builder.append(", Q≠");
				builder.append(equivalenceClass.getKey().getRootQueryInequalities());
			}
			if (equivalenceClass.getKey().getRootInstantiations().size()>0) {
				builder.append(", I");
				builder.append(equivalenceClass.getKey().getRootInstantiations());
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	private Map<Node, Set<Node>> getEquivalenceClasses() {
		Map<Node, Set<Node>> result = new HashMap<Node, Set<Node>>();
		for (Node node : constantTable.values()) {
			Node root = node.getRoot();
			if (!result.containsKey(root)) result.put(root, new HashSet<Node>());
			Set<Node> nodes = result.get(root);
			nodes.add(node);
		}
		return result;
	}
	
}
 