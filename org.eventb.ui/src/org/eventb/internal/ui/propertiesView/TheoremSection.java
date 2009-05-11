/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.TheoremAttributeManipulation;

public class TheoremSection extends ToggleSection {

	private static final AbstractBooleanManipulation manipulation = new TheoremAttributeManipulation();

	@Override
	protected AbstractBooleanManipulation getManipulation() {
		return manipulation;
	}

	@Override
	protected String getLabel() {
		return "Theorem";
	}

}
