/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.tests.model.CopyMoveTests
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IElementManipulation;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

abstract public class CopyMoveTests extends ModifyingResourceTests {

	public CopyMoveTests(String name) {
		super(name);
	}
	
	/**
	 * Attempts to copy the element with optional 
	 * forcing. The operation should fail with the failure code.
	 */
	public void copyNegative(IRodinElement element, IRodinElement destination, IRodinElement sibling, String rename, boolean force, int failureCode) {
		try {
			((IElementManipulation)element).copy(destination, sibling, rename, force, null);
		} catch (RodinDBException jme) {
			assertTrue("Code not correct for RodinDBException: " + jme, jme.getStatus().getCode() == failureCode);
			return;
		}
		assertTrue("The copy should have failed for: " + element, false);
		return;
	}

	/**
	 * Attempts to copy the elements with optional 
	 * forcing. The operation should fail with the failure code.
	 */
	public void copyNegative(IRodinElement[] elements, IRodinElement[] destinations, IRodinElement[] siblings, String[] renames, boolean force, int failureCode) {
		try {
			getRodinDB().copy(elements, destinations, siblings, renames, force, null);
		} catch (RodinDBException jme) {
			assertTrue("Code not correct for RodinDBException: " + jme, jme.getStatus().getCode() == failureCode);
			return;
		}
		fail("The move should have failed for multiple elements");
		return;
	}

	/**
	 * Copies the element to the container with optional rename
	 * and forcing. The operation should succeed, so any exceptions
	 * encountered are thrown.
	 */
	public IRodinElement copyPositive(IElementManipulation element,
			IRodinElement container, IRodinElement sibling, String rename,
			boolean force) throws RodinDBException {

		// if forcing, ensure that a name collision exists
		if (force) {
			IRodinElement collision = generateHandle(element, rename, container);
			assertExists("Collision does not exist", collision);
		}
		
		IRodinElement copy;
		try {
			startDeltas();
			
			// copy
			element.copy(container, sibling, rename, force, null);
			
			// ensure the original element still exists
			assertExists("The original element must still exist", element);
			
			// generate the new element	handle
			copy = generateHandle(element, rename, container);
			assertExists("Copy should exist", copy);
			
			// ensure that the copy is the same as the source element
			if (element instanceof IInternalElement) {
				IInternalElement src = (IInternalElement) element;
				IInternalElement dst = (IInternalElement) copy;
				assertTrue(src.hasSameAttributes(dst));
				assertTrue(src.hasSameChildren(dst));
			}
			
			//ensure correct position
			if (element instanceof IInternalElement) {
				ensureCorrectPositioning((IParent) container, sibling, copy);
			}
			
			final IRodinElementDelta destDelta = getDeltaFor(container, true);
			assertTrue("Destination container not changed",
					destDelta != null
					&& destDelta.getKind() == IRodinElementDelta.CHANGED);
			IRodinElementDelta[] deltas = destDelta.getAffectedChildren();
			assertEquals("Wrong number of added children for element copy",
					1, deltas.length);
			if (force) {
				assertEquals("Invalid delta for element copy",
						IRodinElementDelta.CHANGED, deltas[0].getKind());
				assertTrue("delta for element copy is not a replace",
						(deltas[0].getFlags() & IRodinElementDelta.F_REPLACED) != 0);
			} else {
				assertEquals("Invalid delta for element copy",
						IRodinElementDelta.ADDED, deltas[0].getKind());
			}
			assertEquals("Added children not correct for element copy", 
					copy, deltas[0].getElement());
		} finally {
			stopDeltas();
		}
		return copy;
	}

	/**
	 * Check that dumb copy of an element to itself doesn't change anything.
	 * The operation should succeed, so any exceptions encountered are thrown.
	 */
	public void copyNoop(IRodinElement element, IRodinElement sibling) throws CoreException {

		IRodinDB db = element.getRodinDB();
		IRodinElement parent = element.getParent();
		ensureCorrectPositioning((IParent) parent, sibling, element);
		String stateBefore = expandAll(db);
		try {
			startDeltas();
			
			// copy
			((IElementManipulation) element).copy(element.getParent(), sibling, null, true, null);
			
			// ensure the database didn't change
			assertElementDescendants("The database should not have changed", stateBefore, db);
			
			// Check that there is no delta produced by the operation.
			assertDeltas("No delta should have been produced", "");
		} finally {
			stopDeltas();
		}
	}

