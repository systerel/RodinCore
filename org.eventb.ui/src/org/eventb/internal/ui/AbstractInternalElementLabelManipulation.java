/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *******************************************************************************/
package org.eventb.internal.ui;

import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.ui.IElementLabelProvider;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public abstract class AbstractInternalElementLabelManipulation<E extends IAttributedElement>
		implements IElementLabelProvider, IElementModifier {

	public String getLabel(Object obj) throws RodinDBException {
		E element = getElement(obj);
		if (element == null)
			return null;
		return getFactory(element).getValue(element, null);
	}

	public void modify(IRodinElement obj, String text) throws RodinDBException {
		E element = getElement(obj);
		if (element == null)
			return;
		UIUtils.setStringAttribute(element, getFactory(element), text, null);
	}

	abstract E getElement(Object obj);

	abstract IAttributeFactory<E> getFactory(E element);
}
