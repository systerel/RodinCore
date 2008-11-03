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

import org.eventb.core.IAssignmentElement;
import org.eventb.internal.ui.eventbeditor.editpage.AssignmentAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;

public class AssignmentLabelManipulation extends
		AbstractInternalElementLabelManipulation<IAssignmentElement> {

	private static final IAttributeFactory<IAssignmentElement> factory = new AssignmentAttributeFactory();

	@Override
	IAssignmentElement getElement(Object obj) {
		if (obj instanceof IAssignmentElement) {
			return (IAssignmentElement) obj;
		}
		return null;
	}

	@Override
	IAttributeFactory<IAssignmentElement> getFactory(IAssignmentElement element) {
		return factory;
	}

}
