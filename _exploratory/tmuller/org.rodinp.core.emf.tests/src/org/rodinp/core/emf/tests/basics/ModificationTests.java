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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.rodinp.core.tests.AbstractRodinDBTests.fBool;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Test;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.Attribute;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.tests.basis.NamedElement;

/**
 * Checks that modifications in the light model are transposed in the Rodin
 * model, and vice versa.
 */
public class ModificationTests extends AbstractRodinEMFCoreTest {

	/**
	 * Checks that an element which is suppressed from the light model is
	 * actually suppressed from the database
	 */
	@Test
	public void deleteAnElement() throws RodinDBException {

		final Resource rodinResource = getRodinResource();

		final LightElement root = (LightElement) rodinResource.getContents()
				.get(0);

		final NamedElement ne = getNamedElement(rodinFile.getRoot(), "NE");

		final EList<LightElement> children = root.getEChildren();
		assertTrue(children.size() == 1);
		final LightElement child = children.get(0); // ne
		assertTrue(child.getERodinElement().equals(ne));

		// we delete ne
		EcoreUtil.remove(child);
		assertTrue(children.isEmpty());
		// ensures that the element ne has been removed from the database
		assertFalse(ne.exists());
	}

	/**
	 * Tests the suppression of an attribute for an element in the Rodin
	 * Database is suppressed in the light model
	 */
	@Test
	public void deleteAnAttribute() throws RodinDBException {
		// we create elements, and add one attribute to the first element
		// beneath the root
		final NamedElement ne = getNamedElement(rodinFile.getRoot(), "NE");
		final IAttributeValue v1 = fBool.makeValue(true);
		ne.setAttributeValue(v1, null);

		// we get the resource
		final Resource rodinResource = getRodinResource();
		final LightElement root = (LightElement) rodinResource.getContents()
				.get(0);
		// there is just one child ne for the root
		final EList<LightElement> children = root.getEChildren();
		assertTrue(children.size() == 1);
		final LightElement neLight = children.get(0);
		final Attribute a = neLight.getEAttributes().get(fBool.getId());
		assertTrue(a.getValue().equals(true));
		ne.removeAttribute(fBool, null);
		assertTrue(ne.getAttributeTypes().length == 0);
		assertTrue(neLight.getEAttributes().get(fBool.getId()) == null);
	}

	/**
	 * Checks that a simple modification of an attribute in the EMF model is
	 * updated in the rodin database.
	 */
	@Test
	public void modifyLightAttribute() throws RodinDBException {
		// we get the resource (empty)
		final Resource rodinResource = getRodinResource();
		// we get the root element of the light model
		final LightElement root = (LightElement) rodinResource.getContents()
				.get(0);

		// we create elements, and add one attribute to the first element
		// beneath the root
		final NamedElement ne = getNamedElement(rodinFile.getRoot(), "NE");
		final IAttributeValue v1 = fBool.makeValue(true);
		ne.setAttributeValue(v1, null);

		// we search for NE child in the Light model
		// it has been created by the database delta listener
		final LightElement neLightElement = root.getEChildren().get(0);

		final EMap<String, Attribute> a = neLightElement.getEAttributes();
		// we check that there is just one attribute set for this element
		assertTrue(a.values().size() == 1);

		// we modify the value of the boolean attribute in the light model
		final Attribute lightV1 = a.get(fBool.getId());
		lightV1.setValue(false);

		// we check that the value was updated to false in the database
		final boolean attributeValue = ne.getAttributeValue(fBool);
		assertTrue(attributeValue == false);
	}

	/**
	 * Tests that an attribute that has been updated in the database is also
	 * updated in the light model
	 */
	@Test
	public void modifyRodinAttribute() throws RodinDBException {
		// we get the resource (empty)
		final Resource rodinResource = getRodinResource();
		// we get the root element of the light model
		final LightElement root = (LightElement) rodinResource.getContents()
				.get(0);
		// we create elements, and add one attribute to the first element
		// beneath the root
		final NamedElement ne = getNamedElement(rodinFile.getRoot(), "NE");
		final IAttributeValue v1 = fBool.makeValue(true);
		ne.setAttributeValue(v1, null);

		// we search for NE child in the Light model
		// it has been created by the database delta listener
		final LightElement neLightElement = root.getEChildren().get(0);
		final EMap<String, Attribute> a = neLightElement.getEAttributes();
		final String boolId = fBool.getId();
		// we check that there is just one attribute set for this element
		final Attribute a1 = a.get(boolId);
		assertTrue(a1.getValue().equals(true));

		final IAttributeValue v2 = fBool.makeValue(false);
		ne.setAttributeValue(v2, null);

		final Attribute a2 = a.get(boolId);
		assertTrue(a2.getValue().equals(false));
	}

	/**
	 * Checks that modifying the order of sub elements in database is
	 * transparent for light model elements.
	 */
	@Test
	public void modifyElementOrder() throws RodinDBException {
		final Resource rodinResource = getRodinResource();
		final IInternalElement rodinRoot = rodinFile.getRoot();
		// we create elements, and add one attribute to the first element
		// beneath the root
		final NamedElement ne = getNamedElement(rodinRoot, "NE1");
		final NamedElement ne2 = getNamedElement(rodinRoot, "NE2");
		final NamedElement ne3 = getNamedElement(rodinRoot, "NE3");
		final NamedElement[] ordered = { ne, ne2, ne3 };
		assertArrayEquals(ordered, rodinRoot.getChildren());

		// we get the root element of the light model
		final LightElement root = (LightElement) rodinResource.getContents()
				.get(0);
		assertArrayEquals(ordered, getIRodinElementChildren(root));

		// modify the ordering in the database
		ne2.move(rodinRoot, ne, null, false, null);

		final NamedElement[] ordered2 = { ne2, ne, ne3 };
		assertArrayEquals(ordered2, rodinRoot.getChildren());
		assertArrayEquals(ordered2, getIRodinElementChildren(root));
	}

	/**
	 * Checks that modifying the order of sub element in the light model is
	 * transparent to the rodin database
	 */
	@Test
	public void modifyLightElementOrder() throws RodinDBException {
		final Resource rodinResource = getRodinResource();
		final IInternalElement rodinRoot = rodinFile.getRoot();
		// we create elements, and add one attribute to the first element
		// beneath the root
		final NamedElement ne = getNamedElement(rodinRoot, "NE1");
		final NamedElement ne2 = getNamedElement(rodinRoot, "NE2");
		final NamedElement ne3 = getNamedElement(rodinRoot, "NE3");
		final NamedElement[] ordered = { ne, ne2, ne3 };
		assertArrayEquals(ordered, rodinRoot.getChildren());

		// we get the root element of the light model
		final LightElement root = (LightElement) rodinResource.getContents()
				.get(0);
		assertArrayEquals(ordered, getIRodinElementChildren(root));
		final EList<LightElement> children = root.getEChildren();
		// move ne2 to the first position
		children.move(0, children.get(1));
		final NamedElement[] ordered2 = { ne2, ne, ne3 };
		assertArrayEquals(ordered2, getIRodinElementChildren(root));
		assertArrayEquals(ordered2, rodinRoot.getChildren());

	}

	private IRodinElement[] getIRodinElementChildren(LightElement element) {
		final EList<LightElement> eChildren = element.getEChildren();
		final IRodinElement[] lightChildren = new IRodinElement[eChildren
				.size()];
		int i = 0;
		for (LightElement child : eChildren) {
			lightChildren[i] = (IRodinElement) child.getERodinElement();
			i++;
		}
		return lightChildren;

	}

}
