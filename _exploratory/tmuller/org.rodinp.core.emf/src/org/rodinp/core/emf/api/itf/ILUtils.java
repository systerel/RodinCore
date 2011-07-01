/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.api.itf;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;

/**
 * A set of utility methods for 'IL' elements.
 * 
 * @author "Thomas Muller"
 */
public class ILUtils {
	
	public static IInternalElement getNextSibling(ILElement parent,
			IInternalElement element) throws RodinDBException {
		return SynchroUtils.getNextSibling((LightElement) parent, element);
	}

}
