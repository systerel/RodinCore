/*******************************************************************************
 * Copyright (c) 2008, 2010 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     University of Dusseldorf - added theorem attribute
 *     Systerel - published symbol factory
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.core.ISCWitness;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.internal.core.sc.symbolTable.ISymbolProblem;
import org.eventb.internal.core.sc.symbolTable.ITypedSymbolProblem;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.LabelSymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * This class is the factory class for symbol informations used by static
 * checker modules.
 * <p>
 * It is intended to be used to instantiate all types of symbol informations.
 * </p>
 * 
 * @author Stefan Hallerstede
 * @since 1.3
 */
public final class SymbolFactory {

	private abstract static class CarrierSetSymbolProblem implements
			ITypedSymbolProblem {

		public CarrierSetSymbolProblem() {
			// public constructor
		}

		@Override
		public IRodinProblem getUntypedError() {
			return GraphProblem.UntypedCarrierSetError;
		}

	}

	private static class LocalCarrierSetSymbolProblem extends
			CarrierSetSymbolProblem {

		public LocalCarrierSetSymbolProblem() {
			// public constructor
		}

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.CarrierSetNameConflictError, symbolInfo
							.getSymbol());
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
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

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.CarrierSetNameImportConflictError, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
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

		@Override
		public IRodinProblem getUntypedError() {
			return GraphProblem.UntypedConstantError;
		}

	}

	private static class LocalConstantSymbolProblem extends
			ConstantSymbolProblem {

		public LocalConstantSymbolProblem() {
			// public constructor
		}

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ConstantNameConflictError, symbolInfo
							.getSymbol());
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
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

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ConstantNameImportConflictError, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
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

		@Override
		public IRodinProblem getUntypedError() {
			return GraphProblem.UntypedVariableError;
		}

	}

	private static class LocalVariableSymbolProblem extends
			VariableSymbolProblem {

		public LocalVariableSymbolProblem() {
			// public constructor
		}

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.VariableNameConflictError, symbolInfo
							.getSymbol());

		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
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

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.VariableNameImportConflictError, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
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

		@Override
		public IRodinProblem getUntypedError() {
			return GraphProblem.UntypedParameterError;
		}

	}

	private static class LocalParameterSymbolProblem extends
			ParameterSymbolProblem {

		public LocalParameterSymbolProblem() {
			// public constructor
		}

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ParameterNameConflictError, symbolInfo
							.getSymbol());
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
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

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ParameterNameImportConflictError, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ParameterNameImportConflictWarning, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

	}

	private static class AxiomSymbolProblem implements ISymbolProblem {

		public AxiomSymbolProblem() {
			// public constructor
		}

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.AxiomLabelConflictError, symbolInfo
							.getSymbol());
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.AxiomLabelConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class InvariantSymbolProblem implements ISymbolProblem {

		public InvariantSymbolProblem() {
			// public constructor
		}

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.InvariantLabelConflictError, symbolInfo
							.getSymbol());
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.InvariantLabelConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class EventSymbolProblem implements ISymbolProblem {

		public EventSymbolProblem() {
			// public constructor
		}

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.EventLabelConflictError, symbolInfo
							.getSymbol());
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.EventLabelConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class GuardSymbolProblem implements ISymbolProblem {

		public GuardSymbolProblem() {
			// public constructor
		}

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.GuardLabelConflictError, symbolInfo
							.getSymbol());

		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.GuardLabelConflictWarning, symbolInfo
							.getSymbol());

		}

	}

	private static class ActionSymbolProblem implements ISymbolProblem {

		public ActionSymbolProblem() {
			// public constructor
		}

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ActionLabelConflictError, symbolInfo
							.getSymbol());
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.ActionLabelConflictWarning, symbolInfo
							.getSymbol());
		}

	}

	private static class VariantSymbolProblem implements ISymbolProblem {

		public VariantSymbolProblem() {
			// public constructor
		}

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			throw new UnsupportedOperationException();

		}

	}

	private static class WitnessSymbolProblem implements ISymbolProblem {

		public WitnessSymbolProblem() {
			// public constructor
		}

		@Override
		public void createConflictError(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
					symbolInfo.getProblemAttributeType(),
					GraphProblem.WitnessLabelConflictError, symbolInfo
							.getSymbol(), symbolInfo.getComponentName());
		}

		@Override
		public void createConflictWarning(ISymbolInfo<?, ?> symbolInfo,
				IMarkerDisplay markerDisplay) throws RodinDBException {
			markerDisplay.createProblemMarker(symbolInfo.getProblemElement(),
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

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for a local carrier set
	 * with the given parameters.
	 * 
	 * @param symbol
	 *            the name of the carrier set
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public IIdentifierSymbolInfo makeLocalCarrierSet(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCCarrierSet.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localCarrierSetSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for an imported carrier
	 * set with the given parameters.
	 * 
	 * @param symbol
	 *            the name of the carrier set
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public IIdentifierSymbolInfo makeImportedCarrierSet(String symbol,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component) {
		return new IdentifierSymbolInfo(symbol, ISCCarrierSet.ELEMENT_TYPE,
				persistent, problemElement, problemAttributeType, component,
				importedCarrierSetSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for a local constant with
	 * the given parameters.
	 * 
	 * @param symbol
	 *            the name of the constant
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public IIdentifierSymbolInfo makeLocalConstant(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCConstant.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localConstantSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for an imported constant
	 * with the given parameters.
	 * 
	 * @param symbol
	 *            the name of the constant
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public IIdentifierSymbolInfo makeImportedConstant(String symbol,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component) {
		return new IdentifierSymbolInfo(symbol, ISCConstant.ELEMENT_TYPE,
				persistent, problemElement, problemAttributeType, component,
				importedConstantSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for a local variable with
	 * the given parameters.
	 * 
	 * @param symbol
	 *            the name of the variable
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public IIdentifierSymbolInfo makeLocalVariable(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCVariable.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localVariableSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for an imported variable
	 * with the given parameters.
	 * 
	 * @param symbol
	 *            the name of the variable
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public IIdentifierSymbolInfo makeImportedVariable(String symbol,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component) {
		return new IdentifierSymbolInfo(symbol, ISCVariable.ELEMENT_TYPE,
				persistent, problemElement, problemAttributeType, component,
				importedVariableSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for a local parameter
	 * with the given parameters.
	 * 
	 * @param symbol
	 *            the name of the parameter
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public IIdentifierSymbolInfo makeLocalParameter(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new IdentifierSymbolInfo(symbol, ISCParameter.ELEMENT_TYPE,
				persistent, problemElement,
				EventBAttributes.IDENTIFIER_ATTRIBUTE, component,
				localParameterSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for an imported parameter
	 * with the given parameters.
	 * 
	 * @param symbol
	 *            the name of the parameter
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public IIdentifierSymbolInfo makeImportedParameter(String symbol,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component) {
		return new IdentifierSymbolInfo(symbol, ISCParameter.ELEMENT_TYPE,
				persistent, problemElement, problemAttributeType, component,
				importedParameterSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for a local axiom with
	 * the given parameters.
	 * 
	 * @param symbol
	 *            the name of the axiom
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public ILabelSymbolInfo makeLocalAxiom(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCAxiom.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				axiomSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for a local invariant
	 * with the given parameters.
	 * 
	 * @param symbol
	 *            the name of the invariant
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public ILabelSymbolInfo makeLocalInvariant(String symbol,
			boolean persistent, IInternalElement problemElement,
			String component) {
		return new LabelSymbolInfo(symbol, ISCInvariant.ELEMENT_TYPE,
				persistent, problemElement, EventBAttributes.LABEL_ATTRIBUTE,
				component, invariantSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for a local event with
	 * the given parameters.
	 * 
	 * @param symbol
	 *            the name of the event
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public ILabelSymbolInfo makeLocalEvent(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCEvent.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				eventSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for a local guard with
	 * the given parameters.
	 * 
	 * @param symbol
	 *            the name of the guard
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public ILabelSymbolInfo makeLocalGuard(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCGuard.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				guardSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for an imported guard
	 * with the given parameters.
	 * 
	 * @param symbol
	 *            the name of the guard
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public ILabelSymbolInfo makeImportedGuard(String symbol,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component) {
		return new LabelSymbolInfo(symbol, ISCGuard.ELEMENT_TYPE, persistent,
				problemElement, problemAttributeType, component,
				guardSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for a local action with
	 * the given parameters.
	 * 
	 * @param symbol
	 *            the name of the action
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public ILabelSymbolInfo makeLocalAction(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCAction.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				actionSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for an imported action
	 * with the given parameters.
	 * 
	 * @param symbol
	 *            the name of the action
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public ILabelSymbolInfo makeImportedAction(String symbol,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component) {
		return new LabelSymbolInfo(symbol, ISCAction.ELEMENT_TYPE, persistent,
				problemElement, problemAttributeType, component,
				actionSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for a local variant with
	 * the given parameters.
	 * 
	 * @param symbol
	 *            the name of the variant
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public ILabelSymbolInfo makeLocalVariant(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCVariant.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				variantSymbolProblem);
	}

	/**
	 * Creates a new instance of IIdentifierSymbolInfo for a local witness with
	 * the given parameters.
	 * 
	 * @param symbol
	 *            the name of the witness
	 * @param persistent
	 *            <code>true</code> iff the resulting info shall be persistent
	 * @param problemElement
	 *            an element to which to attach problem markers
	 * @param component
	 *            the name of the component that contains this symbol
	 * @return a new instance of IIdentifierSymbolInfo
	 * @see ISymbolInfo
	 */
	public ILabelSymbolInfo makeLocalWitness(String symbol, boolean persistent,
			IInternalElement problemElement, String component) {
		return new LabelSymbolInfo(symbol, ISCWitness.ELEMENT_TYPE, persistent,
				problemElement, EventBAttributes.LABEL_ATTRIBUTE, component,
				witnessSymbolProblem);
	}

}
