/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.tests.model.CopyMoveResourcesTests
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.RodinTestRoot;

public class CopyMoveResourcesTests extends CopyMoveTests {

	// TODO add tests with two operations done at the same time
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		createRodinProject("P");
		createRodinProject("P2");
	}
	
	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		deleteProject("P2");
		
		super.tearDown();
	}

	/**
	 * Ensures that a Rodin file can be copied to a different project.
	 */
	@Test
	public void testCopyRF() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		
		copyPositive(rfSource, getRodinProject("P2"), null, null, false);
	}
	
	/**
	 * This operation should fail as copying a Rodin file and an internal element at the
	 * same time is not supported.
	 */
	@Test
	public void testCopyRFAndInternal() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		NamedElement ne = createNEPositive(root, "foo", null);
		rfSource.save(null, false);
		
		copyNegative(
				new IRodinElement[]{rfSource, ne}, 
				new IRodinElement[]{rfSource.getParent(), rfSource}, 
				null, 
				new String[]{"Y.test", "bar"}, 
				false, 
				IRodinDBStatusConstants.INVALID_ELEMENT_TYPES);
	}
	
	/**
	 * Ensures that a Rodin file can be copied to a different project, replacing an existing Rodin file.
	 */
	@Test
	public void testCopyRFForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		createNEPositive(root, "foo", null);
		rfSource.save(null, false);

		IRodinFile rfDest = createRodinFile("P2/X.test");
		RodinTestRoot rootDest = (RodinTestRoot) rfDest.getRoot();
		createNEPositive(rootDest, "bar", null);
		// Destination file is left unsaved
		
		copyPositive(rfSource, rfDest.getParent(), null, null, true);
		assertElementDescendants("Internal element not copied with its container",
				"X.test\n" + 
				"  X[org.rodinp.core.tests.test]\n" +
				"    foo[org.rodinp.core.tests.namedElement]",
				rfDest);
	}
	
	/**
	 * Ensures that a Rodin file can be copied to a different project,
	 * and be renamed.
	 */
	@Test
	public void testCopyRFRename() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		createNEPositive(root, "foo", null);
		rfSource.save(null, false);

		copyPositive(rfSource, getRodinProject("P2"), null, "Y.test", false);
	}

	/**
	 * Ensures that copying a file onto itself is a no-op.
	 */
	@Test
	public void testCopyRFNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		
		copyNoop(rfSource, null);
	}

	/**
	 * Ensures that a read-only Rodin file can be copied to a different project.
	 */
	@Test
	public void testCopyRFReadOnly() throws CoreException {
		IFile file = null;
		try {
			IRodinFile rfSource = createRodinFile("/P/X.test");
			file = rfSource.getResource();
			setReadOnly(file, true);
			assertTrue("can't set file as read only", file.isReadOnly());
			assertTrue("Rodin file element should be read only", 
					rfSource.isReadOnly());
			
			copyPositive(rfSource, getRodinProject("P2"), null, null, false);
			
			assertTrue("Destination file should be read-only", 
					getFile("/P2/X.test").isReadOnly());
			assertTrue("Destination rodin file element should be read only",
					getRodinFile("/P2/X.test").isReadOnly());
			
		} finally {
			if (file != null) {
				setReadOnly(file, false);
				// For Mac OS X, we also need to reset the read-only flag on the
				// target file, otherwise the OS doesn't allow us to delete the
				// target project.
				try {
					setReadOnly(getFile("/P2/X.test"), false);
				} catch (CoreException ce) {
					ce.printStackTrace();
				}
			}
		}
	}

	/**
	 * Ensures that a Rodin file can be copied to a different project,
	 * and be renamed, overwriting an existing Rodin file.
	 */
	@Test
	public void testCopyRFRenameForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		createNEPositive(root, "foo", null);
		rfSource.save(null, false);

		IRodinFile rfDest = createRodinFile("P2/Y.test");
		RodinTestRoot rootDest = (RodinTestRoot) rfDest.getRoot();
		createNEPositive(rootDest, "bar", null);
		// Destination file is left unsaved
		
		copyPositive(rfSource, rfDest.getParent(), null, "Y.test", true);
		assertElementDescendants("Internal element not copied with its container",
				"Y.test\n" + 
				"  Y[org.rodinp.core.tests.test]\n" +
				"    foo[org.rodinp.core.tests.namedElement]",
				rfDest);
	}

	/**
	 * Ensures that a Rodin file cannot be copied to a different project,
	 * over an existing Rodin file when no force.
	 */
	@Test
	public void testCopyRFWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		createNEPositive(root, "foo", null);
		rfSource.save(null, false);

		IRodinFile rfDest = createRodinFile("P2/X.test");
		RodinTestRoot rootDest = (RodinTestRoot) rfDest.getRoot();
		createNEPositive(rootDest, "bar", null);
		// Destination file is left unsaved
		
		copyNegative(rfSource, rfDest.getParent(), null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
		assertElementDescendants("Destination file should not have changed",
				"X.test\n" + 
				"  X[org.rodinp.core.tests.test]\n" +
				"    bar[org.rodinp.core.tests.namedElement]",
				rfDest);
	}
	
	/**
	 * Ensures that a Rodin file cannot be copied to an invalid destination
	 */
	@Test
	public void testCopyRFWithInvalidDestination() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		copyNegative(rfSource, rfSource, null, null, false, IRodinDBStatusConstants.INVALID_DESTINATION);
	}
	
	/**
	 * Ensures that a Rodin file cannot be copied to a null container
	 */
	@Test
	public void testCopyRFWithNullContainer() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		try {
			rfSource.copy(null, null, null, false, null);
		} catch (IllegalArgumentException iae) {
			return;
		}
		assertTrue("Should not be able to copy a rf to a null container", false);
	}
	
	/**
	 * Ensures that a Rodin file can be copied to along with its server properties.
	 * (Regression test for PR #1G56QT9)
	 */
	@Test
	public void testCopyRFWithServerProperties() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		
		QualifiedName qualifiedName = new QualifiedName("x.y.z", "a property");
		rfSource.getUnderlyingResource().setPersistentProperty(
				qualifiedName,
				"some value");
		
		copyPositive(rfSource, getRodinProject("P2"), null, null, false);
		IRodinFile rf = getRodinFile("P2/X.test");
		String propertyValue = rf.getUnderlyingResource().getPersistentProperty(qualifiedName);
		assertEquals(
				"Server property should be copied with rf",
				"some value",
				propertyValue
		);
	}
	
