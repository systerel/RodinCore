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
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCMachineFile;
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
public class TestEventBVersion_1 extends BuilderTest {

	private static final String ORG_EVENTB_CORE_FWD = "org.eventb.core.fwd";

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
	
	/**
	 * contexts of version 0 are updated to contexts of version 1
	 */
	public void testVersion_01_context() throws Exception {
		String contents = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<org.eventb.core.contextFile>\n" +
			"<org.eventb.core.extendsContext name=\"x1177\" org.eventb.core.target=\"abs\"/>\n" +
			"<org.eventb.core.carrierSet name=\"x1178\" org.eventb.core.identifier=\"S\"/>\n" +
			"<org.eventb.core.constant name=\"x1179\" org.eventb.core.identifier=\"C\"/>\n" +
			"<org.eventb.core.axiom name=\"x1180\" org.eventb.core.label=\"A\" org.eventb.core.predicate=\"C∈S\"/>\n" +
			"<org.eventb.core.theorem name=\"x1181\" org.eventb.core.label=\"T\" org.eventb.core.predicate=\"⊤\"/>\n" +
			"</org.eventb.core.contextFile>";
		String name = "ctx.buc";
		createFile(name, contents);
		
		IContextFile file = (IContextFile) rodinProject.getRodinFile(name);
		
		convert(file);
		
		String config = file.getConfiguration();
		
		assertEquals("wrong configuration", ORG_EVENTB_CORE_FWD, config);
		
	}

	/**
	 * machines of version 0 are updated to machines of version 1
	 */
	public void testVersion_02_machine() throws Exception {
		String contents = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<org.eventb.core.machineFile>" +
			"<org.eventb.core.event name=\"x782\" org.eventb.core.convergence=\"0\" org.eventb.core.inherited=\"false\" org.eventb.core.label=\"INITIALISATION\"/>" +
			"<org.eventb.core.event name=\"x783\" org.eventb.core.convergence=\"0\" org.eventb.core.inherited=\"false\" org.eventb.core.label=\"e\">" +
			"<org.eventb.core.variable name=\"x784\" org.eventb.core.identifier=\"a\"/>" +
			"<org.eventb.core.guard name=\"x785\" org.eventb.core.label=\"G\" org.eventb.core.predicate=\"a∈ℤ\"/>" +
			"</org.eventb.core.event>" +
			"</org.eventb.core.machineFile>";
		String name = "mch.bum";
		createFile(name, contents);
		
		IMachineFile file = (IMachineFile) rodinProject.getRodinFile(name);
		
		convert(file);
		
		String config = file.getConfiguration();
		
		assertEquals("wrong configuration", ORG_EVENTB_CORE_FWD, config);
		
	}

	private void convert(IRodinFile file) throws RodinDBException {
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
	
	/**
	 * SC contexts are updated to contexts of version 1;
	 * the new attribute is added
	 */
	public void testVersion_03_contextSC() throws Exception {
		IContextFile file = createContext("ctx");
		file.setConfiguration(ORG_EVENTB_CORE_FWD, null);
		file.save(null, true);
		
		runBuilder();
		
		ISCContextFile scFile = file.getSCContextFile();
		
		assertEquals("attribute missing in SC context", ORG_EVENTB_CORE_FWD, scFile.getConfiguration());
	}

	
	/**
	 * SC machines are updated to machines of version 1;
	 * the new attribute is added
	 */
	public void testVersion_04_machineSC() throws Exception {
		IMachineFile file = createMachine("mch");
		file.setConfiguration(ORG_EVENTB_CORE_FWD, null);
		file.save(null, true);
		
		runBuilder();
		
		ISCMachineFile scFile = file.getSCMachineFile();
		
		assertEquals("attribute missing in SC machine", ORG_EVENTB_CORE_FWD, scFile.getConfiguration());
	}

}
