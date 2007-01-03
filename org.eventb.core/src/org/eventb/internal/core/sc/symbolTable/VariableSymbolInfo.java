/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.sc.util.GraphProblem;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class VariableSymbolInfo 
	extends IdentifierSymbolInfo 
	implements IVariableSymbolInfo {

	public VariableSymbolInfo(
			String symbol,
			boolean imported,
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, imported, element, attribute, component);
		
		forbidden = false;
		
		preserved = false;
		
		fresh = false;
	}

	private boolean forbidden;
	
	private boolean preserved;
	
	private boolean fresh;
	
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

	@Override
	public IRodinProblem getUntypedError() {
		return GraphProblem.UntypedVariableError;
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
