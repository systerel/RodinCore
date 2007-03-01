/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ITraceableElement;
import org.eventb.core.sc.IMarkerDisplay;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;


/**
 * Basic class for symbols to be stored in a symbol table, {@link ISymbolTable}.
 * 
 * @author Stefan Hallerstede
 *
 */
public interface ISymbolInfo extends Comparable {
	
	/**
	 * Returns the symbol sting of this symbol info.
	 * 
	 * @return the symbol string
	 */
	String getSymbol();
	
	/**
	 * Returns the name of the component that contains this symbol.
	 * 
	 * @return name of the component that contains this symbol
	 */
	String getComponentName();
	
	/**
	 * Returns the source element. This is not necessarily <b>the</b> <i>source element</i>
	 * of the identifier but only <b>a</b> source element -called <i>reference element</i>- 
	 * which is in some way related to <b>the</b> source element.
	 * <p>
	 * In its function as reference element the source element serves mainly to have a
	 * meaningful place to attach problems ({@link IRodinProblem}).
	 * </p>
	 * <p>
	 * In its function as <b>the</b> source element it serves to provide traceability 
	 * information ({@link ITraceableElement}) in statically checked files.
	 * </p>
	 * Which of the two applies is decided by implementing classes.
	 * 
	 * @see ITraceableElement
	 * 
	 * @return the source element of this symbol info
	 */
	IInternalElement getSourceElement();
	
	/**
	 * Returns whether this symbol is errorneous.
	 * 
	 * @return <code>true</code> if the symbol is erroneous, <code>false</code> otherwise
	 */
	boolean hasError();
	
	/**
	 * Marks this symbol as erroneous.
	 * 
	 * @throws CoreException if the symbol info is not mutable
	 */
	void setError() throws CoreException;
	
	/**
	 * Returns whether the symbol info is (still) mutable.
	 * The static checker can mark symbol infos as immutable.
	 * In particular, this removes the possibility to mark a symbol as erroneous.
	 * 
	 * @return whether or not the symbol info is mutable.
	 */
	boolean isMutable();
	
	/**
	 * Turns the symbol immutable
	 */
	void makeImmutable();
	
	/**
	 * Creates a suitable marker for collisions in the symbol table.
	 * 
	 * @param markerDisplay a place to put the marker
	 * @throws RodinDBException if there was problem creating the marker
	 */
	void createConflictMarker(IMarkerDisplay markerDisplay) throws RodinDBException;
}
