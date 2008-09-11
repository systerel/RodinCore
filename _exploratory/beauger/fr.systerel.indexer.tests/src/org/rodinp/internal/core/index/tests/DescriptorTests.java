package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.Occurrence;

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
	private Descriptor testDesc;
	private NamedElement testElt;

	private Occurrence testOccurrence;

	private IOccurrence[] occurrencesTestSet;

	private static final String testEltName = "testElt";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		file = IndexTestsUtil.createRodinFile(rodinProject, "desc.test");
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

	public void testRemoveOccurrence() throws Exception {
		IndexTestsUtil.addOccurrences(occurrencesTestSet, testDesc);
		testDesc.addOccurrence(testOccurrence);

		testDesc.removeOccurrence(testOccurrence);

		IndexTestsUtil.assertContainsNot(testDesc, testOccurrence);
	}

	public void testRemoveOccurrences() throws Exception {
		Occurrence friendOcc = IndexTestsUtil.createDefaultOccurrence(testElt);
		IRodinFile alien = IndexTestsUtil.createRodinFile(rodinProject, "alienFile.test");
		Occurrence alienOcc = IndexTestsUtil.createDefaultOccurrence(alien);

		testDesc.addOccurrence(friendOcc);
		testDesc.addOccurrence(alienOcc);

		testDesc.removeOccurrences(testElt.getRodinFile());

		IndexTestsUtil.assertContainsNot(testDesc, friendOcc);
		IndexTestsUtil.assertContains(testDesc, alienOcc);
	}

	public void testClearOccurrences() throws Exception {
		IndexTestsUtil.addOccurrences(occurrencesTestSet, testDesc);

		testDesc.clearOccurrences();

		IndexTestsUtil.assertContainsNone(testDesc, occurrencesTestSet);
	}

}
