package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;

public class Instantiation {
	private QuerySource source;
	private Node node;
	
	public Instantiation(Node node, QuerySource source) {
		this.source = source;
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
	
	public QuerySource getSource() {
		return source;
	}
	
	private Map<Node, Level> savedInstantiations = new HashMap<Node, Level>();
	
	public void saveInstantiation(Level level, Node node) {
		savedInstantiations.put(node, level);
	}
	
	public boolean hasInstantiation(Node node) {
		return savedInstantiations.containsKey(node);
	}
	
	public void backtrack(Level level) {
		for (Iterator<Entry<Node, Level>> iter = savedInstantiations.entrySet().iterator(); iter.hasNext();) {
			Entry<Node, Level> entry = iter.next();
			if (level.isAncestorOf(entry.getValue())) iter.remove();
		}
	}
	
}
