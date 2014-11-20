/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended, event variable by parameter
 *     Systerel - separation of file and root element
 *     Systerel - added implicit children for events
 *     Systerel - added theorem attribute of IDerivedPredicateElement
 *     Systerel - fixed bug #2884774 : display guards marked as theorems
 *     Systerel - fixed bug #2936324 : Extends clauses in pretty print
 *     Systerel - Extracted and refactored from AstConverter
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.prettyprinters;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.isTheorem;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.core.IInvariant;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class InvariantsPrettyPrinter extends DefaultPrettyPrinter {

	private static String THEOREM_LABEL = "theoremLabel";
	private static String INVARIANT_LABEL = "invariantLabel";
	private static String INVARIANT_PREDICATE = "invariantPredicate";
	private static String INVARIANT_LABEL_SEPARATOR_BEGIN = null;
	private static String INVARIANT_LABEL_SEPARATOR_END = ":";
	private static String INVARIANT_PREDICATE_SEPARATOR_BEGIN = null;
	private static String INVARIANT_PREDICATE_SEPARATOR_END = null;

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IInvariant) {
			final IInvariant inv = (IInvariant) elt;
			try {
				final String label = wrapString(inv.getLabel());
				final boolean isTheorem = isTheorem(inv);
				appendInvariantLabel(ps, label, isTheorem);
				appendInvariantPredicate(ps, wrapString(inv
						.getPredicateString()));
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get details for invariant "
								+ inv.getElementName());
			}
		}
	}

	private static void appendInvariantLabel(IPrettyPrintStream ps, String label,
			boolean isTheorem) {
		final String cssClass;
		if (isTheorem) {
			cssClass = THEOREM_LABEL;
		} else {
			cssClass = INVARIANT_LABEL;
		}
		ps.appendString(label, //
				getHTMLBeginForCSSClass(cssClass, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(cssClass, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				INVARIANT_LABEL_SEPARATOR_BEGIN, //
				INVARIANT_LABEL_SEPARATOR_END);
	}

	private static void appendInvariantPredicate(IPrettyPrintStream ps,
			String predicate) {
		ps.appendString(predicate, //
				getHTMLBeginForCSSClass(INVARIANT_PREDICATE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(INVARIANT_PREDICATE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				INVARIANT_PREDICATE_SEPARATOR_BEGIN, //
				INVARIANT_PREDICATE_SEPARATOR_END);
	}

}
