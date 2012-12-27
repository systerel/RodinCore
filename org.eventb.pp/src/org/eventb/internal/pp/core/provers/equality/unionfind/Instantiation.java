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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;

public final class Instantiation {

	private final QuerySource source;
	private final Node node;
	
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
	
	@Override
	public String toString() {
		return node.toString()/*+"{"+source.toString()+"}"*/;
	}
	
}
