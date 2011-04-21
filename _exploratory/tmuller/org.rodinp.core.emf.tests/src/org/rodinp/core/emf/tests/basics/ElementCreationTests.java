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
package org.rodinp.core.emf.tests.basics;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILFile;
import org.rodinp.core.tests.basis.NamedElement;

/**
 * 
 * Testing the element creation throught the ILElement interface.
 * 
 * @author Thomas Muller
 */
public class ElementCreationTests extends AbstractRodinEMFCoreTest {

	/**
	 * Tests the creation of a sole element.
	 */
	@Test
	public void testCreateChild() {
		final ILFile rodinResource = getRodinResource();
		// We get the light model
		final ILElement root = rodinResource.getRoot();
		final ILElement child = root.createChild(NamedElement.ELEMENT_TYPE,
				null);
		assertTrue(root.getChildren().size() == 1);
		final ILElement added = root.getChildren().get(0);
		assertTrue(added.equals(child));
	}

	/**
	 * Tests the creation with a given nextSibling parameter.
	 */
	@Test
	public void testCreateChildInFirstPosition() {

		final ILFile resource = getRodinResource();
		final ILElement root = resource.getRoot();
		final ILElement secondChild = root.createChild(
				NamedElement.ELEMENT_TYPE, null);
		final ILElement firstChild = root.createChild(
				NamedElement.ELEMENT_TYPE, secondChild);
		final List<? extends ILElement> children = root.getChildren();
		assertTrue(children.size() == 2);
		final ILElement firstAddedChild = children.get(0);
		assertTrue(firstAddedChild.equals(firstChild));
		final ILElement secondAddedChild = children.get(1);
		assertTrue(secondAddedChild.equals(secondChild));
	}

}
