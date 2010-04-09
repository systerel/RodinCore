/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prettyprint;

import org.eventb.core.IDerivedPredicateElement;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.htmlpage.AstConverter;
import org.eventb.internal.ui.eventbeditor.htmlpage.CorePrettyPrintUtils;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;

/**
 * Static methods that contributors should use to ease the implementation of
 * their contribution pretty printers.
 * 
 * @since 1.2
 */
public class PrettyPrintUtils {

	/**
	 * Display an error and log message.
	 * 
	 * @param e
	 *            the error we catch
	 * @param msg
	 *            the error message to be displayed
	 */
	public static void debugAndLogError(Exception e, String msg) {
		EventBEditorUtils.debugAndLogError(e, msg);
	}

	/**
	 * Method to get the HTML string that should be placed before an element is
	 * pretty printed and allowing an element to be pretty printed with a
	 * defined style and layout. The cssClass given as parameter is the
	 * corresponding CSS class that the contributor defined for an given element
	 * type. This method should be used inside the <code>appendString()</code>
	 * method.
	 * 
	 * @param cssClass
	 *            the CSS class used to pretty print the element
	 * @param hAlign
	 *            the horizontal alignment of the following pretty print
	 * @param vAlign
	 *            the vertical alignment of the following pretty print
	 * @return a string to be appended before the element is pretty printed
	 */
	public static String getHTMLBeginForCSSClass(String cssClass,
			HorizontalAlignment hAlign, VerticalAlignement vAlign) {
		return AstConverter.getHTMLFromCSSClassBegin(cssClass, hAlign, vAlign);
	}

	/**
	 * Method to get the HTML string that should be placed after an element is
	 * pretty printed and allowing an element to be pretty printed with a
	 * defined style and layout. The cssClass given as parameter is the
	 * corresponding CSS class that the contributor defined for an given element
	 * type. This method should be used inside the <code>appendString()</code>
	 * method.
	 * 
	 * @param cssClass
	 *            the CSS class used to pretty print the element
	 * @param hAlign
	 *            the horizontal alignment of the following pretty print
	 * @param vAlign
	 *            the vertical alignment of the following pretty print
	 * @return a string to be appended after the element is pretty printed
	 */
	public static String getHTMLEndForCSSClass(String cssClass,
			HorizontalAlignment hAlign, VerticalAlignement vAlign) {
		return AstConverter.getHTMLFromCSSClassEnd(cssClass, hAlign, vAlign);
	}

	/**
	 * Returns a boolean value indicating is the element is set as a theorem or
	 * not.
	 * 
	 * @param elem
	 *            the element we want to check
	 * @return <code>true</code> if the given element is set as a theorem,
	 *         <code>false</code> otherwise
	 */
	public static boolean isTheorem(IDerivedPredicateElement elem) {
		return CorePrettyPrintUtils.isTheorem(elem);
	}

	/**
	 * Converts a string input to HTML format by replacing special characters
	 * (&, <, >, space, tab).
	 * 
	 * @param text
	 *            the text to wrap up
	 * @return the HTML corresponding string
	 */
	public static String wrapString(String text) {
		return UIUtils.HTMLWrapUp(text);
	}

}
