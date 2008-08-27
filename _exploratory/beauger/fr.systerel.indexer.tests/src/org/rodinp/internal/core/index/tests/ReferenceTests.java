package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.Occurrence;
import org.rodinp.core.index.OccurrenceKind;
import org.rodinp.core.tests.ModifyingResourceTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.RodinLocation;

public class ReferenceTests extends ModifyingResourceTests {

	public ReferenceTests(String name) {
		super(name);
	}

	private final ConcreteIndexer indexer = new ConcreteIndexer();
	private final OccurrenceKind defaultKind = OccurrenceKind.NULL;
	private IRodinLocation location;
	private Occurrence ref;


	private static void assertLocation(IRodinLocation expected,
			IRodinLocation actual) {
		assertEquals("Occurrence construction was not correct", expected, actual);
	}

	private static void assertKind(OccurrenceKind expected, OccurrenceKind actual) {
		assertEquals("Occurrence construction was not correct", expected, actual);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		createRodinProject("P");
		IRodinFile file = IndexTestsUtil.createRodinFile("P/ref.test");
		NamedElement elem = IndexTestsUtil.createNamedElement(file, "elem");

		location = new RodinLocation(elem, null,
				IRodinLocation.NULL_CHAR_POS, IRodinLocation.NULL_CHAR_POS);
		ref = new Occurrence(defaultKind, location, indexer);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		assertKind(defaultKind, ref.getKind());
		assertLocation(location, ref.getLocation());
	}

}
