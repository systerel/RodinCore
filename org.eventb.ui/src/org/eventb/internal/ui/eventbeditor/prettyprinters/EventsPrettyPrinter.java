/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
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
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.core.IEvent;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class EventsPrettyPrinter extends DefaultPrettyPrinter implements
		IElementPrettyPrinter {

	private static final String EVENT_LABEL = "eventLabel";
	private static final String EXTENDED = "extended";
	private static final String CONVERGENCE = "convergence";
	private static final String EVENT_LABEL_SEPARATOR_BEGIN = null;
	private static final String EVENT_LABEL_SEPARATOR_END = "\u2259";
	private static final String EXTENDED_SEPARATOR_BEGIN = null;
	private static final String EXTENDED_SEPARATOR_END = null;
	private static final String BEGIN_CONVERGENCE_SEPARATOR = null;
	private static final String END_CONVERGENCE_SEPARATOR = null;

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IEvent) {
			final IEvent evt = (IEvent) elt;
			try {
				appendEventLabel(ps, wrapString(evt.getLabel()));
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get the label for event "
								+ evt.getElementName());
			}
		}
	}

	private static void appendEventLabel(IPrettyPrintStream ps, String label) {
		ps.appendString(label,//
				getHTMLBeginForCSSClass(EVENT_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(EVENT_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				EVENT_LABEL_SEPARATOR_BEGIN, //
				EVENT_LABEL_SEPARATOR_END);
	}

	private static void appendExtended(IPrettyPrintStream ps) {
		ps.appendLevelBegin();
		ps.appendString("extended", //
				getHTMLBeginForCSSClass(EXTENDED, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(EXTENDED, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				EXTENDED_SEPARATOR_BEGIN, //
				EXTENDED_SEPARATOR_END);
		ps.appendLevelEnd();
	}

	private static void appendConvergence(IPrettyPrintStream ps,
			Convergence convergence) {
		String string = "ordinary";
		if (convergence == Convergence.ORDINARY) {
			string = "ordinary";
		} else if (convergence == Convergence.ANTICIPATED) {
			string = "anticipated";
		} else if (convergence == Convergence.CONVERGENT) {
			string = "convergent";
		}
		ps.appendLevelBegin();
		ps.appendString(string, //
				getHTMLBeginForCSSClass(CONVERGENCE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(CONVERGENCE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				BEGIN_CONVERGENCE_SEPARATOR, //
				END_CONVERGENCE_SEPARATOR);
		ps.appendLevelEnd();
	}

	private static void appendExtended(IEvent evt, IPrettyPrintStream ps) {
		try {
			if (evt.hasExtended() && evt.isExtended()) {
				ps.incrementLevel();
				appendExtended(ps);
				ps.decrementLevel();
			}
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetAttributeException(e);
		}
	}

	private static void appendConvergence(IEvent evt, IPrettyPrintStream ps) {
		try {
			Convergence convergence = evt.getConvergence();
			ps.appendLevelBegin();
			ps.appendKeyword("STATUS");
			appendConvergence(ps, convergence);
			ps.appendLevelEnd();
		} catch (RodinDBException e) {
			// Do nothing
		}
	}

	@Override
	public void appendBeginningDetails(IInternalElement elt,
			IPrettyPrintStream ps) {
		final IEvent evt = elt.getAncestor(IEvent.ELEMENT_TYPE);
		appendExtended(evt, ps);
		appendConvergence(evt, ps);
	}

	@Override
	public void appendEndingDetails(IInternalElement elt, IPrettyPrintStream ps) {
		ps.appendEmptyLine();
	}

}
