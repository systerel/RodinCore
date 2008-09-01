package org.rodinp.internal.core.index.tests;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IndexingFacade;
import org.rodinp.core.index.Occurrence;
import org.rodinp.core.tests.basis.NamedElement;

public class ConcreteIndexer implements IIndexer {

	public ConcreteIndexer() {
		// Nothing to do
	}

	public boolean canIndex(IRodinFile file) {
		return true; // TODO make our own test file type
	}

	public void index(IRodinFile file, IndexingFacade index) {
		try { // TODO: make a real indexable file and really find real
			// references

			// final NamedElement element =
			// IndexTestsUtil.createNamedElement(file,
			// IndexTestsUtil.defaultName);

			final IRodinElement[] fileElems = file
					.getChildrenOfType(NamedElement.ELEMENT_TYPE);

			int elementCount = 0;
			for (IRodinElement element : fileElems) {
				NamedElement namedElt = (NamedElement) element;
				final String name = namedElt.getElementName() + elementCount++;
				final Occurrence[] occurrences = IndexTestsUtil
						.generateOccurrencesTestSet(namedElt, 3);
				IndexTestsUtil.addOccurrences(namedElt,
						name, occurrences, index);
			}
			// if (fileElems.length == 0
			// || !(fileElems[0] instanceof NamedElement)) {
			//
			// if (element == null || !element.exists()) {
			// System.err.println("could not index: found\n" + element);
			// return;
			// }
			// }
			// IndexTestsUtil.addOccurrences(element,
			// IndexTestsUtil.defaultName, IndexTestsUtil
			// .generateReferencesTestSet(element, 3), index);

		} catch (CoreException e) {
			e.printStackTrace();
			assert false;
		}
	}

}
