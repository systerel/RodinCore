/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.itemdescription;

import org.rodinp.core.IInternalElementType;

/**
 * Common protocol for accessing the UI description registry.
 * 
 * @since 3.0
 */
public interface IElementDescRegistry {

	/**
	 * Returns the UI description for the given internal element type.
	 * <p>
	 * If no description was contributed for the given element type, a dummy
	 * description is returned. Users shall therefore check for the validity of
	 * the returned description using the API method
	 * {@link IElementDesc#isValid()}.
	 * </p>
	 * 
	 * @return the descriptor for the given internal element type, maybe invalid
	 */
	IElementDesc getElementDesc(IInternalElementType<?> type);

	/**
	 * Returns the array of valid registered element UI descriptions.
	 * 
	 * @return the array of valid registered element UI descriptions
	 */
	IElementDesc[] getElementDescs();

}
