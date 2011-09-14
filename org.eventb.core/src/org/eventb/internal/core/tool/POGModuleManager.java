/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added getUnknownConfigIds()
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.List;

import org.eventb.internal.core.tool.BasicDesc.ModuleLoadingException;
import org.eventb.internal.core.tool.types.IFilterModule;
import org.eventb.internal.core.tool.types.IModule;
import org.eventb.internal.core.tool.types.IPOGFilterModule;
import org.eventb.internal.core.tool.types.IPOGProcessorModule;
import org.eventb.internal.core.tool.types.IProcessorModule;

/**
 * @author Stefan Hallerstede
 *
 */
public class POGModuleManager extends ModuleManager {

	private static final String POG_MODULES_ID = "pogModuleTypes";
	private static final POGModuleManager MANAGER = new POGModuleManager();
	
	private static final POGConfigManager POG_CONFIG_MANAGER = POGConfigManager.getInstance();
	
	public static POGModuleManager getInstance() {
		return MANAGER;
	}
	
	private POGModuleManager() {
		super(POG_MODULES_ID);
	}

	@Override
	protected List<ModuleDesc<? extends IModule>> getModuleListForConfig(String configId) {
		return POG_CONFIG_MANAGER.getConfigClosure(configId);
	}

	@Override
	public List<String> getUnknownConfigIds(String configIds) {
		return POG_CONFIG_MANAGER.getUnknownConfigs(configIds);
	}
	
	@Override
	protected void verifyFilter(FilterModuleDesc<? extends IFilterModule> moduleDesc) throws ModuleLoadingException {
		try {
			moduleDesc.getClassObject().asSubclass(IPOGFilterModule.class);
		} catch (ClassCastException e) {
			throw new IllegalStateException(
					"Not a POG filter module " + moduleDesc.getId());
		}
		
	}

	@Override
	protected void verifyProcessor(ProcessorModuleDesc<? extends IProcessorModule> moduleDesc) throws ModuleLoadingException {
		try {
			moduleDesc.getClassObject().asSubclass(IPOGProcessorModule.class);
		} catch (ClassCastException e) {
			throw new IllegalStateException(
					"Not a POG processor module " + moduleDesc.getId());
		}
	}

	@Override
	protected String getName() {
		return "POG";
	}

}
