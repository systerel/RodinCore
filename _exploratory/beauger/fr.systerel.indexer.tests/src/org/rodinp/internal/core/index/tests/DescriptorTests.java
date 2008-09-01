package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.Occurrence;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Descriptor;

public class DescriptorTests extends AbstractRodinDBTests {

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

	private IDescriptor testDesc;
	private NamedElement testElt;

	private Occurrence testReference;

	private Occurrence[] referencesTestSet;

	private static final String testEltName = "testElt";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		IRodinFile file = rodinProject.getRodinFile("desc.test");
		file.create(false, null);
		testElt = IndexTestsUtil.createNamedElement(file, testEltName);
		testDesc = new Descriptor(testEltName, testElt);
		referencesTestSet = IndexTestsUtil.generateOccurrencesTestSet(testElt, 3);
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
		IndexTestsUtil.addOccurrences(referencesTestSet, testDesc);

		assertAreReferenced(testDesc, referencesTestSet);
	}

	public void testRemoveReference() throws Exception {
		IndexTestsUtil.addOccurrences(referencesTestSet, testDesc);
		testDesc.addOccurrence(testReference);

		testDesc.removeOccurrence(testReference);

		assertIsNotReferenced(testDesc, testReference);
	}


	public void testClearReferences() throws Exception {
		IndexTestsUtil.addOccurrences(referencesTestSet, testDesc);

		testDesc.clearOccurrences();

		assertAreNotReferenced(testDesc, referencesTestSet);
	}

}
