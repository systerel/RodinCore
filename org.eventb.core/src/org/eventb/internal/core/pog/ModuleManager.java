/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.Hashtable;

import org.eventb.core.pog.IPOGModuleManager;
import org.eventb.core.pog.IPOGProcessorModule;
import org.eventb.internal.core.pog.modules.ContextAxiomModule;
import org.eventb.internal.core.pog.modules.ContextCommitHypothesesModule;
import org.eventb.internal.core.pog.modules.ContextHypothesisModule;
import org.eventb.internal.core.pog.modules.ContextTheoremModule;
import org.eventb.internal.core.pog.modules.MachineCommitHypothesesModule;
import org.eventb.internal.core.pog.modules.MachineEventActionBodySimModule;
import org.eventb.internal.core.pog.modules.MachineEventActionFrameSimModule;
import org.eventb.internal.core.pog.modules.MachineEventActionModule;
import org.eventb.internal.core.pog.modules.MachineEventGuardModule;
import org.eventb.internal.core.pog.modules.MachineEventHypothesisModule;
import org.eventb.internal.core.pog.modules.MachineEventInitialInvariantModule;
import org.eventb.internal.core.pog.modules.MachineEventModule;
import org.eventb.internal.core.pog.modules.MachineEventPreserveInvariantModule;
import org.eventb.internal.core.pog.modules.MachineEventStrengthenGuardModule;
import org.eventb.internal.core.pog.modules.MachineEventVariantModule;
import org.eventb.internal.core.pog.modules.MachineEventWitnessModule;
import org.eventb.internal.core.pog.modules.MachineHypothesisModule;
import org.eventb.internal.core.pog.modules.MachineInvariantModule;
import org.eventb.internal.core.pog.modules.MachineTheoremModule;
import org.eventb.internal.core.pog.modules.MachineVariantModule;


/**
 * @author Stefan Hallerstede
 *
 */
public class ModuleManager implements IPOGModuleManager {

	private static IPOGModuleManager MANAGER = new ModuleManager();
	
	private IPOGProcessorModule[] emptyProc = new IPOGProcessorModule[0];
	
	private Hashtable<String, IModuleCreator> moduleTable;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IModuleManager#getProcessorModules(java.lang.String)
	 */
	public IPOGProcessorModule[] getProcessorModules(String moduleType) {
		IModuleCreator creator = moduleTable.get(moduleType);
		if (creator == null)
			return emptyProc;
		IPOGProcessorModule[] rules = creator.create();
		return rules;
	}

	private ModuleManager() {
		moduleTable = new Hashtable<String, IModuleCreator>(43);
		moduleTable.put(MachinePOGenerator.MACHINE_MODULE,
				new IModuleCreator() {

					public IPOGProcessorModule[] create() {
						return new IPOGProcessorModule[] {
								new MachineHypothesisModule(),
								new MachineTheoremModule(),
								new MachineInvariantModule(),
								new MachineCommitHypothesesModule(),
								new MachineVariantModule(),
								new MachineEventModule()
						};
					}

		});
		moduleTable.put(MachineEventModule.MACHINE_EVENT_MODULE,
				new IModuleCreator() {

					public IPOGProcessorModule[] create() {
						return new IPOGProcessorModule[] {
								new MachineEventHypothesisModule(),
								new MachineEventGuardModule(),
								new MachineEventWitnessModule(),
								new MachineEventActionModule(),
								new MachineEventInitialInvariantModule(),
								new MachineEventPreserveInvariantModule(),
								new MachineEventActionBodySimModule(),
								new MachineEventActionFrameSimModule(),
								new MachineEventStrengthenGuardModule(),
								new MachineEventVariantModule()
						};
					}

		});
		moduleTable.put(ContextPOGenerator.CONTEXT_MODULE,
				new IModuleCreator() {

					public IPOGProcessorModule[] create() {
						return new IPOGProcessorModule[] {
								new ContextHypothesisModule(),
								new ContextTheoremModule(),
								new ContextAxiomModule(),
								new ContextCommitHypothesesModule()
						};
					}

		});
	}
	
	/**
	 * Returns the module manager instance
	 * 
	 * @return the module manager instance
	 */
	public static IPOGModuleManager getModuleManager() {
		return MANAGER;
	}

}
