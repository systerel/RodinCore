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

import static fr.systerel.editor.editors.RodinConfiguration.ATTRIBUTE_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.COMMENT_HEADER_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.COMMENT_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.CONTENT_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.IDENTIFIER_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.IMPLICIT_COMMENT_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.IMPLICIT_CONTENT_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.IMPLICIT_IDENTIFIER_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.KEYWORD_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.LABEL_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.SECTION_TYPE;
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
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.editors.RodinConfiguration;
import fr.systerel.editor.editors.RodinConfiguration.ContentType;

/**
 * Creates the text for a given root. The intervals and editor elements are
 * built too and registered with the document mapper.
 */
public class RodinTextGenerator {

	private static final Character tab = '\u0009';
	private static final Object lineSeparator = System
			.getProperty("line.separator");

	private static final IAttributeType[] BASIC_ATTRIBUTE_TYPES = {
			ASSIGNMENT_ATTRIBUTE, COMMENT_ATTRIBUTE, IDENTIFIER_ATTRIBUTE,
			LABEL_ATTRIBUTE, PREDICATE_ATTRIBUTE };

	private final DocumentMapper documentMapper;

	private StringBuilder builder;
	private int level = 0;

	private ArrayList<Position> foldingRegions = new ArrayList<Position>();

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
		builder = new StringBuilder();
		documentMapper.resetPrevious();
		traverseRoot(null, inputRoot);
		return builder.toString();
	}

	@SuppressWarnings({ "restriction" })
	private void traverseRoot(IProgressMonitor monitor, ILElement e) {
		final IRodinElement rodinElement = (IRodinElement) e.getElement();
		if (rodinElement instanceof IInternalElement) {
			final IElementDesc desc = ElementDescRegistry.getInstance()
					.getElementDesc(rodinElement);
			addSectionRegion(desc.getPrefix());
			level++;
			processCommentedElement(e, true);
			builder.append((String) lineSeparator);
			addPresentationRegion(getTabs(level), e);
			addLabelRegion(rodinElement.getElementName(), e);
			processOtherAttributes(e);
			level--;
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
				start = builder.length();
				if (level < 1) {
					addSectionRegion(childDesc.getPrefix());
				} else {
					addKeywordRegion(childDesc.getPrefix());
				}
			}
			for (ILElement in : c) {
				level++;
				addPresentationRegion(getTabs(level), in);
				processElement(in);
				traverse(mon, in);
				level--;
			}
			final int length = builder.length() - start;
			if (start != -1 && level == 0) {
				documentMapper.addEditorSection(rel.getChildType(), start,
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
		final List<IAttributeValue> attributes = new ArrayList<IAttributeValue>(
				element.getAttributes());
		for (IAttributeValue lv : element.getAttributes()) {
			if (Arrays.asList(BASIC_ATTRIBUTE_TYPES).contains(lv.getType())) {
				attributes.remove(lv);
			}
		}
		int i = 0;
		for (IAttributeDesc d : getAttributeDescs(element.getElementType())) {
			final IInternalElement rElement = element.getElement();
			String value = "";
			try {
				if (i == 0)
					builder.append(" ");
				final IAttributeManipulation manipulation = d.getManipulation();
				value = manipulation.getValue(rElement, null);
				if (!value.isEmpty()) {
					addPresentationRegion(d.getPrefix(), element);
					addAttributeRegion(value, element, manipulation);
					addPresentationRegion(d.getSuffix(), element);
				}
			} catch (RodinDBException e) {
				value = "failure while loading";
			}
			addPresentationRegion(" ", element);
		}
		builder.append((String) lineSeparator);
	}

	protected void addElementRegion(String text, ILElement element,
			ContentType contentType) {
		int start = builder.length();
		builder.append(text);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, element, contentType);
	}

	protected void addAttributeRegion(String text, ILElement element,
			IAttributeManipulation manipulation) {
		int start = builder.length();
		builder.append(text);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, element, ATTRIBUTE_TYPE,
				manipulation);
	}

	protected void addLabelRegion(String text, ILElement element) {
		int start = builder.length();
		builder.append(text);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, element, LABEL_TYPE);
		builder.append(lineSeparator);
	}

	protected void addPresentationRegion(String text, ILElement element) {
		int start = builder.length();
		builder.append(text);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, element,
				RodinConfiguration.PRESENTATION_TYPE);
	}

	protected void addCommentHeaderRegion(ILElement element, boolean appendTabs) {
		if (appendTabs)
			builder.append(getTabs(level));
		int start = builder.length();
		builder.append("§");
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, element,
				COMMENT_HEADER_TYPE);
	}

	protected void addKeywordRegion(String title) {
		addPresentationRegion(getTabs(level), null);
		int start = builder.length();
		builder.append(title);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, null, KEYWORD_TYPE);
		builder.append(lineSeparator);
	}

	protected void addSectionRegion(String title) {
		if (level > 0)
			addPresentationRegion(getTabs(level), null);
		int start = builder.length();
		builder.append(title);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, null, SECTION_TYPE);
		builder.append((String) lineSeparator);
	}

	private void processCommentedElement(ILElement element, boolean appendTabs) {
		addCommentHeaderRegion(element, appendTabs);
		final String commentAttribute = element.getAttribute(COMMENT_ATTRIBUTE);
		final ContentType contentType = getContentType(element,
				IMPLICIT_COMMENT_TYPE, COMMENT_TYPE);
		if (commentAttribute != null) {
			final String comment = processMulti(commentAttribute);
			addElementRegion(comment, element, contentType);
		} else {
			addElementRegion("", element, contentType);
		}
		if (!appendTabs)
			builder.append((String) lineSeparator);
	}

	private void processPredicateElement(ILElement element) {
		final String predAttribute = element.getAttribute(PREDICATE_ATTRIBUTE);
		final ContentType contentType = getContentType(element,
				IMPLICIT_CONTENT_TYPE, CONTENT_TYPE);
		if (predAttribute != null) {
			final String pred = processMulti(predAttribute);
			addElementRegion(pred, element, contentType);
		} else {
			addElementRegion("", element, contentType);
		}
	}

	private void processAssignmentElement(ILElement element) {
		final String assignAttribute = element.getAttribute(ASSIGNMENT_ATTRIBUTE);
		final ContentType contentType = getContentType(element,
				IMPLICIT_CONTENT_TYPE, CONTENT_TYPE);
		if (assignAttribute != null) {
			final String assign = processMulti(assignAttribute);
			addElementRegion(assign, element, contentType);
		} else {
			addElementRegion("", element, contentType);
		}
	}

	private void processLabeledElement(ILElement element) {
		final String labelAttribute = element.getAttribute(LABEL_ATTRIBUTE);
		addPresentationRegion(getTabs(level), element);
		final ContentType contentType = getContentType(element,
				IMPLICIT_IDENTIFIER_TYPE, IDENTIFIER_TYPE);
		if (labelAttribute != null) {
			addElementRegion(labelAttribute, element, contentType);
			addPresentationRegion(" : ", element);
		} else {
			addElementRegion("", element, contentType);
		}
		if (!element.getChildren().isEmpty()
				&& element.getAttributes().isEmpty()) {
			builder.append((String) lineSeparator);
		}
	}

	private void processIdentifierElement(ILElement element) {
		final String identifierAttribute = element
				.getAttribute(IDENTIFIER_ATTRIBUTE);
		final ContentType contentType = getContentType(element,
				IMPLICIT_IDENTIFIER_TYPE, IDENTIFIER_TYPE);
		if (identifierAttribute != null) {
			addElementRegion((String) identifierAttribute, element, contentType);
		} else {
			addElementRegion("", element, contentType);
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

	private ContentType getContentType(ILElement element,
			ContentType implicitIdentifierType, ContentType identifierType) {
		if (element.isImplicit()) {
			return implicitIdentifierType;
		}
		return identifierType;
	}

	private String processMulti(String str) {
		final StringBuilder sb = new StringBuilder();
		final String[] split = str.split((String) lineSeparator);
		int i = 0;
		for (String s : split) {
			if (i != 0)
				sb.append(getTabs(level));
			s.replaceAll("\r", "\r" + getTabs(level));
			sb.append(s);
			if (i != split.length - 1)
				sb.append((String) lineSeparator);
			i++;
		}
		return sb.toString();
	}

	public Position[] getFoldingRegions() {
		return foldingRegions.toArray(new Position[foldingRegions.size()]);
	}

	private String getTabs(int number) {
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < number; i++) {
			tabs.append(tab);
		}
		return tabs.toString();
	}
}
