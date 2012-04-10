/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - fixed bug 3392038: no need to filter "other" empty attributes 
 *******************************************************************************/
package fr.systerel.editor.internal.documentModel;

import static fr.systerel.editor.internal.documentModel.DocumentElementUtils.getAttributeDescs;
import static fr.systerel.editor.internal.documentModel.DocumentElementUtils.getElementDesc;
import static fr.systerel.editor.internal.documentModel.RodinTextStream.MIN_LEVEL;
import static fr.systerel.editor.internal.documentModel.RodinTextStream.getTabs;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.BOLD_IMPLICIT_LABEL_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.BOLD_LABEL_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.COMMENT_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.CONTENT_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.IDENTIFIER_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.IMPLICIT_COMMENT_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.IMPLICIT_CONTENT_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.IMPLICIT_IDENTIFIER_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.IMPLICIT_LABEL_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.LABEL_TYPE;
import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IEvent;
import org.eventb.core.IExpressionElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IAttributeDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementRelationship;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.presentation.RodinConfiguration.ContentType;

/**
 * Creates the text for a given root. The intervals and editor elements are
 * built too and registered with the document mapper.
 */
public class RodinTextGenerator {

	private final DocumentMapper mapper;
	private static final int ONE_TABS_INDENT = 1;

	private RodinTextStream stream;

	public RodinTextGenerator(DocumentMapper mapper) {
		this.mapper = mapper;

	}

	/**
	 * Creates the text for the document and creates the intervals.
	 * 
	 * @param inputRoot
	 *            The machine or context that is displayed in the document.
	 */
	public String createText(ILElement inputRoot) {
		mapper.reinitialize();
		stream = new RodinTextStream();
		traverseRoot(null, inputRoot);
		mapper.processIntervals(stream.getRegions());
		return stream.getText();
	}

	private void traverseRoot(IProgressMonitor monitor, ILElement e) {
		final IInternalElement element = e.getElement();
		final IElementDesc desc = ElementDescRegistry.getInstance()
				.getElementDesc(element);
		stream.addSectionRegion(desc.getPrefix());
		stream.incrementIndentation(ONE_TABS_INDENT);
		stream.appendPresentationTabs(e, ONE_TABS_INDENT);
		final TextAlignator sizer = new TextAlignator();
		stream.appendAlignementTab(e);
		stream.addLabelRegion(element.getElementName(), e);
		sizer.append(element.getElementName());
		processOtherAttributes(e, sizer);
		stream.appendAlignementTab(e);
		processCommentedElement(e, sizer);
		stream.appendLineSeparator();
		stream.decrementIndentation(ONE_TABS_INDENT);
		traverse(monitor, e);
	}

	private void traverse(IProgressMonitor mon, ILElement e) {
		final IElementDesc desc = getElementDesc(e);
		if (e.getElementType().equals(IEvent.ELEMENT_TYPE)) {
			stream.incrementIndentation(ONE_TABS_INDENT);
		}
		for (IElementRelationship rel : desc.getChildRelationships()) {
			final List<ILElement> c = retrieveChildrenToProcess(rel, e);
			final IElementDesc childDesc = getElementDesc(rel.getChildType());
			if (childDesc == null)
				continue;
			int start = -1;
			final boolean noChildren = c.isEmpty();
			if (noChildren) {
				continue;
			}
			start = stream.getLength();
			if (stream.getLevel() < MIN_LEVEL) {
				stream.addSectionRegion(childDesc.getPrefix());
			} else {
				stream.addKeywordRegion(childDesc.getPrefix());
			}
			stream.incrementIndentation(ONE_TABS_INDENT);
			for (ILElement in : c) {
				stream.appendLeftPresentationTabs(in);
				processElement(in);
				traverse(mon, in);
				if (in.getElementType() == IEvent.ELEMENT_TYPE) {
					stream.appendLineSeparator();
				}
			}
			stream.decrementIndentation(ONE_TABS_INDENT);
			final int length = stream.getLength() - start;
			if (start != -1 && !noChildren && stream.getLevel() <= MIN_LEVEL) {
				mapper.addEditorSection(rel.getChildType(), start, length);
				start = -1;
			}
		}
		final String childrenSuffix = desc.getChildrenSuffix();
		if (!childrenSuffix.isEmpty())
			stream.addKeywordRegion(childrenSuffix);
		if (e.getElementType().equals(IEvent.ELEMENT_TYPE)) {
			stream.decrementIndentation(ONE_TABS_INDENT);
		}
	}

