/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;

/**
 * This class is the external interface to the union-find data
 * structures formed by nodes that are equal. It contains all
 * necessary operations (add fact, add query, add instantiation). 
 * It keeps the {@link SourceTable} up to date. Backtracking
 * and removal of query and instantiations is not done at this level.
 *
 * @author François Terrier
 *
 */
public final class EqualitySolver {

	private final SourceTable sourceTable;
	// for dumping only
	private final Set<Node> nodes = new HashSet<Node>(); 
	
	public EqualitySolver(SourceTable table) {
		this.sourceTable = table;
	}
	
	private void addNode(Node node) {
		nodes.add(node);
	}
	
	public FactResult addFactEquality(Equality<FactSource> equality) {
		Node node1 = equality.getLeft();
		Node node2 = equality.getRight();
		
		addNode(node1);
		addNode(node2);
		assert node1.compareTo(node2) < 0;
		
		// add the source to the table
		sourceTable.addSource(node1, node2, equality.getSource());
		Set<FactSource> sourceToRoot1 = new HashSet<FactSource>();
		Node root = find(node1,sourceToRoot1);
		Set<FactSource> sourceToRoot2 = new HashSet<FactSource>();
		Node child = find(node2,sourceToRoot2);
		
		if (root == child) return null;
		
		Set<FactSource> unionSource = new HashSet<FactSource>();
		unionSource.addAll(sourceToRoot1);
		unionSource.addAll(sourceToRoot2);
		unionSource.add(equality.getSource());
		
		union(root,child,unionSource);
		
		// 1 check the fact inequalities
		Set<FactSource> contradictionSource;
		contradictionSource = checkContradiction(root);
		if (contradictionSource != null) return new FactResult(contradictionSource);

		// 2 check queries
		List<QueryResult> queryResultList = new ArrayList<QueryResult>();
		queryResultList.addAll(checkQueryContradiction(root, true));
		queryResultList.addAll(checkQueryContradiction(root, false));
		
		if (!queryResultList.isEmpty()) return new FactResult(queryResultList,false);
		return null;
	}
	
	private List<QueryResult> checkQueryContradiction(Node root, boolean isEquality) {
		List<QueryResult> result = new ArrayList<QueryResult>();
		for (RootInfo<QuerySource> info : isEquality?root.getRootQueryEqualities():root.getRootQueryInequalities()) {
			Equality<QuerySource> equality = info.getEquality();
			if (!equality.getSource().isValid()) {
				if (isEquality) root.removeRootQueryEquality(equality);
				else root.removeRootQueryInequality(equality);
			}
			else if (info.updateAndGetInequalNode()==root) {
				// contradiction
				Set<FactSource> contradictionSource = source(equality.getLeft(),equality.getRight());
				result.add(new QueryResult(equality.getSource(),contradictionSource,isEquality));
			}
		}
		return result;
	}
	
	private Set<FactSource> checkContradiction(Node node) {
		Set<FactSource> source = null;
		Level level = null;
		for (RootInfo<FactSource> info : node.getRootFactsInequalities()) {
			Equality<FactSource> equality = info.getEquality();
			if (info.updateAndGetInequalNode()==node) {
				// contradiction
				Set<FactSource> contradictionSource = source(equality.getLeft(), equality.getRight());
				contradictionSource.add(equality.getSource());
				Level newLevel = Source.getLevel(contradictionSource);
				if (level == null || newLevel.isAncestorOf(level)) {
					source = contradictionSource;
					level = newLevel;
				}
			}
		}
		return source;
	}

	public FactResult addFactInequality(Equality<FactSource> equality) {
		Node node1 = equality.getLeft();
		Node node2 = equality.getRight();
		
		addNode(node1);
		addNode(node2);
		assert node1.compareTo(node2) < 0;
		
		Set<FactSource> sourceToRoot1 = new HashSet<FactSource>();
		Node root1 = find(node1, sourceToRoot1);
		Set<FactSource> sourceToRoot2 = new HashSet<FactSource>();
		Node root2 = find(node2, sourceToRoot2);
		if (root1 == root2) {
			Set<FactSource> contradictionSource = exclusiveOr(sourceToRoot1, sourceToRoot2);
			contradictionSource.add(equality.getSource());
			return new FactResult(contradictionSource);
		}
		
		// add root info
		RootInfo<FactSource> newInfo1 = new RootInfo<FactSource>(root2, equality);
		root1.addRootFactInequality(newInfo1);
		RootInfo<FactSource> newInfo2 = new RootInfo<FactSource>(root1, equality);
		root2.addRootFactInequality(newInfo2);
		
		// check queries
		List<QueryResult> resultList = new ArrayList<QueryResult>();
		resultList.addAll(checkInequalityContradictionWithQuery(root1, root2, node1, node2, equality.getSource(), false));
		resultList.addAll(checkInequalityContradictionWithQuery(root1, root2, node1, node2, equality.getSource(), true));
		
		// check instantiations left and right
		List<InstantiationResult> instantiationResults = new ArrayList<InstantiationResult>();
		instantiationResults.addAll(checkInequalityInstantiation(root1, root2, node1, node2, equality.getSource()));
		instantiationResults.addAll(checkInequalityInstantiation(root2, root1, node2, node1, equality.getSource()));
		
		if (!resultList.isEmpty() && !instantiationResults.isEmpty()) return new FactResult(resultList, instantiationResults);
		else if (!resultList.isEmpty()) return new FactResult(resultList, false);
		else if (!instantiationResults.isEmpty()) return new FactResult(instantiationResults);
		return null;
	}
	
