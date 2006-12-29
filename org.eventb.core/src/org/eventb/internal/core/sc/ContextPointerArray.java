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
import java.util.List;

import org.eventb.core.ISCContext;
import org.eventb.core.ISCContextFile;
import org.eventb.core.sc.state.IContextPointerArray;
import org.eventb.internal.core.tool.state.ToolState;
import org.rodinp.core.IInternalElement;

/**
 * 
 * The contexts referenced by a context (or machine) are stored in 
 * a context pointer array (preserving order). Contexts referenced by the
 * referenced contexts are kept in <i>up-lists</i>.
 * 
 * @author Stefan Hallerstede
 *
 */
public class ContextPointerArray extends ToolState implements IContextPointerArray {

	private final int stateSize;
	
	private final PointerType pointerType;
	
	private final IInternalElement[] contextPointers;
	
	private final ISCContextFile[] contextFiles;
	
	private final boolean[] error;
	
	// TODO replace this by an array!
	// the list of referenced contexts is usually not long!
	private final Hashtable<String, Integer> indexMap; 
	
	private final List<List<ISCContext>> upContexts;
	
	private List<ISCContext> validContexts;

	public ContextPointerArray(
			PointerType pointerType,
			IInternalElement[] contextPointers, 
			ISCContextFile[] contextFiles) {
		
		assert contextPointers.length == contextFiles.length;
		
		assert pointerType == IContextPointerArray.PointerType.EXTENDS_POINTER
			|| pointerType == IContextPointerArray.PointerType.SEES_POINTER;
		
		stateSize = contextPointers.length;
		
		this.pointerType = pointerType;
		
		this.contextPointers = contextPointers;
		this.contextFiles = contextFiles;
		
		indexMap = new Hashtable<String, Integer>(stateSize * 4 / 3 + 1);
		upContexts = new ArrayList<List<ISCContext>>(stateSize);
		validContexts = new ArrayList<ISCContext>(0);
		
		for (int i=0; i<stateSize; i++) {
			indexMap.put(contextPointers[i].getHandleIdentifier(), i);
			upContexts.add(i, new ArrayList<ISCContext>(0));
		}
		
		error = new boolean[stateSize];
		
	}
	
	/**
	 * Sets the error flag of the context pointer with the specified index.
	 * 
	 * @param index the index of the context pointer
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

	/**
	 * Returns the list of statically checked contexts referenced (transitively) by the 
	 * statically checked context with the specified index.
	 * 
	 * @param index the index of the statically checked context
	 * @return the list of statically checked contexts referenced (transitively) by the 
	 * statically checked context with the specified index
	 */
	// TODO remove this from state component!
	public List<ISCContext> getUpContexts(int index) {
		return upContexts.get(index);
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

	public PointerType getContextPointerType() {
		return pointerType;
	}

	/** 
	 * returns the valid contexts, i.e., those that do not have errors.
	 */
	public List<ISCContext> getValidContexts() {
		return new ArrayList<ISCContext>(validContexts);
	}
	public void setValidContexts(List<ISCContext> validContexts) {
		this.validContexts = validContexts;
	}

}
