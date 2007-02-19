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
public class FilterModuleNode extends ModuleNode {

	public FilterModuleNode(ModuleDesc<? extends IModule> object, String id, String[] predecs) {
		super(object, id, predecs);
	}
	
	@Override
	public void connect(Graph<ModuleDesc<? extends IModule>> graph) {
		ModuleGraph moduleGraph = (ModuleGraph) graph;
//		connectParent(moduleGraph);
		
		super.connect(moduleGraph);
	}

	@Override
	public void storeFilterInParent(ModuleNode node) {
		assert getObject().getParent().equals(node.getId());
		node.addChildFilter(this);
	}

	@Override
	public boolean canBeParent() {
		return false;
	}
	
}
