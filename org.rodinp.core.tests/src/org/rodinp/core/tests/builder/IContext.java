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

import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

@Deprecated
public interface IContext extends IRodinFile {

	public static final IFileElementType ELEMENT_TYPE = RodinCore
			.getFileElementType("org.rodinp.core.tests.contextF");

}