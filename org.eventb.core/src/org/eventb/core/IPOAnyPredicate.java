/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IUnnamedInternalElement;

/**
 * Common protocol for predicates in Event-B PO files.
 * This interface unifies access to @link org.eventb.core.IPOPredicate 
 * and @link org.eventb.core.IPOModifiedPredicate.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @author halstefa
 *
 */
public interface IPOAnyPredicate extends IUnnamedInternalElement {
	// marker interface
}