//	/**
//	 * Ensures that a WorkingCopy can be copied to a different package.
//	 */
//	public void testCopyWorkingCopy() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			this.createFolder("/P/src/p1");
//			this.createFile(
//					"/P/src/p1/X.test",
//					"package p1;\n" +
//					"public class X {\n" +
//					"}"
//			);
//			ICompilationUnit cuSource = getCompilationUnit("/P/src/p1/X.test");
//			copy = cuSource.getWorkingCopy(null);
//			
//			this.createFolder("/P/src/p2");
//			IPackageFragment pkgDest = getPackage("/P/src/p2");
//			
//			copyPositive(copy, pkgDest, null, null, false);
//		} finally {
//			if (copy != null) copy.discardWorkingCopy();
//		}
//	}
//	/**
//	 * Ensures that a WorkingCopy can be copied to a different package, replacing an existing WorkingCopy.
//	 */
//	public void testCopyWorkingCopyForce() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			this.createFolder("/P/src/p1");
//			this.createFile(
//					"/P/src/p1/X.test",
//					"package p1;\n" +
//					"public class X {\n" +
//					"}"
//			);
//			ICompilationUnit cuSource = getCompilationUnit("/P/src/p1/X.test");
//			copy = cuSource.getWorkingCopy(null);
//			
//			this.createFolder("/P/src/p2");
//			this.createFile(
//					"/P/src/p2/X.test",
//					"package p2;\n" +
//					"public class X {\n" +
//					"}"
//			);
//			IPackageFragment pkgDest = getPackage("/P/src/p2");
//			
//			copyPositive(copy, pkgDest, null, null, true);
//		} finally {
//			if (copy != null) copy.discardWorkingCopy();
//		}
//	}
//	/**
//	 * Ensures that a WorkingCopy can be copied to a different package,
//	 * and be renamed.
//	 */
//	public void testCopyWorkingCopyRename() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			this.createFolder("/P/src/p1");
//			this.createFile(
//					"/P/src/p1/X.test",
//					"package p1;\n" +
//					"public class X {\n" +
//					"}"
//			);
//			ICompilationUnit cuSource = getCompilationUnit("/P/src/p1/X.test");
//			copy = cuSource.getWorkingCopy(null);
//			
//			this.createFolder("/P/src/p2");
//			IPackageFragment pkgDest = getPackage("/P/src/p2");
//			
//			copyPositive(copy, pkgDest, null, "Y.test", false);
//		} finally {
//			if (copy != null) copy.discardWorkingCopy();
//		}
//	}
//	/**
//	 * Ensures that a WorkingCopy can be copied to a different package,
//	 * and be renamed, overwriting an existing WorkingCopy
//	 */
//	public void testCopyWorkingCopyRenameForce() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			this.createFolder("/P/src/p1");
//			this.createFile(
//					"/P/src/p1/X.test",
//					"package p1;\n" +
//					"public class X {\n" +
//					"}"
//			);
//			ICompilationUnit cuSource = getCompilationUnit("/P/src/p1/X.test");
//			copy = cuSource.getWorkingCopy(null);
//			
//			this.createFolder("/P/src/p2");
//			this.createFile(
//					"/P/src/p2/Y.test",
//					"package p2;\n" +
//					"public class Y {\n" +
//					"}"
//			);
//			IPackageFragment pkgDest = getPackage("/P/src/p2");
//			
//			copyPositive(copy, pkgDest, null, "Y.test", true);
//		} finally {
//			if (copy != null) copy.discardWorkingCopy();
//		}
//	}
//	/**
//	 * Ensures that a WorkingCopy cannot be copied to a different package,over an existing WorkingCopy when no force.
//	 */
//	public void testCopyWorkingCopyWithCollision() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			this.createFolder("/P/src/p1");
//			this.createFile(
//					"/P/src/p1/X.test",
//					"package p1;\n" +
//					"public class X {\n" +
//					"}"
//			);
//			ICompilationUnit cuSource = getCompilationUnit("/P/src/p1/X.test");
//			copy = cuSource.getWorkingCopy(null);
//			
//			this.createFolder("/P/src/p2");
//			this.createFile(
//					"/P/src/p2/X.test",
//					"package p2;\n" +
//					"public class X {\n" +
//					"}"
//			);
//			IPackageFragment pkgDest = getPackage("/P/src/p2");
//			
//			copyNegative(copy, pkgDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
//		} finally {
//			if (copy != null) copy.discardWorkingCopy();
//		}
//	}
//	/**
//	 * Ensures that a WorkingCopy cannot be copied to an invalid destination
//	 */
//	public void testCopyWorkingCopyWithInvalidDestination() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			this.createFolder("/P/src/p1");
//			this.createFile(
//					"/P/src/p1/X.test",
//					"package p1;\n" +
//					"public class X {\n" +
//					"}"
//			);
//			ICompilationUnit cuSource = getCompilationUnit("/P/src/p1/X.test");
//			copy = cuSource.getWorkingCopy(null);
//			
//			copyNegative(copy, cuSource, null, null, false, IRodinDBStatusConstants.INVALID_DESTINATION);
//		} finally {
//			if (copy != null) copy.discardWorkingCopy();
//		}
//	}

	/**
	 * Ensures that a RF can be moved to a different project.
	 */
	@Test
	public void testMoveRF() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		IRodinProject prjDest = getRodinProject("P2");
		
		movePositive(rfSource, prjDest, null, null, false);
	}

	/**
	 * This operation should fail as moving a Rodin file and an internal element at the
	 * same time is not supported.
	 */
	@Test
	public void testMoveRFAndInternal() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		NamedElement ne = createNEPositive(root, "foo", null);
		rfSource.save(null, false);
		
		moveNegative(
				new IRodinElement[]{rfSource, ne}, 
				new IRodinElement[]{rfSource.getParent(), rfSource}, 
				null, 
				new String[]{"Y.test", "bar"}, 
				false, 
				IRodinDBStatusConstants.INVALID_ELEMENT_TYPES);
	}
	
	/**
	 * Ensures that a Rodin file can be moved to a different project, replacing an existing Rodin file.
	 */
	@Test
	public void testMoveRFForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		createNEPositive(root, "foo", null);
		rfSource.save(null, false);

		IRodinFile rfDest = createRodinFile("P2/X.test");
		RodinTestRoot rootDest = (RodinTestRoot) rfDest.getRoot();
		createNEPositive(rootDest, "bar", null);
		// Destination file is left unsaved
		
		movePositive(rfSource, rfDest.getParent(), null, null, true);
		assertElementDescendants("Internal element not copied with its container",
				"X.test\n" + 
				"  X[org.rodinp.core.tests.test]\n" +
				"    foo[org.rodinp.core.tests.namedElement]",
				rfDest);
	}

	/**
	 * Ensures that moving a file onto itself is a no-op.
	 */
	@Test
	public void testMoveRFNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		
		moveNoop(rfSource, null);
	}

	/**
	 * Ensures that a Rodin file can be moved to a different project,
	 * and be renamed.
	 */
	@Test
	public void testMoveRFRename() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		createNEPositive(root, "foo", null);
		rfSource.save(null, false);

		movePositive(rfSource, getRodinProject("P2"), null, "Y.test", false);
	}

	/**
	 * Ensures that a Rodin file can be moved to a different project,
	 * and be renamed, overwriting an existing Rodin file.
	 */
	@Test
	public void testMoveRFRenameForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		createNEPositive(root, "foo", null);
		rfSource.save(null, false);

		IRodinFile rfDest = createRodinFile("P2/Y.test");
		RodinTestRoot rootDest = (RodinTestRoot) rfDest.getRoot();
		createNEPositive(rootDest, "bar", null);
		// Destination file is left unsaved
		
		movePositive(rfSource, rfDest.getParent(), null, "Y.test", true);
		assertElementDescendants("Internal element not copied with its container",
				"Y.test\n" + 
				"  Y[org.rodinp.core.tests.test]\n" +
				"    foo[org.rodinp.core.tests.namedElement]",
				rfDest);
	}

	/**
	 * Ensures that a Rodin file cannot be moved to a different project,
	 * over an existing Rodin file when no force.
	 */
	@Test
	public void testMoveRFWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		createNEPositive(root, "foo", null);
		rfSource.save(null, false);

		IRodinFile rfDest = createRodinFile("P2/X.test");
		RodinTestRoot rootDest = (RodinTestRoot) rfDest.getRoot();
		createNEPositive(rootDest, "bar", null);
		// Destination file is left unsaved
		
		moveNegative(rfSource, rfDest.getParent(), null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
		assertElementDescendants("Destination file should not have changed",
				"X.test\n" + 
				"  X[org.rodinp.core.tests.test]\n" +
				"    bar[org.rodinp.core.tests.namedElement]",
				rfDest);
	}
	
	/**
	 * Ensures that a Rodin file cannot be moved to an invalid destination
	 */
	@Test
	public void testMoveRFWithInvalidDestination() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		moveNegative(rfSource, rfSource, null, null, false, IRodinDBStatusConstants.INVALID_DESTINATION);
	}
	
	/**
	 * Ensures that a Rodin file cannot be moved to a null container
	 */
	@Test
	public void testMoveRFWithNullContainer() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		try {
			rfSource.move(null, null, null, false, null);
		} catch (IllegalArgumentException iae) {
			return;
		}
		assertTrue("Should not be able to move a rf to a null container", false);
	}
	
