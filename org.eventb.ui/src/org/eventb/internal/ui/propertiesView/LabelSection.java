/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.LabelAttributeManipulation;

public class LabelSection extends TextSection {

	private final IAttributeManipulation manipulation = new LabelAttributeManipulation();

	@Override
	String getLabel() {
		return "Label";
	}

	@Override
	protected IAttributeManipulation getFactory() {
		return manipulation;
	}

}
