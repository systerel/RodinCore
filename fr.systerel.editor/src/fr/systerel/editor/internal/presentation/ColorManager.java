/*******************************************************************************
 * Copyright (c) 2008, 2015 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorManager {
	
	private static ColorManager fColorManager = new ColorManager(); 

	
	private ColorManager() {
		// singleton
	}
	
	public static ColorManager getDefault() {
		return fColorManager;
	}
	
	protected Map<RGB, Color> fColorTable = new HashMap<RGB, Color>(10);

	public void dispose() {
		Iterator<Color> e = fColorTable.values().iterator();
		while (e.hasNext())
			 e.next().dispose();
	}
	
	public Color getColor(RGB rgb) {
		Color color = fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}

	public Color getImplicitColor(Color color) {
		final float[] hsb = color.getRGB().getHSB();
		final RGB rgb = new RGB(hsb[0],//
				Math.max(0.1f, hsb[1] * 0.5f), // reduce saturation
				Math.min(0.9f, hsb[2] + .2f) // augment brightness
		);
		return getColor(rgb);
	}
	
}
