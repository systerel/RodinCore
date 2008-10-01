package org.rodinp.internal.core.index.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.TEST_KIND;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Occurrence;

public class OccurrenceTests extends IndexTests {

	public OccurrenceTests(String name) {
		super(name, true);
	}

	private final IOccurrenceKind defaultKind = TEST_KIND;
	private IRodinLocation location;
	private IOccurrence occ;


	private static void assertLocation(IRodinLocation expected,
			IRodinLocation actual) {
		assertEquals("Field IRodinLocation in Occurrence is not correct", expected, actual);
	}

	private static void assertKind(IOccurrenceKind expected, IOccurrenceKind actual) {
		assertEquals("Field IOccurrenceKind in Occurrence is not correct", expected, actual);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final IRodinProject rodinProject = createRodinProject("P");
		IRodinFile file = createRodinFile(rodinProject, "occ.test");
		NamedElement elem = createNamedElement(file, "elem");

		location = RodinIndexer.getRodinLocation(elem);
		occ = new Occurrence(defaultKind, location);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		assertKind(defaultKind, occ.getKind());
		assertLocation(location, occ.getLocation());
	}

}
