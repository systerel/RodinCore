/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Provides Colors management facilities. This class is used in order to prevent
 * from allocating the same Color several times, by taking advantage of the
 * ColorManager.
 * 
 * @author Nicolas Beauger
 * 
 */
public class EventBSharedColor {
	
	private EventBSharedColor() {
		// Functional class: Private constructor
	}
	
	/**
	 * Returns a Color from a given RGB. This method prevents from allocating
	 * the same Color several times.
	 * 
	 * @param rgb
	 *            RGB color.
	 * @return the converted color.
	 */
	public static Color getColor(RGB rgb) {
		return ColorManager.getDefault().getColor(rgb);
	}

	/**
	 * Returns a System Color. This method uses system colors, which prevents
	 * from allocating the same Color several times.
	 * 
	 * @param id
	 *            the integer identifier of the system color.
	 * @return the converted color.
	 * 
	 * @see Display#getSystemColor(int)
	 */
	public static Color getSystemColor(int id) {
		return Display.getCurrent().getSystemColor(id);
	}
}