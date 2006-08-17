/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.core.tests.model.ModifyingResourceTests.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.tests.basis.NamedElement;

/**
 * These test ensure that modifications in Rodin projects are correctly reported as
 * IRodinElementDeltas.
 */
public class RodinElementDeltaTests extends ModifyingResourceTests {
	
	public class DeltaListener implements IElementChangedListener {
		ArrayList<IRodinElementDelta> deltas;
		int eventType;
		
		public DeltaListener() {
			DeltaListener.this.deltas = new ArrayList<IRodinElementDelta>();
			DeltaListener.this.eventType = -1;
		}
		public DeltaListener(int eventType) {
			DeltaListener.this.deltas = new ArrayList<IRodinElementDelta>();
			DeltaListener.this.eventType = eventType;
		}
		public void elementChanged(ElementChangedEvent event) {
			if (DeltaListener.this.eventType == -1 || event.getType() == DeltaListener.this.eventType) {
				DeltaListener.this.deltas.add(event.getDelta());
			}
		}
		public void flush() {
			DeltaListener.this.deltas = new ArrayList<IRodinElementDelta>();
		}
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0, length = DeltaListener.this.deltas.size(); i < length; i++) {
				IRodinElementDelta delta = this.deltas.get(i);
				IRodinElementDelta[] children = delta.getAffectedChildren();
				if (children.length > 0) {
					for (int j=0, childrenLength=children.length; j<childrenLength; j++) {
						buffer.append(children[j]);
						if (j != childrenLength-1) {
							buffer.append("\n");
						}
					}
				} else {
					buffer.append(delta);
				}
				if (i != length-1) {
					buffer.append("\n\n");
				}
			}
			return buffer.toString();
		}
	}
	
