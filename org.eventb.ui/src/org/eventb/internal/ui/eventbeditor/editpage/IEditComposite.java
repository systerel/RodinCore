/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - made IAttributeFactory generic
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;

/**
 * @author htson
 *         <p>
 *         Interface for an edit composite (used for editing an attribute of an
 *         element).
 *         </p>
 *         <p>
 *         This interface is not intended to be implemented by clients.
 *         </p>
 * 
 */
public interface IEditComposite {

	/**
	 * Refresh the information display within this edit composite.
	 */
	public abstract void refresh(boolean refreshMarker);

	/**
	 * Select/Deselect the attribute.
	 * 
	 * @param selected
	 *            <code>true</code> to select. <code>false</code> to
	 *            deselect.
	 */
	public abstract void setSelected(boolean selected);

	/**
	 * Create the actual widgets. This should be called after
	 * {@link #setElement(IInternalElement)} and
	 * {@link #setForm(ScrolledForm)} has been called.
	 * 
	 * @param editor
	 *            the Event-B Editor
	 * @param toolkit
	 *            the form toolkit used to create the widgets.
	 * @param parent
	 *            the composite parent of all the widgets.
	 */
	// TODO To see if this method should be called directly from the
	// constructor, and should not be part of the interface.
	abstract public void createComposite(IEventBEditor<?> editor,
			FormToolkit toolkit, Composite parent);

	/**
	 * Set the corresponding element for the edit composite. This should be call
	 * after the constructor of any class implements this interface.
	 * 
	 * @param element
	 *            an attributed element
	 */
	// TODO To see if this should be set as part of the constructor and since
	// the element should not be changed after initialising.
	public abstract void setElement(IInternalElement element);

	/**
	 * Set the scrolled form of the main edit page. This should be called after
	 * the constructor for any class implements this interface.
	 * 
	 * @param form
	 *            a scrolled form
	 */
	// TODO To see if this should be set as part of the constructor and since
	// the form should not be changed after initialising.
	public abstract void setForm(ScrolledForm form);

	/**
	 * Return the corresponding attribute type for this edit composite.
	 * 
	 * @return the corresponding attribute type {@link IAttributeType}.
	 */
	public abstract IAttributeType getAttributeType();

	/**
	 * Edit the attribute given the start and end positions.
	 * 
	 * @param charStart
	 *            start position.
	 * @param charEnd
	 *            end position.
	 */
	public abstract void edit(int charStart, int charEnd);

	/**
	 * Refresh the information display within this edit with the information
	 * that an attribute has been changed.
	 */
	public abstract void refresh(Set<IAttributeType> set);

}