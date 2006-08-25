/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 * Workaround utility class for a bug in the database that
 * stops newly created elements from being seen immediately afterwards.
 * Saving the file that contains it solves the problem but is
 * slow.
 */
public final class WA {

	private WA() {
		// do not create instances
	}
	
	public static void save(IRodinElement element) throws RodinDBException {
		IOpenable openable = element.getOpenable();
		openable.save(null, true);
	}
	
}