//	public static Test suite() {
//		TestSuite suite = new Suite(RodinElementDeltaTests.class.getName());
//		
//		// add/remove/open/close projects
//		suite.addTest(new RodinElementDeltaTests("testAddRodinProject"));
//		suite.addTest(new RodinElementDeltaTests("testRemoveRodinProject"));
//		suite.addTest(new RodinElementDeltaTests("testRemoveAddRodinProject"));
//		suite.addTest(new RodinElementDeltaTests("testRemoveAddBinaryProject"));
//		suite.addTest(new RodinElementDeltaTests("testAddRodinNature"));
//		suite.addTest(new RodinElementDeltaTests("testAddRodinNatureAndClasspath"));
//		suite.addTest(new RodinElementDeltaTests("testRemoveRodinNature"));
//		suite.addTest(new RodinElementDeltaTests("testOpenRodinProject"));
//		suite.addTest(new RodinElementDeltaTests("testCloseRodinProject"));
//		suite.addTest(new RodinElementDeltaTests("testAddTwoRodinProjects"));
//		suite.addTest(new RodinElementDeltaTests("testAddTwoRodinProjectsWithExtraSetClasspath"));
//		suite.addTest(new RodinElementDeltaTests("testDeleteProjectSetCPAnotherProject"));
//		
//		suite.addTest(new RodinElementDeltaTests("testRenameRodinProject"));
//		
//		// non-Rodin projects
//		suite.addTest(new RodinElementDeltaTests("testAddNonRodinProject"));
//		suite.addTest(new RodinElementDeltaTests("testRemoveNonRodinProject"));
//		suite.addTest(new RodinElementDeltaTests("testRemoveNonRodinProjectUpdateDependent1"));
//		suite.addTest(new RodinElementDeltaTests("testRemoveNonRodinProjectUpdateDependent2"));
//		suite.addTest(new RodinElementDeltaTests("testRemoveNonRodinProjectUpdateDependent3"));
//		suite.addTest(new RodinElementDeltaTests("testOpenNonRodinProject"));
//		suite.addTest(new RodinElementDeltaTests("testCloseNonRodinProject"));
//		suite.addTest(new RodinElementDeltaTests("testCloseNonRodinProjectUpdateDependent"));
//		suite.addTest(new RodinElementDeltaTests("testRenameNonRodinProject"));
//		
//		// package fragment roots
//		suite.addTest(new RodinElementDeltaTests("testDeleteInnerJar"));
//		suite.addTest(new RodinElementDeltaTests("testNestedRootParentMove"));
//		suite.addTest(new RodinElementDeltaTests("testPackageFragmentRootRemoveAndAdd"));
//		
//		// packages
//		suite.addTest(new RodinElementDeltaTests("testAddPackageSourceIsBin"));
//		suite.addTest(new RodinElementDeltaTests("testRenameOuterPkgFragment"));
//		suite.addTest(new RodinElementDeltaTests("testPackageFragmentAddAndRemove"));
//		suite.addTest(new RodinElementDeltaTests("testPackageFragmentMove"));
//		suite.addTest(new RodinElementDeltaTests("testCopyAndOverwritePackage"));
//		
//		// compilation units
//		suite.addTest(new RodinElementDeltaTests("testAddCuInDefaultPkg1"));
//		suite.addTest(new RodinElementDeltaTests("testAddCuInDefaultPkg2"));
//		suite.addTest(new RodinElementDeltaTests("testMoveCuInEnclosingPkg"));
//		suite.addTest(new RodinElementDeltaTests("testCompilationUnitRemoveAndAdd"));
//		suite.addTest(new RodinElementDeltaTests("testAddCuAfterProjectOpen"));
//		
//		// commit/save working copies
//		suite.addTest(new RodinElementDeltaTests("testModifyMethodBodyAndSave"));
//		suite.addTest(new RodinElementDeltaTests("testRenameMethodAndSave"));
//		suite.addTest(new RodinElementDeltaTests("testSaveWorkingCopy"));
//		suite.addTest(new RodinElementDeltaTests("testWorkingCopyCommit"));
////		suite.addTest(new RodinElementDeltaTests("testAddCommentAndCommit"));
//		
//		// managed working copies
//		suite.addTest(new RodinElementDeltaTests("testCreateWorkingCopy"));
//		suite.addTest(new RodinElementDeltaTests("testDestroyWorkingCopy"));
//		suite.addTest(new RodinElementDeltaTests("testCreateSharedWorkingCopy"));
//		suite.addTest(new RodinElementDeltaTests("testDestroySharedWorkingCopy"));
//		
//		// non-Rodin resources
//		suite.addTest(new RodinElementDeltaTests("testMoveResInDotNamedFolder"));
//		suite.addTest(new RodinElementDeltaTests("testMoveTwoResInRoot"));
//		suite.addTest(new RodinElementDeltaTests("testMergeResourceDeltas"));
//		suite.addTest(new RodinElementDeltaTests("testAddFileToNonRodinProject"));
//		suite.addTest(new RodinElementDeltaTests("testDeleteNonRodinFolder"));
//		suite.addTest(new RodinElementDeltaTests("testAddInvalidSubfolder"));
//		suite.addTest(new RodinElementDeltaTests("testCUNotOnClasspath"));
//		suite.addTest(new RodinElementDeltaTests("testNonRodinResourceRemoveAndAdd"));
//		
//		// listeners
////		suite.addTest(new RodinElementDeltaTests("testListenerAutoBuild"));
//		suite.addTest(new RodinElementDeltaTests("testListenerReconcile"));
//		suite.addTest(new RodinElementDeltaTests("testListenerPostChange"));
//		
//		// classpath
//		suite.addTest(new RodinElementDeltaTests("testSetClasspathVariable1"));
//		suite.addTest(new RodinElementDeltaTests("testSetClasspathVariable2"));
//		suite.addTest(new RodinElementDeltaTests("testChangeRootKind"));
//		suite.addTest(new RodinElementDeltaTests("testOverwriteClasspath"));
//		suite.addTest(new RodinElementDeltaTests("testRemoveCPEntryAndRoot1"));
//		suite.addTest(new RodinElementDeltaTests("testRemoveCPEntryAndRoot2"));
//		suite.addTest(new RodinElementDeltaTests("testRemoveCPEntryAndRoot3"));
//		suite.addTest(new RodinElementDeltaTests("testAddDotClasspathFile"));
//		suite.addTest(new RodinElementDeltaTests("testSetClasspathOnFreshProject"));
//		suite.addTest(new RodinElementDeltaTests("testChangeExportFlag"));
//		
//		// batch operations
//		suite.addTest(new RodinElementDeltaTests("testBatchOperation"));
//		suite.addTest(new RodinElementDeltaTests("testModifyProjectDescriptionAndRemoveFolder"));
//		
//		// build
//		suite.addTest(new RodinElementDeltaTests("testBuildProjectUsedAsLib"));
//		
//		// output locations
//		suite.addTest(new RodinElementDeltaTests("testModifyOutputLocation1"));
//		suite.addTest(new RodinElementDeltaTests("testModifyOutputLocation2"));
//		suite.addTest(new RodinElementDeltaTests("testModifyOutputLocation3"));
//		suite.addTest(new RodinElementDeltaTests("testModifyOutputLocation4"));
//		suite.addTest(new RodinElementDeltaTests("testChangeCustomOutput"));
//		
//		return suite;
//	}
	
	public RodinElementDeltaTests(String name) {
		super(name);
	}
	
	/**
	 * Add file in project.
	 */
	public void testAddFile() throws CoreException {
		try {
			createRodinProject("P");
			startDeltas();
			createRodinFile("P/X.test");
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {CHILDREN}\n" +
					"	X.test[+]: {}"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Add file after opening its project
	 */
	public void testAddFileAfterProjectOpen() throws CoreException {
		try {
			createRodinProject("P1");
			IRodinProject p2 = createRodinProject("P2");
			createRodinFile("P2/X.test");
			IProject project = p2.getProject();
			project.close(null);
			
			// open project
			project.open(null);
			
			startDeltas();
			createRodinFile("P2/Y.test");
			assertDeltas(
					"Unexpected delta", 
					"P2[*]: {CHILDREN}\n" +
					"	Y.test[+]: {}"
			);
		} finally {
			stopDeltas();
			deleteProject("P1");
			deleteProject("P2");
		}
	}

	/**
	 * Ensure that a resource delta is fired when a file is added to a non-Rodin project.
	 */
	public void testAddFileToNonRodinProject() throws CoreException {
		try {
			createProject("P");
			startDeltas();
			createFile("/P/toto.txt", "");
			assertDeltas(
					"Unexpected delta", 
					"ResourceDelta(/P)"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Add the Rodin nature to an existing project.
	 */
	public void testAddRodinNature() throws CoreException {
		try {
			createProject("P");
			startDeltas();
			addRodinNature("P");
			assertDeltas(
					"Unexpected delta", 
					"P[+]: {}\n" + 
					"ResourceDelta(/P)"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Add a Rodin project.
	 */
	public void testAddRodinProject() throws CoreException {
		try {
			startDeltas();
			createRodinProject("P");
			assertDeltas(
					"Unexpected delta", 
					"P[+]: {}"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Add a non-Rodin project.
	 */
	public void testAddNonRodinProject() throws CoreException {
		try {
			startDeltas();
			createProject("P");
			assertDeltas(
					"Should get a non-Rodin resource delta", 
					"ResourceDelta(/P)"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Add a (non-Rodin) folder.
	 */
	public void testAddFolder() throws CoreException {
		try {
			createRodinProject("P");
			startDeltas();
			createFolder("P/x");
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {CONTENT}\n" + 
					"	ResourceDelta(/P/x)[+]"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Add a (non-Rodin) folder inside another folder.
	 */
	public void testAddFolder2() throws CoreException {
		try {
			createRodinProject("P");
			createFolder("P/x");

			startDeltas();
			createFolder("P/x/y");
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {CONTENT}\n" + 
					"	ResourceDelta(/P/x)[*]"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Add two Rodin projects in an IWorkspaceRunnable.
	 */
	public void testAddTwoRodinProjects() throws CoreException {
		try {
			startDeltas();
			ResourcesPlugin.getWorkspace().run(
					new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							createRodinProject("P1");
							createRodinProject("P2");
						}
					},
					null);
			assertEquals(
					"Unexpected delta", 
					"P1[+]: {}\n" +
					"P2[+]: {}", 
					getSortedByProjectDeltas());
		} finally {
			stopDeltas();
			deleteProject("P1");
			deleteProject("P2");
		}
	}
	
	/**
	 * Batch operation test.
	 */
	public void testBatchOperation() throws CoreException {
		try {
			createRodinProject("P");
			createRodinFile("P/A.test");
			startDeltas();
			RodinCore.run(
					new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							IRodinFile rf = getRodinFile("P/A.test");
							NamedElement foo = createNEPositive(rf, "foo", null);
							NamedElement bar = createNEPositive(rf, "bar", foo);
							createNEPositive(bar, "baz", null);
						}
					},
					null);
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {CHILDREN}\n" + 
					"	A.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][+]: {}\n" + 
					"		bar[org.rodinp.core.tests.namedElement][+]: {}"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Close a Rodin project.
	 */
	public void testCloseRodinProject() throws CoreException {
		try {
			createRodinProject("P");
			IProject project = getProject("P");
			startDeltas();
			project.close(null);
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {CLOSED}\n" + 
					"ResourceDelta(/P)"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Close a non-Rodin project.
	 */
	public void testCloseNonRodinProject() throws CoreException {
		try {
			createProject("P");
			IProject project = getProject("P");
			startDeltas();
			project.close(null);
			assertDeltas(
					"Unexpected delta", 
					"ResourceDelta(/P)"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Test that deltas are generated when a file is added
	 * and removed from a project via core API.
	 */
	public void testRodinFileRemoveAndAdd() throws CoreException {
		try {
			createRodinProject("P");
			IFile file = createRodinFile("/P/X.test").getResource();
			
			// delete file
			startDeltas();
			deleteResource(file);
			assertDeltas(
					"Unexpected delta after deleting /P/p/X.test",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[-]: {}"
			);
			
			// add file
			clearDeltas();
			createRodinFile("/P/X.test");
			assertDeltas(
					"Unexpected delta after adding /P/p/X.test",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[+]: {}"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
//	public void testCreateSharedWorkingCopy() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			createRodinProject("P", new String[] {""}, "");
//			createFile("P/X.test",
//					"public class X {\n" +
//			"}");
//			ICompilationUnit unit = getCompilationUnit("P", "", "", "X.test");
//			startDeltas();
//			copy = unit.getWorkingCopy(new WorkingCopyOwner() {}, null, null);
//			assertDeltas(
//					"Unexpected delta", 
//					"P[*]: {CHILDREN}\n" +
//					"	<project root>[*]: {CHILDREN}\n" +
//					"		<default>[*]: {CHILDREN}\n" +
//					"			[Working copy] X.test[+]: {}"
//			);
//		} finally {
//			stopDeltas();
//			if (copy != null) copy.discardWorkingCopy();
//			deleteProject("P");
//		}
//	}
//	public void testCreateWorkingCopy() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			createRodinProject("P", new String[] {""}, "");
//			createFile("P/X.test",
//					"public class X {\n" +
//			"}");
//			ICompilationUnit unit = getCompilationUnit("P", "", "", "X.test");
//			startDeltas();
//			copy = unit.getWorkingCopy(null);
//			assertDeltas(
//					"Unexpected delta", 
//					"P[*]: {CHILDREN}\n" +
//					"	<project root>[*]: {CHILDREN}\n" +
//					"		<default>[*]: {CHILDREN}\n" +
//					"			[Working copy] X.test[+]: {}"
//			);
//		} finally {
//			stopDeltas();
//			if (copy != null) copy.discardWorkingCopy();
//			deleteProject("P");
//		}
//	}

//	public void testDestroySharedWorkingCopy() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			createRodinProject("P", new String[] {""}, "");
//			createFile("P/X.test",
//					"public class X {\n" +
//			"}");
//			ICompilationUnit unit = getCompilationUnit("P", "", "", "X.test");
//			copy = unit.getWorkingCopy(new WorkingCopyOwner() {}, null, null);
//			startDeltas();
//			copy.discardWorkingCopy();
//			assertDeltas(
//					"Unexpected delta", 
//					"P[*]: {CHILDREN}\n" +
//					"	<project root>[*]: {CHILDREN}\n" +
//					"		<default>[*]: {CHILDREN}\n" +
//					"			[Working copy] X.test[-]: {}"
//			);
//		} finally {
//			stopDeltas();
//			deleteProject("P");
//		}
//	}
//	public void testDestroyWorkingCopy() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			createRodinProject("P", new String[] {""}, "");
//			createFile("P/X.test",
//					"public class X {\n" +
//			"}");
//			ICompilationUnit unit = getCompilationUnit("P", "", "", "X.test");
//			copy = unit.getWorkingCopy(null);
//			startDeltas();
//			copy.discardWorkingCopy();
//			assertDeltas(
//					"Unexpected delta", 
//					"P[*]: {CHILDREN}\n" +
//					"	<project root>[*]: {CHILDREN}\n" +
//					"		<default>[*]: {CHILDREN}\n" +
//					"			[Working copy] X.test[-]: {}"
//			);
//		} finally {
//			stopDeltas();
//			deleteProject("P");
//		}
//	}

	/*
	 * Ensures that a delta listener that asks for POST_CHANGE events gets those events 
	 * and no others.
	 */
//	public void testListenerPostChange() throws CoreException {
//		DeltaListener listener = new DeltaListener(ElementChangedEvent.POST_CHANGE);
//		IRodinFile wc = null;
//		try {
//			IRodinProject project = createRodinProject("P");
//			RodinCore.addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE);
//			
//			// file creation
//			IRodinFile file = project.createRodinFile(
//					"X.test",
//					false,
//					null);
//			assertEquals(
//					"Unexpected delta after creating CU", 
//					"P[*]: {CHILDREN}\n" +
//					"	X.test[+]: {}", 
//					listener.toString());
//			listener.flush();
//			
//			// type creation
//			file.createType(
//					"class A {\n" +
//					"}",
//					file.getType("X"),
//					false,
//					null);
//			assertEquals(
//					"Unexpected delta after creating type", 
//					"P[*]: {CHILDREN}\n" +
//					"	<project root>[*]: {CHILDREN}\n" +
//					"		<default>[*]: {CHILDREN}\n" +
//					"			X.test[*]: {CHILDREN | PRIMARY RESOURCE}\n" +
//					"				A[+]: {}", 
//					listener.toString());
//			listener.flush();
//			
//			// non-java resource creation
//			createFile("P/readme.txt", "");
//			assertEquals(
//					"Unexpected delta after creating non-java resource",
//					"P[*]: {CONTENT}\n" +
//					"	ResourceDelta(/P/readme.txt)[+]",
//					listener.toString());
//			listener.flush();
//			
//			// shared working copy creation
//			wc = file.getWorkingCopy(new WorkingCopyOwner() {}, null, null);
//			assertEquals(
//					"Unexpected delta after creating shared working copy",
//					"P[*]: {CHILDREN}\n" +
//					"	<project root>[*]: {CHILDREN}\n" +
//					"		<default>[*]: {CHILDREN}\n" +
//					"			[Working copy] X.test[+]: {}",
//					listener.toString());
//			listener.flush();
//			
//			// reconcile
//			wc.getBuffer().setContents(
//					"public class X {\n" +
//					"  public void foo() {\n" +
//					"  }\n" +
//			"}");
//			wc.reconcile(ICompilationUnit.NO_AST, false, null, null);
//			assertEquals(
//					"Unexpected delta after reconciling working copy",
//					"",
//					listener.toString());
//			listener.flush();
//			
//			// commit
//			wc.commitWorkingCopy(false, null);
//			assertEquals(
//					"Unexpected delta after committing working copy",
//					"P[*]: {CHILDREN}\n" +
//					"	<project root>[*]: {CHILDREN}\n" +
//					"		<default>[*]: {CHILDREN}\n" +
//					"			X.test[*]: {CHILDREN | PRIMARY RESOURCE}\n" +
//					"				X[*]: {CHILDREN}\n" +
//					"					foo()[+]: {}\n" +
//					"				A[-]: {}",
//					listener.toString());
//			listener.flush();
//			
//			// shared working copy destruction
//			wc.discardWorkingCopy();
//			assertEquals(
//					"Unexpected delta after destroying shared working copy",
//					"P[*]: {CHILDREN}\n" +
//					"	<project root>[*]: {CHILDREN}\n" +
//					"		<default>[*]: {CHILDREN}\n" +
//					"			[Working copy] X.test[-]: {}",
//					listener.toString());
//			listener.flush();
//			wc = null;
//			
//			
//		} finally {
//			if (wc != null) wc.discardWorkingCopy();
//			RodinCore.removeElementChangedListener(listener);
//			deleteProject("P");
//		}
//	}

	/**
	 * Ensures that merging a Rodin delta with another one that contains a resource delta
	 * results in a Rodin delta with the resource delta.
	 * (regression test for 11210 ResourceDeltas are lost when merging deltas)
	 */
	public void testMergeResourceDeltas() throws CoreException {
		try {
			final IRodinProject project = createRodinProject("P");
			startDeltas();
			ResourcesPlugin.getWorkspace().run(
					new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							// an operation that creates a Rodin delta without firing it
							project.createRodinFile(
									"X.test",
									true,
									null);
							
							// an operation that generates a non Rodin resource delta
							createFile("P/Y.txt", "");
						}
					},
					null
			);
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {CHILDREN | CONTENT}\n" +
					"	X.test[+]: {}\n" +
					"	ResourceDelta(/P/Y.txt)[+]"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}

	public void testModifyContents() throws CoreException {
		try {
			createRodinProject("P");
			IRodinFile rodinFile = createRodinFile("P/A.test");
			NamedElement ne = createNEPositive(rodinFile, "foo", null);
			
			startDeltas();
			assertContentsChanged(ne, "bar");
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {CHILDREN}\n" +
					"	A.test[*]: {CHILDREN}\n" +
					"		foo[org.rodinp.core.tests.namedElement][*]: {CONTENT}"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}

	public void testModifyContentsAndSave() throws CoreException {
		try {
			createRodinProject("P");
			IRodinFile rodinFile = createRodinFile("P/A.test");
			NamedElement ne = createNEPositive(rodinFile, "foo", null);
			
			startDeltas();
			assertContentsChanged(ne, "bar");
			rodinFile.save(null, false);
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {CHILDREN}\n" +
					"	A.test[*]: {CHILDREN}\n" +
					"		foo[org.rodinp.core.tests.namedElement][*]: {CONTENT}"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}

	public void testModifyContentsNoSave() throws CoreException {
		try {
			createRodinProject("P");
			IRodinFile rodinFile = createRodinFile("P/A.test");
			NamedElement ne = createNEPositive(rodinFile, "foo", null);
			
			startDeltas();
			assertContentsChanged(ne, "bar");
			rodinFile.close();
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {CHILDREN}\n" +
					"	A.test[*]: {CHILDREN}\n" +
					"		foo[org.rodinp.core.tests.namedElement][*]: {CONTENT}"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}

	/**
	 * bug 18953
	 */
	public void testModifyProjectDescriptionAndRemoveFolder() throws CoreException {
		try {
			IRodinProject project = createRodinProject("P");
			final IProject projectFolder = project.getProject();
			final IFolder folder = createFolder("/P/folder");
			
			startDeltas();
			getWorkspace().run(
					new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							IProjectDescription desc = projectFolder.getDescription();
							desc.setComment("A comment");
							projectFolder.setDescription(desc, null);
							deleteResource(folder);
						}
					},
					null);
			
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {CONTENT}\n" + 
					"	ResourceDelta(/P/.project)[*]\n" + 
					"	ResourceDelta(/P/folder)[-]"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Move a non-Rodin file that is under a folder.
	 */
	public void testMoveResInFolder() throws CoreException {
		try {
			createRodinProject("P");
			IProject project = getProject("P");
			createFolder("P/x");
			IFile file = createFile("P/x/X.test", emptyBytes);
			
			assertElementDescendants("Unexpected project descendants",
					"P",
					getRodinProject("P")
			);
			startDeltas();
			file.move(project.getFullPath().append("/X.test"), true, null);
			assertElementDescendants("Unexpected project descendants",
					"P\n" + 
					"  X.test",
					getRodinProject("P")
			);
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {CHILDREN | CONTENT}\n" +
					"	X.test[+]: {}\n" +
					"	ResourceDelta(/P/x)[*]"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Test that deltas are generated when a non-Rodin file is
	 * removed and added
	 */
	public void testNonRodinResourceRemoveAndAdd() throws CoreException {
		try {
			createRodinProject("P");
			IFile file = createFile("/P/read.txt", "");
			
			startDeltas();
			deleteResource(file);
			assertDeltas(
					"Unexpected delta after deleting /P/src/read.txt",
					"P[*]: {CONTENT}\n" + 
					"	ResourceDelta(/P/read.txt)[-]"
			);
			
			clearDeltas();
			createFile("/P/read.txt", "");
			assertDeltas(
					"Unexpected delta after creating /P/src/read.txt",
					"P[*]: {CONTENT}\n" + 
					"	ResourceDelta(/P/read.txt)[+]"
			);
			
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	
	/**
	 * Open a Rodin project.
	 */
	public void testOpenRodinProject() throws CoreException {
		try {
			createRodinProject("P");
			IProject project = getProject("P");
			project.close(null);
			startDeltas();
			project.open(null);
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {OPENED}\n" + 
					"ResourceDelta(/P)"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Open a non-Rodin project.
	 */
	public void testOpenNonRodinProject() throws CoreException {
		try {
			createProject("P");
			IProject project = getProject("P");
			project.close(null);
			startDeltas();
			project.open(null);
			assertDeltas(
					"Unexpected delta", 
					"ResourceDelta(/P)"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}

	/**
	 * Remove then add a Rodin project (in a workspace runnable).
	 */
	public void testRemoveAddRodinProject() throws CoreException {
		try {
			createRodinProject("P");
			startDeltas();
			getWorkspace().run(
					new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							deleteProject("P");
							createRodinProject("P");
						}
					},
					null);
			assertDeltas(
					"Unexpected delta", 
					"P[*]: {CONTENT}\n" + 
					"	ResourceDelta(/P/.project)[*]"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}

	/**
	 * Remove the Rodin nature of an existing Rodin project.
	 */
	public void testRemoveRodinNature() throws CoreException {
		try {
			createRodinProject("P");
			startDeltas();
			removeRodinNature("P");
			assertDeltas(
					"Unexpected delta", 
					"P[-]: {}\n" + 
					"ResourceDelta(/P)"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
		}
	}
	
	/**
	 * Remove a Rodin project.
	 */
	public void testRemoveRodinProject() throws CoreException {
		try {
			createRodinProject("P");
			startDeltas();
			deleteProject("P");
			assertDeltas(
					"Unexpected delta", 
					"P[-]: {}"
			);
		} finally {
			stopDeltas();
		}
	}
	
	/**
	 * Remove a non-Rodin project.
	 */
	public void testRemoveNonRodinProject() throws CoreException {
		try {
			createProject("P");
			startDeltas();
			deleteProject("P");
			assertDeltas(
					"Should get a non-Rodin resource delta", 
					"ResourceDelta(/P)"
			);
		} finally {
			stopDeltas();
		}
	}
	
	/**
	 * Rename a Rodin project.
	 */
	public void testRenameRodinProject() throws CoreException {
		try {
			createRodinProject("P");
			startDeltas();
			renameProject("P", "P1");
			assertDeltas(
					"Unexpected delta", 
					"P[-]: {MOVED_TO(P1)}\n" +
					"P1[+]: {MOVED_FROM(P)}"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
			deleteProject("P1");
		}
	}
	
//	public void testRenameMethodAndSave() throws CoreException {
//		ICompilationUnit workingCopy = null;
//		try {
//			createRodinProject("P", new String[] {""}, "");
//			createFolder("P/x/y");
//			createFile("P/x/y/A.test",
//					"package x.y;\n" +
//					"public class A {\n" +
//					"  public void foo1() {\n" +
//					"  }\n" +
//			"}");
//			ICompilationUnit cu = getCompilationUnit("P/x/y/A.test"); 
//			workingCopy = cu.getWorkingCopy(null);
//			workingCopy.getBuffer().setContents(
//					"package x.y;\n" +
//					"public class A {\n" +
//					"  public void foo2() {\n" +
//					"  }\n" +
//			"}");
//			
//			startDeltas();
//			workingCopy.commitWorkingCopy(true, null);
//			assertDeltas(
//					"Unexpected delta", 
//					"P[*]: {CHILDREN}\n" + 
//					"	<project root>[*]: {CHILDREN}\n" + 
//					"		x.y[*]: {CHILDREN}\n" + 
//					"			A.test[*]: {CHILDREN | PRIMARY RESOURCE}\n" + 
//					"				A[*]: {CHILDREN}\n" + 
//					"					foo2()[+]: {}\n" + 
//					"					foo1()[-]: {}"
//			);
//		} finally {
//			stopDeltas();
//			if (workingCopy != null) {
//				workingCopy.discardWorkingCopy();
//			}
//			deleteProject("P");
//		}
//	}

	/**
	 * Rename a non-Rodin project.
	 */
	public void testRenameNonRodinProject() throws CoreException {
		try {
			createProject("P");
			startDeltas();
			renameProject("P", "P1");
			assertDeltas(
					"Unexpected delta", 
					"ResourceDelta(/P)\n" + 
					"ResourceDelta(/P1)"
			);
		} finally {
			stopDeltas();
			deleteProject("P");
			deleteProject("P1");
		}
	}
	
//	/**
//	 * Ensures that saving a working copy doesn't change the underlying resource.
//	 * (only commit should do so)
//	 */
//	public void testSaveWorkingCopy() throws CoreException {
//		ICompilationUnit copy = null;
//		try {
//			createRodinProject("P", new String[] {""}, "");
//			createFile("P/X.test",
//					"public class X {\n" +
//			"}");
//			ICompilationUnit unit = getCompilationUnit("P", "", "", "X.test");
//			copy = unit.getWorkingCopy(null);
//			copy.getType("X").createMethod("void foo() {}", null, true, null);
//			startDeltas();
//			copy.save(null, true);
//			assertDeltas(
//					"Unexpected delta after saving working copy", 
//					""
//			);
//			copy.commitWorkingCopy(true, null);
//			assertDeltas(
//					"Unexpected delta after committing working copy", 
//					"P[*]: {CHILDREN}\n" + 
//					"	<project root>[*]: {CHILDREN}\n" + 
//					"		<default>[*]: {CHILDREN}\n" + 
//					"			X.test[*]: {CHILDREN | PRIMARY RESOURCE}\n" + 
//					"				X[*]: {CHILDREN}\n" + 
//					"					foo()[+]: {}"
//			);
//		} finally {
//			stopDeltas();
//			if (copy != null) copy.discardWorkingCopy();
//			deleteProject("P");
//		}
//	}
	
//	/**
//	 * Ensures that committing a working copy fires a fine grained delta.
//	 */
//	public void testWorkingCopyCommit() throws CoreException {
//		try {
//			createRodinProject("P", new String[] {""}, "");
//			createFolder("P/x/y");
//			createFile("P/x/y/A.test", 
//					"package x.y;\n" +
//					"public class A {\n" +
//			"}");
//			ICompilationUnit cu = getCompilationUnit("P/x/y/A.test");
//			ICompilationUnit copy = cu.getWorkingCopy(null);
//			copy.getBuffer().setContents(
//					"package x.y;\n" +
//					"public class A {\n" +
//					"  public void foo() {\n" +
//					"  }\n" +
//			"}");
//			copy.save(null, false);
//			startDeltas();
//			copy.commitWorkingCopy(true, null);
//			assertDeltas(
//					"Unexpected delta after commit", 
//					"P[*]: {CHILDREN}\n" + 
//					"	<project root>[*]: {CHILDREN}\n" + 
//					"		x.y[*]: {CHILDREN}\n" + 
//					"			A.test[*]: {CHILDREN | PRIMARY RESOURCE}\n" + 
//					"				A[*]: {CHILDREN}\n" + 
//					"					foo()[+]: {}"
//			);
//		} finally {
//			stopDeltas();
//			deleteProject("P");
//		}
//	}
}
