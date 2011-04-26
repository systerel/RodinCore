/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.tactics.perf;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eventb.core.tests.BuilderTest;

/**
 * 
 * @author Laurent Voisin
 */
public class FirstTest extends BuilderTest {

	@Override
	protected URL getProjectsURL() {
		return Platform.getBundle("fr.systerel.tactics.perf").getEntry(
				"projects");
	}

	public void test1() throws Exception {
		importProjectFiles(rodinProject.getProject(), "t1");
		assertEquals(1, rodinProject.getChildren().length);
	}

}
