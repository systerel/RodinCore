/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - redirected dialog opening and externalized strings
 *     Systerel - fixed Rodin errors when input does not exist
 *     Systerel - used eclipse decorator mechanism
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import static org.eventb.internal.ui.utils.Messages.dialogs_canNotGetChildren;
import static org.eventb.internal.ui.utils.Messages.title_error;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eventb.internal.ui.RodinElementTreeLabelProvider;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.ElementSorter;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         A content outline page which always represents the content of the
 *         connected editor (machines, models, etc.) in segments.
 */
public class EventBContentOutlinePage extends ContentOutlinePage {

	/**
	 * @author htson
	 *         <p>
	 *         This is the content provider class for the tree display in the
	 *         outline page.
	 */
	class EventBContentOutlineProvider implements ITreeContentProvider,
			IElementChangedListener {
		// The invisible root of the tree (should be the current editting file).
		private IInternalElement invisibleRoot = null;

		/*
		 * (non-Javadoc) When the input is change, reset the invisible root to
		 * null.
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (oldInput == null && newInput != null)
				RodinCore.addElementChangedListener(this);
			else if (oldInput != null && newInput == null)
				RodinCore.removeElementChangedListener(this);

			invisibleRoot = null;
			return;
		}

		/*
		 * (non-Javadoc) Getting the list of elements, setting the invisible
		 * root if neccesary.
		 * 
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object parent) {
			if (parent instanceof IRodinFile) {
				if (invisibleRoot == null)
					invisibleRoot = fEditor.getRodinInput();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		@Override
		public Object getParent(Object child) {
			if (child instanceof IRodinElement) {
				return ((IRodinElement) child).getParent();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object obj) {
			if (obj instanceof IParent) {
				final IParent parent = (IParent) obj;
				try {
					return parent.getChildren();
				} catch (RodinDBException e) {
					e.printStackTrace();
					EventBMachineEditorContributor.sampleAction.refreshAll();
					UIUtils.showError(title_error,
							dialogs_canNotGetChildren(parent));
				}
			}
			return new Object[0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object obj) {
			if (obj instanceof IParent) {
				final IParent parent = (IParent) obj;
				try {
					return parent.hasChildren();
				} catch (RodinDBException e) {
					UIUtils.showError(title_error,
							dialogs_canNotGetChildren(parent));
					e.printStackTrace();
				}
			}
			return false;
		}

		/**
		 * This method implements the listener method when there is a change in
		 * the Rodin database
		 * <p>
		 * 
		 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
		 */
		@Override
		public void elementChanged(ElementChangedEvent event) {
			// TODO Process the delta increamentally, see
			// ObligationExplorerContentProvider
			IRodinElementDelta[] elements = event.getDelta()
					.getAffectedChildren();
			for (int i = 0; i < elements.length; i++) {
				if ((elements[i].getKind() & IRodinElementDelta.ADDED) != 0) {
					// TODO Element added
				} else if ((elements[i].getKind() & IRodinElementDelta.REMOVED) != 0) {
					// TODO Element removed
				} else if ((elements[i].getKind() & IRodinElementDelta.CHANGED) != 0) {
					// TODO Element chaged
				}
				UIUtils.syncPostRunnable(new Runnable() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run() {
						update();
					}
				}, EventBContentOutlinePage.this.getControl());
			}
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}
	}

	// The currently edited element.
	private IInternalElement fInput;

	// The current associated editor.
	IEventBEditor<?> fEditor;

	/**
	 * Creates a content outline page using the given editor. Register as a
	 * change listener for the Rodin Database.
	 * <p>
	 * 
	 * @param editor
	 *            the editor
	 */
	public EventBContentOutlinePage(IEventBEditor<?> editor) {
		super();
		fEditor = editor;
	}

	/**
	 * Method declared on ContentOutlinePage. Create the tree content of the
	 * page.
	 * <p>
	 * 
	 * @param parent
	 *            the parent composite of the control
	 */
	@Override
	public void createControl(Composite parent) {

		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new EventBContentOutlineProvider());
		viewer.setSorter(new ElementSorter());
		final ILabelDecorator decorator = PlatformUI.getWorkbench()
		.getDecoratorManager().getLabelDecorator();
		viewer.setLabelProvider(new DecoratingLabelProvider(
				new RodinElementTreeLabelProvider(viewer), decorator));
		viewer.addSelectionChangedListener(this);

		if (fInput != null)
			viewer.setInput(fInput);
	}

	/**
	 * Method declared on ContentOutlinePage. This is called when there is a
	 * selection change in the tree. This responses by selecting the object
	 * element of the selection in the editor.
	 * <p>
	 * 
	 * @param event
	 *            the selection event
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {

		ISelection selection = event.getSelection();
		if (!(selection.isEmpty())) {
			Object ssel = ((IStructuredSelection) selection).getFirstElement();
			fEditor.getSite().getSelectionProvider().setSelection(
					new StructuredSelection(ssel));
		}

		super.selectionChanged(event);
	}

	/**
	 * Sets the input of the outline page
	 * <p>
	 * 
	 * @param input
	 *            the input of this outline page
	 */
	public void setInput(IInternalElement input) {
		fInput = input;
		update();
	}

	/**
	 * Updates the outline page. Remember the previous expand states.
	 */
	public void update() {
		TreeViewer viewer = getTreeViewer();
		if (viewer != null) {
			Control control = viewer.getControl();
			if (control != null && !control.isDisposed()) {
				control.setRedraw(false);

				// Saving the expanded elements
				Object[] elements = viewer.getExpandedElements();
				viewer.setInput(fInput.exists() ? fInput : null);
				viewer.setExpandedElements(elements);
				control.setRedraw(true);
			}
		}
	}

}
