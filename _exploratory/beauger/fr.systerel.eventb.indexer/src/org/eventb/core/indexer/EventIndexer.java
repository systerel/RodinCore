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
package org.eventb.core.indexer;

import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.indexer.EventBIndexUtil.REFERENCE;
import static org.rodinp.core.index.RodinIndexer.getRodinLocation;

import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IParameter;
import org.eventb.core.IPredicateElement;
import org.eventb.core.IWitness;
import org.eventb.core.ast.FreeIdentifier;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexingToolkit;

/**
 * @author Nicolas Beauger
 * 
 */
public class EventIndexer extends ElementIndexer {

	private final IEvent event;

	public EventIndexer(IEvent event, SymbolTable abstParamsDeclImports) {
		super(abstParamsDeclImports);
		this.event = event;
	}

	public void process(IIndexingToolkit index) throws RodinDBException {

		final SymbolTable paramsDeclImports = new SymbolTable(symbolTable);

		processParameters(event.getParameters(), paramsDeclImports, index);

		// FIXME manage priorities

		processPredicateElements(event.getGuards(), paramsDeclImports, index);
		processActions(event.getActions(), paramsDeclImports, index);

		processWitnesses(event.getWitnesses(), paramsDeclImports, index);
	}

	/**
	 * @param witnesses
	 * @param eventTable
	 * @param index
	 * @throws RodinDBException
	 */
	private void processWitnesses(IWitness[] witnesses,
			SymbolTable eventTable, IIndexingToolkit index)
			throws RodinDBException {

		processLabeledElements(witnesses, eventTable, index);
		processPredicateElements(witnesses, eventTable, index);
	}

	/**
	 * @param labels
	 * @param eventTable
	 * @param index
	 * @param ff
	 * @throws RodinDBException
	 */
	private void processLabeledElements(ILabeledElement[] labels,
			SymbolTable eventTable, IIndexingToolkit index)
			throws RodinDBException {

		for (ILabeledElement labelElem : labels) {
			final String name = getNameNoPrime(labelElem);

			final IDeclaration declaration = eventTable.lookup(name);

			if (declaration != null) {
				index.addOccurrence(declaration, REFERENCE, getRodinLocation(
						labelElem, LABEL_ATTRIBUTE));
			}
		}
	}

	/**
	 * @param labelElem
	 * @return
	 * @throws RodinDBException
	 */
	private String getNameNoPrime(ILabeledElement labelElem)
			throws RodinDBException {
		final String label = labelElem.getLabel();
		final FreeIdentifier ident = ff.makeFreeIdentifier(label, null);
		final String name;
		if (ident.isPrimed()) {
			name = ident.withoutPrime(ff).getName();
		} else {
			name = ident.getName();
		}
		return name;
	}

	/**
	 * @param parameters
	 * @param table
	 * @param index
	 * @throws RodinDBException
	 */
	private void processParameters(final IParameter[] parameters,
			SymbolTable paramsDeclImports, IIndexingToolkit index)
			throws RodinDBException {
		for (IParameter p : parameters) {
			IDeclaration decl = index.declare(p, p.getIdentifierString());
			paramsDeclImports.put(decl, false);
			index.export(decl);
		}
	}

	private void processActions(IAction[] actions,
			SymbolTable eventTable, IIndexingToolkit index)
			throws RodinDBException {
		for (IAction action : actions) {
			final AssignmentIndexer assignIndexer = new AssignmentIndexer(
					action, eventTable);
			assignIndexer.process(index);
		}
	}

	private void processPredicateElements(IPredicateElement[] preds,
			SymbolTable symbolTable, IIndexingToolkit index)
			throws RodinDBException {
		for (IPredicateElement elem : preds) {
			final PredicateIndexer predIndexer = new PredicateIndexer(elem,
					symbolTable);
			predIndexer.process(index);
		}
	}

}
