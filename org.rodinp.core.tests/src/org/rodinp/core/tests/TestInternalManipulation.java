package org.rodinp.core.tests;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.basis.NamedElement;

public class TestInternalManipulation extends ModifyingResourceTests {

	public TestInternalManipulation() {
		super("org.rodinp.core.tests.TestInternalManipulation");
	}

	private IRodinProject rodinProject;
	private IRodinFile rodinFile;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("foo");
		rodinFile = rodinProject.createRodinFile("toto.test", true, null);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		rodinFile.getResource().delete(true, null);
		rodinProject.getProject().delete(true, true, null);
		rodinProject.getRodinDB().close();
	}

	private void checkChildren(IParent parent,
			IInternalElement... expChildren) throws RodinDBException {
		if (expChildren.length == 0) {
			assertEquals(parent.getChildren().length, 0);
			assertFalse(parent.hasChildren());
		} else {
			IRodinElement[] children = parent.getChildren();
			assertTrue(parent.hasChildren());
			assertEquals(expChildren.length, children.length);
			for (int i = 0; i < children.length; ++i) {
				assertEquals(expChildren[i], children[i]);
			}
		}
	}
	
	private void checkEmpty(IInternalElement... children) throws RodinDBException {
		for (IInternalElement child: children) {
			assertFalse(child.hasChildren());
		}
	}
	
	private void checkEmptyChildren(IParent parent,
			IInternalElement... expChildren) throws RodinDBException {
		checkChildren(parent, expChildren);
		checkEmpty(expChildren);
	}
	
	// Test creation of some top-level internal elements
	public void testCreateInternalElement() throws Exception {
		// File exists and is empty
		assertTrue(rodinFile.exists());
		checkEmptyChildren(rodinFile);
		
		IInternalElement e1 = createNamedElement(rodinFile, "e1", null);
		checkEmptyChildren(rodinFile, e1);

		IInternalElement e3 = createNamedElement(rodinFile, "e3", null);
		checkEmptyChildren(rodinFile, e1, e3);

		IInternalElement e2 = createNamedElement(rodinFile, "e2", e3);
		checkEmptyChildren(rodinFile, e1, e2, e3);

		IInternalElement e0 = createNamedElement(rodinFile, "e0", e1);
		checkEmptyChildren(rodinFile, e0, e1, e2, e3);

		rodinFile.save(null, true);
		rodinFile.close();
		
		//showFile(rodinFile.getResource());
		
		assertTrue(e0.exists());
		assertTrue(e1.exists());
		assertTrue(e2.exists());
		assertTrue(e3.exists());
		checkEmptyChildren(rodinFile, e0, e1, e2, e3);
		
		// Create sub-elements
		IInternalElement e12 = createNamedElement(e1, "e12", null);
		assertTrue(e12.exists());
		checkChildren(rodinFile, e0, e1, e2, e3);
		checkEmpty(e0, e2, e3);
		checkEmptyChildren(e1, e12);
		
		IInternalElement e11 = createNamedElement(e1, "e11", e12);
		assertTrue(e11.exists());
		checkChildren(rodinFile, e0, e1, e2, e3);
		checkEmpty(e0, e2, e3);
		checkEmptyChildren(e1, e11, e12);

		assertEquals("Unexpected file contents",
				"toto.test [in foo]\n"
				+ "  e0[org.rodinp.core.tests.namedElement]\n"
				+ "  e1[org.rodinp.core.tests.namedElement]\n"
				+ "    e11[org.rodinp.core.tests.namedElement]\n"
				+ "    e12[org.rodinp.core.tests.namedElement]\n"
				+ "  e2[org.rodinp.core.tests.namedElement]\n"
				+ "  e3[org.rodinp.core.tests.namedElement]",
				rodinFile.toString());
		
		// rodinFile.save(null, true);
		// showFile(rodinFile.getResource());
		
		// Cleanup
		rodinFile.getResource().delete(true, null);
		assertFalse(rodinFile.exists());
		rodinFile = rodinProject.createRodinFile("toto.test", true, null);
		assertFalse(e0.exists());
		assertFalse(e1.exists());
		assertFalse(e11.exists());
		assertFalse(e12.exists());
		assertFalse(e2.exists());
		assertFalse(e3.exists());
		checkChildren(rodinFile);
	}

	/**
	 * Ensures that attempting to create a new top-level internal element with
	 * the same type and name as an existing one fails.
	 */
	public void testCreateTopDuplicate() throws Exception {
		// File exists and is empty
		assertTrue(rodinFile.exists());
		checkEmptyChildren(rodinFile);
		
		NamedElement e1 = createNamedElement(rodinFile, "foo", null);
		checkEmptyChildren(rodinFile, e1);

		// Attempt to create a duplicate element
		NamedElement e2 = createNamedElement(rodinFile, "foo", null);
		assertNull("Creation should have failed", e2);
		
		// File has not changed
		checkEmptyChildren(rodinFile, e1);
	}

	/**
	 * Ensures that attempting to create a new non top-level internal element
	 * with the same type and name as an existing one fails.
	 */
	public void testCreateNonTopDuplicate() throws Exception {
		// File exists and is empty
		assertTrue(rodinFile.exists());
		checkEmptyChildren(rodinFile);
		
		NamedElement e1 = createNamedElement(rodinFile, "foo", null);
		NamedElement e11 = createNamedElement(e1, "bar", null);
		checkChildren(rodinFile, e1);
		checkEmptyChildren(e1, e11);

		// Attempt to create a duplicate element
		NamedElement e12 = createNamedElement(e1, "bar", null);
		assertNull("Creation should have failed", e12);
		
		// File has not changed
		checkChildren(rodinFile, e1);
		checkEmptyChildren(e1, e11);
	}

	// Test creation of a Rodin file and an internal element as an atomic action
	public void testCreateInternalElementAtomic() throws Exception {
		
		// Start with a fresh file
		final String fileName = "titi.test";
		final IRodinFile rf = rodinProject.getRodinFile(fileName);
		assertFalse("Target file should not exist", rf.exists());
		
		try {
			RodinCore.run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					rodinProject.createRodinFile(fileName, false, null);
					IInternalElement e1 = createNamedElement(rf, "e1", null);
					assertTrue("New file should exist", rf.exists());
					checkEmptyChildren(rf, e1);
				}
			}, null);

			assertTrue("New file should exist", rf.exists());

			IInternalElement e1 =
				rf.getInternalElement(NamedElement.ELEMENT_TYPE, "e1");
			checkEmptyChildren(rf, e1);
		} finally {
			// Ensure the new Rodin file is destroyed.
			rf.delete(true, null);
		}
	}

	// Test deletion of an internal element bypassing the Rodin database
	public void testDeleteInternalElementBypass() throws Exception {

		// First create an internal element
		final IInternalElement e1 = createNamedElement(rodinFile, "e1", null);
		checkEmptyChildren(rodinFile, e1);

		startDeltas();
		
		// Then, inside a workable, empty the Rodin file bypassing the database
		// The database is not updated!
		RodinCore.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IFile file = rodinFile.getResource();
				file.setContents(new ByteArrayInputStream(emptyBytes),
						false, true, null);
				
				// Inside the runnable, the Rodin database doesn't get the
				// resource change delta, so the element is still considered to
				// exist.
				assertTrue("Internal element should exist", e1.exists());
				checkEmptyChildren(rodinFile, e1);
			}
		}, null);

		assertDeltas("Should report an unknown change to the file",
				"foo[*]: {CHILDREN}\n" + 
				"	toto.test[*]: {CONTENT}"
		);

		// Once the workable is finished, the database gets the resource delta
		checkEmptyChildren(rodinFile);
		assertFalse("Internal element should not exist", e1.exists());
	}

	
	// Test creation of some internal elements with contents
	public void testInternalElementWithContents() throws Exception {
		// File exists and is empty
		assertTrue(rodinFile.exists());
		checkEmptyChildren(rodinFile);
		
		IInternalElement e1 = createNamedElement(rodinFile, "e1", null);
		checkEmptyChildren(rodinFile, e1);
		assertEquals("", e1.getContents());
		
		assertContentsChanged(e1, "Hello");
		checkEmptyChildren(rodinFile, e1);

		rodinFile.save(null, true);
		rodinFile.close();
		
		checkEmptyChildren(rodinFile, e1);
		assertContents("Contents should not have changed", "Hello", e1);
		
		try {
			e1.setContents(null);
			fail("null contents should raise an error");
		} catch (RodinDBException e) {
			assertEquals("null contents should raise a NULL_STRING error",
					IRodinDBStatusConstants.NULL_STRING, e.getStatus().getCode());
		}
		assertContents("Contents should not have changed", "Hello", e1);
		
		// showFile(rodinFile.getResource());

		// Cleanup
		rodinFile.getResource().delete(true, null);
		assertFalse(rodinFile.exists());
		rodinFile = rodinProject.createRodinFile("toto.test", true, null);
		assertFalse(e1.exists());
		checkChildren(rodinFile);
	}

	// Test modification of some internal element with contents
	public void testChangeContents() throws Exception {
		// File exists and is empty
		assertTrue(rodinFile.exists());
		checkEmptyChildren(rodinFile);
		
		IInternalElement e1 = createNamedElement(rodinFile, "e1", null);
		assertEquals("", e1.getContents());
		rodinFile.save(null, true);
		assertEquals("", e1.getContents());
		rodinFile.close();
		assertEquals("", e1.getContents());
		
		assertContentsChanged(e1, "Hello");
		rodinFile.save(null, true);
		assertContents("Contents should not have changed", "Hello", e1);
		rodinFile.close();
		assertContents("Contents should not have changed", "Hello", e1);
		
		assertContentsChanged(e1, "Bye");
		rodinFile.save(null, true);
		assertContents("Contents should not have changed", "Bye", e1);
		rodinFile.close();
		assertContents("Contents should not have changed", "Bye", e1);
		
		// Cleanup
		rodinFile.getResource().delete(true, null);
		assertFalse(rodinFile.exists());
		rodinFile = rodinProject.createRodinFile("toto.test", true, null);
		assertFalse(e1.exists());
		checkChildren(rodinFile);
	}

	@SuppressWarnings("unused")
	private void showFile(IFile file) throws CoreException, UnsupportedEncodingException, IOException {
		InputStream contents = file.getContents();
		BufferedReader reader = new BufferedReader(new InputStreamReader(contents, "UTF-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		reader.close();
	}

}
