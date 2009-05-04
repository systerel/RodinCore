/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - generic attribute manipulation
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

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
		protected boolean postExistenceTest(InternalElement left,
				InternalElement right) throws RodinDBException {
			return compareAttributes(left, right);
		}

		@Override
		protected boolean preExistenceTest(InternalElement left,
				InternalElement right) {
			return true;
		}

	}

	protected static class SameChildrenComparer extends ElementComparer {

		@Override
		protected boolean postExistenceTest(InternalElement left,
				InternalElement right) throws RodinDBException {
			return compareChildren(left, right);
		}

		@Override
		protected boolean preExistenceTest(InternalElement left,
				InternalElement right) {
			return true;
		}

	}

	protected static class SameContentsComparer extends ElementComparer {

		@Override
		protected boolean postExistenceTest(InternalElement left,
				InternalElement right) throws RodinDBException {
			return compareAttributes(left, right)
					&& compareChildren(left, right);
		}

		@Override
		protected boolean preExistenceTest(InternalElement left,
				InternalElement right) {
			return compareNameAndType(left, right);
		}

	}

	public static boolean hasSameAttributes(InternalElement left,
			InternalElement right) throws RodinDBException {
		return new SameAttributesComparer().compare(left, right);
	}

	public static boolean hasSameChildren(InternalElement left,
			InternalElement right) throws RodinDBException {
		return new SameChildrenComparer().compare(left, right);
	}

	public static boolean hasSameContents(InternalElement left,
			InternalElement right) throws RodinDBException {
		return new SameContentsComparer().compare(left, right);
	}

	protected final boolean compare(InternalElement left,
			InternalElement right) throws RodinDBException {

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

	protected final boolean compareAttributes(InternalElement left,
			InternalElement right) throws RodinDBException {

		// Attributes are not ordered
		final Set<IAttributeValue> leftSet = asSet(left.getAttributeValues());
		final Set<IAttributeValue> rightSet = asSet(right.getAttributeValues());
		return leftSet.equals(rightSet);
	}

	private Set<IAttributeValue> asSet(IAttributeValue[] members) {
		return new HashSet<IAttributeValue>(Arrays.asList(members));
	}

	protected final boolean compareChildren(InternalElement left,
			InternalElement right) throws RodinDBException {

		// Children are ordered
		final IRodinElement[] leftChildren = left.getChildren();
		final IRodinElement[] rightChildren = right.getChildren();
		final int length = leftChildren.length;
		if (length != rightChildren.length) {
			return false;
		}
		for (int i = 0; i < length; ++i) {
			InternalElement leftChild = (InternalElement) leftChildren[i];
			InternalElement rightChild = (InternalElement) rightChildren[i];
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

	protected final boolean compareNameAndType(InternalElement left,
			InternalElement right) {
		return left.getElementName().equals(right.getElementName())
				&& left.getElementType() == right.getElementType();
	}

	protected abstract boolean postExistenceTest(InternalElement left,
			InternalElement right) throws RodinDBException;

	protected abstract boolean preExistenceTest(InternalElement left,
			InternalElement right) throws RodinDBException;

}
