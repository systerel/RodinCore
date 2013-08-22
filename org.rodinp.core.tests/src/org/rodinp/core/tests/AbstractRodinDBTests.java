/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.tests.model.AbstractJavaModelTests
 *     Systerel - refactored getRodinDB()
 *     Systerel - added assertions for clearing
 *     Systerel - fixed use of pseudo-attribute "contents"
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;

import junit.framework.TestCase;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IElementManipulation;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.NamedElement2;
import org.rodinp.core.tests.util.Util;
import org.rodinp.internal.core.debug.DebugHelpers;

public abstract class AbstractRodinDBTests extends TestCase {

	public static final String PLUGIN_ID = "org.rodinp.core.tests";

	public static final IAttributeType.Boolean fBool = RodinCore
			.getBooleanAttrType(PLUGIN_ID + ".fBool");
	public static final IAttributeType.Handle fHandle = RodinCore
			.getHandleAttrType(PLUGIN_ID + ".fHandle");
	public static final IAttributeType.Integer fInt = RodinCore
			.getIntegerAttrType(PLUGIN_ID + ".fInt");
	public static final IAttributeType.Long fLong = RodinCore
			.getLongAttrType(PLUGIN_ID + ".fLong");
	public static final IAttributeType.String fString = RodinCore
			.getStringAttrType(PLUGIN_ID + ".fString");

	// infos for invalid results
	protected static final int tabs = 2;
	protected boolean displayName = false;
	protected static final String endChar = ",";
	
//	public static class ProblemRequestor implements IProblemRequestor {
//		public StringBuffer problems;
//		public int problemCount;
//		private char[] unitSource;
//		public ProblemRequestor() {
//			initialize(null);
//		}
//		public void acceptProblem(IProblem problem) {
//			org.eclipse.jdt.core.tests.util.Util.appendProblem(this.problems, problem, this.unitSource, ++this.problemCount);
//			this.problems.append("----------\n");
//		}
//		public void beginReporting() {
//			this.problems.append("----------\n");
//		}
//		public void endReporting() {
//			if (this.problemCount == 0)
//				this.problems.append("----------\n");
//		}
//		public boolean isActive() {
//			return true;
//		}
//		public void initialize(char[] source) {
//			this.problems = new StringBuffer();
//			this.problemCount = 0;
//			this.unitSource = source;
//		}
//	}
	
	/**
	 * Delta listener
	 */
	protected static class DeltaListener implements IElementChangedListener {
		/**
		 * Deltas received from the Rodin database. See 
		 * <code>#startDeltas</code> and
		 * <code>#stopDeltas</code>.
		 */
		public IRodinElementDelta[] deltas;
		
		public ByteArrayOutputStream stackTraces;
	
		public void elementChanged(ElementChangedEvent ev) {
			IRodinElementDelta[] copy= new IRodinElementDelta[deltas.length + 1];
			System.arraycopy(deltas, 0, copy, 0, deltas.length);
			copy[deltas.length]= ev.getDelta();
			deltas= copy;
			
			new Throwable("Caller of IElementChangedListener#elementChanged").printStackTrace(new PrintStream(this.stackTraces));
		}
		
		protected void sortDeltas(IRodinElementDelta[] elementDeltas) {
			Comparator<IRodinElementDelta> comparator = new Comparator<IRodinElementDelta>() {
				public int compare(IRodinElementDelta deltaA, IRodinElementDelta deltaB) {
					return deltaA.getElement().getElementName().compareTo(deltaB.getElement().getElementName());
				}
			};
			Arrays.sort(elementDeltas, comparator);
		}

		public String toString() {
			StringBuffer buffer = new StringBuffer();
			for (int i=0, length= this.deltas.length; i<length; i++) {
				IRodinElementDelta[] projects = this.deltas[i].getAffectedChildren();
				sortDeltas(projects);
				for (int j=0, projectsLength=projects.length; j<projectsLength; j++) {
					buffer.append(projects[j]);
					if (j != projectsLength-1) {
						buffer.append("\n");
					}
				}
				IResourceDelta[] nonRodinProjects = this.deltas[i].getResourceDeltas();
				if (nonRodinProjects != null) {
					for (int j=0, nonRodinProjectsLength=nonRodinProjects.length; j<nonRodinProjectsLength; j++) {
						if (j == 0 && buffer.length() != 0) {
							buffer.append("\n");
						}
						buffer.append(nonRodinProjects[j]);
						if (j != nonRodinProjectsLength-1) {
							buffer.append("\n");
						}
					}
				}
				if (i != length-1) {
					buffer.append("\n\n");
				}
			}
			return buffer.toString();
		}
	}

