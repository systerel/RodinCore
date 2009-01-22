/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.internal.core.indexer.tables.NameTable;
import org.rodinp.internal.core.indexer.tables.RodinIndex;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexQuery implements IIndexQuery {

	private static final IOccurrence[] EMPTY_OCCURRENCES = new IOccurrence[] {};

	public IDeclaration getDeclaration(IInternalElement element)
			throws InterruptedException {
		final IRodinProject project = element.getRodinProject();
		final RodinIndex index = IndexManager.getDefault().getIndex(project);

		final Descriptor descriptor = index.getDescriptor(element);

		if (descriptor == null) {
			return null;
		}
		return descriptor.getDeclaration();
	}

	public IOccurrence[] getOccurrences(IDeclaration declaration)
			throws InterruptedException {
		final IInternalElement element = declaration.getElement();
		final IRodinProject project = element.getRodinProject();
		final RodinIndex index = IndexManager.getDefault().getIndex(project);

		final Descriptor descriptor = index.getDescriptor(element);

		if (descriptor == null) {
			return EMPTY_OCCURRENCES;
		}
		return descriptor.getOccurrences();
	}

	public IDeclaration[] getDeclarations(IRodinProject project, String name)
			throws InterruptedException {
		final NameTable nameTable =
				IndexManager.getDefault().getNameTable(project);

		return nameTable.getDeclarations(name);
	}

	public void waitUpToDate() throws InterruptedException {
		IndexManager.getDefault().waitUpToDate();
	}

}
