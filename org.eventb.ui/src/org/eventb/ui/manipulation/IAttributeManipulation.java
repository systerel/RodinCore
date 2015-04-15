/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - made IAttributeFactory generic
 *******************************************************************************/
package org.eventb.ui.manipulation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for manipulating attributes in the Event-B UI.
 * 
 * Implementations of this interface should be contributed to the
 * <code>org.eventb.ui.editorItems</code> extension point in either of the
 * following elements:
 * <ul>
 * <li><code>choiceAttribute</code>,</li>
 * <li><code>textAttribute</code>,</li>
 * <li><code>toggleAttribute</code>.</li>
 * </ul>
 * 
 * @author htson
 * @since 3.0
 */
public interface IAttributeManipulation {

	/**
	 * Sets the value of the attribute for the given element to some default.
	 * This method is called at widget creation or when the user selects the
	 * <code>UNDEFINED</code> pseudo-entry in a combo-box.
	 * 
	 * @param element
	 *            an internal element
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @throws RodinDBException
	 *             if some problem occurred
	 */
	void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns true if the given element has the attribute.
	 * 
	 * @param element
	 *            an internal element
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @throws RodinDBException
	 *             if some problem occurred
	 */
	boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the value of the attribute (as a <code>String</code>) of the
	 * given element.
	 * 
	 * @param element
	 *            an internal element
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @return the string value of the attribute
	 * @throws CoreException
	 *             if some problem occurred
	 */
	String getValue(IRodinElement element, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * Sets the value of the attribute of the given element from a
	 * <code>String</code>.
	 * 
	 * @param element
	 *            an internal element
	 * @param value
	 *            string value of the attribute
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @throws RodinDBException
	 *             if some problem occurred
	 */
	void setValue(IRodinElement element, String value, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Removes the attribute from the given element.
	 * 
	 * @param element
	 *            an internal element
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @throws RodinDBException
	 *             if some problem occurred
	 */
	void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Gets the possible values of the attribute for the given element. This is
	 * used for filling the list of a combo-box. Returns <code>null</code> if
	 * the attribute is not intended to be edited by a combo-box widget.
	 * <p>
	 * The returned list must include the current value of the attribute,
	 * otherwise the display of the combo box will not be accurate on Linux and
	 * Mac OS X.
	 * </p>
	 * 
	 * @param element
	 *            an internal element
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @return an array of all values that the attribute can take, or
	 *         <code>null</code> if unknown
	 */
	String[] getPossibleValues(IRodinElement element, IProgressMonitor monitor);

}
