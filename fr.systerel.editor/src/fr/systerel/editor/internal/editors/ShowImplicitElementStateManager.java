/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;

import fr.systerel.editor.internal.handlers.ToggleImplicitHandler;
import fr.systerel.editor.internal.presentation.updaters.EditorResynchronizer;

/**
 * Class managing implicit elements display state for a given editor, and
 * responsible of updating the toggle state of the corresponding command when
 * the editor is activated.
 * 
 * @author Thomas Muller
 */
public class ShowImplicitElementStateManager implements IPartListener {

	private final RodinEditor editor;
	private boolean showImplicit = true;

	public ShowImplicitElementStateManager(RodinEditor editor) {
		this.editor = editor;
	}

	public void register() {
		final IPartService service = getPartService();
		service.addPartListener(this);
	}

	public void unregister() {
		final IPartService service = getPartService();
		service.removePartListener(this);
	}

	private IPartService getPartService() {
		return (IPartService) editor.getSite().getService(IPartService.class);
	}

	private State getToggleState() {
		final ICommandService cs = (ICommandService) editor.getEditorSite()
				.getService(ICommandService.class);
		final Command cmd = cs.getCommand(ToggleImplicitHandler.COMMAND_ID);
		return cmd.getState(RegistryToggleState.STATE_ID);
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part.equals(editor)) {
			getToggleState().setValue(showImplicit);
		}

	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		// Nothing to do
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		// Nothing to do
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// Nothing to do
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		// Nothing to do
	}

	public void toggle() {
		this.showImplicit = !showImplicit;
		new EditorResynchronizer(editor, null).resynchronize();
	}

	public boolean getValue() {
		return showImplicit;
	}

}
