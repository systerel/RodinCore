package org.rodinp.internal.core.index.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IRodinIndex;
import org.rodinp.core.index.IndexingFacade;
import org.rodinp.core.index.Occurrence;
import org.rodinp.core.index.OccurrenceKind;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.basis.NamedElement;

public class IndexTestsUtil {

	public static class TestReferenceKind extends OccurrenceKind {
		private static final long serialVersionUID = 9174271655290648041L;

		protected TestReferenceKind(String name) {
			super(name);
		}

		public static final TestReferenceKind TEST_KIND = new TestReferenceKind(
				"Test Kind");
	}

	public static class RefKind1 extends OccurrenceKind {

		private static final long serialVersionUID = -6158077370017655468L;

		protected RefKind1(String name) {
			super(name);
		}

		public static final RefKind1 TEST_KIND_1 = new RefKind1("Test Kind 1");
	}

	public static class RefKind2 extends OccurrenceKind {

		private static final long serialVersionUID = -7253224209942479317L;

		protected RefKind2(String name) {
			super(name);
		}

		public static final RefKind2 TEST_KIND_2 = new RefKind2("Test Kind 2");
	}

	public static final String defaultName = "banzai";
	private static final FakeIndexer indexer = new FakeIndexer();

	public static IRodinFile createRodinFile(IRodinProject project,
			String fileName) throws RodinDBException {
		IRodinFile file = project.getRodinFile(fileName);
		file.create(true, null);
		return file;
	}

	public static Occurrence createDefaultOccurrence(IRodinElement element) {
		return new Occurrence(OccurrenceKind.NULL, RodinIndexer
				.getRodinLocation(element), indexer);
	}

	public static Occurrence createDefaultOccurrence(IRodinElement element,
			IIndexer ind) {
		return new Occurrence(OccurrenceKind.NULL, RodinIndexer
				.getRodinLocation(element), ind);
	}

	public static Occurrence[] generateOccurrencesTestSet(IInternalElement ie,
			int numEachKind) throws CoreException {

		OccurrenceKind[] kinds = { IndexTestsUtil.RefKind1.TEST_KIND_1,
				IndexTestsUtil.RefKind2.TEST_KIND_2 };
		ArrayList<Occurrence> result = new ArrayList<Occurrence>();

		for (OccurrenceKind k : kinds) {
			for (int i = 0; i < numEachKind; i++) {
				result.add(new Occurrence(k, RodinIndexer.getRodinLocation(ie
						.getRodinFile()), indexer));
			}
		}
		return result.toArray(new Occurrence[result.size()]);
	}

	public static NamedElement createNamedElement(IRodinFile file,
			String elementName) throws CoreException {
		NamedElement el = new NamedElement(elementName, file);
		el.create(null, null);
		return el;
	}

	public static void assertNoSuchDescriptor(IRodinIndex index,
			IInternalElement element) {
		IDescriptor desc = index.getDescriptor(element);
		TestCase.assertNull("there should not be any descriptor for element "
				+ element.getElementName(), desc);
	}

	public static void assertNotNull(IDescriptor desc) {
		TestCase.assertNotNull("Descriptor " + desc + " should not be null",
				desc);
	}

	public static void addOccurrences(Occurrence[] occurrences,
			IDescriptor descriptor) {
		for (Occurrence occ : occurrences) {
			descriptor.addOccurrence(occ);
		}
	}

	public static void addOccurrences(IInternalElement element, String name,
			Occurrence[] occurrences, IndexingFacade index) {
		for (Occurrence occ : occurrences) {
			index.addOccurrence(element, name, occ);
		}
	}

	public static void assertDescriptor(IDescriptor desc,
			IInternalElement element, String name, int expectedLength) {
		assertNotNull(desc);
		assertElement(desc, element);
		assertName(desc, name);
		assertLength(desc, expectedLength);
	}

	public static void assertContains(IDescriptor desc, Occurrence occ) {
		assertNotNull(desc);
		TestCase.assertTrue("occurrence not found: "
				+ occ.getLocation().getElement(), desc.hasOccurrence(occ));
	}

	public static void assertContainsNot(IDescriptor desc, Occurrence occ) {
		assertNotNull(desc);
		TestCase.assertFalse("occurrence should not be found: "
				+ occ.getLocation().getElement(), desc.hasOccurrence(occ));
	}

	public static void assertContainsAll(IDescriptor desc, Occurrence[] occs) {
		assertNotNull(desc);
		for (Occurrence occ : occs) {
			assertContains(desc, occ);
		}
	}

	public static void assertContainsNone(IDescriptor desc, Occurrence[] occs) {
		assertNotNull(desc);
		for (Occurrence occ : occs) {
			assertContainsNot(desc, occ);
		}
	}

	public static void assertSameOccurrences(IDescriptor desc, Occurrence[] occs) {
		assertNotNull(desc);
		assertContainsAll(desc, occs);

		assertLength(desc, occs.length);
	}

	public static void assertLength(IDescriptor desc, int expectedLength) {
		assertNotNull(desc);
		TestCase.assertEquals("bad number of occurrences", expectedLength, desc
				.getOccurrences().length);
	}

	public static void assertElement(IDescriptor desc, IInternalElement element) {
		assertNotNull(desc);
		TestCase.assertEquals("bad element for descriptor " + desc, element,
				desc.getElement());
	}

	public static void assertName(IDescriptor desc, String name) {
		assertNotNull(desc);
		TestCase.assertEquals("bad element for descriptor " + desc, name, desc
				.getName());
	}

	public static void assertLength(IRodinElement[] elements, int size) {
		TestCase.assertEquals("incorrect number of elements", size,
				elements.length);
	}

	public static void assertIsEmpty(IInternalElement[] elements) {
		assertLength(elements, 0);
	}

	public static void assertContainsAll(IInternalElement[] expectedElements,
			IInternalElement[] actualElements) {

		for (IInternalElement elem : expectedElements) {
			assertContains(elem, actualElements);
		}
	}

	public static void assertSameElements(IInternalElement[] expectedElements,
			IInternalElement[] actualElements) {

		assertContainsAll(expectedElements, actualElements);

		assertLength(actualElements, expectedElements.length);
	}

	public static void assertContains(IInternalElement elem,
			IInternalElement[] actualElements) {

		List<IInternalElement> actList = Arrays.asList(actualElements);

		TestCase.assertTrue("element " + elem.getElementName()
				+ " is not present", actList.contains(elem));
	}

}
