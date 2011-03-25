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

package fr.systerel.editor.editors;

import org.eclipse.swt.graphics.RGB;

public interface IRodinColorConstant {
	RGB COMMENT = new RGB(63, 127, 95);
	RGB COMMENT_HEADER = new RGB(103, 187, 155);
	RGB SECTION = new RGB(0,200,0);
	RGB LABEL = new RGB(0, 0, 0);
	RGB IDENTIFIER = new RGB(0, 0, 0);
	RGB CONTENT = new RGB(0, 0, 192);
	RGB DEFAULT = new RGB(0, 0, 0);
	
	RGB COMMENT_DEBUG_BG = new RGB(63, 163, 83);
	RGB COMMENT_HEADER_DEBUG_BG = new RGB(63,100, 100);
	RGB LABEL_DEBUG_BG = new RGB(240, 255, 110);
	RGB IDENTIFIER_DEBUG_BG = new RGB(200, 200, 200);
	RGB CONTENT_DEBUG_BG = new RGB(150, 150, 192);
	RGB DEFAULT_DEBUG_BG  = new RGB(170, 70, 70);
	RGB SECTION_DEBUG_BG = new RGB(170, 170, 70);
	RGB KEYWORD_DEBUG_BG = new RGB(190, 50, 0);
	
}
