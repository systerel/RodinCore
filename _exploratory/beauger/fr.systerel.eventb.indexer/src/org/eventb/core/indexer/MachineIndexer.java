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

import static org.rodinp.core.index.RodinIndexer.getRodinLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.IEvent;
import org.eventb.core.IExpressionElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.IParameter;
import org.eventb.core.IPredicateElement;
import org.eventb.core.IRefinesEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexingToolkit;

/**
 * @author Nicolas Beauger
 * 
 */
public class MachineIndexer extends EventBIndexer {

	private static final String ID = "fr.systerel.eventb.indexer.machine";

	private IIndexingToolkit index;

	// TODO consider passing as parameter

	public IRodinFile[] getDependencies(IRodinFile file) {
		return NO_DEPENDENCIES;
	}

	public String getId() {
		return ID;
	}

	public void index(IIndexingToolkit index) {
		this.index = index;
		final IRodinFile file = index.getRodinFile();

		if (file instanceof IMachineFile) {
			try {
				index((IMachineFile) file);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				if (DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	private void index(IMachineFile file) throws RodinDBException {
		final SymbolTable imports = new SymbolTable(null);

		final Map<IEvent, SymbolTable> abstractEventTables = processImports(
				index.getImports(), imports);

		final SymbolTable declImports = new SymbolTable(imports);

		processIdentifierElements(file.getVariables(), declImports);

		processPredicateElements(file.getInvariants(), declImports);
		processPredicateElements(file.getTheorems(), declImports);
		processExpressionElements(file.getVariants(), declImports);

		processEvents(file.getEvents(), declImports, abstractEventTables);
	}

	// TODO simplify the code
	// TODO make more homogeneous argument passing and returning
	private Map<IEvent, SymbolTable> processImports(IDeclaration[] imports,
			SymbolTable symbolTable) {
		// put the declarations into the SymbolTable
		// filter event parameters to put them into separate SymbolTables
		final Map<IEvent, SymbolTable> abstractSTs = new HashMap<IEvent, SymbolTable>();
		for (IDeclaration declaration : imports) {
			// index.export(declaration);
			final IInternalElement element = declaration.getElement();
//			if (element instanceof IEvent) {
//				final IEvent event = (IEvent) element;
//				addAbstractDeclaration(abstractSTs, declaration, event);
//			} else 
				if (element instanceof IParameter) {
				final IParameter parameter = (IParameter) element;
				final IEvent event = (IEvent) parameter.getParent();
				addAbstractDeclaration(abstractSTs, declaration, event);
			} else {
				// FIXME should not be added to the main table as they are not
				// visible in guards and actions if not re-declared
				// but we are not implementing a visibility checker => OK
				symbolTable.put(declaration, false);
			}
		}
		return abstractSTs;
	}

	private void addAbstractDeclaration(Map<IEvent, SymbolTable> result,
			IDeclaration declaration, IEvent event) {
		SymbolTable abstractST = result.get(event);
		if (abstractST == null) {
			abstractST = new SymbolTable(null);
			result.put(event, abstractST);
		}
		abstractST.put(declaration, false);
	}

	// TODO pull up
	private void processIdentifierElements(IIdentifierElement[] idents,
			SymbolTable declmports) throws RodinDBException {
		// index declaration for each identifier element and export them
		// put the declarations into the SymbolTable
		// FIXME re-declarations should override import declarations in ST
		// and generate a REFERENCE to the abstract one
		for (IIdentifierElement ident : idents) {
			final String name = ident.getIdentifierString();
			final IDeclaration declaration = indexDeclaration(ident, name,
					index);
			index.export(declaration);
			final IDeclaration previousDecl = declmports.lookup(name);
			if (previousDecl != null) { // re-declaration of abstract variable
				final IRodinFile file = index.getRodinFile();
				indexReference(previousDecl, getRodinLocation(file), index);
			}
			declmports.put(declaration, true);
		}
	}

	private void processPredicateElements(IPredicateElement[] preds,
			SymbolTable symbolTable) throws RodinDBException {
		for (IPredicateElement elem : preds) {
			final PredicateIndexer predIndexer = new PredicateIndexer(elem,
					symbolTable);
			predIndexer.process(index);
		}
	}

	private void processExpressionElements(IExpressionElement[] exprs,
			SymbolTable symbolTable) throws RodinDBException {
		for (IExpressionElement expr : exprs) {
			final ExpressionIndexer exprIndexer = new ExpressionIndexer(expr,
					symbolTable);
			exprIndexer.process(index);
		}
	}

	private void processEvents(IEvent[] events, SymbolTable declImports,
			Map<IEvent, SymbolTable> abstractEventTables)
			throws RodinDBException {
		// index declaration for each event and event parameter, and export them
		// put the declarations into the SymbolTable
		// processLabeledElements(events, symbolTable);
		for (IEvent event : events) {
			// processIdentifierElements(event.getParameters(), symbolTable);
			final SymbolTable abstPrmsDeclImports = new SymbolTable(declImports);
			addAbstractSymbols(event, abstractEventTables, abstPrmsDeclImports);
			
			final EventIndexer eventIndexer = new EventIndexer(event,
					abstPrmsDeclImports);
			eventIndexer.process(index);
		}
	}

	/**
	 * @param eventTable
	 * @throws RodinDBException
	 */
	private void addAbstractSymbols(IEvent event,
			Map<IEvent, SymbolTable> abstractEventTables, SymbolTable abstPrmsDeclImports)
			throws RodinDBException {
		final List<IEvent> abstractEvents = getEvents(event.getRefinesClauses(), abstPrmsDeclImports);
		for (IEvent abstEvent : abstractEvents) {
			final SymbolTable abstSymbolTable = abstractEventTables
					.get(abstEvent);
			if (abstSymbolTable != null) {
				abstPrmsDeclImports.putAll(abstSymbolTable, false);
			}
		}
	}

	/**
	 * @param abstParams 
	 * @throws RodinDBException
	 */
	private List<IEvent> getEvents(IRefinesEvent[] refinesClauses, SymbolTable imports)
			throws RodinDBException {
		final List<IEvent> result = new ArrayList<IEvent>();
		for (IRefinesEvent refinesEvent : refinesClauses) {
			final String absEventLabel = refinesEvent.getAbstractEventLabel();
			final IDeclaration declaration = imports.lookup(absEventLabel);
			final IInternalElement element = declaration.getElement();
			final IEvent absEvent = (IEvent) element;
			result.add(absEvent);
		}
		return result;
	}

}
