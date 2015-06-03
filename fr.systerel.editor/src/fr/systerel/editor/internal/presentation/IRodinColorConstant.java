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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public interface IRodinColorConstant {

	/**
	 * Backgrounds
	 */
	RGB LABEL_DEBUG_BG = new RGB(240, 255, 110);
	RGB IDENTIFIER_DEBUG_BG = new RGB(200, 200, 200);
	RGB CONTENT_DEBUG_BG = new RGB(150, 150, 192);
	RGB HANDLE_DEBUG_BG = new RGB(150, 150, 192);
	RGB DEFAULT_DEBUG_BG = new RGB(170, 70, 70);
	RGB PRESENTATION_DEBUG_BG = new RGB(190, 190, 0);
	RGB BACKGROUND = new RGB(250, 250, 250);
	Color BG_COLOR = ColorManager.getDefault().getColor(BACKGROUND);
}
