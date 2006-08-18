/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core;

/**
 * Common protocol for all attributes provided by internal elements.
 * <p>
 * Attributes are attached to internal elements (that is elements that are
 * stored in a file element) and provide non-structural information pertaining
 * to that element (for instance the target of a refines clause).
 * </p>
 * <p>
 * There are two kinds of attributes: <emph>genuine</emph> and <em>derived</em>.
 * A genuine attribute is actually stored in the file containing the element to
 * which it is attached, while a derived attribute is never stored but computer
 * on the fly, based on other genuine attributes of the same element.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
@Deprecated
public interface IAttribute {

	/**
	 * Returns the name of this attribute.
	 * 
	 * @return the name of this attribute
	 */
	String getName();

	/**
	 * Returns the value of this attribute.
	 * 
	 * @return the value of this attribute
	 */
	Object getValue();

	/**
	 * Tells whether this attribute is derived.
	 * 
	 * @return <code>true<code> iff this attribute is derived
	 */
	boolean isDerived();

}
