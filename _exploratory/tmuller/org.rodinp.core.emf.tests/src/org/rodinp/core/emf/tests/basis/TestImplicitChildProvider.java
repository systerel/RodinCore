/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.tests.basis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.core.emf.lightcore.IImplicitChildProvider;
import org.rodinp.core.tests.basis.NamedElement;

/**
 * Provides implicit children for the elements of type {@link ImplicitHolder}
 * from the other roots which are in the hierarchy given by
 * {@link RodinTestDependency} dependency.
 */
public class TestImplicitChildProvider implements IImplicitChildProvider {

	@Override
	public List<? extends IInternalElement> getImplicitChildren(
			IInternalElement parent) {
		if (parent instanceof ImplicitHolder) {
			try {
				final List<IInternalElement> implicitChildren = new ArrayList<IInternalElement>();
				final List<IInternalElement> result = recursiveAdditionOfImplicitChildren(
						parent, implicitChildren);
				return result;
			} catch (RodinDBException e) {
				// Something went wrong : could not retrieve the implicit
				// children...
				// No need to bother the user with my error
			}
		}
		return Collections.emptyList();
	}

	private List<IInternalElement> recursiveAdditionOfImplicitChildren(
			IInternalElement parent, List<IInternalElement> children)
			throws RodinDBException {
		// retrieves the root to get the dependencies
		final IInternalElement root = parent.getRoot();
		final RodinTestDependency[] dependencies = root
				.getChildrenOfType(RodinTestDependency.ELEMENT_TYPE);
		if (dependencies.length != 1)
			return children;
		// gets the dependency element of the current root
		final RodinTestDependency d = dependencies[0];
		// gets the root element handle from the dependency
		// to go up in the hierarchy, otherwise return...
		if (d.hasDependency()) {
			final RodinTestRoot dependency = (RodinTestRoot) d.getDependency();
			children.addAll(0, getImplicitChildrenFromDependency(dependency));
			recursiveAdditionOfImplicitChildren(dependency, children);
		}
		return children;
	}

	private Collection<? extends NamedElement> getImplicitChildrenFromDependency(
			RodinTestRoot dependency) throws RodinDBException {
		final List<NamedElement> implicitChildren = new ArrayList<NamedElement>();
		final ImplicitHolder[] holders = dependency
				.getChildrenOfType(ImplicitHolder.ELEMENT_TYPE);
		for (ImplicitHolder h : holders) {
			final RodinElement[] children = h.getChildren();
			for (IRodinElement c : children) {
				if (c instanceof NamedElement) {
					implicitChildren.add((NamedElement) c);
				}
			}
		}
		return implicitChildren;
	}

}
