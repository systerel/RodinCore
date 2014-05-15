/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.ISnapshotable;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.RodinTestRoot;

/**
 * Tests about Rodin file snapshots.
 * 
 * @author Laurent Voisin
 */
public class SnapshotTests extends ModifyingResourceTests {
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
	}
	
	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	/**
	 * Ensures that the isSnapshot() method works properly on Rodin files.
	 */
	@Test
	public void testFileIsSnapshot() {
		final IRodinFile rf = getRodinFile("P/X.test");
		testIsSnapshot(rf);
	}

	/**
	 * Ensures that a snapshot exists iff its underlying file exists.
	 */
	@Test
	public void testFileSnapshotExists() throws CoreException {
		final IRodinFile rf = getRodinFile("P/X.test");
		testSnapshotNotExists(rf);
		createRodinFile("P/X.test");
		testSnapshotExists(rf);
	}
	
	/**
	 * Ensures that the mutable copy of a snapshot is the original handle, and
	 * vice-versa. Also, ensures that a snapshot is different from a mutable
	 * copy.
	 */
	@Test
	public void testFileSnapshotInvolutive() {
		final IRodinFile rf = getRodinFile("P/X.test");
		testSnapshotInvolutive(rf);
	}
	
	/**
	 * Ensures that the mutable copy and a snapshot of a file have the same parent.
	 */
	@Test
	public void testFileSnapshotParent() {
		final IRodinFile rf = getRodinFile("P/X.test");
		assertSame("Parents of mutable and snapshot file should be the same",
				rf.getParent(),
				rf.getSnapshot().getParent()
		);
	}
	
	/**
	 * Ensures that a snapshot is readonly and a mutable copy is not.
	 */
	@Test
	public void testFileSnapshotReadonly() {
		final IRodinFile rf = getRodinFile("P/X.test");
		testSnapshotReadonly(rf);
	}
	
	/**
	 * Ensures that the isSnapshot() method works properly.
	 */
	private void testIsSnapshot(ISnapshotable mutable) {
		final ISnapshotable snapshot = mutable.getSnapshot();
		final ISnapshotable mutable2 = snapshot.getMutableCopy();
		assertFalse("Mutable should not be a snapshot", mutable.isSnapshot());
		assertTrue("Snapshot should be a snapshot", snapshot.isSnapshot());
		assertFalse("Mutable should not be a snapshot", mutable2.isSnapshot());
	}
	
	/**
	 * Ensures that a snapshot exists if its mutable copy exists.
	 */
	private void testSnapshotExists(ISnapshotable mutable) {
		final ISnapshotable snapshot = mutable.getSnapshot();
		assertExists("Mutable should exist", mutable);
		assertExists("Snapshot should exist", snapshot);
	}

	/**
	 * Ensures that the mutable copy of a snapshot is the original handle, and
	 * vice-versa. Also, ensures that a snapshot is different from a mutable
	 * copy.
	 */
	private void testSnapshotInvolutive(ISnapshotable mutable) {
		final ISnapshotable snapshot = mutable.getSnapshot();
		assertDiffers("Mutable and snapshot should differ",
				mutable, snapshot);
		assertSame("Snapshot of a snapshot should be identical",
				snapshot, snapshot.getSnapshot());
		assertSame("Mutable of a mutable should be identical",
				mutable, mutable.getMutableCopy());
		assertEquals("Mutable of a snapshot should be the mutable",
				mutable, snapshot.getMutableCopy());
		assertEquals("Snapshot of a mutable should be the snapshot",
				snapshot, snapshot.getMutableCopy().getSnapshot());
	}
	
	/**
	 * Ensures that a snapshot doesn't exist if its mutable copy doesn't.
	 */
	private void testSnapshotNotExists(ISnapshotable mutable) {
		final ISnapshotable snapshot = mutable.getSnapshot();
		assertNotExists("Mutable should not exist", mutable);
		assertNotExists("Snapshot should not exist", snapshot);
	}
	
	/**
	 * Ensures that a snapshot is readonly and a mutable copy is not.
	 */
	private void testSnapshotReadonly(ISnapshotable mutable) {
		final ISnapshotable snapshot = mutable.getSnapshot();
		final ISnapshotable mutable2 = snapshot.getMutableCopy();
		assertFalse("Mutable should not be readonly", mutable.isReadOnly());
		assertTrue("Snapshot should be readonly", snapshot.isReadOnly());
		assertFalse("Mutable should not be readonly", mutable2.isReadOnly());
	}
	
	/**
	 * Ensures that the isSnapshot() method works properly on non-top internal
	 * elements.
	 */
	@Test
	public void testIntIsSnapshot() {
		final IRodinFile rf = getRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final NamedElement e1 = getNamedElement(root, "foo"); 
		final NamedElement e11 = getNamedElement(e1, "bar"); 
		testIsSnapshot(e11);
	}

	/**
	 * Ensures that a snapshot exists iff its mutable copy exists.
	 */
	@Test
	public void testIntSnapshotExists() throws CoreException {
		final IRodinFile rf = getRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final NamedElement e1 = getNamedElement(root, "foo");
		final NamedElement e11 = getNamedElement(e1, "bar"); 
		testSnapshotNotExists(e11);
		createRodinFile("P/X.test");
		createNEPositive(root, "foo", null);
		createNEPositive(e1, "bar", null);
		rf.save(null, false);
		testSnapshotExists(e11);
	}
	
	/**
	 * Ensures that the mutable copy of a snapshot is the original handle, and
	 * vice-versa. Also, ensures that a snapshot is different from a mutable
	 * copy.
	 */
	@Test
	public void testIntSnapshotInvolutive() {
		final IRodinFile rf = getRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final NamedElement e1 = getNamedElement(root, "foo");
		final NamedElement e11 = getNamedElement(e1, "bar"); 
		testSnapshotInvolutive(e11);
	}
	
	/**
	 * Ensures that the mutable copy and a snapshot of a top-level internal
	 * element have the same parent.
	 */
	@Test
	public void testIntSnapshotParent() {
		final IRodinFile rf = getRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final NamedElement e1 = getNamedElement(root, "foo");
		final NamedElement e11 = getNamedElement(e1, "bar"); 
		assertDiffers("Parents of mutable and snapshot internal should differ",
				e11.getParent(), e11.getSnapshot().getParent()
		);
	}
	
	/**
	 * Ensures that a snapshot is readonly and a mutable copy is not.
	 */
	@Test
	public void testIntSnapshotReadonly() {
		final IRodinFile rf = getRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final NamedElement e1 = getNamedElement(root, "foo");
		final NamedElement e11 = getNamedElement(e1, "bar"); 
		testSnapshotReadonly(e11);
	}
	
	/**
	 * Ensures that the isSnapshot() method works properly on top internal
	 * elements.
	 */
	@Test
	public void testTopIsSnapshot() {
		final IRodinFile rf = getRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		testIsSnapshot(getNamedElement(root, "foo"));
	}

	/**
	 * Ensures that a snapshot exists iff its underlying file exists.
	 */
	@Test
	public void testTopSnapshotExists() throws CoreException {
		final IRodinFile rf = getRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final NamedElement e1 = getNamedElement(root, "foo");
		testSnapshotNotExists(e1);
		createRodinFile("P/X.test");
		createNEPositive(root, "foo", null);
		rf.save(null, false);
		testSnapshotExists(e1);
	}
	
	/**
	 * Ensures that the mutable copy of a snapshot is the original handle, and
	 * vice-versa. Also, ensures that a snapshot is different from a mutable
	 * copy.
	 */
	@Test
	public void testTopSnapshotInvolutive() {
		final IRodinFile rf = getRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final NamedElement e1 = getNamedElement(root, "foo");
		testSnapshotInvolutive(e1);
	}
	
	/**
	 * Ensures that the mutable copy and a snapshot of a top-level internal
	 * element have the same parent.
	 */
	@Test
	public void testTopSnapshotParent() {
		final IRodinFile rf = getRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final NamedElement e1 = getNamedElement(root, "foo");
		assertDiffers("Parents of mutable and snapshot internal should differ",
				e1.getParent(), e1.getSnapshot().getParent()
		);
	}
	
	/**
	 * Ensures that a snapshot is readonly and a mutable copy is not.
	 */
	@Test
	public void testTopSnapshotReadonly() {
		final IRodinFile rf = getRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final NamedElement e1 = getNamedElement(root, "foo");
		testSnapshotReadonly(e1);
	}
	
	/**
	 * Ensures that a snapshot is decorrelated from unsaved changes in a file
	 */
	@Test
	public void testSnapshotDecorrelated() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final NamedElement e1 = createNEPositive(root, "foo", null);
		final NamedElement e2 = createNEPositive(root, "bar", null);
		final NamedElement e11 = createNEPositive(e1, "baz", null); 
		rf.save(null, false);
		
		String frozenContents = 
			"  X[org.rodinp.core.tests.test]\n" + 
			"    foo[org.rodinp.core.tests.namedElement]\n" + 
			"      baz[org.rodinp.core.tests.namedElement]\n" + 
			"    bar[org.rodinp.core.tests.namedElement]";
		assertElementDescendants("Unexpected file contents",
				"X.test\n" + 
				frozenContents,
				rf);
		assertElementDescendants("Unexpected snapshot contents",
				"X.test!\n" + 
				frozenContents,
				rf.getSnapshot());
		
		e2.delete(false, null);
		assertElementDescendants("Unexpected file contents",
				"X.test\n" + 
				"  X[org.rodinp.core.tests.test]\n" + 
				"    foo[org.rodinp.core.tests.namedElement]\n" + 
				"      baz[org.rodinp.core.tests.namedElement]",
				rf);
		assertElementDescendants("Unexpected snapshot contents",
				"X.test!\n" + 
				frozenContents,
				rf.getSnapshot());

		e11.delete(false, null);
		assertElementDescendants("Unexpected file contents",
				"X.test\n" + 
				"  X[org.rodinp.core.tests.test]\n" + 
				"    foo[org.rodinp.core.tests.namedElement]",
				rf);
		assertElementDescendants("Unexpected snapshot contents",
				"X.test!\n" + 
				frozenContents,
				rf.getSnapshot());
	}
	
	/**
	 * Ensures that a snapshot internal element cannot be created.
	 */
	@Test
	public void testSnapshotCreateInternalElement() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final NamedElement e1 = createNEPositive(root, "foo", null);
		rf.save(null, false);

		createNENegative(root.getSnapshot(), "bar", null,
				IRodinDBStatusConstants.READ_ONLY);
		createNENegative(e1.getSnapshot(), "baz", null,
				IRodinDBStatusConstants.READ_ONLY); 
	}

	/**
	 * Ensures that a snapshot internal element cannot have its contents changed.
	 */
	@Test
	public void testSnapshotChangeInternalElementContents() throws CoreException {
		final IRodinFile rf = createRodinFile("P/X.test");
		final RodinTestRoot root = (RodinTestRoot) rf.getRoot();
		final NamedElement e1 = createNEPositive(root, "foo", null);
		assertContentsChanged(e1, "initial");
		final NamedElement e11 = createNEPositive(e1, "bar", null);
		assertContentsChanged(e11, "initial");
		rf.save(null, false);

		assertContentsNotChanged(e1.getSnapshot(), "other");
		assertContentsNotChanged(e11.getSnapshot(), "other");
	}

}