	/**
	 * We retrieve all children of the element
	 * <code>elt<code> and if the client defined a
	 * way to retrieve children including implicit ones, we ask the implicit
	 * child providers to get this list of visible children
	 */
	private List<ILElement> retrieveChildrenToProcess(IElementRelationship rel,
			ILElement elt) {
		final ArrayList<ILElement> result = new ArrayList<ILElement>();
		final IInternalElementType<?> type = rel.getChildType();
		result.addAll(elt.getChildrenOfType(type));
		return result;
	}

	/**
	 * Processes the attributes other than comments.
	 */
	private void processOtherAttributes(ILElement element, TextAlignator sizer) {
		int i = 0;
		final IInternalElement rElement = element.getElement();
		boolean addTab = false;
		for (IAttributeDesc d : getAttributeDescs(element.getElementType())) {
			addTab = true;
			final String space = " ";
			stream.addPresentationRegion(space, element);
			sizer.append(space);
			String value = "";
			try {
				if (i == 0)
					stream.addPresentationRegion(space, element);
				sizer.append(space);
				final IAttributeManipulation manipulation = d.getManipulation();
				if (!manipulation.hasValue(rElement, null)
						&& manipulation.getPossibleValues(rElement, null) != null) {
					value = "--undefined--";
				} else {
					value = manipulation.getValue(rElement, null);
				}
				final String prefix = d.getPrefix();
				if (!prefix.isEmpty()) {
					stream.addPresentationRegion(prefix, element);
					sizer.append(prefix);
				}
				stream.addAttributeRegion(value, element, manipulation,
						d.getAttributeType());
				sizer.append(value);
				final String suffix = d.getSuffix();
				if (!suffix.isEmpty()) {
					stream.addPresentationRegion(suffix, element);
					sizer.append(suffix);
				}
				i++;
			} catch (RodinDBException e) {
				value = "failure while loading";
				e.printStackTrace();
			}
		}
		if (addTab) {
			sizer.append(RodinTextStream.TAB);
			stream.appendAlignementTab(element);
		}
	}

	private void processStringEventBAttribute(ILElement element,
			IAttributeType.String type, ContentType t, boolean multiline,
			String alignmentStr) {
		final String attribute = element.getAttribute(type);
		final String value = (attribute != null) ? attribute : "";
		stream.addElementRegion(value, element, t, multiline, alignmentStr);
	}

	private void processCommentedElement(ILElement element, TextAlignator sizer) {
		stream.addCommentHeaderRegion(element);
		sizer.appendCheckForMultiline("");
		processStringEventBAttribute(element, COMMENT_ATTRIBUTE,
				getContentType(element, IMPLICIT_COMMENT_TYPE, COMMENT_TYPE),
				true,
				getTabs(stream.getLevel()) + sizer.getAllAlignementString());
	}

	private void processPredicateElement(ILElement element, TextAlignator sizer) {
		processFormula(element, PREDICATE_ATTRIBUTE, sizer);
	}

	private void processAssignmentElement(ILElement element, TextAlignator sizer) {
		processFormula(element, ASSIGNMENT_ATTRIBUTE, sizer);
	}

	private void processExpressionElement(ILElement element, TextAlignator sizer) {
		processFormula(element, EXPRESSION_ATTRIBUTE, sizer);
	}

