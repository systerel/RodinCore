package org.rodinp.core.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

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
		assertTrue(e12.exists());
		checkChildren(rodinFile, e0, e1, e2, e3);
		checkEmpty(e0, e2, e3);
		checkEmptyChildren(e1, e11, e12);

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

	// Test creation of duplicate internal elements
	public void testCreateDuplicate() throws Exception {
		// File exists and is empty
		assertTrue(rodinFile.exists());
		checkEmptyChildren(rodinFile);

		IInternalElement e1 = createNamedElement(rodinFile, "e", null);
		checkEmptyChildren(rodinFile, e1);

		IInternalElement e2 = createNamedElement(rodinFile, "e", null);
		checkEmptyChildren(rodinFile, e1, e2);
		assertFalse(e1.equals(e2));
		assertFalse(e2.equals(e1));

		IInternalElement e11 = createNamedElement(e1, "e", null);
		checkChildren(rodinFile, e1, e2);
		checkChildren(e1, e11);
		checkEmpty(e11, e2);
		
		IInternalElement e12 = createNamedElement(e1, "e", null);
		checkChildren(rodinFile, e1, e2);
		checkChildren(e1, e11, e12);
		checkEmpty(e11, e12, e2);
		assertFalse(e11.equals(e12));
		assertFalse(e12.equals(e11));

		// Cleanup
		rodinFile.getResource().delete(true, null);
		rodinFile = rodinProject.createRodinFile("toto.test", true, null);
		checkChildren(rodinFile);
	}
	
	// Test creation of some internal elements with contents
	public void testInternalElementWithContents() throws Exception {
		// File exists and is empty
		assertTrue(rodinFile.exists());
		checkEmptyChildren(rodinFile);
		
		IInternalElement e1 = createNamedElement(rodinFile, "e1", null);
		checkEmptyChildren(rodinFile, e1);
		assertEquals("", e1.getContents());
		
		e1.setContents("Hello");
		checkEmptyChildren(rodinFile, e1);
		assertEquals("Hello", e1.getContents());

		rodinFile.save(null, true);
		rodinFile.close();
		
		assertTrue(e1.exists());
		checkEmptyChildren(rodinFile, e1);
		assertEquals("Hello", e1.getContents());
		
		try {
			e1.setContents(null);
			assertFalse("null contents should raise an error", true);
		} catch (RodinDBException e) {
			assertEquals("null contents should raise a NULL_STRING error",
					IRodinDBStatusConstants.NULL_STRING, e.getStatus().getCode());
		}
		assertEquals("Hello", e1.getContents());
		
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
		
		e1.setContents("Hello");
		assertEquals("Hello", e1.getContents());
		rodinFile.save(null, true);
		assertEquals("Hello", e1.getContents());
		rodinFile.close();
		assertEquals("Hello", e1.getContents());
		
		e1.setContents("Bye");
		assertEquals("Bye", e1.getContents());
		rodinFile.save(null, true);
		assertEquals("Bye", e1.getContents());
		rodinFile.close();
		assertEquals("Bye", e1.getContents());
		
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
