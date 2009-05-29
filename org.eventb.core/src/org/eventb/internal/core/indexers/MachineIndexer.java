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
import static org.eventb.core.EventBPlugin.getContextFileName;
import static org.rodinp.core.RodinCore.getInternalLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.IEvent;
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
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;

/**
 * @author Nicolas Beauger
 * 
 */
public class MachineIndexer extends EventBIndexer {

	private static final String ID = "fr.systerel.eventb.indexer.machine";

	@Override
	protected void index(IInternalElement root) throws RodinDBException {
		if (!(root instanceof IMachineRoot)) {
			throwIllArgException(root);
		}
		index((IMachineRoot) root);
	}

	private void index(IMachineRoot root) throws RodinDBException {
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
			if (ident.hasIdentifierString()) {
				final String name = ident.getIdentifierString();
				if (name.length() == 0) {
					continue;
				}
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
							declImportST, currentBridge);
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

		addRefinedFiles(refines, dependFiles);
		addSeenFiles(sees, dependFiles);

		return dependFiles.toArray(new IRodinFile[dependFiles.size()]);
	}

	/**
	 * @param sees
	 * @param extendedFiles
	 * @throws RodinDBException
	 */
	private void addSeenFiles(ISeesContext[] sees,
			List<IRodinFile> extendedFiles) throws RodinDBException {
		for (ISeesContext seesContext : sees) {
			final IRodinFile seenFile = getSeenFile(seesContext);
			if (seenFile != null) {
				extendedFiles.add(seenFile);
			}
		}

	}

	/**
	 * @param seesContext
	 * @throws RodinDBException
	 */
	private IRodinFile getSeenFile(ISeesContext seesContext)
			throws RodinDBException {
		if (!seesContext.hasSeenContextName()) {
			return null;
		}

		final String seenBareName = seesContext.getSeenContextName();
		final String seenFileName = getContextFileName(seenBareName);

		final IRodinProject project = seesContext.getRodinProject();

		return project.getRodinFile(seenFileName);
	}

	/**
	 * @param refines
	 * @param extendedFiles
	 * @throws RodinDBException
	 */
	private void addRefinedFiles(IRefinesMachine[] refines,
			List<IRodinFile> extendedFiles) throws RodinDBException {
		for (IRefinesMachine refinesMachine : refines) {
			if (refinesMachine.hasAbstractMachineName()) {
				final IRodinFile absMachine =
						refinesMachine.getAbstractMachine();
				extendedFiles.add(absMachine);
			}
		}
	}

	public String getId() {
		return ID;
	}

}
