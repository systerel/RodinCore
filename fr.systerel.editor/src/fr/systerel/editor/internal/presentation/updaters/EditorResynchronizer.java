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
package fr.systerel.editor.internal.presentation.updaters;

import static fr.systerel.editor.internal.editors.RodinEditor.DEBUG;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.SelectionController;

/**
 * Abstract class defining the default algorithm to refresh the Rodin editor
 * main <code>styledText</code>.
 *
 * @author Thomas Muller
 */
public class EditorResynchronizer {

	private final IProgressMonitor monitor;
	protected final RodinEditor editor;
	private final ILElement newElement;

	protected EditorSnapshot snapshot;

	public EditorResynchronizer(RodinEditor editor, IProgressMonitor monitor) {
		this(editor, monitor, null);
	}

	public EditorResynchronizer(RodinEditor editor, IProgressMonitor monitor,
			ILElement newElement) {
		this.editor = editor;
		this.monitor = monitor;
		this.newElement = newElement;
	}

	/**
	 * Resynchronizes the editor and reposition selection and caret.
	 */
	public void resynchronize() {
		final StyledText styledText = editor.getStyledText();
		if (styledText == null || styledText.isDisposed()) {
			return;
		}
		final Runnable runnable = new SynchronizationRunnable(this, editor);
		final Display display = styledText.getDisplay();
		display.asyncExec(runnable);
	}
	
	/**
	 * For testing purpose only. Perform re-synchronization synchronously in UI.
	 */
	public void resynchronizeForTests() {
		final StyledText styledText = editor.getStyledText();
		final Display display = styledText.getDisplay();
		Runnable runnable = new SynchronizationRunnable(this, editor);
		display.syncExec(runnable);
	}

	private static class SynchronizationRunnable implements Runnable {

		private final EditorResynchronizer owner;
		private final RodinEditor editor;

		public SynchronizationRunnable(EditorResynchronizer owner,
				RodinEditor editor) {
			this.owner = owner;
			this.editor = editor;
		}

		@Override
		public void run() {
			final StyledText styledText = editor.getStyledText();
			if (styledText.isDisposed()) {
				return;
			}
			final long start = System.currentTimeMillis();
			if (DEBUG)
				System.out.println("\\ Start refreshing Rodin Editor.");
			owner.takeSnapshot();
			owner.synchronize();
			owner.restoreSnapshot();
			if (DEBUG) {
				System.out.println("\\ Finished refreshing Rodin Editor.");
				final long time = System.currentTimeMillis() - start;
				System.out.println("\\ Elapsed time : " + time + "ms.");
			}
		}

	}

	/**
	 * Called before the editor contents are refreshed. Clients may override.
	 */
	protected void takeSnapshot() {
		snapshot = new EditorSnapshot(editor);
		snapshot.record();
	}

	protected void synchronize() {
		editor.getDocumentProvider().synchronizeRoot(monitor);
	}

	/**
	 * Restores the top index, the selection and caret position. Called after
	 * the editor contents are refreshed. Clients may override.
	 */
	protected void restoreSnapshot() {
		final StyledText styledText = editor.getStyledText();
		styledText.setCaretOffset(snapshot.getCaretOffset(newElement));
		styledText.setTopIndex(snapshot.getTopIndex(newElement));
		final SelectionController selCtrlr = editor.getSelectionController();
		selCtrlr.selectItems(snapshot.getSelectedItems());
	}

}
