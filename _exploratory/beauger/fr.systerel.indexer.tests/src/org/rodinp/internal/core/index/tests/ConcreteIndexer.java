package org.rodinp.internal.core.index.tests;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IRodinIndex;
import org.rodinp.core.tests.basis.NamedElement;

public class ConcreteIndexer implements IIndexer {

	public ConcreteIndexer() {
	}

	public boolean canIndex(IRodinFile file) {
		return true; // TODO make our own test file type
	}

	public void index(IRodinFile file, IRodinIndex index) {
		try { // TODO: make a real indexable file and really find real
			// references

			final NamedElement element = IndexTestsUtil.getNamedElement(file,
					IndexTestsUtil.defaultNamedElementName);
			// final IRodinElement[] fileElems = file
			// .getChildrenOfType(NamedElement.ELEMENT_TYPE);

			// if (fileElems.length == 0
			// || !(fileElems[0] instanceof NamedElement)) {
			if (element == null || !element.exists()) {
				System.err.println("could not index: found\n" + element);
				return;
			}

			IDescriptor descriptor = index.makeDescriptor(
					IndexTestsUtil.defaultNamedElementName, element,
					IndexTestsUtil.elementUniqueId(element));

			descriptor.addOccurrences(IndexTestsUtil.generateReferencesTestSet(
					element, 3));

		} catch (CoreException e) {
			e.printStackTrace();
			assert false;
		}
	}

}
