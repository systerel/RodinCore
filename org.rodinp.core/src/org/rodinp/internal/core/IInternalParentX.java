/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

/**
 * Extended interface for internal use.
 * 
 * @author Laurent Voisin
 */
public interface IInternalParentX extends IInternalParent {

	String getAttributeRawValue(String attrName) throws RodinDBException;

}
