package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;

public class EqualitySolver {

	private SourceTable sourceTable;
	private Set<Node> nodes = new HashSet<Node>(); 
	
	public EqualitySolver(SourceTable table) {
		this.sourceTable = table;
	}
	
	private void addNode(Node node) {
		nodes.add(node);
	}
	
	public FactResult addFactEquality(Node node1, Node node2, FactSource source) {
		addNode(node1);
		addNode(node2);
		assert node1.compareTo(node2) < 0;
		
		// add the source to the table
		sourceTable.addSource(node1, node2, source);
		Set<FactSource> sourceToRoot1 = new HashSet<FactSource>();
		Node root1 = find(node1,sourceToRoot1);
		Set<FactSource> sourceToRoot2 = new HashSet<FactSource>();
		Node root2 = find(node2,sourceToRoot2);
		
		if (root1 == root2) return null;
		
		Set<FactSource> unionSource = new HashSet<FactSource>();
		unionSource.addAll(sourceToRoot1);
		unionSource.addAll(sourceToRoot2);
		unionSource.add(source);
		
		Node root,child;
		// TODO check if this is necessary
		if (root1.compareTo(root2) < 0) {
			root = root1;
			child = root2;
		}
		else {
			root = root2;
			child = root1;
		}
		union(root,child,unionSource);
		
		// 1 check the fact inequalities
		Set<FactSource> contradictionSource;
		contradictionSource = checkContradiction(root);
		if (contradictionSource != null) return new FactResult(contradictionSource);

		// 2 check queries
		List<QueryResult> queryResultList = new ArrayList<QueryResult>();
		queryResultList.addAll(checkQueryContradiction(root, true));
		queryResultList.addAll(checkQueryContradiction(root, false));
		
		if (!queryResultList.isEmpty()) return new FactResult(queryResultList);
		return null;
	}
	
	private List<QueryResult> checkQueryContradiction(Node root, boolean isEquality) {
		List<QueryResult> result = new ArrayList<QueryResult>();
		for (RootInfo<QuerySource> info : isEquality?root.getRootQueryEqualities():root.getRootQueryInequalities()) {
			if (!info.getSource().isValid()) {
				if (isEquality) root.removeRootQueryEquality(info);
				else root.removeRootQueryInequality(info);
			}
			else if (info.updateAndGetInequalNode()==root) {
				// contradiction
				Set<FactSource> contradictionSource = source(info.getEquality().getLeft(),info.getEquality().getRight());
				result.add(new QueryResult(info.getSource(),contradictionSource,isEquality));
				if (isEquality) root.removeRootQueryEquality(info);
				else root.removeRootQueryInequality(info);
			}
		}
		return result;
	}
	
	private Set<FactSource> checkContradiction(Node node) {
		for (RootInfo<FactSource> info : node.getRootFactsInequalities()) {
//			if (!info.getSource().isValid()) {
//				node.removeRootFactInequality(info);
//			}
			if (info.updateAndGetInequalNode()==node) {
				// contradiction
				Set<FactSource> contradictionSource = source(info.getEquality().getLeft(), info.getEquality().getRight());
				contradictionSource.add(info.getSource());
				return contradictionSource;
			}
		}
		return null;
	}

	public FactResult addFactInequality(Node node1, Node node2, FactSource source) {
		addNode(node1);
		addNode(node2);
		assert node1.compareTo(node2) < 0;
		
		Set<FactSource> sourceToRoot1 = new HashSet<FactSource>();
		Node root1 = find(node1, sourceToRoot1);
		Set<FactSource> sourceToRoot2 = new HashSet<FactSource>();
		Node root2 = find(node2, sourceToRoot2);
		if (root1 == root2) {
			Set<FactSource> contradictionSource = exclusiveOr(sourceToRoot1, sourceToRoot2);
			contradictionSource.add(source);
			return new FactResult(contradictionSource);
		}
		
		// add root info
		RootInfo<FactSource> newInfo = new RootInfo<FactSource>(root2, new Equality<FactSource>(node1, node2, source));
		root1.addRootFactInequality(newInfo);

		// check queries
		List<QueryResult> resultList = new ArrayList<QueryResult>();
		resultList.addAll(checkInequalityContradictionWithQuery(root2, root1, node1, node2, source, false));
		resultList.addAll(checkInequalityContradictionWithQuery(root1, root2, node1, node2, source, false));
		resultList.addAll(checkInequalityContradictionWithQuery(root2, root1, node1, node2, source, true));
		resultList.addAll(checkInequalityContradictionWithQuery(root1, root2, node1, node2, source, true));
		
		if (!resultList.isEmpty()) return new FactResult(resultList);
		return null;
	}
	
