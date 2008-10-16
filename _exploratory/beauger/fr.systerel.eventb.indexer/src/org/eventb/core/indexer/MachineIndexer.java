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

import static org.eventb.core.EventBPlugin.*;
import static org.rodinp.core.index.RodinIndexer.*;

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
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
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

	public IRodinFile[] getDependencies(IRodinFile file) {
		if (!(file instanceof IMachineFile)) {
			return NO_DEPENDENCIES;
		}
		final IMachineFile machine = (IMachineFile) file;

		final List<IRodinFile> extendedFiles = new ArrayList<IRodinFile>();
		try {
			final IRefinesMachine[] refines = machine.getRefinesClauses();
			final ISeesContext[] sees = machine.getSeesClauses();

			addRefinedFiles(refines, extendedFiles);
			addSeenFiles(sees, extendedFiles);

		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return extendedFiles.toArray(new IRodinFile[extendedFiles.size()]);
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
			final IMachineFile absMachine = refinesMachine.getAbstractMachine();
			extendedFiles.add(absMachine);
		}
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
		final SymbolTable importST = new SymbolTable(null);
		final SymbolTable eventST = new SymbolTable(null);
		final Map<IEvent, SymbolTable> absParamTables = new HashMap<IEvent, SymbolTable>();

		processImports(index.getImports(), absParamTables, eventST, importST);

		final SymbolTable declImportST = new SymbolTable(importST);

		processVariables(file.getVariables(), declImportST);

		processPredicateElements(file.getInvariants(), declImportST);
		processPredicateElements(file.getTheorems(), declImportST);
		processExpressionElements(file.getVariants(), declImportST);

		processEvents(file.getEvents(), absParamTables, eventST, declImportST);
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
			}
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
			final String name = ident.getIdentifierString();
			final IDeclaration declaration = indexDeclaration(ident, name,
					index);
			index.export(declaration);
			final IDeclaration previousDecl = declImports.lookUpper(name);
			if (previousDecl != null) {
				final IInternalElement element = previousDecl.getElement();
				if (element instanceof IVariable) {
					// re-declaration of abstract variable
					final IRodinFile file = index.getRodinFile();
					indexReference(previousDecl, getRodinLocation(file), index);
				}
			}
			declImports.put(declaration);
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

	private void processEvents(IEvent[] events,
			Map<IEvent, SymbolTable> absParamTables, SymbolTable eventST,
			SymbolTable declImportST) throws RodinDBException {
		// index declaration for each event and event parameter, and export them
		// put the declarations into the SymbolTable
		// processLabeledElements(events, symbolTable);
		for (IEvent event : events) {
			// processIdentifierElements(event.getParameters(), symbolTable);

			final EventIndexer eventIndexer = new EventIndexer(event,
					absParamTables, eventST, declImportST, index);
			eventIndexer.process();
		}
	}

}
