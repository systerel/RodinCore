/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     University of Southampton - redesign of symbol table
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.IMarkerDisplay;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

/**
 * Basic class for symbols to be stored in a symbol table, {@link ISymbolTable}.
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.1
 */
public interface ISymbolInfo<E extends IInternalElement, T extends IInternalElementType<? extends E>>
		extends Comparable<ISymbolInfo<?, ?>>, IAttributedSymbol {

	/**
	 * Returns the symbol sting of this symbol info.
	 * 
	 * @return the symbol string
	 */
	String getSymbol();

	/**
	 * Returns the element type of this symbol. This is the element type used
	 * when this symbol is made persistent using <code>createSCElement()</code>.
	 * 
	 * @return the element type of this symbol
	 */
	T getSymbolType();

	/**
	 * Returns the name of the component that contains this symbol.
	 * 
	 * @return name of the component that contains this symbol
	 */
	String getComponentName();

	/**
	 * Returns an element to which to attach problem markers. This is not
	 * necessarily the source element from which the symbol was extracted.
	 * <p>
	 * For example, a constant <code>IConstant</code> <i>c</i> of an Event-B
	 * context <code>IContextFile</code> <i>C</i> will be represented by an
	 * identifier symbol. In context <i>C</i> the element returned is the
	 * element <code>IConstant</code> <i>c</i>. In a context <i>D</i> that sees
	 * (<code>ISeesContext</code> <i>s</i>) context <i>C</i> the element
	 * returned for the identifier symbol is <i>s</i>.
	 * </p>
	 * 
	 * @return the element associated with this symbol info
	 */
	IInternalElement getProblemElement();

	/**
	 * Returns an attribute to which to attach problem markers. See description
	 * of <code>getProblemElement()</code>.
	 * <p>
	 * The problem attribute returned may be <code>null</code> if there is none.
	 * </p>
	 * 
	 * @return an attribute to which to attach problem markers,
	 *         <code>null</code> if there is none
	 */
	IAttributeType getProblemAttributeType();

	/**
	 * Returns whether this symbol is erroneous.
	 * 
	 * @return <code>true</code> if the symbol is erroneous, <code>false</code>
	 *         otherwise
	 */
	boolean hasError();

	/**
	 * Marks this symbol as erroneous.
	 * 
	 * @throws CoreException
	 *             if the symbol info is not mutable
	 */
	void setError() throws CoreException;

	/**
	 * Returns whether the symbol info is (still) mutable. The static checker
	 * can mark symbol infos as immutable: this removes the possibility to mark
	 * a symbol as erroneous.
	 * 
	 * @return whether or not the symbol info is mutable.
	 */
	boolean isMutable();

	/**
	 * Makes the symbol immutable. This excludes the attributes managed by way
	 * of <code>IAttributedSymbol</code>.
	 */
	void makeImmutable();

	/**
	 * Returns whether the symbol is persistent. That is whether it can be
	 * serialized.
	 * 
	 * @see ILabelSymbolInfo#createSCElement(IInternalElement, String,
	 *      IProgressMonitor)
	 * @see IIdentifierSymbolInfo#createSCElement(IInternalElement,
	 *      IProgressMonitor)
	 * 
	 * @return whether the symbol is persistent
	 */
	boolean isPersistent();

	/**
	 * Creates a suitable marker for collisions in the symbol table.
	 * 
	 * @param markerDisplay
	 *            a place to put the marker
	 * @throws RodinDBException
	 *             if there was problem creating the marker
	 */
	void createConflictMarker(IMarkerDisplay markerDisplay)
			throws RodinDBException;
}