	public static void assertDiffers(String message, Object expected, Object actual) {
		if (expected == null && actual == null)
			fail(message);
		if (expected != null && expected.equals(actual))
			fail(message);
		if (actual != null && actual.equals(expected))
			fail(message);
	}
	
	/*
	 * Assert existence of the given element. As a side-effect, checks that the
	 * existence test doesn't change the open status of the element, if it's
	 * openable.
	 */
	protected static void assertExists(String message, IRodinElement element) {
		final IOpenable openable;
		final boolean isOpen;
		if (element instanceof IOpenable) {
			openable = (IOpenable) element;
			isOpen = openable.isOpen();
		} else {
			openable = null;
			isOpen = false;
		}
		assertTrue(message, element.exists());
		if (openable != null) {
			assertEquals("Open status of element changed during existence test",
					isOpen, openable.isOpen());
		}
	}
	
	/*
	 * Assert non-existence of the given element. As a side-effect, checks that
	 * the existence test doesn't change the open status of the element, if it's
	 * openable.
	 */
	protected static void assertNotExists(String message, IRodinElement element) {
		assertFalse(message, element.exists());
		if (element instanceof IOpenable) {
			final IOpenable openable = (IOpenable) element;
			assertFalse("Inexistent element should not be open",
					openable.isOpen());
		}
	}
	
	/*
	 * Assert that the given element has been cleared (doesn't have any
	 * attribute or children)
	 */
	protected static void assertCleared(String message, IInternalElement element)
			throws RodinDBException {
		assertEquals(message, 0, element.getAttributeTypes().length);
		assertEquals(message, 0, element.getChildren().length);
	}
	
	/**
	 * Wait for autobuild notification to occur
	 */
	public static void waitForAutoBuild() {
		boolean wasInterrupted = false;
		do {
			try {
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
				wasInterrupted = false;
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				wasInterrupted = true;
			}
		} while (wasInterrupted);
	}

	protected DeltaListener deltaListener = new DeltaListener();
	 
	
	public AbstractRodinDBTests(String name) {
		super(name);
	}

//	public static Test buildTestSuite(Class evaluationTestClass) {
//		return buildTestSuite(evaluationTestClass, null); //$NON-NLS-1$
//	}
//
//	public static Test buildTestSuite(Class evaluationTestClass, String suiteName) {
//		TestSuite suite = new Suite(suiteName==null?evaluationTestClass.getName():suiteName);
//		List tests = buildTestsList(evaluationTestClass);
//		for (int index=0, size=tests.size(); index<size; index++) {
//			suite.addTest((Test)tests.get(index));
//		}
//		return suite;
//	}

	protected static void addRodinNature(String projectName) throws CoreException {
		IProject project = getWorkspaceRoot().getProject(projectName);
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(description, null);
	}
	
	protected void assertSearchResults(String expected, Object collector) {
		assertSearchResults("Unexpected search results", expected, collector);
	}
	
	protected void assertSearchResults(String message, String expected, Object collector) {
		String actual = collector.toString();
		if (!expected.equals(actual)) {
			if (this.displayName) System.out.println(getName()+" actual result is:");
			System.out.print(displayString(actual, tabs));
			System.out.println(",");
		}
		assertEquals(
			message,
			expected,
			actual
		);
	}