	private void processFormula(ILElement element,
			IAttributeType.String attrType, TextAlignator sizer) {
		sizer.appendCheckForMultiline(element.getAttribute(attrType));
		processStringEventBAttribute(element, attrType,
				getContentType(element, IMPLICIT_CONTENT_TYPE, CONTENT_TYPE),
				true,
				getTabs(stream.getLevel()) + sizer.getFirstAlignementString());
		stream.appendAlignementTab(element);
		sizer.append(RodinTextStream.TAB);
	}

	private void processIdentifierElement(ILElement element, TextAlignator sizer) {
		final String identifierAttribute = element
				.getAttribute(IDENTIFIER_ATTRIBUTE);
		processStringEventBAttribute(
				element,
				IDENTIFIER_ATTRIBUTE,
				getContentType(element, IMPLICIT_IDENTIFIER_TYPE,
						IDENTIFIER_TYPE), false, "");
		sizer.append(identifierAttribute);
		if (!element.getAttributes().isEmpty()) {
			final String s = RodinTextStream.getTabs(1);
			stream.addPresentationRegion(s, element);
			sizer.append(s);
		}
	}

	private void processLabeledElement(ILElement element, TextAlignator sizer) {
		final String labelAttribute = element.getAttribute(LABEL_ATTRIBUTE);
		processStringEventBAttribute(element, LABEL_ATTRIBUTE,
				getLabelType(element), false, "");
		sizer.append(labelAttribute);
		if (labelAttribute != null) {
			final String s = ":" + RodinTextStream.getTabs(1);
			stream.addPresentationRegion(s, element);
			sizer.append(s);
		}
		if (!element.getChildren().isEmpty()
				&& element.getAttributes().isEmpty()) {
			stream.appendLineSeparator();
		}
	}

	private ContentType getLabelType(ILElement element) {
		if ((element.getAttribute(ASSIGNMENT_ATTRIBUTE) == null)
				&& (element.getAttribute(PREDICATE_ATTRIBUTE) == null))
			return getContentType(element, BOLD_IMPLICIT_LABEL_TYPE,
					BOLD_LABEL_TYPE);
		return getContentType(element, IMPLICIT_LABEL_TYPE, LABEL_TYPE);
	}

	private void processElement(ILElement element) {
		final IRodinElement rodinElement = (IRodinElement) element.getElement();
		if (!rodinElement.exists()) {
			return;
		}
		final TextAlignator sizer = new TextAlignator();
		if (rodinElement instanceof ILabeledElement) {
			processLabeledElement(element, sizer);
		} else if (rodinElement instanceof IIdentifierElement) {
			processIdentifierElement(element, sizer);
		}
		if (rodinElement instanceof IExpressionElement) {
			processExpressionElement(element, sizer);
		}
		if (rodinElement instanceof IPredicateElement) {
			processPredicateElement(element, sizer);
		}
		if (rodinElement instanceof IAssignmentElement) {
			processAssignmentElement(element, sizer);
		}
		// display attributes at the end
		processOtherAttributes(element, sizer);
		// display the comments at the end for compacity
		if (rodinElement instanceof ICommentedElement) {
			processCommentedElement(element, sizer);
		}
		stream.appendLineSeparator();
	}

	private static ContentType getContentType(ILElement element,
			ContentType implicitType, ContentType type) {
		if (element.isImplicit()) {
			return implicitType;
		}
		return type;
	}

	private static class TextAlignator {

		final private StringBuilder b;
		private boolean singleLine = true;

		public TextAlignator() {
			this.b = new StringBuilder();
		}

		public void appendCheckForMultiline(String str) {
			singleLine = b.toString().split("(\r\n)|(\r)|(\n)").length <= 1;
			append(str);
		}

		public void append(String str) {
			b.append(str);
		}

		public String getAllAlignementString() {
			final String[] split = b.toString().split("(\r\n)|(\r)|(\n)");
			final String r = split[split.length - 1].replaceAll("[^\t]", " ");
			if (singleLine)
				return r;
			return getFirstAlignementString() + r;
		}

		public String getFirstAlignementString() {
			final String string = b.toString();
			final String[] split = string.split("\t");
			final String r = split[0].replaceAll("[^\t]", " ");
			return r.concat("\t");
		}
	}

}
