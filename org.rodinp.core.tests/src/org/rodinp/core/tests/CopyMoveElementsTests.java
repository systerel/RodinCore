/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.core.tests.model.CopyMoveElementsTests.java which is
 * 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

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
		this.deleteProject("P");
		
		super.tearDown();
	}

	/**
	 * Ensures that an internal element can be copied to a different file.
	 */
	public void testCopyInt() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "target", null);
		
		copyPositive(neSource, neDest, null, null, false);
	}
	
	/**
	 * Ensures that an internal element can be copied to a different
	 * file replacing an existing element.
	 */
	public void testCopyIntForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "target", null);
		createNamedElement(neDest, "foo", null);
		
		copyPositive(neSource, neDest, null, null, true);
	}

	/**
	 * Ensures that copying an internal element to itself is a no-op.
	 */
	public void testCopyIntNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		createNamedElement(neSource, "bar", null);

		copyNoop(neSource, null);
	}
	
	/**
	 * Ensures that an internal element can be copied to a different file,
	 * and renamed.
	 */
	public void testCopyIntRename() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "target", null);
		createNamedElement(neDest, "foo", null);

		copyPositive(neSource, neDest, null, "bar", false);
	}
	
	/**
	 * Ensures that an internal element can be copied to a different file,
	 * and renamed, overwriting an existing element.
	 */
	public void testCopyIntRenameForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "target", null);
		createNamedElement(neDest, "bar", null);

		copyPositive(neSource, neDest, null, "bar", true);
	}

	/**
	 * Ensures that an internal element can be duplicated in the same file.
	 */
	public void testCopyIntSameParent() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		
		copyPositive(neSource, neParent, null, "bar", false);
	}
	
    /**
     * Ensures that an internal element can be copied to a different file, and
     * that all its children are copied.
     */
    public void testCopyIntTree() throws CoreException {
            IRodinFile rfSource = createRodinFile("P/X.test");
            NamedElement neParent = createNamedElement(rfSource, "parent", null);
            NamedElement neSource = createNamedElement(neParent, "foo", null);
            createUnnamedElement(neSource, null);

            IRodinFile rfDest = createRodinFile("P/Y.test");
            NamedElement neDest = createNamedElement(rfDest, "target", null);

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
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "target", null);
		createNamedElement(neDest, "foo", null);

		copyNegative(neSource, neDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
	}
	
	/**
	 * Ensures that an internal element can be copied to a different
	 * file with positioning.
	 */
	public void testCopyIntWithPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "target", null);
		NamedElement neDestNext = createNamedElement(neDest, "bar", null);
		
		copyPositive(neSource, neDest, neDestNext, null, false);
	}

	/**
	 * Ensures that a top-level internal element can be copied to a different file.
	 */
	public void testCopyTop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		createNamedElement(neSource, "bar", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		
		copyPositive(neSource, rfDest, null, null, false);
		NamedElement neDest = getNamedElement(rfDest, "foo");
		NamedElement childDest = getNamedElement(neDest, "bar");
		assertTrue("Children not copied with parent", childDest.exists());
	}

	/**
	 * Ensures that a top-level internal element can be copied to a different
	 * file replacing an existing element.
	 */
	public void testCopyTopForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		createNamedElement(rfDest, "foo", null);
		
		copyPositive(neSource, rfDest, null, null, true);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file across projects 
	 * replacing an existing element.
	 */
	public void testCopyTopForceInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNamedElement(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNamedElement(rfDest, "foo", null);

			copyPositive(neSource, rfDest, null, null, true);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file in a different project.
	 */
	public void testCopyTopInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNamedElement(rfSource, "foo", null);

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
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		createNamedElement(neSource, "bar", null);
		
		copyNoop(neSource, null);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file,
	 * and renamed.
	 */
	public void testCopyTopRename() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");

		copyPositive(neSource, rfDest, null, "bar", false);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file,
	 * and renamed, overwriting an existing element.
	 */
	public void testCopyTopRenameForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		createNamedElement(rfDest, "bar", null);

		copyPositive(neSource, rfDest, null, "bar", true);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file across two different 
	 * projects, and renamed, overwriting an existing element.
	 */
	public void testCopyTopRenameForceInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNamedElement(rfSource, "foo", null);
			
			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNamedElement(rfDest, "bar", null);

			copyPositive(neSource, rfDest, null, "bar", true);
		} finally {
			this.deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file across two different
	 * projects, and renamed.
	 */
	public void testCopyTopRenameInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNamedElement(rfSource, "foo", null);
			
			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");

			copyPositive(neSource, rfDest, null, "bar", false);
		} finally {
			this.deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element can be duplicated in the same file.
	 */
	public void testCopyTopSameParent() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		
		copyPositive(neSource, rfSource, null, "bar", false);
	}

	/**
	 * Ensures that a multi status exception is generated when copying top-level internal elements.
	 */
	public void testCopyTopsMultiStatus() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		createNamedElement(rfSource, "foo", null);
		createNamedElement(rfSource, "bar", null);
		createNamedElement(rfSource, "baz", null);

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
			assertTrue("Copy should exist", copy.exists());
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
			createNamedElement(rfSource, "foo", null);
			createNamedElement(rfSource, "bar", null);
			createNamedElement(rfSource, "baz", null);

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
			assertTrue("Copy should exist", copy.exists());
		} finally {
			stopDeltas();
			this.deleteProject("P2");
		}
	}

	/**
	 * Ensures that a top-level internal element cannot be copied to a different
	 * file replacing an existing element if no force.
	 */
	public void testCopyTopWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		createNamedElement(rfDest, "foo", null);

		copyNegative(neSource, rfDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
	}

	/**
	 * Ensures that a top-level internal element cannot be copied to a different
	 * file across different projects replacing an existing element if no force.
	 */
	public void testCopyTopWithCollisionInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNamedElement(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNamedElement(rfDest, "foo", null);

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
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

		IRodinProject p = getRodinProject("P");
		
		copyNegative(neSource, p, null, null, false, IRodinDBStatusConstants.INVALID_DESTINATION);
	}

	/**
	 * Ensures that a top-level internal element cannot be copied to a different
	 * file with an invalid sibling used for positioning.
	 */
	public void testCopyTopWithInvalidPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		
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
			NamedElement neSource = createNamedElement(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNamedElement(rfDest, "foo", null);

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
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

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
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "bar", null);

		copyPositive(neSource, rfDest, neDest, null, false);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different
	 * file across different projects with positioning.
	 */
	public void testCopyTopWithPositioningInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNamedElement(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			NamedElement neDest = createNamedElement(rfDest, "bar", null);

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
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "target", null);
		
		movePositive(neSource, neDest, null, null, false);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different
	 * file replacing an existing element.
	 */
	public void testMoveIntForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "target", null);
		createNamedElement(neDest, "foo", null);
		
		movePositive(neSource, neDest, null, null, true);
	}

	/**
	 * Ensures that moving an internal element to itself is a no-op.
	 */
	public void testMoveIntNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		createNamedElement(neSource, "bar", null);

		moveNoop(neSource, null);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different file,
	 * and renamed.
	 */
	public void testMoveIntRename() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "target", null);
		createNamedElement(neDest, "foo", null);

		movePositive(neSource, neDest, null, "bar", false);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different file,
	 * and renamed, overwriting an existing element.
	 */
	public void testMoveIntRenameForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "target", null);
		createNamedElement(neDest, "bar", null);

		movePositive(neSource, neDest, null, "bar", true);
	}

	/**
	 * Ensures that an internal element can be moved in the same file.
	 */
	public void testMoveIntSameParent() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		
		movePositive(neSource, neParent, null, "bar", false);
	}
	
	/**
	 * Ensures that an internal element cannot be moved to a different
	 * file replacing an existing element if no force.
	 */
	public void testMoveIntWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "target", null);
		createNamedElement(neDest, "foo", null);

		moveNegative(neSource, neDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different
	 * file with positioning.
	 */
	public void testMoveIntWithPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "target", null);
		NamedElement neDestNext = createNamedElement(neDest, "bar", null);
		
		movePositive(neSource, neDest, neDestNext, null, false);
	}

	/**
	 * Ensures that a top-level internal element can be moved to a different file.
	 */
	public void testMoveTop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		createNamedElement(neSource, "bar", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		
		movePositive(neSource, rfDest, null, null, false);
		NamedElement neDest = getNamedElement(rfDest, "foo");
		NamedElement childDest = getNamedElement(neDest, "bar");
		assertTrue("Children not moved with parent", childDest.exists());
	}

	/**
	 * Ensures that a top-level internal element can be moved to a different
	 * file replacing an existing element.
	 */
	public void testMoveTopForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		createNamedElement(rfDest, "foo", null);
		
		movePositive(neSource, rfDest, null, null, true);
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different file across projects 
	 * replacing an existing element.
	 */
	public void testMoveTopForceInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNamedElement(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNamedElement(rfDest, "foo", null);

			movePositive(neSource, rfDest, null, null, true);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different file in a different project.
	 */
	public void testMoveTopInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNamedElement(rfSource, "foo", null);

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
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		createNamedElement(neSource, "bar", null);
		
		moveNoop(neSource, null);
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different file,
	 * and renamed.
	 */
	public void testMoveTopRename() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");

		movePositive(neSource, rfDest, null, "bar", false);
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different file,
	 * and renamed, overwriting an existing element.
	 */
	public void testMoveTopRenameForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		createNamedElement(rfDest, "bar", null);

		movePositive(neSource, rfDest, null, "bar", true);
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different file across two different 
	 * projects, and renamed, overwriting an existing element.
	 */
	public void testMoveTopRenameForceInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNamedElement(rfSource, "foo", null);
			
			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNamedElement(rfDest, "bar", null);

			movePositive(neSource, rfDest, null, "bar", true);
		} finally {
			this.deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different file across two different
	 * projects, and renamed.
	 */
	public void testMoveTopRenameInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNamedElement(rfSource, "foo", null);
			
			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");

			movePositive(neSource, rfDest, null, "bar", false);
		} finally {
			this.deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a top-level internal element can be moved in the same file.
	 */
	public void testMoveTopSameParent() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		
		movePositive(neSource, rfSource, null, "bar", false);
	}

	/**
	 * Ensures that a multi status exception is generated when moveing top-level internal elements.
	 */
	public void testMoveTopsMultiStatus() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		createNamedElement(rfSource, "foo", null);
		createNamedElement(rfSource, "bar", null);
		createNamedElement(rfSource, "baz", null);

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
			assertTrue("Move should exist", move.exists());
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
			createNamedElement(rfSource, "foo", null);
			createNamedElement(rfSource, "bar", null);
			createNamedElement(rfSource, "baz", null);

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
			assertTrue("Move should exist", move.exists());
		} finally {
			stopDeltas();
			this.deleteProject("P2");
		}
	}

	/**
	 * Ensures that a top-level internal element cannot be moved to a different
	 * file replacing an existing element if no force.
	 */
	public void testMoveTopWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		createNamedElement(rfDest, "foo", null);

		moveNegative(neSource, rfDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
	}

	/**
	 * Ensures that a top-level internal element cannot be moved to a different
	 * file across different projects replacing an existing element if no force.
	 */
	public void testMoveTopWithCollisionInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNamedElement(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNamedElement(rfDest, "foo", null);

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
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

		IRodinProject p = getRodinProject("P");
		
		moveNegative(neSource, p, null, null, false, IRodinDBStatusConstants.INVALID_DESTINATION);
	}

	/**
	 * Ensures that a top-level internal element cannot be moved to a different
	 * file with an invalid sibling used for positioning.
	 */
	public void testMoveTopWithInvalidPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		
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
			NamedElement neSource = createNamedElement(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			createNamedElement(rfDest, "foo", null);

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
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

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
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		NamedElement neDest = createNamedElement(rfDest, "bar", null);

		movePositive(neSource, rfDest, neDest, null, false);
	}
	
	/**
	 * Ensures that a top-level internal element can be moved to a different
	 * file across different projects with positioning.
	 */
	public void testMoveTopWithPositioningInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			NamedElement neSource = createNamedElement(rfSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			NamedElement neDest = createNamedElement(rfDest, "bar", null);

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
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		
		renamePositive(neSource, "bar", false);
	}

	/**
	 * Ensures that an internal element can be renamed, replacing an existing element.
	 */
	public void testRenameIntForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		NamedElement neDest = createNamedElement(neParent, "bar", null);
		
		renamePositive(neSource, neDest.getElementName(), true);
	}
	
	/**
	 * Ensures that renaming an internal element to itself is a no-op.
	 */
	public void testRenameIntNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		createNamedElement(neSource, "bar", null);

		renameNoop(neSource);
	}
	
	/**
	 * Ensures that an internal element cannot be renamed,
	 * replacing an existing element if no force.
	 */
	public void testRenameIntWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neParent = createNamedElement(rfSource, "parent", null);
		NamedElement neSource = createNamedElement(neParent, "foo", null);
		NamedElement neDest = createNamedElement(neParent, "bar", null);

		renameNegative(neSource, neDest.getElementName(), false, IRodinDBStatusConstants.NAME_COLLISION);
	}

	/**
	 * Ensures that a top-level internal element can be renamed.
	 */
	public void testRenameTop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		createNamedElement(neSource, "bar", null);

		renamePositive(neSource, "baz", false);
		NamedElement neDest = getNamedElement(rfSource, "baz");
		NamedElement childDest = getNamedElement(neDest, "bar");
		assertTrue("Children not renamed with parent", childDest.exists());
	}
	
	/**
	 * Ensures that a top-level internal element can be renamed, replacing an
	 * existing element.
	 */
	public void testRenameTopForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		NamedElement neDest = createNamedElement(rfSource, "bar", null);

		renamePositive(neSource, neDest.getElementName(), true);
	}
	
	/**
	 * Ensures that copying a top-level internal element to itself is a no-op.
	 */
	public void testRenameTopNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		createNamedElement(neSource, "bar", null);
		
		renameNoop(neSource);
	}
	
	/**
	 * Ensures that a multi status exception is generated when renaming top-level internal elements.
	 */
	public void testRenameTopsMultiStatus() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement foo = createNamedElement(rfSource, "foo", null);
		NamedElement bar = createNamedElement(rfSource, "bar", null);
		createNamedElement(rfSource, "baz", null);

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
			assertTrue("Rename should exist", rename.exists());
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
		NamedElement neSource = createNamedElement(rfSource, "foo", null);
		NamedElement neDest = createNamedElement(rfSource, "bar", null);

		renameNegative(neSource, neDest.getElementName(), false, IRodinDBStatusConstants.NAME_COLLISION);
	}

	/**
	 * Ensures that attempting to rename with an incorrect number of renamings fails
	 */
	public void testRenameTopWithInvalidNames() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement neSource = createNamedElement(rfSource, "foo", null);

		renameNegative(
				new IRodinElement[] {neSource}, 
				new String[] {"bar", "baz"}, 
				false, 
				IRodinDBStatusConstants.INDEX_OUT_OF_BOUNDS);
	}

	/**
	 * Ensures that an internal element can be reordered.
	 */
	public void testReorderTop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		NamedElement foo = createNamedElement(rfSource, "foo", null);
		NamedElement bar = createNamedElement(rfSource, "bar", null);
		NamedElement baz = createNamedElement(rfSource, "baz", null);
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
	
}
