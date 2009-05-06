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
package org.rodinp.core.tests.version.db.basis;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.tests.version.db.IVersionRootH;

/**
 * @author Nicolas Beauger
 *
 */
public class VersionRootH extends InternalElement implements IVersionRootH {

	public VersionRootH(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IVersionRootH> getElementType() {
		return ELEMENT_TYPE;
	}

}
