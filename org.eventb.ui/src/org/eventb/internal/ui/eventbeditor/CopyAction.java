/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.part.ResourceTransfer;
import org.eventb.internal.ui.DragAndCopyUtil;
import org.eventb.internal.ui.RodinHandleTransfer;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

public class CopyAction extends SelectionListenerAction {

	/**
	 * The id of this action.
	 */
	public static final String ID = EventBUIPlugin.PLUGIN_ID + ".CopyAction"; //$NON-NLS-1$

	/**
	 * System clipboard
	 */
	private Clipboard clipboard;

	/**
	 * Associated paste action. May be <code>null</code>
	 */
	private PasteAction pasteAction;

	/**
	 * Creates a new action.
	 * @param clipboard
	 *            a platform clipboard
	 */
	protected CopyAction(Clipboard clipboard) {
		super(Messages.editorAction_copy_title);
		Assert.isNotNull(clipboard);
		this.clipboard = clipboard;
		setToolTipText(Messages.editorAction_copy_toolTip);
		setId(CopyAction.ID);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
		// INavigatorHelpContextIds.COPY_ACTION);
	}

	/**
	 * Creates a new action.
	 * 
	 * @param shell
	 *            the shell for any dialogs
	 * @param clipboard
	 *            a platform clipboard
	 * @param pasteAction
	 *            a paste action
	 * 
	 * @since 2.0
	 */
	public CopyAction(Shell shell, Clipboard clipboard, PasteAction pasteAction) {
		this(clipboard);
		this.pasteAction = pasteAction;
	}

	/**
	 * The <code>CopyAction</code> implementation of this method defined on
	 * <code>IAction</code> depends on the type of selections to copies
	 * elements to the clipboard.
	 */
	@Override
	public void run() {
		IStructuredSelection selection = this.getStructuredSelection();
		Collection<IRodinElement> elements = new ArrayList<IRodinElement>();

		// Added to list of parent only.
		for (Iterator<?> it = selection.iterator(); it.hasNext();) {
			Object obj = it.next();
			IRodinElement element = null;
			if (obj instanceof IRodinElement) {
				element = (IRodinElement) obj;
			}
			if (element != null)
				elements = UIUtils.addToTreeSet(elements, element);
		}

		boolean projSelected = DragAndCopyUtil.selectionIsOfTypeRodinProject(elements);
		boolean fileFoldersSelected = DragAndCopyUtil.selectionIsOfTypeRodinFile(elements);

		// Copies projects into the clipboard
		// Copies as Resource, Text transfer
		// and as Rodin Handle?
		int length = selection.size();
		if (projSelected) {
			IResource[] resources = new IResource[length];
			int i = 0;
			StringBuffer buf = new StringBuffer();
			for (Iterator<?> it = selection.iterator(); it.hasNext();) {
				IRodinProject prj = (IRodinProject) it.next();
				resources[i] = prj.getResource();
				if (i > 0)
					buf.append("\n"); //$NON-NLS-1$
				buf.append(prj.getElementName());
				i++;
			}
			clipboard.setContents(new Object[] { resources, buf.toString() },
					new Transfer[] { ResourceTransfer.getInstance(),
							TextTransfer.getInstance() });
			return;
		}

		// Copies files into the clipboard
		// Copies as Resource, File, Text transfer
		// and as Rodin Handle?
		if (fileFoldersSelected) {
			IResource[] resources = new IResource[length];
			String[] fileNames = new String[length];
			int actualLength = 0;
			int i = 0;
			StringBuffer buf = new StringBuffer();
			for (Iterator<?> it = selection.iterator(); it.hasNext();) {
				IRodinFile file = (IRodinFile) it.next();
				resources[i] = file.getResource();
				IPath location = resources[i].getLocation();
				// location may be null. See bug 29491.
				if (location != null)
					fileNames[actualLength++] = location.toOSString();
				if (i > 0)
					buf.append("\n"); //$NON-NLS-1$
				buf.append(file.getElementName());
				i++;
			}
			// was one or more of the locations null?
			if (actualLength < length) {
				String[] tempFileNames = fileNames;
				fileNames = new String[actualLength];
				for (int j = 0; j < actualLength; j++)
					fileNames[j] = tempFileNames[j];
			}
			setClipboard(resources, fileNames, buf.toString());

			return;
		}

		// Copies internal element
		// Copies as Rodin Handle
		// Text (XML format)?
		else {
			clipboard.setContents(new Object[] { elements
					.toArray(new IRodinElement[selection.size()]) },
					new Transfer[] { RodinHandleTransfer.getInstance() });

		}
//		List selectedResources = getSelectedResources();
//		IResource[] resources = (IResource[]) selectedResources
//				.toArray(new IResource[selectedResources.size()]);

		// Get the file names and a string representation
//		final int length = resources.length;
//		int actualLength = 0;
//		String[] fileNames = new String[length];
//		StringBuffer buf = new StringBuffer();
//		for (int i = 0; i < length; i++) {
//			IPath location = resources[i].getLocation();
//			// location may be null. See bug 29491.
//			if (location != null)
//				fileNames[actualLength++] = location.toOSString();
//			if (i > 0)
//				buf.append("\n"); //$NON-NLS-1$
//			buf.append(resources[i].getName());
//		}
//		// was one or more of the locations null?
//		if (actualLength < length) {
//			String[] tempFileNames = fileNames;
//			fileNames = new String[actualLength];
//			for (int i = 0; i < actualLength; i++)
//				fileNames[i] = tempFileNames[i];
//		}
//		setClipboard(resources, fileNames, buf.toString());

		// update the enablement of the paste action
		// workaround since the clipboard does not suppot callbacks
		if (pasteAction != null && pasteAction.getStructuredSelection() != null)
			pasteAction.selectionChanged(pasteAction.getStructuredSelection());
		
	}

	/**
	 * Set the clipboard contents. Prompt to retry if clipboard is busy.
	 * 
	 * @param resources
	 *            the resources to copy to the clipboard
	 * @param fileNames
	 *            file names of the resources to copy to the clipboard
	 * @param names
	 *            string representation of all names
	 */
	private void setClipboard(IResource[] resources, String[] fileNames,
			String names) {
		try {
			// set the clipboard contents
			if (fileNames.length > 0) {
				clipboard.setContents(new Object[] { resources, fileNames,
						names },
						new Transfer[] { ResourceTransfer.getInstance(),
								FileTransfer.getInstance(),
								TextTransfer.getInstance() });
			} else {
				clipboard.setContents(new Object[] { resources, names },
						new Transfer[] { ResourceTransfer.getInstance(),
								TextTransfer.getInstance() });
			}
		} catch (SWTError e) {
			if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD)
				throw e;
		}
	}

	/**
	 * The <code>CopyAction</code> implementation of this
	 * <code>SelectionListenerAction</code> method enables this action if one
	 * or more resources of compatible types are selected.
	 */
	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (!super.updateSelection(selection))
			return false;

		return DragAndCopyUtil.isEnable(selection);
	}


}
