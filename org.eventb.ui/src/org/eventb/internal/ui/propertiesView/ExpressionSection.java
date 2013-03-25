/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
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

import org.eclipse.swt.SWT;
import org.eventb.internal.ui.eventbeditor.manipulation.ExpressionAttributeManipulation;
import org.eventb.ui.manipulation.IAttributeManipulation;

public class ExpressionSection extends TextSection {

	private final IAttributeManipulation factory = new ExpressionAttributeManipulation();
	
	@Override
	String getLabel() {
		return "Expression";
	}

	@Override
	void setStyle() {
		style = SWT.MULTI;
		math = true;
	}

	@Override
	protected IAttributeManipulation getFactory() {
		return factory;
	}

}
