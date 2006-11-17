/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ITheorem;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.symbolTable.TheoremSymbolInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineTheoremModule extends TheoremModule {
	
	public static final String MACHINE_THEOREM_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".machineTheoremAcceptor";

	private IAcceptorModule[] rules;

	public MachineTheoremModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		rules = manager.getAcceptorModules(MACHINE_THEOREM_ACCEPTOR);
	}

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		
		IMachineFile machineFile = (IMachineFile) element;
		
		ITheorem[] theorems = machineFile.getTheorems(null);
		
		IAbstractEventTable abstractEventTable =
			(IAbstractEventTable) repository.getState(IAbstractEventTable.STATE_TYPE);
		
		ISCMachineFile scMachineFile = abstractEventTable.getMachineFile();
		
		monitor.subTask(Messages.bind(Messages.progress_MachineTheorems));
		
		int offset = 0;
		
		if (scMachineFile != null) {
			ISCTheorem[] scTheorems = scMachineFile.getSCTheorems(null);
			offset = scTheorems.length;
			copySCPredicates(scTheorems, target, monitor);
		}
		
		if (theorems.length == 0)
			return;
				
		checkAndSaveTheorems(
				target, 
				offset,
				theorems,
				rules,
				repository,
				monitor);
		
	}

	@Override
	protected void makeProgress(IProgressMonitor monitor) {
		monitor.worked(1);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledElementModule#getLabelSymbolTableFromRepository(org.eventb.core.sc.IStateRepository)
	 */
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			IStateRepository<IStateSC> repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IMachineLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ISCTheorem getSCTheorem(IInternalParent target, String elementName) {
		return ((ISCMachineFile) target).getSCTheorem(elementName);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(
			String symbol, ILabeledElement element, String component) throws CoreException {
		return new TheoremSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
	}

}
