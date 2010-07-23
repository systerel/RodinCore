/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eventb.internal.core.tool.ModuleDesc;
import org.eventb.internal.core.tool.types.IModule;

/**
 * @author Stefan Hallerstede
 *
 */
public class ParentGraph extends Graph<ModuleDesc<? extends IModule>> implements Iterable<String> {

	public ParentGraph(String creator) {
		super(creator);
		roots = new ArrayList<Node<ModuleDesc<? extends IModule>>>();
	}

	private List<Node<ModuleDesc<? extends IModule>>> roots;
	
	private static class PGIterator implements Iterator<String> {
		
		private final List<Node<ModuleDesc<? extends IModule>>> sorted;
		int index;
		
		public PGIterator(List<Node<ModuleDesc<? extends IModule>>> sorted) {
			this.sorted = sorted;
			index = sorted.size();
		}

		@Override
		public boolean hasNext() {
			return index > 0;
		}

		@Override
		public String next() {
			index--;
			String id = sorted.get(index).getId();
			return id;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Override
	public Node<ModuleDesc<? extends IModule>> add(ModuleDesc<? extends IModule> object) {
		Node<ModuleDesc<? extends IModule>> node = super.add(object);
		if (object.getParent() == null)
			roots.add(node);
		return node;
	}

	private static final String[] NO_PARENT = new String[0];
	
	@Override
	protected Node<ModuleDesc<? extends IModule>> createNode(ModuleDesc<? extends IModule> object) {
		String parent = object.getParent();
		String[] parents = parent == null ? NO_PARENT : new String[] {parent};
		return new ParentNode(object, object.getId(), parents, this);
	}

	@Override
	public String getName() {
		return super.getName() + " Parent graph";
	}

	// returns the ids of all nodes from leafs down to the roots
	@Override
	public Iterator<String> iterator() {
		return new PGIterator(getSorted());
	}

}
