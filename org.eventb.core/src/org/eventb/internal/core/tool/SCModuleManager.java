/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - allowed multiple configurations in input file
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eventb.internal.core.tool.BasicDesc.ModuleLoadingException;
import org.eventb.internal.core.tool.types.IFilterModule;
import org.eventb.internal.core.tool.types.IModule;
import org.eventb.internal.core.tool.types.IProcessorModule;
import org.eventb.internal.core.tool.types.ISCFilterModule;
import org.eventb.internal.core.tool.types.ISCProcessorModule;

/**
 * @author Stefan Hallerstede
 *
 */
public class SCModuleManager extends ModuleManager {

	private static final String CONFIG_DELIM = ";";
	private static final String SC_MODULES_ID = "scModuleTypes";
	private static final SCModuleManager MANAGER = new SCModuleManager();
	
	private static final SCConfigManager SC_CONFIG_MANAGER = SCConfigManager.getInstance();
	
	public static SCModuleManager getInstance() {
		return MANAGER;
	}
	
	private SCModuleManager() {
		super(SC_MODULES_ID);
	}

	@Override
	protected List<ModuleDesc<? extends IModule>> getModuleListForConfig(
			String configIds) {
		final StringTokenizer tokenizer = new StringTokenizer(configIds,
				CONFIG_DELIM);
		final List<ModuleDesc<? extends IModule>> result = new ArrayList<ModuleDesc<? extends IModule>>();
		while (tokenizer.hasMoreTokens()) {
			final String configId = tokenizer.nextToken();
			result.addAll(SC_CONFIG_MANAGER.getConfigClosure(configId));
		}
		return result;
	}
	
	@Override
	protected void verifyFilter(FilterModuleDesc<? extends IFilterModule> moduleDesc) throws ModuleLoadingException {
		try {
			moduleDesc.getClassObject().asSubclass(ISCFilterModule.class);
		} catch (Throwable e) {
			throw new ModuleLoadingException(e);
		}
		
	}

	@Override
	protected void verifyProcessor(ProcessorModuleDesc<? extends IProcessorModule> moduleDesc) throws ModuleLoadingException {
		try {
			moduleDesc.getClassObject().asSubclass(ISCProcessorModule.class);
		} catch (Throwable e) {
			throw new ModuleLoadingException(e);
		}
	}

	@Override
	protected String getName() {
		return "SC";
	}

}
