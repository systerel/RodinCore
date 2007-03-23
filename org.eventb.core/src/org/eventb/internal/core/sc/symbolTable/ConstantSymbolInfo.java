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
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.symbolTable.IConstantSymbolInfo;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ConstantSymbolInfo 
	extends IdentifierSymbolInfo 
	implements IConstantSymbolInfo {
	
	private static class AbstractConstantSymbolInfo extends ConstantSymbolInfo {

		public AbstractConstantSymbolInfo(
				String symbol, 
				IInternalElement element, 
				IAttributeType.String attribute, 
				String component) {
			super(symbol, true, element, attribute, component);
		}

		public ISCIdentifierElement createSCElement(
				IInternalParent parent, IProgressMonitor monitor) throws CoreException {
			throw Util.newCoreException(Messages.symtab_cannotCreateAbstractConstant);
		}

		@Override
		protected void createConflictError(IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(
					getSourceElement(), 
					getSourceAttributeType(), 
					GraphProblem.ConstantNameImportConflictError, 
					getSymbol(), getComponentName());
		}

		@Override
		protected void createConflictWarning(IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(
					getSourceElement(), 
					getSourceAttributeType(), 
					GraphProblem.ConstantNameImportConflictWarning, 
					getSymbol(), getComponentName());
		}

	}

	private static class ConcreteConstantSymbolInfo extends ConstantSymbolInfo {

		public ConcreteConstantSymbolInfo(
				String symbol, 
				IInternalElement element, 
				IAttributeType.String attribute, 
				String component) {
			super(symbol, false, element, attribute, component);
		}
		
		public ISCIdentifierElement createSCElement(
				IInternalParent parent, 
				IProgressMonitor monitor) throws CoreException {
			ISCConstant constant = ((ISCContextFile) parent).getSCConstant(getSymbol());
			constant.create(null, monitor);
			constant.setType(getType(), null);
			constant.setSource(getSourceElement(), monitor);
			return constant;
		}


		@Override
		protected void createConflictError(IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(
					getSourceElement(), 
					getSourceAttributeType(), 
					GraphProblem.ConstantNameConflictError, 
					getSymbol());
		}

		@Override
		protected void createConflictWarning(IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(
					getSourceElement(), 
					getSourceAttributeType(), 
					GraphProblem.ConstantNameConflictWarning, 
					getSymbol());
		}
		
	}

	public static ConstantSymbolInfo makeAbstractConstantSymbolInfo(
			String symbol, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		return new AbstractConstantSymbolInfo(symbol, element, attribute, component);
	}

	public static ConstantSymbolInfo makeConcreteConstantSymbolInfo(
			String symbol, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		return new ConcreteConstantSymbolInfo(symbol, element, attribute, component);
	}

	ConstantSymbolInfo(
			String symbol, 
			boolean imported, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, imported, element, attribute, component);
	}

	@Override
	public IRodinProblem getUntypedError() {
		return GraphProblem.UntypedConstantError;
	}

}
