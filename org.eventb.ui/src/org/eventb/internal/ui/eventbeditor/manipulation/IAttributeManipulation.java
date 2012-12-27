/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
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
package org.eventb.internal.ui.eventbeditor.manipulation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This interface associated with attribute registry. Client implements
 *         this interface to extend the editor page for a specific attribute of
 *         a specific element type.
 *         </p>
 */
public interface IAttributeManipulation {

	/**
	 * Set the default value of the attribute for a given element.
	 * 
	 * @param element
	 *            an internal element
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             if some problems occurred.
	 */
	public abstract void setDefaultValue(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Return true if a given element has the attribute
	 * 
	 * @param element
	 *            an internal element
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             if some problems occurred.
	 */
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Get the value of the attribute (in term of string) of a given element.
	 * 
	 * @param element
	 *            an internal element.
	 * @param monitor
	 *            a progress monitor.
	 * @return the string value of the attribute. This must not be
	 *         <code>null</code>.
	 * @throws RodinDBException
	 *             if some problems occurred.
	 */
	public abstract String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Set the value of the attribute of a given element from a string.
	 * 
	 * @param element
	 *            an internal element.
	 * @param value
	 *            value of the attribute (in term of string). This value must
	 *            not be <code>null</code>
	 * @param monitor
	 *            a progress monitor.
	 * @throws RodinDBException
	 *             if some problems occurred
	 */
	public abstract void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Remove the attribute of a given element.
	 * 
	 * @param element
	 *            an internal element.
	 * @param monitor
	 *            a progress monitor.
	 * @throws RodinDBException
	 *             if some problems occurred.
	 */
	public abstract void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Get the possible values of the attribute of a given element. This is used
	 * for setting up the possible values of combo dropdown list. Return null if
	 * the attribute is not intended to be edited by a combo widget.
	 * 
	 * @param element
	 *            an internal element
	 * @param monitor
	 *            a progress monitor
	 * @return an array of strings represents possible value of this attribute.
	 *         Return <code>null</code> if the attribute has undefined
	 *         possible values.
	 */
	public abstract String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor);

}
