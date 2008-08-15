/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.state.IEventLabelSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;

/**
 * Common protocol for labeled elements stored in a label symbol table.
 * <p>
 * Clients that need to contribute symbols to a label symbol table,
 * {@link ILabelSymbolTable}, must implement this interface.
 * </p>
 * 
 * @see ILabelSymbolTable
 * @see IMachineLabelSymbolTable
 * @see IEventLabelSymbolTable
 * 
 * @author Stefan Hallerstede
 * 
 */
public interface ILabelSymbolInfo
		extends
		ISymbolInfo<ILabeledElement, IInternalElementType<? extends ILabeledElement>> {

	/**
	 * Create a statically checked element for this symbol with the specified
	 * parent.
	 * 
	 * @param parent
	 *            the parent of the element to create
	 * @param elementName
	 *            the element name to use for the new element
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @return the created statically checked identifier element
	 * @throws CoreException
	 *             if there was a problem creating the element
	 */
	ILabeledElement createSCElement(IInternalParent parent, String elementName,
			IProgressMonitor monitor) throws CoreException;

}
