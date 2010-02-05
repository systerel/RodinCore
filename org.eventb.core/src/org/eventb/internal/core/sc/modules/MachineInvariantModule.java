/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAbstractMachineInfo;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineAccuracyInfo;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.SymbolFactory;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineInvariantModule extends
		PredicateWithTypingModule<IInvariant> {

	public static final IModuleType<MachineInvariantModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".machineInvariantModule"); //$NON-NLS-1$

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private static String INVARIANT_NAME_PREFIX = "INV";

	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		IAbstractMachineInfo abstractMachineInfo = (IAbstractMachineInfo) repository
				.getState(IAbstractMachineInfo.STATE_TYPE);

		ISCMachineRoot scMachineRoot = abstractMachineInfo.getAbstractMachine();

		monitor.subTask(Messages.bind(Messages.progress_MachineInvariants));

		int offset = 0;

		if (scMachineRoot != null) {
			ISCInvariant[] scInvariants = scMachineRoot.getSCInvariants();
			offset = scInvariants.length;
			copySCPredicates(scInvariants, target, monitor);
		}

		if (formulaElements.length == 0)
			return;

		checkAndType(element.getElementName(), repository, monitor);

		createSCPredicates(target, INVARIANT_NAME_PREFIX, offset, monitor);

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
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return SymbolFactory.getInstance().makeLocalInvariant(symbol, true, element,
				component);
	}

	@Override
	protected IInvariant[] getFormulaElements(IRodinElement element)
			throws CoreException {
		IRodinFile machineFile = (IRodinFile) element;
		IMachineRoot machineRoot = (IMachineRoot) machineFile.getRoot();
		return machineRoot.getInvariants();
	}

	@Override
	protected IAccuracyInfo getAccuracyInfo(ISCStateRepository repository)
			throws CoreException {
		return (IMachineAccuracyInfo) repository
				.getState(IMachineAccuracyInfo.STATE_TYPE);
	}

}
