/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *     Systerel - redirected dialog opening
 *     Systerel - refactored saveDefaultPage()
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import static org.eclipse.ui.actions.ActionFactory.REDO;
import static org.eclipse.ui.actions.ActionFactory.UNDO;
import static org.eventb.internal.ui.utils.Messages.error_cannot_save_as_message;
import static org.eventb.internal.ui.utils.Messages.error_unsupported_action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.actions.HistoryAction;
import org.eventb.internal.ui.eventbeditor.actions.HistoryActionFactory;
import org.eventb.internal.ui.eventbeditor.editpage.EditPage;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
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
public abstract class EventBEditor<R extends IInternalElement> extends
		EventBFormEditor implements IElementChangedListener, IEventBEditor<R>,
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
	private IRodinFile rodinFile;

	// The associated rodin file handle
	private R rodinRoot;

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
	 * @seeorg.eventb.internal.ui.eventbeditor.IEventBEditor#
	 * removeElementChangedListener(org.rodinp.core.IElementChangedListener)
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
		super.init(site, input);
		final IInternalElement root ;
		setSite(site);
		setInput(input);
		site.setSelectionProvider(new FormEditorSelectionProvider(this));
		RodinCore.addElementChangedListener(this);
		rodinFile = getRodinFile(input);
		root = rodinFile.getRoot();
		assert (root instanceof IContextRoot) || (root instanceof IMachineRoot);
		rodinRoot = (R) root;
		setPartName(rodinFile.getBareName());
		
		// Activate Event-B Editor Context
		IContextService contextService = (IContextService) getSite()
				.getService(IContextService.class);
		contextActivation = contextService
				.activateContext(EventBUIPlugin.PLUGIN_ID
						+ ".contexts.eventBEditorScope");
		// set Retargeted Action
		setRetargetedAction();

	}

	private void setRetargetedAction() {
		final IWorkbenchWindow wb = getEditorSite().getWorkbenchWindow();
		final HistoryActionFactory actionFactory = HistoryActionFactory.INSTANCE;
		final Action undoAction = actionFactory.getUndoAction(wb);
		final Action redoAction = actionFactory.getRedoAction(wb);
		final IPartListener listener = new PartListener();
		setHistoryHandler(UNDO.getId(), undoAction, listener);
		setHistoryHandler(REDO.getId(), redoAction, listener);
	}

	IAction getGlobalActionHandler(String actionId) {
		final IActionBars bars = getEditorSite().getActionBars();
		return bars.getGlobalActionHandler(actionId);
	}

	/**
	 * Set a global action handler for a undo/redo action and add the given part
	 * listener.
	 * */
	private void setHistoryHandler(String actionId, IAction handler,
			IPartListener listener) {
		if (!(getGlobalActionHandler(actionId) == handler)) {
			final IActionBars bars = getEditorSite().getActionBars();
			bars.setGlobalActionHandler(actionId, handler);
			getEditorSite().getPage().addPartListener(listener);
		}
	}
	
// protected abstract IRodinFile getRodinFile(IEditorInput input);

	protected IRodinFile getRodinFile(IEditorInput input) {
		FileEditorInput editorInput = (FileEditorInput) input;
		IFile inputFile = editorInput.getFile();
		return RodinCore.valueOf(inputFile);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
	 */
	@Override
	public boolean isDirty() {
		try {
			return this.getRodinInputFile().hasUnsavedChanges();
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

		// Dispose all operation in the History
		History.getInstance().dispose(OperationFactory.getContext(rodinRoot));

		saveDefaultPage();

		try { // Make the associated RodinFile consistent if it is has some
			// unsaved change
			IRodinFile rodinInput = this.getRodinInputFile();
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
        if (lastActivePageID == null) {
            return;
        }
        final IFile resource = this.getRodinInputFile().getResource();
        if (!resource.exists()) {
            return;
        }
        if (EventBEditorUtils.DEBUG)
            EventBEditorUtils.debug("Save Page: " + lastActivePageID);
        final QualifiedName key = new QualifiedName(EventBUIPlugin.PLUGIN_ID,
                "default-editor-page");
        try {
            resource.setPersistentProperty(key, lastActivePageID);
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
		IRodinFile inputFile = this.getRodinInputFile();
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

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSaveAs() {
		UIUtils.showError(error_unsupported_action,
				error_cannot_save_as_message);
	}

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

			getRodinInputFile().save(monitor, true);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		
		notifyAndClearNewElements();
		editorDirtyStateChanged(); // Refresh the dirty state of the editor
	}

	private void notifyAndClearNewElements() {
		final Iterator<IRodinElement> iterator = newElements.iterator();
		while (iterator.hasNext()) {
			final IRodinElement element = iterator.next();
			iterator.remove();
			notifyStatusChanged(element);
		}
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

	@Override
	public IRodinFile getRodinInputFile() {
		if (rodinFile == null)
			throw new IllegalStateException(
					"Editor hasn't been initialized yet");
		return rodinFile;
	}

	public R getRodinInput() {
		if (rodinRoot == null)
			throw new IllegalStateException(
					"Editor hasn't been initialized yet");
		return rodinRoot;
	}

	public FormulaFactory getFormulaFactory() {
		return ((IEventBRoot)rodinRoot).getFormulaFactory();
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
			if (!this.getRodinInput().getRodinProject().equals(prj)) {
				return;
			}
			IRodinElementDelta[] deltas = delta.getAffectedChildren();
			for (int i = 0; i < deltas.length; i++) {
				processDelta(deltas[i]);
			}
			return;
		}

		if (element instanceof IRodinFile) {
			if (!this.getRodinInput().getParent().equals(element)) {
				return;
			}
			notifyElementChangedListeners(delta);
			Display display = PlatformUI.getWorkbench().getDisplay();
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

	/**
	 * A listener to update the undo/redo action when the Event-B editor is
	 * activated.
	 */
	class PartListener implements IPartListener {
		private void refreshUndoRedoAction() {
			final IAction undoAction = getGlobalActionHandler(UNDO.getId());
			final IAction redoAction = getGlobalActionHandler(REDO.getId());

			if (undoAction instanceof HistoryAction
					&& redoAction instanceof HistoryAction) {
				((HistoryAction) undoAction).refresh();
				((HistoryAction) redoAction).refresh();
			}
		}

		public void partActivated(IWorkbenchPart part) {
			refreshUndoRedoAction();
		}

		public void partBroughtToTop(IWorkbenchPart part) {
			// do nothing
		}

		public void partClosed(IWorkbenchPart part) {
			// do nothing
		}

		public void partDeactivated(IWorkbenchPart part) {
			// do nothing
		}

		public void partOpened(IWorkbenchPart part) {
			// do nothing
		}
	}

}
