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
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.symbolTable.ICarrierSetSymbolInfo;
import org.eventb.internal.core.Util;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class CarrierSetSymbolInfo 
	extends IdentifierSymbolInfo
	implements ICarrierSetSymbolInfo {

	private static class AbstractCarrierSetSymbolInfo extends CarrierSetSymbolInfo {

		public AbstractCarrierSetSymbolInfo(
				String symbol, 
				IInternalElement element, 
				IAttributeType.String attribute, 
				String component) {
			super(symbol, true, element, attribute, component);
		}

		public ISCIdentifierElement createSCElement(
				IInternalParent parent, IProgressMonitor monitor) throws CoreException {
			throw Util.newCoreException("Attempt to create abstract carrier set");
		}

		@Override
		protected void createConflictError(IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(
					getSourceElement(), 
					getSourceAttributeType(), 
					GraphProblem.CarrierSetNameImportConflictError, 
					getSymbol(), getComponentName());
		}

		@Override
		protected void createConflictWarning(IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(
					getSourceElement(), 
					getSourceAttributeType(), 
					GraphProblem.CarrierSetNameImportConflictWarning, 
					getSymbol(), getComponentName());
		}

	}
	
	private static class ConcreteCarrierSetSymbolInfo extends CarrierSetSymbolInfo {

		public ConcreteCarrierSetSymbolInfo(
				String symbol, 
				IInternalElement element, 
				IAttributeType.String attribute, 
				String component) {
			super(symbol, false, element, attribute, component);
		}
		
		public ISCIdentifierElement createSCElement(
				IInternalParent parent, 
				IProgressMonitor monitor) throws CoreException {
			ISCCarrierSet set = ((ISCContextFile) parent).getSCCarrierSet(getSymbol());
			set.create(null, monitor);
			set.setType(getType(), null);
			set.setSource(getSourceElement(), monitor);
			return set;
		}

		@Override
		protected void createConflictError(IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(
					getSourceElement(), 
					getSourceAttributeType(), 
					GraphProblem.CarrierSetNameConflictError, 
					getSymbol());
		}

		@Override
		protected void createConflictWarning(IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(
					getSourceElement(), 
					getSourceAttributeType(), 
					GraphProblem.CarrierSetNameConflictWarning, 
					getSymbol());
		}
	}
	
	protected CarrierSetSymbolInfo(
			String symbol, 
			boolean imported, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, imported, element, attribute, component);
	}
	
	public static CarrierSetSymbolInfo makeAbstractCarrierSetSymbolInfo(
			String symbol, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		return new AbstractCarrierSetSymbolInfo(symbol, element, attribute, component);
	}
	
	public static CarrierSetSymbolInfo makeConcreteCarrierSetSymbolInfo(
			String symbol, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		return new ConcreteCarrierSetSymbolInfo(symbol, element, attribute, component);
	}

	@Override
	public IRodinProblem getUntypedError() {
		return GraphProblem.UntypedCarrierSetError;
	}

}
