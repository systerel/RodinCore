/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.rodinp.core.RodinCore.getInternalElementType;
import static org.rodinp.core.tests.TypeTreeShape.TypeTreeShapeComparisonResult.notSameTree;
import static org.rodinp.core.tests.TypeTreeShape.TypeTreeShapeComparisonResult.sameTree;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Describes an expected hierarchy of internal elements.
 * 
 * @author Thomas Muller
 */
public class TypeTreeShape {

	/**
	 * Returns a tree shape with the given children and attribute types. The
	 * check will verify that all children are present, that there are no other
	 * children and that the specified attributes are also present. It will
	 * <em>not</em> check for the absence of other attributes.
	 * 
	 * @param namespace
	 *            namespace of the element type of the root
	 * @param id
	 *            id of the element type of the root (last component)
	 * @param attributeTypes
	 *            types of the attributes that should be present
	 * @param children
	 *            shapes of the children
	 * @return a new tree shape with the given root, children and attributes
	 */
	public static TypeTreeShape s(String namespace, String id,
			IAttributeType[] attributeTypes, TypeTreeShape... children) {
		return new TypeTreeShape(namespace + "." + id, attributeTypes, children);
	}

	/**
	 * Returns a tree shape with the given children. The check will verify that
	 * all children are present and that there are no other children. It will
	 * <em>not</em> check anything about attributes.
	 * 
	 * @param namespace
	 *            namespace of the element type of the root
	 * @param id
	 *            id of the element type of the root (last component)
	 * @param children
	 *            shapes of the children
	 * @return a new tree shape with the given root, children and attributes
	 */
	public static TypeTreeShape s(String namespace, String id,
			TypeTreeShape... children) {
		return s(namespace, id, NO_ATTRIBUTE_TYPES, children);
	}

	private static final IAttributeType[] NO_ATTRIBUTE_TYPES = {};

	private final IInternalElementType<?> elementType;
	private final IAttributeType[] attributesTypes;
	private final TypeTreeShape[] children;

	private TypeTreeShape(String elementTypeId, IAttributeType[] atributeTypes,
			TypeTreeShape... children) {
		this.elementType = getInternalElementType(elementTypeId);
		this.attributesTypes = atributeTypes;
		this.children = children;
	}

	public TypeTreeShapeComparisonResult assertSameTreeWithMessage(
			IInternalElement element) throws RodinDBException {
		if (!(elementType.equals(element.getElementType()))) {
			return notSameTree("Element " + element
					+ " does not have the type " + elementType.getName());
		}
		for (IAttributeType type : attributesTypes) {
			if (!element.hasAttribute(type)) {
				return notSameTree("Attribute " + type.getName()
						+ " not present in element " + element);
			}
		}
		final IRodinElement[] eChildren = element.getChildren();
		if (children.length != eChildren.length) {
			return notSameTree("Not same number of child elements in " + element);
		}
		for (int i = 0; i < children.length; i++) {
			final TypeTreeShapeComparisonResult childCompResult = children[i]
					.assertSameTreeWithMessage((IInternalElement) eChildren[i]);
			if (!childCompResult.same()) {
				return notSameTree("At least one child is different :"
						+ childCompResult.getMessage());
			}
		}
		return sameTree();
	}

	public boolean assertSameTree(IInternalElement element)
			throws RodinDBException {
		return assertSameTreeWithMessage(element).same();
	}

	public static class TypeTreeShapeComparisonResult {

		private final boolean sameTree;
		private final String message;

		private TypeTreeShapeComparisonResult(boolean sameTree, String message) {
			this.sameTree = sameTree;
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public boolean same() {
			return sameTree;
		}

		public static TypeTreeShapeComparisonResult sameTree() {
			return new TypeTreeShapeComparisonResult(true, "");
		}

		public static TypeTreeShapeComparisonResult notSameTree(String message) {
			return new TypeTreeShapeComparisonResult(false, message);
		}

	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		this.toString(sb, 1);
		return sb.toString();
	}

	public void toString(StringBuilder sb, int level) {
		getLevelChars(sb, level);
		sb.append(elementType.toString());
		sb.append("\n");
		level++;
		if (children != null) {
			for (TypeTreeShape c : children) {
				c.toString(sb, level);
			}
		}
	}

	private void getLevelChars(StringBuilder sb, int level) {
		for (int i = 0; i < level; i++) {
			sb.append("-");
		}
	}

}
