/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.location;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.location.IInternalLocation;

public class InternalLocation extends Location<IInternalElement> implements IInternalLocation {


	public InternalLocation(IInternalElement element) {
		super(element);
	}

}
