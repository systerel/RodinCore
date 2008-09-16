package org.rodinp.internal.core.index.tests;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IIndexingFacade;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.OccurrenceKind;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.Occurrence;
import org.rodinp.internal.core.index.RodinIndex;

public class IndexTestsUtil {

	public static enum TestOccurrenceKind implements OccurrenceKind {
		TEST_KIND
	}

	public static enum OccKind1 implements OccurrenceKind {
		TEST_KIND_1
	}

	public static enum OccKind2 implements OccurrenceKind {
		TEST_KIND_2
	}

	public static final String defaultName = "banzai";

	public static IRodinFile createRodinFile(IRodinProject project,
			String fileName) throws RodinDBException {
		IRodinFile file = project.getRodinFile(fileName);
		file.create(true, null);
		return file;
	}

	public static Occurrence createDefaultOccurrence(IRodinElement element) {
		return new Occurrence(TestOccurrenceKind.TEST_KIND, RodinIndexer
				.getRodinLocation(element));
	}

	public static void addOccurrencesTestSet(IInternalElement ie,
			int numEachKind, IIndexingFacade index) throws CoreException {

		OccurrenceKind[] kinds = { IndexTestsUtil.OccKind1.TEST_KIND_1,
				IndexTestsUtil.OccKind2.TEST_KIND_2 };
		for (OccurrenceKind k : kinds) {
			for (int i = 0; i < numEachKind; i++) {
				final IRodinLocation loc = RodinIndexer.getRodinLocation(ie
						.getRodinFile());
				index.addOccurrence(ie, k, loc);
			}
		}
	}

	public static NamedElement createNamedElement(IRodinFile file,
			String elementName) throws CoreException {
		NamedElement el = new NamedElement(elementName, file);
		el.create(null, null);
		return el;
	}

	public static void assertNoSuchDescriptor(RodinIndex index,
			IInternalElement element) {
		Descriptor desc = index.getDescriptor(element);
		TestCase.assertNull("there should not be any descriptor for element "
				+ element.getElementName(), desc);
	}

	public static void assertNotNull(Descriptor desc) {
		TestCase.assertNotNull("Descriptor " + desc + " should not be null",
				desc);
	}

	public static void addOccurrences(Occurrence[] occurrences,
			Descriptor descriptor) {
		for (Occurrence occ : occurrences) {
			descriptor.addOccurrence(occ);
		}
	}

	public static void assertDescriptor(Descriptor desc,
			IInternalElement element, String name, int expectedLength) {
		assertNotNull(desc);
		assertElement(desc, element);
		assertName(desc, name);
		assertLength(desc, expectedLength);
	}

	public static void assertContains(Descriptor desc, Occurrence occ) {
		assertNotNull(desc);
		TestCase.assertTrue("occurrence not found: "
				+ occ.getLocation().getElement(), desc.hasOccurrence(occ));
	}

	public static void assertContainsNot(Descriptor desc, Occurrence occ) {
		assertNotNull(desc);
		TestCase.assertFalse("occurrence should not be found: "
				+ occ.getLocation().getElement(), desc.hasOccurrence(occ));
	}

	public static void assertContainsAll(Descriptor desc, Occurrence[] occs) {
		assertNotNull(desc);
		for (Occurrence occ : occs) {
			assertContains(desc, occ);
		}
	}

	public static void assertContainsNone(Descriptor desc, Occurrence[] occs) {
		assertNotNull(desc);
		for (Occurrence occ : occs) {
			assertContainsNot(desc, occ);
		}
	}

	public static void assertSameOccurrences(Descriptor desc, Occurrence[] occs) {
		assertNotNull(desc);
		assertLength(desc, occs.length);

		assertContainsAll(desc, occs);
	}

	public static void assertLength(Descriptor desc, int expectedLength) {
		assertNotNull(desc);
		TestCase.assertEquals("bad number of occurrences", expectedLength, desc
				.getOccurrences().length);
	}

	public static void assertElement(Descriptor desc, IInternalElement element) {
		assertNotNull(desc);
		TestCase.assertEquals("bad element for descriptor " + desc, element,
				desc.getElement());
	}

	public static void assertName(Descriptor desc, String name) {
		assertNotNull(desc);
		TestCase.assertEquals("bad name for descriptor " + desc, name, desc
				.getName());
	}

	public static void assertLength(IRodinElement[] elements, int size) {
		TestCase.assertEquals("incorrect number of elements", size,
				elements.length);
	}

	public static void assertIsEmpty(IInternalElement[] elements) {
		assertLength(elements, 0);
	}

	public static void assertIsEmpty(IRodinFile[] files) {
		assertLength(files, 0);
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

	public static void assertExports(Set<IInternalElement> expected,
			Set<IInternalElement> actual) {

		TestCase.assertEquals("Bad exports.", expected, actual);
	}

	public static IInternalElement[] makeIIEArray(IInternalElement... elements) {
		return elements;
	}

	public static IRodinFile[] makeIRFArray(IRodinFile... files) {
		return files;
	}

}
