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
package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IInternalLocation;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.tables.RodinIndex;

public class FakeIndexer implements IIndexer {

	private static final String ID = "fr.systerel.indexer.tests.fakeindexer";

	private static final IRodinFile[] NO_FILES = new IRodinFile[0];

	protected final RodinIndex localIndex;

	public FakeIndexer(RodinIndex index) {
		this.localIndex = index;
	}

	public IRodinFile[] getDependencies(IInternalElement root) {
		return NO_FILES;
	}

	public boolean index(IIndexingToolkit index) {
		final IRodinFile file = index.getRootToIndex().getRodinFile();
		final IDeclaration[] imports = index.getImports();

		for (Descriptor desc : localIndex.getDescriptors()) {
			final IInternalElement element = desc.getDeclaration().getElement();
			final IDeclaration declaration;
			if (element.getRodinFile().equals(file)) {
				declaration = index.declare(element, desc.getDeclaration()
						.getName());
			} else {
				declaration = findDeclaration(element, imports);
			}
			assert declaration != null;
			for (IOccurrence occ : desc.getOccurrences()) {
				final IInternalLocation location = occ.getLocation();
				if (file.equals(location.getRodinFile())) {
					index.addOccurrence(declaration, occ.getKind(), location);
				}
			}
		}
		return true;
	}

	private static IDeclaration findDeclaration(IInternalElement element,
			IDeclaration[] declarations) {
		for (IDeclaration declaration : declarations) {
			if (declaration.getElement().equals(element)) {
				return declaration;
			}
		}
		return null;
	}

	// private boolean isInFile(IRodinFile file, IInternalLocation location) {
	// final IRodinElement locElem = location.getElement();
	// final IRodinFile locElemFile;
	// if (locElem instanceof IRodinFile) {
	// locElemFile = (IRodinFile) locElem;
	// } else if (locElem instanceof IInternalElement) {
	// locElemFile = ((IInternalElement) locElem).getRodinFile();
	// } else {
	// return false;
	// }
	// return locElemFile.equals(file);
	// }

	public String getId() {
		return ID;
	}

}
