/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.Hashtable;

import org.eventb.core.sc.IFilterModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IProcessorModule;
import org.eventb.internal.core.sc.modules.ContextAxiomFreeIdentsModule;
import org.eventb.internal.core.sc.modules.ContextAxiomModule;
import org.eventb.internal.core.sc.modules.ContextCarrierSetModule;
import org.eventb.internal.core.sc.modules.ContextConstantModule;
import org.eventb.internal.core.sc.modules.ContextExtendsModule;
import org.eventb.internal.core.sc.modules.ContextSaveIdentifiersModule;
import org.eventb.internal.core.sc.modules.ContextTheoremFreeIdentsModule;
import org.eventb.internal.core.sc.modules.ContextTheoremModule;
import org.eventb.internal.core.sc.modules.MachineContextClosureModule;
import org.eventb.internal.core.sc.modules.MachineEventActionFreeIdentsModule;
import org.eventb.internal.core.sc.modules.MachineEventActionModule;
import org.eventb.internal.core.sc.modules.MachineEventConvergenceModule;
import org.eventb.internal.core.sc.modules.MachineEventGuardFreeIdentsModule;
import org.eventb.internal.core.sc.modules.MachineEventGuardModule;
import org.eventb.internal.core.sc.modules.MachineEventInheritedModule;
import org.eventb.internal.core.sc.modules.MachineEventModule;
import org.eventb.internal.core.sc.modules.MachineEventRefinesModule;
import org.eventb.internal.core.sc.modules.MachineEventSaveIdentifiersModule;
import org.eventb.internal.core.sc.modules.MachineEventVariableModule;
import org.eventb.internal.core.sc.modules.MachineEventWitnessFreeIdentsModule;
import org.eventb.internal.core.sc.modules.MachineEventWitnessModule;
import org.eventb.internal.core.sc.modules.MachineInvariantFreeIdentsModule;
import org.eventb.internal.core.sc.modules.MachineInvariantModule;
import org.eventb.internal.core.sc.modules.MachinePreviousEventLabelModule;
import org.eventb.internal.core.sc.modules.MachineRefinesModule;
import org.eventb.internal.core.sc.modules.MachineSaveIdentifiersModule;
import org.eventb.internal.core.sc.modules.MachineSeesContextModule;
import org.eventb.internal.core.sc.modules.MachineTheoremFreeIdentsModule;
import org.eventb.internal.core.sc.modules.MachineTheoremModule;
import org.eventb.internal.core.sc.modules.MachineVariableFromLocalModule;
import org.eventb.internal.core.sc.modules.MachineVariableModule;
import org.eventb.internal.core.sc.modules.MachineVariantFreeIdentsModule;
import org.eventb.internal.core.sc.modules.MachineVariantModule;

/**
 * @author Stefan Hallerstede
 *
 */
public final class ModuleManager implements IModuleManager {
	
	private static IModuleManager MANAGER = new ModuleManager();
	
