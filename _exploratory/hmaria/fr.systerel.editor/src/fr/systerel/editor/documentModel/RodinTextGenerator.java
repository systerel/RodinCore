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

import static fr.systerel.editor.editors.RodinConfiguration.COMMENT_HEADER_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.COMMENT_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.CONTENT_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.IDENTIFIER_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.IMPLICIT_COMMENT_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.IMPLICIT_CONTENT_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.IMPLICIT_IDENTIFIER_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.IMPLICIT_LABEL_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.KEYWORD_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.LABEL_TYPE;
import static fr.systerel.editor.editors.RodinConfiguration.SECTION_TYPE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IAttributeDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementRelationship;
import org.eventb.internal.ui.eventbeditor.elementdesc.NullAttributeDesc;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.lightcore.Attribute;
import org.rodinp.core.emf.lightcore.ImplicitElement;
import org.rodinp.core.emf.lightcore.LightElement;

/**
 * Creates the text for a given root. The intervals and editor elements are
 * built too and registered with the document mapper.
 */
public class RodinTextGenerator {

	private static final Character tab = '\u0009';
	private static final Object lineSeparator = System
			.getProperty("line.separator");

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
	public String createText(LightElement inputRoot) {
		builder = new StringBuilder();
		documentMapper.resetPrevious();
		traverseRoot(null, inputRoot);
		return builder.toString();
	}

	@SuppressWarnings({ "restriction" })
	private void traverseRoot(IProgressMonitor monitor, LightElement e) {
		final IRodinElement rodinElement = (IRodinElement) e.getERodinElement();
		if (rodinElement instanceof IInternalElement) {
			final IElementDesc desc = ElementDescRegistry.getInstance()
					.getElementDesc(rodinElement);
			addSectionRegion(desc.getPrefix());
			level++;
			if (rodinElement instanceof ICommentedElement) {
				processCommentedElement(e);
			}
			addLabelRegion(rodinElement.getElementName(), e);
			level--;
			// process the comment
			processElement(e);
			traverse(monitor, e);
		}
	}

	@SuppressWarnings("restriction")
	private void traverse(IProgressMonitor mon, LightElement e) {
		final IElementDesc desc = getElementDesc(e);
		for (IElementRelationship rel : desc.getChildRelationships()) {
			final List<LightElement> c = retrieveChildrenToProcess(rel, e);
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
			for (LightElement in : c) {
				level++;
				processElement(in);
				// final int eStart = builder.length();
				traverse(mon, in);
				// setFoldingForSubElement(in, eStart);
				level--;
			}
			final int length = builder.length() - start;
			if (start != -1 && level == 0) {
				documentMapper.addEditorSection(rel.getChildType(), start,
						length);
			}
		}
	}

	// private void setFoldingForSubElement(LightElement toFold,
	// final int foldStart) {
	// if (level == 1) {
	// final EditorItem foldedElement = documentMapper
	// .getEditorElement(toFold);
	// final int length = builder.length() - foldStart;
	// if (foldedElement != null)
	// foldedElement.setFoldingPosition(foldStart, length);
	// }
	// }

	// We retrieve all children of the element elt and if the client defined a
	// way to retrieve children including implicit ones, we ask the implicit
	// child provider to get this list of visible children
	@SuppressWarnings("restriction")
	private List<LightElement> retrieveChildrenToProcess(
			IElementRelationship rel, LightElement elt) {
		final ArrayList<LightElement> result = new ArrayList<LightElement>();
		final IInternalElementType<?> type = rel.getChildType();
		result.addAll(elt.getElementsOfType(type));
		return result;
	}

	// Retrieves the element desc from the registry for the given element e
	@SuppressWarnings("restriction")
	private static IElementDesc getElementDesc(LightElement e) {
		final IRodinElement rodinElement = (IRodinElement) e.getERodinElement();
		return ElementDescRegistry.getInstance().getElementDesc(rodinElement);
	}

	// Retrieves the element desc from the registry for the given element e
	@SuppressWarnings("restriction")
	private static IElementDesc getElementDesc(IInternalElementType<?> type) {
		return ElementDescRegistry.getInstance().getElementDesc(type);
	}

	@SuppressWarnings("restriction")
	private static IAttributeDesc getAttributeDesc(String id,
			IInternalElementType<?> type) {
		int i = 0;
		IAttributeDesc desc;
		final List<IAttributeDesc> descs = new ArrayList<IAttributeDesc>();
		while ((desc = ElementDescRegistry.getInstance().getAttribute(type, i)) != null) {
			descs.add(desc);
			i++;
		}
		for (IAttributeDesc r : descs) {
			if (r.getAttributeType().getId().equals(id)) {
				return r;
			}
		}
		return new NullAttributeDesc();
	}