	protected void assertSortedElementsEqual(String message, String expected, IRodinElement[] elements) {
		sortElements(elements);
		assertElementsEqual(message, expected, elements);
	}
	
	
	protected void assertResourcesEqual(String message, String expected, IResource[] resources) {
		sortResources(resources);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, length = resources.length; i < length; i++){
			IResource resource = (IResource)resources[i];
			buffer.append(resource == null ? "<null>" : resource.getName());
			if (i != length-1)buffer.append("\n");
		}
		if (!expected.equals(buffer.toString())) {
			System.out.print(Util.displayString(buffer.toString(), 2));
			System.out.println(endChar);
		}
		assertEquals(
			message,
			expected,
			buffer.toString()
		);
	}

	protected void assertElementEquals(String message, String expected, IRodinElement element) {
		String actual = element == null ? "<null>" : ((RodinElement) element).toStringWithAncestors();
		if (!expected.equals(actual)) {
			if (this.displayName) System.out.println(getName()+" actual result is:");
			System.out.println(displayString(actual, tabs) + endChar);
		}
		assertEquals(message, expected, actual);
	}
	
	protected void assertElementsEqual(String message, String expected, IRodinElement[] elements) {
		StringBuffer buffer = new StringBuffer();
		if (elements != null) {
			for (int i = 0, length = elements.length; i < length; i++){
				RodinElement element = (RodinElement)elements[i];
				if (element == null) {
					buffer.append("<null>");
				} else {
					buffer.append(element.toStringWithAncestors());
				}
				if (i != length-1) buffer.append("\n");
			}
		} else {
			buffer.append("<null>");
		}
		String actual = buffer.toString();
		if (!expected.equals(actual)) {
			if (this.displayName) System.out.println(getName()+" actual result is:");
			System.out.println(displayString(actual, tabs) + endChar);
		}
		assertEquals(message, expected, actual);
	}

//	protected void assertProblems(String message, String expected, ProblemRequestor problemRequestor) {
//		String actual = org.eclipse.jdt.core.tests.util.Util.convertToIndependantLineDelimiter(problemRequestor.problems.toString());
//		String independantExpectedString = org.eclipse.jdt.core.tests.util.Util.convertToIndependantLineDelimiter(expected);
//		if (!independantExpectedString.equals(actual)){
//		 	System.out.println(org.eclipse.jdt.core.tests.util.Util.displayString(actual, this.tabs));
//		}
//		assertEquals(
//			message,
//			independantExpectedString,
//			actual);
//	}

	/**
	 * Ensures that the given element has the given contents.
	 */
	public static void assertContents(String message, String expectedContents,
			IInternalElement element) throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertEquals(message, expectedContents, element.getAttributeValue(fString));
	}
	
	/**
	 * Ensures that changing the contents of the given element works properly.
	 */
	public static void assertContentsChanged(IInternalElement element,
			String newContents) throws RodinDBException {
		
		assertExists("Element should exist", element);
		element.setAttributeValue(fString, newContents, null);
		assertContents("Contents should have changed", newContents, element);
	}
	
	/**
	 * Ensures that changing the contents of the given element is rejected.
	 */
	public static void assertContentsNotChanged(IInternalElement element,
			String newContents) throws RodinDBException {
		
		assertExists("Element should exist", element);
		assertTrue("Element should be read-only", element.isReadOnly());
		String oldContents = element.getAttributeValue(fString);
		assertDiffers("Old and new contents should differ",
				oldContents, newContents);
		try {
			element.setAttributeValue(fString, newContents, null);
			fail("Changed contents of a read-only element.");
		} catch (RodinDBException e) {
			assertReadOnlyErrorFor(e, element);
		}
		assertContents("Contents should not have changed", oldContents, element);
	}
	
	/**
	 * Ensures the elements are present after creation.
	 */
	public static void assertCreation(IRodinElement[] newElements) {
		for (int i = 0; i < newElements.length; i++) {
			IRodinElement newElement = newElements[i];
			assertExists("Element should be present after creation", newElement);
		}
	}

	/**
	 * Ensures the element is present after creation.
	 */
	public static void assertCreation(IRodinElement newElement) {
		assertCreation(new IRodinElement[] {newElement});
	}
	
	/**
	 * Creates an operation to delete the given elements, asserts
	 * the operation is successful, and ensures the elements are no
	 * longer present in the database.
	 */
	public static void assertDeletion(IRodinElement[] elementsToDelete) throws RodinDBException {
		for (IRodinElement element: elementsToDelete) {
			assertExists("Element must be present to be deleted", element);
		}
	
		getRodinDB().delete(elementsToDelete, false, null);
		
		for (IRodinElement element: elementsToDelete) {
			assertNotExists(
					"Element should not be present after deletion: " + element,
					element);
		}
	}

	protected void assertDeltas(String message, String expected) {
		String actual = this.deltaListener.toString();
		if (!expected.equals(actual)) {
			System.out.println("Expected:\n" + expected);
			System.out.println("Got:\n" + displayString(actual, 2));
			System.out.println(this.deltaListener.stackTraces.toString());
		}
		assertEquals(
				message,
				expected,
				actual);
	}
	
	protected void assertNoDeltas(String message) {
		assertDeltas(message, "");
	}

	protected static void assertStringsEqual(String message, String expected, String[] strings) {
		String actual = toString(strings, true/*add extra new lines*/);
		if (!expected.equals(actual)) {
			System.out.println(displayString(actual, 3) + endChar);
		}
		assertEquals(message, expected, actual);
	}

	protected static void assertStringsEqual(String message, String[] expectedStrings, String[] actualStrings) {
		String expected = toString(expectedStrings, false/*don't add extra new lines*/);
		String actual = toString(actualStrings, false/*don't add extra new lines*/);
		if (!expected.equals(actual)) {
			System.out.println(displayString(actual, 3) + endChar);
		}
		assertEquals(message, expected, actual);
	}
	
	/**
	 * Creates an operation to delete the given element, asserts
	 * the operation is successful, and ensures the element is no
	 * longer present in the database.
	 */
	public static void assertDeletion(IRodinElement elementToDelete) throws RodinDBException {
		assertDeletion(new IRodinElement[] {elementToDelete});
	}

	/**
	 * Creates an operation to clear the given element, asserts the operation is
	 * successful, and ensures the element has no longer any attribute nor child
	 * present in the database.
	 */
	public static void assertClearing(IInternalElement elementToClear)
			throws RodinDBException {
		assertExists("Element must be present to be cleared", elementToClear);
		((IElementManipulation) elementToClear).clear(false, null);
		assertCleared("Element should be cleared after clearing: "
				+ elementToClear, elementToClear);
	}

	/**
	 * Empties the current deltas.
	 */
	public void clearDeltas() {
		this.deltaListener.deltas = new IRodinElementDelta[0];
		this.deltaListener.stackTraces = new ByteArrayOutputStream();
	}
	
	/**
	 * Copy file from src (path to the original file) to dest (path to the destination file).
	 */
	public static void copy(File src, File dest) throws IOException {
		// read source bytes
		byte[] srcBytes = read(src);
		
//		if (convertToIndependantLineDelimiter(src)) {
//			String contents = new String(srcBytes);
//			contents = Util.convertToIndependantLineDelimiter(contents);
//			srcBytes = contents.getBytes();
//		}
	
		// write bytes to dest
		FileOutputStream out = new FileOutputStream(dest);
		out.write(srcBytes);
		out.close();
	}
	
