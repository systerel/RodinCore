package org.rodinp.internal.core.index.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertContains;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertContainsNot;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertName;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createDefaultOccurrence;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
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

	private static final String testEltName = "testElt";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "desc.test");
		testElt = createNamedElement(file, testEltName);
		testDesc = new Descriptor(testElt, testEltName);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		testElt = null;
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		assertElement(testDesc, testElt);
		assertName(testDesc, testEltName);
		assertNotNull("occurrences should not be null", testDesc
				.getOccurrences());
	}

	public void testAddOccurrence() throws Exception {
		Occurrence occ = createDefaultOccurrence(testElt);

		testDesc.addOccurrence(occ);

		assertContains(testDesc, occ);
	}

	public void testRemoveOccurrences() throws Exception {
		Occurrence friendOcc = createDefaultOccurrence(testElt);
		IRodinFile alien = createRodinFile(rodinProject, "alienFile.test");
		Occurrence alienOcc = createDefaultOccurrence(alien);

		testDesc.addOccurrence(friendOcc);
		testDesc.addOccurrence(alienOcc);

		testDesc.removeOccurrences(testElt.getRodinFile());

		assertContainsNot(testDesc, friendOcc);
		assertContains(testDesc, alienOcc);
	}

	public void testSetName() throws Exception {
		final String name2 = testEltName+"2";

		testDesc.setName(name2);
		
		assertName(testDesc, name2);
	}
}
