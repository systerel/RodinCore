/*******************************************************************************
 * Copyright (c) 2008-2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.rodinp.core.tests.indexer.persistence;

import java.util.ArrayList;
import java.util.List;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.core.indexer.IIndexingBridge;

/**
 * @author Nicolas Beauger
 *
 */
public class FakeOrderRecordIndexer implements IIndexer {

	private static final IRodinFile[] NO_FILE = new IRodinFile[0];
	
	private final List<IRodinFile> indexedFiles;
	
	public FakeOrderRecordIndexer() {
		this.indexedFiles = new ArrayList<IRodinFile>();
	}
	
	public IRodinFile[] getDependencies(IInternalElement root) {
		return NO_FILE;
	}

	public String getId() {
		return "org.rodinp.core.tests.indexer.persistence.orderrecord";
	}

	public boolean index(IIndexingBridge bridge) {
		indexedFiles.add(bridge.getRootToIndex().getRodinFile());
		return true;
	}

	public List<IRodinFile> getIndexedFiles() {
		return indexedFiles;
	}
}
