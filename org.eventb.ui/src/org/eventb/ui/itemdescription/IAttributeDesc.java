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
public interface IAttributeDesc {

	/**
	 * Returns the prefix that should be displayed before the attribute.
	 * 
	 * @return the string corresponding to the prefix to display before the
	 *         attribute value
	 */
	String getPrefix();

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

	/**
	 * Tells if the attribute is displayed as a choice in a list, that is was
	 * contributed as a <code>choiceAttribute</code> to the
	 * <code>editorItems</code> extension point.
	 * 
	 * @return <code>true</code> if the attribute is a choice attribute,
	 *         <code>false</code> otherwise
	 */
	boolean isChoiceAttribute();

	/**
	 * Tells if the attribute is displayed as a toggle, that is was contributed
	 * as a <code>toggleAttribute</code> to the <code>editorItems</code>
	 * extension point.
	 * 
	 * @return <code>true</code> if the attribute is a toggle attribute,
	 *         <code>false</code> otherwise
	 */
	boolean isToggleAttribute();

	/**
	 * Tells if the attribute is displayed as text, that is was contributed as a
	 * <code>textAttribute</code> to the <code>editorItems</code> extension
	 * point.
	 * 
	 * @return <code>true</code> if the attribute is a text attribute,
	 *         <code>false</code> otherwise
	 */
	boolean isTextAttribute();

	/**
	 * Tells if the attribute is displayed as text on several lines. This means
	 * that the attribute is a text attribute and that the <code>style</code>
	 * attribute was set to <code>multi</code>.
	 * 
	 * @return <code>true</code> if the attribute is a text attribute displayed
	 *         on several lines, <code>false</code> otherwise
	 * @see #isTextAttribute()
	 */
	boolean isMultiLine();

}
