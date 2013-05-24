/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.itemdescription;

import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;

/**
 * Common protocol for describing how to render attributes in the Event-B UI.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 3.0
 */
public interface IAttributeDesc extends IItemDesc {

	/**
	 * Returns the suffix that should be displayed after the attribute.
	 * 
	 * @return the string corresponding to the suffix to display after the
	 *         attribute value
	 */
	String getSuffix();

	/**
	 * Indicates that the editing area should expand horizontally.
	 * 
	 * @return <code>true</code> if the attribute edition zone shall expand
	 *         horizontally, <code>false</code> otherwise
	 */
	boolean isHorizontalExpand();

	/**
	 * Returns the attributeType corresponding to this description.
	 * 
	 * @return the attribute type which this description concerns
	 */
	IAttributeType getAttributeType();

	/**
	 * Returns the manipulation associated to the attribute.
	 * 
	 * @return the attribute manipulation for the concerned attribute
	 */
	IAttributeManipulation getManipulation();

}