package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.index.IAttributeLocation;
import org.rodinp.core.index.IAttributeSubstringLocation;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.basis.NamedElement;

public class RodinLocationTests extends IndexTests {

	public RodinLocationTests(String name) {
		super(name, true);
	}

	private static final IAttributeType.String attrType = RodinCore
			.getStringAttrType("org.rodinp.core.testAttributeType");
	private static final int defaultStart = 1;
	private static final int defaultEnd = 3;

	private IRodinProject project;
	private IRodinFile file;
	private IInternalElement locElement;

	public static void assertLocation(IRodinLocation loc, IRodinElement element) {
		assertEquals("unexpected element in location", element, loc
				.getElement());
	}

	public static void assertLocation(IRodinLocation loc,
			IInternalParent element, IAttributeType attributeType) {
		assertLocation(loc, element);
		assertTrue(loc instanceof IAttributeLocation);
		final IAttributeLocation aLoc = (IAttributeLocation) loc;
		assertEquals("unexpected attribute type in location", attributeType,
				aLoc.getAttributeType());
	}

	public static void assertLocation(IRodinLocation loc,
			IInternalParent element, IAttributeType.String attributeType,
			int start, int end) {
		assertLocation(loc, element, attributeType);
		assertTrue(loc instanceof IAttributeSubstringLocation);
		final IAttributeSubstringLocation aLoc = (IAttributeSubstringLocation) loc;
		assertEquals("unexpected start position in location", start, aLoc
				.getCharStart());
		assertEquals("unexpected end position in location", end, aLoc
				.getCharEnd());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = getRodinProject("P");
		file = project.getRodinFile("rodLoc.test");
		locElement = file.getInternalElement(NamedElement.ELEMENT_TYPE, "foo");
	}

	public void testConstructor() throws Exception {
		IRodinLocation loc = RodinIndexer.getRodinLocation(locElement, attrType, defaultStart,
				defaultEnd);
		assertLocation(loc, locElement, attrType, defaultStart, defaultEnd);
	}

	public void testNullElement() throws Exception {
		try {
			RodinIndexer.getRodinLocation(null);
			fail("expected NullPointerException");
		} catch (NullPointerException e) {
			// Pass
		}
	}

	public void testFileElement() throws Exception {
		IRodinLocation loc = RodinIndexer.getRodinLocation(file);
		assertLocation(loc, file);
	}

	public void testInternalElement() throws Exception {
		IRodinLocation loc = RodinIndexer.getRodinLocation(locElement);
		assertLocation(loc, locElement);
	}

	public void testAttribute() throws Exception {
		IRodinLocation loc = RodinIndexer.getRodinLocation(locElement, attrType);
		assertLocation(loc, locElement, attrType);
	}

	public void testAttributeSubstring() throws Exception {
		IRodinLocation loc = RodinIndexer.getRodinLocation(locElement, attrType, defaultStart,
				defaultEnd);
		assertLocation(loc, locElement, attrType, defaultStart, defaultEnd);
	}

	public void testNullAttribute() throws Exception {
		try {
			RodinIndexer.getRodinLocation(locElement, null, defaultStart,
					defaultEnd);
			fail("expected NullPointerException");
		} catch (NullPointerException e) {
			// Pass
		}
	}

	public void testInvalidStart() throws Exception {
		try {
			RodinIndexer.getRodinLocation(locElement, attrType, -1, 0);
			fail("expected NullPointerException");
		} catch (IllegalArgumentException e) {
			// Pass
		}
	}

	public void testInvalidEnd() throws Exception {
		try {
			RodinIndexer.getRodinLocation(locElement, attrType, 0, -1);
			fail("expected NullPointerException");
		} catch (IllegalArgumentException e) {
			// Pass
		}
	}

	public void testEmptySubstring() throws Exception {
		try {
			RodinIndexer.getRodinLocation(locElement, attrType, 0, 0);
			fail("expected NullPointerException");
		} catch (IllegalArgumentException e) {
			// Pass
		}
	}

}
