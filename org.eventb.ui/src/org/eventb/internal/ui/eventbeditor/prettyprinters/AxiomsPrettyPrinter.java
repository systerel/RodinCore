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

import org.eventb.core.IAxiom;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class AxiomsPrettyPrinter extends DefaultPrettyPrinter {

	private static String THEOREM_LABEL = "theoremLabel";
	private static String AXIOM_LABEL = "axiomLabel";
	private static String AXIOM_PREDICATE = "axiomPredicate";

	private static final String AXIOM_LABEL_SEPARATOR_BEGIN = null;
	private static final String AXIOM_LABEL_SEPARATOR_END = ":";
	private static final String AXIOM_PREDICATE_SEPARATOR_BEGIN = null;
	private static final String AXIOM_PREDICATE_SEPARATOR_END = null;

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IAxiom) {
			final IAxiom axm = (IAxiom) elt;
			try {
				final String label = wrapString(axm.getLabel());
				final boolean isTheorem = isTheorem(axm);
				appendAxiomLabel(ps, label, isTheorem);
				appendAxiomPredicate(ps, wrapString(axm.getPredicateString()));
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get details for axiom " + axm.getElementName());
			}
		}
	}

	private static void appendAxiomLabel(IPrettyPrintStream ps, String label,
			boolean isTheorem) {
		final String cssClass;
		if (isTheorem) {
			cssClass = THEOREM_LABEL;
		} else {
			cssClass = AXIOM_LABEL;
		}
		ps.appendString(label, //
				getHTMLBeginForCSSClass(cssClass,//
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(cssClass, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				AXIOM_LABEL_SEPARATOR_BEGIN, //
				AXIOM_LABEL_SEPARATOR_END);

	}

	private static void appendAxiomPredicate(IPrettyPrintStream ps,
			String predicate) {
		ps.appendString(predicate, //
				getHTMLBeginForCSSClass(AXIOM_PREDICATE, //
						HorizontalAlignment.LEFT,//
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(AXIOM_PREDICATE, //
						HorizontalAlignment.LEFT,//
						VerticalAlignement.MIDDLE), //
				AXIOM_PREDICATE_SEPARATOR_BEGIN, //
				AXIOM_PREDICATE_SEPARATOR_END);
	}

}
