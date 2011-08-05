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
 *     Systerel - replaced inherited by extended, event variable by parameter
 *     Systerel - separation of file and root element
 *     Systerel - added implicit children for events
 *     Systerel - added theorem attribute of IDerivedPredicateElement
 * 	   Systerel - fixed bug #2884774 : display guards marked as theorems
 * 	   Systerel - fixed bug #2936324 : Extends clauses in pretty print
 *     Systerel - Extracted and refactored from AstConverter
 ******************************************************************************/
package org.eventb.internal.ui.eventbeditor.prettyprinters;

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.core.IWitness;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class WitnessesPrettyPrinter extends DefaultPrettyPrinter implements
		IElementPrettyPrinter {

	private static final String WITNESS_LABEL = "witnessLabel";
	private static final String WITNESS_PREDICATE = "witnessPredicate";
	private static final String WITNESS_PREDICATE_SEPARATOR_BEGIN = null;
	private static final String WITNESS_PREDICATE_SEPARATOR_END = null;
	private static final String BEGIN_WITNESS_LABEL_SEPARATOR = null;
	private static final String END_WITNESS_LABEL_SEPARATOR = ":";

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IWitness) {
			final IWitness witness = (IWitness) elt;
			try {
				final String label = wrapString(witness.getLabel());
				appendWitnessLabel(ps, label);
				appendWitnessPredicate(ps, wrapString(witness
						.getPredicateString()));
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get details for guard "
								+ witness.getElementName());
			}
		}
	}

	private static void appendWitnessLabel(IPrettyPrintStream ps, String label) {
		ps.appendString(label, //
				getHTMLBeginForCSSClass(WITNESS_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(WITNESS_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				BEGIN_WITNESS_LABEL_SEPARATOR, //
				END_WITNESS_LABEL_SEPARATOR);
	}

	private static void appendWitnessPredicate(IPrettyPrintStream ps,
			String predicate) {
		ps.appendString(predicate,//
				getHTMLBeginForCSSClass(WITNESS_PREDICATE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE),//
				getHTMLEndForCSSClass(WITNESS_PREDICATE,
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE),//
				WITNESS_PREDICATE_SEPARATOR_BEGIN, //
				WITNESS_PREDICATE_SEPARATOR_END);
	}

}
