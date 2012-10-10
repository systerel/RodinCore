/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.rodinp.core.IRodinDBStatusConstants.ATTRIBUTE_DOES_NOT_EXIST;
import static org.rodinp.core.IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST;
import static org.rodinp.core.IRodinDBStatusConstants.INVALID_MARKER_LOCATION;
import static org.rodinp.core.RodinMarkerUtil.ARGUMENTS;
import static org.rodinp.core.RodinMarkerUtil.ATTRIBUTE_ID;
import static org.rodinp.core.RodinMarkerUtil.CHAR_END;
import static org.rodinp.core.RodinMarkerUtil.CHAR_START;
import static org.rodinp.core.RodinMarkerUtil.ELEMENT;
import static org.rodinp.core.RodinMarkerUtil.ERROR_CODE;
import static org.rodinp.core.RodinMarkerUtil.RODIN_PROBLEM_MARKER;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.core.tests.basis.RodinTestRoot;

/**
 * Tests about Rodin markers.
 * 
 * @author Laurent Voisin
 */

public class MarkerTests extends ModifyingResourceTests {

	private static interface Runnable {
		void run() throws RodinDBException;
	}

	private static final IAttributeType.String nullType = null;

	IRodinProject rodinProject;

	public MarkerTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
	}

	public void tearDown() throws Exception {
		// Clean up the workspace
		getWorkspaceRoot().delete(true, true, null);
		super.tearDown();
	}

	private void assertSameArgs(Object[] expected, String[] actual) {
		assertEquals("Incompatible array lengths", expected.length,
				actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].toString(), actual[i]);
		}
	}

	@SuppressWarnings({"rawtypes"})
	private void assertMarkerAtributes(IMarker marker, Map<String, Object> attrs)
			throws Exception {

		Map<String, Object> actualAttrs = marker.getAttributes();
		for (Map.Entry entry : attrs.entrySet()) {
			final Object key = entry.getKey();
			final Object value = entry.getValue();
			if (value != null) {
				final Object actual = actualAttrs.get(key);
				assertEquals("Unexpected marker attribute " + key, value,
						actual);
			}
		}
		if (attrs.size() != actualAttrs.size()) {
			Set<String> keys = new HashSet<String>(actualAttrs.keySet());
			keys.removeAll(attrs.keySet());
			// List unexpected attributes
			fail("Unexpected marker attributes " + keys);
		}
	}

	private void assertNoMarker(IResource resource) throws Exception {

		final IMarker[] markers = resource.findMarkers(null, false,
				IResource.DEPTH_INFINITE);
		assertEquals("No marker expected", 0, markers.length);
	}

	private void assertNoMarker() throws Exception {
		assertNoMarker(getWorkspaceRoot());
	}

	private IMarker getMarker(IRodinElement elem, String markerType)
			throws Exception {

		final IResource resource = elem.getUnderlyingResource();
		final IMarker[] markers = resource.findMarkers(markerType, false,
				IResource.DEPTH_ZERO);
		assertEquals("Exactly one marker expected", 1, markers.length);
		return markers[0];
	}

	private Map<String, Object> getAttributes(IRodinElement elem,
			IAttributeType attrType, int charStart, int charEnd,
			IRodinProblem pb, Object... args) {

		final Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put(IMarker.SEVERITY, pb.getSeverity());
		attrs.put(IMarker.MESSAGE, pb.getLocalizedMessage(args));
		attrs.put(ERROR_CODE, pb.getErrorCode());
		StringBuilder builder = new StringBuilder();
		for (Object arg : args) {
			final String string = arg.toString();
			builder.append(string.length());
			builder.append(':');
			builder.append(string);
		}
		attrs.put(ARGUMENTS, builder.toString());
		if (elem instanceof IInternalElement) {
			// We don't want to check the element exactly (in case of file
			// renaming).
			attrs.put(ELEMENT, null);
		}
		if (attrType != null) {
			attrs.put(ATTRIBUTE_ID, attrType.getId());
		}
		if (0 <= charStart) {
			attrs.put(CHAR_START, charStart);
			attrs.put(CHAR_END, charEnd);
		}
		return attrs;
	}

	private void assertProblemMarker(IRodinElement elem,
			IAttributeType attrType, int charStart, int charEnd,
			IRodinProblem pb, Object... args) throws Exception {

		final IMarker marker = getMarker(elem, RODIN_PROBLEM_MARKER);

		// Check attributes directly
		final Map<String, Object> attrs = getAttributes(elem, attrType,
				charStart, charEnd, pb, args);
		assertMarkerAtributes(marker, attrs);

		// Check attributes through enquiry methods of RodinMarkerUtil
		assertEquals(pb.getErrorCode(), RodinMarkerUtil.getErrorCode(marker));
		assertSameArgs(args, RodinMarkerUtil.getArguments(marker));
		assertEquals(elem, RodinMarkerUtil.getElement(marker));
		assertEquals(elem instanceof IInternalElement ? elem : null,
				RodinMarkerUtil.getInternalElement(marker));
		assertEquals(attrType, RodinMarkerUtil.getAttributeType(marker));
		// Normalize invalid position
		if (charStart < 0)
			charStart = -1;
		if (charEnd < 0)
			charEnd = -1;
		assertEquals(charStart, RodinMarkerUtil.getCharStart(marker));
		assertEquals(charEnd, RodinMarkerUtil.getCharEnd(marker));
	}

	private void createMarkerPositive(IRodinElement elem, IRodinProblem pb,
			Object... args) throws Exception {

		elem.createProblemMarker(pb, args);
		assertProblemMarker(elem, null, -1, -1, pb, args);
	}

	private void createMarkerPositive(IInternalElement elem,
			IAttributeType attrType, IRodinProblem pb, Object... args)
			throws Exception {

		elem.createProblemMarker(attrType, pb, args);
		assertProblemMarker(elem, attrType, -1, -1, pb, args);
	}

	private void createMarkerPositive(IInternalElement elem,
			IAttributeType.String attrType, int charStart, int charEnd,
			IRodinProblem pb, Object... args) throws Exception {

		elem.createProblemMarker(attrType, charStart, charEnd, pb, args);
		assertProblemMarker(elem, attrType, charStart, charEnd, pb, args);
	}

	private void assertException(int code, Runnable runnable) {
		try {
			runnable.run();
			fail("Operation should have raised en exception");
		} catch (RodinDBException re) {
			assertEquals("Wrong status code for exception", code, re
					.getStatus().getCode());
		}
	}

	private void createMarkerNegative(int code, final IRodinElement elem,
			final IRodinProblem pb, final Object... args) throws Exception {

		assertException(code, new Runnable() {
			public void run() throws RodinDBException {
				elem.createProblemMarker(pb, args);
			}
		});
		assertNoMarker();
	}

	private void createMarkerNegative(int code, final IInternalElement elem,
			final IAttributeType attrType, final IRodinProblem pb,
			final Object... args) throws Exception {

		assertException(code, new Runnable() {
			public void run() throws RodinDBException {
				elem.createProblemMarker(attrType, pb, args);
			}
		});
		assertNoMarker();
	}

	private void createMarkerNegative(int code, final IInternalElement elem,
			final IAttributeType.String attrType, final int charStart,
			final int charEnd, final IRodinProblem pb, final Object... args)
			throws Exception {

		assertException(code, new Runnable() {
			public void run() throws RodinDBException {
				elem
						.createProblemMarker(attrType, charStart, charEnd, pb,
								args);
			}
		});
		assertNoMarker();
	}

	/**
	 * Ensures that a problem marker can be set on the Rodin database.
	 */
	public void testDBMarker() throws Exception {
		IRodinDB db = getRodinDB();
		createMarkerPositive(db, TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker can be set on a Rodin Project.
	 */
	public void testProjectMarker() throws Exception {
		createMarkerPositive(rodinProject, TestProblem.warn1, "1");
	}

	/**
	 * Ensures that a problem marker cannot be set on an inexistent Rodin
	 * Project.
	 */
	public void testProjectMarkerInexistent() throws Exception {
		IRodinProject rp = getRodinProject("Inexistent");
		createMarkerNegative(ELEMENT_DOES_NOT_EXIST, rp, TestProblem.warn1, "1");
	}

	/**
	 * Ensures that a problem marker can be set on a Rodin File.
	 */
	public void testFileMarker() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		createMarkerPositive(rodinFile, TestProblem.info2, 1, "");
	}

	/**
	 * Ensures that a problem marker cannot be set on an inexistent Rodin File.
	 */
	public void testFileMarkerInexistent() throws Exception {
		IRodinFile rodinFile = getRodinFile("P/inexistent.test");
		createMarkerNegative(ELEMENT_DOES_NOT_EXIST, rodinFile,
				TestProblem.info2, 1, 2);
	}

	/**
	 * Ensures that a problem marker can be set on a top-level internal element.
	 */
	public void testTopMarker() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		rodinFile.save(null, false);
		createMarkerPositive(ne, TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker cannot be set on an inexistent top-level
	 * internal element.
	 */
	public void testTopMarkerInexistent() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = getNamedElement(root, "ne1");
		createMarkerNegative(ELEMENT_DOES_NOT_EXIST, ne, TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker can be set on an attribute of a top-level
	 * internal element.
	 */
	public void testTopMarkerAttr() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		ne.setAttributeValue(fString, "bar", null);
		rodinFile.save(null, false);
		createMarkerPositive(ne, fString, TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker cannot be set on an attribute of an
	 * inexistent top-level internal element.
	 */
	public void testTopMarkerAttrInexistent() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = getNamedElement(root, "ne1");
		createMarkerNegative(ELEMENT_DOES_NOT_EXIST, ne, fString,
				TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker can be set on an attribute of a top-level
	 * internal element, together with a location.
	 */
	public void testTopMarkerAttrLoc() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		ne.setAttributeValue(fString, "bar", null);
		rodinFile.save(null, false);
		createMarkerPositive(ne, fString, 0, 3, TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker cannot be set on an attribute of an
	 * inexistent top-level internal element, together with a location.
	 */
	public void testTopMarkerAttrLocInexistent() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = getNamedElement(root, "ne1");
		createMarkerNegative(ELEMENT_DOES_NOT_EXIST, ne, fString, 0, 3,
				TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker can be set on an attribute of a non
	 * top-level internal element, together with a location.
	 */
	public void testIntMarkerAttrLoc() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne1 = createNEPositive(root, "ne1", null);
		IInternalElement ne11 = createNEPositive(ne1, "ne11", null);
		ne11.setAttributeValue(fString, "baz", null);
		rodinFile.save(null, false);
		createMarkerPositive(ne11, fString, 0, 3, TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker cannot be set on an attribute of an
	 * inexistent non top-level internal element, together with a location.
	 */
	public void testIntMarkerAttrLocInexistent() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne1 = createNEPositive(root, "ne1", null);
		rodinFile.save(null, false);
		IInternalElement ne11 = getNamedElement(ne1, "ne11");
		createMarkerNegative(ELEMENT_DOES_NOT_EXIST, ne11, fString, 0, 3,
				TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker can be set on an inexistent attribute of an
	 * internal element.
	 */
	public void testMarkerAttrInexistent() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		rodinFile.save(null, false);
		createMarkerPositive(ne, fString, TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker cannot be set on an inexistent attribute of
	 * an internal element, together with a location.
	 */
	public void testMarkerAttrLocInexistent() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		rodinFile.save(null, false);
		createMarkerNegative(ATTRIBUTE_DOES_NOT_EXIST, ne, fString, 0, 3,
				TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker can be set on an attribute of an internal
	 * element, when the attribute is of kind "boolean".
	 */
	public void testMarkerAttrBool() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		ne.setAttributeValue(fBool, true, null);
		rodinFile.save(null, false);
		createMarkerPositive(ne, fBool, TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker can be set on an attribute of an internal
	 * element, when the attribute is of kind "handle".
	 */
	public void testMarkerAttrHandle() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		ne.setAttributeValue(fHandle, root, null);
		rodinFile.save(null, false);
		createMarkerPositive(ne, fHandle, TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker can be set on an attribute of an internal
	 * element, when the attribute is of kind "integer".
	 */
	public void testMarkerAttrInt() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		ne.setAttributeValue(fInt, -55, null);
		rodinFile.save(null, false);
		createMarkerPositive(ne, fInt, TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker can be set on an attribute of an internal
	 * element, when the attribute is of kind "long".
	 */
	public void testMarkerAttrLong() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		ne.setAttributeValue(fLong, 12345678901L, null);
		rodinFile.save(null, false);
		createMarkerPositive(ne, fLong, TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker cannot be set with a location but no
	 * attribute.
	 */
	public void testMarkerNoAttrLoc() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		rodinFile.save(null, false);
		createMarkerNegative(INVALID_MARKER_LOCATION, ne, nullType, 0, 3,
				TestProblem.err0);
		createMarkerNegative(INVALID_MARKER_LOCATION, ne, nullType, -1, 3,
				TestProblem.err0);
		createMarkerNegative(INVALID_MARKER_LOCATION, ne, nullType, 0, -1,
				TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker cannot be set with a partial location.
	 */
	public void testMarkerAttrBadLoc() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		ne.setAttributeValue(fString, "bar", null);
		rodinFile.save(null, false);
		// no end location
		createMarkerNegative(INVALID_MARKER_LOCATION, ne, fString, 0, -1,
				TestProblem.err0);

		// no start location
		createMarkerNegative(INVALID_MARKER_LOCATION, ne, fString, -1, 0,
				TestProblem.err0);

		// end before start location
		createMarkerNegative(INVALID_MARKER_LOCATION, ne, fString, 4, 2,
				TestProblem.err0);

		// equal start and end location
		createMarkerNegative(INVALID_MARKER_LOCATION, ne, fString, 2, 2,
				TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker can be set with a null attribute id.
	 */
	public void testMarkerAttrNull() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		rodinFile.save(null, false);
		createMarkerPositive(ne, (IAttributeType) null, TestProblem.err0);
	}

	/**
	 * Ensures that a problem marker can be set with a null attribute id.
	 */
	public void testMarkerAttrLocNull() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		IInternalElement ne = createNEPositive(root, "ne1", null);
		rodinFile.save(null, false);
		createMarkerPositive(ne, nullType, -5, -2, TestProblem.err0);
	}

	/**
	 * Ensures that the data stored in a marker on a top-level internal element
	 * is still relevant after moving its file.
	 */
	public void testTopMarkerMoveFile() throws Exception {
		IRodinProject rp2 = createRodinProject("P2");
		IRodinFile rf = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		IInternalElement top = createNEPositive(root, "ne1", null);
		top.setAttributeValue(fString, "foo", null);
		rf.save(null, false);
		createMarkerPositive(top, fString, 0, 3, TestProblem.err0);

		rf.move(rp2, null, null, false, null);
		IRodinFile rf2 = getRodinFile(rp2, "x.test");
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();
		IInternalElement top2 = getNamedElement(root2, "ne1");
		assertProblemMarker(top2, fString, 0, 3, TestProblem.err0);
	}

	/**
	 * Ensures that the data stored in a marker on a non top-level internal
	 * element is still relevant after moving its file.
	 */
	public void testIntMarkerMoveFile() throws Exception {
		IRodinProject rp2 = createRodinProject("P2");
		IRodinFile rf = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		IInternalElement top = createNEPositive(root, "ne1", null);
		IInternalElement ne = createNEPositive(top, "ne11", null);
		ne.setAttributeValue(fString, "bar", null);
		rf.save(null, false);
		createMarkerPositive(ne, fString, 0, 1, TestProblem.warn1, "baz");

		rf.move(rp2, null, null, false, null);
		IRodinFile rf2 = getRodinFile(rp2, "x.test");
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();
		IInternalElement top2 = getNamedElement(root2, "ne1");
		IInternalElement ne2 = getNamedElement(top2, "ne11");
		assertProblemMarker(ne2, fString, 0, 1, TestProblem.warn1, "baz");
	}

	/**
	 * Ensures that the data stored in a marker on a top-level internal element
	 * is still relevant after moving its file with renaming.
	 */
	public void testTopMarkerMoveFileRenaming() throws Exception {
		IRodinProject rp2 = createRodinProject("P2");
		IRodinFile rf = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		IInternalElement top = createNEPositive(root, "ne1", null);
		top.setAttributeValue(fString, "foo", null);
		rf.save(null, false);
		createMarkerPositive(top, fString, 0, 3, TestProblem.err0);

		rf.move(rp2, null, "y.test", false, null);
		IRodinFile rf2 = getRodinFile(rp2, "y.test");
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();
		IInternalElement top2 = getNamedElement(root2, "ne1");
		assertProblemMarker(top2, fString, 0, 3, TestProblem.err0);
	}

	/**
	 * Ensures that the data stored in a marker on a non top-level internal
	 * element is still relevant after moving its file with renaming.
	 */
	public void testIntMarkerMoveFileRenaming() throws Exception {
		IRodinProject rp2 = createRodinProject("P2");
		IRodinFile rf = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		IInternalElement top = createNEPositive(root, "ne1", null);
		IInternalElement ne = createNEPositive(top, "ne11", null);
		ne.setAttributeValue(fString, "bar", null);
		rf.save(null, false);
		createMarkerPositive(ne, fString, 0, 1, TestProblem.warn1, "baz");

		rf.move(rp2, null, "y.test", false, null);
		IRodinFile rf2 = getRodinFile(rp2, "y.test");
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();
		IInternalElement top2 = getNamedElement(root2, "ne1");
		IInternalElement ne2 = getNamedElement(top2, "ne11");
		assertProblemMarker(ne2, fString, 0, 1, TestProblem.warn1, "baz");
	}

	/**
	 * Ensures that a marker on a top-level internal element is not copied with
	 * its Rodin file.
	 */
	public void testTopMarkerCopyFile() throws Exception {
		IRodinFile rf = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		IInternalElement top = createNEPositive(root, "ne1", null);
		top.setAttributeValue(fString, "foo", null);
		rf.save(null, false);
		createMarkerPositive(top, fString, 0, 3, TestProblem.err0);

		rf.copy(rodinProject, null, "y.test", false, null);
		IRodinFile rf2 = getRodinFile(rodinProject, "y.test");
		assertNoMarker(rf2.getCorrespondingResource());
	}

	/**
	 * Ensures that a marker on a non top-level internal element is not copied
	 * with its Rodin file.
	 */
	public void testIntMarkerCopyFile() throws Exception {
		IRodinFile rf = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		IInternalElement top = createNEPositive(root, "ne1", null);
		IInternalElement ne = createNEPositive(top, "ne11", null);
		ne.setAttributeValue(fString, "bar", null);
		rf.save(null, false);
		createMarkerPositive(ne, fString, 0, 1, TestProblem.warn1, "baz");

		rf.copy(rodinProject, null, "y.test", false, null);
		IRodinFile rf2 = getRodinFile(rodinProject, "y.test");
		assertNoMarker(rf2.getCorrespondingResource());
	}

	/**
	 * Ensures that the data stored in a marker on a top-level internal element
	 * is still relevant after renaming its project.
	 */
	public void testTopMarkerRenameProject() throws Exception {
		IRodinFile rf = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		IInternalElement top = createNEPositive(root, "ne1", null);
		top.setAttributeValue(fString, "foo", null);
		rf.save(null, false);
		createMarkerPositive(top, fString, 0, 3, TestProblem.err0);

		IRodinProject rp2 = getRodinProject("P2");
		rodinProject.getProject().move(rp2.getPath(), false, null);
		IRodinFile rf2 = getRodinFile(rp2, "x.test");
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();
		IInternalElement top2 = getNamedElement(root2, "ne1");
		assertProblemMarker(top2, fString, 0, 3, TestProblem.err0);
	}

	/**
	 * Ensures that the data stored in a marker on a top-level internal element
	 * is still relevant after renaming its file.
	 */
	public void testTopMarkerRenameFile() throws Exception {
		IRodinFile rf = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		IInternalElement top = createNEPositive(root, "ne1", null);
		top.setAttributeValue(fString, "foo", null);
		rf.save(null, false);
		createMarkerPositive(top, fString, 0, 3, TestProblem.err0);

		rf.rename("y.test", false, null);
		IRodinFile rf2 = getRodinFile(rodinProject, "y.test");
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();
		IInternalElement top2 = getNamedElement(root2, "ne1");
		assertProblemMarker(top2, fString, 0, 3, TestProblem.err0);
	}

	/**
	 * Ensures that the data stored in a marker on a non top-level internal
	 * element is still relevant after renaming its file.
	 */
	public void testIntMarkerRenameFile() throws Exception {
		IRodinFile rf = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		IInternalElement top = createNEPositive(root, "ne1", null);
		IInternalElement ne = createNEPositive(top, "ne11", null);
		ne.setAttributeValue(fString, "bar", null);
		rf.save(null, false);
		createMarkerPositive(ne, fString, 0, 1, TestProblem.warn1, "baz");

		rf.rename("y.test", false, null);
		IRodinFile rf2 = getRodinFile(rodinProject, "y.test");
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();
		IInternalElement top2 = getNamedElement(root2, "ne1");
		IInternalElement ne2 = getNamedElement(top2, "ne11");
		assertProblemMarker(ne2, fString, 0, 1, TestProblem.warn1, "baz");
	}

	/**
	 * Ensure that mapping from IRodinProblem to error code works in both ways.
	 */
	public void testProblem() {
		assertEquals("Unexpected error code", PLUGIN_ID + ".err0",
				TestProblem.err0.getErrorCode());
		assertEquals("Unexpected problem object", TestProblem.warn1,
				TestProblem.valueOfErrorCode(TestProblem.warn1.getErrorCode()));
	}

	/**
	 * Ensures that passing a null marker to an inquiry method throws an
	 * exception.
	 */
	public void testNullMarkerAccess() throws Exception {
		for (MarkerMethod m: MarkerMethod.values()) {
			m.invokeNull();
		}
	}

	/**
	 * Ensures that passing a non-Rodin marker throws an exception.
	 */
	public void testNonRodinMarker() throws Exception {
		final IProject project = rodinProject.getProject();
		final IMarker marker = project.createMarker(IMarker.MARKER);
		for (MarkerMethod m: MarkerMethod.values()) {
			m.invokeNonRodin(marker);
		}
	}
	
	/**
	 * Ensures that passing an inexistent marker returns null.
	 */
	public void testInexistentMarker() throws Exception {
		rodinProject.createProblemMarker(TestProblem.err0);
		final IMarker marker = getMarker(rodinProject, RODIN_PROBLEM_MARKER);
		marker.delete();
		for (MarkerMethod m: MarkerMethod.values()) {
			m.invokeDefaultResult(marker, rodinProject);
		}
	}
	
	private enum DefaultResult {
		NULL {
			@Override
			public void check(Object result, IRodinElement element) {
				assertNull(result);
			}
		}, ELEMENT {
			@Override
			public void check(Object result, IRodinElement element) {
				assertEquals(element, result);
			}
		}, MINUS_ONE {
			@Override
			public void check(Object result, IRodinElement element) {
				assertEquals(-1, result);
			}
		};
		
		public abstract void check(Object result, IRodinElement element);
	}
	
	private enum MarkerMethod {

		GET_ARGUMENTS("getArguments", DefaultResult.NULL), //
		GET_ERROR_CODE("getErrorCode", DefaultResult.NULL), //
		GET_ELEMENT("getElement", DefaultResult.ELEMENT), //
		GET_INTERNAL_ELEMENT("getInternalElement", DefaultResult.NULL), //
		GET_ATTRIBUTE_TYPE("getAttributeType", DefaultResult.NULL), //
		GET_CHAR_START("getCharStart", DefaultResult.MINUS_ONE), //
		GET_CHAR_END("getCharEnd", DefaultResult.MINUS_ONE), //
		;

		public final Method method;
		public final DefaultResult defaultResult;

		private MarkerMethod(String methodName, DefaultResult dr) {
			Method m = null;
			try {
				m = RodinMarkerUtil.class.getMethod(methodName, IMarker.class);
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.toString());
			}
			this.method = m;
			this.defaultResult = dr;
		}

		public void invokeNull() throws Exception {
			try {
				method.invoke(null, (Object) null);
				fail("Should have raised an exception");
			} catch (InvocationTargetException e) {
				assertTrue(e.getCause() instanceof NullPointerException);
			}
		}

		public void invokeNonRodin(IMarker marker) throws Exception {
			try {
				method.invoke(null, marker);
				fail("Should have raised an exception");
			} catch (InvocationTargetException e) {
				assertTrue(e.getCause() instanceof IllegalArgumentException);
			}
		}
		
		public void invokeDefaultResult(IMarker marker, IRodinElement element)
				throws Exception {
			defaultResult.check(method.invoke(null, marker), element);
		}

		public static void checkPresent(Method other) {
			for (MarkerMethod mm: values()) {
				if (other.equals(mm.method)) {
					return;
				}
			}
			fail("Method " + other.getName() + " is missing.");
		}
	}

	// Ensures that all marker methods are in the MarkerMethod enumeration
	static {
		for (Method method : RodinMarkerUtil.class.getMethods()) {
			Class<?>[] types = method.getParameterTypes();
			if (types.length == 1 && types[0] == IMarker.class) {
				MarkerMethod.checkPresent(method);
			}
		}
		
	}

}
