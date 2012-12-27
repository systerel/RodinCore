/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.pm;

/**
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IUserSupportInformation {

	/**
	 * @since 2.3
	 */
	public static final int ERROR_PRIORITY = 3;
	
	public static final int MAX_PRIORITY = 2;

	public static final int MIN_PRIORITY = 1;

	public abstract Object getInformation();

	public abstract int getPriority();

}