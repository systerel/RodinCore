package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eventb.ui.eventbeditor.IEventBEditor;

public class MasterSectionActionGroup<E extends IEventBEditor<?>> extends ActionGroup {
	private Clipboard clipboard;

	protected CopyAction copyAction;

	protected PasteAction pasteAction;

	// The Event-B Editor.
	protected E editor;

	// The tree viewer in the master section
	protected TreeViewer viewer;

	public MasterSectionActionGroup(E eventBEditor,
			TreeViewer treeViewer) {
		this.editor = eventBEditor;
		this.viewer = treeViewer;

		Shell shell = viewer.getControl().getShell();
		clipboard = new Clipboard(shell.getDisplay());

		pasteAction = new PasteAction(shell, clipboard, editor.getRodinInput());
		ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
		pasteAction.setDisabledImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		pasteAction.setImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));

		copyAction = new CopyAction(shell, clipboard, pasteAction);
		copyAction.setDisabledImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		copyAction.setImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
	}

	/**
	 * Fill the context menu with the actions create initially.
	 * <p>
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		ISelection sel = getContext().getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;
			copyAction.selectionChanged(ssel);
			menu.add(copyAction);
			pasteAction.selectionChanged(ssel);
			menu.add(pasteAction);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#dispose()
	 */
	@Override
	public void dispose() {
		if (clipboard != null) {
			clipboard.dispose();
			clipboard = null;
		}
		super.dispose();
	}
	
}
