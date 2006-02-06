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
import org.eventb.eventBKeyboard.translators.IEventBKeyboardTranslator;

/**
 * @author htson
 * The translator for text symbols
 */
public class EventBKeyboardTextTranslator
	implements IEventBKeyboardTranslator
{

	private static final String [] textCombo =
		{"NAT1", "NAT", "POW1", "POW", "INTER", "INT", "UNION", "or",
		"not", "true", "false", "\u2124ER", "circ"};
	
	private static final String [] textComboTranslation =
		{"\u2115\u0031", "\u2115", "\u2119\u0031", "\u2119", "\u22c2", "\u2124", "\u22c3", "\u2228",
		"\u00ac", "\u22a4", "\u22a5", "\u22c2", "\u2218"};
	
	/**
     * Testing if a character is a text character 
     * @param c: a character
     * @return true if the character is one of the text characters (i.e. 'A' to 'Z', 'a' to 'z', etc.) 
     *        false otherwise 
     */
    private boolean isTextCharacter(char c) {
    	if (c <= 'Z' && c >= 'A') return true;
    	if (c <= 'z' && c >= 'a') return true;
    	if (c <= '9' && c >= '0') return true;
    	if (c == '_') return true;
    	return false;
    }

    /**
     * Attempting to find and translate a text combo. The combo is translated if there are white space
     * at the beginning and the end of the combo.
     *  
     * @param widget: The Text Widget
     * @param text:   The actual string contains in the "widget"
     * @param currentPos  Current position in the string "text"
     * 
     * @return true if there is a string which is translated into mathematical expression
     *        false otherwise
     */
     private boolean translateTextCombo(Text widget) {
    	boolean isTranslated = false;
    	String text = widget.getText();
		int currentPos = widget.getCaretPosition();
		
    	for (int i = 0; i < textCombo.length; i++) {
			String test = textCombo[i];
			
			// Only consider the related sub-string
			int beginIndex = (currentPos - (test.length()+2) < 0) ? 0 : currentPos - (test.length()+2);
			int endIndex = (currentPos + (test.length() + 2) > text.length()) ? text.length() : currentPos + (test.length() + 2);
		
			String subString = text.substring(beginIndex, endIndex);
		
			//System.out.println("Substring: |" + subString + "|");
		
			int index = subString.indexOf(test);
		
			if (index != -1 ) {
				if ((index + test.length()) < subString.length()) {
					if (!isTextCharacter(subString.charAt(index+test.length()))) {
						if (index != 0) {
							if (!isTextCharacter(subString.charAt(index-1))) {
								//System.out.println("Found: " + test + " at " + (beginIndex+index));
								widget.setSelection(beginIndex + index, beginIndex+index+test.length());
								widget.insert(textComboTranslation[i]);
								isTranslated = true;
							}
						}
						else {
							if (beginIndex == 0) {
								//System.out.println("Found: " + test + " at " + (beginIndex+index));
								widget.setSelection(beginIndex + index, beginIndex+index+test.length());
								widget.insert(textComboTranslation[i]);
								isTranslated = true;
							}
						}
					}
				}
			}

			if (isTranslated) {
				if (currentPos <= beginIndex+index) {widget.setSelection(currentPos);}
		
				else if (beginIndex+index + test.length() < currentPos) widget.setSelection(beginIndex+index+textComboTranslation[i].length()+1);
				return true;
			}
		
		}
    	return false;
    }

	public void translate(Text widget) {
		boolean isTranslated;
		
		isTranslated = translateTextCombo(widget);
		if (isTranslated) translateTextCombo(widget);
	}
     
     
     
}
