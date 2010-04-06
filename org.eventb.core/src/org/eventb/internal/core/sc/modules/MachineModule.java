/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
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
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IMachineAccuracyInfo;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.MachineAccuracyInfo;
import org.eventb.internal.core.sc.symbolTable.MachineLabelSymbolTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineModule extends BaseModule {

	private final static int LABEL_SYMTAB_SIZE = 2047;

	private ISCMachineRoot machineFile;

	private IMachineAccuracyInfo accuracyInfo;

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		accuracyInfo = new MachineAccuracyInfo();

		final IMachineLabelSymbolTable labelSymbolTable = new MachineLabelSymbolTable(
				LABEL_SYMTAB_SIZE);

		repository.setState(labelSymbolTable);
		repository.setState(accuracyInfo);

		super.initModule(element, repository, monitor);
	}

	public static final IModuleType<MachineModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".machineModule"); //$NON-NLS-1$

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		machineFile = (ISCMachineRoot) target;
		super.process(element, target, repository, monitor);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		machineFile.setAccuracy(accuracyInfo.isAccurate(), monitor);

		super.endModule(element, repository, monitor);
	}

}
