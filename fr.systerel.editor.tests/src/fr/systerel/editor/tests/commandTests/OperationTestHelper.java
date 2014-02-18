/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.tests.commandTests;

import static fr.systerel.editor.tests.TestUtils.WORKBENCH;
import static fr.systerel.editor.tests.TestUtils.openRodinEditor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISources;
import org.eclipse.ui.commands.ICommandService;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILFile;

import fr.systerel.editor.internal.editors.OverlayEditor;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.SelectionController;

/**
 * An helper class dedicated to perform operations in the Rodin Editor.
 */
public class OperationTestHelper {

	private final RodinEditor rodinEditor;
	private final ILElement root;
	private final SelectionController selController;

	private SelectionChangeSynchronizer synchronizer;

	private Map<String, IExecutionListener> synchronizers;

	public OperationTestHelper(IFile file) throws Exception {
		rodinEditor = (RodinEditor) openRodinEditor(file);
		final ILFile rootFile = rodinEditor.getResource();
		root = rootFile.getRoot();
		selController = rodinEditor.getSelectionController();
		addSelectionSynchronization();
	}

	public void addSelectionSynchronization() {
		synchronizer = new SelectionChangeSynchronizer();
		selController.addSelectionChangedListener(synchronizer);
		synchronizers = new HashMap<String, IExecutionListener>();
	}
	
	public void clearClipboard() {
		final Clipboard clipboard = new Clipboard(WORKBENCH.getDisplay());
		clipboard.clearContents();
		clipboard.dispose();
	}

	public void closeRodinEditor() {
		rodinEditor.getEditorSite().getPage().closeEditor(rodinEditor, false);
	}

	public void setSelection(ILElement[] toSelect) throws InterruptedException {
		selController.selectItems(toSelect);
		// wait for the postSelectionChange event is fully propagated
		synchronized (synchronizer) {
			synchronizer.wait(1000);
		}
	}
	
	public void setSelection(int start, int length) throws InterruptedException {
		selController.clearSelection();
		rodinEditor.getOverlayEditor().quitEdition(false);
		rodinEditor.getStyledText().setSelection(new Point(start, length));
	}

	public RodinEditor getEditor() {
		return rodinEditor;
	}

	public OverlayEditor getOverlay() {
		return rodinEditor.getOverlayEditor();
	}

	public ILElement getRoot() {
		return root;
	}

	public void executeOperation(String commandId) throws Exception {
		final ICommandService service = (ICommandService) WORKBENCH
				.getService(ICommandService.class);
		final Command command = service.getCommand(commandId);
		final EvaluationContext context = new EvaluationContext(null,
				Collections.EMPTY_LIST);
		context.addVariable(ISources.ACTIVE_EDITOR_NAME, rodinEditor);

		final IExecutionListener commandSync = getSynchronizer(commandId);
		command.addExecutionListener(commandSync);
		command.executeWithChecks(new ExecutionEvent(command,
				Collections.EMPTY_MAP, null, context));
		synchronized (commandSync) {
			commandSync.wait(500);
		}
	}

	private IExecutionListener getSynchronizer(String commandId) {
		IExecutionListener s = synchronizers.get(commandId);
		if (s == null) {
			s = new CommandPerformedSynchronizer();
			synchronizers.put(commandId, s);
		}
		return s;
	}

	private static class SelectionChangeSynchronizer implements
			ISelectionChangedListener {
	
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			synchronized (this) {
				notifyAll();
			}
		}
	
	}

	private static class CommandPerformedSynchronizer implements IExecutionListener {
	
		@Override
		public void notHandled(String commandId, NotHandledException exception) {
			// Nothing to do.
			
		}
	
		@Override
		public void postExecuteFailure(String commandId,
				ExecutionException exception) {
			// Nothing to do.
			
		}
	
		@Override
		public void postExecuteSuccess(String commandId, Object returnValue) {
			synchronized (this) {
				notifyAll();
			}
		}
	
		@Override
		public void preExecute(String commandId, ExecutionEvent event) {
			// Nothing to do.
			
		}
		
	}

}