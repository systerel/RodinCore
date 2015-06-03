/*******************************************************************************
 * Copyright (c) 2008, 2015 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.documentModel;

import static fr.systerel.editor.internal.documentModel.DocumentElementUtils.getElementDesc;
import static fr.systerel.editor.internal.documentModel.DocumentElementUtils.getManipulation;
import static fr.systerel.editor.internal.documentModel.DocumentElementUtils.getNonBasicAttributeDescs;
import static fr.systerel.editor.internal.documentModel.RodinTextStream.MIN_LEVEL;
import static fr.systerel.editor.internal.documentModel.RodinTextStream.getTabs;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.BOLD_LABEL_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.COMMENT_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.HANDLE_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.IDENTIFIER_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.IMPLICIT_BOLD_LABEL_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.IMPLICIT_COMMENT_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.IMPLICIT_HANDLE_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.IMPLICIT_IDENTIFIER_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.IMPLICIT_LABEL_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.LABEL_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.getContentType;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.getFormulaContentType;
import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IEvent;
import org.eventb.core.IExpressionElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.eventb.ui.itemdescription.IAttributeDesc;
import org.eventb.ui.itemdescription.IElementDesc;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.presentation.RodinConfiguration.ContentType;

/**
 * Creates the text for a given root. The intervals and editor elements are
 * built too and registered with the document mapper.
 */
public class RodinTextGenerator {

	private final DocumentMapper mapper;
	private static final String WHITESPACE = " ";
	
	private RodinTextStream stream;

	public RodinTextGenerator(DocumentMapper mapper) {
		this.mapper = mapper;

	}

	/**
	 * Creates the text for the document and creates the intervals.
	 * 
	 * @param inputRoot
	 *            The machine or context that is displayed in the document.
	 * @param showImplicitElements
	 *            boolean value telling whether implicit elements should be
	 *            displayed or not
	 */
	public String createText(ILElement inputRoot, boolean showImplicitElements) {
		mapper.reinitialize();
		stream = new RodinTextStream();
		traverseRoot(null, inputRoot, showImplicitElements);
		mapper.processIntervals(stream.getRegions());
		return stream.getText();
	}

	private void traverseRoot(IProgressMonitor monitor, ILElement e,
			boolean showImplicitElements) {
		final IElementDesc desc = getElementDesc(e);
		stream.addSectionRegion(desc.getPrefix(null), e);
		stream.incrementIndentation();
		stream.appendPresentationTabs(e, MIN_LEVEL);
		final TextAlignator sizer = new TextAlignator();
		stream.appendAlignementTab(e);
		final IInternalElement element = e.getElement();
		stream.addLabelRegion(element.getElementName(), e);
		sizer.append(getRootNameSpace(element));
		processOtherAttributes(e, sizer);
		stream.appendAlignementTab(e);
		sizer.append(getTabs(1));
		processCommentedElement(e, sizer);
		stream.appendLineSeparator(e);
		stream.decrementIndentation();
		traverse(monitor, e, showImplicitElements);
	}

	// there is no ":" after the root name so we remove it from the sizer
	private String getRootNameSpace(final IInternalElement element) {
		final String elementName = element.getElementName();
		final StringBuilder b = new StringBuilder();
		for (int i = 0; i < elementName.length() - 1; i++) {
			b.append(" ");
		}
		return b.toString();
	}

	private void traverse(IProgressMonitor mon, ILElement e,
			boolean showImplicitElements) {
		final IElementDesc desc = getElementDesc(e);
		if (e.getElementType().equals(IEvent.ELEMENT_TYPE)) {
			stream.incrementIndentation();
		}
		for (IInternalElementType<?> childType : desc.getChildTypes()) {
			final IElementDesc childDesc = getElementDesc(childType);
			if (childDesc == null)
				continue;
			int start = -1;
			final List<ILElement> c = e.getChildrenOfType(childType);
			if (noChildToDisplay(c, showImplicitElements)) {
				continue;
			}
			start = stream.getLength();
			if (stream.getLevel() < MIN_LEVEL) {
				stream.addSectionRegion(desc.getPrefix(childType), e);
			} else {
				stream.addKeywordRegion(desc.getPrefix(childType), e);
			}
			for (ILElement in : c) {
				if (in.isImplicit() && !showImplicitElements) {
					continue; // do not show implicit elements
				}
				stream.appendLeftPresentationTabs(in);
				processElement(in);
				traverse(mon, in, showImplicitElements);
				if (in.getElementType() == IEvent.ELEMENT_TYPE) {
					stream.appendLineSeparator(e);
				}
			}
			final int length = stream.getLength() - start -1;
			if (start != -1 && stream.getLevel() <= MIN_LEVEL) {
				mapper.addEditorSection(childType, start, length);
				start = -1;
			}
		}
		final String childrenSuffix = desc.getChildrenSuffix();
		if (!childrenSuffix.isEmpty())
			stream.addKeywordRegion(childrenSuffix, e);
		if (e.getElementType().equals(IEvent.ELEMENT_TYPE)) {
			stream.decrementIndentation();
		}
	}

