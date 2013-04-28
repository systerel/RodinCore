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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.rodinp.core.tests.AbstractRodinDBTests.fBool;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILFile;
import org.rodinp.core.emf.lightcore.adapters.dboperations.OperationProcessor;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;
import org.rodinp.core.tests.basis.NamedElement;

/**
 * Checks that modifications in the light model are transposed in the Rodin
 * model, and vice versa.
 */
public class ModificationTests extends AbstractRodinEMFCoreTest {

	/**
	 * Tests the suppression of an attribute for an element in the Rodin
	 * Database is suppressed in the light model
	 */
	@Test
	public void deleteAnAttribute() throws RodinDBException,
			InterruptedException {
		// we create elements, and add one attribute to the first element
		// beneath the root
		final NamedElement ne = getNamedElement(rodinFile.getRoot(), "NE");
		final IAttributeValue v1 = fBool.makeValue(true);
		ne.setAttributeValue(v1, null);

		// we get the resource
		final ILFile rodinResource = getRodinResource();
		final ILElement root = rodinResource.getRoot();

		// there is just one child ne for the root
		final List<? extends ILElement> children = root.getChildren();
		assertTrue(children.size() == 1);
		final ILElement neLight = children.get(0);
		final Boolean a = neLight.getAttribute(fBool);
		assertTrue(a.equals(true));
		ne.removeAttribute(fBool, null);

		((IRodinFile) ne.getRodinFile()).save(null, true);
		OperationProcessor.waitUpToDate();

		assertTrue(ne.getAttributeTypes().length == 0);
		assertTrue(neLight.getAttribute(fBool) == null);
	}

	/**
	 * Tests that an attribute that has been updated in the database is also
	 * updated in the light model
	 */
	@Test
	public void modifyRodinAttribute() throws RodinDBException,
			InterruptedException {
		// we get the resource (empty)
		final ILFile rodinResource = getRodinResource();
		// we get the root element of the light model
		final ILElement root = rodinResource.getRoot();
		
		// we create elements, and add one attribute to the first element
		// beneath the root
		final NamedElement ne = getNamedElement(rodinFile.getRoot(), "NE");
		final IAttributeValue v1 = fBool.makeValue(true);
		ne.setAttributeValue(v1, null);

		((IRodinFile) ne.getRodinFile()).save(null, true);
		OperationProcessor.waitUpToDate();
		
		// we search for NE child in the Light model
		// it has been created by the database delta listener
		final ILElement neLightElement = root.getChildren().get(0);
		final Boolean a = neLightElement.getAttribute(fBool);
		// we check that there is just one attribute set for this element
		assertTrue(a.equals(true));

		final IAttributeValue v2 = fBool.makeValue(false);
		ne.setAttributeValue(v2, null);

		((IRodinFile) ne.getRodinFile()).save(null, true);
		OperationProcessor.waitUpToDate();
		
		final Boolean a2 = neLightElement.getAttribute(fBool);
		assertTrue(a2.equals(false));
	}

	/**
	 * Checks that modifying the order of sub elements in database is
	 * transparent for light model elements.
	 */
	@Test
	public void modifyElementOrder() throws RodinDBException,
			InterruptedException {
		final ILFile rodinResource = getRodinResource();
		final IInternalElement rodinRoot = rodinFile.getRoot();
		// we create elements, and add one attribute to the first element
		// beneath the root
		final NamedElement ne = getNamedElement(rodinRoot, "NE1");
		final NamedElement ne2 = getNamedElement(rodinRoot, "NE2");
		final NamedElement ne3 = getNamedElement(rodinRoot, "NE3");
		final NamedElement[] ordered = { ne, ne2, ne3 };
		assertArrayEquals(ordered, rodinRoot.getChildren());
		OperationProcessor.waitUpToDate();

		// we get the root element of the light model
		final ILElement root = rodinResource.getRoot();
		
		assertArrayEquals(ordered, getIRodinElementChildren(root));

		// modify the ordering in the database
		ne2.move(rodinRoot, ne, null, false, null);

		((IRodinFile) ne.getRodinFile()).save(null, true);
		OperationProcessor.waitUpToDate();

		final NamedElement[] ordered2 = { ne2, ne, ne3 };
		assertArrayEquals(ordered2, rodinRoot.getChildren());
		assertArrayEquals(ordered2, getIRodinElementChildren(root));
	}

	@Test
	public void testGetChildPosition() throws RodinDBException,
			InterruptedException {
		final ILFile rodinResource = getRodinResource();
		final IInternalElement rodinRoot = rodinFile.getRoot();
		// we create elements, and add one attribute to the first element
		// beneath the root
		final NamedElement ne = getNamedElement(rodinRoot, "NE1");
		final NamedElement ne2 = getNamedElement(rodinRoot, "NE2");
		final NamedElement ne3 = getNamedElement(rodinRoot, "NE3");
		final NamedElement[] ordered = { ne, ne2, ne3 };
		assertArrayEquals(ordered, rodinRoot.getChildren());

		OperationProcessor.waitUpToDate();

		// we get the root element of the light model
		final ILElement root = rodinResource.getRoot();
		final ILElement lne = SynchroUtils.findElement(ne, root);
		assertEquals(lne.getElement(), ne);
		
		final ILElement lne2 = SynchroUtils.findElement(ne2, root);
		assertEquals(lne2.getElement(), ne2);
		
		final ILElement lne3 = SynchroUtils.findElement(ne3, root);
		assertEquals(lne3.getElement(), ne3);
		
		final int lnePos = root.getChildPosition(lne);
		Assert.assertTrue(lnePos == 0);
		final int lne2Pos = root.getChildPosition(lne2);
		Assert.assertTrue(lne2Pos == 1);
		final int lne3Pos = root.getChildPosition(lne3);
		Assert.assertTrue(lne3Pos == 2);
	}

	private IRodinElement[] getIRodinElementChildren(ILElement root) {
		final List<? extends ILElement> eChildren = root.getChildren();
		final IRodinElement[] lightChildren = new IRodinElement[eChildren
				.size()];
		int i = 0;
		for (ILElement child : eChildren) {
			lightChildren[i] = child.getElement();
			i++;
		}
		return lightChildren;

	}

}
