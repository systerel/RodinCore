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
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.IContextPointerArray;
import org.eventb.core.sc.IContextTable;
import org.eventb.core.sc.IIdentifierSymbolTable;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.ITypingState;
import org.eventb.core.sc.ProcessorModule;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.StaticChecker;
import org.eventb.internal.core.sc.symbolTable.SymbolInfoFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ContextPointerModule extends ProcessorModule {

	protected IContextTable contextTable;
	protected IIdentifierSymbolTable identifierSymbolTable;
	protected ITypingState typingState;
	protected FormulaFactory factory;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
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
	public void endModule(IRodinElement element, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		contextTable = null;
		identifierSymbolTable = null;
		typingState = null;
		factory = null;
	}


	protected void fetchSCContexts(
			IContextPointerArray contextPointerArray,
			IProgressMonitor monitor) throws RodinDBException, CoreException {
		
		for (int index=0; index<contextPointerArray.size(); index++) {
			
			ISCContextFile scCF = contextPointerArray.getSCContextFile(index);
			
			if (!scCF.exists()) {
				String message = null;
				if (contextPointerArray.getContextPointerType() == IContextPointerArray.EXTENDS_POINTER)
					message = Messages.scuser_AbstractContextNotFound;
				else if (contextPointerArray.getContextPointerType() == IContextPointerArray.SEES_POINTER)
					message = Messages.scuser_SeenContextNotFound;
				else
					assert false;
				
				issueMarker(
						IMarkerDisplay.SEVERITY_ERROR, 
						contextPointerArray.getContextPointer(index), 
						message);
				contextPointerArray.setError(index);
				continue;
			}
			
			List<ISCContext> upContexts = contextPointerArray.getUpContexts(index);
			
			createUpContexts(scCF, upContexts);
			
			for (ISCContext scIC : upContexts) {
				
				String contextName = scIC.getElementName();
								
				ISCCarrierSet[] scCarrierSets = scIC.getSCCarrierSets();
				
				ISCConstant[] scConstants = scIC.getSCConstants(); 
				
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
								set);
					}
	
					for (ISCConstant constant : scConstants) {
						fetchSymbol(
								symbolInfos, 
								index,
								contextPointerArray, 
								constant);
					}
				}
				
			}
			
			monitor.worked(1);
		}
		
		commitValidContexts(contextPointerArray, contextTable.size() * 4 / 3 + 1);
		
	}


	/**
	 * @param scCF
	 * @param upContexts
	 * @throws RodinDBException
	 */
	private void createUpContexts(ISCContextFile scCF, List<ISCContext> upContexts) 
	throws RodinDBException {
		ISCInternalContext[] iscic = scCF.getAbstractSCContexts();
		
		ISCContext[] upContextArray = new ISCContext[iscic.length + 1];
		
		System.arraycopy(iscic, 0, upContextArray, 0, iscic.length);
		
		upContextArray[iscic.length] = scCF;
		
		upContexts.addAll(Arrays.asList(upContextArray));
	}


	private void reuseSymbol(
			List<IIdentifierSymbolInfo> symbolInfos, 
			ISCIdentifierElement element) throws RodinDBException {
		
		IIdentifierSymbolInfo info = 
			(IIdentifierSymbolInfo) identifierSymbolTable.getSymbolInfo(element.getIdentifierName());
		
		assert info != null;
		
		symbolInfos.add(info);
	}

	private void fetchSymbol(
			List<IIdentifierSymbolInfo> symbolList, 
			int index,
			IContextPointerArray contextPointerArray, 
			ISCIdentifierElement element) throws CoreException {
		
		String name = element.getIdentifierName();
		
		IIdentifierSymbolInfo newSymbolInfo = 
			SymbolInfoFactory.createIdentifierSymbolInfo(
					name,
					element, 
					contextPointerArray.getContextPointer(index), 
					StaticChecker.getParentName(element));
		
		try {
			identifierSymbolTable.putSymbolInfo(newSymbolInfo);
		} catch (CoreException e) {
			
			newSymbolInfo.issueNameConflictMarker(this);
			
			contextPointerArray.setError(index);

			// the new symbol info is discarded now
			
			IIdentifierSymbolInfo symbolInfo = 
				(IIdentifierSymbolInfo) identifierSymbolTable.getSymbolInfo(name);
			
			symbolList.add(symbolInfo);
			
			if(symbolInfo.hasError())
				return; // the element in the symbol table has already an associated error message
			
			symbolInfo.issueNameConflictMarker(this);
			
			if (symbolInfo.isMutable())
				symbolInfo.setError();
			
			int pointerIndex = contextPointerArray.getPointerIndex(symbolInfo.getPointer());
			
			if (pointerIndex != -1)
				contextPointerArray.setError(pointerIndex);
			
			return;
		}
		
		// finally set the type of the identifier
		newSymbolInfo.setType(element.getType(factory));
		
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
			
				ISCInternalContext internalContext = 
					(ISCInternalContext) target.createInternalElement(
							ISCInternalContext.ELEMENT_TYPE, 
							context.getElementName(), null, monitor);
				
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
