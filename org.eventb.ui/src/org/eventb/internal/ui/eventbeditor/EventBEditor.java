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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

/**
 * @author htson
 *         <p>
 *         Abstract Event-B specific form editor for machines, contexts.
 */
public abstract class EventBEditor extends FormEditor implements
		IElementChangedListener, IGotoMarker, IEventBEditor {

	private String lastActivePageID = null;

	/**
	 * @author htson
	 *         <p>
	 *         This override the Selection Provider and redirect the provider to
	 *         the current active page of the editor.
	 */
	private static class FormEditorSelectionProvider implements
			ISelectionProvider {
		private ISelection globalSelection;

		private ListenerList listeners;

		private FormEditor formEditor;

		/**
		 * Constructor.
		 * <p>
		 * 
		 * @param formEditor
		 *            the Form Eidtor contains this provider.
		 */
		public FormEditorSelectionProvider(FormEditor formEditor) {
			listeners = new ListenerList();
			this.formEditor = formEditor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
		 */
		public ISelection getSelection() {
			IFormPage activePage = formEditor.getActivePageInstance();
			if (activePage != null) {
				if (activePage instanceof EventBFormPage) {
					EventBPartWithButtons part = ((EventBFormPage) activePage)
							.getPart();
					if (part != null) {
						ISelectionProvider selectionProvider = part.getViewer();
						if (selectionProvider != null)
							if (selectionProvider != this)
								return selectionProvider.getSelection();
					}
				}
			}
			return globalSelection;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
		 */
		public void setSelection(ISelection selection) {
			IFormPage activePage = formEditor.getActivePageInstance();
			if (activePage != null) {
				ISelectionProvider selectionProvider = activePage.getSite()
						.getSelectionProvider();
				if (selectionProvider != null) {
					if (selectionProvider != this)
						selectionProvider.setSelection(selection);
				}
			} else {
				this.globalSelection = selection;
				fireSelectionChanged(new SelectionChangedEvent(this,
						globalSelection));
			}
		}

		/**
		 * Notifies all registered selection changed listeners that the editor's
		 * selection has changed. Only listeners registered at the time this
		 * method is called are notified.
		 * 
		 * @param event
		 *            the selection changed event
		 */
		public void fireSelectionChanged(final SelectionChangedEvent event) {
			Object[] listeners = this.listeners.getListeners();
			for (int i = 0; i < listeners.length; ++i) {
				final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
				Platform.run(new SafeRunnable() {
					public void run() {
						l.selectionChanged(event);
					}
				});
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void addSelectionChangedListener(
				ISelectionChangedListener listener) {
			listeners.add(listener);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
			listeners.remove(listener);
		}
	}

	// The outline page
	private EventBContentOutlinePage fOutlinePage;

	// The associated rodin file handle
	private IRodinFile rodinFile = null;

	// List of Element Changed listeners for the editor.
	private Collection<IElementChangedListener> listeners;

	// Collection of new elements (unsaved).
	private Collection<IRodinElement> newElements;

	// List of status changed listener (when elements are saved).
	private Collection<IStatusChangedListener> statusListeners;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEventBEditor#addNewElement(org.rodinp.core.IRodinElement)
	 */
	public void addNewElement(IRodinElement element) {
		newElements.add(element);
		notifyStatusChanged(element);
	}

	// Notify a status change of an element
	private void notifyStatusChanged(IRodinElement element) {
		for (IStatusChangedListener listener : statusListeners) {
			listener.statusChanged(element);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEventBEditor#addStatusListener(org.eventb.internal.ui.eventbeditor.IStatusChangedListener)
	 */
	public void addStatusListener(IStatusChangedListener listener) {
		statusListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEventBEditor#removeStatusListener(org.eventb.internal.ui.eventbeditor.IStatusChangedListener)
	 */
	public void removeStatusListener(IStatusChangedListener listener) {
		statusListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEventBEditor#isNewElement(org.rodinp.core.IRodinElement)
	 */
	public boolean isNewElement(IRodinElement element) {
		return newElements.contains(element);
	}

	/**
	 * Constructor.
	 */
	public EventBEditor() {
		super();

		listeners = new ArrayList<IElementChangedListener>();
		newElements = new HashSet<IRodinElement>();
		statusListeners = new HashSet<IStatusChangedListener>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEventBEditor#addElementChangedListener(org.rodinp.core.IElementChangedListener)
	 */
	public void addElementChangedListener(IElementChangedListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener))
				listeners.add(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEventBEditor#removeElementChangedListener(org.rodinp.core.IElementChangedListener)
	 */
	public void removeElementChangedListener(IElementChangedListener listener) {
		synchronized (listeners) {
			if (listeners.contains(listener))
				listeners.remove(listener);
		}
	}

	/**
	 * Notified the element changed.
	 * <p>
	 * 
	 * @param delta
	 *            Rodin Element Delta which is related to the current editting
	 *            file
	 */
	private void notifyElementChangedListeners(final IRodinElementDelta delta) {
		IElementChangedListener[] safeCopy;
		synchronized (listeners) {
			safeCopy = listeners.toArray(new IElementChangedListener[listeners
					.size()]);
		}
		for (final IElementChangedListener listener : safeCopy) {
			Platform.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// do nothing, will be logged by the platform
				}

				public void run() throws Exception {
					listener.elementChanged(new ElementChangedEvent(delta,
							ElementChangedEvent.POST_CHANGE));
				}
			});
		}
	}

	/*
	 * (non-Javadoc) Overrides super to plug in a different selection provider.
	 * 
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		RodinCore.addElementChangedListener(this);
		site.setSelectionProvider(new FormEditorSelectionProvider(this));
		IRodinFile rodinFile = this.getRodinInput();

		this.setPartName(EventBPlugin.getComponentName(rodinFile
				.getElementName()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
	 */
	public boolean isDirty() {
		try {
			return this.getRodinInput().hasUnsavedChanges();
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return super.isDirty();
	}

	/**
	 * The <code>EventBMachineEditor</code> implementation of this
	 * <code>AbstractTextEditor</code> method performs any extra disposal
	 * actions required by the Event-B editor.
	 */
	public void dispose() {
		if (EventBEditorUtils.DEBUG)
			EventBEditorUtils.debug("Dispose");
		if (fOutlinePage != null)
			fOutlinePage.setInput(null);

		saveDefaultPage();

		try { // Make the associated RodinFile consistent if it is has some
				// unsaved change
			IRodinFile rodinInput = this.getRodinInput();
			if (rodinInput.hasUnsavedChanges())
				rodinInput.makeConsistent(new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Log the error
			e.printStackTrace();
		}
		RodinCore.removeElementChangedListener(this);
		super.dispose();
	}

	/**
	 * Saving the default page for the next open.
	 */
	private void saveDefaultPage() {
		IRodinFile inputFile = this.getRodinInput();
		try {
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Save Page: " + lastActivePageID);
			if (lastActivePageID != null)
				inputFile.getResource().setPersistentProperty(
						new QualifiedName(EventBUIPlugin.PLUGIN_ID,
								"default-editor-page"), lastActivePageID);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#createPages()
	 */
	@Override
	protected void createPages() {
		super.createPages();
		loadDefaultPage();
	}

	/**
	 * Load the default page (read from the persistent property
	 * <code>default-editor-page</code>.
	 */
	protected void loadDefaultPage() {
		IRodinFile inputFile = this.getRodinInput();
		try {
			String pageID = inputFile.getResource().getPersistentProperty(
					new QualifiedName(EventBUIPlugin.PLUGIN_ID,
							"default-editor-page"));
			if (pageID != null)
				this.setActivePage(pageID);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The <code>EventBMachineEditor</code> implementation of this method
	 * performs gets the content outline page if request is for a an outline
	 * page.
	 * <p>
	 * 
	 * @param required
	 *            the required type
	 *            <p>
	 * @return an adapter for the required type or <code>null</code>
	 */
	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null) {
				fOutlinePage = new EventBContentOutlinePage(this);
				if (getEditorInput() != null)
					fOutlinePage.setInput(getRodinInput());
			}
			return fOutlinePage;
		}

		return super.getAdapter(required);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		// TODO Do save as
		MessageDialog.openInformation(null, null, "Saving");
		// EventBFormPage editor = (EventBFormPage) this.getEditor(0);
		// editor.doSaveAs();
		// IEditorPart editor = getEditor(0);
		// editor.doSaveAs();
		// setPageText(0, editor.getTitle());
		// setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		try {
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Save");
			if (this.pages != null) {
				for (int i = 0; i < pages.size(); i++) {
					Object page = pages.get(i);
					if (page instanceof IFormPage) {
						IFormPage fpage = (IFormPage) page;
						if (fpage.isDirty()) {
							if (EventBEditorUtils.DEBUG)
								EventBEditorUtils.debug("Saving "
										+ fpage.toString());
							fpage.doSave(monitor);
						}
					}
				}
			}

			// Save the file from the database to file
			// Collection<IRodinElement> originals = new
			// HashSet<IRodinElement>();
			//
			// for (Iterator<IRodinElement> it = newElements.iterator(); it
			// .hasNext();) {
			// IRodinElement element = it.next();
			// UIUtils.debugEventBEditor("New element: "
			// + element.getElementName());
			// if (isOriginal(element)) {
			// originals.add(element);
			// ((IInternalElement) element).delete(true, null);
			// }
			// }
			// newElements.removeAll(originals);

			IRodinFile inputFile = this.getRodinInput();
			inputFile.save(monitor, true);

			while (!newElements.isEmpty()) {
				IRodinElement element = (IRodinElement) newElements.toArray()[0];
				newElements.remove(element);
				notifyStatusChanged(element);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}

		editorDirtyStateChanged(); // Refresh the dirty state of the editor
	}

	/**
	 * Checking if a Rodin element is "original" (created automatically, but is
	 * not modified).
	 * <p>
	 * 
	 * @param element
	 *            a Rodin element
	 * @return <code>true</code> if the element has default created values.
	 *         <code>false</code> otherwise.
	 */
	// private boolean isOriginal(IRodinElement element) {
	// if (element instanceof IGuard) {
	// try {
	// if (((IGuard) element).getContents().equals(
	// EventBUIPlugin.GRD_DEFAULT)) {
	// return true;
	// }
	// } catch (RodinDBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// if (element instanceof IAction) {
	// try {
	// if (((IAction) element).getContents().equals(
	// EventBUIPlugin.SUB_DEFAULT)) {
	// return true;
	// }
	// } catch (RodinDBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// return false;
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEventBEditor#edit(java.lang.Object)
	 */
	public void edit(Object ssel) {
		if (ssel instanceof IRodinElement) {
			elementEdit((IRodinElement) ssel);
			return;
		}

		if (ssel instanceof TreeNode) { // For tree node, just select the node.
			setTreeNodeSelection((TreeNode) ssel);
			return;
		}
		return;
	}

	/**
	 * Set the selection in the editor if the input is a TreeNode.
	 * <p>
	 * 
	 * @param node
	 *            instance of TreeNode
	 */
	private void setTreeNodeSelection(TreeNode node) {
		if (node.isType(IVariable.ELEMENT_TYPE)) {
			this.setActivePage(VariablePage.PAGE_ID);
			return;
		}
		if (node.isType(IInvariant.ELEMENT_TYPE)) {
			this.setActivePage(InvariantPage.PAGE_ID);
			return;
		}
		if (node.isType(ITheorem.ELEMENT_TYPE)) {
			this.setActivePage(TheoremPage.PAGE_ID);
			return;
		}
		if (node.isType(IEvent.ELEMENT_TYPE)) {
			this.setActivePage(EventPage.PAGE_ID);
			return;
		}
		if (node.isType(ICarrierSet.ELEMENT_TYPE)) {
			this.setActivePage(CarrierSetPage.PAGE_ID);
			return;
		}
		if (node.isType(IConstant.ELEMENT_TYPE)) {
			this.setActivePage(ConstantPage.PAGE_ID);
			return;
		}
		if (node.isType(IAxiom.ELEMENT_TYPE)) {
			this.setActivePage(AxiomPage.PAGE_ID);
			return;
		}
		return;
	}

	/**
	 * Set the selection in the editor if the input is a Rodin element.
	 * <p>
	 * 
	 * @param node
	 *            instance of IRodinElement
	 */
	private void elementEdit(IRodinElement element) {
		if (element instanceof IMachineFile)
			return;

		if (element instanceof IContextFile)
			return;

		if (element instanceof ISeesContext) {
			this.setActivePage(DependenciesPage.PAGE_ID);
			return;
		}

		if (element instanceof IAxiom) {
			this.setActivePage(AxiomPage.PAGE_ID);
		}

		else if (element instanceof ITheorem) {
			this.setActivePage(TheoremPage.PAGE_ID);
		}

		else if (element instanceof ICarrierSet) {
			this.setActivePage(CarrierSetPage.PAGE_ID);
		}

		else if (element instanceof IConstant)
			this.setActivePage(ConstantPage.PAGE_ID);

		else if (element instanceof IInvariant)
			this.setActivePage(InvariantPage.PAGE_ID);

		else if (element instanceof IEvent)
			this.setActivePage(EventPage.PAGE_ID);

		else if (element instanceof IVariable) {
			if (element.getParent() instanceof IMachineFile)
				this.setActivePage(VariablePage.PAGE_ID);
			else
				this.setActivePage(EventPage.PAGE_ID);
		}

		else if (element instanceof IGuard) {
			this.setActivePage(EventPage.PAGE_ID);
		}

		else if (element instanceof IAction) {
			this.setActivePage(EventPage.PAGE_ID);
		}

		else if (element instanceof IRefinesEvent) {
			this.setActivePage(EventPage.PAGE_ID);
		}

		else if (element instanceof IWitness) {
			this.setActivePage(EventPage.PAGE_ID);
		}

		// select the element within the page
		IFormPage page = this.getActivePageInstance();
		if (page instanceof EventBFormPage) {
			((EventBFormPage) page).edit(element);
		}

	}

	/**
	 * Getting the RodinFile associated with this editor.
	 * <p>
	 * 
	 * @return a handle to a Rodin file
	 */
	public IRodinFile getRodinInput() {
		if (rodinFile == null) {
			FileEditorInput editorInput = (FileEditorInput) this
					.getEditorInput();

			IFile inputFile = editorInput.getFile();

			rodinFile = (IRodinFile) RodinCore.create(inputFile);
		}
		return rodinFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		processDelta(delta);
	}

	/**
	 * Process the delta recursively until finding the delta that is related to
	 * the Rodin file associated with this editor.
	 * <p>
	 * 
	 * @param delta
	 *            a Rodin Element Delta
	 */
	private void processDelta(IRodinElementDelta delta) {
		IRodinElement element = delta.getElement();

		if (element instanceof IRodinDB) {
			IRodinElementDelta[] deltas = delta.getAffectedChildren();
			for (int i = 0; i < deltas.length; i++) {
				processDelta(deltas[i]);
			}
			return;
		}
		if (element instanceof IRodinProject) {
			IRodinProject prj = (IRodinProject) element;
			if (!this.getRodinInput().getParent().equals(prj)) {
				return;
			}
			IRodinElementDelta[] deltas = delta.getAffectedChildren();
			for (int i = 0; i < deltas.length; i++) {
				processDelta(deltas[i]);
			}
			return;
		}

		if (element instanceof IRodinFile) {
			if (!this.getRodinInput().equals(element)) {
				return;
			}
			notifyElementChangedListeners(delta);
			Display display = Display.getDefault();
			display.syncExec(new Runnable() {
				public void run() {
					editorDirtyStateChanged();
				}
			});
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#pageChange(int)
	 */
	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		IFormPage page = getActivePageInstance();
		if (page != null)
			lastActivePageID = page.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEventBEditor#setSelection(org.rodinp.core.IInternalElement)
	 */
	public void setSelection(IInternalElement element) {
		if (element instanceof IRodinElement) {
			elementSelect((IRodinElement) element);
			return;
		}
	}

	private void elementSelect(IRodinElement element) {
		if (element instanceof IMachineFile)
			return;

		if (element instanceof IContextFile)
			return;

		if (element instanceof ISeesContext) {
			this.setActivePage(DependenciesPage.PAGE_ID);
			return;
		}

		if (element instanceof IAxiom) {
			this.setActivePage(AxiomPage.PAGE_ID);
		}

		else if (element instanceof ITheorem) {
			this.setActivePage(TheoremPage.PAGE_ID);
		}

		else if (element instanceof ICarrierSet) {
			this.setActivePage(CarrierSetPage.PAGE_ID);
		}

		else if (element instanceof IConstant)
			this.setActivePage(ConstantPage.PAGE_ID);

		else if (element instanceof IInvariant)
			this.setActivePage(InvariantPage.PAGE_ID);

		else if (element instanceof IEvent)
			this.setActivePage(EventPage.PAGE_ID);

		else if (element instanceof IVariable) {
			if (element.getParent() instanceof IMachineFile)
				this.setActivePage(VariablePage.PAGE_ID);
			else
				this.setActivePage(EventPage.PAGE_ID);
		}

		else if (element instanceof IGuard) {
			this.setActivePage(EventPage.PAGE_ID);
		}

		else if (element instanceof IAction) {
			this.setActivePage(EventPage.PAGE_ID);
		}

		else if (element instanceof IRefinesEvent) {
			this.setActivePage(EventPage.PAGE_ID);
		}
		
		else if (element instanceof IWitness) {
			this.setActivePage(EventPage.PAGE_ID);
		}
		
		// select the element within the page
		IFormPage page = this.getActivePageInstance();
		if (page instanceof EventBFormPage) {
			((EventBFormPage) page).selectElement(element);
		}

	}

	public void gotoMarker(IMarker marker) {
		IInternalElement element;
		try {
			element = RodinMarkerUtil.getElement(marker);
		}
		catch (IllegalArgumentException e) {
			if (EventBEditorUtils.DEBUG) {
				EventBEditorUtils.debug("Not a Rodin Marker");
				e.printStackTrace();
			}
			return;
		}
		if (element != null) {
			this.edit(element);
		}
	}

}
