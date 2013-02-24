/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPSRoot;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.EventBMachineEditor;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.tests.utils.EventBUITest;
import org.junit.After;
import org.junit.Test;
import org.rodinp.core.IRodinElement;

/**
 * Testing the EditorManager class, responsible of closing active and inactive
 * editors when :
 * <ul>
 * <li>components are renamed,</li>
 * <li>projects are :
 * <ul>
 * <li>closed</li>
 * <li>renamed</li>
 * <li>suppressed</li></li>
 * </ul>
 * 
 * @author Thomas Muller
 */
public class TestEditorManager extends EventBUITest {

	/**
	 * We re-open the project if it was closed in tests
	 */
	@After
	@Override
	public void tearDown() throws Exception {
		final IProject project = rodinProject.getProject();
		if (project.exists() && !project.isOpen()) {
			project.open(null);
		}
		super.tearDown();
	}

	/**
	 * Ensures that an editor related to a deleted file is correctly closed.
	 */
	@Test
	public void testCloseEditorForDeletedFile() throws Exception {
		final IMachineRoot myMachine = createMachine("MyMachine");
		final IEditorPart editor = openEditor(myMachine);
		final IEditorInput input = editor.getEditorInput();
		assertEquals(editor, getEditorPartFor(input));
		myMachine.getRodinFile().delete(true, null);
		assertFalse(myMachine.exists());
		assertNull(getEditorPartFor(input));
	}

