/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ISCContext;
import org.eventb.core.ISCContextFile;
import org.eventb.core.sc.state.IContextPointerArray;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextPointerArray implements IContextPointerArray {

	private final int stateSize;
	
	private final int pointerType;
	
	private final IInternalElement[] contextPointers;
	
	private final ISCContextFile[] contextFiles;
	
	private final boolean[] error;
	
	private final Hashtable<String, Integer> indexMap; 
	
	private final List<List<IIdentifierSymbolInfo>> symbolInfos;
	
	private final List<List<ISCContext>> upContexts;
	
	private final List<ISCContext> validContexts;

	public ContextPointerArray(
			int pointerType,
			IInternalElement[] contextPointers, 
			ISCContextFile[] contextFiles) {
		
		assert contextPointers.length == contextFiles.length;
		
		assert pointerType == EXTENDS_POINTER || pointerType == SEES_POINTER;
		
		stateSize = contextPointers.length;
		
		this.pointerType = pointerType;
		
		this.contextPointers = contextPointers;
		this.contextFiles = contextFiles;
		
		indexMap = new Hashtable<String, Integer>(stateSize * 4 / 3 + 1);
		symbolInfos = new ArrayList<List<IIdentifierSymbolInfo>>(stateSize);
		upContexts = new ArrayList<List<ISCContext>>(stateSize);
		validContexts = new ArrayList<ISCContext>(0);
		
		for (int i=0; i<stateSize; i++) {
			indexMap.put(contextPointers[i].getHandleIdentifier(), i);
			symbolInfos.add(i, new LinkedList<IIdentifierSymbolInfo>());
			upContexts.add(i, new ArrayList<ISCContext>(0));
		}
		
		error = new boolean[stateSize];
		
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IContextPointerArray#setError()
	 */
	public void setError(int index) {
		error[index] = true;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IContextPointerArray#hasError(java.lang.String)
	 */
	public boolean hasError(int index) {
		return error[index];
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IContextPointerArray#getUpContextList(java.lang.String)
	 */
	public List<ISCContext> getUpContexts(int index) {
		return upContexts.get(index);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IContextPointerArray#getIdentifierSymbolInfoList(java.lang.String)
	 */
	public List<IIdentifierSymbolInfo> getIdentifierSymbolInfos(
			int index) {
		return symbolInfos.get(index);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public String getStateType() {
		return STATE_TYPE;
	}

	public int size() {
		return stateSize;
	}

	public int getPointerIndex(String pointer) {
		Integer i = indexMap.get(pointer);
		return (i == null) ? -1 : i;
	}

	public IInternalElement getContextPointer(int index) {
		return contextPointers[index];
	}

	public ISCContextFile getSCContextFile(int index) {
		return contextFiles[index];
	}

	public int getContextPointerType() {
		return pointerType;
	}

	public List<ISCContext> getValidContexts() {
		return validContexts;
	}

}
