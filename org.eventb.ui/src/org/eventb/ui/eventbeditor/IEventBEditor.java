/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - extended with getFormulaFactory()
 *******************************************************************************/
package org.eventb.ui.eventbeditor;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.IGotoMarker;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.ui.IStatusChangedListener;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         This is the commond inteface for the Event-B Editor.
 * @param <R>
 *            This specifies the "input type" of the editor, e.g. IMachineRoot
 *            or IContextRoot.
 * @since 1.0
 */
public interface IEventBEditor<R extends IInternalElement> extends IEditorPart,
		IGotoMarker {

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
	 * @since 3.0
	 */
	public abstract void addStatusListener(IStatusChangedListener listener);

	/**
	 * Remove a status changed listener.
	 * <p>
	 * 
	 * @param listener
	 *            a status changed listener
	 * @since 3.0
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
	 * Returns the Rodin file associated with this editor. Can return
	 * <code>null</code> if the the editor has NOT been initialized.
	 * 
	 * @return the Rodin file (IEventBFile) associated with this editor
	 */
	public abstract R getRodinInput();

	/**
	 * Return the string ID for this editor.
	 * <p>
	 * 
	 * @return the unique string ID for the editor.
	 */
	public String getEditorId();
	
	/**
	 * Returns the formula factory used by the root input of this editor.
	 * 
	 * @return the formula factory used
	 * @since 2.0
	 */
	public FormulaFactory getFormulaFactory();
}