	private boolean noChildToDisplay(List<ILElement> elements,
			boolean showImplicitElements) {
		if (elements.isEmpty()) {
			return true;
		} // else
		if (showImplicitElements) {
			return false;
		} // else
		for (ILElement element : elements) {
			if (!element.isImplicit()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Processes the attributes other than comments.
	 */
	private void processOtherAttributes(ILElement element, TextAlignator sizer) {
		final IInternalElement rElement = element.getElement();
		for (IAttributeDesc d : getNonBasicAttributeDescs(element)) {
			stream.addPresentationRegion(WHITESPACE, element);
			sizer.append(WHITESPACE);
			String value = "";
			try {
				final IAttributeManipulation manipulation = d.getManipulation();
				if (d.getAttributeType() instanceof IAttributeType.String
						&& !manipulation.hasValue(rElement, null)
						&& manipulation.getPossibleValues(rElement, null) != null) {
					value = "--undefined--";
				} else {
					value = manipulation.getValue(rElement, null);
				}
				final String prefix = d.getPrefix();
				if (!prefix.isEmpty()) {
					stream.addPresentationRegion(prefix + WHITESPACE, element);
					sizer.append(prefix + WHITESPACE);
				}
				final boolean multiline = d.isMultiLine();
				final String fas = sizer.getAllAlignementString();
				stream.addAttributeRegion(value, element, manipulation,
						d.getAttributeType(), multiline,
						getTabs(stream.getLevel()) + fas);
				sizer.appendCheckForMultiline(value);
				final String suffix = d.getSuffix();
				if (!suffix.isEmpty()) {
					stream.addPresentationRegion(WHITESPACE + suffix, element);
					sizer.append(WHITESPACE + suffix);
				}
			} catch (CoreException e) {
				value = "failure while loading";
				e.printStackTrace();
			}
		}
		stream.addPresentationRegion(WHITESPACE, element);
		sizer.append(WHITESPACE);
	}

	private void processStringEventBAttribute(ILElement elem,
			IAttributeType.String attrType, ContentType ct, boolean multiLine,
			String alignStr) {
		final String attribute = elem.getAttribute(attrType);
		final String value = (attribute != null) ? attribute : "";
		final IAttributeManipulation manip = getManipulation(elem, attrType);
		if (manip == null) {
			return;
		}
		stream.addElementRegion(value, elem, ct, manip, multiLine, alignStr);
	}

	private void processCommentedElement(ILElement element, TextAlignator sizer) {
		stream.addCommentHeaderRegion(element);
		sizer.appendCheckForMultiline("");
		processStringEventBAttribute(element, COMMENT_ATTRIBUTE,
				getContentType(element.isImplicit() ? IMPLICIT_COMMENT_TYPE
						: COMMENT_TYPE), true, getTabs(stream.getLevel())
						+ sizer.getAllAlignementString());
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
				getFormulaContentType(attrType, element.isImplicit()), true,
				getTabs(stream.getLevel()) + sizer.getFirstAlignementString());
	}

	private void processIdentifierElement(ILElement element, TextAlignator sizer) {
		final String identifierAttribute = element
				.getAttribute(IDENTIFIER_ATTRIBUTE);
		processStringEventBAttribute(element, IDENTIFIER_ATTRIBUTE,
				getContentType(element.isImplicit() ? IMPLICIT_IDENTIFIER_TYPE
						: IDENTIFIER_TYPE), false, "");
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
			stream.appendLineSeparator(element);
		}
	}

	private ContentType getLabelType(ILElement element) {
		final boolean isImplicit = element.isImplicit();
		if ((element.getAttribute(ASSIGNMENT_ATTRIBUTE) == null)
				&& (element.getAttribute(PREDICATE_ATTRIBUTE) == null)) {
			return getContentType(isImplicit ? IMPLICIT_BOLD_LABEL_TYPE
					: BOLD_LABEL_TYPE);
		}
		return getContentType(isImplicit ? IMPLICIT_LABEL_TYPE : LABEL_TYPE);
	}

	private void processElement(ILElement element) {
		final IRodinElement rodinElement = element.getElement();
		if (!rodinElement.exists()) {
			return;
		}
		final TextAlignator sizer = new TextAlignator();
		processElementHandle(element, sizer);
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
		stream.appendLineSeparator(element);
	}

	private void processElementHandle(ILElement element, TextAlignator sizer) {
		final boolean implicit = element.isImplicit();
		final ContentType contentType = getContentType(implicit ? IMPLICIT_HANDLE_TYPE
				: HANDLE_TYPE);
		stream.appendElementHandle(element,
				contentType);
		sizer.append(RodinTextStream.ELEMENT_PREFIX);
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
			final StringBuilder result = new StringBuilder();
			for (int i = 0; i < Math.min(split.length, 2); i ++) {
				result.append(split[i].replaceAll("[^\t]", " "));
				result.append("\t");
			}
			return result.toString();
		}
	}

}
