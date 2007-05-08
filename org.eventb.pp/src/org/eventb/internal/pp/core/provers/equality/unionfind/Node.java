package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;

// each constant has a corresponding node information 
// TODO -> each constant is same object
public class Node implements Comparable<Node> {
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node tmp = (Node) obj;
			return name.equals(tmp.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return name;
	}

	private String name;
	private Node parent;

	public Node(String name) {
		this.name = name;
	}
	
	public Node getParent() {
		return parent;
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
	}
	
	public void backtrack() {
		deleteRootInfos();
		parent = null;
		for (Iterator<Equality<FactSource>> iter = factsInequalities.iterator(); iter.hasNext();) {
			Equality<FactSource> equality = iter.next();
			if (equality.getSource().isValid()) rootFactsInequalities.add(new RootInfo<FactSource>(equality.getRight(),equality));
			else iter.remove();
		}
		for (Iterator<Equality<QuerySource>> iter = queryEqualities.iterator(); iter.hasNext();) {
			Equality<QuerySource> equality = iter.next();
			if (equality.getSource().isValid()) rootQueryEqualities.add(new RootInfo<QuerySource>(equality.getRight(),equality));
			else iter.remove();
		}
		for (Iterator<Equality<QuerySource>> iter = queryInequalities.iterator(); iter.hasNext();) {
			Equality<QuerySource> equality = iter.next();
			if (equality.getSource().isValid()) rootQueryInequalities.add(new RootInfo<QuerySource>(equality.getRight(),equality));
			else iter.remove();
		}
	}
	
	private Set<Equality<FactSource>> factsInequalities = new HashSet<Equality<FactSource>>();
	private Set<Equality<QuerySource>> queryEqualities = new HashSet<Equality<QuerySource>>();
	private Set<Equality<QuerySource>> queryInequalities = new HashSet<Equality<QuerySource>>();
	
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
	
	private List<RootInfo<FactSource>> rootFactsInequalities = new ArrayList<RootInfo<FactSource>>();
	private List<RootInfo<QuerySource>> rootQueryEqualities = new ArrayList<RootInfo<QuerySource>>();
	private List<RootInfo<QuerySource>> rootQueryInequalities = new ArrayList<RootInfo<QuerySource>>();

	public void addRootFactInequality(RootInfo<FactSource> info) {
		rootFactsInequalities.add(info);
	}
	
	public void addRootFactsInequalities(List<RootInfo<FactSource>> infos) {
		rootFactsInequalities.addAll(infos);
	}
	
	public void addRootQueryEquality(RootInfo<QuerySource> info) {
		rootQueryEqualities.add(info);
	}
	
	public void removeRootFactInequality(RootInfo<FactSource> info) {
		rootFactsInequalities.remove(info);
	}
	
	public void removeRootQueryEquality(RootInfo<QuerySource> info) {
		rootQueryEqualities.remove(info);
	}
	
	public void addRootQueryEqualities(List<RootInfo<QuerySource>> infos) {
		rootQueryEqualities.addAll(infos);
	}
	
	public void addRootQueryInequality(RootInfo<QuerySource> info) {
		rootQueryInequalities.add(info);
	}
	
	public void removeRootQueryInequality(RootInfo<QuerySource> info) {
		rootQueryInequalities.remove(info);
	}
	
	public void addRootQueryInequalities(List<RootInfo<QuerySource>> infos) {
		rootQueryInequalities.addAll(infos);
	}
	
	// root informations
	public List<RootInfo<FactSource>> getRootFactsInequalities() {
		return new ArrayList<RootInfo<FactSource>>(rootFactsInequalities);
	}
	public List<RootInfo<QuerySource>> getRootQueryEqualities() {
		return new ArrayList<RootInfo<QuerySource>>(rootQueryEqualities);
	}
	public List<RootInfo<QuerySource>> getRootQueryInequalities() {
		return new ArrayList<RootInfo<QuerySource>>(rootQueryInequalities);
	}

	public int compareTo(Node o) {
		return name.compareTo(o.name);
	}
	
}
