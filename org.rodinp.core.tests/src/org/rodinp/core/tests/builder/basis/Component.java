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
package org.rodinp.core.tests.builder.basis;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.RodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
@Deprecated
public abstract class Component extends RodinFile {

	protected Component(IFile file, IRodinElement parent) {
		super(file, parent);
	}

}
