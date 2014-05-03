/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - got factory from repository
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.sc.state.IContextTable;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.IReservedNameTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.sc.ContextPointerArray;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class ContextPointerModule extends IdentifierCreatorModule {

	protected IContextTable contextTable;
	protected IIdentifierSymbolTable identifierSymbolTable;
	protected ITypeEnvironmentBuilder typeEnvironment;
	protected FormulaFactory factory;
	protected IReservedNameTable reservedNameTable;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.ProcessorModule#initModule(org.rodinp.core.IRodinElement
	 * , org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		contextTable = (IContextTable) repository
				.getState(IContextTable.STATE_TYPE);
		identifierSymbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);
		typeEnvironment = repository.getTypeEnvironment();
		factory = repository.getFormulaFactory();
		// reservedNameTable must be initialized by sub-classes
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement
	 * , org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		contextTable = null;
		identifierSymbolTable = null;
		typeEnvironment = null;
		factory = null;
		reservedNameTable = null;
	}

	protected boolean fetchSCContexts(ContextPointerArray contextPointerArray,
			IProgressMonitor monitor) throws RodinDBException, CoreException {

		boolean accurate = true;

		final IIdentifierSymbolInfo[][] declaredIdentifiers = new IIdentifierSymbolInfo[contextPointerArray
				.size()][];

		final ISCContext[][] upContexts = new ISCContext[contextPointerArray
				.size()][];

		final List<String> topNames = new ArrayList<String>(contextPointerArray
				.size());

		for (int index = 0; index < contextPointerArray.size(); index++) {

			final IRodinFile scCF = contextPointerArray.getSCContextFile(index);
			
			if (scCF == null)
				continue; // the context file has not been found

			final ISCContextRoot scRoot = (ISCContextRoot) scCF.getRoot();
			String name = scRoot.getComponentName();
			if (topNames.contains(name)) {
				topNames.add(null);
				createProblemMarker(contextPointerArray
						.getContextPointer(index),
						EventBAttributes.TARGET_ATTRIBUTE,
						getRedundantContextWarning(), name);
			} else {
				topNames.add(name);
			}

			accurate &= scRoot.isAccurate();

			upContexts[index] = createUpContexts(scCF);

			List<IIdentifierSymbolInfo> symbolInfos = new LinkedList<IIdentifierSymbolInfo>();

			for (ISCContext scIC : upContexts[index]) {

				String contextName = scIC.getComponentName();

				ISCCarrierSet[] scCarrierSets = scIC.getSCCarrierSets();

				ISCConstant[] scConstants = scIC.getSCConstants();

				if (contextTable.containsContext(contextName)) {

					for (ISCCarrierSet set : scCarrierSets) {
						reuseSymbol(symbolInfos, set);
					}

					for (ISCConstant constant : scConstants) {
						reuseSymbol(symbolInfos, constant);
					}

				} else {

					contextTable.addContext(contextName, scIC);

					for (ISCCarrierSet set : scCarrierSets) {
						fetchSymbol(symbolInfos, index, contextPointerArray,
								set, importedCarrierSetCreator);
					}

					for (ISCConstant constant : scConstants) {
						fetchSymbol(symbolInfos, index, contextPointerArray,
								constant, importedConstantCreator);
					}
				}

			}

			declaredIdentifiers[index] = new IIdentifierSymbolInfo[symbolInfos
					.size()];
			symbolInfos.toArray(declaredIdentifiers[index]);

			monitor.worked(1);
		}

		commitValidContexts(contextPointerArray, topNames, declaredIdentifiers,
				upContexts, contextTable.size());

		contextPointerArray.makeImmutable();
		contextTable.makeImmutable();

		return accurate;
	}

	protected abstract IRodinProblem getRedundantContextWarning();

	private ISCContext[] createUpContexts(final IRodinFile scCF)
			throws RodinDBException {
		final ISCContextRoot root = (ISCContextRoot) scCF.getRoot();
		ISCInternalContext[] iscic = root.getAbstractSCContexts();

		ISCContext[] upContexts = new ISCContext[iscic.length + 1];

		System.arraycopy(iscic, 0, upContexts, 0, iscic.length);

		upContexts[iscic.length] = root;

		return upContexts;
	}

	private void reuseSymbol(List<IIdentifierSymbolInfo> symbolInfos,
			ISCIdentifierElement element) throws RodinDBException {

		IIdentifierSymbolInfo info = identifierSymbolTable
				.getSymbolInfo(element.getIdentifierString());

		assert info != null;

		symbolInfos.add(info);
	}

	private void fetchSymbol(List<IIdentifierSymbolInfo> symbolList, int index,
			ContextPointerArray contextPointerArray,
			ISCIdentifierElement element, IIdentifierSymbolInfoCreator creator)
			throws CoreException {

		String name = element.getIdentifierString();

		IIdentifierSymbolInfo newSymbolInfo = creator
				.createIdentifierSymbolInfo(name, element, contextPointerArray
						.getContextPointer(index));

		if (reservedNameTable.isInConflict(name)) {
			reservedNameTable.issueWarningFor(name, this);
			newSymbolInfo.createConflictMarker(this);
			contextPointerArray.setError(index);
			return;
		}

		if (!identifierSymbolTable.tryPutSymbolInfo(newSymbolInfo)) {

			newSymbolInfo.createConflictMarker(this);

			contextPointerArray.setError(index);

			// the new symbol info is discarded now

			IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
					.getSymbolInfo(name);

			if (symbolInfo.hasError())
				return; // the element in the symbol table has already an
						// associated error message

			symbolInfo.createConflictMarker(this);

			if (symbolInfo.isMutable())
				symbolInfo.setError();

			int pointerIndex = contextPointerArray.getPointerIndex(symbolInfo
					.getProblemElement().getHandleIdentifier());

			if (pointerIndex != -1)
				contextPointerArray.setError(pointerIndex);

			return;
		}

		// finally set the type of the identifier
		newSymbolInfo.setType(element.getType(factory));

		symbolList.add(newSymbolInfo);
	}

	void commitValidContexts(final ContextPointerArray contextPointerArray,
			final List<String> topNames,
			final IIdentifierSymbolInfo[][] declaredIdentifiers,
			final ISCContext[][] upContexts, int s) throws CoreException {

		final int arraySize = contextPointerArray.size();

		final HashSet<String> contextNames = new HashSet<String>(
				(s + arraySize) * 4 / 3 + 1);

		final ArrayList<ISCContext> validContexts = new ArrayList<ISCContext>(s
				+ arraySize);

		final boolean[] redundant = new boolean[arraySize];

		for (int index = 0; index < arraySize; index++) {

			if (contextPointerArray.getSCContextFile(index) == null)
				continue; // there is no context file for this index

			if (contextPointerArray.hasError(index)) {
				for (IIdentifierSymbolInfo symbolInfo : declaredIdentifiers[index]) {
					if (symbolInfo.isMutable()) {
						symbolInfo.setError();
						symbolInfo.makeImmutable();
					}
				}
				continue;
			}

			for (IIdentifierSymbolInfo symbolInfo : declaredIdentifiers[index]) {
				if (symbolInfo.isMutable()) {
					symbolInfo.makeImmutable();

					typeEnvironment.addName(symbolInfo.getSymbol(), symbolInfo
							.getType());
				}
				assert typeEnvironment.contains(symbolInfo.getSymbol());
			}

			final ISCContext[] contexts = upContexts[index];
			for (int up = 0; up < contexts.length; up++) {// ISCContext
															// scContext :
															// contexts) {
				String name = contexts[up].getComponentName();
				if (!contextNames.contains(name)) {
					contextNames.add(name);
					validContexts.add(contexts[up]);
				}
				int i = topNames.indexOf(name);
				if (i != -1 && i != index && up != contexts.length - 1) {
					redundant[i] = true;
				}
			}
		}

		for (int index = 0; index < arraySize; index++) {
			if (redundant[index] == true) {
				createProblemMarker(contextPointerArray
						.getContextPointer(index),
						EventBAttributes.TARGET_ATTRIBUTE,
						getRedundantContextWarning(), topNames.get(index));
			}
		}

		contextPointerArray.setValidContexts(validContexts);

	}

	protected abstract ISCInternalContext getSCInternalContext(
			IInternalElement target, String elementName);

	protected void createInternalContexts(IInternalElement target,
			List<ISCContext> scContexts, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		for (ISCContext context : scContexts) {

			if (context instanceof ISCInternalContext) {

				ISCInternalContext internalContext = (ISCInternalContext) context;

				internalContext.copy(target, null, null, false, monitor);
			} else {

				ISCContextRoot contextFile = (ISCContextRoot) context;

				ISCInternalContext internalContext = getSCInternalContext(
						target, contextFile.getComponentName());
				internalContext.create(null, monitor);

				copyElements(contextFile.getChildren(), internalContext,
						monitor);

			}

		}

	}

	private boolean copyElements(IRodinElement[] elements,
			IInternalElement target, IProgressMonitor monitor)
			throws RodinDBException {

		for (IRodinElement element : elements) {
			// Do not copy nested internal contexts.
			if (element.getElementType() != ISCInternalContext.ELEMENT_TYPE) {
				IInternalElement internalElement = (IInternalElement) element;
				internalElement.copy(target, null, null, false, monitor);
			}
		}

		return true;
	}

}