	private List<InstantiationResult> checkInequalityInstantiation(Node root1, Node root2, Node node1, Node node2, FactSource source) {
		List<InstantiationResult> instantiationResults = new ArrayList<InstantiationResult>();
		for (Instantiation instantiation : root1.getRootInstantiations()) {
			Set<FactSource> sourceToInsRoot = new HashSet<FactSource>();
			Node instantiationRoot = find(instantiation.getNode(), sourceToInsRoot);
			Node other = null;
			Node same = null;
			Node otherRoot = null;
			if (instantiationRoot == root1) {
				other = node2;
				same = node1;
				otherRoot = root2;
			}
			else if (instantiationRoot == root2) {
				other = node1;
				same = node2;
				otherRoot = root1;
			}
			else assert false;
			if (instantiation.hasInstantiation(otherRoot)) continue;
			
			Set<FactSource> instantiationSource = source(same, instantiation.getNode());
			instantiationSource.add(source);
			InstantiationResult result = new InstantiationResult(other, instantiation.getSource(), instantiationSource);
			
			instantiation.saveInstantiation(result.getLevel(), other);
			instantiationResults.add(result);
		}
		return instantiationResults;
	}
	
	private List<QueryResult> checkInequalityContradictionWithQuery(Node infoNode, Node root, Node node1, Node node2, FactSource source, boolean isPositive) {
		List<QueryResult> resultList = new ArrayList<QueryResult>(); 
		for (RootInfo<QuerySource> info : isPositive?infoNode.getRootQueryInequalities():infoNode.getRootQueryEqualities()) {
			Equality<QuerySource> equality = info.getEquality();
			if (!equality.getSource().isValid()) {
				if (isPositive) infoNode.removeRootQueryInequality(equality);
				else infoNode.removeRootQueryEquality(equality);
			}
			else if (info.updateAndGetInequalNode() == root) {
				Node left = equality.getLeft();
				Node right = equality.getRight();
				
				Set<FactSource> contradictionSource = getContradictionSourceInTwoTrees(node1, node2, left, right);
				contradictionSource.add(source);
				resultList.add(new QueryResult(equality.getSource(),contradictionSource,isPositive));
			}
		}
		return resultList;
	}
	
	private Set<FactSource> getContradictionSourceInTwoTrees(Node node1, Node node2, Node left, Node right) {
		Set<FactSource> sourceToRootNode1 = new HashSet<FactSource>();
		Node rootNode1 = find(node1, sourceToRootNode1);
		Set<FactSource> sourceToRootNode2 = new HashSet<FactSource>();
		Node rootNode2 = find(node2, sourceToRootNode2);
		Set<FactSource> sourceToRootLeft = new HashSet<FactSource>();
		Node rootLeft = find(left, sourceToRootLeft);
		Set<FactSource> sourceToRootRight = new HashSet<FactSource>();
		Node rootRight = find(right, sourceToRootRight);
		Set<FactSource> source1 = null;
		Set<FactSource> source2 = null;
		if (rootNode1 == rootLeft) {
			assert rootNode2 == rootRight;
			source1 = exclusiveOr(sourceToRootNode1, sourceToRootLeft);
			source2 = exclusiveOr(sourceToRootNode2, sourceToRootRight);
		}
		else if (rootNode1 == rootRight) {
			assert rootNode2 == rootLeft;
			source1 = exclusiveOr(sourceToRootNode1, sourceToRootRight);
			source2 = exclusiveOr(sourceToRootNode2, sourceToRootLeft);
		}
		else assert false;
		
		// TODO no need for XOR here ?
		Set<FactSource> contradictionSource = exclusiveOr(source1, source2);
		return contradictionSource;
	}
	
