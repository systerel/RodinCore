/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
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
 *     Systerel - refactored according to the new pretty printer mechanism
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.htmlpage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ICommentedElement;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementRelationship;
import org.eventb.ui.IImplicitChildProvider;
import org.eventb.ui.itemdescription.IElementDesc;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

/**
 * Common implementation for conversion from a Rodin file to a string (used for
 * the pretty print page of event-B editors).
 * 
 * @author htson
 */
public abstract class AstConverter {

	/**
	 * Common string values
	 */
	public static String SPACE = "";
	public static String HEADER = "";
	public static String FOOTER = "";
	public static String BEGIN_MASTER_KEYWORD = "";
	public static String BEGIN_KEYWORD_1 = "";
	public static String END_MASTER_KEYWORD = "";
	public static String END_KEYWORD_1 = "";
	public static String EMPTY_LINE = "";
	public static String BEGIN_MULTILINE = "";
	public static String END_MULTILINE = "";
	public static String BEGIN_LINE = "";
	public static String END_LINE = "";
	public static String BEGIN_COMMENT = "";
	public static String END_COMMENT = "";
	public static String BEGIN_LEVEL_0 = "";
	public static String BEGIN_LEVEL_1 = "";
	public static String BEGIN_LEVEL_2 = "";
	public static String BEGIN_LEVEL_3 = "";
	public static String END_LEVEL_0 = "";
	public static String END_LEVEL_1 = "";
	public static String END_LEVEL_2 = "";
	public static String END_LEVEL_3 = "";

	public static String BEGIN_COMMENT_SEPARATOR = "//";
	public static String END_COMMENT_SEPARATOR = null;

	// The content string of the form text
	private final PrettyPrintStream stream;

	public AstConverter() {
		stream = new PrettyPrintStream();
	}

	public String getText(IProgressMonitor monitor, IInternalElement root) {
		if (root.getUnderlyingResource() == null)
			return null;
		final StringBuilder stringBuilder = stream.getStringBuilder();
		stringBuilder.setLength(0);
		stringBuilder.append(HEADER);
		traverseRoot(monitor, root);
		stringBuilder.append(FOOTER);

		return stringBuilder.toString();
	}

	private void traverseRoot(IProgressMonitor monitor, IInternalElement e) {
		final ElementDesc desc = ElementDescRegistry.getInstance()
				.getElementDesc(e);
		stream.appendKeyword(desc.getPrefix(null));
		final IElementPrettyPrinter pp = desc.getPrettyPrinter();
		appendItemBegin(e, null, pp);
		traverse(monitor, e);
		appendItemEnd(e, desc, pp);
	}

	private void traverse(IProgressMonitor mon, IInternalElement e) {
		final ElementDesc desc = getElementDesc(e);
		for (IElementRelationship rel : desc.getChildRelationships()) {
			final List<IInternalElement> children;
			children = retrieveChildrenToProcess(rel, e);
			final ElementDesc childDesc = getElementDesc(rel.getChildType());
			final IElementPrettyPrinter pp = childDesc.getPrettyPrinter();
			if (pp == null)
				continue;

			final boolean noChildren = children.size() == 0;
			// We look if the client has overriden the default prefix
			try {
				final String prefix = desc.getPrefix(rel.getChildType());
				final boolean addedSpecialPrefix = pp.appendSpecialPrefix(e,
						prefix, stream, noChildren);
				if (noChildren && !addedSpecialPrefix) {
					continue;
				}
				// If there are children, but the client did not want to append
				// a
				// custom prefix
				if (!(noChildren) && !addedSpecialPrefix) {
					stream.appendKeyword(prefix);
				}
			} catch (Exception exception) {
				EventBEditorUtils.debugAndLogError(exception,
						"An exception occured");
			}
			for (IInternalElement in : children) {
				appendItemBegin(in, e, pp);
				stream.incrementLevel();
				// We traverse the children of this child element
				traverse(mon, in);
				appendItemEnd(in, childDesc, pp);
				stream.decrementLevel();
			}
		}
	}

	// We begin now to append this element by adding its contents and its
	// associated beginning details if there are some
	private void appendItemBegin(IInternalElement child,
			IInternalElement parent, IElementPrettyPrinter pp) {
		if (pp != null) {
			try {
				stream.appendLevelBegin();
				pp.prettyPrint(child, parent, stream);
				addComment(child);
				stream.appendLevelEnd();
				// Append the beginning details for this child element
				pp.appendBeginningDetails(child, stream);
			} catch (Exception exception) {
				EventBEditorUtils.debugAndLogError(exception,
						"An exception occured");
			}
		}
	}

	// We finish now to append this element by adding the suffix,
	// and the ending details
	private void appendItemEnd(IInternalElement child, IElementDesc desc,
			IElementPrettyPrinter pp) {
		stream.appendKeyword(desc.getChildrenSuffix());
		pp.appendEndingDetails(child, stream);
	}

	// We retrieve all children of the element elt and if the client defined a
	// way to retrieve children including implicit ones, we ask the implicit
	// child provider to get this list of visible children
	private List<IInternalElement> retrieveChildrenToProcess(
			IElementRelationship rel, IInternalElement elt) {
		final IImplicitChildProvider childProvider = rel
				.getImplicitChildProvider();
		List<IInternalElement> result = new ArrayList<IInternalElement>();
		if (childProvider != null) {
			result.addAll(childProvider.getImplicitChildren(elt));
		}
		try {
			final IInternalElementType<?> type = rel.getChildType();
			result.addAll(Arrays.asList(elt.getChildrenOfType(type)));
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "An exception occured");
		}
		return result;
	}

	// Simply appends the comment associated to the given element
	private void addComment(IInternalElement e) {
		if (e instanceof ICommentedElement)
			stream.appendComment(((ICommentedElement) e));
	}

	// Retrieves the element desc from the registry for the given element e
	private static ElementDesc getElementDesc(IInternalElement e) {
		return ElementDescRegistry.getInstance().getElementDesc(e);
	}

	// Retrieves the element desc from the registry for a given element type
	private static ElementDesc getElementDesc(IElementType<?> e) {
		return ElementDescRegistry.getInstance().getElementDesc(e);
	}

	public static String getHTMLFromCSSClassBegin(String cssClass,
			HorizontalAlignment hAlign, VerticalAlignement vAlign) {
		return "<td class=\"" + cssClass + "\" align=\"" + hAlign.getAlign()
				+ "\" valign=\"" + vAlign.getAlign() + "\">";
	}

	public static String getHTMLFromCSSClassEnd(String cssClass,
			HorizontalAlignment hAlign, VerticalAlignement vAlign) {
		return "</td>";
	}

}