//	public boolean convertToIndependantLineDelimiter(File file) {
//		return file.getName().endsWith(".java");
//	}
	
	/**
	 * Copy the given source directory (and all its contents) to the given target directory.
	 */
	protected static void copyDirectory(File source, File target) throws IOException {
		if (!target.exists()) {
			target.mkdirs();
		}
		File[] files = source.listFiles();
		if (files == null) return;
		for (int i = 0; i < files.length; i++) {
			File sourceChild = files[i];
			String name =  sourceChild.getName();
			if (name.equals("CVS")) continue;
			File targetChild = new File(target, name);
			if (sourceChild.isDirectory()) {
				copyDirectory(sourceChild, targetChild);
			} else {
				copy(sourceChild, targetChild);
			}
		}
	}

	protected static IFolder createFolder(IPath path) throws CoreException {
		final IFolder folder = getWorkspaceRoot().getFolder(path);
		getWorkspace().run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IContainer parent = folder.getParent();
				if (parent instanceof IFolder && !parent.exists()) {
					createFolder(parent.getFullPath());
				} 
				folder.create(true, true, null);
			}
		},
		null);
	
		return folder;
	}
	
	protected static IRodinProject createRodinProject(final String projectName) throws CoreException {
		IWorkspaceRunnable create = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				// create project
				createProject(projectName);
				
				// set Rodin nature
				addRodinNature(projectName);
			}
		};
		getWorkspace().run(create, null);	
		return getRodinProject(projectName);
	}
	
	/*
	 * Create simple project.
	 */
	protected static IProject createProject(final String projectName) throws CoreException {
		final IProject project = getProject(projectName);
		IWorkspaceRunnable create = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				project.create(null);
				project.open(null);
			}
		};
		getWorkspace().run(create, null);	
		return project;
	}
	
	public static void deleteFile(File file) {
		file = file.getAbsoluteFile();
		if (!file.exists())
			return;
		if (file.isDirectory()) {
			String[] files = file.list();
			//file.list() can return null
			if (files != null) {
				for (int i = 0; i < files.length; ++i) {
					deleteFile(new File(file, files[i]));
				}
			}
		}
		boolean success = file.delete();
		int retryCount = 60; // wait 1 minute at most
		while (!success && --retryCount >= 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			success = file.delete();
		}
		if (success) return;
		System.err.println("Failed to delete " + file.getPath());
	}
	
	protected static void deleteFolder(IPath folderPath) throws CoreException {
		deleteResource(getFolder(folderPath));
	}
	
	protected static void deleteProject(String projectName) throws CoreException {
		IProject project = getProject(projectName);
		if (project.exists() && !project.isOpen()) { // force opening so that project can be deleted without logging (see bug 23629)
			project.open(null);
		}
		deleteResource(project);
	}
	
	/**
	 * Batch deletion of projects
	 */
	protected static void deleteProjects(final String[] projectNames) throws CoreException {
		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				if (projectNames != null){
					for (int i = 0, max = projectNames.length; i < max; i++){
						if (projectNames[i] != null)
							deleteProject(projectNames[i]);
					}
				}
			}
		},
		null);
	}
	
	/**
	 * Delete this resource.
	 */
	public static void deleteResource(IResource resource) throws CoreException {
		CoreException lastException = null;
		try {
			resource.delete(true, null);
		} catch (CoreException e) {
			lastException = e;
			// just print for info
			System.out.println(e.getMessage());
		} catch (IllegalArgumentException iae) {
			// just print for info
			System.out.println(iae.getMessage());
		}
		int retryCount = 60; // wait 1 minute at most
		while (resource.isAccessible() && --retryCount >= 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			try {
				resource.delete(true, null);
			} catch (CoreException e) {
				lastException = e;
				// just print for info
				System.out.println("Retry "+retryCount+": "+ e.getMessage());
			} catch (IllegalArgumentException iae) {
				// just print for info
				System.out.println("Retry "+retryCount+": "+ iae.getMessage());
			}
		}
		if (!resource.isAccessible()) return;
		System.err.println("Failed to delete " + resource.getFullPath());
		if (lastException != null) {
			throw lastException;
		}
	}
	
	/**
	 * Returns true if this delta is flagged as having changed children.
	 */
	protected static boolean deltaChildrenChanged(IRodinElementDelta delta) {
		return delta.getKind() == IRodinElementDelta.CHANGED &&
			(delta.getFlags() & IRodinElementDelta.F_CHILDREN) != 0;
	}
	
	/**
	 * Returns true if this delta is flagged as having moved from a location
	 */
	protected static boolean deltaMovedFrom(IRodinElementDelta delta) {
		return delta.getKind() == IRodinElementDelta.ADDED &&
			(delta.getFlags() & IRodinElementDelta.F_MOVED_FROM) != 0;
	}
	
	/**
	 * Returns true if this delta is flagged as having moved to a location
	 */
	protected static boolean deltaMovedTo(IRodinElementDelta delta) {
		return delta.getKind() == IRodinElementDelta.REMOVED &&
			(delta.getFlags() & IRodinElementDelta.F_MOVED_TO) != 0;
	}
	
	/**
	 * Ensure that the positioned element is in the correct position within the parent.
	 */
	public static void ensureCorrectPositioning(IParent container, IRodinElement sibling, IRodinElement positioned) throws RodinDBException {
		IRodinElement[] children = container.getChildren();
		if (sibling != null) {
			// find the sibling
			boolean found = false;
			for (int i = 0; i < children.length; i++) {
				if (children[i].equals(sibling)) {
					assertTrue("element should be before sibling", i > 0 && children[i - 1].equals(positioned));
					found = true;
					break;
				}
			}
			assertTrue("Did not find sibling", found);
		}
	}
	
	protected static IRodinFile getRodinFile(String path) {
		return RodinCore.valueOf(getFile(path));
	}
	
	/**
	 * Returns the specified Rodin file in the given project.
	 */
	public static IRodinFile getRodinFile(IRodinProject project, String fileName)
			throws RodinDBException {
		return project.getRodinFile(fileName);
	}
	
	/**
	 * Returns the specified Rodin file in the given project.
	 */
	public static IRodinFile getRodinFile(String projectName, String fileName) throws RodinDBException {
		return getRodinProject(projectName).getRodinFile(fileName);
	}
	
	/**
	 * Returns the Rodin files in the given project.
	 */
	public static IRodinFile[] getRodinFiles(String projectName) throws RodinDBException {
		return getRodinProject(projectName).getRodinFiles();
	}
	
	protected static IRodinFile getRodinFileFor(IRodinElement element) {
		return (IRodinFile) element.getOpenable();
	}

	/**
	 * Returns the last delta for the given element from the cached delta.
	 */
	protected IRodinElementDelta getDeltaFor(IRodinElement element) {
		return getDeltaFor(element, false);
	}
	
	/**
	 * Returns the delta for the given element from the cached delta.
	 * If the boolean is true returns the first delta found.
	 */
	protected IRodinElementDelta getDeltaFor(IRodinElement element, boolean returnFirst) {
		IRodinElementDelta[] deltas = this.deltaListener.deltas;
		if (deltas == null) return null;
		IRodinElementDelta result = null;
		for (int i = 0; i < deltas.length; i++) {
			IRodinElementDelta delta = searchForDelta(element, this.deltaListener.deltas[i]);
			if (delta != null) {
				if (returnFirst) {
					return delta;
				}
				result = delta;
			}
		}
		return result;
	}

	protected static IFile getFile(String path) {
		return getWorkspaceRoot().getFile(new Path(path));
	}

	protected static IFolder getFolder(IPath path) {
		return getWorkspaceRoot().getFolder(path);
	}
	
	protected static NamedElement getNamedElement(IInternalElement parent, String name) {
		return parent.getInternalElement(NamedElement.ELEMENT_TYPE, name);
	}

	protected static NamedElement2 getNamedElement2(IInternalElement parent, String name) {
		return parent.getInternalElement(NamedElement2.ELEMENT_TYPE, name);
	}

	/**
	 * Returns the Rodin Model this test suite is running on.
	 */
	public static IRodinDB getRodinDB() {
		return RodinCore.getRodinDB();
	}
	
	/**
	 * Returns the Rodin Project with the given name in this test
	 * suite's database. This is a convenience method.
	 */
	public static IRodinProject getRodinProject(String name) {
		IProject project = getProject(name);
		return RodinCore.valueOf(project);
	}
	
	protected static IProject getProject(String project) {
		return getWorkspaceRoot().getProject(project);
	}

