/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui;

import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         A common interface for proving label of an object
 *         
 * @deprecated This interface is not used anymore.
 * @since 1.0
 */
@Deprecated
public interface IElementLabelProvider {

	/**
	 * Getting the label of an object. The result must NOT be <code>null</code>
	 * <p>
	 * 
	 * @param obj
	 *            an object
	 * @return the label of the input object
	 */
	public String getLabel(Object obj) throws RodinDBException;

}
