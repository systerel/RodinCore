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

import static org.eventb.internal.ui.eventbeditor.htmlpage.CorePrettyPrintUtils.getDirectOrImplicitChildString;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import java.util.List;

import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class ActionsPrettyPrinter extends DefaultPrettyPrinter implements
		IElementPrettyPrinter {

	private static final String ACTION_LABEL = "actionLabel";
	private static final String IMPLICIT_ACTION_LABEL = "implicitActionLabel";
	private static final String ACTION_ASSIGNMENT = "actionAssignment";
	private static final String IMPLICIT_ACTION_ASSIGNMENT = "implicitActionAssignment";

	private static final String BEGIN_ACTION_LABEL_SEPARATOR = null;
	private static final String END_ACTION_LABEL_SEPARATOR = ":";
	private static final String BEGIN_ACTION_ASSIGNMENT_SEPARATOR = null;
	private static final String END_ACTION_ASSIGNMENT_SEPARATOR = null;

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IAction) {
			final IAction action = (IAction) elt;
			final IEvent evt = ((IEvent) parent);
			try {
				appendActionLabel(ps, action, evt);
				appendActionAssignment(ps, action, evt);
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get details for action "
								+ action.getElementName());
			}
		}
	}

	private static void appendActionLabel(IPrettyPrintStream ps, IAction act,
			IEvent evt) throws RodinDBException {
		final String label = act.getLabel();
		final String bal = getBeginActionLabel(act, evt);
		final String eal = getEndActionLabel(act, evt);
		ps.appendString(label, bal, eal, BEGIN_ACTION_LABEL_SEPARATOR,
				END_ACTION_LABEL_SEPARATOR);
	}

	private static void appendActionAssignment(IPrettyPrintStream ps,
			IAction act, IEvent evt) throws RodinDBException {
		final String assignment = wrapString(act.getAssignmentString());
		final String baa = getBeginActionAssignment(act, evt);
		final String eaa = getEndActionAssignment(act, evt);
		ps.appendString(assignment, baa, eaa,
				BEGIN_ACTION_ASSIGNMENT_SEPARATOR,
				END_ACTION_ASSIGNMENT_SEPARATOR);
	}

	private static String getBeginActionLabel(IAction act, IEvent evt) {
		return getDirectOrImplicitChildString(evt, act, //
				getHTMLBeginForCSSClass(ACTION_LABEL, HorizontalAlignment.LEFT,
						VerticalAlignement.MIDDLE), //
				getHTMLBeginForCSSClass(IMPLICIT_ACTION_LABEL,
						HorizontalAlignment.LEFT, VerticalAlignement.MIDDLE));
	}

	private static String getEndActionLabel(IAction act, IEvent evt) {
		return getDirectOrImplicitChildString(evt, act, //
				getHTMLEndForCSSClass(ACTION_LABEL, HorizontalAlignment.LEFT,
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(IMPLICIT_ACTION_LABEL,
						HorizontalAlignment.LEFT, VerticalAlignement.MIDDLE));
	}

	private static String getBeginActionAssignment(IAction act, IEvent evt) {
		return getDirectOrImplicitChildString(evt, act,//
				getHTMLBeginForCSSClass(ACTION_ASSIGNMENT,//
						HorizontalAlignment.LEFT, VerticalAlignement.MIDDLE),// 
				getHTMLBeginForCSSClass(IMPLICIT_ACTION_ASSIGNMENT, //
						HorizontalAlignment.LEFT, VerticalAlignement.MIDDLE));
	}

	private static String getEndActionAssignment(IAction act, IEvent evt) {
		return getDirectOrImplicitChildString(evt, act,//
				getHTMLEndForCSSClass(ACTION_ASSIGNMENT, //
						HorizontalAlignment.LEFT, VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(IMPLICIT_ACTION_ASSIGNMENT, //
						HorizontalAlignment.LEFT, VerticalAlignement.MIDDLE));
	}

	@Override
	public boolean appendSpecialPrefix(IInternalElement parent,
			String defaultKeyword, IPrettyPrintStream ps, boolean empty) {
		try {
			final List<IGuard> guards = UIUtils.getVisibleChildrenOfType(
					parent, IGuard.ELEMENT_TYPE);
			if (guards.size() == 0) {
				ps.appendKeyword("BEGIN");
			} else {
				ps.appendKeyword(defaultKeyword);
			}
			if (empty) {
				ps.appendLevelBegin();
				ps.appendString("skip",//
						getHTMLBeginForCSSClass(ACTION_ASSIGNMENT, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE),//
						getHTMLEndForCSSClass(ACTION_ASSIGNMENT, //
								HorizontalAlignment.LEFT, //
								VerticalAlignement.MIDDLE), //
						BEGIN_ACTION_ASSIGNMENT_SEPARATOR, //
						END_ACTION_ASSIGNMENT_SEPARATOR);
				ps.appendLevelEnd();
			}
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e,
					"Cannot append keywords for children of"
							+ parent.getElementName());
		}
		return true;
	}

}