//	public ICompilationUnit getWorkingCopy(String path, boolean computeProblems) throws RodinDBException {
//		return getWorkingCopy(path, "", computeProblems);
//	}	
//	public ICompilationUnit getWorkingCopy(String path, String source) throws RodinDBException {
//		return getWorkingCopy(path, source, new WorkingCopyOwner() {}, null/*don't compute problems*/);
//	}	
//	public ICompilationUnit getWorkingCopy(String path, String source, boolean computeProblems) throws RodinDBException {
//		return getWorkingCopy(path, source, new WorkingCopyOwner() {}, computeProblems);
//	}
//	public ICompilationUnit getWorkingCopy(String path, String source, WorkingCopyOwner owner, boolean computeProblems) throws RodinDBException {
//		IProblemRequestor problemRequestor = computeProblems
//			? new IProblemRequestor() {
//				public void acceptProblem(IProblem problem) {}
//				public void beginReporting() {}
//				public void endReporting() {}
//				public boolean isActive() {
//					return true;
//				}
//			} 
//			: null;
//		return getWorkingCopy(path, source, owner, problemRequestor);
//	}
//	public ICompilationUnit getWorkingCopy(String path, String source, WorkingCopyOwner owner, IProblemRequestor problemRequestor) throws RodinDBException {
//		ICompilationUnit workingCopy = getCompilationUnit(path).getWorkingCopy(owner, problemRequestor, null/*no progress monitor*/);
//		workingCopy.getBuffer().setContents(source);
//		workingCopy.makeConsistent(null/*no progress monitor*/);
//		return workingCopy;
//	}

	/**
	 * Returns the IWorkspace this test suite is running on.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	public static IWorkspaceRoot getWorkspaceRoot() {
		return getWorkspace().getRoot();
	}
	
//	protected void discardWorkingCopies(ICompilationUnit[] units) throws RodinDBException {
//		if (units == null) return;
//		for (int i = 0, length = units.length; i < length; i++)
//			if (units[i] != null)
//				units[i].discardWorkingCopy();
//	}
	
	protected static String displayString(String toPrint, int indent) {
    	return org.rodinp.core.tests.util.Util.displayString(toPrint, indent);
    }

	public static byte[] read(java.io.File file) throws java.io.IOException {
		int fileLength;
		byte[] fileBytes = new byte[fileLength = (int) file.length()];
		java.io.FileInputStream stream = new java.io.FileInputStream(file);
		int bytesRead = 0;
		int lastReadSize = 0;
		while ((lastReadSize != -1) && (bytesRead != fileLength)) {
			lastReadSize = stream.read(fileBytes, bytesRead, fileLength - bytesRead);
			bytesRead += lastReadSize;
		}
		stream.close();
		return fileBytes;
	}
	
	protected static void removeRodinNature(String projectName) throws CoreException {
		IProject project = getProject(projectName);
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] {});
		project.setDescription(description, null);
	}
	
	/**
	 * Returns a delta for the given element in the delta tree
	 */
	protected static IRodinElementDelta searchForDelta(IRodinElement element, IRodinElementDelta delta) {
	
		if (delta == null) {
			return null;
		}
		if (delta.getElement().equals(element)) {
			return delta;
		}
		for (int i= 0; i < delta.getAffectedChildren().length; i++) {
			IRodinElementDelta child= searchForDelta(element, delta.getAffectedChildren()[i]);
			if (child != null) {
				return child;
			}
		}
		return null;
	}
	
