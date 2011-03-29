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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILFile;
import org.rodinp.core.emf.lightcore.InternalElement;
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
		final ILFile rodinResource = getRodinResource();
		// We get the light model
		final ILElement element = rodinResource.getRoot();

		assertTrue(element instanceof InternalElement);
		assertTrue(((InternalElement) element).isERoot());

		// Then delete the file
		rodinFile.getResource().delete(true, null);
		assertFalse("The rodin file should not exist.", rodinFile.exists());

		// Ensures that the resource contents is empty (i.e. resource is
		// unloaded)
		assertTrue("The resource data should be empty", rodinResource.isEmpty());
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
		final ILFile rodinResource = getRodinResource();
		// We get the light model
		final ILElement rootElement = rodinResource.getRoot();

		final List<? extends ILElement> rootChildren = rootElement.getChildren();
		assertTrue("It should be just one child: NE", rootChildren.size() == 1);

		final ILElement lightNE = rootChildren.get(0);
		final List<IAttributeValue> NEatts = lightNE.getAttributes();
		assertTrue("It should be just one attribute for NE", NEatts.size() == 1);

		final IAttributeValue attVal = NEatts.get(0);
		final Object NEatt = attVal.getValue();
		assertEquals("", NEatt, true);
	}

	/**
	 * Checks that the model is modified after some elements have been added to
	 * the database, and that the elements are well loaded.
	 */
	@Test
	public void afterLoadingTest() throws RodinDBException {
		// first we retreive the resource that has a root (see the first test)
		final ILFile rodinResource = getRodinResource();
		final ILElement root = rodinResource.getRoot();

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
		final List<? extends ILElement> children = root.getChildren();
		assertTrue(children.size() == 1);
		final ILElement child = children.get(0);
		assertTrue(child.getElement().equals(ne));
		final List<? extends ILElement> neChildren = child.getChildren();
		assertTrue(neChildren.size() == 1);
		final ILElement neChild = neChildren.get(0);
		assertTrue(neChild.getElement().equals(ne2));

		final String attribute = neChild
				.getAttribute(AbstractRodinDBTests.fString);
		assertEquals(attribute, attributeString);
		final Boolean attribute2 = neChild
				.getAttribute(AbstractRodinDBTests.fBool);
		assertEquals(attribute2, true);
	}

}
