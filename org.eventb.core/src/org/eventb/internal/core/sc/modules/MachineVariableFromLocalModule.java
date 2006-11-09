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
import org.eventb.core.IIdentifierElement;
import org.eventb.core.sc.AcceptorModule;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.StaticChecker;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariableFromLocalModule extends AcceptorModule {

	private IAbstractEventTable abstractEventTable;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEventTable = (IAbstractEventTable) 
			repository.getState(IAbstractEventTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAcceptorModule#accept(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean accept(
			IRodinElement element, 
			IStateRepository<IStateSC> repository,
			IProgressMonitor monitor) throws CoreException {
		
		IIdentifierElement identifierElement = (IIdentifierElement) element;
		
		String variableName = identifierElement.getIdentifierString();
		
		if (abstractEventTable.isLocalVariable(variableName)) {
			
			String abstractName = 
				StaticChecker.getStrippedComponentName(
						abstractEventTable.getMachineFile().getElementName());
			
			createProblemMarker(
					identifierElement, 
					EventBAttributes.IDENTIFIER_ATTRIBUTE, 
					GraphProblem.VariableIsLocalInAbstractMachineError, 
					variableName,
					abstractName);
			
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEventTable = null;
	}

}
