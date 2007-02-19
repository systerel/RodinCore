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
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAbstractMachineInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.symbolTable.InvariantSymbolInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineInvariantModule extends PredicateWithTypingModule<IInvariant> {

	public static final IModuleType<MachineInvariantModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineInvariantModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private static String INVARIANT_NAME_PREFIX = "INV";
	
	public void process(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {

		IAbstractMachineInfo abstractMachineInfo =
			(IAbstractMachineInfo) repository.getState(IAbstractMachineInfo.STATE_TYPE);
		
		ISCMachineFile scMachineFile = abstractMachineInfo.getAbstractMachine();
		
		monitor.subTask(Messages.bind(Messages.progress_MachineInvariants));
		
		int offset = 0;
		
		if (scMachineFile != null) {
			ISCInvariant[] scInvariants = scMachineFile.getSCInvariants();
			offset = scInvariants.length;
			copySCPredicates(scInvariants, target, monitor);
		}
		
		if (formulaElements.length == 0)
			return;
		
		checkAndType(
				target, 
				element.getElementName(),
				repository,
				monitor);
		
		saveInvariants((ISCMachineFile) target, offset, monitor);

	}
	
	private void saveInvariants(
			ISCMachineFile target, 
			int offset,
			IProgressMonitor monitor) throws RodinDBException {
		
		int index = offset;
		
		for (int i=0; i<formulaElements.length; i++) {
			if (formulas[i] == null)
				continue;
			ISCInvariant scInvariant = target.getSCInvariant(INVARIANT_NAME_PREFIX + index++);
			scInvariant.create(null, monitor);
			scInvariant.setLabel(formulaElements[i].getLabel(), monitor);
			scInvariant.setPredicate(formulas[i], null);
			scInvariant.setSource(formulaElements[i], monitor);
		}
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
			ISCStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IMachineLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(
			String symbol, ILabeledElement element, String component) throws CoreException {
		return new InvariantSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
	}

	@Override
	protected IInvariant[] getFormulaElements(IRodinElement element) throws CoreException {
		IMachineFile machineFile = (IMachineFile) element;
		return machineFile.getInvariants();
	}

}
