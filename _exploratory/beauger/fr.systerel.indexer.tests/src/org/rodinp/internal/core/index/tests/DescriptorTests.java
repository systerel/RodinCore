package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.Occurrence;
import org.rodinp.core.tests.ModifyingResourceTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Descriptor;

public class DescriptorTests extends ModifyingResourceTests {

	public DescriptorTests(String name) {
		super(name);
	}

	private void assertElement(IRodinElement expected, IRodinElement actual) {
		assertEquals("unexpected element", expected, actual);
	}

	private void assertIsReferenced(IDescriptor desc, Occurrence ref) {
		assertTrue("reference not found: " + ref.getLocation().getElement(),
				desc.hasOccurrence(ref));
	}

	private void assertAreReferenced(IDescriptor desc, Occurrence[] refs) {
		for (Occurrence ref : refs) {
			assertIsReferenced(desc, ref);
		}
	}

	private void assertIsNotReferenced(IDescriptor desc, Occurrence ref) {
		assertFalse("reference should not be found: "
				+ ref.getLocation().getElement(), desc.hasOccurrence(ref));
	}

	private void assertAreNotReferenced(IDescriptor desc, Occurrence[] refs) {
		for (Occurrence ref : refs) {
			assertIsNotReferenced(desc, ref);
		}
	}

	// private void assertReference(Occurrence expected, Occurrence actual) {
	// assertEquals("references are not equal", expected, actual);
	// }
	//
	// private void assertReferences(Occurrence[] expected, Occurrence[] actual) {
	// assertEquals("compared Occurrence[] have different lengths",
	// expected.length, actual.length);
	//
	// for (int i = 0; i < expected.length; i++) {
	// assertReference(expected[i], actual[i]);
	// }
	// }


	private IDescriptor testDesc;
	private NamedElement testElt;

	private Occurrence testReference;

	private Occurrence[] referencesTestSet;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
		IRodinFile file = IndexTestsUtil.createRodinFile("P/desc.test");
		final String testEltName = "testElt";
		testElt = IndexTestsUtil.createNamedElement(file, testEltName);
		testDesc = new Descriptor(testEltName, testElt);
		referencesTestSet = IndexTestsUtil.generateReferencesTestSet(testElt, 3);
		testReference = IndexTestsUtil.createDefaultReference(testElt);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		testDesc.clearOccurrences();
		testElt = null;
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		assertElement(testElt, testDesc.getElement());
		assertNotNull("references should not be null", testDesc.getOccurrences());
	}

	public void testAddReference() throws Exception {
		Occurrence ref = IndexTestsUtil.createDefaultReference(testElt);

		testDesc.addOccurrence(ref);

		assertIsReferenced(testDesc, ref);
	}

	public void testAddReferences() throws Exception {
		testDesc.addOccurrences(referencesTestSet);

		assertAreReferenced(testDesc, referencesTestSet);
	}

	public void testRemoveReference() throws Exception {
		testDesc.addOccurrences(referencesTestSet);
		testDesc.addOccurrence(testReference);

		testDesc.removeOccurrence(testReference);

		assertIsNotReferenced(testDesc, testReference);
	}

	public void testRemoveReferences() throws Exception {
		testDesc.addOccurrences(referencesTestSet);
		testDesc.addOccurrence(testReference);

		testDesc.removeOccurrences(referencesTestSet);

		assertAreNotReferenced(testDesc, referencesTestSet);
		assertIsReferenced(testDesc, testReference);
	}

//	public void testReplaceReference() throws Exception {
//		testDesc.addReferences(referencesTestSet);
//		testDesc.addReference(testReference);
//
//		testDesc.replaceReference(referencesTestSet[2], testReference);
//
//		assertIsReferenced(testDesc, testReference);
//		assertIsNotReferenced(testDesc, referencesTestSet[2]);
//	}
//
//	/**
//	 * Should not affect the 'new' existing one. Should not remove the 'old'
//	 * one. Actually: should do nothing.
//	 * 
//	 * @throws Exception
//	 */
//	public void testReplaceReferenceWithExisting() throws Exception {
//		testDesc.addReferences(referencesTestSet);
//
//		testDesc.replaceReference(referencesTestSet[1], referencesTestSet[2]);
//
//		assertIsReferenced(testDesc, referencesTestSet[1]);
//		assertIsReferenced(testDesc, referencesTestSet[2]);
//	}

	public void testClearReferences() throws Exception {
		testDesc.addOccurrences(referencesTestSet);

		testDesc.clearOccurrences();

		assertAreNotReferenced(testDesc, referencesTestSet);
	}

	// public void testFindReferencesAllNull() throws Exception {
	// testDesc.addReferences(referencesTestSet);
	//
	// Occurrence[] expected = testDesc.getReferences();
	// Occurrence[] result = testDesc.findReferences(null, null);
	//
	// assertReferences(expected, result);
	// }
	//
	// public void testFindReferencesNullFile() throws Exception {
	// testDesc.addReferences(referencesTestSet);
	//
	// Occurrence[] expected = { referencesTestSet[0], referencesTestSet[2] };
	// Occurrence[] result = testDesc.findReferences(null, referencesTestSet[0]
	// .getKind());
	//
	// assertReferences(expected, result);
	// }
	//
	// public void testFindReferencesNullKind() throws Exception {
	// testDesc.addReferences(referencesTestSet);
	//
	// Occurrence[] expected = { referencesTestSet[0], referencesTestSet[1] };
	// Occurrence[] result = testDesc.findReferences(referencesTestSet[0]
	// .getLocation().getFile(), null);
	//
	// assertReferences(expected, result);
	// }
	//
	// public void testFindReferencesNoNull() throws Exception {
	// testDesc.addReferences(referencesTestSet);
	//
	// Occurrence[] expected = { referencesTestSet[0], referencesTestSet[1] };
	// Occurrence[] result = testDesc.findReferences(referencesTestSet[3]
	// .getLocation().getFile(), referencesTestSet[3].getKind());
	//
	// assertReferences(expected, result);
	// }
	//
	// public void testRemoveReferencesAllNull() throws Exception {
	// testDesc.addReferences(referencesTestSet);
	//
	// testDesc.removeReferences(null, null);
	// Occurrence[] expected = new Occurrence[0];
	// Occurrence[] result = testDesc.getReferences();
	//
	// assertReferences(expected, result);
	// }
	//
	// public void testRemoveReferencesNullFile() throws Exception {
	// testDesc.addReferences(referencesTestSet);
	//
	// testDesc.removeReferences(null, referencesTestSet[1].getKind());
	// Occurrence[] expected = { referencesTestSet[0], referencesTestSet[2] };
	// Occurrence[] result = testDesc.getReferences();
	//
	// assertReferences(expected, result);
	// }
	//
	// public void testRemoveReferencesNullKind() throws Exception {
	// testDesc.addReferences(referencesTestSet);
	//
	// testDesc.removeReferences(referencesTestSet[1].getLocation().getFile(),
	// null);
	// Occurrence[] expected = { referencesTestSet[2], referencesTestSet[3] };
	// Occurrence[] result = testDesc.getReferences();
	//
	// assertReferences(expected, result);
	// }
	//
	// public void testRemoveReferencesNoNull() throws Exception {
	// testDesc.addReferences(referencesTestSet);
	//
	// testDesc.removeReferences(referencesTestSet[1].getLocation().getFile(),
	// referencesTestSet[1].getKind());
	// Occurrence[] expected = { referencesTestSet[0], referencesTestSet[2],
	// referencesTestSet[3] };
	// Occurrence[] result = testDesc.getReferences();
	//
	// assertReferences(expected, result);
	// }

}
