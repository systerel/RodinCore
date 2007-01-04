/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import org.eventb.core.tool.IToolFilterModule;
import org.eventb.core.tool.IToolProcessorModule;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ToolModuleManager <FM extends IToolFilterModule, PM extends IToolProcessorModule> {

	/**
	 * Returns the id of the extension point corresponding to this manager.
	 * 
	 * @return the id of the extension point corresponding to this manager
	 */
	protected abstract String getExtensionId();
	
	/**
	 * Returns the id of the root module of the module tree.
	 * 
	 * @return the id of the root module of the module tree
	 */
	protected abstract String getRootModuleId();

	private final FM[] NO_FILTER = (FM[]) new Object[0];
	
	private final PM[] NO_PROCESSOR = (PM[]) new Object[0];

	
}
