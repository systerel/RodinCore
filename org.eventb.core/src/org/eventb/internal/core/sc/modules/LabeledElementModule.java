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
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.ILabelSymbolTable;
import org.eventb.core.sc.ProcessorModule;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.SymbolInfoFactory;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class LabeledElementModule extends ProcessorModule {

	/**
	 * Adds a new label symbol to the label symbol table.
	 * Returns the new symbol info created if the label is not already in use,
	 * and <code>null</code> otherwise.
	 * 
	 * @param labeledElement the labeled element
	 * @param labelSymbolTable the label symbol table
	 * @return the new label symbol
	 * @throws CoreException if there was a problem with the database or the symbol table
	 */
	protected ILabelSymbolInfo fetchLabel(
			ILabeledElement labeledElement, 
			ILabelSymbolTable labelSymbolTable, 
			String component,
			IProgressMonitor monitor) throws CoreException {
		
		String label = labeledElement.getLabel(monitor);
		
		ILabelSymbolInfo newSymbolInfo = 
			SymbolInfoFactory.createLabelSymbolInfo(label, labeledElement, component);
		
		try {
			
			labelSymbolTable.putSymbolInfo(newSymbolInfo);
			
		} catch (CoreException e) {
			
			ILabelSymbolInfo symbolInfo = 
				(ILabelSymbolInfo) labelSymbolTable.getSymbolInfo(label);
			
			newSymbolInfo.issueLabelConflictMarker(this);
			
			if(symbolInfo.hasError())
				return null; // do not produce too many error messages
			
			symbolInfo.issueLabelConflictMarker(this);
			
			if (symbolInfo.isMutable())
				symbolInfo.setError();
			
			return null;
	
		}
	
		return newSymbolInfo;
	}

}
