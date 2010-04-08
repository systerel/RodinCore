package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.actions.ActionGroup;
import org.eventb.ui.eventbeditor.IEventBEditor;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the tree
 *         viewer for the Project Explorer.
 */
public abstract class EventBEditableTreeViewerActionGroup extends ActionGroup {

	// The project explorer.
	protected EventBEditableTreeViewer viewer;

	protected IEventBEditor<?> editor;

	/**
	 * Dynamically fill the context menu (depends on the selection).
	 * <p>
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		ISelection sel = getContext().getSelection();
		if (sel instanceof IStructuredSelection) {
			MenuManager newMenu = new MenuManager("&New");

			newMenu.add(new Separator("new"));
			menu.add(newMenu);
			menu.add(new Separator("modelling"));
			menu.add(new Separator("proving"));
			menu.add(new Separator());
			// menu.add(refreshAction);

			menu.add(new Separator());
		}
	}

	/**
	 * Constructs a new navigator action group and creates its actions.
	 * 
	 * @param viewer
	 *            the resource navigator
	 */
	public EventBEditableTreeViewerActionGroup(IEventBEditor<?> editor,
			EventBEditableTreeViewer viewer) {
		this.viewer = viewer;

		this.editor = editor;
		makeActions();
	}

	/**
	 * Handles a key pressed event by invoking the appropriate action. Does
	 * nothing by default.
	 */
	public void handleKeyPressed(KeyEvent event) {
		// Do nothing
	}

	/**
	 * Makes the actions contained in this action group.
	 */
	protected abstract void makeActions();

}
