/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Common implementation for comparing internal parents. Implements the sameness
 * tests.
 * <p>
 * Even internally in this plug-in, only public methods should be used.
 * </p>
 * 
 * @author Laurent Voisin
 */
public abstract class ElementComparer {

	protected static class SameAttributesComparer extends ElementComparer {

		@Override
		protected boolean postExistenceTest(IInternalParentX left,
				IInternalParentX right) throws RodinDBException {
			return compareAttributes(left, right);
		}

		@Override
		protected boolean preExistenceTest(IInternalParentX left,
				IInternalParentX right) {
			return true;
		}

	}

	protected static class SameChildrenComparer extends ElementComparer {

		@Override
		protected boolean postExistenceTest(IInternalParentX left,
				IInternalParentX right) throws RodinDBException {
			return compareChildren(left, right);
		}

		@Override
		protected boolean preExistenceTest(IInternalParentX left,
				IInternalParentX right) {
			return true;
		}

	}

	protected static class SameContentsComparer extends ElementComparer {

		@Override
		protected boolean postExistenceTest(IInternalParentX left,
				IInternalParentX right) throws RodinDBException {
			return compareAttributes(left, right)
					&& compareChildren(left, right);
		}

		@Override
		protected boolean preExistenceTest(IInternalParentX left,
				IInternalParentX right) {
			return compareNameAndType(left, right);
		}

	}

	public static boolean hasSameAttributes(IInternalParentX left,
			IInternalParentX right) throws RodinDBException {
		return new SameAttributesComparer().compare(left, right);
	}

	public static boolean hasSameChildren(IInternalParentX left,
			IInternalParentX right) throws RodinDBException {
		return new SameChildrenComparer().compare(left, right);
	}

	public static boolean hasSameContents(IInternalParentX left,
			IInternalParentX right) throws RodinDBException {
		return new SameContentsComparer().compare(left, right);
	}

	protected final boolean compare(IInternalParentX left,
			IInternalParentX right) throws RodinDBException {

		if (!preExistenceTest(left, right)) {
			return false;
		}
		final boolean leftExists = left.exists();
		final boolean rightExists = right.exists();
		if (leftExists != rightExists) {
			return false;
		}
		if (!leftExists) {
			return true;
		}
		return postExistenceTest(left, right);
	}

	protected final boolean compareAttributes(IInternalParentX left,
			IInternalParentX right) throws RodinDBException {

		// Attributes are not ordered
		final IAttributeType[] leftAttrs = left.getAttributeTypes();
		final IAttributeType[] rightAttrs = right.getAttributeTypes();
		if (leftAttrs.length != rightAttrs.length) {
			return false;
		}
		for (IAttributeType attr : leftAttrs) {
			if (!right.hasAttribute(attr)) {
				return false;
			}
			final String attrName = attr.getId();
			if (!left.getAttributeRawValue(attrName).equals(
					right.getAttributeRawValue(attrName))) {
				return false;
			}
		}
		return true;
	}

	protected final boolean compareChildren(IInternalParentX left,
			IInternalParentX right) throws RodinDBException {

		// Children are ordered
		final IRodinElement[] leftChildren = left.getChildren();
		final IRodinElement[] rightChildren = right.getChildren();
		final int length = leftChildren.length;
		if (length != rightChildren.length) {
			return false;
		}
		for (int i = 0; i < length; ++i) {
			IInternalParentX leftChild = (IInternalParentX) leftChildren[i];
			IInternalParentX rightChild = (IInternalParentX) rightChildren[i];
			final boolean sameChildren = compareNameAndType(leftChild,
					rightChild)
					&& compareAttributes(leftChild, rightChild)
					&& compareChildren(leftChild, rightChild);
			if (!sameChildren) {
				return false;
			}
		}
		return true;
	}

	protected final boolean compareNameAndType(IInternalParentX left,
			IInternalParentX right) {
		return left.getElementName().equals(right.getElementName())
				&& left.getElementType() == right.getElementType();
	}

	protected abstract boolean postExistenceTest(IInternalParentX left,
			IInternalParentX right) throws RodinDBException;

	protected abstract boolean preExistenceTest(IInternalParentX left,
			IInternalParentX right) throws RodinDBException;

}
