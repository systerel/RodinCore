/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core;

/**
 * Common protocol for Rodin item types (internal element and attribute)
 * contributed through an extension point.
 * 
 * @author Laurent Voisin
 */
public interface IContributedItemType {

	/**
	 * Returns the unique identifier of this item type.
	 * 
	 * @return the unique identifier of this item type
	 */
	String getId();

	/**
	 * Returns the human-readable name of this item type.
	 * 
	 * @return the name of this item type
	 */
	String getName();

}
