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
package org.eventb.internal.ui.eventbeditor.prettyprint;

import org.eventb.internal.ui.UIUtils;

/**
 * @author Markus Gaisbauer
 * @deprecated replaced by
 *             {@link org.eventb.internal.ui.eventbeditor.htmlpage.Ast2HtmlConverter}
 */
@Deprecated
public class Ast2HtmlConverter extends AstConverter {
	
	public Ast2HtmlConverter() {
		BOLD = "<b>";
		END_BOLD = "</b>";
		BEGIN_LEVEL_0 = "<li style=\"text\">";
		BEGIN_LEVEL_1 = "<li style=\"text\" bindent = \"20\">";
		BEGIN_LEVEL_2 = "<li style=\"text\" bindent = \"40\">";
		BEGIN_LEVEL_3 = "<li style=\"text\" bindent = \"60\">";
		END_LEVEL = "</li>";
		EMPTY_LINE = "<li style=\"text\" value=\"\"></li>";
	}
	
	@Override
	protected String makeHyperlink(String link, String text) {
		return UIUtils.makeHyperlink(link, text);
	}

}