	private List<QueryResult> checkInequalityContradictionWithQuery(Node root, Node infoNode, Node node1, Node node2, FactSource source, boolean isPositive) {
		List<QueryResult> resultList = new ArrayList<QueryResult>(); 
		for (RootInfo<QuerySource> info : isPositive?infoNode.getRootQueryInequalities():infoNode.getRootQueryEqualities()) {
			if (!info.getSource().isValid()) {
				if (isPositive) root.removeRootQueryInequality(info);
				else root.removeRootQueryEquality(info);
			}
			else if (info.updateAndGetInequalNode() == root) {
				Node left = info.getEquality().getLeft();
				Node right = info.getEquality().getRight();
				
				Set<FactSource> sourceLeft = source(node1, left);
				Set<FactSource> sourceRight = source(node2, right);
				// TODO no need for XOR here ?
				Set<FactSource> contradictionSource = exclusiveOr(sourceLeft, sourceRight);
				contradictionSource.add(source);
				resultList.add(new QueryResult(info.getSource(),contradictionSource,isPositive));
				if (isPositive) infoNode.removeRootQueryInequality(info);
				else infoNode.removeRootQueryEquality(info);
			}
		}
		return resultList;
	}
	
	public QueryResult addQuery(Node node1, Node node2, QuerySource source, boolean isPositive) {
		addNode(node1);
		addNode(node2);
		assert node1.compareTo(node2) < 0;
		
		Set<FactSource> sourceToRoot1 = new HashSet<FactSource>();
		Node root1 = find(node1, sourceToRoot1);
		Set<FactSource> sourceToRoot2 = new HashSet<FactSource>();
		Node root2 = find(node2, sourceToRoot2);
		
		// check if already equal
		if (root1 == root2) {
			Set<FactSource> contradictionSource = exclusiveOr(sourceToRoot1, sourceToRoot2);
			return new QueryResult(source, contradictionSource, isPositive);
		}
		
		// check if already unequal
		QueryResult result;
		result = checkQueryContradictionWithInequality(node1, node2, root1, root2, source, !isPositive);
		if (result != null) return result;
		result = checkQueryContradictionWithInequality(node2, node1, root2, root1, source, !isPositive);
		if (result != null) return result;

		// add root info
		RootInfo<QuerySource> info = new RootInfo<QuerySource>(root2, new Equality<QuerySource>(node1, node2, source));
		if (isPositive) root1.addRootQueryEquality(info);
		else root1.addRootQueryInequality(info);
		
		return null;
	}
	
	private QueryResult checkQueryContradictionWithInequality(Node node1, Node node2, Node root1, Node root2, QuerySource source, boolean isPositive) {
		for (RootInfo<FactSource> info : root1.getRootFactsInequalities()) {
//			if (!info.getSource().isValid()) {
//				root1.removeRootFactInequality(info);
//			}
			if (info.updateAndGetInequalNode()==root2) {
				// contradiction
				Node left = info.getEquality().getLeft();
				Node right = info.getEquality().getRight();
				
				Set<FactSource> sourceLeft = source(left, node1);
				Set<FactSource> sourceRight = source(right, node2);
				// TODO no need for XOR here ?
				Set<FactSource> contradictionSource = exclusiveOr(sourceLeft,sourceRight);
				contradictionSource.add(info.getSource());
				return new QueryResult(source, contradictionSource, isPositive);
			}
		}
		return null;
	}
	
	private void union(Node root, Node child, Set<FactSource> source) {
		// choose the root
		child.setParent(root);
		
		// set the source of the edge
		sourceTable.addSource(root, child, source);
		
		// take the equalities and put them on the new root
		root.addRootFactsInequalities(child.getRootFactsInequalities());
		root.addRootQueryEqualities(child.getRootQueryEqualities());
		root.addRootQueryInequalities(child.getRootQueryInequalities());

		child.deleteRootInfos();
	}
	
	private Node find(Node node, Set<FactSource> sourceToRoot) {
		if (node.isRoot()) return node;
		else {
			Set<FactSource> result = new HashSet<FactSource>();
			Node root = find(node.getParent(),result);
			
			Set<FactSource> temporarySource = exclusiveOr(result, sourceTable.getSource(node,node.getParent()));
			sourceTable.addSource(node, root, temporarySource);

			sourceToRoot.addAll(sourceTable.getSource(node, root));
			
			// we flatten the tree
			if (!node.getParent().isRoot()) {
				node.setParent(root);
				sourceTable.addSource(node, root, sourceToRoot);
			}
				
			return root;
		}
	}
	
	private Set<FactSource> source(Node node1, Node node2) {
		Set<FactSource> sourceToRoot1 = new HashSet<FactSource>();
		find(node1, sourceToRoot1);
		Set<FactSource> sourceToRoot2 = new HashSet<FactSource>();
		find(node2, sourceToRoot2);
		return exclusiveOr(sourceToRoot1, sourceToRoot2); 
	}
	
	private <T extends Source> Set<T> exclusiveOr(Set<T> source1, Set<T> source2) {
		Set<T> result = new HashSet<T>();
		for (T source : source1) {
			if (!source2.contains(source)) result.add(source);
		}
		for (T source : source2) {
			if (!source1.contains(source)) result.add(source);
		}
		return result;
	}

	public Set<String> dump(){
		Set<String> result = new HashSet<String>();
		for (Node node : nodes) {
			if (!node.isRoot()) result.add(node.toString()+"->"+node.getParent().toString());
			if (!node.getRootFactsInequalities().isEmpty()) {
				for (RootInfo<FactSource> info : node.getRootFactsInequalities()) {
					result.add(node.toString()+"[F, ≠"+info.updateAndGetInequalNode()+"]");
				}
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		return dump().toString();
	}
	
	public SourceTable getSourceTable() {
		return sourceTable;
	}
	
}
