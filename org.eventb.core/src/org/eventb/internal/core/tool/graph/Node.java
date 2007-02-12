/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool.graph;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Stefan Hallerstede
 *
 */
public class Node<T> implements Iterable<Node<T>> {

	@Override
	public boolean equals(Object obj) {
		return id.equals(((Node) obj).id);
	}

	@Override
	public String toString() {
		return id;
	}

	private final T object;
	private final String id;
	private List<String> predecs;
	
	protected List<Node<T>> succ;
	protected int count;
	
	public Node(T object, String id, String[] predecs) {
		this.id = id;
		this.object = object;
		this.predecs = Arrays.asList(predecs);
		
		count = 0;
		succ = new LinkedList<Node<T>>();
	}
	
	public void connect(Graph<T> graph) {
		for (String predec : predecs) {
			Node<T> node = graph.getNode(predec);
			if (node == null)
				throw new IllegalStateException(
						"[" + graph.getName() + "] Unsatisfied dependency: " + predec, null);
			boolean incr = node.addSucc(this);
			if (incr) 
				count++;
		}
	}
	
	public boolean addSucc(Node<T> node) {
		if (succ.contains(node))
			return false;
		succ.add(node);
		return true;
	}

	public void addPredec(String p) {
		if (predecs.contains(p))
			return;
		predecs.add(p);
	}
	
	public List<String> getPredecs() {
		return predecs;
	}
	
	public String getId() {
		return id;
	}
	
	public T getObject() {
		return object;
	}
	
	public int getCount() {
		return count;
	}
	
	public void decCount() {
		assert count > 0;
		count--;
	}
	
	public Iterator<Node<T>> iterator() {
		return succ.iterator();
	}
}
