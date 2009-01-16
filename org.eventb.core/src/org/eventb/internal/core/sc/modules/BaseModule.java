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
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IContextTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.sc.ContextTable;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class BaseModule extends SCProcessorModule {

	private final static int IDENT_SYMTAB_SIZE = 2047;

	private final static int CONTEXT_TABLE_SIZE = 137;

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		endProcessorModules(element, repository, monitor);
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		final IdentifierSymbolTable identifierSymbolTable = new IdentifierSymbolTable(
				IDENT_SYMTAB_SIZE);

		final IContextTable contextTable = new ContextTable(CONTEXT_TABLE_SIZE);

		repository.setState(identifierSymbolTable);
		repository.setState(contextTable);

		initProcessorModules(element, repository, monitor);
	}

	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		processModules(element, target, repository, monitor);
	}

}
