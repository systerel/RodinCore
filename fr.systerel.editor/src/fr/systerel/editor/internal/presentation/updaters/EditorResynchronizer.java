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
package fr.systerel.editor.internal.presentation.updaters;

import static fr.systerel.editor.internal.editors.CaretPositionHelper.getHelper;
import static fr.systerel.editor.internal.editors.RodinEditor.DEBUG;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.editors.CaretPositionHelper;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.SelectionController;

/**
 * Abstract class defining the default algorithm to refresh the Rodin editor
 * main <code>styledText</code>.
 *
 * @author Thomas Muller
 */
public abstract class EditorResynchronizer {

	private final IProgressMonitor monitor;
	protected final RodinEditor editor;

	protected EditorSnapshot snapshot;

	public EditorResynchronizer(RodinEditor editor,
			IProgressMonitor monitor) {
		this.editor = editor;
		this.monitor = monitor;
	}

	/**
	 * Resynchronizes the editor and reposition selection and caret.
	 */
	public void resynchronize() {
		final StyledText styledText = editor.getStyledText();
		if (styledText == null || styledText.isDisposed()) {
			return;
		}
		Runnable runnable = new SynchronizationRunnable(this, editor, monitor); 
		final Display display = styledText.getDisplay();
		display.asyncExec(runnable);
	}
	
	/**
	 * For testing purpose only. Perform re-synchronization synchronously in UI.
	 */
	public void resynchronizeForTests() {
		final StyledText styledText = editor.getStyledText();
		final Display display = styledText.getDisplay();
		Runnable runnable = new SynchronizationRunnable(this, editor, monitor);
		display.syncExec(runnable);
	}

	private static class SynchronizationRunnable implements Runnable {

		private final EditorResynchronizer owner;
		private final IProgressMonitor monitor;
		private final RodinEditor editor;

		public SynchronizationRunnable(EditorResynchronizer owner,
				RodinEditor editor, IProgressMonitor monitor) {
			this.owner = owner;
			this.editor = editor;
			this.monitor = monitor;
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
			editor.getDocumentProvider().synchronizeRoot(monitor, true);
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

	/**
	 * Restores the top index, the selection and caret position. Called after
	 * the editor contents are refreshed. Clients may override.
	 */
	protected void restoreSnapshot() {
		final StyledText styledText = editor.getStyledText();
		styledText.setTopIndex(snapshot.getTopIndex());
		final SelectionController selCtrlr = editor.getSelectionController();
		selCtrlr.selectItems(snapshot.getSelectedItems());
		repositionCaret(styledText);
	}

	/**
	 * Places the caret at the best logical position after the editor contents
	 * have been refreshed.
	 *
	 * @param styledText
	 *            the main editor <code>styledText</code> after it has been
	 *            refreshed
	 */
	protected abstract void repositionCaret(final StyledText styledText);

	/**
	 * A snapshot of the current editor state in terms of caret position,
	 * topIndex value, and selected items.
	 */
	protected static class EditorSnapshot {

		private final RodinEditor editor;
		private final CaretPositionHelper caretHelper;

		private int topIndex;
		private ILElement[] selection;

		public EditorSnapshot(RodinEditor editor) {
			this.editor = editor;
			this.caretHelper = getHelper(editor.getStyledText());
		}

		public void record() {
			caretHelper.recordCaretPosition();
			topIndex = editor.getStyledText().getTopIndex();
			selection = editor.getSelectionController().getSelectedElements();
		}

		public int getTopIndex() {
			return topIndex;
		}

		public int getCaretOffset() {
			return caretHelper.getSafeNewPositionToEnd();
		}

		public ILElement[] getSelectedItems() {
			return selection;
		}
		
		public int getSafeLineOffset(int lineIndex) {
			return caretHelper.getSafeLineOffset(lineIndex);
		}

	}

}
