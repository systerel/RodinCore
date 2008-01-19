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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eventb.internal.ui.eventbeditor.editpage.EditPage;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IAttributeType;
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
public abstract class EventBEditor<F extends IRodinFile> extends FormEditor
		implements IElementChangedListener, IEventBEditor<F>,
		ITabbedPropertySheetPageContributor {

	// The last active page id before closing.
	private String lastActivePageID = null;

	// The context activation.
	IContextActivation contextActivation; 
	
	/**
	 * @author htson
	 *         <p>
	 *         This override the Selection Provider and redirect the provider to
	 *         the current active page of the editor.
	 */
	private static class FormEditorSelectionProvider implements
			ISelectionProvider {
		private ISelection globalSelection;

		private ListenerList listenerList;

		private FormEditor formEditor;

		/**
		 * Constructor.
		 * <p>
		 * 
		 * @param formEditor
		 *            the Form Editor contains this provider.
		 */
		public FormEditorSelectionProvider(FormEditor formEditor) {
			listenerList = new ListenerList();
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
				if (activePage instanceof ISelectionProvider)
					return ((ISelectionProvider) activePage).getSelection();
			}
			return globalSelection;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
		 */
		public void setSelection(ISelection selection) {
			// Pass the selection to Edit Page
			formEditor.setActivePage(EditPage.PAGE_ID);
			IFormPage page = formEditor.getActivePageInstance();
			if (page instanceof EditPage) {
				((EditPage) page).setSelection(selection);
			}
			this.globalSelection = selection;
			fireSelectionChanged(new SelectionChangedEvent(this,
					globalSelection));
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
			Object[] listeners = this.listenerList.getListeners();
			for (int i = 0; i < listeners.length; ++i) {
				final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
				SafeRunner.run(new SafeRunnable() {
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
			listenerList.add(listener);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
			listenerList.remove(listener);
		}
	}

	// The outline page
	private EventBContentOutlinePage fOutlinePage;

	// The associated rodin file handle
	private F rodinFile;

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
			SafeRunner.run(new ISafeRunnable() {
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
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		site.setSelectionProvider(new FormEditorSelectionProvider(this));
		RodinCore.addElementChangedListener(this);
		rodinFile = getRodinFile(input);
		setPartName(rodinFile.getBareName());
		
		// Activate Event-B Editor Context
		IContextService contextService = (IContextService) getSite()
				.getService(IContextService.class);
		contextActivation = contextService
				.activateContext(EventBUIPlugin.PLUGIN_ID
						+ ".contexts.eventBEditorScope");
	}
	

	protected abstract F getRodinFile(IEditorInput input);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
	 */
	@Override
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
	@Override
	public void dispose() {
		if (EventBEditorUtils.DEBUG)
			EventBEditorUtils.debug("Dispose");
		if (fOutlinePage != null)
			fOutlinePage.setInput(null);
		// De-activate the Event-B Editor context
		if (contextActivation != null) {
			IContextService contextService = (IContextService) getSite()
					.getService(IContextService.class);
			contextService.deactivateContext(contextActivation);
		}

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
	@Override
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null) {
				fOutlinePage = new EventBContentOutlinePage(this);
				if (getEditorInput() != null)
					fOutlinePage.setInput(getRodinInput());
			}
			return fOutlinePage;
		}

		if (IPropertySheetPage.class.equals(required)) {
			return new TabbedPropertySheetPage(this);
		}
		return super.getAdapter(required);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@Override
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
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IEventBEditor#edit(java.lang.Object)
	 */
	@Deprecated
	public void edit(Object ssel) {
		this.getSite().getSelectionProvider().setSelection(
				new StructuredSelection(ssel));
		return;
	}

	public F getRodinInput() {
		if (rodinFile == null)
			throw new IllegalStateException(
					"Editor hasn't been initialized yet");
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
	@Deprecated
	public void setSelection(IInternalElement element) {
		this.setActivePage(EditPage.PAGE_ID);
		// select the element within the page
		IFormPage page = this.getActivePageInstance();
		if (page instanceof EditPage) {
			((EditPage) page).select(element, true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ide.IGotoMarker#gotoMarker(org.eclipse.core.resources.IMarker)
	 */
	public void gotoMarker(IMarker marker) {
		IInternalElement element;
		try {
			element = RodinMarkerUtil.getInternalElement(marker);
		} catch (IllegalArgumentException e) {
			if (EventBEditorUtils.DEBUG) {
				EventBEditorUtils.debug("Not a Rodin Marker");
				e.printStackTrace();
			}
			return;
		}
		if (element != null) {
			IAttributeType attributeType = RodinMarkerUtil
					.getAttributeType(marker);
			int charStart = RodinMarkerUtil.getCharStart(marker);
			int charEnd = RodinMarkerUtil.getCharEnd(marker);
			this.edit(element, attributeType, charStart, charEnd);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * Utility method for editing an element. This will be handle by the Edit
	 * page.
	 * 
	 * @param element
	 *            an internal element.
	 * @param attributeType
	 *            the attribute to be edited for the input element.
	 * @param charStart
	 *            the start character position.
	 * @param charEnd
	 *            the end character position.
	 */
	private void edit(IInternalElement element, IAttributeType attributeType,
			int charStart, int charEnd) {
		// select the element within the edit page
		IFormPage page = this.setActivePage(EditPage.PAGE_ID);
		if (page instanceof EditPage) {
			((EditPage) page).edit(element, attributeType, charStart, charEnd);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor#getContributorId()
	 */
	public String getContributorId() {
		return this.getSite().getId(); // Return the ID of the editor
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.ui.eventbeditor.IEventBEditor#getEditorId()
	 */
	abstract public String getEditorId();

	/**
	 * A new page has been selected.
	 * 
	 * @param event
	 *            the selection change event.
	 */
	public void pageSelectionChanged(SelectionChangedEvent event) {
		FormEditorSelectionProvider selectionProvider = (FormEditorSelectionProvider) this
				.getEditorSite().getSelectionProvider();
		selectionProvider.fireSelectionChanged(event);
	}

}
