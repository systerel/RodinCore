/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;

/**
 * Source table for storing the source of fact equalities.
 * <p>
 * In the source table, there exists an entry for each fact
 * equality that has been derived by the prover. Beside there
 * exists an entry for each link node-parent in the union-find
 * data structure.
 * 
 * @author François Terrier
 *
 */
public final class SourceTable {

	private final Hashtable<Node, Hashtable<Node, Set<FactSource>>> table = new Hashtable<Node, Hashtable<Node, Set<FactSource>>>();
	
	/**
	 * Adds the given set of fact source as a source of the equality between
	 * node1 and node2. Only adds it if there is no existing source between node1
	 * and node2 which has a higher priority.
	 * <p>
	 * Priority of sources are defined as such :
	 * <ul>
	 * <li>the source with the lowest level has the higher priority</li>
	 * <li>if sources have the same level, smaller sources have higher priority</li>
	 * </ul>
	 * 
	 * @param node1 the first node
	 * @param node2 the second node
	 * @param source the source
	 */
	public void addSource(Node node1, Node node2, Set<FactSource> source) {
		assert source.size() > 0;
		
		Node first,second;
		if (node1.compareTo(node2) < 0) {
			first = node1;
			second = node2;
		}
		else {
			first = node2;
			second = node1;
		}
		
		Hashtable<Node, Set<FactSource>> nodes = table.get(first);
		if (nodes == null) {
			nodes = new Hashtable<Node, Set<FactSource>>();
			table.put(first, nodes);
			nodes.put(second, source);
		}
		else {
			Set<FactSource> existingSource = nodes.get(second);
			if (existingSource == null) {
				nodes.put(second, source);
			}
			else {
				Level existingLevel = Source.getLevel(existingSource);
				Level newLevel = Source.getLevel(source);
				if (newLevel.isAncestorOf(existingLevel)) nodes.put(second, source);
				else if (newLevel.equals(existingLevel)) {
					if (source.size() < existingSource.size()) nodes.put(second, source);
				}
			}
		}
	}
	
	/**
	 * Adds the given set of fact source as a source of the equality between
	 * node1 and node2. Only adds it if there is no existing source between node1
	 * and node2 which has a higher priority.
	 * <p>
	 * Priority of sources are defined as such :
	 * <ul>
	 * <li>smaller sources have higher priority</li>
	 * <li>if sources have the same size, the one with the lowest level has the higher
	 * priority</li>
	 * </ul>
	 * 
	 * @param node1 the first node
	 * @param node2 the second node
	 * @param source the source
	 */
	public void addSource(Node node1, Node node2, FactSource source) {
		Set<FactSource> sourceList = new HashSet<FactSource>();
		sourceList.add(source);
		addSource(node1, node2, sourceList);
	}
	
	/**
	 * Gets the source of the equality between the two given nodes.
	 * 
	 * @param node1 the first node
	 * @param node2 the second node
	 * @return the source of the equality between the two nodes
	 */
	public Set<FactSource> getSource(Node node1, Node node2) {
		Node first,second;
		if (node1.compareTo(node2) < 0) {
			first = node1;
			second = node2;
		}
		else {
			first = node2;
			second = node1;
		}
		
		return table.get(first).get(second);
	}
	
	/**
	 * Clear this source table.
	 */
	public void clear() {
		table.clear();
	}
	
	public Set<String> dump() {
		Set<String> result = new HashSet<String>();
		for (Entry<Node, Hashtable<Node, Set<FactSource>>> table1 : table.entrySet()) {
			for (Entry<Node, Set<FactSource>> entry : table1.getValue().entrySet()) {
				result.add(table1.getKey()+","+entry.getKey()+entry.getValue());
			}
		}
		return result;
	}
	
}
