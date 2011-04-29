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
import static fr.systerel.editor.documentModel.DocumentElementUtils.getManipulation;
import static fr.systerel.editor.documentModel.RodinTextGeneratorUtils.MIN_LEVEL;
import static fr.systerel.editor.documentModel.RodinTextGeneratorUtils.NONE;
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
	private final int TWO_TABS_INDENT = 2;

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
		processAllIntervals();
		return stream.getText();
	}
	
	public void processAllIntervals() {
		for (RegionInfo r : stream.getIntervalInfos()) {
			documentMapper.processInterval(r.getStart(), r.getLength(),
					r.getElement(), r.getType(), r.getManipulation(),
					r.isMultiline(), r.getIndentation(), r.isAddWhitespace());
		}
	}

	@SuppressWarnings({ "restriction" })
	private void traverseRoot(IProgressMonitor monitor, ILElement e) {
		final IInternalElement element = e.getElement();
		final IElementDesc desc = ElementDescRegistry.getInstance()
				.getElementDesc(element);
		stream.appendClauseRegion(desc.getPrefix());
		stream.incrementIndentation(TWO_TABS_INDENT);
		stream.appendPresentationTabs(e, TWO_TABS_INDENT);
		processCommentedElement(e, true, NONE);
		stream.appendLineSeparator();
		stream.appendLeftPresentationTabs(e);
		stream.appendLabelRegion(element.getElementName(), e);
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
				stream.appendClauseRegion(childDesc.getPrefix());
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
					stream.appendPresentationRegion(" ", element);
				final IAttributeManipulation manipulation = d.getManipulation();
				value = manipulation.getValue(rElement, null);
				if (!value.isEmpty()) {
					final String prefix = d.getPrefix();
					if (!prefix.isEmpty()) {
						stream.appendPresentationRegion(prefix, element);
					}
					stream.appendAttributeRegion(value, element, manipulation,
							d.getAttributeType());
					final String suffix = d.getSuffix();
					if (!suffix.isEmpty()) {
						stream.appendPresentationRegion(suffix, element);
					}
					i++;
				}
			} catch (RodinDBException e) {
				value = "failure while loading";
				e.printStackTrace();
			}
			stream.appendPresentationRegion(" ", element);
		}
		stream.appendLineSeparator();
	}

	private void processBasicStringAttribute(ILElement element,
			IAttributeType.String type, ContentType ct, ContentType implicitct,
			boolean appendTabs, int additionnalTabs) {
		final String value = element.getAttribute(type);
		final ContentType contentType = getContentType(element, implicitct, ct);
		final IInternalElementType<?> etype = element.getElementType();
		final String attributeId = type.getId();
		final IAttributeManipulation manipulation = getManipulation(etype, attributeId);
		final String text = (value == null) ? "" : value;
		stream.appendRegion(text, element, contentType, manipulation, true,
				additionnalTabs);
	}

	private void processCommentedElement(ILElement element, boolean appendTabs,
			int additionnalTabs) {
		stream.appendCommentHeaderRegion(element, appendTabs);
		processBasicStringAttribute(element, COMMENT_ATTRIBUTE, COMMENT_TYPE,
				IMPLICIT_COMMENT_TYPE, true, additionnalTabs);
		if (!appendTabs)
			stream.appendLineSeparator();
	}
	
	private void processPredicateElement(ILElement element) {
		processBasicStringAttribute(element, PREDICATE_ATTRIBUTE, CONTENT_TYPE,
				IMPLICIT_CONTENT_TYPE, true, TWO_TABS_INDENT);
	}

	private void processAssignmentElement(ILElement element) {
		processBasicStringAttribute(element, ASSIGNMENT_ATTRIBUTE,
				CONTENT_TYPE, IMPLICIT_CONTENT_TYPE, true, TWO_TABS_INDENT);
	}

	private void processLabeledElement(ILElement element) {
		stream.appendLeftPresentationTabs(element);
		final ContentType contentType;
		if ((element.getAttribute(ASSIGNMENT_ATTRIBUTE) == null)
				&& (element.getAttribute(PREDICATE_ATTRIBUTE) == null)) {
			contentType = getContentType(element, BOLD_IMPLICIT_LABEL_TYPE,
					BOLD_LABEL_TYPE);
		} else {
			contentType = getContentType(element, IMPLICIT_LABEL_TYPE,
					LABEL_TYPE);
		}
		final String id = LABEL_ATTRIBUTE.getId();
		final IAttributeManipulation manipulation = getManipulation(
				element.getElementType(), id);
		final String labelAttribute = element.getAttribute(LABEL_ATTRIBUTE);
		if (labelAttribute != null) {
			stream.appendRegion(labelAttribute, element, contentType,
					manipulation, false, NONE);
			stream.appendPresentationRegion(":\t", element);
		} else {
			stream.appendRegion("", element, contentType, manipulation, false,
					NONE);
		}
		if (!element.getChildren().isEmpty()
				&& element.getAttributes().isEmpty()) {
			stream.appendLineSeparator();
		}
	}

	private void processIdentifierElement(ILElement element) {
		processBasicStringAttribute(element, IDENTIFIER_ATTRIBUTE,
				IDENTIFIER_TYPE, IMPLICIT_IDENTIFIER_TYPE, false, NONE);
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
