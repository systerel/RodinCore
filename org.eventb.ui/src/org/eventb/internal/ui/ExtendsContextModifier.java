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
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         A modifier class for extends context elements.
 *         </p>
 */
public class ExtendsContextModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		// Try to set the name string if element is a IExtendsContext element.
		if (element instanceof IExtendsContext) {
			IExtendsContext aElement = (IExtendsContext) element;
			IAttributeFactory<IExtendsContext> factory = new ExtendsContextAbstractContextNameAttributeFactory();
			UIUtils.setStringAttribute(aElement, factory, text, null);
		}
		// Do nothing if the element is not a IExtendsContext element.
		return;
	}

}