	/**
	 * Generates a new handle to the original element in
	 * its new container.
	 */
	public IRodinElement generateHandle(IRodinElement original, String rename, IRodinElement container) {
		final String name;
		if (rename == null) {
			name = original.getElementName();
		} else {
			name = rename;
		}
		if (container instanceof IRodinProject) {
			assertTrue("illegal child type", original instanceof IRodinFile);
			return ((IRodinProject) container).getRodinFile(name);
		} else if (container instanceof IRodinFile) {
			final IInternalElement root = ((IRodinFile) container).getRoot();
			assertEquals(root.getElementType(), original.getElementType());
			assertEquals(root.getElementName(), name);
			return root;
		} else if (container instanceof IInternalElement) {
			assertTrue("illegal child type", original instanceof IInternalElement);
			final IInternalElementType<? extends IInternalElement> type =
				((IInternalElement) original).getElementType();
			return ((IInternalElement) container).getInternalElement(type, name);
		} else {
			assertTrue("illegal container type", false);
			return null;
		}
	}
	
	/**
	 * Attempts to move the element with optional 
	 * forcing. The operation should fail with the failure code.
	 */
	public void moveNegative(IRodinElement element, IRodinElement destination, IRodinElement sibling, String rename, boolean force, int failureCode) {
		try {
			((IElementManipulation)element).move(destination, sibling, rename, force, null);
		} catch (RodinDBException jme) {
			assertTrue("Code not correct for RodinDBException: " + jme, jme.getStatus().getCode() == failureCode);
			return;
		}
		assertTrue("The move should have failed for: " + element, false);
		return;
	}

	/**
	 * Attempts to move the element with optional 
	 * forcing. The operation should fail with the failure code.
	 */
	public void moveNegative(IRodinElement[] elements, IRodinElement[] destinations, IRodinElement[] siblings, String[] renames, boolean force, int failureCode) {
		try {
			getRodinDB().move(elements, destinations, siblings, renames, force, null);
		} catch (RodinDBException rde) {
			assertEquals("Code not correct for RodinDBException: " + rde,
					failureCode, rde.getStatus().getCode());
			return;
		}
		assertTrue("The move should have failed for for multiple elements: ", false);
		return;
	}
	
	/**
	 * Check that dumb move of an element to itself doesn't change anything.
	 * The operation should succeed, so any exceptions encountered are thrown.
	 */
	public void moveNoop(IRodinElement element, IRodinElement sibling) throws CoreException {

		IRodinDB db = element.getRodinDB();
		IRodinElement parent = element.getParent();
		ensureCorrectPositioning((IParent) parent, sibling, element);
		String stateBefore = expandAll(db);
		try {
			startDeltas();
			
			// copy
			((IElementManipulation) element).move(element.getParent(), sibling, null, true, null);
			
			// ensure the database didn't change
			assertElementDescendants("The database should not have changed", stateBefore, db);
			
			// Check that there is no delta produced by the operation.
			assertDeltas("No delta should have been produced", "");
		} finally {
			stopDeltas();
		}
	}

	/**
	 * Moves the element to the container with optional rename
	 * and forcing. The operation should succeed, so any exceptions
	 * encountered are thrown.
	 */
	public void movePositive(IRodinElement element, IRodinElement container, IRodinElement sibling, String rename, boolean force) throws RodinDBException {
		IRodinElement[] siblings = new IRodinElement[] {sibling};
		String[] renamings = new String[] {rename};
		if (sibling == null) {
			siblings = null;
		}
		if (rename == null) {
			renamings = null;
		}
		movePositive(new IRodinElement[] {element}, new IRodinElement[] {container}, siblings, renamings, force);
	}
	
	/**
	 * Moves the elements to the containers with optional renaming
	 * and forcing. The operation should succeed, so any exceptions
	 * encountered are thrown.
	 */
	public void movePositive(IRodinElement[] elements, IRodinElement[] destinations, IRodinElement[] siblings, String[] names, boolean force) throws RodinDBException {
		movePositive(elements, destinations, siblings, names, force, null);
	}
	
