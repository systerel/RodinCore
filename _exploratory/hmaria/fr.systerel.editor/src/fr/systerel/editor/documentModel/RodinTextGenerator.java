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

import static fr.systerel.editor.presentation.RodinConfiguration.COMMENT_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.CONTENT_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.IDENTIFIER_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.IMPLICIT_COMMENT_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.IMPLICIT_CONTENT_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.IMPLICIT_IDENTIFIER_TYPE;
import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ICommentedElement;
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

	private static final IAttributeType[] BASIC_ATTRIBUTE_TYPES = {
			ASSIGNMENT_ATTRIBUTE, COMMENT_ATTRIBUTE, IDENTIFIER_ATTRIBUTE,
			LABEL_ATTRIBUTE, PREDICATE_ATTRIBUTE };

	private final DocumentMapper documentMapper;

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
		stream = new RodinTextStream(documentMapper);
		traverseRoot(null, inputRoot);
		return stream.getText();
	}

	@SuppressWarnings({ "restriction" })
	private void traverseRoot(IProgressMonitor monitor, ILElement e) {
		final IRodinElement rodinElement = (IRodinElement) e.getElement();
		if (rodinElement instanceof IInternalElement) {
			final IElementDesc desc = ElementDescRegistry.getInstance()
					.getElementDesc(rodinElement);
			stream.addSectionRegion(desc.getPrefix());
			stream.incrementIndentation();
			processCommentedElement(e, true);
			stream.appendLineSeparator();
			stream.appendPresentationTabs(e);
			stream.addLabelRegion(rodinElement.getElementName(), e);
			processOtherAttributes(e);
			stream.decrementIndentation();
			traverse(monitor, e);
		}
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
			if (!(noChildren)) {
				start = stream.getLength();
				if (stream.getLevel() < 1) {
					stream.addSectionRegion(childDesc.getPrefix());
				} else {
					stream.addKeywordRegion(childDesc.getPrefix());
				}
			}
			for (ILElement in : c) {
				stream.incrementIndentation();
				stream.appendPresentationTabs(in);
				processElement(in);
				traverse(mon, in);
				stream.decrementIndentation();
			}
			final int length = stream.getLength() - start;
			if (start != -1 && stream.getLevel() == 0) {
				stream.addEditorSection(rel.getChildType(), start,
						length);
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

	// Retrieves the element desc from the registry for the given element e
	@SuppressWarnings("restriction")
	private static IElementDesc getElementDesc(ILElement e) {
		final IRodinElement rodinElement = (IRodinElement) e.getElement();
		return ElementDescRegistry.getInstance().getElementDesc(rodinElement);
	}

	/**
	 * Retrieves the element desc from the registry for the given element type
	 * <code>type<code>.
	 * 
	 * @param type
	 *            the element type to retrieve the descriptor for
	 */
	@SuppressWarnings("restriction")
	private static IElementDesc getElementDesc(IInternalElementType<?> type) {
		return ElementDescRegistry.getInstance().getElementDesc(type);
	}
	
	@SuppressWarnings("restriction")
	private static List<IAttributeDesc> getAttributeDescs(IInternalElementType<?> elementType) {
		final List<IAttributeDesc> descs = new ArrayList<IAttributeDesc>();
		int i = 0;
		IAttributeDesc desc;
		final List<IAttributeType> refList = Arrays.asList(BASIC_ATTRIBUTE_TYPES);
		while ((desc = ElementDescRegistry.getInstance().getAttribute(
				elementType, i)) != null) {
			if (!refList.contains(desc.getAttributeType())){
				descs.add(desc);
			}
			i++;
		}
		return descs;
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
				value = manipulation.getValue(rElement, null);
				if (!value.isEmpty()) {
					final String prefix = d.getPrefix();
					if (!prefix.isEmpty()) {
						stream.addPresentationRegion(prefix, element);
					}
					stream.addAttributeRegion(value, element, manipulation, d.getAttributeType());
					final String suffix = d.getSuffix();
					if (!suffix.isEmpty()){
						stream.addPresentationRegion(suffix, element);						
					}
					i++;
				}
			} catch (RodinDBException e) {
				value = "failure while loading";
			}
			stream.addPresentationRegion(" ", element);
		}
		stream.appendLineSeparator();
	}

	private void processCommentedElement(ILElement element, boolean appendTabs) {
		stream.addCommentHeaderRegion(element, appendTabs);
		final String commentAttribute = element.getAttribute(COMMENT_ATTRIBUTE);
		final ContentType contentType = getContentType(element,
				IMPLICIT_COMMENT_TYPE, COMMENT_TYPE);
		if (commentAttribute != null) {
			final String comment = commentAttribute;
			stream.addElementRegion(comment, element, contentType, true);
		} else {
			stream.addElementRegion("", element, contentType, true);
		}
		if (!appendTabs)
			stream.appendLineSeparator();
	}

	private void processPredicateElement(ILElement element) {
		final String predAttribute = element.getAttribute(PREDICATE_ATTRIBUTE);
		final ContentType contentType = getContentType(element,
				IMPLICIT_CONTENT_TYPE, CONTENT_TYPE);
		if (predAttribute != null) {
			final String pred = predAttribute;
			stream.addElementRegion(pred, element, contentType, false);
		} else {
			stream.addElementRegion("", element, contentType, false);
		}
	}

	private void processAssignmentElement(ILElement element) {
		final String assignAttribute = element.getAttribute(ASSIGNMENT_ATTRIBUTE);
		final ContentType contentType = getContentType(element,
				IMPLICIT_CONTENT_TYPE, CONTENT_TYPE);
		if (assignAttribute != null) {
			final String assign = assignAttribute;
			stream.addElementRegion(assign, element, contentType, false);
		} else {
			stream.addElementRegion("", element, contentType, false);
		}
	}

	private void processLabeledElement(ILElement element) {
		final String labelAttribute = element.getAttribute(LABEL_ATTRIBUTE);
		stream.appendPresentationTabs(element);
		final ContentType contentType = getContentType(element,
				IMPLICIT_IDENTIFIER_TYPE, IDENTIFIER_TYPE);
		if (labelAttribute != null) {
			stream.addElementRegion(labelAttribute, element, contentType, false);
			stream.addPresentationRegion(" : ", element);
		} else {
			stream.addElementRegion("", element, contentType, false);
		}
		if (!element.getChildren().isEmpty()
				&& element.getAttributes().isEmpty()) {
			stream.appendLineSeparator();
		}
	}

	private void processIdentifierElement(ILElement element) {
		final String identifierAttribute = element
				.getAttribute(IDENTIFIER_ATTRIBUTE);
		final ContentType contentType = getContentType(element,
				IMPLICIT_IDENTIFIER_TYPE, IDENTIFIER_TYPE);
		if (identifierAttribute != null) {
			stream.addElementRegion((String) identifierAttribute, element, contentType, false);
		} else {
			stream.addElementRegion("", element, contentType, false);
		}
	}

	private void processElement(ILElement element) {
		final IRodinElement rodinElement = (IRodinElement) element.getElement();
		boolean commentToProcess = true;
		if (rodinElement instanceof ILabeledElement) {
			if (rodinElement instanceof ICommentedElement) {
				processCommentedElement(element, false);
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
			processCommentedElement(element, true);
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
	
//	private String processMulti(String str) {
//		final StringBuilder sb = new StringBuilder();
//		final String[] split = str.split((String) RodinTextStream.lineSeparator);
//		int i = 0;
//		for (String s : split) {
//			final String tabs = stream.getTabsForCurrentLevel();
//			if (i != 0) {
//				sb.append(tabs);
//			}
//			s.replaceAll("\r", "\r" + tabs);
//			sb.append(s);
//			if (i != split.length - 1)
//				sb.append(RodinTextStream.lineSeparator);
//			i++;
//		}
//		return sb.toString();
//	}
	
	public Position[] getFoldingRegions() {
		return foldingRegions.toArray(new Position[foldingRegions.size()]);
	}

}