	public QueryResult addQuery(Equality<QuerySource> equality, boolean isPositive) {
		Node node1 = equality.getLeft();
		Node node2 = equality.getRight();
		
		addNode(node1);
		addNode(node2);
		assert node1.compareTo(node2) < 0:"wrong order: "+node1+"="+node2;
		
		Set<FactSource> sourceToRoot1 = new HashSet<FactSource>();
		Node root1 = find(node1, sourceToRoot1);
		Set<FactSource> sourceToRoot2 = new HashSet<FactSource>();
		Node root2 = find(node2, sourceToRoot2);
		
		// check if already equal
		if (root1 == root2) {
			Set<FactSource> contradictionSource = exclusiveOr(sourceToRoot1, sourceToRoot2);
			return new QueryResult(equality.getSource(), contradictionSource, isPositive);
		}
		
		// check if already unequal
		QueryResult result;
		result = checkQueryContradictionWithInequality(node1, node2, root1, root2, equality.getSource(), !isPositive);
		if (result != null) return result;
		result = checkQueryContradictionWithInequality(node2, node1, root2, root1, equality.getSource(), !isPositive);
		if (result != null) return result;

		addRootInfo(equality, isPositive, root2, root1);
		addRootInfo(equality, isPositive, root1, root2);
		
		return null;
	}

	private void addRootInfo(Equality<QuerySource> equality,
			boolean isPositive, Node root1, Node root2) {
		RootInfo<QuerySource> info2 = new RootInfo<QuerySource>(root1, equality);
		if (isPositive) root2.addRootQueryEquality(info2);
		else root2.addRootQueryInequality(info2);
	}
	
	
	private QueryResult checkQueryContradictionWithInequality(Node node1, Node node2, Node root1, Node root2, QuerySource source, boolean isPositive) {
		for (RootInfo<FactSource> info : root1.getRootFactsInequalities()) {
			if (info.updateAndGetInequalNode()==root2) {
				// contradiction
				Set<FactSource> contradictionSource = getContradictionSourceInTwoTrees(node1, node2, info.getEquality().getLeft(), info.getEquality().getRight());
				contradictionSource.add(info.getEquality().getSource());
				return new QueryResult(source, contradictionSource, isPositive);
			}
		}
		return null;
	}
	
	public List<InstantiationResult> addInstantiation(Instantiation instantiation) {
		Node node = instantiation.getNode();

		Set<FactSource> sourceToRoot = new HashSet<FactSource>();
		Node root = find(node, sourceToRoot);
		
		Map<Node, InstantiationResult> resultMap = new HashMap<Node, InstantiationResult>();
		for (RootInfo<FactSource> inequalityInfo : root.getRootFactsInequalities()) {
			if (!instantiation.hasInstantiation(inequalityInfo.updateAndGetInequalNode())) {
				Equality<FactSource> inequality = inequalityInfo.getEquality();
				Set<FactSource> sourceToRootLeft = new HashSet<FactSource>();
				Node rootLeft = find(inequality.getLeft(), sourceToRootLeft);
				Set<FactSource> sourceToRootRight = new HashSet<FactSource>();
				Node rootRight = find(inequality.getRight(), sourceToRootRight);
				Node other = null;
				Node same = null;
				Node otherRoot = null;
				if (root == rootRight) {
					same = inequality.getRight();
					other = inequality.getLeft();
					otherRoot = rootLeft;
				}
				else if (root == rootLeft) {
					same = inequality.getLeft();
					other = inequality.getRight();
					otherRoot = rootRight;
				}
				else assert false;
				Set<FactSource> instantiationSource = source(same, node);
				instantiationSource.add(inequality.getSource());
				InstantiationResult result = new InstantiationResult(other, instantiation.getSource(), instantiationSource);
				if (resultMap.containsKey(otherRoot)) {
					InstantiationResult oldResult = resultMap.get(otherRoot);
					if (result.getLevel().isAncestorOf(oldResult.getLevel())) {
						resultMap.put(otherRoot, result);
					}
				}
				else {
					resultMap.put(otherRoot, result);
				}
			}
		}
		
		for (Entry<Node, InstantiationResult> result : resultMap.entrySet()) {
			instantiation.saveInstantiation(result.getValue().getLevel(), result.getKey());
		}
		
		// add instantiation to root
		root.addRootInstantiation(instantiation);
		
		if (resultMap.isEmpty()) return null;
		return new ArrayList<InstantiationResult>(resultMap.values());
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
		Set<String> result = new LinkedHashSet<String>();
		for (Node node : nodes) {
			if (!node.isRoot()) result.add(node.toString()+"->"+node.getParent().toString());
			if (!node.getRootFactsInequalities().isEmpty()) {
				for (RootInfo<FactSource> info : node.getRootFactsInequalities()) {
					result.add(node.toString()+"[F, ≠"+info.updateAndGetInequalNode()+"]");
				}
			}
			if (!node.getRootQueryEqualities().isEmpty()) {
				for (RootInfo<QuerySource> info : node.getRootQueryEqualities()) {
					result.add(node.toString()+"[Q, ="+info.updateAndGetInequalNode()+"]");
				}
			}
			if (!node.getRootQueryInequalities().isEmpty()) {
				for (RootInfo<QuerySource> info : node.getRootQueryInequalities()) {
					result.add(node.toString()+"[Q, ≠"+info.updateAndGetInequalNode()+"]");
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
