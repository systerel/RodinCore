package org.eventb.internal.pp.core.provers.equality;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.provers.equality.unionfind.Equality;
import org.eventb.internal.pp.core.provers.equality.unionfind.EqualitySolver;
import org.eventb.internal.pp.core.provers.equality.unionfind.FactResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Node;
import org.eventb.internal.pp.core.provers.equality.unionfind.QueryResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source;
import org.eventb.internal.pp.core.provers.equality.unionfind.SourceTable;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;

public class EquivalenceManager implements IEquivalenceManager {

	private SourceTable table;
	private EqualitySolver solver;
	
	private Hashtable<Constant, Node> constantTable = new Hashtable<Constant, Node>();

	private Hashtable<IEquality, Equality<FactSource>> factEqualityTable = new Hashtable<IEquality, Equality<FactSource>>();
	private Hashtable<IEquality, Equality<QuerySource>> queryEqualityTable = new Hashtable<IEquality, Equality<QuerySource>>();

	private Set<Equality<FactSource>> equalities = new HashSet<Equality<FactSource>>();
 
	public EquivalenceManager() {
		this.table = new SourceTable();
		this.solver = new EqualitySolver(table);
	}
	
	public void removeQueryEquality(IEquality equality, IClause clause) {
		// for queries only, facts are handled differently
		// facts are never removed without being put back with a different level
		assert !clause.isUnit() && !clause.isFalse();
		
		Equality<QuerySource> nodeEquality = queryEqualityTable.get(equality);
		if (nodeEquality == null) return;
		
		QuerySource source = nodeEquality.getSource();
		IClause oldClause = source.removeClause(clause);
		if (!source.isValid()) queryEqualityTable.remove(equality);
		if (oldClause == null) return;
		
		if (equality.isPositive()) nodeEquality.getLeft().removeQueryEquality(nodeEquality);
		else nodeEquality.getLeft().removeQueryInequality(nodeEquality);
	}
	
	private void removeQuery(IEquality equality) {
		// not for fact equalities
		Equality<QuerySource> nodeEquality = queryEqualityTable.remove(equality);
		if (equality.isPositive()) nodeEquality.getLeft().removeQueryEquality(nodeEquality);
		else nodeEquality.getLeft().removeQueryInequality(nodeEquality);
	}
	
	public FactResult addFactEquality(IEquality equality, IClause clause) {
		Node left, right;
		if (equality.getTerms().get(0) instanceof Constant && equality.getTerms().get(1) instanceof Constant) {
			left = getNodeAndAddConstant((Constant)equality.getTerms().get(0));
			right = getNodeAndAddConstant((Constant)equality.getTerms().get(1));
		}
		else return null;
		// TODO handle non constant cases
		
		Node node1, node2;
		if (left.compareTo(right) < 0) {
			node1 = left;
			node2 = right;
		}
		else {
			node1 = right;
			node2 = left;
		}
		
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
		else node1.addFactInequality(nodeEquality);
		source.setClause(clause);
		
		
		FactResult result = equality.isPositive()?solver.addFactEquality(node1, node2, source):
			solver.addFactInequality(node1, node2, source);
		if (result != null && !result.hasContradiction()) {
			for (QueryResult queryResult : result.getSolvedQueries()) {
				removeQuery(queryResult.getEquality());
			}
		}
			
		return result;
	}
	
	public QueryResult addQueryEquality(IEquality equality, IClause clause) {
		Node left, right;
		if (equality.getTerms().get(0) instanceof Constant && equality.getTerms().get(1) instanceof Constant) {
			left = getNodeAndAddConstant((Constant)equality.getTerms().get(0));
			right = getNodeAndAddConstant((Constant)equality.getTerms().get(1));
		}
		else return null;
		// TODO handle non constant cases
		
		Node node1, node2;
		if (left.compareTo(right) < 0) {
			node1 = left;
			node2 = right;
		}
		else {
			node1 = right;
			node2 = left;
		}
		
		Equality<QuerySource> nodeEquality = queryEqualityTable.get(equality);
		boolean alreadyExists = true;
		if (nodeEquality == null) {
			alreadyExists = false;
			nodeEquality = addEquality(equality, node1, node2, queryEqualityTable, false);
		}
		QuerySource source = nodeEquality.getSource();
		source.addClause(clause);
		
		if (alreadyExists) return null;
		
		if (equality.isPositive()) node1.addQueryEquality(nodeEquality);
		else node1.addQueryInequality(nodeEquality);
		
		QueryResult result = solver.addQuery(node1, node2, source, equality.isPositive());
		if (result!=null) removeQuery(result.getEquality());
		
		return result;
	}
	
	public void backtrack(Level level) {
		// TODO lazy backtrack
		
		// 1 backtrack sources
		for (Iterator<Entry<IEquality, Equality<FactSource>>> iter = factEqualityTable.entrySet().iterator(); iter.hasNext();) {
			Entry<IEquality, Equality<FactSource>> entry = iter.next();
			FactSource source = entry.getValue().getSource();
			source.backtrack(level);
			if (!source.isValid()) iter.remove();
		}
		for (Iterator<Entry<IEquality, Equality<QuerySource>>> iter = queryEqualityTable.entrySet().iterator(); iter.hasNext();) {
			Entry<IEquality, Equality<QuerySource>> entry = iter.next();
			QuerySource source = entry.getValue().getSource();
			source.backtrack(level);
			if (!source.isValid()) iter.remove();
		}

		// 2 backtrack nodes
		for (Node node : constantTable.values()) {
			node.backtrack();
		}
		// 3 backtrack sourcetable
		table.backtrack(level);
		
		// 4 add equalities
		for (Equality<FactSource> equality : equalities) {
			if (equality.getSource().isValid()) {
				IFactResult result = solver.addFactEquality(equality.getLeft(), equality.getRight(), equality.getSource());
				assert result == null;
			}
		}
	}
	
	private Node getNodeAndAddConstant(Constant constant) {
		if (constantTable.containsKey(constant)) return constantTable.get(constant);
		else {
			Node node = new Node(constant.getName());
			constantTable.put(constant, node);
			return node;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Source> Equality<T> addEquality(IEquality equality, Node node1, Node node2, Hashtable<IEquality, Equality<T>> table, boolean fact) {
		assert !table.containsKey(equality);
		
		Source source = fact?new Source.FactSource(equality):new Source.QuerySource(equality);
		Equality<T> nodeEquality = new Equality(node1, node2, source);
		table.put(equality, nodeEquality);
		return nodeEquality;
	}

}
 