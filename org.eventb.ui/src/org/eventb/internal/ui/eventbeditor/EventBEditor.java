/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContext;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachine;
import org.eventb.core.ISees;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * Abstract Event-B specific form editor for machines, contexts.
 */
public abstract class EventBEditor
	extends FormEditor
	implements IElementChangedListener 
{	

	private static class FormEditorSelectionProvider
	implements ISelectionProvider
	{
		private ISelection globalSelection;
		private ListenerList listeners;
		private FormEditor formEditor;
		/**
		 * @param multiPageEditor
		 */
		public FormEditorSelectionProvider(FormEditor formEditor) {
			listeners = new ListenerList();
			this.formEditor = formEditor;
		}

		public ISelection getSelection() {
			IFormPage activePage = formEditor.getActivePageInstance();
//			UIUtils.debug("Active Pages " + activePage);
			if (activePage != null) {
				if (activePage instanceof EventBFormPage) {
					ISelectionProvider selectionProvider = ((EventBFormPage) activePage).getPart().getViewer();
//					UIUtils.debug("Provider: " + selectionProvider);
					if (selectionProvider != null)
						if (selectionProvider != this)
							return selectionProvider.getSelection();
				}
			}
			return globalSelection;
		}

		/*
		 * (non-Javadoc) Method declared on <code> ISelectionProvider </code> .
		 */
		public void setSelection(ISelection selection) {
			IFormPage activePage = formEditor.getActivePageInstance();
			if (activePage != null) {
				ISelectionProvider selectionProvider = activePage.getSite().getSelectionProvider();
				if (selectionProvider != null) {
					if (selectionProvider != this)
						selectionProvider.setSelection(selection);
				}
			}
			else {
				this.globalSelection = selection;
				fireSelectionChanged(new SelectionChangedEvent(this,
						globalSelection));
			}
		}

	    /**
	     * Notifies all registered selection changed listeners that the editor's 
	     * selection has changed. Only listeners registered at the time this method is
	     * called are notified.
	     *
	     * @param event the selection changed event
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

	    /* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			listeners.add(listener);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			listeners.remove(listener);
		}
	}

	// The outline page
	private EventBContentOutlinePage fOutlinePage;
	
	// The associated rodin file handle
	private IRodinFile rodinFile = null;
	
	public static Collection<IElementChangedListener> listeners;
	
	private Collection<IRodinElement> newElements;
	
	private Collection<IStatusChangedListener> statusListeners;
	
	public void addNewElement(IRodinElement element) {
		newElements.add(element);
		notifyStatusChanged(element);
	}
	
	private void notifyStatusChanged(IRodinElement element) {
		for (IStatusChangedListener listener : statusListeners) {
			listener.statusChanged(element);
		}		
	}
	
	public void addStatusListener(IStatusChangedListener listener) {
		statusListeners.add(listener);
	}
	
	public void removeStatusListener(IStatusChangedListener listener) {
		statusListeners.remove(listener);
	}

	public boolean isNewElement(IRodinElement element) {
		return newElements.contains(element);
	}
	
	/**
	 * Default constructor.
	 */
	public EventBEditor() {
		super();
		RodinCore.addElementChangedListener(this);
		listeners = new HashSet<IElementChangedListener>();
		newElements = new HashSet<IRodinElement>();
		statusListeners = new HashSet<IStatusChangedListener>();
	}

	public void addElementChangedListener(IElementChangedListener listener) {
		listeners.add(listener);
	}

	public void removeElementChangedListener(IElementChangedListener listener) {
		listeners.remove(listener);
	}
	
	public void notifyElementChangedListeners(IRodinElementDelta delta) {
		for (IElementChangedListener listener : listeners) {
			listener.elementChanged(new ElementChangedEvent(delta, ElementChangedEvent.POST_CHANGE));
		}
	}
	
	/**
	 * Overrides super to plug in a different selection provider.
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		site.setSelectionProvider(new FormEditorSelectionProvider(this));
		IRodinFile rodinFile = this.getRodinInput();
		
		this.setPartName(EventBPlugin.getComponentName(rodinFile.getElementName()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
	 */
	@Override
	public boolean isDirty() {
//		UIUtils.debug("Checking dirty");
		try {
			return this.getRodinInput().hasUnsavedChanges();
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
		return super.isDirty();
	}


	/** The <code>EventBMachineEditor</code> implementation of this 
	 * <code>AbstractTextEditor</code> method performs any extra 
	 * disposal actions required by the Event-B editor.
	 */
	public void dispose() {
		if (fOutlinePage != null)
			fOutlinePage.setInput(null);
		
		try { // Close the associated RodinFile
			this.getRodinInput().close();
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
		
		super.dispose();
	}	
			
	/**
	 * The <code>EventBMachineEditor</code> implementation of this 
	 * method performs gets the content outline page if request
	 * is for a an outline page.
	 * <p> 
	 * @param required the required type
	 * <p>
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
	
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}


	/**
	 * Saves the multi-page editor's document as another file.
	 */
	public void doSaveAs() {
		// TODO Do save as
		MessageDialog.openInformation(null, null, "Saving");
		//EventBFormPage editor = (EventBFormPage) this.getEditor(0);
		//editor.doSaveAs();
		//IEditorPart editor = getEditor(0);
		//editor.doSaveAs();
		//setPageText(0, editor.getTitle());
		//setInput(editor.getEditorInput());
	}
	
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		try {
			UIUtils.debug("Save");
			if (this.pages != null) {
				for (int i = 0; i < pages.size(); i++) {
					Object page = pages.get(i);
					if (page instanceof IFormPage) {
						IFormPage fpage = (IFormPage) page;
						if (fpage.isDirty()) {
							UIUtils.debug("Saving " + fpage.toString());
							fpage.doSave(monitor);
						}
					}
				}
			}

			// Save the file from the database to file
			IRodinFile inputFile = this.getRodinInput();
			inputFile.save(monitor, true);
			
			while (!newElements.isEmpty()) {
				IRodinElement element = (IRodinElement) newElements.toArray()[0];
				newElements.remove(element);
				notifyStatusChanged(element);
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}

		editorDirtyStateChanged(); // Refresh the dirty state of the editor
	}

	
	/**
	 * Set the selection in the editor.
	 * <p>
	 * @param ssel the current selecting element
	 */
	public void setSelection(Object ssel) {
		if (ssel instanceof IRodinElement) {
			setElementSelection((IRodinElement) ssel);
			return;
		}
		
		if (ssel instanceof TreeNode) {
			setTreeNodeSelection((TreeNode) ssel);
			return;
		}
		return;
	}
	
	
	/*
	 * Set the selection in the editor if the input is a TreeNode.
	 * <p> 
	 * @param node instance of TreeNode
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
	

	/*
	 * Set the selection in the editor if the input is a Rodin element.
	 * <p> 
	 * @param node instance of IRodinElement
	 */
	private void setElementSelection(IRodinElement element) {
		if (element instanceof IMachine) {
			this.setActivePage(DependenciesPage.PAGE_ID);
			return;
		}

		if (element instanceof IContext) {
			this.setActivePage(CarrierSetPage.PAGE_ID);
			return;
		}

		if (element instanceof ISees) {
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
			if (element.getParent() instanceof IMachine) 
				this.setActivePage(VariablePage.PAGE_ID);
			else this.setActivePage(EventPage.PAGE_ID);
		}
		
		else if (element instanceof IGuard) {
			this.setActivePage(EventPage.PAGE_ID);
		}
		
		else if (element instanceof IAction) {
			this.setActivePage(EventPage.PAGE_ID);
		}
		
		else {
			UIUtils.debug("Unknown element type");
			return;
		}
		
		// select the element within the page
		IFormPage page = this.getActivePageInstance();
		if (page instanceof EventBFormPage) {
			((EventBFormPage) page).setSelection(element);
		}
		
	}
	
	
	/**
	 * Getting the RodinFile associated with this editor
	 * <p>
	 * @return a handle to a Rodin file
	 */
	public IRodinFile getRodinInput() {
		if (rodinFile == null) {
			FileEditorInput editorInput = (FileEditorInput) this.getEditorInput();
			
			IFile inputFile = editorInput.getFile();
			
			rodinFile = (IRodinFile) RodinCore.create(inputFile);
		}
		return rodinFile;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {		
		IRodinElementDelta delta = event.getDelta();
		UIUtils.debug("Delta: " + delta);
		processDelta(delta);
	}

	private void processDelta(IRodinElementDelta delta) {
		IRodinElement element= delta.getElement();
		
		if (element instanceof IRodinDB) {
			IRodinElementDelta [] deltas = delta.getAffectedChildren();
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
			IRodinElementDelta [] deltas = delta.getAffectedChildren();
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
}
