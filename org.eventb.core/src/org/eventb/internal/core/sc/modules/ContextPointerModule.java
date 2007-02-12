/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.state.IContextTable;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.internal.core.sc.ContextPointerArray;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ContextPointerModule extends IdentifierCreatorModule {

	protected IContextTable contextTable;
	protected IIdentifierSymbolTable identifierSymbolTable;
	protected ITypeEnvironment typeEnvironment;
	protected FormulaFactory factory;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		contextTable = 
			(IContextTable) repository.getState(IContextTable.STATE_TYPE);
		identifierSymbolTable = 
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		typeEnvironment = repository.getTypeEnvironment();
		factory = repository.getFormulaFactory();
	}


	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		contextTable = null;
		identifierSymbolTable = null;
		typeEnvironment = null;
		factory = null;
	}

	protected abstract IRodinProblem getTargetContextNotFoundProblem();
	
	protected void fetchSCContexts(
			ContextPointerArray contextPointerArray,
			IProgressMonitor monitor) throws RodinDBException, CoreException {
		
		final IIdentifierSymbolInfo[][] declaredIdentifiers =
			new IIdentifierSymbolInfo[contextPointerArray.size()][];
		
		final ISCContext[][] upContexts =
			new ISCContext[contextPointerArray.size()][];
		
		for (int index=0; index<contextPointerArray.size(); index++) {
			
			ISCContextFile scCF = contextPointerArray.getSCContextFile(index);
			
			if (!scCF.exists()) {
				createProblemMarker(
						contextPointerArray.getContextPointer(index), 
						EventBAttributes.TARGET_ATTRIBUTE,
						getTargetContextNotFoundProblem());
				
				contextPointerArray.setError(index);
				continue;
			}
			
			upContexts[index] = createUpContexts(scCF);
			
			List<IIdentifierSymbolInfo> symbolInfos = new LinkedList<IIdentifierSymbolInfo>();
			
			for (ISCContext scIC : upContexts[index]) {
				
				String contextName = scIC.getElementName();
								
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
						fetchSymbol(
								symbolInfos, 
								index,
								contextPointerArray, 
								set,
								abstractCarrierSetCreator);
					}
	
					for (ISCConstant constant : scConstants) {
						fetchSymbol(
								symbolInfos, 
								index,
								contextPointerArray, 
								constant,
								abstractConstantCreator);
					}
				}
				
				
			}
			
			declaredIdentifiers[index] = new IIdentifierSymbolInfo[symbolInfos.size()];
			symbolInfos.toArray(declaredIdentifiers[index]);
			
			monitor.worked(1);
		}
		
		commitValidContexts(contextPointerArray, declaredIdentifiers, upContexts, contextTable.size());
		
		contextPointerArray.makeImmutable();
		contextTable.makeImmutable();
		
	}

	private ISCContext[]  createUpContexts(ISCContextFile scCF) throws RodinDBException {
		ISCInternalContext[] iscic = scCF.getAbstractSCContexts();
		
		ISCContext[] upContexts = new ISCContext[iscic.length + 1];
		
		System.arraycopy(iscic, 0, upContexts, 0, iscic.length);
		
		upContexts[iscic.length] = scCF;
		
		return upContexts;
	}


	private void reuseSymbol(
			List<IIdentifierSymbolInfo> symbolInfos, 
			ISCIdentifierElement element) throws RodinDBException {
		
		IIdentifierSymbolInfo info = 
			identifierSymbolTable.getSymbolInfo(element.getIdentifierString());
		
		assert info != null;
		
		symbolInfos.add(info);
	}

	private void fetchSymbol(
			List<IIdentifierSymbolInfo> symbolList, 
			int index,
			ContextPointerArray contextPointerArray, 
			ISCIdentifierElement element,
			IIdentifierSymbolInfoCreator creator) throws CoreException {
		
		String name = element.getIdentifierString();
		
		IIdentifierSymbolInfo newSymbolInfo = 
			creator.createIdentifierSymbolInfo(
					name, 
					element, 
					contextPointerArray.getContextPointer(index));
		
		try {
			identifierSymbolTable.putSymbolInfo(newSymbolInfo);
		} catch (CoreException e) {
			
			newSymbolInfo.createConflictMarker(this);
			
			contextPointerArray.setError(index);

			// the new symbol info is discarded now
			
			IIdentifierSymbolInfo symbolInfo = 
				identifierSymbolTable.getSymbolInfo(name);
			
			symbolList.add(symbolInfo);
			
			if(symbolInfo.hasError())
				return; // the element in the symbol table has already an associated error message
			
			symbolInfo.createConflictMarker(this);
			
			if (symbolInfo.isMutable())
				symbolInfo.setError();
			
			int pointerIndex = contextPointerArray.getPointerIndex(
					symbolInfo.getReferenceElement().getHandleIdentifier());
			
			if (pointerIndex != -1)
				contextPointerArray.setError(pointerIndex);
			
			return;
		}
		
		// finally set the type of the identifier
		newSymbolInfo.setType(element.getType(factory));
		
		symbolList.add(newSymbolInfo);
	}
	
	void commitValidContexts(
			ContextPointerArray contextPointerArray, 
			final IIdentifierSymbolInfo[][] declaredIdentifiers,
			final ISCContext[][] upContexts,
			int s) throws CoreException {
		
		final int adjust = contextPointerArray.size();

		HashSet<String> contextNames = new HashSet<String>((s+adjust) * 4 / 3 + 1);
		
		ArrayList<ISCContext> validContexts = new ArrayList<ISCContext>(s+adjust);
		
		for (int index = 0; index < contextPointerArray.size(); index++) {

			if (contextPointerArray.hasError(index)) {
				for (IIdentifierSymbolInfo symbolInfo : declaredIdentifiers[index]) {
					symbolInfo.makeImmutable();
				}
				continue;
			}

			for (IIdentifierSymbolInfo symbolInfo : declaredIdentifiers[index]) {
				if (symbolInfo.isMutable()) {
					symbolInfo.makeVisible();
					symbolInfo.makeImmutable();
				}
				typeEnvironment.addName(symbolInfo.getSymbol(), symbolInfo.getType());
			}

			for (ISCContext scContext : upContexts[index]) {
				String name = scContext.getElementName();
				if (!contextNames.contains(name)) {
					contextNames.add(name);
					validContexts.add(scContext);
				}
			}
		}
		
		contextPointerArray.setValidContexts(validContexts);
		
	}
	
	protected abstract ISCInternalContext getSCInternalContext(
			IInternalParent target, String elementName);
	
	protected void createInternalContexts(
			IInternalParent target, 
			List<ISCContext> scContexts,
			ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		for (ISCContext context : scContexts) {
			
			if (context instanceof ISCInternalContext) {
				
				ISCInternalContext internalContext = (ISCInternalContext) context;
				
				internalContext.copy(target, null, null, false, monitor);
			} else {
				
				ISCContextFile contextFile = (ISCContextFile) context;
			
				ISCInternalContext internalContext = getSCInternalContext(target, context.getElementName());
				internalContext.create(null, monitor);
				
				copyElements(contextFile.getChildren(), internalContext, monitor);
				
			}
			
		}
		
		repository.setTargetChanged();
		
	}
	
	private boolean copyElements(
			IRodinElement[] elements, 
			IInternalElement target, 
			IProgressMonitor monitor) throws RodinDBException {
		
		for (IRodinElement element : elements) {
			IInternalElement internalElement = (IInternalElement) element;
			internalElement.copy(target, null, null, false, monitor);
		}
		
		return true;
	}

}
