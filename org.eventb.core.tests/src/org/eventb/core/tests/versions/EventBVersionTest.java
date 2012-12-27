/*******************************************************************************
 * Copyright (c) 2008, 2012 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.versions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.tests.BuilderTest;
import org.rodinp.core.IConversionResult;
import org.rodinp.core.IConversionResult.IEntry;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinMarkerUtil;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class EventBVersionTest extends BuilderTest {

	protected static final String ORG_EVENTB_CORE_FWD = "org.eventb.core.fwd";

	protected static <T> T assertSingleGet(final T[] objs) {
		assertEquals(1, objs.length);
		return objs[0];
	}

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

}
