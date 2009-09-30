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

import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is the common interface for the element modifiers used in the
 *         Event-B Table/Tree.
 *
 * @deprecated This interface is not used anymore.
 * @since 1.0
 */
@Deprecated
public interface IElementModifier {

	/**
	 * This is the method that is used to modify the element
	 * <p>
	 * 
	 * @param element
	 *            the Rodin element in concern.
	 * @param text
	 *            the string that is used to modify the element.
	 * @throws RodinDBException
	 *             throws the Rodin DB Exception if there is some problems when
	 *             modifying the element.
	 */
	void modify(IRodinElement element, String text) throws RodinDBException;

}
