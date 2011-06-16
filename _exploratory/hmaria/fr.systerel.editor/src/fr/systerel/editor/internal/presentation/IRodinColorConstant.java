/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.editor.internal.presentation;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eventb.internal.ui.EventBSharedColor;

public interface IRodinColorConstant {

	// TODO make preferences

	RGB COMMENT = new RGB(12000, 127, 95);
	RGB IMPLICIT_COMMENT = new RGB(95, 150, 95);

	RGB CONTENT = new RGB(0, 0, 192);
	RGB IMPLICIT_CONTENT = new RGB(105, 105, 200);

	RGB LABEL = new RGB(0, 50, 70);
	RGB IMPLICIT_LABEL = new RGB(130, 130, 130);

	RGB IDENTIFIER = new RGB(0, 0, 0);
	RGB IMPLICIT_IDENTIFIER = new RGB(130, 130, 130);

	RGB ATTRIBUTE = new RGB(80, 80, 100);
	RGB IMPLICIT_ATTRIBUTE = new RGB(160, 160, 190);

	RGB COMMENT_HEADER = new RGB(103, 187, 155); // The comment header §
	RGB SECTION = new RGB(0, 50, 70);
	RGB DEFAULT = new RGB(0, 0, 0);

	/**
	 * Backgrounds
	 */
	RGB COMMENT_DEBUG_BG = new RGB(63, 163, 83);
	RGB COMMENT_HEADER_DEBUG_BG = new RGB(63, 100, 100);
	RGB LABEL_DEBUG_BG = new RGB(240, 255, 110);
	RGB IDENTIFIER_DEBUG_BG = new RGB(200, 200, 200);
	RGB CONTENT_DEBUG_BG = new RGB(150, 150, 192);
	RGB DEFAULT_DEBUG_BG = new RGB(170, 70, 70);
	RGB SECTION_DEBUG_BG = new RGB(170, 170, 70);
	RGB KEYWORD_DEBUG_BG = new RGB(190, 50, 0);

	RGB BACKGROUND = new RGB(250, 250, 250);
	Color BG_COLOR = EventBSharedColor.getColor(BACKGROUND);
}
