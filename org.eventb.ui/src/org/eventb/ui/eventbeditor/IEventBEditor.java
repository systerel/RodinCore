/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui.eventbeditor;

import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.eventbeditor.IStatusChangedListener;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 *         <p>
 *         This is the commond inteface for the Event-B Editor.
 * @param <F>
 *            This specifies the "input type" of the editor, e.g. IMachineFile
 *            or IContextFile.
 */
public interface IEventBEditor<F extends IRodinFile> extends IEditorPart {

	/**
	 * Add an element to be new.
	 * <p>
	 * 
	 * @param element
	 *            a Rodin element
	 */
	public abstract void addNewElement(IRodinElement element);

	/**
	 * Add a new status changed listener.
	 * <p>
	 * 
	 * @param listener
	 *            a status changed listener
	 */
	public abstract void addStatusListener(IStatusChangedListener listener);

	/**
	 * Remove a status changed listener.
	 * <p>
	 * 
	 * @param listener
	 *            a status changed listener
	 */
	public abstract void removeStatusListener(IStatusChangedListener listener);

	/**
	 * Check if an element is new (unsaved).
	 * <p>
	 * 
	 * @param element
	 *            a Rodin element
	 * @return <code>true</code> if the element is new (unsaved)
	 */
	public abstract boolean isNewElement(IRodinElement element);

	/**
	 * Add a new element changed listener.
	 * <p>
	 * 
	 * @param listener
	 *            an element changed listener
	 */
	public abstract void addElementChangedListener(
			IElementChangedListener listener);

	/**
	 * Remove an element changed listener.
	 * <p>
	 * 
	 * @param listener
	 *            an element changed listener
	 */
	public abstract void removeElementChangedListener(
			IElementChangedListener listener);

	/**
	 * Set the selection in the editor.
	 * <p>
	 * 
	 * @param ssel
	 *            the current selecting element. It can be an IRodinElement or a
	 *            TreeNode (from the ProjectExplorer).
	 */
	public abstract void edit(Object ssel);

	/**
	 * Select an internal element in the editor.
	 * <p>
	 * 
	 * @param element
	 *            the internal element which will be selected.
	 */
	public abstract void setSelection(IInternalElement element);

	/**
	 * Returns the Rodin file associated with this editor. Can return
	 * <code>null</code> if the the editor has NOT been initialized.
	 * 
	 * @return the Rodin file (IEventBFile) associated with this editor
	 */
	public abstract F getRodinInput();

	/**
	 * Return the string ID for this editor.
	 * <p>
	 * 
	 * @return the unique string ID for the editor.
	 */
	public String getEditorId();
}