/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/
package org.eventb.internal.ui.eventbeditor.htmlpage;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.internal.ui.BundledFileExtractor;
import org.eventb.ui.EventBUIPlugin;
import org.osgi.framework.Bundle;


/**
 * @author Markus Gaisbauer
 */
public class Ast2HtmlConverter extends AstConverter {
	
	public Ast2HtmlConverter() {
		Bundle bundle = EventBUIPlugin.getDefault().getBundle();
		IPath path = new Path("src/org/eventb/internal/ui/eventbeditor/htmlpage/style.css");
		IPath absolutePath = BundledFileExtractor.extractFile(bundle, path);
		HEADER = "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
			"<head>"+
			"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"+
			"<link type=\"text/css\" rel=\"stylesheet\" href=\"" + absolutePath.toOSString() +"\" />"+
			"</head>" +
			"<body><div class=\"main\">";
		FOOTER = "</div></body></html>";
		BEGIN_KEYWORD_0 = "<div class=\"keyword0\">";
		BEGIN_KEYWORD_1 = "<div class=\"keyword1\">";
		BEGIN_KEYWORD_2 = "<div class=\"keyword2\">";
		BEGIN_KEYWORD_3 = "<div class=\"keyword3\">";
		END_KEYWORD_0 = "</div>";
		END_KEYWORD_1 = "</div>";
		END_KEYWORD_2 = "</div>";
		END_KEYWORD_3 = "</div>";
		BEGIN_LEVEL_0 = "";
		BEGIN_LEVEL_1 = "<table class=\"level1\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
		BEGIN_LEVEL_2 = "<table class=\"level2\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
		BEGIN_LEVEL_3 = "<table class=\"level3\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
		END_LEVEL_0 = "";
		END_LEVEL_1 = "</tr></table>";
		END_LEVEL_2 = "</tr></table>";
		END_LEVEL_3 = "</tr></table>";
		EMPTY_LINE = "<br>";
		BEGIN_ATTRIBUTE = "<td class=\"attribute\" align=\"left\" valign=\"top\">";
		END_ATTRIBUTE = "</td>";
		BEGIN_MULTILINE = "<table class=\"multiline\" cellspacing=\"0\" cellpadding=\"0\">";
		END_MULTILINE = "</table>";
		BEGIN_LINE = "<tr><td>";
		END_LINE = "</td></tr>";
		SPACE = "&nbsp;&nbsp;&nbsp;";
	}
	
	@Override
	protected String makeHyperlink(String link, String text) {
		return text;
	}

}
