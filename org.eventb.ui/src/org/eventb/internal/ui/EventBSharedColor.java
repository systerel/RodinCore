/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
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
	
	// System colors as RGB objects.
	public static final RGB RGB_BLACK = new RGB(0, 0, 0);
	public static final RGB RGB_DARK_RED = new RGB(0x80, 0, 0);
	public static final RGB RGB_DARK_GREEN = new RGB(0, 0x80, 0);
	public static final RGB RGB_DARK_YELLOW = new RGB(0x80, 0x80, 0);
	public static final RGB RGB_DARK_BLUE = new RGB(0, 0, 0x80);
	public static final RGB RGB_DARK_MAGENTA = new RGB(0x80, 0, 0x80);
	public static final RGB RGB_DARK_CYAN = new RGB(0, 0x80, 0x80);
	public static final RGB RGB_GRAY = new RGB(0xC0, 0xC0, 0xC0);
	public static final RGB RGB_DARK_GRAY = new RGB(0x80, 0x80, 0x80);
	public static final RGB RGB_RED = new RGB(0xFF, 0, 0);
	public static final RGB RGB_GREEN = new RGB(0, 0xFF, 0);
	public static final RGB RGB_YELLOW = new RGB(0xFF, 0xFF, 0);
	public static final RGB RGB_BLUE = new RGB(0, 0, 0xFF);
	public static final RGB RGB_MAGENTA = new RGB(0xFF, 0, 0xFF);
	public static final RGB RGB_CYAN = new RGB(0, 0xFF, 0xFF);
	public static final RGB RGB_WHITE = new RGB(0xFF, 0xFF, 0xFF);

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