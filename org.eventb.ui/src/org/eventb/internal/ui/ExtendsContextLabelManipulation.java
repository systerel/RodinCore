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

import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.eventbeditor.editpage.ExtendsContextAbstractContextNameAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;

/**
 * @author htson
 *         <p>
 *         A modifier class for extends context elements.
 *         </p>
 */
public class ExtendsContextLabelManipulation extends
		AbstractLabelManipulation<IExtendsContext> {

	private static final IAttributeFactory<IExtendsContext> factory = new ExtendsContextAbstractContextNameAttributeFactory();

	@Override
	IExtendsContext getElement(Object obj) {
		if (obj instanceof IExtendsContext) {
			return (IExtendsContext) obj;
		}
		return null;
	}

	@Override
	IAttributeFactory<IExtendsContext> getFactory(IExtendsContext element) {
		return factory;
	}

}
