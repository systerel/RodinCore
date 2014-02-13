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
package org.eventb.internal.core.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.core.tool.IModule;

/**
 * @author Stefan Hallerstede
 *
 */
public class ModuleConfig extends ConfigWithClosure<ModuleDesc<? extends IModule>> {
	
	private List<ModuleDesc<? extends IModule>> modules;

	public ModuleConfig(
			String modulesId,
			IConfigurationElement configElement, 
			ModuleManager moduleManager) throws ModuleLoadingException {
		super(configElement);
		
		IConfigurationElement[] elements = getChildren(configElement, modulesId);
		loadModules(elements, moduleManager);
	}
	
	private void loadModules(
			IConfigurationElement[] elements, 
			ModuleManager moduleManager) throws ModuleLoadingException {
		modules = new ArrayList<ModuleDesc<? extends IModule>>(elements.length);
		for (IConfigurationElement element : elements) {
			String moduleId = getAttribute(element, "id");
			ModuleDesc<? extends IModule> desc = moduleManager.getModuleDesc(moduleId);
			if (desc == null) {
				throw new ModuleDesc.ModuleLoadingException(
						new IllegalStateException("Unknown module id"
								+ moduleId
								+ " in configuration "
								+ getId()));
			}
			modules.add(desc);
		}
	}
	
	public List<ModuleDesc<? extends IModule>> getModuleDescs() {
		return modules;
	}

	@Override
	public List<ModuleDesc<? extends IModule>> computeClosure(
			Map<String, ? extends ConfigWithClosure<ModuleDesc<? extends IModule>>> configs) {
		List<ModuleDesc<? extends IModule>> closure =  super.computeClosure(configs);
		for (ModuleDesc<? extends IModule> desc : getModuleDescs()) {
			if (closure.contains(desc))
				continue;
			closure.add(desc);
		}
		return closure;
	}

}
