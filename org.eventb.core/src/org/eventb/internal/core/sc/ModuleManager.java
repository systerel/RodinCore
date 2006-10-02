/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.Hashtable;

import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IProcessorModule;
import org.eventb.internal.core.sc.modules.ContextAxiomModule;
import org.eventb.internal.core.sc.modules.ContextCarrierSetModule;
import org.eventb.internal.core.sc.modules.ContextConstantModule;
import org.eventb.internal.core.sc.modules.ContextExtendsModule;
import org.eventb.internal.core.sc.modules.ContextPredicateFreeIdentsModule;
import org.eventb.internal.core.sc.modules.ContextSaveIdentifiersModule;
import org.eventb.internal.core.sc.modules.ContextTheoremModule;
import org.eventb.internal.core.sc.modules.MachineContextClosureModule;
import org.eventb.internal.core.sc.modules.MachineEventActionFreeIdentsModule;
import org.eventb.internal.core.sc.modules.MachineEventActionModule;
import org.eventb.internal.core.sc.modules.MachineEventGuardFreeIdentsModule;
import org.eventb.internal.core.sc.modules.MachineEventGuardModule;
import org.eventb.internal.core.sc.modules.MachineEventModule;
import org.eventb.internal.core.sc.modules.MachineEventRefinesModule;
import org.eventb.internal.core.sc.modules.MachineEventSaveIdentifiersModule;
import org.eventb.internal.core.sc.modules.MachineEventVariableModule;
import org.eventb.internal.core.sc.modules.MachineEventWitnessFreeIdentsModule;
import org.eventb.internal.core.sc.modules.MachineEventWitnessModule;
import org.eventb.internal.core.sc.modules.MachineInvariantModule;
import org.eventb.internal.core.sc.modules.MachinePredicateFreeIdentsModule;
import org.eventb.internal.core.sc.modules.MachinePreviousEventLabelModule;
import org.eventb.internal.core.sc.modules.MachineRefinesModule;
import org.eventb.internal.core.sc.modules.MachineSaveIdentifiersModule;
import org.eventb.internal.core.sc.modules.MachineSeesContextModule;
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
	
	private IAcceptorModule[] emptyAcc = new IAcceptorModule[0];
	private IProcessorModule[] emptyProc = new IProcessorModule[0];
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.IModuleManager#getModules(java.lang.String)
	 */
	public IAcceptorModule[] getAcceptorModules(String moduleType) {
		IAcceptorCreator creator = 
			(IAcceptorCreator) moduleTable.get(moduleType);
		if (creator == null)
			return emptyAcc;
		IAcceptorModule[] rules = creator.create();
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
		
		moduleTable.put(ContextAxiomModule.CONTEXT_AXIOM_ACCEPTOR,
				new IAcceptorCreator() {
					public IAcceptorModule[] create() {
						return new IAcceptorModule[] {
								new ContextPredicateFreeIdentsModule()
						};
					}
				}
		);
		
		moduleTable.put(ContextTheoremModule.CONTEXT_THEOREM_ACCEPTOR,
				new IAcceptorCreator() {
					public IAcceptorModule[] create() {
						return new IAcceptorModule[] {
								new ContextPredicateFreeIdentsModule()
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
				MachineVariableModule.MACHINE_VARIABLE_ACCEPTOR, 
				new IAcceptorCreator() {
					public IAcceptorModule[] create() {
						return new IAcceptorModule[] {
								new MachineVariableFromLocalModule()
						};
					}
					
				}
		);
		
		moduleTable.put(
				MachineInvariantModule.MACHINE_INVARIANT_ACCEPTOR, 
				new IAcceptorCreator() {
					public IAcceptorModule[] create() {
						return new IAcceptorModule[] {
								new MachinePredicateFreeIdentsModule(),
								new MachinePreviousEventLabelModule()
						};
					}
					
				}
		);
		
		moduleTable.put(
				MachineTheoremModule.MACHINE_THEOREM_ACCEPTOR, 
				new IAcceptorCreator() {
					public IAcceptorModule[] create() {
						return new IAcceptorModule[] {
								new MachinePredicateFreeIdentsModule(),
								new MachinePreviousEventLabelModule()
						};
					}
			
				}
		);
		
		moduleTable.put(
				MachineVariantModule.MACHINE_VARIANT_ACCEPTOR, 
				new IAcceptorCreator() {
					public IAcceptorModule[] create() {
						return new IAcceptorModule[] {
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
				MachineEventGuardModule.MACHINE_EVENT_GUARD_ACCEPTOR, 
				new IAcceptorCreator() {
					public IAcceptorModule[] create() {
						return new IAcceptorModule[] {
								new MachineEventGuardFreeIdentsModule()
						};
					}
			
				}
		);	
		
		moduleTable.put(
				MachineEventWitnessModule.MACHINE_EVENT_WITNESS_ACCEPTOR, 
				new IAcceptorCreator() {
					public IAcceptorModule[] create() {
						return new IAcceptorModule[] {
								new MachineEventWitnessFreeIdentsModule()
						};
					}
			
				}
		);	
		
		moduleTable.put(
				MachineEventActionModule.MACHINE_EVENT_ACTION_ACCEPTOR, 
				new IAcceptorCreator() {
					public IAcceptorModule[] create() {
						return new IAcceptorModule[] {
								new MachineEventActionFreeIdentsModule()
						};
					}
			
				}
		);	
		
		moduleTable.put(
				MachineEventModule.MACHINE_EVENT_ACCEPTOR, 
				new IAcceptorCreator() {
					public IAcceptorModule[] create() {
						return new IAcceptorModule[] {
								new MachinePreviousEventLabelModule()
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
