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
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IDerivedPredicateElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class TheoremModule extends SCFilterModule {

	private ILabelSymbolTable labelSymbolTable;

	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		labelSymbolTable = getLabelSymbolTable(repository);
	}

	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
	
		IDerivedPredicateElement derivedElement = (IDerivedPredicateElement) element;
		if (!derivedElement.hasTheorem()) {
			createProblemMarker(derivedElement,
					EventBAttributes.THEOREM_ATTRIBUTE,
					GraphProblem.DerivedPredUndefError);
			return false;
		}
		final boolean isTheorem = derivedElement.isTheorem();
		final String label = ((ILabeledElement) element).getLabel();
	
		checkAndSetSymbolInfo(label, isTheorem);
	
		return true;
	}

	private void checkAndSetSymbolInfo(String label, boolean isTheorem) {
		final ILabelSymbolInfo symbolInfo = labelSymbolTable
				.getSymbolInfo(label);
		if (symbolInfo == null) {
			throw new IllegalStateException("No defined symbol for: " + label);
		}
		symbolInfo.setAttributeValue(EventBAttributes.THEOREM_ATTRIBUTE,
				isTheorem);
	}

	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		labelSymbolTable = null;
		super.endModule(repository, monitor);
	}

	protected abstract ILabelSymbolTable getLabelSymbolTable(
			ISCStateRepository repository) throws CoreException;

}
