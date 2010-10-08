/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added default name
 *     Systerel - added renaming of proof files
 *     Systerel - added renaming of occurrences 
 *******************************************************************************/
package org.eventb.internal.ui.projectexplorer.actions;

import static org.eventb.internal.ui.utils.Messages.dialogs_cancelRenaming;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.utils.Messages;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeType.Handle;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.location.IAttributeLocation;
import org.rodinp.core.location.IAttributeSubstringLocation;

public class Renames implements IObjectActionDelegate {

	private ISelection selection;

	private IWorkbenchPart part;

	private String defaultName = "";

	/**
	 * Constructor.
	 */
	public Renames() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
	}

	/**
	 * Return the selected element if it is a {@link IContextRoot} or
	 * {@link IMachineRoot}. Else return null.
	 * */
	private IEventBRoot getSelectedRoot() {
		if (!(selection instanceof IStructuredSelection))
			return null;

		final IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() != 1)
			return null;

		final Object obj = ssel.getFirstElement();
		if (!(obj instanceof IInternalElement))
			return null;

		final IInternalElement root = (IInternalElement) obj;
		if (!(root instanceof IContextRoot || root instanceof IMachineRoot))
			return null;

		return (IEventBRoot) root;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	@Override
	public void run(IAction action) {
		final IEventBRoot root = getSelectedRoot();
		if (root == null)
			return;

		final IRodinFile file = root.getRodinFile();
		final IRodinProject prj = file.getRodinProject();
		final String fileName = getDefaultName(root);

		final RenamesComponentDialog dialog = new RenamesComponentDialog(part
				.getSite().getShell(), fileName, false,
				new RodinFileInputValidator(prj));

		dialog.open();

		final String bareName = dialog.getValue();

		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return; // Cancel

		assert bareName != null;

		launchRename(new RenameRunnable(dialog, prj, file, root, fileName,
					bareName));
	}
	
	private void launchRename(final RenameRunnable rename) {
		UIUtils.runWithProgressDialog(part.getSite().getShell(),
				new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						try {
							RodinCore.run(rename, monitor);
						} catch (RodinDBException e) {
							rename.issueError(e);
						}
					}

				});

	}

	private String getDefaultName(IInternalElement root) {
		if (root instanceof IMachineRoot || root instanceof IContextRoot) {
			return root.getElementName();
		} else {
			return defaultName;
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection sel) {
		this.selection = sel;
	}

	private static class RenameRunnable implements IWorkspaceRunnable {

		private final RenamesComponentDialog dialog;
		private final IRodinProject prj;
		private final IRodinFile file;
		private final IInternalElement root;
		private final String fileName;
		private final String bareName;

		public RenameRunnable(RenamesComponentDialog dialog, IRodinProject prj,
				IRodinFile file, IInternalElement root, String fileName,
				String bareName) {
			this.dialog = dialog;
			this.prj = prj;
			this.file = file;
			this.root = root;
			this.fileName = fileName;
			this.bareName = bareName;
		}

		@Override
		public void run(IProgressMonitor monitor) throws RodinDBException {
			final Set<IOccurrence> occurences = getOccurences();
			renameComponentFile(monitor);
			renamePRFile(monitor);
			renameInOccurences(occurences, monitor);
		}

		public void issueError(Throwable t) {
			UIUtils.showUnexpectedError(t, Messages.uiUtils_unexpectedError);
			UIUtils.log(t, "while renaming " + fileName);
		}

		public boolean cancelRenaming(String newName) {
			return UIUtils.showQuestion(dialogs_cancelRenaming(newName));
		}

		private Set<IOccurrence> getOccurences() {
			if (dialog.updateReferences()) {
				try {
					final IIndexQuery query = RodinCore.makeIndexQuery();
					query.waitUpToDate();
					final IDeclaration i = query.getDeclaration(root);
					return query.getOccurrences(i);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return Collections.emptySet();
				}
			} else {
				return Collections.emptySet();
			}
		}

		private void renameComponentFile(IProgressMonitor monitor)
				throws RodinDBException {
			final String newName;
			if (root instanceof IContextRoot) {
				newName = EventBPlugin.getContextFileName(bareName);
			} else {
				// root is an instance of IMachineRoot
				newName = EventBPlugin.getMachineFileName(bareName);
			}
			file.rename(newName, false, monitor);
		}

		private void renamePRFile(IProgressMonitor monitor)
				throws RodinDBException {
			final IEventBProject evbProject = (IEventBProject) prj
					.getAdapter(IEventBProject.class);
			final IRodinFile proofFile = evbProject.getPRFile(root
					.getElementName());
			final String newName = EventBPlugin.getPRFileName(bareName);
			final IRodinFile pRFile = evbProject.getPRFile(bareName);
			if (pRFile.exists()) {
				if (cancelRenaming(bareName)) {
					return;
				}
				proofFile.rename(newName, true, monitor);
			} else {
				proofFile.rename(newName, false, monitor);
			}
		}

		/**
		 * Replace the occurrence of old file name specified by location by the
		 * new name
		 * 
		 * @throws RodinDBException
		 * */
		private void replaceStringOccurence(String oldName, String newName,
				IAttributeLocation location, IAttributeType.String type,
				IProgressMonitor monitor) throws RodinDBException {
			final IInternalElement element = location.getElement();
			final String attribute = element.getAttributeValue(type);

			// new value of attribute
			final String newAttribute;
			// value of occurence specified by location
			final String occurence;
			if (location instanceof IAttributeSubstringLocation) {
				// the new value of attribute is result from replacing
				// the occurence by newName in old value of attribute
				final IAttributeSubstringLocation subStringLoc = (IAttributeSubstringLocation) location;
				final int start = subStringLoc.getCharStart();
				final int end = subStringLoc.getCharEnd();
				occurence = attribute.substring(start, end);
				newAttribute = attribute.substring(0, start) + newName
						+ attribute.substring(start);
			} else {
				occurence = attribute;
				newAttribute = newName;
			}

			if (occurence.equals(oldName)) {
				element.setAttributeValue(type, newAttribute, monitor);
				element.getRodinFile().save(monitor, false);
			}
		}

		/**
		 * Replace the occurrence of old handle specified by location by the new
		 * handle
		 * 
		 * @throws RodinDBException
		 * */
		private void replaceHandleOccurence(IAttributeLocation loc,
				Handle type, IProgressMonitor monitor) throws RodinDBException {
			final IInternalElement element = loc.getElement();
			final IRodinElement attribute = element.getAttributeValue(type);
			if (attribute.equals(file)) {
				element.setAttributeValue(type, root.getSimilarElement(file),
						monitor);
				element.getRodinFile().save(monitor, false);
			}
		}

		private void renameInOccurences(Set<IOccurrence> occurences,
				IProgressMonitor monitor) throws RodinDBException {
			for (IOccurrence occ : occurences) {
				if (occ.getLocation() instanceof IAttributeLocation) {
					final IAttributeLocation loc = (IAttributeLocation) occ
							.getLocation();
					final IAttributeType type = loc.getAttributeType();
					if (type instanceof IAttributeType.String) {
						replaceStringOccurence(fileName, bareName, loc,
								(IAttributeType.String) type, monitor);
					} else if (type instanceof IAttributeType.Handle) {
						replaceHandleOccurence(loc,
								(IAttributeType.Handle) type, monitor);
					}
				}
			}
		}
	}
	
}
