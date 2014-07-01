/*******************************************************************************
 * Copyright (c) 2013, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.tests;

import static fr.systerel.editor.tests.TestUtils.copyTestFileInProject;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_DELETE;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_UNDO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.custom.StyledText;
import org.eventb.core.IAxiom;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILUtils;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.presentation.updaters.EditorResynchronizer;
import fr.systerel.editor.tests.commandTests.OperationTestHelper;

/**
 * Tests for {@link EditorResynchronizer} classes.
 */
public class CaretRepositionTests {

	private static final String contextName = "context2.buc";

	private static IRodinProject rodinProject;
	private static IProject project;

	private RodinEditor editor;
	private OperationTestHelper helper;

	@BeforeClass
	public static void beforeClass() throws Exception {
		rodinProject = TestUtils.createRodinProject("P");
		project = rodinProject.getProject();
		copyTestFileInProject(contextName, project);
	}

	@Before
	public void setUp() throws Exception {
		final IFile testFile = project.getFile(contextName);
		editor = (RodinEditor) TestUtils.openRodinEditor(testFile);
		helper = new OperationTestHelper(testFile);
	}

	@After
	public void tearDown() throws Exception {
		helper.closeRodinEditor();
	}

	@AfterClass
	public static void afterClass() throws CoreException {
		TestUtils.deleteProject("P");
	}

	/**
	 * Test which checks {@link BasicEditorResynchronizer}. The default
	 * behaviour is to maintain the caret position after the editor has been
	 * resynchronized.
	 */
	@Test
	public void testBasicCaretReposition() throws Exception {
		final int offset = 58;
		editor.getStyledText().setCaretOffset(offset);
		
		// force refresh synchronously
		new EditorResynchronizer(editor, null).resynchronizeForTests();
		
		assertEquals(offset, editor.getCurrentOffset());
	}

	/**
	 * Test which checks both {@link AfterDeletionResynchronizer} and
	 * {@link AfterAdditionResynchronizer}. <br />
	 * The caret is re-positioned at the starting offset of the deleted element
	 * line, and re-positioned at the first editable interval of an added
	 * element.
	 */
	@Test
	public void testCaretRepositionAfterEdition() throws Exception {
		final DocumentMapper mapper = editor.getDocumentMapper();
		final ILElement root = editor.getResource().getRoot();

		final List<ILElement> axioms = root
				.getChildrenOfType(IAxiom.ELEMENT_TYPE);

		final ILElement axm2IL = axioms.get(1);
		final IInternalElement axm2 = axm2IL.getElement();

		final EditorElement axm2EditElement = mapper.findEditorElement(axm2IL);
		assertNotNull(axm2EditElement);

		final Interval axm2EditableInter = mapper
				.findEditableIntervalAfter(axm2EditElement.getOffset());
		final int axm2OriginalOffset = axm2EditableInter.getOffset();

		final StyledText styledText = editor.getStyledText();
		final int firstDeletedLine = styledText
				.getLineAtOffset(axm2EditElement.getOffset());
		styledText.setCaretOffset(axm2OriginalOffset);
		helper.setSelection(new ILElement[] { axm2IL });
		helper.executeOperation(EDIT_DELETE);

		// force refresh synchronously
		new EditorResynchronizer(editor, null).resynchronizeForTests();

		helper.executeOperation(EDIT_UNDO);
		final int expectedOffset = styledText.getOffsetAtLine(firstDeletedLine);
		assertEquals(expectedOffset, editor.getCurrentOffset());

		// find the new IL element which corresponds to axm2
		final ILElement addedElement = ILUtils.findElement(axm2, root);
		
		// force refresh synchronously
		new EditorResynchronizer(editor, null, addedElement).resynchronizeForTests();
		
		final EditorElement editElem2 = mapper.findEditorElement(addedElement);
		final int expectedOffset2 = mapper.findEditableIntervalAfter(
				editElem2.getOffset()).getOffset();

		assertEquals(axm2OriginalOffset, expectedOffset2);
		assertEquals(expectedOffset2, editor.getCurrentOffset());
	}

}
