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

package org.eventb.internal.ui;

import org.eclipse.swt.graphics.Color;

public interface IEventBSharedColor {

	public static final String DIRTY_STATE = "Dirty state";

	public static final String BOX_BORDER = "Box border";
	
	/**
	 * Get the color corresponding to the input key. The color are managed by
	 * the internal shared registry. Clients should NOT dispose the color.
	 * 
	 * @param key
	 *            strng key for getting color
	 * @return the color corresponding to the input key.
	 */
	public Color getColor(String key);

}