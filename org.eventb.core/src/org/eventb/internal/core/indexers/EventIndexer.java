/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.indexers;

import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;
import static org.eventb.core.EventBPlugin.DECLARATION;
import static org.eventb.core.EventBPlugin.REDECLARATION;
import static org.eventb.core.EventBPlugin.REFERENCE;
import static org.eventb.internal.core.indexers.IdentTable.getUnprimedName;
import static org.rodinp.core.RodinCore.getInternalLocation;

import java.util.Map;

import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IParameter;
import org.eventb.core.IPredicateElement;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.core.indexer.IOccurrenceKind;
import org.rodinp.core.location.IInternalLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class EventIndexer extends Cancellable {

	private final IEvent event;
	private final Map<IEvent, SymbolTable> absParamTables;
	private final SymbolTable eventST;
	private final SymbolTable declImportST;
	private final IIndexingBridge bridge;

	/**
	 * Constructor.
	 * 
	 * @param event
	 *            the event to index
	 * @param absParamTables
	 * @param eventST
	 * @param declImportST
	 *            a SymbolTable containing, by decreasing order of priority:
	 *            <ul>
	 *            <li>local declarations</li>
	 *            <li>imported declarations</li>
	 *            </ul>
	 * @param bridge
	 */
	public EventIndexer(IEvent event, Map<IEvent, SymbolTable> absParamTables,
			SymbolTable eventST, SymbolTable declImportST,
			IIndexingBridge bridge) {
		this.declImportST = declImportST;
		this.event = event;
		this.absParamTables = absParamTables;
		this.eventST = eventST;
		this.bridge = bridge;
	}

	public void process() throws RodinDBException {
		checkCancel();
		final SymbolTable absPrmDeclImpST = new SymbolTable(declImportST);

		processEventLabel(absPrmDeclImpST);

		checkCancel();
		processRefines(event.getRefinesClauses(), absPrmDeclImpST);

		checkCancel();
		final SymbolTable totalST = new SymbolTable(absPrmDeclImpST);
		processParameters(event.getParameters(), totalST);

		checkCancel();
		processPredicateElements(event.getGuards(), totalST);
		checkCancel();
		processActions(event.getActions(), totalST);

		checkCancel();
		processWitnesses(event.getWitnesses(), totalST);
	}

	private void processRefines(IRefinesEvent[] refinesEvents,
			SymbolTable absParamDeclImportST) throws RodinDBException {
		for (IRefinesEvent refinesEvent : refinesEvents) {
			if (refinesEvent.hasAbstractEventLabel()) {
				final String absEventLabel =
						refinesEvent.getAbstractEventLabel();

				processEventRedecl(absEventLabel, getInternalLocation(
						refinesEvent, TARGET_ATTRIBUTE), absParamDeclImportST);
			}
			checkCancel();
		}

	}

	private void processEventRedecl(String absEventLabel,
			IInternalLocation location, SymbolTable absParamDeclImportST) {
		final IDeclaration declAbsEvent = eventST.lookup(absEventLabel);
		if (declAbsEvent == null) {
			return;
		}
		final IInternalElement element = declAbsEvent.getElement();
		if (element.getElementType() == IEvent.ELEMENT_TYPE) {
			bridge.addOccurrence(declAbsEvent, REDECLARATION, location);
			addAbstractParams((IEvent) element, absParamDeclImportST);
		}
	}

	private void addOccurrence(IDeclaration declaration, IOccurrenceKind kind,
			IInternalElement element, IAttributeType.String attribute) {
		bridge.addOccurrence(declaration, kind, getInternalLocation(element,
				attribute));
	}
	
	private void addIdentOcc(IDeclaration declaration, IOccurrenceKind kind,
			IIdentifierElement element) {
		addOccurrence(declaration, kind, element, IDENTIFIER_ATTRIBUTE);
	}

	private void addLabelOcc(IDeclaration declaration, IOccurrenceKind kind,
			ILabeledElement element) {
		addOccurrence(declaration, kind, element, LABEL_ATTRIBUTE);
	}

	private void addAbstractParams(IEvent abstractEvent,
			SymbolTable absParamDeclImportST) {
		final SymbolTable absParamST = absParamTables.get(abstractEvent);
		if (absParamST != null) {
			absParamDeclImportST.putAll(absParamST);
		}
	}

	private void processEventLabel(SymbolTable absParamDeclImportST) throws RodinDBException {
		if (event.hasLabel()) {
			final String eventLabel = event.getLabel();
			final IDeclaration declaration = bridge.declare(event, eventLabel);
			addLabelOcc(declaration, DECLARATION, event);
			bridge.export(declaration);
			if (event.isInitialisation()) {
				processEventRedecl(IEvent.INITIALISATION, getInternalLocation(
						event, LABEL_ATTRIBUTE), absParamDeclImportST);
			}
		}
	}

	/**
	 * @param witnesses
	 * @param totalST
	 * @throws RodinDBException
	 */
	private void processWitnesses(IWitness[] witnesses, SymbolTable totalST)
			throws RodinDBException {

		processWitnessLabels(witnesses, totalST);
		processPredicateElements(witnesses, totalST);
	}

	private void processWitnessLabels(IWitness[] witnesses, SymbolTable totalST)
			throws RodinDBException {

		for (IWitness witness : witnesses) {
			if (witness.hasLabel()) {
				final String label = witness.getLabel();
				final String name = getUnprimedName(label);
				final IDeclaration declAbs = totalST.lookUpper(name);

				if (declAbs != null) {
					final IInternalElement element = declAbs.getElement();
					if (element instanceof IParameter
							|| element instanceof IVariable) {
						// could be a namesake
						addLabelOcc(declAbs, REFERENCE, witness);
					}
				}
			}
			checkCancel();
		}
	}

	private void processParameters(IParameter[] parameters, SymbolTable totalST)
			throws RodinDBException {
		for (IParameter parameter : parameters) {
			if (parameter.hasIdentifierString()) {
				final String ident = parameter.getIdentifierString();

				IDeclaration declaration = bridge.declare(parameter, ident);
				addIdentOcc(declaration, DECLARATION, parameter);
				totalST.put(declaration);
				bridge.export(declaration);

				refAnyAbstractParam(ident, parameter, totalST);
			}
		}
	}

	private void refAnyAbstractParam(String ident, IParameter parameter,
			SymbolTable totalST) {
		final IDeclaration declAbsParam = totalST.lookUpper(ident);
		if (declAbsParam != null) {
			if (declAbsParam.getElement() instanceof IParameter) {
				// could be a namesake
				addIdentOcc(declAbsParam, REDECLARATION, parameter);
			}
		}
	}

	private void processActions(IAction[] actions, SymbolTable eventTable)
			throws RodinDBException {
		for (IAction action : actions) {
			final AssignmentIndexer assignIndexer =
					new AssignmentIndexer(action, eventTable, bridge);
			assignIndexer.process();

			checkCancel();
		}
	}

	private void processPredicateElements(IPredicateElement[] preds,
			SymbolTable symbolTable) throws RodinDBException {
		for (IPredicateElement elem : preds) {
			final PredicateIndexer predIndexer =
					new PredicateIndexer(elem, symbolTable, bridge);
			predIndexer.process();

			checkCancel();
		}
	}

	protected void checkCancel() {
		checkCancel(bridge);
	}

}
