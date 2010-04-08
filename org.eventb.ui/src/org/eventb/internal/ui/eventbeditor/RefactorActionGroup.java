package org.eventb.internal.ui.eventbeditor;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.TextActionHandler;
import org.eclipse.ui.views.navigator.ResourceSelectionUtil;
import org.eventb.ui.eventbeditor.IEventBEditor;

public class RefactorActionGroup extends EventBEditableTreeViewerActionGroup {

	private Clipboard clipboard;

	private CopyAction copyAction;

	private PasteAction pasteAction;

//	private DeleteAction deleteAction;

	private TextActionHandler textActionHandler;

	public RefactorActionGroup(IEventBEditor<?> editor, EventBEditableTreeViewer explorer) {
		super(editor, explorer);
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

	@Override
	public void fillContextMenu(IMenuManager menu) {
		IStructuredSelection selection = (IStructuredSelection) getContext()
				.getSelection();

		boolean anyResourceSelected = !selection.isEmpty()
				&& ResourceSelectionUtil.allResourcesAreOfType(selection,
						IResource.PROJECT | IResource.FOLDER | IResource.FILE);

		copyAction.selectionChanged(selection);
		menu.add(copyAction);
		pasteAction.selectionChanged(selection);
		menu.add(pasteAction);

//		deleteAction.selectionChanged(selection);
//		menu.add(deleteAction);
		
		if (anyResourceSelected) {
			// deleteAction.selectionChanged(selection);
			// menu.add(deleteAction);
			// moveAction.selectionChanged(selection);
			// menu.add(moveAction);
			// renameAction.selectionChanged(selection);
			// menu.add(renameAction);
		}
	}

	@Override
	public void fillActionBars(IActionBars actionBars) {
		textActionHandler = new TextActionHandler(actionBars); // hooks
																// handlers
		textActionHandler.setCopyAction(copyAction);
		textActionHandler.setPasteAction(pasteAction);
//		textActionHandler.setDeleteAction(deleteAction);
		// renameAction.setTextActionHandler(textActionHandler);
		//
		// actionBars.setGlobalActionHandler(ActionFactory.MOVE.getId(),
		// moveAction);
		// actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(),
		// renameAction);
	}

	@Override
	protected void makeActions() {
		// TreeViewer treeViewer = explorer.getViewer();
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

		// moveAction = new ResourceNavigatorMoveAction(shell, treeViewer);
		// renameAction = new ResourceNavigatorRenameAction(shell, treeViewer);
		//
//		deleteAction = new DeleteAction(shell);
//		deleteAction.setDisabledImageDescriptor(images
//				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
//		deleteAction.setImageDescriptor(images
//				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	}

}
