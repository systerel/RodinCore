package org.rodinp.internal.core.index.tests;

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

	// private void assertElement(IRodinElement expected, IRodinElement actual)
	// {
	// assertEquals("unexpected element", expected, actual);
	// }

	private IRodinProject rodinProject;
	private IRodinFile file;
	private IDescriptor testDesc;
	private NamedElement testElt;

	private Occurrence testOccurrence;

	private Occurrence[] occurrencesTestSet;

	private static final String testEltName = "testElt";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		file = rodinProject.getRodinFile("desc.test");
		file.create(false, null);
		testElt = IndexTestsUtil.createNamedElement(file, testEltName);
		testDesc = new Descriptor(testEltName, testElt);
		occurrencesTestSet = IndexTestsUtil.generateOccurrencesTestSet(testElt,
				3);
		testOccurrence = IndexTestsUtil.createDefaultOccurrence(testElt);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		testDesc.clearOccurrences();
		testElt = null;
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		IndexTestsUtil.assertElement(testDesc, testElt);
		IndexTestsUtil.assertName(testDesc, testEltName);
		assertNotNull("occurrences should not be null", testDesc
				.getOccurrences());
	}

	public void testAddOccurrence() throws Exception {
		Occurrence occ = IndexTestsUtil.createDefaultOccurrence(testElt);

		testDesc.addOccurrence(occ);

		IndexTestsUtil.assertContains(testDesc, occ);
	}

	public void testAddAlienOccurrence() throws Exception {
		IRodinFile alien = rodinProject.getRodinFile("alienFile.test");
		alien.create(true, null);
		Occurrence alienOccurrence = IndexTestsUtil
				.createDefaultOccurrence(alien);

		try {
			testDesc.addOccurrence(alienOccurrence);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("IllegalArgumentException should have been raised when "
				+ "adding an alien occurrence");
	}

	public void testRemoveOccurrence() throws Exception {
		IndexTestsUtil.addOccurrences(occurrencesTestSet, testDesc);
		testDesc.addOccurrence(testOccurrence);

		testDesc.removeOccurrence(testOccurrence);

		IndexTestsUtil.assertContainsNot(testDesc, testOccurrence);
	}

	public void testClearOccurrences() throws Exception {
		IndexTestsUtil.addOccurrences(occurrencesTestSet, testDesc);

		testDesc.clearOccurrences();

		IndexTestsUtil.assertContainsNone(testDesc, occurrencesTestSet);
	}

}
