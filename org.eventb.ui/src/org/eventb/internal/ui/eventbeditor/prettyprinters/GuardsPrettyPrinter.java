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

import static org.eventb.internal.ui.eventbeditor.htmlpage.CorePrettyPrintUtils.getDirectOrImplicitChildString;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.isTheorem;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import java.util.List;

import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IParameter;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class GuardsPrettyPrinter extends DefaultPrettyPrinter {
	private static final String GUARD_LABEL = "guardLabel";
	private static final String GUARD_THEOREM_LABEL = "guardTheoremLabel";
	private static final String IMPLICIT_GUARD_LABEL = "implicitGuardLabel";
	private static final String IMPLICIT_GUARD_THEOREM_LABEL = "implicitGuardTheoremLabel";
	private static final String GUARD_PREDICATE = "guardPredicate";
	private static final String IMPLICIT_GUARD_PREDICATE = "implicitGuardPredicate";

	private static final String GUARD_LABEL_SEPARATOR_BEGIN = null;
	private static final String GUARD_LABEL_SEPARATOR_END = ":";
	private static final String GUARD_PREDICATE_SEPARATOR_BEGIN = null;
	private static final String GUARD_PREDICATE_SEPARATOR_END = null;

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream sb) {
		if (elt instanceof IGuard) {
			final IGuard guard = (IGuard) elt;
			try {
				final boolean isTheorem = isTheorem(guard);
				final IEvent evt = (IEvent) parent;
				appendGuard(sb, guard, evt, isTheorem);
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get details for guard "
								+ guard.getElementName());
			}
		}
	}

	private static void appendGuard(IPrettyPrintStream ps, IGuard guard,
			IEvent evt, boolean isTheorem) throws RodinDBException {
		appendGuardLabel(ps, guard, evt, isTheorem);
		appendGuardPredicate(ps, guard, evt, isTheorem);
	}

	private static void appendGuardPredicate(IPrettyPrintStream ps,
			IGuard guard, IEvent evt, boolean isTheorem)
			throws RodinDBException {
		final String predicate = wrapString(guard.getPredicateString());
		final String bgp = getBeginGuardPredicate(guard, evt);
		final String egp = getEndGuardPredicate(guard, evt);
		ps.appendString(predicate, //
				bgp, egp, //
				GUARD_PREDICATE_SEPARATOR_BEGIN, //
				GUARD_PREDICATE_SEPARATOR_END);
	}

	private static void appendGuardLabel(IPrettyPrintStream ps, IGuard guard,
			IEvent evt, boolean isTheorem) throws RodinDBException {
		final String label = wrapString(guard.getLabel());
		final String bgl;
		final String egl;
		if (isTheorem) {
			bgl = getBeginGuardTheoremLabel(guard, evt);
			egl = getEndGuardTheoremLabel(guard, evt);
		} else {
			bgl = getBeginGuardLabel(guard, evt);
			egl = getEndGuardLabel(guard, evt);
		}
		ps.appendString(label, //
				bgl, egl, //
				GUARD_LABEL_SEPARATOR_BEGIN, //
				GUARD_LABEL_SEPARATOR_END);
	}

	private static String getBeginGuardTheoremLabel(IGuard grd, IEvent evt) {
		return getDirectOrImplicitChildString(evt, grd, //
				getHTMLBeginForCSSClass(GUARD_THEOREM_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLBeginForCSSClass(IMPLICIT_GUARD_THEOREM_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE));
	}

	private static String getEndGuardTheoremLabel(IGuard grd, IEvent evt) {
		return getDirectOrImplicitChildString(evt, grd, //
				getHTMLEndForCSSClass(GUARD_THEOREM_LABEL,
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(IMPLICIT_GUARD_THEOREM_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE));
	}

	private static String getBeginGuardLabel(IGuard grd, IEvent evt) {
		return getDirectOrImplicitChildString(evt, grd, //
				getHTMLBeginForCSSClass(GUARD_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLBeginForCSSClass(IMPLICIT_GUARD_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE));
	}

	private static String getEndGuardLabel(IGuard grd, IEvent evt) {
		return getDirectOrImplicitChildString(evt, grd, //
				getHTMLEndForCSSClass(GUARD_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(IMPLICIT_GUARD_LABEL, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE));
	}

	private static String getBeginGuardPredicate(IGuard grd, IEvent evt) {
		return getDirectOrImplicitChildString(evt, grd, //
				getHTMLBeginForCSSClass(GUARD_PREDICATE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLBeginForCSSClass(IMPLICIT_GUARD_PREDICATE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE));
	}

	private static String getEndGuardPredicate(IGuard grd, IEvent evt) {
		return getDirectOrImplicitChildString(evt, grd, //
				getHTMLEndForCSSClass(GUARD_PREDICATE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(IMPLICIT_GUARD_PREDICATE, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE));
	}

	@Override
	public boolean appendSpecialPrefix(IInternalElement parent,
			String defaultKeyword, IPrettyPrintStream ps, boolean empty) {
		if (empty) {
			return false;
		}
		try {
			final List<IParameter> params = UIUtils.getVisibleChildrenOfType(
					parent, IParameter.ELEMENT_TYPE);
			if (params.size() == 0) {
				ps.appendKeyword("WHEN");
			} else {
				ps.appendKeyword(defaultKeyword);
			}
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get all guards of "
					+ parent.getElementName());
		}
		return true;
	}

}
