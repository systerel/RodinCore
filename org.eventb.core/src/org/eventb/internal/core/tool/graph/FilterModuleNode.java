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
	
	public void connect(ModuleGraph graph) {
		connectParent(graph);
		
		super.connect(graph);
	}

	@Override
	public void storeFilterInParent(ModuleNode node) {
		assert getObject().getParent() == node.getId();
		node.addChildFilter(this);
	}
	
}
