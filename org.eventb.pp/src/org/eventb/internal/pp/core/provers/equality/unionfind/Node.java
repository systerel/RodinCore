/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;

/**
 * Node in the union-find data structure of equal constants.
 * <p>
 * Each node but the root of the tree has a parent which points 
 * towards the root. The root of a tree contains the information
 * of all nodes in the tree. This information is located in the 
 * data structrue {@link #rootFactsInequalities}, {@link #rootInstantiations},
 * {@link #rootQueryEqualities}, {@link #rootQueryInequalities}.
 *
 * @author François Terrier
 */
public final class Node implements Comparable<Node> {
	
	private final Constant constant;
	private Node parent;

	public Node(Constant constant) {
		this.constant = constant;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public Constant getConstant() {
		return constant;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public boolean isRoot() {
		return parent == null;
	}

	public void deleteRootInfos() {
		rootFactsInequalities.clear();
		rootQueryEqualities.clear();
		rootQueryInequalities.clear();
		rootInstantiations.clear();
	}
	
	public void backtrack() {
		deleteRootInfos();
		parent = null;
		for (Iterator<Equality<FactSource>> iter = factsInequalities.iterator(); iter.hasNext();) {
			Equality<FactSource> equality = iter.next();
			if (equality.getSource().isValid()) {
				RootInfo<FactSource> info = getRootInfo(equality);
				rootFactsInequalities.add(info);
			}
			else iter.remove();
		}
		for (Iterator<Equality<QuerySource>> iter = queryEqualities.iterator(); iter.hasNext();) {
			Equality<QuerySource> equality = iter.next();
			if (equality.getSource().isValid()) {
				RootInfo<QuerySource> info = getRootInfo(equality);
				rootQueryEqualities.put(info.getEquality(), info);
			}
			else iter.remove();
		}
		for (Iterator<Equality<QuerySource>> iter = queryInequalities.iterator(); iter.hasNext();) {
			Equality<QuerySource> equality = iter.next();
			if (equality.getSource().isValid()) {
				RootInfo<QuerySource> info = getRootInfo(equality);
				rootQueryInequalities.put(info.getEquality(), info);
			}
			else iter.remove();
		}
		// instantiations
		for (Iterator<Instantiation> iter = instantiations.iterator(); iter.hasNext();) {
			Instantiation instantiation = iter.next();
			if (instantiation.getSource().isValid()) {
				rootInstantiations.add(instantiation);
			}
			else iter.remove();
		}
	}
	
	private <T extends Source> RootInfo<T> getRootInfo(Equality<T> equality) {
		RootInfo<T> info = null;
		if (equality.getLeft() == this) {
			info = new RootInfo<T>(equality.getRight(),equality);
		}
		else if (equality.getRight() == this) {
			info = new RootInfo<T>(equality.getLeft(),equality);
		}
		else {
			assert false;
		}
		return info;
	}
	
	private Set<Equality<FactSource>> factsInequalities = new HashSet<Equality<FactSource>>();
	private Set<Equality<QuerySource>> queryEqualities = new HashSet<Equality<QuerySource>>();
	private Set<Equality<QuerySource>> queryInequalities = new HashSet<Equality<QuerySource>>();
	private Set<Instantiation> instantiations = new HashSet<Instantiation>();
	
	// return facts and query equalities and disequalities belonging to this node
	public void addFactInequality(Equality<FactSource> equality) {
		factsInequalities.add(equality);
	}
	
	public void addQueryEquality(Equality<QuerySource> equality) {
		queryEqualities.add(equality);
	}
	
	public void removeQueryEquality(Equality<QuerySource> equality) {
		queryEqualities.remove(equality);
	}
	
	public void addQueryInequality(Equality<QuerySource> equality) {
		queryInequalities.add(equality);
	}
	
	public void removeQueryInequality(Equality<QuerySource> equality) {
		queryInequalities.remove(equality);
	}
	
	public void addInstantiation(Instantiation instantiation) {
		instantiations.add(instantiation);
	}
	
	public void removeInstantiation(Instantiation instantiation) {
		instantiations.remove(instantiation);
	}
	
	private Set<RootInfo<FactSource>> rootFactsInequalities = new LinkedHashSet<RootInfo<FactSource>>();
	private Map<Equality<QuerySource>, RootInfo<QuerySource>> rootQueryEqualities = new LinkedHashMap<Equality<QuerySource>, RootInfo<QuerySource>>();
	private Map<Equality<QuerySource>, RootInfo<QuerySource>> rootQueryInequalities = new LinkedHashMap<Equality<QuerySource>, RootInfo<QuerySource>>();
	private Set<Instantiation> rootInstantiations = new LinkedHashSet<Instantiation>();
	
	public void addRootInstantiation(Instantiation instantiation) {
		rootInstantiations.add(instantiation);
	}
	
	public void addRootFactInequality(RootInfo<FactSource> info) {
		rootFactsInequalities.add(info);
	}
	
	public void addRootFactsInequalities(List<RootInfo<FactSource>> infos) {
		rootFactsInequalities.addAll(infos);
	}
	
	public void removeRootFactInequality(RootInfo<FactSource> info) {
		rootFactsInequalities.remove(info);
	}
	
	public void addRootQueryEquality(RootInfo<QuerySource> info) {
		rootQueryEqualities.put(info.getEquality(), info);
	}
	
	public void removeRootQueryEquality(Equality<QuerySource> info) {
		rootQueryEqualities.remove(info);
	}
	
	public void addRootQueryEqualities(List<RootInfo<QuerySource>> infos) {
		for (RootInfo<QuerySource> info : infos) {
			rootQueryEqualities.put(info.getEquality(), info);
		}
	}
	
	public void addRootQueryInequality(RootInfo<QuerySource> info) {
		rootQueryInequalities.put(info.getEquality(), info);
	}
	
	public void removeRootQueryInequality(Equality<QuerySource> info) {
		rootQueryInequalities.remove(info);
	}
	
	public void addRootQueryInequalities(List<RootInfo<QuerySource>> infos) {
		for (RootInfo<QuerySource> info : infos) {
			rootQueryInequalities.put(info.getEquality(), info);
		}
	}
	
	// root informations
	public List<RootInfo<FactSource>> getRootFactsInequalities() {
		return new ArrayList<RootInfo<FactSource>>(rootFactsInequalities);
	}
	public List<RootInfo<QuerySource>> getRootQueryEqualities() {
		return new ArrayList<RootInfo<QuerySource>>(rootQueryEqualities.values());
	}
	public List<RootInfo<QuerySource>> getRootQueryInequalities() {
		return new ArrayList<RootInfo<QuerySource>>(rootQueryInequalities.values());
	}
	public List<Instantiation> getRootInstantiations() {
		return new ArrayList<Instantiation>(rootInstantiations);
	}
	
	@Override
	public int compareTo(Node o) {
		return constant.compareTo(o.constant);
	}

	public Node getRoot() {
		Node tmp = this;
		while (!tmp.isRoot()) {
			tmp = tmp.getParent();
		}
		return tmp;
	}
	
	@Override
	public String toString() {
		return constant.getName();
	}
}
