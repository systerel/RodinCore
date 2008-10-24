/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - made IAttributeFactory generic
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;

/**
 * @author htson
 *         <p>
 *         Interface for UI Spec information of an attribute.
 *         </p>
 *         <p>
 *         This interface is NOT intended for clients to implement.
 *         </p>
 */
public interface IAttributeUISpec<E extends IAttributedElement> {
	
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
	public IAttributeFactory<E> getAttributeFactory();

	/**
	 * Get the corresponding attribute type.
	 * 
	 * @return an attribute type, this must not be <code>null</code>.
	 */
	public IAttributeType getAttributeType();

}
