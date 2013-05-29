/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.eventbeditor.tests.basis;

import org.eventb.ui.tests.EventBUITestsPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

public interface ITestChildElement extends IInternalElement {

	final IInternalElementType<ITestChildElement> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBUITestsPlugin.PLUGIN_ID
					+ ".childTestElement");

}