	/**
	 * Ensures that a proving editor related to a deleted file is correctly
	 * closed.
	 */
	@Test
	public void testCloseProvingEditorForDeletedProofFile() throws Exception {
		final IMachineRoot myMachine = createMachine("MyMachine");
		final IPSRoot psRoot = myMachine.getPSRoot();
		final IEditorInput mpInput = new FileEditorInput(psRoot.getResource());
		final IEditorPart mProvingEditor = openUnactiveMachineEditor(mpInput,
				psRoot);
		psRoot.getRodinProject().getProject().build(
				IncrementalProjectBuilder.FULL_BUILD, null);
		assertEquals(mProvingEditor, getEditorPartFor(mpInput));
		myMachine.getRodinFile().delete(true, null);
		psRoot.getRodinProject().getProject().build(
				IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		assertEquals(null, getEditorPartFor(mpInput));
	}

	/**
	 * Ensures that editors related to renamed files are correctly closed.
	 */
	@Test
	public void testCloseOnFileRename() throws Exception {
		final IMachineRoot myMachine = createMachine("MyMachine");
		final IContextRoot myContext = createContext("MyContext");
		final IEditorInput mInput = new FileEditorInput(myMachine.getResource());
		final IEditorInput cInput = new FileEditorInput(myContext.getResource());
		final IEditorPart machineEditor = openUnactiveMachineEditor(mInput,
				myMachine);
		final IEditorPart contextEditor = openUnactiveMachineEditor(cInput,
				myContext);
		assertEquals(machineEditor, getEditorPartFor(mInput));
		assertEquals(contextEditor, getEditorPartFor(cInput));

		myMachine.getRodinFile().rename(
				EventBPlugin.getMachineFileName("newMachine"), true, null);
		myContext.getRodinFile().rename(
				EventBPlugin.getContextFileName("newContext"), true, null);
		assertFalse(myMachine.exists());
		assertFalse(myContext.exists());
		assertNull(getEditorPartFor(mInput));
		assertNull(getEditorPartFor(cInput));
	}

	/**
	 * Ensures that proving editors related to renamed files are correctly
	 * closed.
	 */
	@Test
	public void testCloseProvingEditorOnFileRename() throws Exception {
		final IMachineRoot myMachine = createMachine("MyMachine");
		final IPSRoot psRoot = myMachine.getPSRoot();
		final IEditorInput mpInput = new FileEditorInput(psRoot.getResource());
		final IEditorPart mProvingEditor = openUnactiveMachineEditor(mpInput,
				psRoot);
		psRoot.getRodinProject().getProject().build(
				IncrementalProjectBuilder.FULL_BUILD, null);
		assertEquals(mProvingEditor, getEditorPartFor(mpInput));
		myMachine.getRodinFile().rename(
				EventBPlugin.getMachineFileName("newMachine"), true, null);
		psRoot.getRodinProject().getProject().build(
				IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		assertEquals(null, getEditorPartFor(mpInput));
	}

	/**
	 * Ensures that editors related to a renamed (e.g. moved) project are
	 * correctly closed.
	 */
	@Test
	public void testCloseOnProjectRename() throws Exception {
		final IMachineRoot myMachine = createMachine("MyMachine");
		final IContextRoot myContext = createContext("MyContext");
		final IEditorInput mInput = new FileEditorInput(myMachine.getResource());
		final IEditorInput cInput = new FileEditorInput(myContext.getResource());
		final IEditorPart machineEditor = openUnactiveMachineEditor(mInput,
				myMachine);
		final IEditorPart contextEditor = openEditor(myContext);
		assertEquals(machineEditor, getEditorPartFor(mInput));
		assertEquals(contextEditor, getEditorPartFor(cInput));
		rodinProject.getProject().move(
				workspace.getRoot().getFullPath().append("movedProject"), true,
				null);
		assertNull(getEditorPartFor(mInput));
		assertNull(getEditorPartFor(cInput));
	}

	/**
	 * Ensures that proved editors related to a renamed (e.g. moved) project are
	 * correctly closed.
	 */
	@Test
	public void testCloseProvingEditorOnProjectRename() throws Exception {
		final IMachineRoot myMachine = createMachine("MyMachine");
		final IPSRoot psRoot = myMachine.getPSRoot();
		final IEditorInput mpInput = new FileEditorInput(psRoot.getResource());
		final IEditorPart mProvingEditor = openUnactiveMachineEditor(mpInput,
				psRoot);
		psRoot.getRodinProject().getProject().build(
				IncrementalProjectBuilder.FULL_BUILD, null);
		assertEquals(mProvingEditor, getEditorPartFor(mpInput));
		rodinProject.getProject().move(
				workspace.getRoot().getFullPath().append("movedProject"), true,
				null);
		assertNull(getEditorPartFor(mpInput));
	}

	/**
	 * Ensures that editors related to a deleted project are correctly closed.
	 */
	@Test
	public void testCloseEditorsForDeletedProject() throws Exception {
		final IMachineRoot myMachine = createMachine("MyMachine");
		final IContextRoot myContext = createContext("MyContext");
		final IEditorPart machineEditor = openEditor(myMachine);
		final IEditorInput machineInput = machineEditor.getEditorInput();
		final IEditorPart contextEditor = openEditor(myContext);
		final IEditorInput contextInput = contextEditor.getEditorInput();
		assertEquals(machineEditor, getEditorPartFor(machineInput));
		assertEquals(contextEditor, getEditorPartFor(contextInput));
		rodinProject.getCorrespondingResource().delete(true, null);
		assertFalse(myMachine.exists());
		assertFalse(myContext.exists());
		assertNull(getEditorPartFor(machineInput));
		assertNull(getEditorPartFor(contextInput));
	}

	/**
	 * Ensures that proving editors related to a deleted project are correctly
	 * closed.
	 */
	@Test
	public void testCloseProvingEditorForDeletedProject() throws Exception {
		final IMachineRoot myMachine = createMachine("MyMachine");
		final IPSRoot psRoot = myMachine.getPSRoot();
		final IEditorInput mpInput = new FileEditorInput(psRoot.getResource());
		final IEditorPart mProvingEditor = openUnactiveMachineEditor(mpInput,
				psRoot);
		psRoot.getRodinProject().getProject().build(
				IncrementalProjectBuilder.FULL_BUILD, null);
		assertEquals(mProvingEditor, getEditorPartFor(mpInput));
		rodinProject.getCorrespondingResource().delete(true, null);
		assertFalse(myMachine.exists());
		assertFalse(psRoot.exists());
		assertNull(getEditorPartFor(mpInput));
	}

	/**
	 * Ensures that unactive editors related to a deleted project are correctly
	 * closed.
	 */
	@Test
	public void testCloseUnactiveEditorsForDeletedProject() throws Exception {
		final IMachineRoot myMachine = createMachine("MyMachine");
		final IContextRoot myContext = createContext("MyContext");
		final IEditorInput mInput = new FileEditorInput(myMachine.getResource());
		final IEditorInput cInput = new FileEditorInput(myContext.getResource());
		final IEditorPart machineEditor = openUnactiveMachineEditor(mInput,
				myMachine);
		final IEditorPart contextEditor = openEditor(myContext);
		assertEquals(machineEditor, getEditorPartFor(mInput));
		assertEquals(contextEditor, getEditorPartFor(cInput));
		rodinProject.getCorrespondingResource().delete(true, null);
		assertFalse(myMachine.exists());
		assertFalse(myContext.exists());
		assertNull(getEditorPartFor(mInput));
		assertNull(getEditorPartFor(cInput));
	}

	/**
	 * Ensures that editors related to a project which is closed are correctly
	 * closed too.
	 */
	@Test
	public void testCloseOnProjectClose() throws Exception {
		final IMachineRoot myMachine = createMachine("MyMachine");
		final IContextRoot myContext = createContext("MyContext");
		final IEditorInput mInput = new FileEditorInput(myMachine.getResource());
		final IEditorInput cInput = new FileEditorInput(myContext.getResource());
		final IEditorPart machineEditor = openUnactiveMachineEditor(mInput,
				myMachine);
		final IEditorPart contextEditor = openEditor(myContext);
		assertEquals(machineEditor, getEditorPartFor(mInput));
		assertEquals(contextEditor, getEditorPartFor(cInput));
		rodinProject.getProject().close(null);
		assertNull(getEditorPartFor(cInput));
		assertNull(getEditorPartFor(mInput));
	}

	/**
	 * Ensures that proving editors are closed when project is closed.
	 * 
	 */
	@Test
	public void testCloseProvingeditorOnProjectClose() throws Exception {
		final IMachineRoot myMachine = createMachine("MyMachine");
		final IPSRoot psRoot = myMachine.getPSRoot();
		final IEditorInput mpInput = new FileEditorInput(psRoot.getResource());
		final IEditorPart mProvingEditor = openUnactiveMachineEditor(mpInput,
				psRoot);
		assertEquals(mProvingEditor, getEditorPartFor(mpInput));
		rodinProject.getProject().close(null);
		assertEquals(null, getEditorPartFor(mpInput));
		assertNull(getEditorPartFor(mpInput));
	}

	private static void waitForAsyncExecs() {
		final Display display = Display.findDisplay(Thread.currentThread());
		while (display.readAndDispatch()) {
			// do nothing
		}
	}

	private static IEditorPart getEditorPartFor(IEditorInput input) {
		waitForAsyncExecs();
		final IWorkbench workbench = PlatformUI.getWorkbench();
		for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
			for (IWorkbenchPage page : window.getPages()) {
				return page.findEditor(input);
			}
		}
		return null;
	}

	private IEditorPart openUnactiveMachineEditor(IEditorInput input,
			IRodinElement e) throws PartInitException {
		final IWorkbenchPage p = EventBUIPlugin.getActivePage();
		if (e instanceof IContextRoot) {
			return p.openEditor(input, EventBContextEditor.EDITOR_ID, false);
		}
		if (e instanceof IMachineRoot) {
			return p.openEditor(input, EventBMachineEditor.EDITOR_ID, false);
		}
		if (e instanceof IPSRoot) {
			return p.openEditor(input, ProverUI.EDITOR_ID, false);
		}
		return null;
	}

}
