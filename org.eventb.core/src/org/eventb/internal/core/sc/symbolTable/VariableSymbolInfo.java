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
import org.eventb.core.ISCVariable;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class VariableSymbolInfo 
	extends IdentifierSymbolInfo 
	implements IVariableSymbolInfo {

	/**
	 * Do not use this constructor.
	 * Use the <code>SymbolInfoFactory</code> instead!
	 * 
	 * {@link SymbolInfoFactory}
	 */
	public VariableSymbolInfo(
			String symbol, 
			String link, 
			IRodinElement element, 
			String component) {
		super(symbol, link, element, component);
		
		forbidden = false;
		
		preserved = false;
		
		fresh = false;
		
		local = false;
	}

	private boolean forbidden;
	
	private boolean preserved;
	
	private boolean fresh;
	
	private boolean local;
	
	public boolean isForbidden() {
		return forbidden;
	}
	
	public void setForbidden() throws CoreException {
		if (!isMutable())
			throw Util.newCoreException(Messages.symtab_ImmutableSymbolViolation);
		this.forbidden = true;
	}

	public boolean isConcrete() {
		return preserved;
	}

	public void setPreserved() {
		this.preserved = true;
	}

	public void createSCElement(
			IInternalParent parent, 
			IProgressMonitor monitor) throws RodinDBException {
		
		if (parent == null)
			return;
		
		ISCVariable variable = 
			(ISCVariable) parent.createInternalElement(
					ISCVariable.ELEMENT_TYPE, getSymbol(), null, monitor);
		variable.setType(getType());
		if (!isLocal()) {
			variable.setForbidden(isForbidden() || !isConcrete(), monitor);
			variable.setPreserved(isConcrete() && !isFresh(), monitor);
		}
		variable.setSource(getSourceElement(), monitor);
	}

	@Override
	public IRodinProblem getUntypedError() {
		return GraphProblem.UntypedVariableError;
	}

	public void setLocal() throws CoreException {
		if (!isMutable())
			throw Util.newCoreException(Messages.symtab_ImmutableSymbolViolation);
		local = true;
	}

	public boolean isLocal() {
		return local;
	}

	public void setFresh() throws CoreException {
		if (!isMutable())
			throw Util.newCoreException(Messages.symtab_ImmutableSymbolViolation);
		fresh = true;
	}

	public boolean isFresh() {
		return fresh;
	}

	@Override
	public IRodinProblem getConflictWarning() {
		if (isImported())
			return GraphProblem.VariableNameImportConflictWarning;
		else
			return GraphProblem.VariableNameConflictWarning;
	}

	@Override
	public IRodinProblem getConflictError() {
		if (isImported())
			return GraphProblem.VariableNameImportConflictError;
		else
			return GraphProblem.VariableNameConflictError;
	}
	
}
