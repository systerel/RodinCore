/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
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
import static org.rodinp.core.RodinCore.getInternalLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IExpressionElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;

/**
 * @author Nicolas Beauger
 * 
 */
public class MachineIndexer extends EventBIndexer {

	private static final String ID = "fr.systerel.eventb.indexer.machine";

	private static final DependenceIndexer<ISeesContext> seesIndexer = new DependenceIndexer<ISeesContext>() {

		@Override
		protected boolean hasName(ISeesContext clause) throws RodinDBException {
			return clause.hasSeenContextName();
		}

		@Override
		protected String getName(ISeesContext clause) throws RodinDBException {
			return clause.getSeenContextName();
		}

		@Override
		protected IEventBRoot getRoot(ISeesContext clause)
				throws RodinDBException {
			return clause.getSeenContextRoot();
		}

	};

	private static DependenceIndexer<IRefinesMachine> refinesIndexer = new DependenceIndexer<IRefinesMachine>() {

		@Override
		protected boolean hasName(IRefinesMachine clause)
				throws RodinDBException {
			return clause.hasAbstractMachineName();
		}

		@Override
		protected String getName(IRefinesMachine clause)
				throws RodinDBException {
			return clause.getAbstractMachineName();
		}

		@Override
		protected IEventBRoot getRoot(IRefinesMachine clause)
				throws RodinDBException {
			return clause.getAbstractMachineRoot();
		}

	};

	@Override
	protected void index(IInternalElement root) throws RodinDBException {
		if (!(root instanceof IMachineRoot)) {
			throwIllArgException(root);
		}
		index((IMachineRoot) root);
	}

	private void index(IMachineRoot root) throws RodinDBException {
		checkCancel();
		
		indexAndExportRoot(root);
		checkCancel();

		processSees(root.getSeesClauses());
		processRefines(root.getRefinesClauses());
		checkCancel();
		
		final SymbolTable importST = new SymbolTable(null);
		final SymbolTable eventST = new SymbolTable(null);
		final Map<IEvent, SymbolTable> absParamTables =
				new HashMap<IEvent, SymbolTable>();
		processImports(currentBridge.getImports(), absParamTables, eventST,
				importST);
		checkCancel();

		final SymbolTable declImportST = new SymbolTable(importST);
		processVariables(root.getVariables(), declImportST);
		checkCancel();

		processPredicateElements(root.getInvariants(), declImportST);
		checkCancel();
		processExpressionElements(root.getVariants(), declImportST);
		checkCancel();

		processEvents(root.getEvents(), absParamTables, eventST, declImportST);
	}

	private void processSees(ISeesContext[] seesClauses)
			throws RodinDBException {
		seesIndexer.process(currentBridge, seesClauses);
	}

	private void processRefines(IRefinesMachine[] refinesClauses)
			throws RodinDBException {
		refinesIndexer.process(currentBridge, refinesClauses);
	}

	private void processImports(IDeclaration[] imports,
			Map<IEvent, SymbolTable> absParamTables, SymbolTable eventST,
			SymbolTable importST) {
		for (IDeclaration declaration : imports) {
			final IInternalElement element = declaration.getElement();
			if (element instanceof IEvent) {
				eventST.put(declaration);
			} else if (element instanceof IParameter) {
				addAbstractParam(absParamTables, declaration);
			} else {
				// NOTE: the following statement makes abstract variables
				// visible in guards and actions, even if they are not
				// re-declared, but this is not a visibility checker => OK
				importST.put(declaration);
				if (!(element instanceof IVariable)) {
					export(declaration);
				}
			}
			checkCancel();
		}
	}

	private void addAbstractParam(Map<IEvent, SymbolTable> absParamST,
			IDeclaration paramDecl) {
		final IRodinElement parent = paramDecl.getElement().getParent();
		if (!(parent instanceof IEvent)) {
			return;
		}
		final IEvent event = (IEvent) parent;
		SymbolTable abstractST = absParamST.get(event);
		if (abstractST == null) {
			abstractST = new SymbolTable(null);
			absParamST.put(event, abstractST);
		}
		abstractST.put(paramDecl);
	}

	private void processVariables(IIdentifierElement[] idents,
			SymbolTable declImports) throws RodinDBException {

		for (IIdentifierElement ident : idents) {
			final String name = getIdentifierName(ident);
			if (name != null) {
				final IDeclaration declaration = indexDeclaration(ident, name);
				export(declaration);
				refIfRedeclared(name, declImports, ident);
				declImports.put(declaration);
			}
		}
	}

	private void refIfRedeclared(final String name, SymbolTable declImports,
			IIdentifierElement refElement) {
		final IDeclaration previousDecl = declImports.lookUpper(name);
		if (previousDecl != null) {
			final IInternalElement element = previousDecl.getElement();
			if (element instanceof IVariable) {
				// re-declaration of abstract variable
				indexRedeclaration(previousDecl, getInternalLocation(
						refElement, IDENTIFIER_ATTRIBUTE));
			}
		}
	}

	private void processExpressionElements(IExpressionElement[] exprs,
			SymbolTable symbolTable) throws RodinDBException {
		for (IExpressionElement expr : exprs) {
			final ExpressionIndexer exprIndexer =
					new ExpressionIndexer(expr, symbolTable, currentBridge);
			exprIndexer.process();

			checkCancel();
		}
	}

	private void processEvents(IEvent[] events,
			Map<IEvent, SymbolTable> absParamTables, SymbolTable eventST,
			SymbolTable declImportST) throws RodinDBException {
		for (IEvent event : events) {
			final EventIndexer eventIndexer =
					new EventIndexer(event, absParamTables, eventST,
							declImportST, currentBridge, this);
			eventIndexer.process();
		}
	}

	@Override
	public IRodinFile[] getDeps(IInternalElement root) throws RodinDBException {
		if (!(root instanceof IMachineRoot)) {
			throwIllArgException(root);
		}
		final IMachineRoot machine = (IMachineRoot) root;

		final List<IRodinFile> dependFiles = new ArrayList<IRodinFile>();

		final IRefinesMachine[] refines = machine.getRefinesClauses();
		final ISeesContext[] sees = machine.getSeesClauses();
		
		final List<IRodinFile> refined = refinesIndexer.getDeps(refines);
		final List<IRodinFile> seen = seesIndexer.getDeps(sees);

		dependFiles.addAll(refined);
		dependFiles.addAll(seen);

		return dependFiles.toArray(new IRodinFile[dependFiles.size()]);
	}

	public String getId() {
		return ID;
	}

}
