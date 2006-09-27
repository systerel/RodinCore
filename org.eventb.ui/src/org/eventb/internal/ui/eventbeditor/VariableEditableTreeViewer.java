/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.eventb.core.basis.MachineFile;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This sub-class Event-B Editable table viewer for editing variable
 *         elements.
 */
public class VariableEditableTreeViewer extends EventBEditableTreeViewer {

	/**
	 * @author htson
	 *         <p>
	 *         The content provider class.
	 */
	class VariableContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {
		// The invisible root
		private IMachineFile invisibleRoot = null;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object child) {
			if (child instanceof IRodinElement)
				return ((IRodinElement) child).getParent();
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parent) {
			if (parent instanceof IMachineFile) {
				try {
					IRodinElement[] elements = ((IMachineFile) parent)
							.getChildrenOfType(IVariable.ELEMENT_TYPE);
					return elements;
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (parent instanceof IParent) {
				try {
					return ((IParent) parent).getChildren();
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return new Object[0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object parent) {
			return getChildren(parent).length > 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object parent) {
			if (parent instanceof IRodinFile) {
				if (invisibleRoot == null) {
					invisibleRoot = (MachineFile) parent;
					return getChildren(invisibleRoot);
				}
			}
			return getChildren(parent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			invisibleRoot = null;
		}

	}

	public VariableEditableTreeViewer(EventBEditor editor, Composite parent,
			int style) {
		super(editor, parent, style);
		this.setContentProvider(new VariableContentProvider());
		this.setLabelProvider(new EventBTreeLabelProvider(editor, this));
		this.setSorter(new RodinElementSorter());
	}

	public void commit(IRodinElement element, int col, String text) {
		IVariable var = (IVariable) element;
		switch (col) {
		case 0: // Commit name
			try {
				if (EventBEditor.DEBUG)
					EventBEditorUtils.debug("Commit : "
							+ var.getIdentifierString() + " to be : " + text);
				if (!var.getIdentifierString().equals(text)) {
					var.setIdentifierString(text);
				}
			} catch (RodinDBException e) {
				e.printStackTrace();
			}

			break;
		}
	}

	protected void createTreeColumns() {
		numColumn = 1;

		Tree tree = this.getTree();
		TreeColumn elementColumn = new TreeColumn(tree, SWT.LEFT);
		elementColumn.setText("Name");
		elementColumn.setResizable(true);
		elementColumn.setWidth(200);

		tree.setHeaderVisible(true);
	}

	@Override
	protected boolean isNotSelectable(Object object, int column) {
		if (!(object instanceof IRodinElement))
			return true;
		return false;
	}

	protected void edit(IRodinElement element) {
		this.reveal(element);
		TreeItem item = TreeSupports.findItem(this.getTree(), element);
		selectItem(item, 0);
	}

	// public void selectionChanged(SelectionChangedEvent event) {
	// UIUtils.debugEventBEditor("Selection changed: ");
	// IMachineFile file = (IMachineFile) this.editor.getRodinInput();
	// try {
	// IRodinElement[] refines = file
	// .getChildrenOfType(IRefinesMachine.ELEMENT_TYPE);
	// if (refines.length == 1) {
	// IRodinElement refine = refines[0];
	// String name = ((IInternalElement) refine).getContents();
	// IRodinProject prj = file.getRodinProject();
	// IMachineFile refinedFile = (IMachineFile) prj
	// .getRodinFile(EventBPlugin.getMachineFileName(name));
	// UIUtils.debugEventBEditor("Refined: "
	// + refinedFile.getElementName());
	// if (refinedFile.exists()) {
	// IWorkbenchPage activePage = EventBUIPlugin.getActivePage();
	// IEditorReference[] editors = activePage
	// .getEditorReferences();
	// for (IEditorReference editor : editors) {
	// IEditorPart part = editor.getEditor(true);
	// if (part instanceof EventBMachineEditor) {
	// IRodinFile rodinInput = ((EventBMachineEditor) part)
	// .getRodinInput();
	// UIUtils.debugEventBEditor("Trying: "
	// + rodinInput.getElementName());
	// if (rodinInput.equals(refinedFile)) {
	// UIUtils.debugEventBEditor("Focus");
	// if (activePage.isPartVisible(part)) {
	// IStructuredSelection ssel = (IStructuredSelection) event
	// .getSelection();
	// if (ssel.size() == 1) {
	// IInternalElement obj = (IInternalElement) ssel
	// .getFirstElement();
	// IInternalElement element = refinedFile
	// .getInternalElement(obj
	// .getElementType(), obj
	// .getElementName());
	// if (element != null)
	// ((EventBEditor) part)
	// .setSelection(element);
	// }
	// }
	// }
	// }
	// }
	//					
	// editor.setFocus();
	// }
	// }
	// } catch (RodinDBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

}