/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.refine;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.projectexplorer.actions.RodinFileInputValidator;
import org.eventb.internal.ui.refine.RefinementUIRegistry.RefinementUI;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRefinementManager;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Handler for command "org.eventb.ui.refine".
 * 
 * @author Nicolas Beauger
 * 
 */
public class RefineHandler extends AbstractHandler {

	private static final class CreateRefinement implements IWorkspaceRunnable {

		private final IInternalElement sourceRoot;
		private final IInternalElement targetRoot;

		public CreateRefinement(IInternalElement sourceRoot,
				IInternalElement targetRoot) {
			this.sourceRoot = sourceRoot;
			this.targetRoot = targetRoot;
		}

		@Override
		public void run(IProgressMonitor monitor) throws RodinDBException {
			targetRoot.getRodinFile().create(false, monitor);

			final IRefinementManager refMgr = RodinCore.getRefinementManager();
			final boolean success = refMgr.refine(sourceRoot, targetRoot,
					monitor);

			if (success) {
				targetRoot.getRodinFile().save(monitor, false);
				UIUtils.linkToEventBEditor(targetRoot);
			}
		}

	}

	private IInternalElement currentRoot;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (currentRoot == null) {
			throw new IllegalStateException(
					"I have no clue which component to refine !");
		}
		final IInternalElementType<? extends IInternalElement> rootType = currentRoot
				.getElementType();
		final Shell activeShell = HandlerUtil.getActiveShellChecked(event);
		final RefinementUI refUI = RefinementUIRegistry.getDefault()
				.getRefinementUI(rootType);
		final String extension = getExtension(rootType);
		final IRodinFile target = askRefinementFileFor(
				currentRoot.getRodinFile(), activeShell, refUI, extension);
		if (target == null) {
			return null;
		}

		final CreateRefinement op = new CreateRefinement(currentRoot,
				target.getRoot());
		try {
			RodinCore.run(op, null);
		} catch (RodinDBException e) {
			UIUtils.log(
					e,
					"When creating a refinement of "
							+ currentRoot.getElementName() + " by  "
							+ target.getElementName());
		}
		return null;
	}

	private static String getExtension(IInternalElementType<?> rootType) {
		final IContentType contentType = Platform.getContentTypeManager()
				.getContentType(rootType.getId());
		final String[] fileSpecs = contentType
				.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
		if (fileSpecs.length == 0) {
			throw new IllegalStateException(
					"no file extension for content type " + contentType.getId());
		}
		final String extension = fileSpecs[0];
		return extension;
	}

	// Asks the user the name of the refined file to create and returns it.
	private static IRodinFile askRefinementFileFor(IRodinFile abs,
			Shell parentShell, RefinementUI refUI, String extension) {
		final IRodinProject prj = abs.getRodinProject();
		final InputDialog dialog = new InputDialog(parentShell, refUI.title,
				refUI.message, abs.getBareName() + "0",
				new RodinFileInputValidator(prj));
		dialog.open();

		final String name = dialog.getValue();
		if (name == null) {
			return null;
		}
		final IPath path = new Path(name).addFileExtension(extension);
		final String fileName = path.toString();
		return prj.getRodinFile(fileName);
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		final EvaluationContext eval = (EvaluationContext) evaluationContext;
		final Object defaultVariable = eval.getDefaultVariable();
		final List<?> selection = (List<?>) defaultVariable;
		final boolean enabled = computeEnablement(selection);

		if (enabled) {
			this.currentRoot = (IInternalElement) selection.get(0);
		}
		setBaseEnabled(enabled);
	}

	private static boolean computeEnablement(List<?> selection) {
		if (selection.size() != 1)
			return false;
		final Object x = selection.get(0);
		if (!(x instanceof IInternalElement))
			return false;
		final IInternalElement e = (IInternalElement) x;
		return RefinementUIRegistry.getDefault().getRefinementUI(
				e.getElementType()) != null;
	}

}
