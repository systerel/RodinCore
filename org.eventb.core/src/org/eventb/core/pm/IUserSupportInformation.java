/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.core.pm;

/**
 * @since 1.0
 */
public interface IUserSupportInformation {

	public static final int MAX_PRIORITY = 2;

	public static final int MIN_PRIORITY = 1;

	public abstract Object getInformation();

	public abstract int getPriority();

}