	/**
	 * Moves the elements to the containers with optional renaming
	 * and forcing. The operation should succeed, so any exceptions
	 * encountered are thrown.
	 */
	public void movePositive(IRodinElement[] elements, IRodinElement[] destinations, IRodinElement[] siblings, String[] names, boolean force, IProgressMonitor monitor) throws RodinDBException {
		// if forcing, ensure that a name collision exists
		int i;
		if (force) {
			for (i = 0; i < elements.length; i++) {
				IRodinElement e = elements[i];
				IRodinElement collision = null;
				if (names == null) {
					collision = generateHandle(e, null, destinations[i]);
				} else {
					collision = generateHandle(e, names[i], destinations[i]);
				}
				assertExists("Collision does not exist", collision);
			}
		}
		
		try {
			startDeltas();
			
			// move
			getRodinDB().move(elements, destinations, siblings, names, force, monitor);
			for (i = 0; i < elements.length; i++) {
				IRodinElement element = elements[i];
				IRodinElement moved = null;
				if (names == null) {
					moved = generateHandle(element, null, destinations[i]);
				} else {
					moved = generateHandle(element, names[i], destinations[i]);
				}
				// ensure the original element no longer exists, unless moving within the same container
				if (!destinations[i].equals(element.getParent())) {
					assertNotExists("The original element must not exist", element);
				}
				assertExists("Moved element should exist", moved);
				
				//ensure correct position
				if (element instanceof IInternalElement) {
					if (siblings != null && siblings.length > 0) {
						ensureCorrectPositioning((IParent) moved.getParent(), siblings[i], moved);
					}
				}
				IRodinElementDelta destDelta = null;
				destDelta = getDeltaFor(destinations[i], true);
				assertTrue("Destination container not changed", destDelta != null && destDelta.getKind() == IRodinElementDelta.CHANGED);
				IRodinElementDelta[] deltas = destDelta.getAddedChildren();
				if (deltas.length != 0) {
					assertEquals("Wrong number of added children for element move", deltas.length, 1);
					assertTrue("Added children not correct for element move", deltas[0].getElement().equals(moved));
				} else {
					deltas = destDelta.getChangedChildren();
					assertEquals("Wrong number of added children for element move", deltas.length, 1);
					assertEquals("Invalid delta for element move", IRodinElementDelta.CHANGED, deltas[0].getKind());
					assertTrue("delta for element move is not a replace",
							(deltas[0].getFlags() & IRodinElementDelta.F_REPLACED) != 0);
				}
				IRodinElementDelta sourceDelta= getDeltaFor(element, false);
				assertTrue("should be K_REMOVED", sourceDelta.getKind() == IRodinElementDelta.REMOVED);
			}
		} finally {
			stopDeltas();
		}
	}

	/**
	 * Attempts to rename the element with optional 
	 * forcing. The operation should fail with the failure code.
	 */
	public void renameNegative(IRodinElement element, String rename, boolean force, int failureCode) {
		try {
			((IElementManipulation)element).rename(rename, force, null);
		} catch (RodinDBException jme) {
			assertTrue("Code not correct for RodinDBException: " + jme, jme.getStatus().getCode() == failureCode);
			return;
		}
		assertTrue("The renaming should have failed for: " + element, false);
		return;
	}

	/**
	 * Attempts to rename the element with optional 
	 * forcing. The operation should fail with the failure code.
	 */
	public void renameNegative(IRodinElement[] elements, String[] renames, boolean force, int failureCode) {
		try {
			getRodinDB().rename(elements, renames, force, null);
		} catch (RodinDBException jme) {
			assertTrue("Code not correct for RodinDBException: " + jme, jme.getStatus().getCode() == failureCode);
			return;
		}
		assertTrue("The renaming should have failed for for multiple elements: ", false);
		return;
	}
	
	/**
	 * Check that dumb renaming of an element to itself doesn't change anything.
	 * The operation should succeed, so any exceptions encountered are thrown.
	 */
	public void renameNoop(IRodinElement element) throws CoreException {

		IRodinDB db = element.getRodinDB();
		String stateBefore = expandAll(db);
		try {
			startDeltas();
			
			// copy
			((IElementManipulation) element).rename(element.getElementName(), true, null);
			
			// ensure the database didn't change
			assertElementDescendants("The database should not have changed", stateBefore, db);
			
			// Check that there is no delta produced by the operation.
			assertDeltas("No delta should have been produced", "");
		} finally {
			stopDeltas();
		}
	}

	/**
	 * Renames the element with optional forcing. The operation should succeed,
	 * so any exceptions encountered are thrown.
	 */
	public void renamePositive(IRodinElement element, String name, boolean force) throws RodinDBException {
		renamePositive(new IRodinElement[] {element}, new String[] {name}, force, null);
	}

