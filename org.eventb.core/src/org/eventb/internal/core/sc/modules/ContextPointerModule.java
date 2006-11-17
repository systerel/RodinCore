/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
import org.eventb.core.sc.state.IContextPointerArray;
import org.eventb.core.sc.state.IContextTable;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.sc.state.ITypingState;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.state.IStateRepository;
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
	protected ITypingState typingState;
	protected FormulaFactory factory;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		contextTable = 
			(IContextTable) repository.getState(IContextTable.STATE_TYPE);
		identifierSymbolTable = 
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		typingState =
			(ITypingState) repository.getState(ITypingState.STATE_TYPE);
		factory = repository.getFormulaFactory();
	}


	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		contextTable = null;
		identifierSymbolTable = null;
		typingState = null;
		factory = null;
	}

	protected abstract IRodinProblem getTargetContextNotFoundProblem();

	protected void fetchSCContexts(
			IContextPointerArray contextPointerArray,
			IProgressMonitor monitor) throws RodinDBException, CoreException {
		
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
			
			List<ISCContext> upContexts = contextPointerArray.getUpContexts(index);
			
			createUpContexts(scCF, upContexts);
			
			for (ISCContext scIC : upContexts) {
				
				String contextName = scIC.getElementName();
								
				ISCCarrierSet[] scCarrierSets = scIC.getSCCarrierSets(null);
				
				ISCConstant[] scConstants = scIC.getSCConstants(null); 
				
				List<IIdentifierSymbolInfo> symbolInfos = 
					contextPointerArray.getIdentifierSymbolInfos(index);
				
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
			
			monitor.worked(1);
		}
		
		commitValidContexts(contextPointerArray, contextTable.size() * 4 / 3 + 1);
		
	}

	private void createUpContexts(ISCContextFile scCF, List<ISCContext> upContexts) 
	throws RodinDBException {
		ISCInternalContext[] iscic = scCF.getAbstractSCContexts(null);
		
		ISCContext[] upContextArray = new ISCContext[iscic.length + 1];
		
		System.arraycopy(iscic, 0, upContextArray, 0, iscic.length);
		
		upContextArray[iscic.length] = scCF;
		
		upContexts.addAll(Arrays.asList(upContextArray));
	}


	private void reuseSymbol(
			List<IIdentifierSymbolInfo> symbolInfos, 
			ISCIdentifierElement element) throws RodinDBException {
		
		IIdentifierSymbolInfo info = 
			identifierSymbolTable.getSymbolInfo(element.getIdentifierString(null));
		
		assert info != null;
		
		symbolInfos.add(info);
	}

	private void fetchSymbol(
			List<IIdentifierSymbolInfo> symbolList, 
			int index,
			IContextPointerArray contextPointerArray, 
			ISCIdentifierElement element,
			IIdentifierSymbolInfoCreator creator) throws CoreException {
		
		String name = element.getIdentifierString(null);
		
		IIdentifierSymbolInfo newSymbolInfo = 
			creator.createIdentifierSymbolInfo(name, element, contextPointerArray.getContextPointer(index));
		
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
			
			int pointerIndex = contextPointerArray.getPointerIndex(symbolInfo.getPointer());
			
			if (pointerIndex != -1)
				contextPointerArray.setError(pointerIndex);
			
			return;
		}
		
		// finally set the type of the identifier
		newSymbolInfo.setType(element.getType(factory, null));
		
		symbolList.add(newSymbolInfo);
	}
	
	void commitValidContexts(
			IContextPointerArray contextPointerArray, 
			int s) throws CoreException {

		HashSet<String> contextNames = new HashSet<String>(s);
		
		ArrayList<ISCContext> validContexts = new ArrayList<ISCContext>(s);
		
		ITypeEnvironment typeEnvironment = typingState.getTypeEnvironment();
		
		for (int index = 0; index < contextPointerArray.size(); index++) {

			List<IIdentifierSymbolInfo> symbolList = 
				contextPointerArray.getIdentifierSymbolInfos(index);

			if (contextPointerArray.hasError(index)) {
				for (IIdentifierSymbolInfo symbolInfo : symbolList) {
					symbolInfo.setImmutable();
				}
				continue;
			}

			for (IIdentifierSymbolInfo symbolInfo : symbolList) {
				if (symbolInfo.isMutable()) {
					symbolInfo.setVisible();
					symbolInfo.setImmutable();
				}
				typeEnvironment.addName(symbolInfo.getSymbol(), symbolInfo.getType());
			}

			List<ISCContext> upContexts = 
				contextPointerArray.getUpContexts(index);

			for (ISCContext scContext : upContexts) {
				String name = scContext.getElementName();
				if (!contextNames.contains(name)) {
					contextNames.add(name);
					validContexts.add(scContext);
				}
			}
		}
		
		contextPointerArray.getValidContexts().addAll(validContexts);
		
	}
	
	protected abstract ISCInternalContext getSCInternalContext(IInternalParent target, String elementName);
	
	protected void createInternalContexts(
			IInternalParent target, 
			List<ISCContext> scContexts,
			IStateRepository repository,
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
		
		repository.setChanged();
		
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
