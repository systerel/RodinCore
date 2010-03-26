/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - now closing editors in the GUI thread using asyncExec()
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * Class that handles events from the database, and performs a clean closing of
 * opened editors containing removed files.
 * 
 * @author Thomas Muller
 */
public class EditorManager implements IElementChangedListener {

	public void elementChanged(ElementChangedEvent event) {
		handleDelta(event.getDelta());
	}

	private void handleDelta(IRodinElementDelta delta) {
		final IRodinElement element = delta.getElement();
		if (element instanceof IRodinDB) {
			final IRodinElementDelta[] deltas = delta.getAffectedChildren();
			for (final IRodinElementDelta childDelta : deltas) {
				handleDelta(childDelta);
			}
			return;
		}
		if (element instanceof IRodinProject) {
			handleRemoveOperation(delta.getRemovedChildren());
		}
	}

	private void handleRemoveOperation(IRodinElementDelta[] removedDeltas) {
		final List<IRodinFile> removedFiles = getRemovedFiles(removedDeltas);
		for (final IRodinFile file : removedFiles) {
			closeRelatedEditors(file.getResource());
		}
	}

	private List<IRodinFile> getRemovedFiles(IRodinElementDelta[] removed) {
		final List<IRodinFile> files = new ArrayList<IRodinFile>();
		for (final IRodinElementDelta delta : removed) {
			final IRodinElement element = delta.getElement();
			if (element instanceof IRodinFile) {
				files.add((IRodinFile) element);
			}
		}
		return files;
	}

	private void closeRelatedEditors(IFile file) {
		final List<IEditorReference> editorsToClose = new ArrayList<IEditorReference>();
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		for (final IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
			for (final IWorkbenchPage page : window.getPages()) {
				for (final IEditorReference ref : page.getEditorReferences()) {
					final IEditorInput input;
					try {
						input = ref.getEditorInput();
					} catch (PartInitException e) {
						// ignore this editor reference
						continue;
					}
					if (isConcernedEditor(file, input)) {
						editorsToClose.add(ref);
					}
				}
				final int size = editorsToClose.size();
				final IEditorReference[] array = new IEditorReference[size];
				editorsToClose.toArray(array);
				display.asyncExec(new Runnable() {
					public void run() {
						page.closeEditors(array, false);
					}
				});
			}
		}
	}

	private boolean isConcernedEditor(IFile file, IEditorInput input) {
		if (!(input instanceof FileEditorInput)) {
			return false;
		}
		final IFile inputFile = ((FileEditorInput) input).getFile();
		return inputFile.equals(file);
	}

}
