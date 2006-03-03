/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.eventBKeyboard.internal.translators;

import org.eclipse.swt.widgets.Text;
import org.eventb.eventBKeyboard.IEventBKeyboardTranslator;

/**
 * @author htson
 * The translator for mathematical symbols
 */
public class EventBKeyboardMathTranslator
	implements IEventBKeyboardTranslator
{

	private static final String [] mathCombo =
	{"<=>", "\u2264>", "<\u21d2", "=>", "&", "!", "#", "/=", "\u00f7=", "<=", ">=",
	"\u00f7<<:", "<<:", "/\u2282", "\u00f7<:", "<:", "/\u2286", "\u00f7:", "/\u2208",
	"<<->>", "<<\u2212>>", "\ue100>", "<\ue101", "<<->", "<<\u2212>", "<\u2194", "<->>", "<\u2212>>", "\u2194>", "<->", "<\u2212>",
	">->>", ">\u2212>>", "\u21a3>", ">\u21a0", "+->", "+\u2212>", "-\u2212>", "\u2212\u2212>", ">+>", ">->", ">\u2212>", "+>>", "->>", "\u2212>>",
	"\u2223->", "\u2223\u2212>", "{}", "/\u2216", "\u00f7\\", "\\\u00f7", "\u2216/", "\\", "\u2217*", "*\u2217",
	"<+", "><", "\u2223|", "|\u2223", "~", "<<|", "<|", "<\u25c1", "\u2223>", "\u2223>>", "\u25b7>",
	"%", ".\u00b7", "\u00b7.", ".", "-", "*", "/",
	":=", "\u2208=", "\u2208:", "\u2208|", ":", "|"};

	private static final String [] mathComboTranslation =
	{"\u21d4", "\u21d4", "\u21d4", "\u21d2", "\u2227", "\u2200", "\u2203", "\u2260", "\u2260", "\u2264", "\u2265",
	"\u2284", "\u2282", "\u2284", "\u2288", "\u2286", "\u2288", "\u2209", "\u2209",
	"\ue102", "\ue102", "\ue102", "\ue102", "\ue100", "\ue100", "\ue100", "\ue101", "\ue101", "\ue101", "\u2194", "\u2194",
	"\u2916", "\u2916", "\u2916", "\u2916", "\u21f8", "\u21f8", "\u2192", "\u2192", "\u2914", "\u21a3", "\u21a3", "\u2900", "\u21a0", "\u21a0",
	"\u21a6", "\u21a6", "\u2205", "\u2229", "\u2229", "\u222a", "\u222a", "\u2216", "\u00d7", "\u00d7",
	"\ue103", "\u2297", "\u2225", "\u2225", "\u223c", "\u2a64", "\u25c1", "\u2a64", "\u25b7", "\u2a65", "\u2a65",
	"\u03bb", "\u2025", "\u2025", "\u00b7", "\u2212", "\u2217", "\u00f7",
	"\u2254", "\u2254", ":\u2208", ":\u2223", "\u2208", "\u2223"};
 
	/**
	 * Attempting to find and translate a mathematical combo. The combo is translated once it is
	 * found in the string.
	 *  
	 * @param widget: The Text Widget
	 * @param text:   The actual string contains in the "widget"
	 * @param currentPos  Current position in the string "text"
	 * 
	 * @return true if there is a mathematical string which is translated into mathematical expression
	 *        false otherwise
	 */
	public void translate(Text widget) {
		boolean isTranslated = false;
		//if (EventBKeyboardPlugin.debug) System.out.println("Position Before: " + currentPos);
		String text = widget.getText();
		int currentPos = widget.getCaretPosition();
		
		for (int i = 0; i < mathCombo.length; i++) {
			String test = mathCombo[i];
			int beginIndex = (currentPos - test.length() < 0) ? 0 : currentPos - test.length();
			int endIndex = (currentPos + (test.length() - 1) > text.length()) ? text.length() : currentPos + (test.length() - 1);
	
			String subString = text.substring(beginIndex, endIndex);
	
			//	if (EventBKeyboardPlugin.debug) System.out.println("Testing for " + test);
			//	if (EventBKeyboardPlugin.debug) System.out.println("Substring: |" + subString + "|");
	
			int index = subString.indexOf(test);
	
			if (index != -1) {
				widget.setSelection(beginIndex + index, beginIndex+index+test.length());
				widget.insert(mathComboTranslation[i]);
				isTranslated = true;
			}

			if (isTranslated) {
				if (currentPos <= beginIndex+index) {widget.setSelection(currentPos);}
	
				else if (beginIndex+index + test.length() < currentPos) widget.setSelection(beginIndex+index+mathComboTranslation[i].length()+1);
				//	if (EventBKeyboardPlugin.debug) System.out.println("Position After: " + widget.getCaretPosition());
				return;
			}
	
		}
		//if (EventBKeyboardPlugin.debug) System.out.println("Position After: " + widget.getCaretPosition());
		return;
	}

     
}
