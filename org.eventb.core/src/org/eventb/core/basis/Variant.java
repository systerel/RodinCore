/*******************************************************************************
 * Copyright (c) 2006, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IVariant;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B variants as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>IVariant</code>.
 * </p>
 * 
 * @author Laurent Voisin
 * @author Stefan Hallerstede
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class Variant extends EventBElement implements IVariant {

	public Variant(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IVariant> getElementType() {
		return ELEMENT_TYPE;
	}

	/**
	 * Tests whether the label is defined or not.
	 * 
	 * For backward compatibility with previous versions, we always consider that
	 * the label is set, even if it is not present in the database.
	 * 
	 * @return whether the label is defined or not
	 * @since 3.4
	 */
	@Override
	public boolean hasLabel() {
		return true;
	}

	/**
	 * Sets the label contained in this element.
	 * 
	 * For backward compatibility with previous versions, if the new label is the
	 * {@link #DEFAULT_LABEL}, then the corresponding attribute is removed from the
	 * Rodin database rather than set.
	 * 
	 * @param label   the label for the element
	 * @param monitor a progress monitor, or <code>null</code> if progress reporting
	 *                is not desired
	 * @since 3.4
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	@Override
	public void setLabel(String label, IProgressMonitor monitor) throws RodinDBException {
		if (DEFAULT_LABEL.equals(label)) {
			removeAttribute(EventBAttributes.LABEL_ATTRIBUTE, monitor);
		} else {
			super.setLabel(label, monitor);
		}
	}

	/**
	 * Returns the label contained in this element.
	 * 
	 * For backward compatibility with previous versions, if the attribute is not
	 * present, the {@link #DEFAULT_LABEL} is returned.
	 * 
	 * @return the label of this element as a string
	 * @since 3.4
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	@Override
	public String getLabel() throws RodinDBException {
		if (super.hasLabel()) {
			return super.getLabel();
		}
		return DEFAULT_LABEL;
	}
}
