/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
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

	/**
	 * @deprecated Logging each time GetPossibleValues is called produces
	 *             unnecessary log entries. GetPossibleValues is allowed to
	 *             return <code>null</code>, this is part of the API and needs
	 *             not be logged whatsoever.
	 */
	@Deprecated
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
