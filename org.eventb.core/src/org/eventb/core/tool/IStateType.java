/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tool;

import org.eventb.internal.core.tool.types.IState;

/**
 * @author Stefan Hallerstede
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @since 1.0
 */
public interface IStateType<T extends IState> {

	/**
	 * Returns the unique identifier of this state type.
	 * 
	 * @return the unique identifier of this state type
	 */
	String getId();

	/**
	 * Returns the human-readable name of this state type.
	 * 
	 * @return the name of this state type
	 */
	String getName();

	/**
	 * Returns a string representation of this state type, that is its unique
	 * identifier.
	 * 
	 * @return the unique identifier of this state type
	 */
	@Override
	String toString();

}