//	protected void search(IRodinElement element, int limitTo, IRodinSearchScope scope, SearchRequestor requestor) throws CoreException {
//		search(element, limitTo, SearchPattern.R_EXACT_MATCH|SearchPattern.R_CASE_SENSITIVE, scope, requestor);
//	}
//	protected void search(IRodinElement element, int limitTo, int matchRule, IRodinSearchScope scope, SearchRequestor requestor) throws CoreException {
//		SearchPattern pattern = SearchPattern.createPattern(element, limitTo, matchRule);
//		assertNotNull("Pattern should not be null", pattern);
//		new SearchEngine().search(
//			pattern,
//			new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()},
//			scope,
//			requestor,
//			null
//		);
//	}
//	protected void search(String patternString, int searchFor, int limitTo, IRodinSearchScope scope, SearchRequestor requestor) throws CoreException {
//		search(patternString, searchFor, limitTo, SearchPattern.R_EXACT_MATCH|SearchPattern.R_CASE_SENSITIVE, scope, requestor);
//	}
//	protected void search(String patternString, int searchFor, int limitTo, int matchRule, IRodinSearchScope scope, SearchRequestor requestor) throws CoreException {
//		if (patternString.indexOf('*') != -1 || patternString.indexOf('?') != -1)
//			matchRule |= SearchPattern.R_PATTERN_MATCH;
//		SearchPattern pattern = SearchPattern.createPattern(
//			patternString, 
//			searchFor,
//			limitTo, 
//			matchRule);
//		assertNotNull("Pattern should not be null", pattern);
//		new SearchEngine().search(
//			pattern,
//			new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()},
//			scope,
//			requestor,
//			null);
//	}


	/* ************
	 * Suite set-ups *
	 *************/
	protected void setUp() throws Exception {
		super.setUp();
		
		// ensure autobuilding is turned off
		IWorkspaceDescription description = getWorkspace().getDescription();
		if (description.isAutoBuilding()) {
			description.setAutoBuilding(false);
			getWorkspace().setDescription(description);
		}
		
		// disable indexer
		DebugHelpers.disableIndexing();
	}
	
	protected static void sortElements(IRodinElement[] elements) {
		Comparator<IRodinElement> comparer = new Comparator<IRodinElement>() {
			public int compare(IRodinElement a, IRodinElement b) {
	    		String idA = ((RodinElement) a).toStringWithAncestors();
	    		String idB = ((RodinElement) b).toStringWithAncestors();
				return idA.compareTo(idB);
			}
		};
		Arrays.sort(elements, comparer);
	}
	
	protected static void sortResources(IResource[] resources) {
		Comparator<IResource> comparer = new Comparator<IResource>() {
			public int compare(IResource a, IResource b) {
				return a.getName().compareTo(b.getName());
			}
		};
		Arrays.sort(resources, comparer);
	}
	
