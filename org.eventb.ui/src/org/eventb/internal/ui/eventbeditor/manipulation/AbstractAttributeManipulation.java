/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IAttributeType;

public abstract class AbstractAttributeManipulation implements
		IAttributeManipulation {

	protected void logCantGetPossibleValues(IAttributeType attribute) {
		UIUtils.log(null,
				"The method GetPossibleValues cannot be called for attribute "
						+ attribute);
	}

	protected void logNotPossibleValues(IAttributeType attribute, String value) {
		UIUtils.log(null, value + " is not a possible value for attribute "
				+ attribute);
	}

	protected void logCantRemove(IAttributeType attribute) {
		UIUtils.log(null, "Attribute " + attribute + " cannot be removed");
	}

}