//	/**
//	 * Ensures that a WorkingCopy cannot be moved to a different package.
//	 */
//	public void testMoveWorkingCopy() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			this.createFolder("/P/src/p1");
//			this.createFile(
//					"/P/src/p1/X.test",
//					"package p1;\n" +
//					"public class X {\n" +
//					"}"
//			);
//			ICompilationUnit rfSource = getCompilationUnit("/P/src/p1/X.test");
//			copy = rfSource.getWorkingCopy(null);
//			
//			this.createFolder("/P/src/p2");
//			IPackageFragment pkgDest = getPackage("/P/src/p2");
//			
//			moveNegative(copy, pkgDest, null, null, false, IRodinDBStatusConstants.INVALID_ELEMENT_TYPES);
//		} finally {
//			if (copy != null) copy.discardWorkingCopy();
//		}
//	}
//	
//	/*
//	 * Ensures that a primary working copy can be moved to a different package
//	 * and that its buffer doesn't contain unsaved changed after the move.
//	 * (regression test for bug 83599 RF dirty after move refactoring)
//	 */
//	public void testMoveWorkingCopy2() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			this.createFolder("/P/src/p1");
//			this.createFile(
//					"/P/src/p1/X.test",
//					"package p1;\n" +
//					"public class X {\n" +
//					"}"
//			);
//			copy = getCompilationUnit("/P/src/p1/X.test");
//			copy.becomeWorkingCopy(null, null);
//			
//			this.createFolder("/P/src/p2");
//			IPackageFragment pkgDest = getPackage("/P/src/p2");
//			
//			movePositive(copy, pkgDest, null, null, false);
//			assertTrue("Should not have unsaved changes", !copy.getBuffer().hasUnsavedChanges());
//		} finally {
//			if (copy != null) copy.discardWorkingCopy();
//		}
//	}

	/**
	 * Ensures that a RF can be renamed.
	 */
	@Test
	public void testRenameRF() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		
		renamePositive(rfSource, "Y.test", false);
	}

	/**
	 * This operation should fail as renaming a Rodin file and an internal element at the
	 * same time is not supported.
	 */
	@Test
	public void testRenameRFAndInternal() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		NamedElement ne = createNEPositive(root, "foo", null);
		rfSource.save(null, false);
		
		renameNegative(
				new IRodinElement[]{rfSource, ne}, 
				new String[]{"Y.test", "bar"}, 
				false, 
				IRodinDBStatusConstants.INVALID_ELEMENT_TYPES);
	}
	
	/**
	 * Ensures that a Rodin file can be renamed, replacing an existing Rodin file.
	 */
	@Test
	public void testRenameRFForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		createNEPositive(root, "foo", null);
		rfSource.save(null, false);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		RodinTestRoot rootDest = (RodinTestRoot) rfDest.getRoot();
		createNEPositive(rootDest, "bar", null);
		// Destination file is left unsaved
		
		renamePositive(rfSource, rfDest.getElementName(), true);
		assertElementDescendants("Internal element not copied with its container",
				"Y.test\n" + 
				"  Y[org.rodinp.core.tests.test]\n" +
				"    foo[org.rodinp.core.tests.namedElement]",
				rfDest);
	}

	/**
	 * Ensures that renaming a file onto itself is a no-op.
	 */
	@Test
	public void testRenameRFNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		
		renameNoop(rfSource);
	}

	/**
	 * Ensures that a Rodin file cannot be renamed
	 * over an existing Rodin file when no force.
	 */
	@Test
	public void testRenameRFWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		RodinTestRoot root = (RodinTestRoot) rfSource.getRoot();
		createNEPositive(root, "foo", null);
		rfSource.save(null, false);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		RodinTestRoot rootDest = (RodinTestRoot) rfDest.getRoot();
		createNEPositive(rootDest, "bar", null);
		// Destination file is left unsaved
		
		renameNegative(rfSource, rfDest.getElementName(), false, IRodinDBStatusConstants.NAME_COLLISION);
		assertElementDescendants("Destination file should not have changed",
				"Y.test\n" + 
				"  Y[org.rodinp.core.tests.test]\n" +
				"    bar[org.rodinp.core.tests.namedElement]",
				rfDest);
	}
	
	/**
	 * Ensures that a Rodin file cannot be renamed to an invalid name
	 */
	@Test
	public void testRenameRFWithInvalidName() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");

		renameNegative(rfSource, "foo", false, IRodinDBStatusConstants.INVALID_NAME);
	}
	
	/**
	 * Ensures that a Rodin file cannot be renamed to a null name
	 */
	@Test
	public void testRenameRFWithNullName() throws CoreException {
		IRodinFile rfSource = createRodinFile("/P/X.test");
		renameNegative(rfSource, "foo", false, IRodinDBStatusConstants.INVALID_NAME);
	}
	
}
