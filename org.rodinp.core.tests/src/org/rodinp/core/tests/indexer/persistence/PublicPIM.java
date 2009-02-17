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

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.internal.core.indexer.sort.TotalOrder;
import org.rodinp.internal.core.indexer.tables.ExportTable;
import org.rodinp.internal.core.indexer.tables.FileTable;
import org.rodinp.internal.core.indexer.tables.NameTable;
import org.rodinp.internal.core.indexer.tables.RodinIndex;

/**
 * @author Nicolas Beauger
 *
 */
public class PublicPIM {
	public final IRodinProject project;

	public final RodinIndex index;

	public final FileTable fileTable;

	public final NameTable nameTable;

	public final ExportTable exportTable;

	public final TotalOrder<IRodinFile> order;

	public PublicPIM(IRodinProject project) {
		this.project = project;
		this.index = new RodinIndex();
		this.fileTable = new FileTable();
		this.nameTable = new NameTable();
		this.exportTable = new ExportTable();
		this.order = new TotalOrder<IRodinFile>();
	}

}
