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
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.symbolTable.ContextLabelSymbolTable;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextModule extends BaseModule {
	
	private final static int LABEL_SYMTAB_SIZE = 2047;
	
	@Override
	public void initModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		final ContextLabelSymbolTable labelSymbolTable = 
			new ContextLabelSymbolTable(LABEL_SYMTAB_SIZE);
		repository.setState(labelSymbolTable);	
		super.initModule(element, repository, monitor);
	}

	public static final IModuleType<ContextModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".contextModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
}
