/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexRequester;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.internal.core.index.tables.NameTable;
import org.rodinp.internal.core.index.tables.RodinIndex;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexRequester implements IIndexRequester {

	private static final String EMPTY_STRING = "";

	private static final IOccurrence[] EMPTY_OCCURRENCES = new IOccurrence[] {};

	public String getIndexName(IInternalElement element) {
		final IRodinProject project = element.getRodinProject();
		final RodinIndex index = IndexManager.getDefault().getIndex(project);

		final Descriptor descriptor = index.getDescriptor(element);

		if (descriptor == null) {
			return EMPTY_STRING;
		}
		return descriptor.getName();
	}

	public IOccurrence[] getOccurrences(IInternalElement element) {
		final IRodinProject project = element.getRodinProject();
		final RodinIndex index = IndexManager.getDefault().getIndex(project);

		final Descriptor descriptor = index.getDescriptor(element);

		if (descriptor == null) {
			return EMPTY_OCCURRENCES;
		}
		return descriptor.getOccurrences();
	}

	public IInternalElement[] getElements(IRodinProject project,
			String name) {
		final NameTable nameTable = IndexManager.getDefault().getNameTable(
				project);

		return nameTable.getElements(name);
	}

	public boolean isUpToDate() {
		return IndexManager.getDefault().isUpToDate();
	}

}
