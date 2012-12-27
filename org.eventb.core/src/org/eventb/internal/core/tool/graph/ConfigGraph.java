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

import org.eventb.internal.core.tool.BaseConfig;

/**
 * @author Stefan Hallerstede
 *
 */
public class ConfigGraph extends Graph<BaseConfig> {

	public ConfigGraph(String creator) {
		super(creator);
	}

	@Override
	protected Node<BaseConfig> createNode(BaseConfig object) {
		return new ConfigNode(object, object.getId(), object.getIncluded(), this);
	}

	@Override
	public String getName() {
		return super.getName() + " Configuration graph";
	}

}
