/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool.graph;

import java.util.LinkedList;
import java.util.List;

import org.eventb.core.tool.IModule;
import org.eventb.internal.core.tool.ModuleDesc;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ModuleNode extends Node<ModuleDesc<? extends IModule>> {
	
	private final List<ModuleNode> childFilters; 
	
	public ModuleNode(ModuleDesc<? extends IModule> object, String id, String[] predecs) {
		super(object, id, predecs);
		childFilters = new LinkedList<ModuleNode>();
	}
	
	protected void connectParent(ModuleGraph graph) {
		String parent = getObject().getParent();
		Node<ModuleDesc<? extends IModule>> node = graph.getNode(parent);
		if (node == null)
			throw new IllegalStateException(
					"Cannot find parent: " + parent + " for node: " + getId(), null);
		boolean incr = node.addSucc(this);
		if (incr)
			count++;
	}
	
	protected void addChildFilter(ModuleNode node) {
		if (childFilters.contains(node))
			return;
		childFilters.add(node);
	}
	
	public List<ModuleNode> getChildFilters() {
		return childFilters;
	}
	
	public abstract void storeFilterInParent(ModuleNode node);
	
}
