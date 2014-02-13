/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tool;


/**
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IModuleType<T extends IModule> {

	/**
	 * Returns the unique identifier of this module type.
	 * 
	 * @return the unique identifier of this module type
	 */
	String getId();

	/**
	 * Returns the human-readable name of this module type.
	 * 
	 * @return the name of this module type
	 */
	String getName();

	/**
	 * Returns a string representation of this module type, that is its unique
	 * identifier.
	 * 
	 * @return the unique identifier of this module type
	 */
	@Override
	String toString();

}
