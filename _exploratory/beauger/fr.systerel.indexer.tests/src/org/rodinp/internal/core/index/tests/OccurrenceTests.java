package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.OccurrenceKind;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Occurrence;
import org.rodinp.internal.core.index.RodinLocation;

public class OccurrenceTests extends AbstractRodinDBTests {

	public OccurrenceTests(String name) {
		super(name);
	}

	private final FakeIndexer indexer = new FakeIndexer();
	private final OccurrenceKind defaultKind = OccurrenceKind.NULL;
	private IRodinLocation location;
	private Occurrence occ;


	private static void assertLocation(IRodinLocation expected,
			IRodinLocation actual) {
		assertEquals("Field IRodinLocation in Occurrence is not correct", expected, actual);
	}

	private static void assertKind(OccurrenceKind expected, OccurrenceKind actual) {
		assertEquals("Field OccurrenceKind in Occurrence is not correct", expected, actual);
	}

	private static void assertIndexer(IIndexer expected, IIndexer actual) {
		assertEquals("Field IIndexer in Occurrence is not correct", expected, actual);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final IRodinProject rodinProject = createRodinProject("P");
		IRodinFile file = IndexTestsUtil.createRodinFile(rodinProject, "occ.test");
		NamedElement elem = IndexTestsUtil.createNamedElement(file, "elem");

		location = new RodinLocation(elem, null,
				IRodinLocation.NULL_CHAR_POS, IRodinLocation.NULL_CHAR_POS);
		occ = new Occurrence(defaultKind, location, indexer);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		assertKind(defaultKind, occ.getKind());
		assertLocation(location, occ.getLocation());
		assertIndexer(indexer, occ.getIndexer());
	}

}
