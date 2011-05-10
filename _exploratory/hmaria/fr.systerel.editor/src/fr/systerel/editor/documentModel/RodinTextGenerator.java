/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.documentModel;

import static fr.systerel.editor.documentModel.DocumentElementUtils.getAttributeDescs;
import static fr.systerel.editor.documentModel.DocumentElementUtils.getElementDesc;
import static fr.systerel.editor.documentModel.RodinTextStream.MIN_LEVEL;
import static fr.systerel.editor.presentation.RodinConfiguration.BOLD_IMPLICIT_LABEL_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.BOLD_LABEL_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.COMMENT_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.CONTENT_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.IDENTIFIER_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.IMPLICIT_COMMENT_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.IMPLICIT_CONTENT_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.IMPLICIT_IDENTIFIER_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.IMPLICIT_LABEL_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.LABEL_TYPE;
import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IEvent;
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

import fr.systerel.editor.presentation.RodinConfiguration.ContentType;

/**
 * Creates the text for a given root. The intervals and editor elements are
 * built too and registered with the document mapper.
 */
public class RodinTextGenerator {

	private final DocumentMapper documentMapper;
	private static final int TWO_TABS_INDENT = 2;

	private ArrayList<Position> foldingRegions = new ArrayList<Position>();
	private RodinTextStream stream;

	public RodinTextGenerator(DocumentMapper documentMapper) {
		this.documentMapper = documentMapper;
	}

	/**
	 * Creates the text for the document and creates the intervals.
	 * 
	 * @param inputRoot
	 *            The machine or context that is displayed in the document.
	 */
	public String createText(ILElement inputRoot) {
		documentMapper.reinitialize();
		stream = new RodinTextStream();
		traverseRoot(null, inputRoot);
		documentMapper.processIntervals(stream.getRegions());
		return stream.getText();
	}

	@SuppressWarnings({ "restriction" })
	private void traverseRoot(IProgressMonitor monitor, ILElement e) {
		final IInternalElement element = e.getElement();
		final IElementDesc desc = ElementDescRegistry.getInstance()
				.getElementDesc(element);
		stream.addSectionRegion(desc.getPrefix());
		stream.incrementIndentation(TWO_TABS_INDENT);
		stream.appendPresentationTabs(e, TWO_TABS_INDENT);
		processCommentedElement(e, true, 0);
		stream.appendLineSeparator();
		stream.appendLeftPresentationTabs(e);
		stream.addLabelRegion(element.getElementName(), e);
		processOtherAttributes(e);
		stream.decrementIndentation(TWO_TABS_INDENT);
		traverse(monitor, e);
	}

