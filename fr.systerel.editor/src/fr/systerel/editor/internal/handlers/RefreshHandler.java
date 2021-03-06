/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;


import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.RefreshAction;
import org.rodinp.core.emf.api.itf.ILFile;

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * A handler to refresh the resource manipulated by the active Rodin editor and
 * the editor as well.
 * 
 * @author "Thomas Muller"
 */
public class RefreshHandler extends AbstractEditorHandler {

	private static final MutexRule MUTEX = new MutexRule();

	/**
	 * Schedules sequentially two jobs:
	 * <ul>
	 * <li>a job refreshing the resource opened with the Rodin Editor, and</li>
	 * <li>a job refreshing the editor</li>.
	 * </ul>
	 * The job refreshing the editor is run only when the job refreshing the
	 * resource has finished.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final RodinEditor editor = getActiveRodinEditor();
		if (editor == null)
			return null;
		final IWorkbenchWindow ww = editor.getSite().getWorkbenchWindow();
		final RefreshAction refreshAction = new CustomRefreshAction(ww, MUTEX);
		refreshAction.run();

		final Job c = new RefreshEditorJob(editor);
		c.setRule(MUTEX);
		c.schedule();
		return null;
	}

	/**
	 * A mutual exclusion rule to create job synchronization.
	 */
	public static class MutexRule implements ISchedulingRule {

		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this;
		}

		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}
		
	}

	private static class RefreshEditorJob extends Job {

		private final RodinEditor editor;

		public RefreshEditorJob(RodinEditor editor) {
			super("Refresh Rodin Editor");
			this.editor = editor;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			editor.abordEdition();
			final ILFile resource = editor.getResource();
			resource.reload();
			return Status.OK_STATUS;
		}

	}

}
