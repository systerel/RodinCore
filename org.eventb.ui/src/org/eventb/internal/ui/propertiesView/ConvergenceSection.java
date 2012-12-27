/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - used IAttributeFactory
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import org.eventb.internal.ui.eventbeditor.manipulation.ConvergenceAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;

public class ConvergenceSection extends CComboSection {

	@Override
	String getLabel() {
		return "Conv.";
	}

	@Override
	protected IAttributeManipulation createFactory() {
		return new ConvergenceAttributeManipulation();
	}

	@Override
	public int getColumn() {
		return 2;
	}
}