	@SuppressWarnings("restriction")
	private void traverse(IProgressMonitor mon, ILElement e) {
		final IElementDesc desc = getElementDesc(e);
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
			stream.incrementIndentation(TWO_TABS_INDENT);
			for (ILElement in : c) {
				stream.appendLeftPresentationTabs(in);
				processElement(in);
				traverse(mon, in);
				if (in.getElementType() == IEvent.ELEMENT_TYPE) {
					stream.appendLineSeparator();
					stream.appendLineSeparator();
				}
			}
			stream.decrementIndentation(TWO_TABS_INDENT);
			final int length = stream.getLength() - start;
			if (start != -1 && !noChildren) {
				documentMapper.addEditorSection(rel.getChildType(), start, length);
				start = -1;
			}
		}
	}

	/**
	 * We retrieve all children of the element
	 * <code>elt<code> and if the client defined a
	 * way to retrieve children including implicit ones, we ask the implicit
	 * child providers to get this list of visible children
	 */
	@SuppressWarnings("restriction")
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
	@SuppressWarnings("restriction")
	private void processOtherAttributes(ILElement element) {
		int i = 0;
		final IInternalElement rElement = element.getElement();
		for (IAttributeDesc d : getAttributeDescs(element.getElementType())) {
			String value = "";
			try {
				if (i == 0)
					stream.addPresentationRegion(" ", element);
				final IAttributeManipulation manipulation = d.getManipulation();
				if (!manipulation.hasValue(rElement, null)
						&& manipulation.getPossibleValues(rElement, null) != null) {
					value = "--undefined--";
				} else {
					value = manipulation.getValue(rElement, null);					
				}
				if (!value.isEmpty()) {
					final String prefix = d.getPrefix();
					if (!prefix.isEmpty()) {
						stream.addPresentationRegion(prefix, element);
					}
					stream.addAttributeRegion(value, element, manipulation,
							d.getAttributeType());
					final String suffix = d.getSuffix();
					if (!suffix.isEmpty()) {
						stream.addPresentationRegion(suffix, element);
					}
					i++;
				}
			} catch (RodinDBException e) {
				value = "failure while loading";
				e.printStackTrace();
			}
			stream.addPresentationRegion(" ", element);
		}
		stream.appendLineSeparator();
	}

	private void processStringEventBAttribute(ILElement element,
			IAttributeType.String type, ContentType t, boolean multiline,
			int additionnalTabs) {
		final String attribute = element.getAttribute(type);
		final String value = (attribute != null) ? attribute : "";
		stream.addElementRegion(value, element, t, multiline, additionnalTabs);
	}

	private void processCommentedElement(ILElement element, boolean appendTabs,
			int additionnalTabs) {
		stream.addCommentHeaderRegion(element, appendTabs);
		processStringEventBAttribute(element, COMMENT_ATTRIBUTE,
				getContentType(element, IMPLICIT_COMMENT_TYPE, COMMENT_TYPE),
				true, additionnalTabs);
		if (!appendTabs)
			stream.appendLineSeparator();
	}

	private void processPredicateElement(ILElement element) {
		processPredAssElement(element, PREDICATE_ATTRIBUTE);
	}

	private void processAssignmentElement(ILElement element) {
		processPredAssElement(element, ASSIGNMENT_ATTRIBUTE);
	}

	private void processPredAssElement(ILElement element,
			IAttributeType.String attrType) {
		processStringEventBAttribute(element, attrType,
				getContentType(element, IMPLICIT_CONTENT_TYPE, CONTENT_TYPE),
				true, TWO_TABS_INDENT);
	}

	private void processIdentifierElement(ILElement element) {
		processStringEventBAttribute(
				element,
				IDENTIFIER_ATTRIBUTE,
				getContentType(element, IMPLICIT_IDENTIFIER_TYPE,
						IDENTIFIER_TYPE), false, 0);
	}

	private void processLabeledElement(ILElement element) {
		stream.appendLeftPresentationTabs(element);
		final String labelAttribute = element.getAttribute(LABEL_ATTRIBUTE);
		processStringEventBAttribute(element, LABEL_ATTRIBUTE,
				getLabelType(element), false, 0);
		if (labelAttribute != null) {
			stream.addPresentationRegion(":\t", element);
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
		boolean commentToProcess = true;
		if (rodinElement instanceof ILabeledElement) {
			if (rodinElement instanceof ICommentedElement) {
				processCommentedElement(element, false, 0);
				commentToProcess = false;
			}
			processLabeledElement(element);
		} else if (rodinElement instanceof IIdentifierElement) {
			processIdentifierElement(element);
		}
		if (rodinElement instanceof IPredicateElement) {
			processPredicateElement(element);
		}
		if (rodinElement instanceof IAssignmentElement) {
			processAssignmentElement(element);

		}
		if (rodinElement instanceof ICommentedElement && commentToProcess) {
			processCommentedElement(element, true, 1);
		}
		// display attributes at the end
		processOtherAttributes(element);
	}

	private static ContentType getContentType(ILElement element,
			ContentType implicitType, ContentType type) {
		if (element.isImplicit()) {
			return implicitType;
		}
		return type;
	}

	public Position[] getFoldingRegions() {
		return foldingRegions.toArray(new Position[foldingRegions.size()]);
	}

}
