/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ITheorem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAbstractMachineInfo;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineAccuracyInfo;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.symbolTable.SymbolFactory;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineTheoremModule extends TheoremModule {

	public static final IModuleType<MachineTheoremModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".machineTheoremModule"); //$NON-NLS-1$

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	public void process(IRodinElement element, IInternalParent target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		IAbstractMachineInfo abstractMachineInfo = (IAbstractMachineInfo) repository
				.getState(IAbstractMachineInfo.STATE_TYPE);

		ISCMachineRoot scMachineFile = abstractMachineInfo.getAbstractMachine();

		monitor.subTask(Messages.bind(Messages.progress_MachineTheorems));

		int offset = 0;

		if (scMachineFile != null) {
			ISCTheorem[] scTheorems = scMachineFile.getSCTheorems();
			offset = scTheorems.length;
			copySCPredicates(scTheorems, target, monitor);
		}

		if (formulaElements.length == 0)
			return;

		checkAndSaveTheorems(target, offset, repository, monitor);

	}

	@Override
	protected void makeProgress(IProgressMonitor monitor) {
		monitor.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eventb.internal.core.sc.modules.LabeledElementModule#
	 * getLabelSymbolTableFromRepository(org.eventb.core.sc.IStateRepository)
	 */
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository
				.getState(IMachineLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ISCTheorem getSCTheorem(IInternalParent target, String elementName) {
		return ((ISCMachineRoot) target).getSCTheorem(elementName);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return SymbolFactory.getInstance().makeLocalTheorem(symbol, true, element,
				component);
	}

	@Override
	protected ITheorem[] getFormulaElements(IRodinElement element)
			throws CoreException {
		IRodinFile machineFile = (IRodinFile) element;
		IMachineRoot machineRoot = (IMachineRoot) machineFile.getRoot();
		return machineRoot.getTheorems();
	}

	@Override
	protected IAccuracyInfo getAccuracyInfo(ISCStateRepository repository)
			throws CoreException {
		return (IMachineAccuracyInfo) repository
				.getState(IMachineAccuracyInfo.STATE_TYPE);
	}

}
