/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

/**
 * @author htson
 *         <p>
 *         Interface for UI Spec information of an attribute.
 *         </p>
 *         <p>
 *         This interface is NOT intended for clients to implement.
 *         </p>
 */
public interface IAttributeUISpec {
	
	/**
	 * Get the prefix for the attribute.
	 * 
	 * @return The string prefix for the attribute of <code>null</code> if the
	 *         attribute has no prefix.
	 */
	public String getPrefix();

	/**
	 * Get the postfix for the attribute.
	 * 
	 * @return The string postfix for the attribute of <code>null</code> if the
	 *         attribute has no postfix.
	 */
	public String getPostfix();

	/**
	 * Check if the attribute editing area should fill horizontally.
	 * 
	 * @return <code>true</code> if the editing area should fill horizontally.
	 *         Return <code>false</code> otherwise.
	 */
	public boolean isFillHorizontal();

	/**
	 * Get the corresponding attribute factory which is used to create and/or
	 * edit the attribute.
	 * 
	 * @return an attribute factory {@link IAttributeFactory}.
	 */
	public IAttributeFactory getAttributeFactory();

}
