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

package org.rodinp.internal.core.indexer.persistence;

import java.util.List;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.internal.core.indexer.Descriptor;
import org.rodinp.internal.core.indexer.tables.IExportTable;

/**
 * @author Nicolas Beauger
 *
 */
public class PersistentPIM {

	private final IRodinProject project;
	private final Descriptor[] descriptors;
	private final IExportTable exportTable;
	private final PersistentTotalOrder<IRodinFile> order;
	private final List<IRodinFile> unprocessedFiles;

	public PersistentPIM(IRodinProject project, Descriptor[] descriptors,
			IExportTable exportTable, PersistentTotalOrder<IRodinFile> order,
			List<IRodinFile> unprocessedFiles) {
		this.project = project;
		this.descriptors = descriptors;
		this.exportTable = exportTable;
		this.order = order;
		this.unprocessedFiles = unprocessedFiles;
	}
	
	public IRodinProject getProject() {
		return project;
	}

	public Descriptor[] getDescriptors() {
		return descriptors;
	}

	public IExportTable getExportTable() {
		return exportTable;
	}

	public PersistentTotalOrder<IRodinFile> getOrder() {
		return order;
	}
	
	public List<IRodinFile> getUnprocessedFiles() {
		return unprocessedFiles;
	}
}