//	/*
//	 * Simulate a save/exit of the workspace
//	 */
//	protected void simulateExit() throws CoreException {
//		waitForAutoBuild();
//		getWorkspace().save(true/*full save*/, null/*no progress*/);
//		RodinDBManager.getRodinDBManager().shutdown();
//	}
//
//	/*
//	 * Simulate a save/exit/restart of the workspace
//	 */
//	protected void simulateExitRestart() throws CoreException {
//		simulateExit();
//		simulateRestart();
//	}
//	/*
//	 * Simulate a restart of the workspace
//	 */
//	protected void simulateRestart() throws CoreException {
//		RodinDBManager.doNotUse(); // reset the MANAGER singleton
//		RodinDBManager.getRodinDBManager().startup();
//		new RodinCorePreferenceInitializer().initializeDefaultPreferences();
//	}

	/**
	 * Starts listening to element deltas, and queues them in fgDeltas.
	 */
	public void startDeltas() {
		clearDeltas();
		RodinCore.addElementChangedListener(this.deltaListener);
	}
	
	/**
	 * Stops listening to element deltas, and clears the current deltas.
	 */
	public void stopDeltas() {
		RodinCore.removeElementChangedListener(this.deltaListener);
		clearDeltas();
	}
	
	protected static IPath[] toIPathArray(String[] paths) {
		if (paths == null) return null;
		int length = paths.length;
		IPath[] result = new IPath[length];
		for (int i = 0; i < length; i++) {
			result[i] = new Path(paths[i]);
		}
		return result;
	}
	
	protected String toString(String[] strings) {
		return toString(strings, false/*don't add extra new line*/);
	}
	
	protected static String toString(String[] strings, boolean addExtraNewLine) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, length = strings.length; i < length; i++){
			buffer.append(strings[i]);
			if (addExtraNewLine || i < length - 1)
				buffer.append("\n");
		}
		return buffer.toString();
	}

	protected static void assertError(IWorkspaceRunnable runnable,
			int expectedCode) throws CoreException {
		assertErrorFor(runnable, expectedCode, null);
	}

	protected static void assertErrorFor(IWorkspaceRunnable runnable,
			int expectedCode, IRodinElement element) throws CoreException {
		try {
			runnable.run(null);
			fail("Should have raised an exception");
		} catch (RodinDBException e) {
			assertErrorFor(e, expectedCode, element);
		}
	}

	protected static void assertError(RodinDBException exception,
			int expectedCode) {
		assertErrorFor(exception, expectedCode, null);
	}

	protected static void assertErrorFor(RodinDBException exception,
			int expectedCode, IRodinElement element) {

		final IRodinDBStatus status = exception.getRodinDBStatus();
		assertEquals("Status should be an error",
				IRodinDBStatus.ERROR,
				status.getSeverity());
		assertEquals("Unexpected status code", 
				expectedCode, 
				status.getCode());
		IRodinElement[] elements = status.getElements(); 
		if (element == null) {
			assertEquals("Status should have no related element", 0, elements.length);
		} else {
			assertEquals("Status should be related to the given element", 
					1, 
					elements.length);
			assertEquals("Status should be related to the given element", 
					element, 
					elements[0]);
		}
	}

	protected static void assertNameCollisionErrorFor(RodinDBException exception,
			IRodinElement element) {
		assertErrorFor(exception,  
				IRodinDBStatusConstants.NAME_COLLISION, 
				element
		);
	}

	protected static void assertReadOnlyErrorFor(RodinDBException exception,
			IRodinElement element) {
		assertErrorFor(exception,  
				IRodinDBStatusConstants.READ_ONLY, 
				element
		);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

//	public static void waitUntilIndexesReady() {
//		// dummy query for waiting until the indexes are ready
//		SearchEngine engine = new SearchEngine();
//		IRodinSearchScope scope = SearchEngine.createWorkspaceScope();
//		try {
//			engine.searchAllTypeNames(
//				null,
//				"!@$#!@".toCharArray(),
//				SearchPattern.R_PATTERN_MATCH | SearchPattern.R_CASE_SENSITIVE,
//				IRodinSearchConstants.CLASS,
//				scope, 
//				new TypeNameRequestor() {
//					public void acceptType(
//						int modifiers,
//						char[] packageName,
//						char[] simpleTypeName,
//						char[][] enclosingTypeNames,
//						String path) {}
//				},
//				IRodinSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
//				null);
//		} catch (CoreException e) {
//		}
//	}

}
