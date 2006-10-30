package org.eventb.internal.ui.eventbeditor;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.CopyFilesAndFoldersOperation;
import org.eclipse.ui.actions.CopyProjectOperation;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.part.ResourceTransfer;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.RodinHandleTransfer;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

public class PasteAction extends SelectionListenerAction {

	/**
	 * The id of this action.
	 */
	public static final String ID = EventBUIPlugin.PLUGIN_ID + ".PasteAction"; //$NON-NLS-1$

	/**
	 * The shell in which to show any dialogs.
	 */
	private Shell shell;

	/**
	 * System clipboard
	 */
	private Clipboard clipboard;

	private IRodinFile file;

	protected PasteAction(Shell shell, Clipboard clipboard, IRodinFile file) {
		super(Messages.editorAction_paste_title);
		Assert.isNotNull(shell);
		Assert.isNotNull(clipboard);
		this.shell = shell;
		this.clipboard = clipboard;
		this.file = file;
		// setToolTipText(ProjectExplorerMessages.PasteAction_toolTip);
		setId(CopyAction.ID);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
		// INavigatorHelpContextIds.COPY_ACTION);
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		// try a resource transfer
		ResourceTransfer resTransfer = ResourceTransfer.getInstance();
		IResource[] resourceData = (IResource[]) clipboard
				.getContents(resTransfer);

		if (resourceData != null && resourceData.length > 0) {
			if (resourceData[0].getType() == IResource.PROJECT) {
				// enablement checks for all projects
				for (int i = 0; i < resourceData.length; i++) {
					CopyProjectOperation operation = new CopyProjectOperation(
							this.shell);
					operation.copyProject((IProject) resourceData[i]);
				}
			} else {
				// enablement should ensure that we always have access to a
				// Rodin Project
				IRodinElement element = getTarget(getStructuredSelection());

				IContainer container = ((IRodinProject) element).getProject();

				CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(
						this.shell);
				operation.copyResources(resourceData, container);
			}
			return;
		}
		// try a file transfer
		FileTransfer fileTransfer = FileTransfer.getInstance();
		String[] fileData = (String[]) clipboard.getContents(fileTransfer);

		if (fileData != null) {
			// enablement should ensure that we always have access to a
			// Rodin Project
			IRodinElement element = getTarget(getStructuredSelection());

			IContainer container = ((IRodinProject) element).getProject();

			CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(
					this.shell);
			operation.copyFiles(fileData, container);
			return;
		}

		// try a rodin handle transfer
		RodinHandleTransfer rodinHandleTransfer = RodinHandleTransfer
				.getInstance();
		final IRodinElement[] handleData = (IRodinElement[]) clipboard
				.getContents(rodinHandleTransfer);

		if (handleData == null)
			return;
		for (IRodinElement element : handleData) {
			if (!element.exists()) {
				MessageDialog.openError(this.shell, "Cannot Paste", "Element "
						+ element + " does not exist.");
			}
		}
		// enablement should ensure that we always have access to a
		// parent element
		final IRodinElement parent = getTarget(getStructuredSelection());

		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					for (IRodinElement element : handleData) {
						IRodinFile pasteInto;
						if (parent != null)
							pasteInto = (IRodinFile) parent.getOpenable();
						else
							pasteInto = file;
						if (element instanceof IEvent)
							((IInternalElement) element).copy(
									(IRodinElement) pasteInto, null, "evt"
											+ UIUtils.getFreeElementNameIndex(
													pasteInto,
													IEvent.ELEMENT_TYPE, "evt",
													0), false,
									new NullProgressMonitor());
						else if (element instanceof IInvariant)
							((IInternalElement) element).copy(
									(IRodinElement) pasteInto, null, "inv"
											+ UIUtils.getFreeElementNameIndex(
													pasteInto,
													IInvariant.ELEMENT_TYPE,
													"inv", 0), false,
									new NullProgressMonitor());
						else if (element instanceof ITheorem)
							((IInternalElement) element).copy(
									(IRodinElement) pasteInto, null, "thm"
											+ UIUtils.getFreeElementNameIndex(
													pasteInto,
													ITheorem.ELEMENT_TYPE,
													"thm", 0), false,
									new NullProgressMonitor());
						else if (element instanceof IVariant)
							((IInternalElement) element).copy(
									(IRodinElement) pasteInto, null, "variant"
											+ UIUtils.getFreeElementNameIndex(
													pasteInto,
													IVariant.ELEMENT_TYPE,
													"variant", 0), false,
									new NullProgressMonitor());
						else if (element instanceof IAxiom)
							((IInternalElement) element).copy(
									(IRodinElement) pasteInto, null, "axm"
											+ UIUtils.getFreeElementNameIndex(
													pasteInto,
													IAxiom.ELEMENT_TYPE, "axm",
													0), false,
									new NullProgressMonitor());
						else if (element instanceof IConstant)
							((IInternalElement) element).copy(
									(IRodinElement) pasteInto, null, "cst"
											+ UIUtils.getFreeElementNameIndex(
													pasteInto,
													IConstant.ELEMENT_TYPE,
													"cst", 0), false,
									new NullProgressMonitor());
						else if (element instanceof ICarrierSet)
							((IInternalElement) element).copy(
									(IRodinElement) pasteInto, null, "set"
											+ UIUtils.getFreeElementNameIndex(
													pasteInto,
													ICarrierSet.ELEMENT_TYPE,
													"set", 0), false,
									new NullProgressMonitor());
						else
							((IInternalElement) element).copy(parent, null,
									"element"
											+ UIUtils.getFreeElementNameIndex(
													(IInternalParent) parent,
													element.getElementType(),
													"element", 1), false,
									new NullProgressMonitor());
					}
				}

			}, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isSupportedType(Transfer transfer, TransferData[] types) {
		for (TransferData type : types) {
			if (transfer.isSupportedType(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * The <code>PasteAction</code> implementation of this
	 * <code>SelectionListenerAction</code> method enables this action if a
	 * resource compatible with what is on the clipboard is selected.
	 * 
	 * -Clipboard must have IResource or java.io.File -Projects can always be
	 * pasted if they are open -Workspace folder may not be copied into itself
	 * -Files and folders may be pasted to a single selected folder in open
	 * project or multiple selected files in the same folder
	 */
	protected boolean updateSelection(IStructuredSelection selection) {
		if (!super.updateSelection(selection))
			return false;

		TransferData[] types = clipboard.getAvailableTypes();
		boolean resTransfer = isSupportedType(ResourceTransfer.getInstance(),
				types);

		if (resTransfer) {
			final IResource[][] clipboardData = new IResource[1][];
			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					// clipboard must have resources or files
					ResourceTransfer resTransfer = ResourceTransfer
							.getInstance();
					clipboardData[0] = (IResource[]) clipboard
							.getContents(resTransfer);
				}
			});
			IResource[] resourceData = clipboardData[0];
			boolean isProjectRes = resourceData != null
					&& resourceData.length > 0
					&& resourceData[0].getType() == IResource.PROJECT;

			if (isProjectRes) {
				for (int i = 0; i < resourceData.length; i++) {
					// make sure all resource data are open projects
					// can paste open projects regardless of selection
					if (resourceData[i].getType() != IResource.PROJECT
							|| ((IProject) resourceData[i]).isOpen() == false)
						return false;
				}
				return true;
			}
		}

		IRodinElement targetElement = getTarget(selection);
		if (targetElement == null) { // if target is null then paste to the
										// file
			return isSupportedType(RodinHandleTransfer.getInstance(), types);
		} else {
			if (targetElement instanceof IRodinProject) {
				if (resTransfer) // Here transfer resource files
					return true;
				else {
					// Trying file transfer
					return isSupportedType(FileTransfer.getInstance(), types);
				}
			}

			else {
				if (!(targetElement instanceof IParent))
					return false;
				// Trying Rodin Handle transfer
				return isSupportedType(RodinHandleTransfer.getInstance(), types);
			}
		}

		// can paste files and folders to a single selection (file, folder,
		// open project) or multiple file selection with the same parent
		// List selectedResources = getSelectedResources();
		// if (selectedResources.size() > 1) {
		// for (int i = 0; i < selectedResources.size(); i++) {
		// IResource resource = (IResource) selectedResources.get(i);
		// if (resource.getType() != IResource.FILE)
		// return false;
		// if (!targetElement.equals(resource.getParent()))
		// return false;
		// }
		// }
		// if (resourceData != null) {
		// // linked resources can only be pasted into projects
		// if (isLinked(resourceData)
		// && targetElement.getType() != IResource.PROJECT)
		// return false;
		//
		// if (targetElement.getType() == IResource.FOLDER) {
		// // don't try to copy folder to self
		// for (int i = 0; i < resourceData.length; i++) {
		// if (targetElement.equals(resourceData[i]))
		// return false;
		// }
		// }
		// return true;
		// }
	}

	/**
	 * Returns the actual target of the paste action. Returns null if no valid
	 * target is selected.
	 * 
	 * @return the actual target of the paste action
	 */
	private IRodinElement getTarget(IStructuredSelection selection) {
		if (selection.size() != 1)
			return null;

		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IRodinElement)
			return (IRodinElement) firstElement;

		// if (firstElement instanceof TreeNode)
		// return (IRodinElement) ((TreeNode) firstElement).getParent();
		return null;
	}

}
