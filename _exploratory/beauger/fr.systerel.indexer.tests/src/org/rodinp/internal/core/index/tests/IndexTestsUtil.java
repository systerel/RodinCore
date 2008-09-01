package org.rodinp.internal.core.index.tests;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDescriptor;
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
	private static final ConcreteIndexer indexer = new ConcreteIndexer();

	public static Occurrence createDefaultReference(IInternalElement element) {
		return new Occurrence(OccurrenceKind.NULL, RodinIndexer
				.getRodinLocation(element), indexer);
	}

	// public static Occurrence[] generateFaultyReferencesTestSet()
	// throws CoreException { // TODO use next method
	// IInternalElement ie1 = IndexTestsUtil.createNamedElement("P/f1.test",
	// "ie1");
	// IInternalElement ie2 = IndexTestsUtil.createNamedElement("P/f2.test",
	// "ie2");
	//
	// IInternalElement[] elems = { ie1, ie2 };
	// OccurrenceKind[] kinds = { IndexTestsUtil.RefKind1.TEST_KIND_1,
	// IndexTestsUtil.RefKind2.TEST_KIND_2 };
	// ArrayList<Occurrence> result = new ArrayList<Occurrence>();
	//
	// for (IInternalElement el : elems) {
	// final IRodinLocation loc = IndexTestsUtil.createDefaultLocation(el);
	// for (OccurrenceKind k : kinds) {
	// result.add(new Occurrence(k, loc));
	// }
	// }
	// return result.toArray(new Occurrence[result.size()]);
	// }

	public static Occurrence[] generateOccurrencesTestSet(IInternalElement ie,
			int numEachKind) throws CoreException {

		OccurrenceKind[] kinds = { IndexTestsUtil.RefKind1.TEST_KIND_1,
				IndexTestsUtil.RefKind2.TEST_KIND_2 };
		ArrayList<Occurrence> result = new ArrayList<Occurrence>();

		for (OccurrenceKind k : kinds) {
			for (int i = 0; i < numEachKind; i++) {
				result.add(new Occurrence(k, RodinIndexer.getRodinLocation(ie),
						indexer));
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

	public static void assertDescriptor(IRodinIndex index,
			IInternalElement element, String name, int expectedLength) {
		// FIXME very incomplete assertion, make more intrusive tests
		final IDescriptor descriptor = index.getDescriptor(element);
		TestCase.assertNotNull("expected descriptor not found for "
				+ element.getElementName(), descriptor);

//		TestCase.assertEquals("incorrect name for element "
//				+ element.getElementName() + " in descriptor" + descriptor,
//				name, descriptor.getName());
		
		final int occsLength = descriptor.getOccurrences().length;
		TestCase.assertEquals("Did not index correctly", expectedLength,
				occsLength);
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

}
