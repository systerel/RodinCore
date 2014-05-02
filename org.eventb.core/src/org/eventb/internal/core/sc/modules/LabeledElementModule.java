/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class LabeledElementModule extends SCProcessorModule {

	ILabelSymbolTable labelSymbolTable;

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		labelSymbolTable = getLabelSymbolTableFromRepository(repository);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		labelSymbolTable = null;
	}

	protected abstract ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException;

	/**
	 * Adds a new label symbol to the label symbol table. Returns the new symbol
	 * info created if the label is not already in use, and <code>null</code>
	 * otherwise.
	 * 
	 * @param internalElement
	 *            the labeled element
	 * @return the new label symbol
	 * @throws CoreException
	 *             if there was a problem with the database or the symbol table
	 */
	protected ILabelSymbolInfo fetchLabel(IInternalElement internalElement,
			String component, IProgressMonitor monitor) throws CoreException {

		ILabeledElement labeledElement = (ILabeledElement) internalElement;

		if (!labeledElement.hasLabel()) {
			createProblemMarker(labeledElement,
					EventBAttributes.LABEL_ATTRIBUTE,
					GraphProblem.LabelUndefError);
			return null;
		}
		
		if (labeledElement.getLabel().trim().equals("")) {
			createProblemMarker(labeledElement,
					EventBAttributes.LABEL_ATTRIBUTE,
					GraphProblem.EmptyLabelError);
			return null;
		}

		String label = labeledElement.getLabel();

		ILabelSymbolInfo newSymbolInfo = createLabelSymbolInfo(label,
				labeledElement, component);

		if (!labelSymbolTable.tryPutSymbolInfo(newSymbolInfo)) {

			ILabelSymbolInfo symbolInfo = labelSymbolTable.getSymbolInfo(label);

			newSymbolInfo.createConflictMarker(this);

			if (symbolInfo.hasError())
				return null; // do not produce too many error messages

			symbolInfo.createConflictMarker(this);

			if (symbolInfo.isMutable())
				symbolInfo.setError();

			return null;

		}

		newSymbolInfo.setAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE,
				labeledElement);
		return newSymbolInfo;
	}

	protected abstract ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException;

}
