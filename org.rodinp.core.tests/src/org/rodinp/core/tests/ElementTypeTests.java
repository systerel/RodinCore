/**
 * 
 */
package org.rodinp.core.tests;

import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.RodinTestFile;

import junit.framework.TestCase;

/**
 * Test cases for element types.
 * 
 * @author Laurent Voisin
 */
public class ElementTypeTests extends TestCase {

	static void assertElementTypeId(IElementType type, String id) {
		assertEquals("Wrong id", id, type.getId());
	}
	
	static void assertETypePos(IElementType expected) {
		String id = expected.getId();
		IElementType actual = RodinCore.getElementType(id);
		assertSame("Wrong elementType", expected, actual);
	}
	
	static void assertETypeNeg(String id) {
		try {
			RodinCore.getElementType(id);
			fail("Should have raised an exception.");
		} catch (IllegalArgumentException e) {
			// success
		}
	}
	
	/**
	 * Ensures that the Rodin database element type can be retrieved.
	 */
	public void testDBElementType() throws Exception {
		assertETypePos(IRodinDB.ELEMENT_TYPE);
	}

	/**
	 * Ensures that the Rodin project element type can be retrieved.
	 */
	public void testProjectElementType() throws Exception {
		assertETypePos(IRodinProject.ELEMENT_TYPE);
	}

	/**
	 * Ensures that the test file element type can be retrieved.
	 */
	public void testFileElementType() throws Exception {
		assertETypePos(RodinTestFile.ELEMENT_TYPE);
	}

	/**
	 * Ensures that the test internal element type can be retrieved.
	 */
	public void testInternalElementType() throws Exception {
		assertETypePos(NamedElement.ELEMENT_TYPE);
	}

	/**
	 * Ensures that an unknown element type can't be retrieved.
	 */
	public void testUnknownElementType() throws Exception {
		assertETypeNeg("unknown element type");
	}

}
