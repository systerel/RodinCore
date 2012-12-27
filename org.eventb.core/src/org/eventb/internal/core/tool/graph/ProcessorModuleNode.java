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

import java.util.List;

import org.eventb.internal.core.tool.ModuleDesc;
import org.eventb.internal.core.tool.types.IModule;

/**
 * @author Stefan Hallerstede
 *
 */
public class ProcessorModuleNode extends ModuleNode {
	
	public ProcessorModuleNode(
			ModuleDesc<? extends IModule> object, String id, String[] predecs, ModuleGraph graph) {
		super(object, id, predecs, graph);
	}

	@Override
	public void connect() {
		connectFilters();
		super.connect();
	}

	@Override
	public void storeFilterInParent(ModuleNode node) {
		assert getObject().getParent().equals(node.getId());
		// nothing to do
	}

	protected void connectFilters() {
		String parent = getObject().getParent();
		if (parent == null)
			return;
		ModuleNode pNode = getGraph().getNode(parent);
		List<ModuleNode> filters = pNode.getChildFilters();
		for (ModuleNode node : filters) {
			boolean incr = node.addSucc(this);
			if (incr)
				count++;
		}
	}

	@Override
	public boolean canBeParent() {
		return true;
	}

}
