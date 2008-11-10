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

import java.util.HashMap;

import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;

public class LabelLabelManipulation extends
		AbstractLabelManipulation<ILabeledElement> {

	private static final HashMap<Class<? extends ILabeledElement>, IAttributeFactory<ILabeledElement>> factory = new HashMap<Class<? extends ILabeledElement>, IAttributeFactory<ILabeledElement>>();

	@Override
	ILabeledElement getElement(Object obj) {
		if (obj instanceof ILabeledElement) {
			return (ILabeledElement) obj;
		}
		return null;
	}

	@Override
	IAttributeFactory<ILabeledElement> getFactory(ILabeledElement element) {
		final Class<? extends ILabeledElement> key = element.getClass();
		if (factory.containsKey(key))
			return factory.get(key);

		final IAttributeFactory<ILabeledElement> f = UIUtils
				.getLabelAttributeFactory(element);
		factory.put(key, f);
		return f;
	}
}
