/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.indexer;

import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.core.indexer.IIndexingBridge;

public class FakeExceptionIndexer implements IIndexer {

	private static final String ID = PLUGIN_ID + ".indexer.fakeExceptionIndexer";
	
	private static final IRodinFile[] NO_FILES = new IRodinFile[0];

	public IRodinFile[] getDependencies(IInternalElement root) {
		return NO_FILES;
	}

	public String getId() {
		return ID;
	}

	public boolean index(IIndexingBridge bridge) {
		throw new NullPointerException();
	}

}
