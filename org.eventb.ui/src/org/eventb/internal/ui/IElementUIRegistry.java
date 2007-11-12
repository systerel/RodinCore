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

package org.eventb.internal.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is the interface for a singleton class that manages the UI information
 *         related to different elements that appeared in the Event-B UI.
 */
public interface IElementUIRegistry {

	/**
	 * Getting a image descriptor corresponding to an element type.
	 * <p>
	 * 
	 * @param type
	 *            a Rodin element type.
	 * @return the image descriptor corresponding to the input element type.
	 */
	public abstract ImageDescriptor getImageDescriptor(IElementType<?> type);

	/**
	 * Getting the label corresponding to an object
	 * <p>
	 * 
	 * @param obj
	 *            any object
	 * @return the label corresponding to the input object
	 */
	public abstract String getLabel(Object obj);

	/**
	 * Getting the secondary label corresponding to an object (which is used in
	 * the second column of Event-B Table Tree Viewer
	 * <p>
	 * 
	 * @param obj
	 *            any object
	 * @return the secondary label corresponding to the input object
	 */
	public abstract String getLabelAtColumn(String columnID, Object obj);

	/**
	 * Getting the priority of an object
	 * <p>
	 * 
	 * @param obj
	 *            any object
	 * @return the priority corresponding to the input object
	 */
	public abstract int getPriority(Object obj);

	/**
	 * Return if an object is select-able at a particular column (represented by
	 * the input <code>columnID</code>) in an edit-able table/tree viewer.
	 * 
	 * @param obj
	 *            an object in the edit-able table/tree viewer.
	 * @param columnID
	 *            the string representing the column in the viewer.
	 * @return <code>true</code> if the object is select-able at the given
	 *         column. Return <code>false</code> otherwise.
	 */
	public abstract boolean isNotSelectable(Object obj, String columnID);

	/**
	 * Modify the input element at a particular column (represented by the input
	 * <code>columnID</code> in an edit-able table/tree viewer. The
	 * corresponding attribute will be changed to the value represented by the
	 * input <code>text</code>
	 * 
	 * @param element
	 *            a Rodin element
	 * @param columnID
	 *            the string representing the column in the viewer.
	 * @param text
	 *            the string representing the new value of the attribute of the
	 *            input element.
	 * @throws RodinDBException
	 *             if some problems occur when setting the attribute of the
	 *             input element.
	 */
	public abstract void modify(IRodinElement element, String columnID,
			String text) throws RodinDBException;

	/**
	 * Return the default editing column for an element in the edit-able
	 * table/tree viewer.
	 * 
	 * @param element
	 *            a Rodin element
	 * @return the id of the default column in the viewer.
	 */
	public abstract String getDefaultColumn(IRodinElement element);

}