	protected void addElementRegion(String text, LightElement element,
			String contentType) {
		int start = builder.length();
		builder.append(text);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, element, contentType);
	}

	protected void addLabelRegion(String text, LightElement element) {
		int start = builder.length();
		builder.append(getTabs(level));
		builder.append(text);
		builder.append(lineSeparator);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, element, LABEL_TYPE);
	}

	protected void addCommentHeaderRegion(LightElement element) {
		int start = builder.length();
		builder.append(getTabs(level));
		builder.append("§");
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, element,
				COMMENT_HEADER_TYPE);
	}

	protected void addKeywordRegion(String title) {
		int start = builder.length();
		builder.append(getTabs(level));
		builder.append(title);
		builder.append(lineSeparator);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, null, KEYWORD_TYPE);
	}

	protected void addSectionRegion(String title) {
		int start = builder.length();
		builder.append(getTabs(level));
		builder.append(title);
		builder.append(lineSeparator);
		int length = builder.length() - start;
		documentMapper.processInterval(start, length, null, SECTION_TYPE);
	}

	private void processCommentedElement(LightElement element) {
		addCommentHeaderRegion(element);
		final Attribute commentAttribute = element.getEAttributes().get(
				EventBAttributes.COMMENT_ATTRIBUTE.getId());
		final String contentType = getContentType(element,
				IMPLICIT_COMMENT_TYPE, COMMENT_TYPE);
		if (commentAttribute != null) {
			final String comment = processMulti((String) commentAttribute
					.getValue());
			addElementRegion(comment, element, contentType);
		} else {
			addElementRegion("", element, contentType);
		}
		builder.append(lineSeparator);
	}

	private void processPredicateElement(LightElement element) {
		final Attribute predAttribute = element.getEAttributes().get(
				EventBAttributes.PREDICATE_ATTRIBUTE.getId());
		final String contentType = getContentType(element,
				IMPLICIT_CONTENT_TYPE, CONTENT_TYPE);
		if (predAttribute != null) {
			final String pred = processMulti((String) predAttribute.getValue());
			addElementRegion(pred, element, contentType);
		} else {
			addElementRegion("", element, contentType);
		}
		builder.append(lineSeparator);
	}

	private void processAssignmentElement(LightElement element) {
		final Attribute assignAttribute = element.getEAttributes().get(
				EventBAttributes.ASSIGNMENT_ATTRIBUTE.getId());
		final String contentType = getContentType(element,
				IMPLICIT_CONTENT_TYPE, CONTENT_TYPE);
		if (assignAttribute != null) {
			final String assign = processMulti((String) assignAttribute
					.getValue());
			addElementRegion(assign, element, contentType);
		} else {
			addElementRegion("", element, contentType);
		}
		builder.append(lineSeparator);
	}

	private void processLabeledElement(LightElement element) {
		final Attribute labelAttribute = element.getEAttributes().get(
				EventBAttributes.LABEL_ATTRIBUTE.getId());
		builder.append(getTabs(level));
		final String contentType = getContentType(element, IMPLICIT_LABEL_TYPE,
				LABEL_TYPE);
		if (labelAttribute != null) {
			addElementRegion((String) labelAttribute.getValue(), element,
					contentType);
			builder.append(" : ");
		} else {
			addElementRegion("", element, contentType);
		}
		if (!element.getEChildren().isEmpty()) {
			builder.append(lineSeparator);
		}
	}

	private void processIdentifierElement(LightElement element) {
		final Attribute identifierAttribute = element.getEAttributes().get(
				EventBAttributes.IDENTIFIER_ATTRIBUTE.getId());
		builder.append(getTabs(level));
		final String contentType = getContentType(element,
				IMPLICIT_IDENTIFIER_TYPE, IDENTIFIER_TYPE);
		if (identifierAttribute != null) {
			addElementRegion((String) identifierAttribute.getValue(), element,
					contentType);
		} else {
			addElementRegion("", element, contentType);
		}
	}

	private void processElement(LightElement element) {
		final IRodinElement rodinElement = (IRodinElement) element
				.getERodinElement();
		if (rodinElement instanceof ILabeledElement) {
			if (rodinElement instanceof ICommentedElement) {
				processCommentedElement(element);
			}
			processLabeledElement(element);
		}
		if (rodinElement instanceof IIdentifierElement) {
			processIdentifierElement(element);
			if (rodinElement instanceof ICommentedElement) {
				processCommentedElement(element);
			}
		}
		if (rodinElement instanceof IPredicateElement) {
			processPredicateElement(element);
		}
		if (rodinElement instanceof IAssignmentElement) {
			processAssignmentElement(element);
		}
		// builder.append(lineSeparator);
	}

	private String getContentType(LightElement element,
			String implicitContentType, String contentType) {
		if (element instanceof ImplicitElement) {
			return implicitContentType;
		}
		return contentType;
	}

	private String processMulti(String str) {
		final StringBuilder sb = new StringBuilder();
		final String[] split = str.split((String) lineSeparator);
		int i = 0;
		for (String s : split) {
			if (i != 0)
				sb.append(getTabs(level));
			sb.append(s);
			if (i != split.length - 1)
				sb.append(lineSeparator);
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
