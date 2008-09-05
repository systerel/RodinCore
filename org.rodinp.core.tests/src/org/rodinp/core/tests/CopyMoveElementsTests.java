/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation as
 * 			org.eclipse.jdt.core.tests.model.CopyMoveElementsTests
 *     ETH Zurich - adaptation from JDT to Rodin
 *     Systerel - fixed use of pseudo-attribute "contents"
 *******************************************************************************/
package org.rodinp.core.tests;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.basis.NamedElement;

public class CopyMoveElementsTests extends CopyMoveTests {

	// TODO add tests with two operations done at the same time
	
	public CopyMoveElementsTests(String name) {
		super(name);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
	}

	@Override
	public void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	/**
	 * Ensures that an internal element can be copied to a different file.
	 */
	public void testCopyInt() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		
		copyPositive(neSource, neDest, null, null, false);
	}
	
	/**
	 * Ensures that an internal element can be copied to a different
	 * file replacing an existing element.
	 */
	public void testCopyIntForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		createNEPositive(neDest, "foo", null);
		
		copyPositive(neSource, neDest, null, null, true);
	}

	/**
	 * Ensures that an internal element snapshot can be copied to a
	 * different file.
	 */
	public void testCopyIntFromSnapshotToOtherFile() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		rfSource.save(null, false);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		copyPositive(neSource.getSnapshot(), neDest, null, null, false);
	}
	
	/**
	 * Ensures that an internal element snapshot can be copied to the
	 * mutable copy of its file with a different name.
	 */
	public void testCopyIntFromSnapshotToSameFile() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		rfSource.save(null, false);
		
		copyPositive(neSource.getSnapshot(), neSource, null, "bar", false);
	}
	
	/**
	 * Ensures that copying an internal element to itself is a no-op.
	 */
	public void testCopyIntNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		createNEPositive(neSource, "bar", null);

		copyNoop(neSource, null);
	}
	
	/**
	 * Ensures that an internal element can be copied to a different file,
	 * and renamed.
	 */
	public void testCopyIntRename() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		createNEPositive(neDest, "foo", null);

		copyPositive(neSource, neDest, null, "bar", false);
	}
	
	/**
	 * Ensures that an internal element can be copied to a different file,
	 * and renamed, overwriting an existing element.
	 */
	public void testCopyIntRenameForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		createNEPositive(neDest, "bar", null);

		copyPositive(neSource, neDest, null, "bar", true);
	}

	/**
	 * Ensures that an internal element can be duplicated in the same file.
	 */
	public void testCopyIntSameParent() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		copyPositive(neSource, neParent, null, "bar", false);
	}
	
    /**
     * Ensures that an internal element can be copied to a different file, and
     * that all its children are copied.
     */
    public void testCopyIntTree() throws CoreException {
            IRodinFile rfSource = createRodinFile("P/X.test");
            NamedElement neParent = createNEPositive(rfSource, "parent", null);
            NamedElement neSource = createNEPositive(neParent, "foo", null);
            createNEPositive(neSource, "bar", null);

            IRodinFile rfDest = createRodinFile("P/Y.test");
            NamedElement neDest = createNEPositive(rfDest, "target", null);

            copyPositive(neSource, neDest, null, null, false);
            NamedElement neCopy = getNamedElement(neDest, "foo");
            assertEquals("Child not copied with parent",
                            neSource.getChildren().length,
                            neCopy.getChildren().length);
    }

	/**
	 * Ensures that an internal element cannot be copied to a different
	 * file replacing an existing element if no force.
	 */
	public void testCopyIntWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		createNEPositive(neDest, "foo", null);

		copyNegative(neSource, neDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
	}
	
	/**
	 * Ensures that an internal element can be copied to a different
	 * file with positioning.
	 */
	public void testCopyIntWithPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		NamedElement neDestNext = createNEPositive(neDest, "bar", null);
		
		copyPositive(neSource, neDest, neDestNext, null, false);
	}

	/**
	 * Ensures that a top-level internal element can be copied to a different file.
	 */
	public void testCopyTop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		createNEPositive(neSource, "bar", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		
		copyPositive(neSource, rfDest, null, null, false);
		NamedElement neDest = getNamedElement(rfDest, "foo");
		NamedElement childDest = getNamedElement(neDest, "bar");
		assertExists("Children not copied with parent", childDest);
	}

	/**
	 * Ensures that a top-level internal element can be copied to a different
	 * file replacing an existing element.
	 */
	public void testCopyTopForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		createNEPositive(rfDest, "foo", null);
		
		copyPositive(neSource, rfDest, null, null, true);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file across projects 
	 * replacing an existing element.
	 */
	public void testCopyTopForceInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNEPositive(rfDest, "foo", null);

			copyPositive(neSource, rfDest, null, null, true);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element snapshot can be copied to a
	 * different file.
	 */
	public void testCopyTopFromSnapshotToOtherFile() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		rfSource.save(null, false);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		copyPositive(neSource.getSnapshot(), rfDest, null, null, false);
	}
	
	/**
	 * Ensures that a top-level internal element snapshot can be copied to the
	 * mutable copy of its file with a different name.
	 */
	public void testCopyTopFromSnapshotToSameFile() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		rfSource.save(null, false);
		
		copyPositive(neSource.getSnapshot(), rfSource, null, "bar", false);
	}
	
	/**
	 * Ensures that one cannot copy to a snapshot.
	 */
	public void testCopyTopToSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		rfSource.save(null, false);
		
		copyNegative(neSource, rfSource.getSnapshot(), null, "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
		copyNegative(neSource, neSource.getSnapshot(), null, "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file in a different project.
	 */
	public void testCopyTopInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");

			copyPositive(neSource, rfDest, null, null, false);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that copying a top-level internal element to itself is a no-op.
	 */
	public void testCopyTopNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		createNEPositive(neSource, "bar", null);
		
		copyNoop(neSource, null);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file,
	 * and renamed.
	 */
	public void testCopyTopRename() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");

		copyPositive(neSource, rfDest, null, "bar", false);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file,
	 * and renamed, overwriting an existing element.
	 */
	public void testCopyTopRenameForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		createNEPositive(rfDest, "bar", null);

		copyPositive(neSource, rfDest, null, "bar", true);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file across two different 
	 * projects, and renamed, overwriting an existing element.
	 */
	public void testCopyTopRenameForceInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);
			
			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNEPositive(rfDest, "bar", null);

			copyPositive(neSource, rfDest, null, "bar", true);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file across two different
	 * projects, and renamed.
	 */
	public void testCopyTopRenameInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);
			
			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");

			copyPositive(neSource, rfDest, null, "bar", false);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element can be duplicated in the same file.
	 */
	public void testCopyTopSameParent() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		
		copyPositive(neSource, rfSource, null, "bar", false);
	}

	/**
	 * Ensures that a multi status exception is generated when copying top-level internal elements.
	 */
	public void testCopyTopsMultiStatus() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		createNEPositive(rfSource, "foo", null);
		createNEPositive(rfSource, "bar", null);
		createNEPositive(rfSource, "baz", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		
		IRodinElement[] nesSource = rfSource.getChildren();
		IRodinElement[] dests = new IRodinElement[nesSource.length];
		for (int i = 0; i < dests.length; i++) {
			dests[i] = rfDest;
		}
		IRodinProject p = getRodinProject("P");
		dests[1] = p; //invalid destination
		dests[2]=  p;
		
		try {
			startDeltas();
			boolean e= false;
			try {
				rfDest.getRodinDB().copy(nesSource, dests, null, null, false, null);
			} catch (RodinDBException jme) {
				assertTrue("Should be multistatus", jme.getStatus().isMultiStatus());
				assertTrue(
						"Should be an invalid destination",
						((IRodinDBStatus) jme.getStatus().getChildren()[0])
								.getCode() == IRodinDBStatusConstants.INVALID_DESTINATION);
				e = true;
			}
			assertTrue("Should have been an exception", e);
			
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	Y.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][+]: {}"
			);
			
			IRodinElement copy= generateHandle(nesSource[0], null, rfDest);
			assertExists("Copy should exist", copy);
		} finally {
			stopDeltas();
		}
	}
	
	/**
	 * Ensures that a multi status exception is generated when copying top-level internal elements.
	 */
	public void testCopyTopsMultiStatusInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			createNEPositive(rfSource, "foo", null);
			createNEPositive(rfSource, "bar", null);
			createNEPositive(rfSource, "baz", null);

			IRodinProject p2 = createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			
			IRodinElement[] nesSource = rfSource.getChildren();
			IRodinElement[] dests = new IRodinElement[nesSource.length];
			for (int i = 0; i < dests.length; i++) {
				dests[i] = rfDest;
			}
			dests[1] = p2; //invalid destination
			dests[2] = p2;
			
			startDeltas();
			boolean e= false;
			try {
				rfDest.getRodinDB().copy(nesSource, dests, null, null, false, null);
			} catch (RodinDBException jme) {
				assertTrue("Should be multistatus", jme.getStatus().isMultiStatus());
				assertTrue("Should be an invalid destination", ((IRodinDBStatus)jme.getStatus().getChildren()[0]).getCode()== IRodinDBStatusConstants.INVALID_DESTINATION);
				e = true;
			}
			assertTrue("Should have been an exception", e);
			
			assertDeltas(
					"Unexpected delta",
					"P2[*]: {CHILDREN}\n" + 
					"	Y.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][+]: {}"
			);
			
			IRodinElement copy= generateHandle(nesSource[0], null, rfDest);
			assertExists("Copy should exist", copy);
		} finally {
			stopDeltas();
			deleteProject("P2");
		}
	}

	/**
	 * Ensures that a top-level internal element cannot be copied to a different
	 * file replacing an existing element if no force.
	 */
	public void testCopyTopWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		createNEPositive(rfDest, "foo", null);

		copyNegative(neSource, rfDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
	}

	/**
	 * Ensures that a top-level internal element cannot be copied to a different
	 * file across different projects replacing an existing element if no force.
	 */
	public void testCopyTopWithCollisionInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNEPositive(rfDest, "foo", null);

			copyNegative(neSource, rfDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element cannot be copied to an invalid destination.
	 */
	public void testCopyTopWithInvalidDestination() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		IRodinProject p = getRodinProject("P");
		
		copyNegative(neSource, p, null, null, false, IRodinDBStatusConstants.INVALID_DESTINATION);
	}

	/**
	 * Ensures that a top-level internal element cannot be copied to a different
	 * file with an invalid sibling used for positioning.
	 */
	public void testCopyTopWithInvalidPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		
		copyNegative(
				neSource, 
				rfDest, 
				rfDest.getInternalElement(NamedElement.ELEMENT_TYPE, "invalid"), 
				null, 
				false, 
				IRodinDBStatusConstants.INVALID_SIBLING);
	}
	
	/**
	 * Ensures that a top-level internal element cannot be copied to a different
	 * file with an invalid sibling used for positioning.
	 */
	public void testCopyTopWithInvalidPositioningInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNEPositive(rfDest, "foo", null);

			copyNegative(
					neSource, 
					rfDest, 
					rfDest.getInternalElement(NamedElement.ELEMENT_TYPE, "invalid"), 
					null, 
					false, 
					IRodinDBStatusConstants.INVALID_SIBLING);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that attempting to rename with an incorrect number of renamings fails
	 */
	public void testCopyTopWithInvalidRenamings() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		copyNegative(
				new IRodinElement[] {neSource}, 
				new IRodinElement[] {rfSource}, 
				null, 
				new String[] {"bar", "baz"}, 
				false, 
				IRodinDBStatusConstants.INDEX_OUT_OF_BOUNDS);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different
	 * file with positioning.
	 */
	public void testCopyTopWithPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "bar", null);

		copyPositive(neSource, rfDest, neDest, null, false);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different
	 * file across different projects with positioning.
	 */
	public void testCopyTopWithPositioningInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			NamedElement neDest = createNEPositive(rfDest, "bar", null);

			copyPositive(neSource, rfDest, neDest, null, false);
		} finally {
			deleteProject("P2");
		}
	}

	/**
	 * Ensures that an internal element can be moved to a different file.
	 */
	public void testMoveInt() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		
		movePositive(neSource, neDest, null, null, false);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different
	 * file replacing an existing element.
	 */
	public void testMoveIntForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		createNEPositive(neDest, "foo", null);
		
		movePositive(neSource, neDest, null, null, true);
	}

	/**
	 * Ensures that moving an internal element to itself is a no-op.
	 */
	public void testMoveIntNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		createNEPositive(neSource, "bar", null);

		moveNoop(neSource, null);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different file,
	 * and renamed.
	 */
	public void testMoveIntRename() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		createNEPositive(neDest, "foo", null);

		movePositive(neSource, neDest, null, "bar", false);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different file,
	 * and renamed, overwriting an existing element.
	 */
	public void testMoveIntRenameForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		createNEPositive(neDest, "bar", null);

		movePositive(neSource, neDest, null, "bar", true);
	}

	/**
	 * Ensures that an internal element can be moved in the same file.
	 */
	public void testMoveIntSameParent() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		movePositive(neSource, neParent, null, "bar", false);
	}
	
	/**
	 * Ensures that an internal element snapshot cannot be moved.
	 */
	public void testMoveIntFromSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		rfSource.save(null, false);
		
		moveNegative(neSource.getSnapshot(), neParent, null, "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that an internal element cannot be moved to a snapshot.
	 */
	public void testMoveIntToSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		rfSource.save(null, false);
		
		moveNegative(neSource, neParent.getSnapshot(), null, "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that an internal element cannot be moved to a different
	 * file replacing an existing element if no force.
	 */
	public void testMoveIntWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		createNEPositive(neDest, "foo", null);

		moveNegative(neSource, neDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different
	 * file with positioning.
	 */
	public void testMoveIntWithPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "target", null);
		NamedElement neDestNext = createNEPositive(neDest, "bar", null);
		
		movePositive(neSource, neDest, neDestNext, null, false);
	}

	/**
	 * Ensures that a top-level internal element can be moved to a different file.
	 */
	public void testMoveTop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		createNEPositive(neSource, "bar", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		
		movePositive(neSource, rfDest, null, null, false);
		NamedElement neDest = getNamedElement(rfDest, "foo");
		NamedElement childDest = getNamedElement(neDest, "bar");
		assertExists("Children not moved with parent", childDest);
	}

	/**
	 * Ensures that a top-level internal element can be moved to a different
	 * file replacing an existing element.
	 */
	public void testMoveTopForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		createNEPositive(rfDest, "foo", null);
		
		movePositive(neSource, rfDest, null, null, true);
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different file across projects 
	 * replacing an existing element.
	 */
	public void testMoveTopForceInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNEPositive(rfDest, "foo", null);

			movePositive(neSource, rfDest, null, null, true);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element snapshot cannot be moved.
	 */
	public void testMoveTopFromSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		rfSource.save(null, false);
		
		moveNegative(neSource.getSnapshot(), rfSource, null, "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that a top-level internal element cannot be moved to a snapshot.
	 */
	public void testMoveTopToSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		rfSource.save(null, false);
		
		moveNegative(neSource, rfSource.getSnapshot(), null, "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different file in a different project.
	 */
	public void testMoveTopInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");

			movePositive(neSource, rfDest, null, null, false);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that moving a top-level internal element to itself is a no-op.
	 */
	public void testMoveTopNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		createNEPositive(neSource, "bar", null);
		
		moveNoop(neSource, null);
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different file,
	 * and renamed.
	 */
	public void testMoveTopRename() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");

		movePositive(neSource, rfDest, null, "bar", false);
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different file,
	 * and renamed, overwriting an existing element.
	 */
	public void testMoveTopRenameForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		createNEPositive(rfDest, "bar", null);

		movePositive(neSource, rfDest, null, "bar", true);
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different file across two different 
	 * projects, and renamed, overwriting an existing element.
	 */
	public void testMoveTopRenameForceInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);
			
			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNEPositive(rfDest, "bar", null);

			movePositive(neSource, rfDest, null, "bar", true);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different file across two different
	 * projects, and renamed.
	 */
	public void testMoveTopRenameInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);
			
			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");

			movePositive(neSource, rfDest, null, "bar", false);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element can be moved in the same file.
	 */
	public void testMoveTopSameParent() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		
		movePositive(neSource, rfSource, null, "bar", false);
	}

	/**
	 * Ensures that a multi status exception is generated when moveing top-level internal elements.
	 */
	public void testMoveTopsMultiStatus() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		createNEPositive(rfSource, "foo", null);
		createNEPositive(rfSource, "bar", null);
		createNEPositive(rfSource, "baz", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		
		IRodinElement[] nesSource = rfSource.getChildren();
		IRodinElement[] dests = new IRodinElement[nesSource.length];
		for (int i = 0; i < dests.length; i++) {
			dests[i] = rfDest;
		}
		IRodinProject p = getRodinProject("P");
		dests[1] = p; //invalid destination
		dests[2]=  p;
		
		try {
			startDeltas();
			boolean e= false;
			try {
				rfDest.getRodinDB().move(nesSource, dests, null, null, false, null);
			} catch (RodinDBException jme) {
				assertTrue("Should be multistatus", jme.getStatus().isMultiStatus());
				assertTrue(
						"Should be an invalid destination",
						((IRodinDBStatus) jme.getStatus().getChildren()[0])
								.getCode() == IRodinDBStatusConstants.INVALID_DESTINATION);
				e = true;
			}
			assertTrue("Should have been an exception", e);
			
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][-]: {MOVED_TO(foo[org.rodinp.core.tests.namedElement] [in Y.test [in P]])}\n" +
					"	Y.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][+]: {MOVED_FROM(foo[org.rodinp.core.tests.namedElement] [in X.test [in P]])}"
			);
			
			IRodinElement move= generateHandle(nesSource[0], null, rfDest);
			assertExists("Move should exist", move);
		} finally {
			stopDeltas();
		}
	}
	
	/**
	 * Ensures that a multi status exception is generated when moveing top-level internal elements.
	 */
	public void testMoveTopsMultiStatusInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			createNEPositive(rfSource, "foo", null);
			createNEPositive(rfSource, "bar", null);
			createNEPositive(rfSource, "baz", null);

			IRodinProject p2 = createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			
			IRodinElement[] nesSource = rfSource.getChildren();
			IRodinElement[] dests = new IRodinElement[nesSource.length];
			for (int i = 0; i < dests.length; i++) {
				dests[i] = rfDest;
			}
			dests[1] = p2; //invalid destination
			dests[2] = p2;
			
			startDeltas();
			boolean e= false;
			try {
				rfDest.getRodinDB().move(nesSource, dests, null, null, false, null);
			} catch (RodinDBException jme) {
				assertTrue("Should be multistatus", jme.getStatus().isMultiStatus());
				assertTrue("Should be an invalid destination", ((IRodinDBStatus)jme.getStatus().getChildren()[0]).getCode()== IRodinDBStatusConstants.INVALID_DESTINATION);
				e = true;
			}
			assertTrue("Should have been an exception", e);
			
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][-]: {MOVED_TO(foo[org.rodinp.core.tests.namedElement] [in Y.test [in P2]])}\n" +
					"P2[*]: {CHILDREN}\n" + 
					"	Y.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][+]: {MOVED_FROM(foo[org.rodinp.core.tests.namedElement] [in X.test [in P]])}"
			);
			
			IRodinElement move= generateHandle(nesSource[0], null, rfDest);
			assertExists("Move should exist", move);
		} finally {
			stopDeltas();
			deleteProject("P2");
		}
	}

	/**
	 * Ensures that a top-level internal element cannot be moved to a different
	 * file replacing an existing element if no force.
	 */
	public void testMoveTopWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		createNEPositive(rfDest, "foo", null);

		moveNegative(neSource, rfDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
	}

	/**
	 * Ensures that a top-level internal element cannot be moved to a different
	 * file across different projects replacing an existing element if no force.
	 */
	public void testMoveTopWithCollisionInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNEPositive(rfDest, "foo", null);

			moveNegative(neSource, rfDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element cannot be moved to an invalid destination.
	 */
	public void testMoveTopWithInvalidDestination() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		IRodinProject p = getRodinProject("P");
		
		moveNegative(neSource, p, null, null, false, IRodinDBStatusConstants.INVALID_DESTINATION);
	}

	/**
	 * Ensures that a top-level internal element cannot be moved to a different
	 * file with an invalid sibling used for positioning.
	 */
	public void testMoveTopWithInvalidPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		
		moveNegative(
				neSource, 
				rfDest, 
				rfDest.getInternalElement(NamedElement.ELEMENT_TYPE, "invalid"), 
				null, 
				false, 
				IRodinDBStatusConstants.INVALID_SIBLING);
	}
	
	/**
	 * Ensures that a top-level internal element cannot be moved to a different
	 * file with an invalid sibling used for positioning.
	 */
	public void testMoveTopWithInvalidPositioningInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNEPositive(rfDest, "foo", null);

			moveNegative(
					neSource, 
					rfDest, 
					rfDest.getInternalElement(NamedElement.ELEMENT_TYPE, "invalid"), 
					null, 
					false, 
					IRodinDBStatusConstants.INVALID_SIBLING);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that attempting to rename with an incorrect number of renamings fails
	 */
	public void testMoveTopWithInvalidRenamings() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		moveNegative(
				new IRodinElement[] {neSource}, 
				new IRodinElement[] {rfSource}, 
				null, 
				new String[] {"bar", "baz"}, 
				false, 
				IRodinDBStatusConstants.INDEX_OUT_OF_BOUNDS);
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different
	 * file with positioning.
	 */
	public void testMoveTopWithPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNEPositive(rfDest, "bar", null);

		movePositive(neSource, rfDest, neDest, null, false);
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different
	 * file across different projects with positioning.
	 */
	public void testMoveTopWithPositioningInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNEPositive(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			NamedElement neDest = createNEPositive(rfDest, "bar", null);

			movePositive(neSource, rfDest, neDest, null, false);
		} finally {
			deleteProject("P2");
		}
	}

	/**
	 * Ensures that an internal element can be renamed.
	 */
	public void testRenameInt() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		renamePositive(neSource, "bar", false);
	}

	/**
	 * Ensures that an internal element can be renamed, replacing an existing element.
	 */
	public void testRenameIntForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		NamedElement neDest = createNEPositive(neParent, "bar", null);
		
		renamePositive(neSource, neDest.getElementName(), true);
	}
	
	/**
	 * Ensures that renaming an internal element to itself is a no-op.
	 */
	public void testRenameIntNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		createNEPositive(neSource, "bar", null);

		renameNoop(neSource);
	}
	
	/**
	 * Ensures that an internal element snapshot cannot be renamed.
	 */
	public void testRenameIntSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		rfSource.save(null, false);
		
		renameNegative(neSource.getSnapshot(), "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that an internal element cannot be renamed,
	 * replacing an existing element if no force.
	 */
	public void testRenameIntWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		NamedElement neDest = createNEPositive(neParent, "bar", null);

		renameNegative(neSource, neDest.getElementName(), false, IRodinDBStatusConstants.NAME_COLLISION);
	}

	/**
	 * Ensures that a top-level internal element can be renamed.
	 */
	public void testRenameTop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		createNEPositive(neSource, "bar", null);

		renamePositive(neSource, "baz", false);
		NamedElement neDest = getNamedElement(rfSource, "baz");
		NamedElement childDest = getNamedElement(neDest, "bar");
		assertExists("Children not renamed with parent", childDest);
	}
	
	/**
	 * Ensures that a top-level internal element snapshot cannot be renamed.
	 */
	public void testRenameTopSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		rfSource.save(null, false);
		
		renameNegative(neSource.getSnapshot(), "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that a top-level internal element can be renamed twice.
	 */
	public void testRenameTopTwice() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		createNEPositive(neSource, "bar", null);

		renamePositive(neSource, "baz", false);
		NamedElement neDest = getNamedElement(rfSource, "baz");
		NamedElement childDest = getNamedElement(neDest, "bar");
		assertExists("Children not renamed with parent", childDest);

		renamePositive(neDest, "foo", false);
		NamedElement neDest2 = getNamedElement(rfSource, "foo");
		NamedElement childDest2 = getNamedElement(neDest2, "bar");
		assertExists("Children not renamed with parent", childDest2);
	}
	
	/**
	 * Ensures that a top-level internal element can be renamed, replacing an
	 * existing element.
	 */
	public void testRenameTopForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		NamedElement neDest = createNEPositive(rfSource, "bar", null);

		renamePositive(neSource, neDest.getElementName(), true);
	}
	
	/**
	 * Ensures that copying a top-level internal element to itself is a no-op.
	 */
	public void testRenameTopNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		createNEPositive(neSource, "bar", null);
		
		renameNoop(neSource);
	}
	
	/**
	 * Ensures that a multi status exception is generated when renaming top-level internal elements.
	 */
	public void testRenameTopsMultiStatus() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement foo = createNEPositive(rfSource, "foo", null);
		NamedElement bar = createNEPositive(rfSource, "bar", null);
		createNEPositive(rfSource, "baz", null);

		IRodinElement[] nesSource = new IRodinElement[] { foo, bar, rfSource };
		String[] newNames = new String[] { "fun", "baz", "baz" };
		
		try {
			startDeltas();
			boolean e= false;
			try {
				rfSource.getRodinDB().rename(nesSource, newNames, false, null);
			} catch (RodinDBException jme) {
				assertTrue("Should be multistatus", jme.getStatus().isMultiStatus());
				assertTrue(
						"Should be a name collision",
						((IRodinDBStatus) jme.getStatus().getChildren()[0])
								.getCode() == IRodinDBStatusConstants.NAME_COLLISION);
				e = true;
			}
			assertTrue("Should have been an exception", e);
			
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][-]: {MOVED_TO(fun[org.rodinp.core.tests.namedElement] [in X.test [in P]])}\n" +
					"		fun[org.rodinp.core.tests.namedElement][+]: {MOVED_FROM(foo[org.rodinp.core.tests.namedElement] [in X.test [in P]])}"
			);
			
			IRodinElement rename= generateHandle(nesSource[0], "fun", rfSource);
			assertExists("Rename should exist", rename);
		} finally {
			stopDeltas();
		}
	}
	
	/**
	 * Ensures that a top-level internal element cannot be renamed,
	 * replacing an existing element if no force.
	 */
	public void testRenameTopWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		NamedElement neDest = createNEPositive(rfSource, "bar", null);

		renameNegative(neSource, neDest.getElementName(), false, IRodinDBStatusConstants.NAME_COLLISION);
	}

	/**
	 * Ensures that attempting to rename with an incorrect number of renamings fails
	 */
	public void testRenameTopWithInvalidNames() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);

		renameNegative(
				new IRodinElement[] {neSource}, 
				new String[] {"bar", "baz"}, 
				false, 
				IRodinDBStatusConstants.INDEX_OUT_OF_BOUNDS);
	}

	/**
	 * Ensures that renaming a top element with children does indeed move the
	 * children.
	 */
	public void testRenameTopWithChildren() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNEPositive(rfSource, "foo", null);
		NamedElement ne1 = createNEPositive(neSource, "1", null);
		ne1.setAttributeValue(fString, "child1", null);
		NamedElement ne2 = createNEPositive(neSource, "2", null);
		ne2.setAttributeValue(fString, "child2", null);

		renamePositive(neSource, "baz", false);
		NamedElement neDest = getNamedElement(rfSource, "baz");
		assertElementDescendants("Unexpected children",
				"baz[org.rodinp.core.tests.namedElement]\n" +
				"  1[org.rodinp.core.tests.namedElement]\n" +
				"  2[org.rodinp.core.tests.namedElement]",
				neDest
		);
		IRodinElement[] children = 
			neDest.getChildrenOfType(NamedElement.ELEMENT_TYPE);
		assertEquals("Destination should have two children", 2, children.length);
		assertContents("Wrong contents for first child", "child1", 
				(IInternalElement) children[0]);
		assertContents("Wrong contents for first child", "child2", 
				(IInternalElement) children[1]);
	}

	/**
	 * Ensures that an internal element snapshot cannot be reordered.
	 */
	public void testReorderIntSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNEPositive(rfSource, "parent", null);
		NamedElement ne1 = createNEPositive(neParent, "foo", null);
		NamedElement ne2 = createNEPositive(neParent, "bar", null);
		rfSource.save(null, false);
		
		reorderNegative(ne2.getSnapshot(), ne1.getSnapshot(),
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that an internal element can be reordered.
	 */
	public void testReorderTop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement foo = createNEPositive(rfSource, "foo", null);
		NamedElement bar = createNEPositive(rfSource, "bar", null);
		NamedElement baz = createNEPositive(rfSource, "baz", null);
		assertElementDescendants("Unexpected children",
				"X.test\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]",
				rfSource
		);

		// move first in first pos.
		reorderPositive(foo, bar, false);
		assertElementDescendants("Unexpected children",
				"X.test\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]",
				rfSource
		);
		
		// move first in second pos.
		reorderPositive(foo, baz, true);
		assertElementDescendants("Unexpected children",
				"X.test\n" + 
				"  bar[org.rodinp.core.tests.namedElement]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]",
				rfSource
		);
		
		// move first in last pos.
		reorderPositive(bar, null, true); 
		assertElementDescendants("Unexpected children",
				"X.test\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]",
				rfSource
		);
		
		// move second in first pos.
		reorderPositive(baz, foo, true);
		assertElementDescendants("Unexpected children",
				"X.test\n" + 
				"  baz[org.rodinp.core.tests.namedElement]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]",
				rfSource
		);
		
		// move second in second pos.
		reorderPositive(foo, bar, false);  
		assertElementDescendants("Unexpected children",
				"X.test\n" + 
				"  baz[org.rodinp.core.tests.namedElement]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]",
				rfSource
		);
		
		// move second in last pos.
		reorderPositive(foo, null, true);  
		assertElementDescendants("Unexpected children",
				"X.test\n" + 
				"  baz[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]",
				rfSource
		);
		
		// move last in first pos.
		reorderPositive(foo, baz, true);
		assertElementDescendants("Unexpected children",
				"X.test\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]",
				rfSource
		);
		
		// move last in second pos.
		reorderPositive(bar, baz, true);
		assertElementDescendants("Unexpected children",
				"X.test\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]",
				rfSource
		);
		
		// move last in last pos.
		reorderPositive(baz, null, false);
		assertElementDescendants("Unexpected children",
				"X.test\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]",
				rfSource
		);
	}
	
	/**
	 * Ensures that a top-level internal element snapshot cannot be renamed.
	 */
	public void testReorderTopSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement ne1 = createNEPositive(rfSource, "foo", null);
		NamedElement ne2 = createNEPositive(rfSource, "bar", null);
		rfSource.save(null, false);
		
		reorderNegative(ne2.getSnapshot(), ne1.getSnapshot(),
				IRodinDBStatusConstants.READ_ONLY);
	}
	
}
