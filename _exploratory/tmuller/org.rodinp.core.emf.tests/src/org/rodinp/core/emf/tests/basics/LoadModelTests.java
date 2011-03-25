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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Test;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.Attribute;
import org.rodinp.core.emf.lightcore.InternalElement;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;

/**
 * Verify that loading and unloading is working.
 * 
 * @author Thomas Muller
 */
public class LoadModelTests extends AbstractRodinEMFCoreTest {

	/**
	 * Tests the loading and deletion of a simple Rodin resource. A root in the
	 * light model should be created, and after deletion, the resource should
	 * have no contents.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws CoreException
	 */
	@Test
	public void loadAResource() throws IOException, InterruptedException,
			CoreException {
		final Resource rodinResource = getRodinResource();
		// We get the light model
		final LightElement element = (LightElement) rodinResource.getContents()
				.get(0);

		assertTrue(element instanceof InternalElement);
		assertTrue(element.isERoot());

		// Then delete the file
		rodinFile.getResource().delete(true, null);
		assertFalse("The rodin file should not exist.", rodinFile.exists());

		// Ensures that the resource contents is empty (i.e. resource is
		// unloaded)
		assertTrue("The resource data should be empty", rodinResource
				.getContents().size() == 0);
	}

	/**
	 * Checking the loading of a more complicated resource.
	 * 
	 * @throws RodinDBException
	 */
	@Test
	public void loadAResourceFull() throws RodinDBException {

		final NamedElement ne = getNamedElement(rodinFile.getRoot(),
				"ANamedElement");
		final NamedElement ne2 = getNamedElement(ne, "ASubNamedElement");
		final IAttributeValue v1 = AbstractRodinDBTests.fBool.makeValue(true);
		ne.setAttributeValue(v1, null);
		final String attributeString = "An attribute string";
		final IAttributeValue v2 = AbstractRodinDBTests.fString
				.makeValue(attributeString);
		ne2.setAttributeValue(v2, null);

		// saves the file
		rodinFile.save(null, true);

		// now checking the loaded resource
		final Resource rodinResource = getRodinResource();
		// We get the light model
		final LightElement rootElement = (LightElement) rodinResource
				.getContents().get(0);

		final EList<LightElement> rootChildren = rootElement.getEChildren();
		assertTrue("It should be just one child: NE", rootChildren.size() == 1);

		final LightElement lightNE = rootChildren.get(0);
		final EMap<String, Attribute> NEatts = lightNE.getEAttributes();
		assertTrue("It should be just one attribute for NE", NEatts.size() == 1);

		final Attribute NEatt = NEatts.get(0).getValue();
		assertEquals("", NEatt.getValue(), true);
	}

	/**
	 * Checks that the model is modified after some elements have been added to
	 * the database, and that the elements are well loaded.
	 */
	@Test
	public void afterLoadingTest() throws RodinDBException {
		// first we retreive the resource that has a root (see the first test)
		final Resource rodinResource = getRodinResource();
		final LightElement root = (LightElement) rodinResource.getContents()
				.get(0);

		// then we create children in the database
		final NamedElement ne = getNamedElement(rodinFile.getRoot(), "NE");
		final NamedElement ne2 = getNamedElement(ne, "NE2");
		final String attributeString = "An attribute string";
		final IAttributeValue v = AbstractRodinDBTests.fString
				.makeValue(attributeString);
		ne2.setAttributeValue(v, null);
		final IAttributeValue v2 = AbstractRodinDBTests.fBool.makeValue(true);
		ne2.setAttributeValue(v2, null);

		// We verify that the elements are created
		final EList<LightElement> children = root.getEChildren();
		assertTrue(children.size() == 1);
		final LightElement child = children.get(0);
		assertTrue(child.getERodinElement().equals(ne));
		final EList<LightElement> neChildren = child.getEChildren();
		assertTrue(neChildren.size() == 1);
		final LightElement neChild = neChildren.get(0);
		assertTrue(neChild.getERodinElement().equals(ne2));

		final Attribute attribute = neChild.getEAttributes().get(
				AbstractRodinDBTests.fString.getId());
		assertEquals(attribute.getValue(), attributeString);
		final Attribute attribute2 = neChild.getEAttributes().get(
				AbstractRodinDBTests.fBool.getId());
		assertEquals(attribute2.getValue(), true);
	}

}
