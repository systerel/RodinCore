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
package org.rodinp.core.tests.indexer.tables;

import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;
import static org.rodinp.core.tests.util.IndexTestsUtil.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.core.location.IInternalLocation;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.util.IndexTestsUtil;

public class FakeNameIndexer implements IIndexer {

	private static final boolean DEBUG = false;

	private static final String ID = PLUGIN_ID + ".indexer.fakeNameIndexer";

	private final String[] names;
	private final int numberEach;
	private final Map<String, Set<IDeclaration>> indexedElements;

	public FakeNameIndexer(int numberEach, String... names) {
		TestCase.assertTrue("numberEach is not positive", numberEach > 0);

		this.numberEach = numberEach;
		this.names = names;
		indexedElements = new HashMap<String, Set<IDeclaration>>();
	}

	public boolean index(IIndexingBridge bridge) {
		indexedElements.clear();

		IInternalElement fileRoot = bridge.getRootToIndex();
		try {
			fileRoot.clear(true, null);
			for (String name : names) {
				final NamedElement elt = createNamedElement(fileRoot, name);
				final IDeclaration declaration = bridge.declare(elt, name);
				final HashSet<IDeclaration> set =
						new HashSet<IDeclaration>();
				indexedElements.put(name, set);
				set.add(declaration);
				for (int i = 0; i < numberEach; i++) {
					final NamedElement element =
							IndexTestsUtil.createNamedElement(fileRoot, name
									+ "_DB"
									+ i);
					final IInternalLocation loc =
							RodinCore.getInternalLocation(element);
					bridge.addOccurrence(declaration, TEST_KIND, loc);
					if (DEBUG) {
						System.out.println(name
								+ ": "
								+ element.getElementName());
					}
				}
			}
			return true;
		} catch (CoreException e) {
			e.printStackTrace();
			TestCase.fail("FakeNameIndexer unable to index "
					+ fileRoot.getRodinFile().getBareName()
					+ "\nreason: "
					+ e.getLocalizedMessage());
			return false;
		}
	}

	public Set<IDeclaration> getIndexedElements(String name) {
		Set<IDeclaration> elements = indexedElements.get(name);
		if (elements == null || elements.size() == 0) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(elements);
	}

	public IRodinFile[] getDependencies(IInternalElement root) {
		return new IRodinFile[0];
	}

	public String getId() {
		return ID;
	}

}
