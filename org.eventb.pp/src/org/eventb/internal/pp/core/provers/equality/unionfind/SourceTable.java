package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;

public class SourceTable {

	private Hashtable<Node, Hashtable<Node, Set<FactSource>>> table = new Hashtable<Node, Hashtable<Node, Set<FactSource>>>();
	
	// adds the source at the given entry if there is no existing source
	// of a higher priority
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
	
	public void addSource(Node node1, Node node2, FactSource source) {
		Set<FactSource> sourceList = new HashSet<FactSource>();
		sourceList.add(source);
		addSource(node1, node2, sourceList);
	}
	
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
	
	public void backtrack(Level level) {
		table = new Hashtable<Node, Hashtable<Node, Set<FactSource>>>();
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
