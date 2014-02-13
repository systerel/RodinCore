/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.tool.graph;

import org.eventb.core.tool.IModule;
import org.eventb.internal.core.tool.ModuleDesc;

/**
 * @author Stefan Hallerstede
 *
 */
public class ParentNode extends Node<ModuleDesc<? extends IModule>> {
	
	@Override
	public ParentGraph getGraph() {
		return (ParentGraph) super.getGraph();
	}

	public ParentNode(ModuleDesc<? extends IModule> object, String id, String[] predecs, ParentGraph graph) {
		super(object, id, predecs, graph);
	}

}
