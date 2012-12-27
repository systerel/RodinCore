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
package org.eventb.internal.core.tool.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eventb.internal.core.tool.ModuleDesc;
import org.eventb.internal.core.tool.types.IModule;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ModuleNode extends Node<ModuleDesc<? extends IModule>> {
	
	private final List<ModuleNode> childFilters; 
	
	private ArrayList<String> parents;
	
	public ModuleNode(ModuleDesc<? extends IModule> object, String id, String[] predecs, ModuleGraph graph) {
		super(object, id, predecs, graph);
		childFilters = new LinkedList<ModuleNode>();
		parents = null;
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
	
	public abstract boolean canBeParent();
	
	@Override
	public ModuleGraph getGraph() {
		return (ModuleGraph) super.getGraph();
	}
	
	public List<String> getParents() {
		ModuleGraph moduleGraph = getGraph();
		if (parents == null) {
			parents = new ArrayList<String>();
			String parentId = getObject().getParent();
			if (parentId != null) {
				ModuleNode pNode = moduleGraph.getNode(parentId);
				parents.addAll(pNode.getParents());
				parents.add(parentId);
			}
			parents.trimToSize();
		}
		
		return parents;
	}
	
}