	/**
	 * Renames the elements with optional forcing. The operation should succeed,
	 * so any exceptions encountered are thrown.
	 */
	public void renamePositive(IRodinElement[] elements, String[] names, boolean force, IProgressMonitor monitor) throws RodinDBException {
		assertNotNull("New names are not given", names);
		// if forcing, ensure that a name collision exists
		if (force) {
			for (int i = 0; i < elements.length; i++) {
				IRodinElement e = elements[i];
				assertNotNull("New name is null", names[i]);
				IRodinElement collision = generateHandle(e, names[i], e.getParent());
				assertExists("Collision does not exist", collision);
			}
		}
		
		try {
			startDeltas();
			
			// move
			getRodinDB().rename(elements, names, force, monitor);
			for (int i = 0; i < elements.length; i++) {
				IRodinElement element = elements[i];
				IRodinElement parent = element.getParent();
				IRodinElement renamed = generateHandle(element, names[i], parent);
				
				// ensure the original element no longer exists
				assertNotExists("The original element must not exist", element);
				assertExists("Renamed element should exist", renamed);
				
				IRodinElementDelta destDelta = null;
				destDelta = getDeltaFor(parent, true);
				assertTrue("Parent not changed", destDelta != null && destDelta.getKind() == IRodinElementDelta.CHANGED);
				IRodinElementDelta[] deltas = destDelta.getAddedChildren();
				if (deltas.length != 0) {
					assertEquals("Wrong number of added children for element renaming", 
							1, deltas.length);
					assertEquals("Added children not correct for element renaming",
							renamed, deltas[0].getElement());
				} else {
					deltas = destDelta.getChangedChildren();
					assertEquals("Wrong number of added children for element renaming",
							1, deltas.length);
					assertEquals("Invalid delta for element renaming",
							IRodinElementDelta.CHANGED, deltas[0].getKind());
					assertTrue("delta for element renaming is not a replace",
							(deltas[0].getFlags() & IRodinElementDelta.F_REPLACED) != 0);
				}
				IRodinElementDelta sourceDelta= getDeltaFor(element, false);
				assertEquals("delta for renamed element should be REMOVED", 
						IRodinElementDelta.REMOVED, sourceDelta.getKind());
			}
		} finally {
			stopDeltas();
		}
	}

	/**
	 * Reorders the element. The operation should succeed,
	 * so any exceptions encountered are thrown.
	 */
	public void reorderPositive(IInternalElement src, IInternalElement nextSibling, boolean changeExpected) throws RodinDBException {
		try {
			startDeltas();
			
			// move
			src.move(src.getParent(), nextSibling, null, false, null);
			
			// ensure the original element still exists
			assertExists("Reordered element should exist", src);
				
			//ensure correct position
			ensureCorrectPositioning((IParent) src.getParent(), nextSibling, src);

			// check delta
			IRodinElementDelta destDelta = getDeltaFor(src.getParent(), true);
			if (changeExpected) {
				assertTrue("Destination container not changed", destDelta != null && destDelta.getKind() == IRodinElementDelta.CHANGED);
				IRodinElementDelta[] deltas = destDelta.getChangedChildren();
				assertEquals("Wrong number of changed children for element reorder", 
						1, deltas.length);
				assertEquals("Changed children not correct for element reorder",
						src, deltas[0].getElement());
				assertEquals("Delta flags not correct for element reorder", 
						IRodinElementDelta.F_REORDERED, deltas[0].getFlags());
			} else {
				assertNull("Parent changed in no-op reordering", destDelta);
			}
		} finally {
			stopDeltas();
		}
	}

	/**
	 * Attempts to reorder the element.
	 * The operation should fail with the failure code.
	 */
	public void reorderNegative(IInternalElement src, IInternalElement nextSibling, int failureCode) throws RodinDBException {
		try {
			startDeltas();

//			reorder
			try {
				src.move(src.getParent(), nextSibling, null, false, null);
				fail("The reordering should have failed for: " + src);
			} catch (RodinDBException jme) {
				assertTrue("Code not correct for RodinDBException: " + jme, jme.getStatus().getCode() == failureCode);
				return;
			}

			// check delta
			IRodinElementDelta destDelta = getDeltaFor(src.getParent(), true);
			assertNull("Parent changed in failed reordering", destDelta);
		} finally {
			stopDeltas();
		}
	}

}
