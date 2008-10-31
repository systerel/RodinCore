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
package org.rodinp.internal.core.index.tables.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.TEST_KIND;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IInternalLocation;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.tests.IndexTestsUtil;

public class FakeNameIndexer implements IIndexer {

	private static final boolean DEBUG = false;
	
	private static final String ID = "fr.systerel.indexer.tests.fakenameindexer";

	private final String[] names;
	private final int numberEach;
	private final Map<String, Set<IInternalElement>> indexedElements;

	public FakeNameIndexer(int numberEach, String... names) {
		TestCase.assertTrue("numberEach is not positive", numberEach > 0);

		this.numberEach = numberEach;
		this.names = names;
		indexedElements = new HashMap<String, Set<IInternalElement>>();
	}

	public void index(IIndexingToolkit index) {
		indexedElements.clear();
		
		IRodinFile rodinFile = index.getRootToIndex().getRodinFile(); 
		try {
			rodinFile.clear(true, null);
			for (String name : names) {
				final NamedElement elt = createNamedElement(
						rodinFile, name);
				final IDeclaration declaration = index.declare(elt, name);
				final HashSet<IInternalElement> set = new HashSet<IInternalElement>();
				indexedElements.put(name, set);
				set.add(elt);
				for (int i = 0; i < numberEach; i++) {
					final NamedElement element = IndexTestsUtil
							.createNamedElement(rodinFile, name + "_DB" + i);
					final IInternalLocation loc = RodinIndexer
							.getInternalLocation(element);
					index.addOccurrence(declaration, TEST_KIND, loc);
					if (DEBUG) {
						System.out.println(name + ": "
								+ element.getElementName());
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
			TestCase.fail("FakeNameIndexer unable to index "
					+ rodinFile.getBareName() + "\nreason: "+e.getLocalizedMessage());
		}
	}

	public IInternalElement[] getIndexedElements(String name) {
		Set<IInternalElement> elements = indexedElements.get(name);
		if (elements == null || elements.size() == 0) {
			return new IInternalElement[0];
		}
		return elements.toArray(new IInternalElement[elements.size()]);
	}

	public IRodinFile[] getDependencies(IInternalElement root) {
		return new IRodinFile[0];
	}

	public String getId() {
		return ID;
	}

}
