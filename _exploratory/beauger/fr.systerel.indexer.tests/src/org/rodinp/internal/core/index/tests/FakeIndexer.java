package org.rodinp.internal.core.index.tests;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingFacade;
import org.rodinp.core.tests.basis.NamedElement;

public class FakeIndexer implements IIndexer {

	// TODO better pass the wanted index to the constructor and index with it.
	// private final IRodinIndex index;

	public FakeIndexer() {
		// nothing to do
	}

	// public FakeIndexer(RodinIndex index) {
	// this.index = index;
	// }

	public boolean canIndex(IRodinFile file) {
		return true;
	}

	/**
	 * Calls
	 * {@link IndexTestsUtil#addOccurrencesTestSet(IInternalElement, int, IIndexingFacade)}
	 * for every children of type {@link NamedElement#ELEMENT_TYPE} generating 3
	 * occurrences of each kind.
	 */
	public void index(IRodinFile file, IIndexingFacade index) {
		try {

			final IRodinElement[] fileElems = file
					.getChildrenOfType(NamedElement.ELEMENT_TYPE);

			for (IRodinElement element : fileElems) {
				NamedElement namedElt = (NamedElement) element;
				final String name = namedElt.getElementName();
				index.addDeclaration(namedElt, name);
				IndexTestsUtil
						.addOccurrencesTestSet(namedElt, 3, index);
			}

		} catch (CoreException e) {
			e.printStackTrace();
			assert false;
		}
	}

	public IRodinFile[] getDependencies(IRodinFile file) {
		return new IRodinFile[0];
	}

	public Map<IInternalElement, String> getExports(IRodinFile file) {
		return new HashMap<IInternalElement, String>();
	}

}
