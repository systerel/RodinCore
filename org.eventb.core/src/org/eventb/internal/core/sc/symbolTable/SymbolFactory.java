/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.core.ISCWitness;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public final class SymbolFactory {

	private abstract static class CarrierSetSymbolProblem implements
			ITypedSymbolProblem {

		public CarrierSetSymbolProblem() {
			// public constructor
		}

		public IRodinProblem getUntypedError() {
			return GraphProblem.UntypedCarrierSetError;
		}

	}

	private static class LocalCarrierSetSymbolProblem extends
			CarrierSetSymbolProblem {

		public LocalCarrierSetSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.CarrierSetNameConflictError, symbolInfo
							.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.CarrierSetNameConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class ImportedCarrierSetSymbolProblem extends
			CarrierSetSymbolProblem {

		public ImportedCarrierSetSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.CarrierSetNameImportConflictError, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.CarrierSetNameImportConflictWarning,
					symbolInfo.getSymbol(), symbolInfo.getComponentName());
		}

	}

	private abstract static class ConstantSymbolProblem implements
			ITypedSymbolProblem {

		public ConstantSymbolProblem() {
			// public constructor
		}

		public IRodinProblem getUntypedError() {
			return GraphProblem.UntypedConstantError;
		}

	}

	private static class LocalConstantSymbolProblem extends
			ConstantSymbolProblem {

		public LocalConstantSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ConstantNameConflictError, symbolInfo
							.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ConstantNameConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class ImportedConstantSymbolProblem extends
			ConstantSymbolProblem {

		public ImportedConstantSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ConstantNameImportConflictError, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ConstantNameImportConflictWarning, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

	}

	private abstract static class VariableSymbolProblem implements
			ITypedSymbolProblem {

		public VariableSymbolProblem() {
			// public constructor
		}

		public IRodinProblem getUntypedError() {
			return GraphProblem.UntypedVariableError;
		}

	}

	private static class LocalVariableSymbolProblem extends
			VariableSymbolProblem {

		public LocalVariableSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.VariableNameConflictError, symbolInfo
							.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.VariableNameConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class ImportedVariableSymbolProblem extends
			VariableSymbolProblem {

		public ImportedVariableSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.VariableNameImportConflictError, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.VariableNameImportConflictWarning, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

	}

	private abstract static class ParameterSymbolProblem implements
			ITypedSymbolProblem {

		public ParameterSymbolProblem() {
			// public constructor
		}

		public IRodinProblem getUntypedError() {
			return GraphProblem.UntypedParameterError;
		}

	}

	private static class LocalParameterSymbolProblem extends
			ParameterSymbolProblem {

		public LocalParameterSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ParameterNameConflictError, symbolInfo
							.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ParameterNameConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class ImportedParameterSymbolProblem extends
			ParameterSymbolProblem {

		public ImportedParameterSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ParameterNameImportConflictError, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ParameterNameImportConflictWarning, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

	}

	private static class AxiomSymbolProblem implements ISymbolProblem {

		public AxiomSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.AxiomLabelConflictError, symbolInfo
							.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.AxiomLabelConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class TheoremSymbolProblem implements ISymbolProblem {

		public TheoremSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.TheoremLabelConflictError, symbolInfo
							.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.TheoremLabelConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class InvariantSymbolProblem implements ISymbolProblem {

		public InvariantSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.InvariantLabelConflictError, symbolInfo
							.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.InvariantLabelConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class EventSymbolProblem implements ISymbolProblem {

		public EventSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.EventLabelConflictError, symbolInfo
							.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.EventLabelConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class GuardSymbolProblem implements ISymbolProblem {

		public GuardSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.GuardLabelConflictError, symbolInfo
							.getSymbol());

		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.GuardLabelConflictWarning, symbolInfo
							.getSymbol());

		}

	}

	private static class ActionSymbolProblem implements ISymbolProblem {

		public ActionSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ActionLabelConflictError, symbolInfo
							.getSymbol());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ActionLabelConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class VariantSymbolProblem implements ISymbolProblem {

		public VariantSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			throw new UnsupportedOperationException();
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			throw new UnsupportedOperationException();

		}

	}

	private static class WitnessSymbolProblem implements ISymbolProblem {

		public WitnessSymbolProblem() {
			// public constructor
		}

		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.WitnessLabelConflictError, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.WitnessLabelConflictWarning, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

	}

	private static LocalCarrierSetSymbolProblem localCarrierSetSymbolProblem = new LocalCarrierSetSymbolProblem();
	private static ImportedCarrierSetSymbolProblem importedCarrierSetSymbolProblem = new ImportedCarrierSetSymbolProblem();
	private static LocalConstantSymbolProblem localConstantSymbolProblem = new LocalConstantSymbolProblem();
	private static ImportedConstantSymbolProblem importedConstantSymbolProblem = new ImportedConstantSymbolProblem();
	private static LocalVariableSymbolProblem localVariableSymbolProblem = new LocalVariableSymbolProblem();
	private static ImportedVariableSymbolProblem importedVariableSymbolProblem = new ImportedVariableSymbolProblem();
	private static LocalParameterSymbolProblem localParameterSymbolProblem = new LocalParameterSymbolProblem();
	private static ImportedParameterSymbolProblem importedParameterSymbolProblem = new ImportedParameterSymbolProblem();
	private static AxiomSymbolProblem axiomSymbolProblem = new AxiomSymbolProblem();
	private static TheoremSymbolProblem theoremSymbolProblem = new TheoremSymbolProblem();
	private static InvariantSymbolProblem invariantSymbolProblem = new InvariantSymbolProblem();
	private static EventSymbolProblem eventSymbolProblem = new EventSymbolProblem();
	private static GuardSymbolProblem guardSymbolProblem = new GuardSymbolProblem();
	private static ActionSymbolProblem actionSymbolProblem = new ActionSymbolProblem();
	private static VariantSymbolProblem variantSymbolProblem = new VariantSymbolProblem();
	private static WitnessSymbolProblem witnessSymbolProblem = new WitnessSymbolProblem();

	private SymbolFactory() {
		// singleton
	}

	private static SymbolFactory factory = new SymbolFactory();

	public static SymbolFactory getInstance() {
		return factory;
	}

	public IIdentifierSymbolInfo makeLocalCarrierSet(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCCarrierSet.ELEMENT_TYPE,
				persistent, problemElement, EventBAttributes.IDENTIFIER_ATTRIBUTE,
				component, localCarrierSetSymbolProblem);
	}

	public IIdentifierSymbolInfo makeImportedCarrierSet(String symbol,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component) {
		return new IdentifierSymbolInfo(symbol, ISCCarrierSet.ELEMENT_TYPE,
				persistent, problemElement, problemAttributeType, component,
				importedCarrierSetSymbolProblem);
	}

	public IIdentifierSymbolInfo makeLocalConstant(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCConstant.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localConstantSymbolProblem);
	}

	public IIdentifierSymbolInfo makeImportedConstant(String symbol,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component) {
		return new IdentifierSymbolInfo(symbol, ISCConstant.ELEMENT_TYPE,
				persistent, problemElement, problemAttributeType, component,
				importedConstantSymbolProblem);
	}

	public IIdentifierSymbolInfo makeLocalVariable(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCVariable.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localVariableSymbolProblem);
	}

	public IIdentifierSymbolInfo makeImportedVariable(String symbol,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component) {
		return new IdentifierSymbolInfo(symbol, ISCVariable.ELEMENT_TYPE,
				persistent, problemElement, problemAttributeType, component,
				importedVariableSymbolProblem);
	}

	public IIdentifierSymbolInfo makeLocalParameter(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCParameter.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localParameterSymbolProblem);
	}

	public IIdentifierSymbolInfo makeImportedParameter(String symbol,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component) {
		return new IdentifierSymbolInfo(symbol, ISCParameter.ELEMENT_TYPE,
				persistent, problemElement, problemAttributeType, component,
				importedParameterSymbolProblem);
	}

	public ILabelSymbolInfo makeAxiom(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCAxiom.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				axiomSymbolProblem);
	}

	public ILabelSymbolInfo makeTheorem(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCTheorem.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				theoremSymbolProblem);
	}

	public ILabelSymbolInfo makeInvariant(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCInvariant.ELEMENT_TYPE,
				persistent, problemElement, EventBAttributes.LABEL_ATTRIBUTE,
				component, invariantSymbolProblem);
	}

	public ILabelSymbolInfo makeEvent(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCEvent.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				eventSymbolProblem);
	}

	public ILabelSymbolInfo makeGuard(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCGuard.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				guardSymbolProblem);
	}

	public ILabelSymbolInfo makeAction(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCAction.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				actionSymbolProblem);
	}

	public ILabelSymbolInfo makeVariant(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCVariant.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				variantSymbolProblem);
	}

	public ILabelSymbolInfo makeWitness(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCWitness.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				witnessSymbolProblem);
	}

}
