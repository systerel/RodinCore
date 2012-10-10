/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

public interface ISCContextRoot extends ISCProvableRoot {

	public static final IInternalElementType<ISCContextRoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(PLUGIN_ID + ".scContext");

	IContextRoot getUncheckedVersion();

}
