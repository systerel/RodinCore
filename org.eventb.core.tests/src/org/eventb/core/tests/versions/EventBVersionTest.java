/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.versions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.tests.BuilderTest;
import org.rodinp.core.IConversionResult;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.core.IConversionResult.IEntry;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class EventBVersionTest extends BuilderTest {

	protected static final String ORG_EVENTB_CORE_FWD = "org.eventb.core.fwd";

	protected void createFile(String fileName, String contents) throws CoreException {
		IProject project = rodinProject.getProject();
		IFile file = project.getFile(fileName);
		InputStream ios = new ByteArrayInputStream(contents.getBytes());
		file.create(ios, true, null);
	}

	protected void hasBuilderMarkers(String name) throws CoreException {
		IRodinFile csc = rodinProject.getRodinFile(name);
		IMarker[] markers = 
			csc.getResource().findMarkers(
					RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER, 
					false, 
					IResource.DEPTH_ZERO);
		assertNotSame("has markers", markers.length, 0);
	}

	protected void hasNotBuilderMarkers(String name) throws CoreException {
		IRodinFile csc = rodinProject.getRodinFile(name);
		IMarker[] markers = 
			csc.getResource().findMarkers(
					RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER, 
					false, 
					IResource.DEPTH_ZERO);
		assertSame("has markers", markers.length, 0);
	}

	protected boolean convSuccess(IConversionResult result) {
		for (IEntry entry : result.getEntries()) {
			if (entry.success())
				continue;
			return false;
		}
		return true;
	}

	protected void convert(IRodinFile file) throws RodinDBException {
		try {
		
			file.getChildren();
			fail("opening the file should have failed");
			
		} catch(RodinDBException e) {
			assertEquals("not a past version", IRodinDBStatusConstants.PAST_VERSION, e.getRodinDBStatus().getCode());
		}
		
		IConversionResult result = RodinCore.convert(rodinProject, true, null);
		
		assertTrue("conversion not succeeded", convSuccess(result));
		
		result.accept(true, false, null);
	}

}
