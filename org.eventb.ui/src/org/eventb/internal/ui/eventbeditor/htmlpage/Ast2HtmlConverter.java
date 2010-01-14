/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended
 *     Systerel - added implicit children for events
 *     Systerel - added theorem attribute of IDerivedPredicateElement
 *     Systerel - added guard theorem labels
 ******************************************************************************/
package org.eventb.internal.ui.eventbeditor.htmlpage;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eventb.internal.ui.BundledFileExtractor;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.osgi.framework.Bundle;


/**
 * @author Markus Gaisbauer
 */
public class Ast2HtmlConverter extends AstConverter {
	
	public Ast2HtmlConverter() {
		Bundle bundle = EventBUIPlugin.getDefault().getBundle();
		IPath path = new Path("html/style.css");
		IPath absolutePath = BundledFileExtractor.extractFile(bundle, path);
		HEADER = "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
			"<head>"+
			"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"+
			"<link type=\"text/css\" rel=\"stylesheet\" href=\"" + absolutePath.toOSString() +"\" />"+
			"</head>" +
			"<body><div class=\"main\">";
		FOOTER = "</div></body></html>";
		BEGIN_MASTER_KEYWORD = "<div class=\"masterKeyword\">";
		BEGIN_KEYWORD_1 = "<div class=\"secondaryKeyword\">";
		END_MASTER_KEYWORD = "</div>";
		END_KEYWORD_1 = "</div>";
		BEGIN_LEVEL_0 = "";
		BEGIN_LEVEL_1 = "<table class=\"level1\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
		BEGIN_LEVEL_2 = "<table class=\"level2\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
		BEGIN_LEVEL_3 = "<table class=\"level3\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
		END_LEVEL_0 = "";
		END_LEVEL_1 = "</tr></table>";
		END_LEVEL_2 = "</tr></table>";
		END_LEVEL_3 = "</tr></table>";
		EMPTY_LINE = "<br>";
		BEGIN_MULTILINE = "<td><table class=\"multiline\" cellspacing=\"0\" cellpadding=\"0\">";
		END_MULTILINE = "</table></td>";
		BEGIN_LINE = "<tr>";
		END_LINE = "</tr>";
		BEGIN_COMPONENT_NAME = "<td class=\"componentName\" align=\"left\" valign=\"center\">";
		END_COMPONENT_NAME = "</td>";
		BEGIN_COMMENT = "<td class=\"comment\" align=\"left\" valign=\"top\">";
		END_COMMENT = "</td>";
		BEGIN_VARIABLE_IDENTIFIER = "<td class=\"variableIdentifier\" align=\"left\" valign=\"center\">";
		END_VARIABLE_IDENTIFIER = "</td>";
		BEGIN_INVARIANT_LABEL = "<td class=\"invariantLabel\" align=\"left\" valign=\"center\">";
		END_INVARIANT_LABEL = "</td>";
		BEGIN_INVARIANT_PREDICATE = "<td class=\"invariantPredicate\" align=\"left\" valign=\"center\">";
		END_INVARIANT_PREDICATE = "</td>";
		BEGIN_THEOREM_LABEL = "<td class=\"theoremLabel\" align=\"left\" valign=\"center\">";
		END_THEOREM_LABEL = "</td>";
		BEGIN_EVENT_LABEL = "<td class=\"eventLabel\" align=\"left\" valign=\"center\">";
		END_EVENT_LABEL = "</td>";
		BEGIN_EXTENDED = "<td class=\"extended\" align=\"left\" valign=\"center\">";
		END_EXTENDED = "</td>";
		BEGIN_CONVERGENCE = "<td class=\"convergence\" align=\"left\" valign=\"center\">";
		END_CONVERGENCE = "</td>";
		BEGIN_ABSTRACT_EVENT_LABEL = "<td class=\"abstractEventLabel\" align=\"left\" valign=\"center\">";
		END_ABSTRACT_EVENT_LABEL = "</td>";
		BEGIN_PARAMETER_IDENTIFIER = "<td class=\"parameterIdentifier\" align=\"left\" valign=\"center\">";
		END_PARAMETER_IDENTIFIER = "</td>";
		BEGIN_IMPLICIT_PARAMETER_IDENTIFIER = "<td class=\"implicitParameterIdentifier\" align=\"left\" valign=\"center\">";
		END_IMPLICIT_PARAMETER_IDENTIFIER = "</td>";
		BEGIN_GUARD_LABEL = "<td class=\"guardLabel\" align=\"left\" valign=\"center\">";
		END_GUARD_LABEL = "</td>";
		BEGIN_GUARD_THEOREM_LABEL = "<td class=\"guardTheoremLabel\" align=\"left\" valign=\"center\">";
		END_GUARD_THEOREM_LABEL = "</td>";
		BEGIN_IMPLICIT_GUARD_LABEL = "<td class=\"implicitGuardLabel\" align=\"left\" valign=\"center\">";
		END_IMPLICIT_GUARD_LABEL = "</td>";
		BEGIN_IMPLICIT_GUARD_THEOREM_LABEL = "<td class=\"implicitGuardTheoremLabel\" align=\"left\" valign=\"center\">";
		END_IMPLICIT_GUARD_THEOREM_LABEL = "</td>";
		BEGIN_GUARD_PREDICATE = "<td class=\"guardPredicate\" align=\"left\" valign=\"center\">";
		END_GUARD_PREDICATE = "</td>";
		BEGIN_IMPLICIT_GUARD_PREDICATE = "<td class=\"implicitGuardPredicate\" align=\"left\" valign=\"center\">";
		END_IMPLICIT_GUARD_PREDICATE = "</td>";
		BEGIN_WITNESS_LABEL = "<td class=\"witnessLabel\" align=\"left\" valign=\"center\">";
		END_WITNESS_LABEL = "</td>";
		BEGIN_WITNESS_PREDICATE = "<td class=\"witnessPredicate\" align=\"left\" valign=\"center\">";
		END_WITNESS_PREDICATE = "</td>";
		BEGIN_ACTION_LABEL = "<td class=\"actionLabel\" align=\"left\" valign=\"center\">";
		BEGIN_IMPLICIT_ACTION_LABEL = "<td class=\"implicitActionLabel\" align=\"left\" valign=\"center\">";
		END_ACTION_LABEL = "</td>";
		END_IMPLICIT_ACTION_LABEL = "</td>";
		BEGIN_ACTION_ASSIGNMENT = "<td class=\"actionAssignment\" align=\"left\" valign=\"center\">";
		BEGIN_IMPLICIT_ACTION_ASSIGNMENT = "<td class=\"implicitActionAssignment\" align=\"left\" valign=\"center\">";
		END_ACTION_ASSIGNMENT = "</td>";
		END_IMPLICIT_ACTION_ASSIGNMENT = "</td>";
		BEGIN_VARIANT_EXPRESSION = "<td class=\"variantExpression\" align=\"left\" valign=\"center\">";
		END_VARIANT_EXPRESSION = "</td>";
		BEGIN_SET_IDENTIFIER = "<td class=\"setIdentifier\" align=\"left\" valign=\"center\">";
		END_SET_IDENTIFIER = "</td>";
		BEGIN_AXIOM_LABEL = "<td class=\"axiomLabel\" align=\"left\" valign=\"center\">";
		END_AXIOM_LABEL = "</td>";
		BEGIN_AXIOM_PREDICATE = "<td class=\"axiomPredicate\" align=\"left\" valign=\"center\">";
		END_AXIOM_PREDICATE = "</td>";
		BEGIN_CONSTANT_IDENTIFIER = "<td class=\"constantIdentifier\" align=\"left\" valign=\"center\">";
		END_CONSTANT_IDENTIFIER = "</td>";

		SPACE = "&nbsp;&nbsp;&nbsp;";
	}
	
	@Override
	protected String makeHyperlink(String hyperlink, String text) {
		return text;
	}

	@Override
	protected String wrapString(String text) {
		return UIUtils.HTMLWrapUp(text);
	}

}
