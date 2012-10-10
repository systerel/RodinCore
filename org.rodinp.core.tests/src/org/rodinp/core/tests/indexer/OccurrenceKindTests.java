package org.rodinp.core.tests.indexer;

import org.rodinp.core.RodinCore;
import org.rodinp.core.indexer.IOccurrenceKind;

/**
 * Unit tests for occurrence kinds
 * 
 * @author Laurent Voisin
 */
public class OccurrenceKindTests extends IndexTests {

	// Namespace for plugin.xml
	public static final String NAMESPACE = PLUGIN_ID;

	// Ids declared in plugin.xml
	public static final String TEST_KIND = "testKind";
	public static final String INVALID_KIND = "invalid.kind";

	// Id not declared in plugin.xml
	public static final String UNDECLARED_KIND = "undeclared";

	private static void assertExists(String id) {
		IOccurrenceKind kind = RodinCore.getOccurrenceKind(id);
		assertNotNull(kind);
		assertEquals(id, kind.getId());
	}

	private static void assertNotExists(String id) {
		IOccurrenceKind kind = RodinCore.getOccurrenceKind(id);
		assertNull(kind);
	}

	public OccurrenceKindTests(String name) {
		super(name);
	}

	public void testValidKind() {
		assertExists(NAMESPACE + "." + TEST_KIND);
	}

	public void testInValidKind() {
		assertNotExists(TEST_KIND);

		assertNotExists(INVALID_KIND);
		assertNotExists(NAMESPACE + "." + INVALID_KIND);

		assertNotExists(UNDECLARED_KIND);
		assertNotExists(NAMESPACE + "." + UNDECLARED_KIND);
	}

}
