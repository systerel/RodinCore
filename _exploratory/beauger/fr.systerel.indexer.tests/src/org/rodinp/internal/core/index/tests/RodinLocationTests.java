package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.internal.core.index.RodinLocation;

public class RodinLocationTests extends AbstractRodinDBTests {

	public RodinLocationTests(String name) {
		super(name);
	}

	private static IAttributeType.String TEST_ATTRIBUTE;
	private static IRodinProject project;
	private static IRodinFile file;
	private static IRodinLocation loc;
	private static final int defaultStart = 1;
	private static final int defaultEnd = 3;

	private void assertLocation(IRodinElement element,
			IAttributeType attributeType, int start, int end) {
		assertEquals("RodinLocation constructor: bad element", element, loc
				.getElement());
		assertEquals("RodinLocation constructor: bad attribute type",
				attributeType, loc.getAttributeType());
		assertEquals("RodinLocation constructor: bad char start", start, loc
				.getCharStart());
		assertEquals("RodinLocation constructor: bad char end", end, loc
				.getCharEnd());
	}

	private void assertException(Exception e, String messageExtract) {
		assertTrue("bad exception message", e.getMessage().contains(messageExtract));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TEST_ATTRIBUTE = RodinCore
				.getStringAttrType("org.rodinp.core.testAttributeType");
		project = createRodinProject("P");
		file = IndexTestsUtil.createRodinFile(project, "rodLoc.test");
		file.setAttributeValue(TEST_ATTRIBUTE, "testAttribute", null);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		deleteProject("P");
	}

	public void testConstructor() throws Exception {
		loc = new RodinLocation(file, TEST_ATTRIBUTE, defaultStart, defaultEnd);

		assertLocation(file, TEST_ATTRIBUTE, defaultStart, defaultEnd);
	}

	public void testNullElement() throws Exception {
		try {
			loc = new RodinLocation(null, TEST_ATTRIBUTE, defaultStart,
					defaultEnd);
		} catch (NullPointerException e) {
			assertException(e, "null");
			return;
		}
		fail("Trying to construct a RodinLocation from a null element should raise NullPointerException");
	}

	public void testElementDoesNotExist() throws Exception {
		IRodinFile fileDoesNotExist = project
				.getRodinFile("fileDoesNotExist.test");

		try {
			loc = new RodinLocation(fileDoesNotExist, TEST_ATTRIBUTE,
					defaultStart, defaultEnd);
		} catch (IllegalArgumentException e) {
			assertException(e, "exist");
			return;
		}
		fail("Trying to construct a RodinLocation from a RodinElement that does not exist should raise IllegalArgumentException");
	}

	public void testNullAttribute() throws Exception {
		loc = new RodinLocation(file, null, RodinLocation.NULL_CHAR_POS,
				RodinLocation.NULL_CHAR_POS);

		assertLocation(file, null, RodinLocation.NULL_CHAR_POS,
				RodinLocation.NULL_CHAR_POS);
	}

	public void testNullAttWithPos() throws Exception {
		try {
			loc = new RodinLocation(file, null, defaultStart, defaultEnd);
		} catch (IllegalArgumentException e) {
			assertException(e, "attribute");
			return;
		}
		fail("Constructing a RodinLocation with null attribute and non null char positions should raise IllegalArgumentException");
	}

	public void testReversePositions() throws Exception {
		try {
			loc = new RodinLocation(file, TEST_ATTRIBUTE, defaultEnd,
					defaultStart);
		} catch (IllegalArgumentException e) {
			assertException(e, "before");
			return;
		}
		fail("Constructing a RodinLocation with char positions in reverse order should raise IllegalArgumentException");
	}
}
