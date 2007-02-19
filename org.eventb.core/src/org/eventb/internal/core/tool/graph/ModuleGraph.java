/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool.graph;

import org.eventb.core.tool.IModule;
import org.eventb.internal.core.tool.ModuleDesc;

/**
 * @author Stefan Hallerstede
 *
 */
public class ModuleGraph extends Graph<ModuleDesc<? extends IModule>> {

	public ModuleGraph(String creator) {
		super(creator);
	}

	public void analyse(ParentGraph parentGraph) {
		complete(parentGraph);
		analyse();
	}

	@Override
	public ModuleNode getNode(String id) {
		assert id != null;
		return (ModuleNode) super.getNode(id);
	}
	
	private void complete(ParentGraph parentGraph) {
		for (String id : parentGraph) {
			ModuleNode node = getNode(id);
			String parentId = node.getObject().getParent();
			if (parentId == null)
				continue;
			ModuleNode pNode = getNode(parentId);
			if (pNode == null)
				throw new IllegalStateException("Unknown parent " + parentId + " for module type " + id);
			if (!pNode.canBeParent())
				throw new IllegalStateException("Module type " + id + " cannot be parent");
			for (String reqId : node.getPredecs()) {
				pNode.addPredec(reqId);
			}
			pNode.addPredec(id);
			
			node.storeFilterInParent(pNode);
		}
	}
	
	@Override
	protected Node<ModuleDesc<? extends IModule>> createNode(ModuleDesc<? extends IModule> object) {
		return object.createNode();
	}

	@Override
	public String getName() {
		return super.getName() + " Module graph";
	}

}
