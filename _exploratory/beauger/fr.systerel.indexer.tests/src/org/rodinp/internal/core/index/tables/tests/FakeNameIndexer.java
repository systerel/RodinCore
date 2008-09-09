package org.rodinp.internal.core.index.tables.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IndexingFacade;
import org.rodinp.core.index.Occurrence;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.tests.IndexTestsUtil;

public class FakeNameIndexer implements IIndexer {

	private static final boolean DEBUG = false;

	private final String[] names;
	private final int numberEach;
	private final Map<String, Set<IInternalElement>> indexedElements;

	public FakeNameIndexer(int numberEach, String... names) {
		TestCase.assertTrue("numberEach is not positive", numberEach > 0);

		this.numberEach = numberEach;
		this.names = names;
		indexedElements = new HashMap<String, Set<IInternalElement>>();
	}

	public boolean canIndex(IRodinFile rodinFile) {
		return true;
	}

	public void index(IRodinFile rodinFile, IndexingFacade index) {
		indexedElements.clear();
		try {
			rodinFile.clear(true, null);
			for (String name : names) {
				final HashSet<IInternalElement> set = new HashSet<IInternalElement>();
				indexedElements.put(name, set);
				for (int i = 0; i < numberEach; i++) {
					final NamedElement element = IndexTestsUtil
							.createNamedElement(rodinFile, name + "_DB" + i);
					final Occurrence occurrence = IndexTestsUtil
							.createDefaultOccurrence(rodinFile, this);
					index.addOccurrence(element, name, occurrence);
					set.add(element);
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

	public IRodinFile[] getDependencies(IRodinFile file) {
		return new IRodinFile[0];
	}

	public Map<IInternalElement, String> getExports(IRodinFile file) {
		return new HashMap<IInternalElement, String>();
	}
	
}