	private IFilterModule[] emptyFlt = new IFilterModule[0];
	private IProcessorModule[] emptyProc = new IProcessorModule[0];
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.IModuleManager#getModules(java.lang.String)
	 */
	public IFilterModule[] getFilterModules(String moduleType) {
		IFilterCreator creator = 
			(IFilterCreator) moduleTable.get(moduleType);
		if (creator == null)
			return emptyFlt;
		IFilterModule[] rules = creator.create();
		return rules;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.IModuleManager#getModules(java.lang.String)
	 */
	public IProcessorModule[] getProcessorModules(String moduleType) {
		IProcessorCreator creator = 
			(IProcessorCreator) moduleTable.get(moduleType);
		if (creator == null)
			return emptyProc;
		IProcessorModule[] rules = creator.create();
		return rules;
	}
	
	private Hashtable<String, IModuleCreator> moduleTable;
	
	private ModuleManager() {
		moduleTable = new Hashtable<String, IModuleCreator>(43);
		
		// contexts:
		
		moduleTable.put(ContextAxiomModule.CONTEXT_AXIOM_FILTER,
				new IFilterCreator() {
					public IFilterModule[] create() {
						return new IFilterModule[] {
								new ContextAxiomFreeIdentsModule()
						};
					}
				}
		);
		
		moduleTable.put(ContextTheoremModule.CONTEXT_THEOREM_FILTER,
				new IFilterCreator() {
					public IFilterModule[] create() {
						return new IFilterModule[] {
								new ContextTheoremFreeIdentsModule()
						};
					}
				}
		);
		
		moduleTable.put(ContextStaticChecker.CONTEXT_PROCESSOR,
				new IProcessorCreator() {
					public IProcessorModule[] create() {
						return new IProcessorModule[] {
								new ContextExtendsModule(),
								new ContextCarrierSetModule(),
								new ContextConstantModule(),
								new ContextAxiomModule(),
								new ContextSaveIdentifiersModule(),
								new ContextTheoremModule()
						};
					}
	
				}
		);
		
		// machines:
		
		moduleTable.put(
				MachineVariableModule.MACHINE_VARIABLE_FILTER, 
				new IFilterCreator() {
					public IFilterModule[] create() {
						return new IFilterModule[] {
								new MachineVariableFromLocalModule()
						};
					}
					
				}
		);
		
		moduleTable.put(
				MachineInvariantModule.MACHINE_INVARIANT_FILTER, 
				new IFilterCreator() {
					public IFilterModule[] create() {
						return new IFilterModule[] {
								new MachineInvariantFreeIdentsModule(),
								new MachinePreviousEventLabelModule()
						};
					}
					
				}
		);
		
		moduleTable.put(
				MachineTheoremModule.MACHINE_THEOREM_FILTER, 
				new IFilterCreator() {
					public IFilterModule[] create() {
						return new IFilterModule[] {
								new MachineTheoremFreeIdentsModule(),
								new MachinePreviousEventLabelModule()
						};
					}
			
				}
		);
		
		moduleTable.put(
				MachineVariantModule.MACHINE_VARIANT_FILTER, 
				new IFilterCreator() {
					public IFilterModule[] create() {
						return new IFilterModule[] {
								new MachineVariantFreeIdentsModule()
						};
					}
			
				}
		);	
		
		moduleTable.put(MachineStaticChecker.MACHINE_PROCESSOR,
				new IProcessorCreator() {
					public IProcessorModule[] create() {
						return new IProcessorModule[] {
								new MachineRefinesModule(),
								new MachineSeesContextModule(),
								new MachineContextClosureModule(),
								new MachineVariableModule(),
								new MachineInvariantModule(),
								new MachineSaveIdentifiersModule(),
								new MachineTheoremModule(),
								new MachineVariantModule(),
								new MachineEventModule()
						};
					}
	
				}
		);
		
		// events:
		
		moduleTable.put(
				MachineEventGuardModule.MACHINE_EVENT_GUARD_FILTER, 
				new IFilterCreator() {
					public IFilterModule[] create() {
						return new IFilterModule[] {
								new MachineEventGuardFreeIdentsModule()
						};
					}
			
				}
		);	
		
		moduleTable.put(
				MachineEventWitnessModule.MACHINE_EVENT_WITNESS_FILTER, 
				new IFilterCreator() {
					public IFilterModule[] create() {
						return new IFilterModule[] {
								new MachineEventWitnessFreeIdentsModule()
						};
					}
			
				}
		);	
		
		moduleTable.put(
				MachineEventActionModule.MACHINE_EVENT_ACTION_FILTER, 
				new IFilterCreator() {
					public IFilterModule[] create() {
						return new IFilterModule[] {
								new MachineEventActionFreeIdentsModule()
						};
					}
			
				}
		);	
		
		moduleTable.put(
				MachineEventModule.MACHINE_EVENT_FILTER, 
				new IFilterCreator() {
					public IFilterModule[] create() {
						return new IFilterModule[] {
								new MachineEventInheritedModule()
						};
					}
			
				}
		);
		
		moduleTable.put(
				MachineEventModule.MACHINE_EVENT_PROCESSOR, 
				new IProcessorCreator() {
					public IProcessorModule[] create() {
						return new IProcessorModule[] {
								new MachineEventRefinesModule(),
								new MachineEventConvergenceModule(),
								new MachineEventVariableModule(),
								new MachineEventGuardModule(),
								new MachineEventSaveIdentifiersModule(),
								new MachineEventWitnessModule(),
								new MachineEventActionModule()
						};
					}
			
				}
		);
		
	
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
