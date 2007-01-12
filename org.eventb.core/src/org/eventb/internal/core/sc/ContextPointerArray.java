/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
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

	@Override
	public void makeImmutable() {
		super.makeImmutable();
		validContexts = Collections.unmodifiableList(validContexts);
	}
	private final int arraySize;
	
	private final PointerType pointerType;
	
	private final IInternalElement[] contextPointers;
	
	private final ISCContextFile[] contextFiles;
	
	private final boolean[] error;
	
	private final ArrayList<String> indexMap; 
	
	private List<ISCContext> validContexts;

	public ContextPointerArray(
			PointerType pointerType,
			IInternalElement[] contextPointers, 
			ISCContextFile[] contextFiles) {
		
		assert contextPointers.length == contextFiles.length;
		
		assert pointerType == IContextPointerArray.PointerType.EXTENDS_POINTER
			|| pointerType == IContextPointerArray.PointerType.SEES_POINTER;
		
		arraySize = contextPointers.length;
		
		this.pointerType = pointerType;
		
		this.contextPointers = contextPointers;
		this.contextFiles = contextFiles;
		
		indexMap = new ArrayList<String>(arraySize);
		validContexts = null;
		
		for (IInternalElement contextPointer : contextPointers) {
			indexMap.add(contextPointer.getHandleIdentifier());
		}
		
		error = new boolean[arraySize];
		
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

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public String getStateType() {
		return STATE_TYPE;
	}

	public int size() {
		return arraySize;
	}

	public int getPointerIndex(final String pointer) {
		return indexMap.indexOf(pointer);
	}

	public IInternalElement getContextPointer(final int index) {
		return contextPointers[index];
	}

	public ISCContextFile getSCContextFile(final int index) {
		return contextFiles[index];
	}

	public PointerType getContextPointerType() {
		return pointerType;
	}

	/** 
	 * returns the valid contexts, i.e., those that do not have errors.
	 */
	public List<ISCContext> getValidContexts() {
		return validContexts;
	}
	public void setValidContexts(List<ISCContext> validContexts) throws CoreException {
		assertMutable();
		this.validContexts = validContexts;
	}

}
