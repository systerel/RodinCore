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

import org.eventb.core.IExpressionElement;
import org.eventb.internal.ui.eventbeditor.editpage.ExpressionAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;

/**
 * @author htson
 *         <p>
 *         An modifier class for expression elements.
 *         </p>
 */
public class ExpressionLabelManipulation extends
		AbstractInternalElementLabelManipulation<IExpressionElement> {

	private static final IAttributeFactory<IExpressionElement> factory = new ExpressionAttributeFactory();

	@Override
	IExpressionElement getElement(Object obj) {
		if (obj instanceof IExpressionElement) {
			return (IExpressionElement) obj;
		}
		return null;
	}

	@Override
	IAttributeFactory<IExpressionElement> getFactory(IExpressionElement element) {
		return factory;
	}
}
