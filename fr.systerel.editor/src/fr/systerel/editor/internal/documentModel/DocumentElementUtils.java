/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.documentModel;

import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IAttributeDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDesc;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

public class DocumentElementUtils {
	
	private static final ElementDescRegistry DESC_REGISTRY = ElementDescRegistry
			.getInstance();
	
	private static final IAttributeType[] BASIC_ATTRIBUTE_TYPES = {
		ASSIGNMENT_ATTRIBUTE, COMMENT_ATTRIBUTE, IDENTIFIER_ATTRIBUTE,
		LABEL_ATTRIBUTE, PREDICATE_ATTRIBUTE, EXPRESSION_ATTRIBUTE};
	
	// Retrieves the element desc from the registry for the given element e
	public static IElementDesc getElementDesc(ILElement e) {
		final IRodinElement rodinElement = (IRodinElement) e.getElement();
		return DESC_REGISTRY.getElementDesc(rodinElement);
	}

	/**
	 * Retrieves the element desc from the registry for the given element type
	 * <code>type<code>.
	 * 
	 * @param type
	 *            the element type to retrieve the descriptor for
	 */
	public static IElementDesc getElementDesc(IInternalElementType<?> type) {
		return DESC_REGISTRY.getElementDesc(type);
	}

	public static List<IAttributeDesc> getAttributeDescs(ILElement element,
			boolean filterBasic) {
		final List<IAttributeDesc> descs = new ArrayList<IAttributeDesc>();
		int i = 0;
		IAttributeDesc desc;
		final List<IAttributeType> basicDescs = Arrays
				.asList(BASIC_ATTRIBUTE_TYPES);
		final IInternalElementType<?> elementType = element.getElementType();
		while ((desc = DESC_REGISTRY.getAttribute(elementType, i)) != null) {
			if (!(filterBasic && basicDescs.contains(desc.getAttributeType()))) {
				descs.add(desc);
			}
			i++;
		}
		return descs;
	}
	
	public static List<IAttributeDesc> getNonBasicAttributeDescs(
			ILElement element) {
		final List<IAttributeDesc> descs = getAttributeDescs(element, true);
		return descs;
	}
	
	public static IAttributeDesc getAttributeDesc(ILElement elem,
			IAttributeType attrType) {
		final List<IAttributeDesc> attributeDescs = getAttributeDescs(elem,
				false);
		for (IAttributeDesc desc : attributeDescs) {
			if (desc.getAttributeType().equals(attrType))
				return desc;
		}
		return null;
	}
	
	public static IAttributeManipulation getManipulation(
			ILElement element, IAttributeType attributeType) {
		final IAttributeDesc attributeDesc = getAttributeDesc(element, attributeType);
		return attributeDesc.getManipulation();
	}
	
	public static Set<IInternalElementType<?>> getChildrenTypes(
			ILElement element) {
		final IElementDesc eDesc = getElementDesc(element);
		final Set<IInternalElementType<?>> types = new HashSet<IInternalElementType<?>>();
		for (IElementType<?> t : eDesc.getChildTypes()) {
			if (t instanceof IInternalElementType<?>) {
				types.add((IInternalElementType<?>) t);
			}
		}
		return types;
	}
	
	public static ILElement getSibling(ILElement element) {
		final ILElement parent = element.getParent();
		if (parent == null) {
			return null;
		}
		final List<ILElement> ofType = parent.getChildrenOfType(element
				.getElementType());
		int sibling = 0;
		for (ILElement el : ofType) {
			if (el == element) {
				sibling++;
				break;
			}
			sibling++;
		}
		if (sibling < ofType.size()) {
			return ofType.get(sibling);
		}
		return null;
	}
	
	public static ILElement getSiblingBefore(ILElement element) {
		if (element == null)
			return null;
		final ILElement parent = element.getParent();
		if (parent == null)
			return null;
		final List<ILElement> sameType = parent.getChildrenOfType(element
				.getElementType());
		final ListIterator<ILElement> itr = sameType.listIterator();
		ILElement siblingBefore = null;
		ILElement e = null;
		while (itr.hasNext()) {
			siblingBefore = e;
			e = (ILElement) itr.next();
			if (e.equals(element)) {
				break;
			}
		}
		return siblingBefore;
	}
	
	public static ILElement getNextSibling(ILElement element,
			List<ILElement> sameType) {
		for (int i = 0; i < sameType.size() - 1; i++) {
			if (sameType.get(i).equals(element)) {
				return sameType.get(i + 1);
			}
		}
		return null;
	}

	public static ILElement getPreviousSibling(ILElement element,
			List<ILElement> sameType) {
		for (int i = sameType.size() - 1; i > 0; i--) {
			if (sameType.get(i).equals(element)) {
				return sameType.get(i - 1);
			}
		}
		return null;
	}
	
}
