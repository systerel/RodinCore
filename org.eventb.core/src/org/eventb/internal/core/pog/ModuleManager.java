/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.Hashtable;

import org.eventb.core.pog.IModule;
import org.eventb.core.pog.IModuleManager;
import org.eventb.internal.core.pog.modules.MachineEventActionBasicModule;
import org.eventb.internal.core.pog.modules.MachineEventActionBodySimModule;
import org.eventb.internal.core.pog.modules.MachineEventActionFrameSimModule;
import org.eventb.internal.core.pog.modules.MachineEventEstablishInvariantModule;
import org.eventb.internal.core.pog.modules.MachineEventGuardModule;
import org.eventb.internal.core.pog.modules.MachineEventHypothesisModule;
import org.eventb.internal.core.pog.modules.MachineEventModule;
import org.eventb.internal.core.pog.modules.MachineEventPreserveInvariantModule;
import org.eventb.internal.core.pog.modules.MachineEventStrengthenGuardModule;
import org.eventb.internal.core.pog.modules.MachineHypothesisModule;


/**
 * @author Stefan Hallerstede
 *
 */
public class ModuleManager implements IModuleManager {

	private static IModuleManager MANAGER = new ModuleManager();
	
	private IModule[] emptyProc = new IModule[0];
	
	private Hashtable<String, IModuleCreator> moduleTable;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IModuleManager#getProcessorModules(java.lang.String)
	 */
	public IModule[] getProcessorModules(String moduleType) {
		IModuleCreator creator = moduleTable.get(moduleType);
		if (creator == null)
			return emptyProc;
		IModule[] rules = creator.create();
		return rules;
	}

	private ModuleManager() {
		moduleTable = new Hashtable<String, IModuleCreator>(43);
		moduleTable.put(MachinePOGenerator.MACHINE_MODULE,
				new IModuleCreator() {

					public IModule[] create() {
						return new IModule[] {
								new MachineHypothesisModule(),
								new MachineEventModule()
						};
					}

		});
		moduleTable.put(MachineEventModule.MACHINE_EVENT_MODULE,
				new IModuleCreator() {

					public IModule[] create() {
						return new IModule[] {
								new MachineEventHypothesisModule(),
								new MachineEventGuardModule(),
								new MachineEventActionBasicModule(),
								new MachineEventEstablishInvariantModule(),
								new MachineEventPreserveInvariantModule(),
								new MachineEventActionBodySimModule(),
								new MachineEventActionFrameSimModule(),
								new MachineEventStrengthenGuardModule()
						};
					}

		});
	}
	
	/**
	 * Returns the module manager instance
	 * 
	 * @return the module manager instance
	 */
	public static IModuleManager getModuleManager() {
		return MANAGER;
	}

}
