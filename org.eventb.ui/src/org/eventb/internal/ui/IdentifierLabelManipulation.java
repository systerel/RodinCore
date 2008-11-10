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

import org.eventb.core.IIdentifierElement;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;

public class IdentifierLabelManipulation extends
		AbstractLabelManipulation<IIdentifierElement> {

	private static final HashMap<Class<? extends IIdentifierElement>, IAttributeFactory<IIdentifierElement>> factory = new HashMap<Class<? extends IIdentifierElement>, IAttributeFactory<IIdentifierElement>>();

	@Override
	IIdentifierElement getElement(Object obj) {
		if (obj instanceof IIdentifierElement) {
			return (IIdentifierElement) obj;
		}
		return null;
	}

	@Override
	IAttributeFactory<IIdentifierElement> getFactory(IIdentifierElement element) {
		final Class<? extends IIdentifierElement> key = element.getClass();
		if (factory.containsKey(key))
			return factory.get(key);

		final IAttributeFactory<IIdentifierElement> f = UIUtils
				.getIdentifierAttributeFactory(element);
		factory.put(key, f);
		return f;
	}
}
