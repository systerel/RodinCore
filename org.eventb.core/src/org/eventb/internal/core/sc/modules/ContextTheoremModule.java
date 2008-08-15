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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ITheorem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.sc.state.IContextAccuracyInfo;
import org.eventb.core.sc.state.IContextLabelSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.symbolTable.SymbolFactory;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public class ContextTheoremModule extends TheoremModule {

	public static final IModuleType<ContextTheoremModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".contextTheoremModule"); //$NON-NLS-1$

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	public void process(IRodinElement element, IInternalParent target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		monitor.subTask(Messages.bind(Messages.progress_ContextTheorems));

		if (formulaElements.length == 0)
			return;

		checkAndSaveTheorems(target, 0, repository, monitor);

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
				.getState(IContextLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ISCTheorem getSCTheorem(IInternalParent target, String elementName) {
		return ((ISCContextFile) target).getSCTheorem(elementName);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return SymbolFactory.getInstance().makeTheorem(symbol, true, element,
				component);
	}

	@Override
	protected ITheorem[] getFormulaElements(IRodinElement element)
			throws CoreException {
		IContextFile contextFile = (IContextFile) element;
		return contextFile.getTheorems();
	}

	@Override
	protected IAccuracyInfo getAccuracyInfo(ISCStateRepository repository)
			throws CoreException {
		return (IContextAccuracyInfo) repository
				.getState(IContextAccuracyInfo.STATE_TYPE);
	}

}
