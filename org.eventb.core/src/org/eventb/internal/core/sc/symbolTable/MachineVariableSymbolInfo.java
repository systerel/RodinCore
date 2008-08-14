/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCVariable;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariableSymbolInfo extends IdentifierSymbolInfo implements IVariableSymbolInfo {

	private boolean forbidden;
	private boolean abstractVar;
	private boolean concrete;
	private boolean fresh;

	private MachineVariableSymbolInfo(
			String symbol, 
			boolean imported,
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, imported, element, attribute, component);
		
		forbidden = false;
		
		abstractVar = false;
		
		concrete = false;
		
		fresh = false;
	}
	
	public static MachineVariableSymbolInfo makeAbstractVariableSymbolInfo(
			String symbol, 
			IInternalElement element, 
			org.rodinp.core.IAttributeType.String attribute, 
			String component) {
		 return new MachineVariableSymbolInfo(symbol, true, element, attribute, component);
	}
	
	public static MachineVariableSymbolInfo makeConcreteVariableSymbolInfo(
			String symbol, 
			IInternalElement element, 
			org.rodinp.core.IAttributeType.String attribute, 
			String component) {
		return new MachineVariableSymbolInfo(symbol, false, element, attribute, component);
	}
	
	public ISCIdentifierElement createSCElement(
			IInternalParent parent, 
			IProgressMonitor monitor) throws CoreException {
		
		if (parent == null)
			return null;
		
		ISCVariable variable = ((ISCMachineFile) parent).getSCVariable(getSymbol());
		variable.create(null, monitor);
		variable.setType(getType(), null);
		variable.setConcrete(isConcrete(), monitor);
		variable.setAbstract(isAbstract(), monitor);
		variable.setSource(getElement(), monitor);
		return variable;
	}

	@Override
	protected void createConflictError(IMarkerDisplay markerDisplay) throws RodinDBException {
		if (isImported())
			markerDisplay.createProblemMarker(
					getElement(), 
					getSourceAttributeType(), 
					GraphProblem.VariableNameImportConflictError, 
					getSymbol(), getComponentName());
		else
			markerDisplay.createProblemMarker(
					getElement(), 
					getSourceAttributeType(), 
					GraphProblem.VariableNameConflictError, 
					getSymbol());
	}

	@Override
	protected void createConflictWarning(IMarkerDisplay markerDisplay) throws RodinDBException {
		if (isImported())
			markerDisplay.createProblemMarker(
					getElement(), 
					getSourceAttributeType(), 
					GraphProblem.VariableNameImportConflictWarning, 
					getSymbol(), getComponentName());
		else
			markerDisplay.createProblemMarker(
					getElement(), 
					getSourceAttributeType(), 
					GraphProblem.VariableNameConflictWarning, 
					getSymbol());
	}

	@Deprecated
	public boolean isForbidden() {
		return forbidden;
	}

	@Deprecated
	public void setForbidden() throws CoreException {
		assertMutable();
		this.forbidden = true;
		this.concrete = false;
	}

	public boolean isConcrete() {
		return concrete;
	}

	public void setConcrete() {
		this.concrete = true;
	}

	@Override
	public IRodinProblem getUntypedError() {
		return GraphProblem.UntypedVariableError;
	}

	@Deprecated
	public void setFresh() throws CoreException {
		assertMutable();
		fresh = true;
	}

	@Deprecated
	public boolean isFresh() {
		return fresh;
	}

	public boolean isAbstract() {
		return abstractVar;
	}

	public void setAbstract() {
		abstractVar = true;
	}

}
