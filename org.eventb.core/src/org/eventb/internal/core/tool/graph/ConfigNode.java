/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool.graph;

import org.eventb.internal.core.tool.BaseConfig;

/**
 * @author Stefan Hallerstede
 *
 */
public class ConfigNode extends Node<BaseConfig> {

	@Override
	public ConfigGraph getGraph() {
		return (ConfigGraph) super.getGraph();
	}

	public ConfigNode(BaseConfig object, String id, String[] predecs, ConfigGraph graph) {
		super(object, id, predecs, graph);
	}

}
