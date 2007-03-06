/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IContextFile;
import org.eventb.core.IPOFile;
import org.eventb.core.tests.GenericContextTest;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class GenericContextPOTest extends GenericContextTest<EventBPOTest> implements IGenericPOTest<IContextFile> {

	public GenericContextPOTest(EventBPOTest test) {
		super(test);
	}

	public void addSuper(IContextFile file, String name) throws RodinDBException {
		test.addContextExtends(file, name);
	}

	public IPOFile getPOFile(IContextFile file) throws RodinDBException {
		return file.getPOFile();
	}

}
