/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.tests.basics;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the time needed to create a resource.
 * 
 * To run this test, store a big model file on your disk, enter its file path
 * into the Java system property "filePath" and enable this test.
 */
@Ignore("Peformance test")
public class ModelLoadTimeTests extends AbstractRodinEMFCoreTest {

	/**
	 * The file BigRes.ert contains 17 000 elements with a 2 level hierarchy.
	 */
	@Test
	public void testLoadABigModel() throws Exception {
		final ResourceSet resourceSet = new ResourceSetImpl();
		// The file that contains a thousand simple elements

		final File toLoad = getFile();
		importFiles(rodinProject.getProject(), toLoad, false);
		final String projectName = rodinProject.getElementName();
		final URI uri = URI.createPlatformResourceURI(projectName + "/"
				+ toLoad.getName(), true);
		// The time of the test tells the time needed to load the light model
		final long start = System.currentTimeMillis();
		@SuppressWarnings("unused")
		final Resource resource = resourceSet.getResource(uri, true);
//		final LightElement root = (LightElement) resource.getContents().get(0);
//		final LightElement child = root.getChildren().get(0);
//		final Attribute attribute = child.getAttributes().get("org.rodinp.core.tests.fString");
//		attribute.setValue("NEW VALUE!");
		final long timeToLoad = System.currentTimeMillis() - start;
		System.out.println("Loading the big model took:" + timeToLoad + " ms.");
	}

	protected static void importFiles(IProject project, File file,
			boolean isRoot) throws Exception {
		if (file.isFile()) {
			InputStream is = new FileInputStream(file);
			final String name = file.getName();
			final IFile target = project.getFile(name);
			if (target.exists()) { // true for ".project" if present
				target.delete(true, null);
			}
			target.create(is, false, null);
		}
	}

	public static File getFile() throws Exception {
		final String filePath = System.getProperty("filePath");
		assertNotNull(filePath);
		final java.net.URI uri = URIUtil.fromString(filePath);
		final java.net.URI absURI = URIUtil.makeAbsolute(uri,
				URIUtil.fromString("file://"));
		final File file = URIUtil.toFile(absURI);
		assertTrue(file.exists());
		return file;
	}

}
