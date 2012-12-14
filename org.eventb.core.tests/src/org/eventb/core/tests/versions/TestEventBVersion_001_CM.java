/*******************************************************************************
 * Copyright (c) 2009 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soton - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.versions;

import static junit.framework.Assert.assertEquals;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCMachineRoot;
import org.junit.Test;
import org.rodinp.core.IRodinFile;

/**
 * Version 1 of the machine and context database introduces configurations
 * 
 * @author Stefan Hallerstede
 *
 */
public class TestEventBVersion_001_CM extends EventBVersionTest {

	/**
	 * contexts of version 0 are updated to contexts of version 1
	 */
	@Test
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
		
		IRodinFile file = rodinProject.getRodinFile(name);
		IContextRoot root = (IContextRoot)file.getRoot();
		String config = root.getConfiguration();
		
		assertEquals("wrong configuration", ORG_EVENTB_CORE_FWD, config);
		
	}

	/**
	 * machines of version 0 are updated to machines of version 1
	 */
	@Test
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
		
		IRodinFile file = rodinProject.getRodinFile(name);
		IMachineRoot root = (IMachineRoot) file.getRoot();
		String config = root.getConfiguration();
		
		assertEquals("wrong configuration", ORG_EVENTB_CORE_FWD, config);
		
	}

	/**
	 * SC contexts are updated to contexts of version 1;
	 * the new attribute is added
	 */
	@Test
	public void testVersion_03_contextSC() throws Exception {
		IContextRoot root = createContext("ctx");
		root.setConfiguration(ORG_EVENTB_CORE_FWD, null);
		saveRodinFileOf(root);
		
		runBuilder();
		
		ISCContextRoot scRoot = root.getSCContextRoot();
		
		assertEquals("attribute missing in SC context", ORG_EVENTB_CORE_FWD, scRoot.getConfiguration());
	}

	
	/**
	 * SC machines are updated to machines of version 1;
	 * the new attribute is added
	 */
	@Test
	public void testVersion_04_machineSC() throws Exception {
		IMachineRoot root = createMachine("mch");
		root.setConfiguration(ORG_EVENTB_CORE_FWD, null);
		saveRodinFileOf(root);
		
		runBuilder();
		
		ISCMachineRoot scRoot = root.getSCMachineRoot();
		
		assertEquals("attribute missing in SC machine", ORG_EVENTB_CORE_FWD, scRoot.getConfiguration());
	}

}
