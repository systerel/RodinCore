/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
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

import static org.eclipse.core.runtime.SubMonitor.convert;
import static org.eventb.internal.ui.utils.Messages.dialogs_cancelRenaming;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
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

		final IRodinProject prj = root.getRodinProject();
		final String fileName = root.getElementName();
		final Shell shell = part.getSite().getShell();

		final RenamesComponentDialog dialog = new RenamesComponentDialog(shell,
				fileName, true, new RodinFileInputValidator(prj));
		dialog.open();
		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return; // Cancel

		final String newBareName = dialog.getValue();
		assert newBareName != null;

		UIUtils.runWithProgressDialog(shell, new RenameTask(root, newBareName,
				dialog.updateReferences()));
	}

	private static class RenameTask implements IRunnableWithProgress {

		private static final Set<IOccurrence> NO_OCCURRENCES = Collections
				.emptySet();

		private final IEventBRoot root;
		private final String newBareName;
		private final boolean updateReferences;

		public RenameTask(IEventBRoot root, String newBareName,
				boolean updateReferences) {
			this.root = root;
			this.newBareName = newBareName;
			this.updateReferences = updateReferences;
		}

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			final String msg = Messages.renameElement(root.getElementName(),
					newBareName);
			final SubMonitor subMonitor = convert(monitor, msg, 2 + 8);
			final Set<IOccurrence> occurrences = getOccurrences(subMonitor
					.newChild(2));
			if (monitor.isCanceled())
				return;
			final IWorkspaceRunnable op = new RenameOperation(root,
					newBareName, occurrences);
			try {
				RodinCore.run(op, subMonitor.newChild(8));
			} catch (RodinDBException e) {
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();
			}
		}

		private Set<IOccurrence> getOccurrences(SubMonitor monitor)
				throws InterruptedException {
			monitor.subTask(Messages.rename_task_wait_for_indexer);
			monitor.worked(1);
			if (!updateReferences) {
				return NO_OCCURRENCES;
			}
			final IIndexQuery query = RodinCore.makeIndexQuery();
			query.waitUpToDate(monitor);
			if (monitor.isCanceled())
				return NO_OCCURRENCES;
			final IDeclaration decl = query.getDeclaration(root);
			if (decl == null) {
				return NO_OCCURRENCES;
			}
			return query.getOccurrences(decl);
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection sel) {
		this.selection = sel;
	}

	private static class RenameOperation implements IWorkspaceRunnable {

		private final Set<IOccurrence> occurences;
		private final IRodinProject prj;
		private final IRodinFile file;
		private final IEventBRoot root;
		private final String fileName;
		private final String newBareName;

		public RenameOperation(IEventBRoot root, String newBareName,
				Set<IOccurrence> occurences) {
			this.occurences = occurences;
			this.prj = root.getRodinProject();
			this.file = root.getRodinFile();
			this.root = root;
			this.fileName = root.getElementName();
			this.newBareName = newBareName;
		}

		@Override
		public void run(IProgressMonitor monitor) throws RodinDBException {
			monitor.subTask(Messages.rename_task_perform_renaming);
			 // Two default renamings +  one per occurence to rename
			final int nbOccurrences = occurences.size();
			final SubMonitor subMonitor = convert(monitor, 2 + nbOccurrences);
			renameComponentFile(subMonitor.newChild(1));
			renamePRFile(subMonitor.newChild(1));
			renameInOccurences(subMonitor.newChild(nbOccurrences));
		}

		public boolean cancelRenaming(String newName) {
			return UIUtils.showQuestion(dialogs_cancelRenaming(newName));
		}

		private void renameComponentFile(SubMonitor monitor)
				throws RodinDBException {
			final String newName;
			if (root instanceof IContextRoot) {
				newName = EventBPlugin.getContextFileName(newBareName);
			} else {
				// root is an instance of IMachineRoot
				newName = EventBPlugin.getMachineFileName(newBareName);
			}
			file.rename(newName, false, monitor);
		}

		private void renamePRFile(IProgressMonitor monitor)
				throws RodinDBException {
			final IEventBProject evbProject = (IEventBProject) prj
					.getAdapter(IEventBProject.class);
			final IRodinFile proofFile = evbProject.getPRFile(root
					.getElementName());
			final String newName = EventBPlugin.getPRFileName(newBareName);
			final IRodinFile pRFile = evbProject.getPRFile(newBareName);
			if (pRFile.exists()) {
				if (cancelRenaming(newBareName)) {
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

		private void renameInOccurences(SubMonitor monitor)
				throws RodinDBException {
			for (IOccurrence occ : occurences) {
				if (occ.getLocation() instanceof IAttributeLocation) {
					final IAttributeLocation loc = (IAttributeLocation) occ
							.getLocation();
					final IAttributeType type = loc.getAttributeType();
					if (type instanceof IAttributeType.String) {
						replaceStringOccurence(fileName, newBareName, loc,
								(IAttributeType.String) type, monitor);
					} else if (type instanceof IAttributeType.Handle) {
						replaceHandleOccurence(loc,
								(IAttributeType.Handle) type, monitor);
					}
				}
				monitor.worked(1);
			}
		}
	}

}
