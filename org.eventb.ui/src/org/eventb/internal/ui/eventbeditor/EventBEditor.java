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

import java.lang.reflect.Constructor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
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
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.ExtensionLoader;
import org.eventb.internal.ui.Utils;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * Event-B specific form editor for machines, contexts, etc.
 */
public class EventBEditor
	extends FormEditor
{
	// TODO This editor need to listen to the changed in the RODIN database
	
	/**
	 * The plug-in identifier of the Event-B Editor (value
	 * <code>"org.eventb.internal.ui.editors.EventBEditor"</code>).
	 */
	public static final String EDITOR_ID = EventBUIPlugin.PLUGIN_ID + ".editors.EventBEditor";
	
	// The outline page
	private EventBContentOutlinePage fOutlinePage;
	
	// The associated rodin file handle
	private IRodinFile rodinFile = null;
	

	/**
	 * Default constructor.
	 */
	public EventBEditor() {
		super();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		
		IRodinFile rodinFile = this.getRodinInput();
		
		this.setPartName(Utils.getFileNameWithoutExtension(rodinFile.getElementName()));
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		if (rodinFile instanceof IMachine)
			this.setTitleImage(registry.get(EventBImage.IMG_MACHINE));
		if (rodinFile instanceof IContext)
			this.setTitleImage(registry.get(EventBImage.IMG_CONTEXT));
	}


	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void addPages() {
		IRodinFile rodinFile = this.getRodinInput();
		
		Constructor [] constructors = new Constructor[0];
		
		if (rodinFile instanceof IMachine) {
			constructors = ExtensionLoader.getMachinePages();
		}
		if (rodinFile instanceof IContext) {
			constructors = ExtensionLoader.getContextPages();
		}
				
		try {
			// Create the pages
			for (int i = 0; i < constructors.length; i++) {
				Object [] objects = {this};
				addPage((IFormPage) constructors[i].newInstance(objects));
			}
		}
		catch (PartInitException e) {
			// TODO Handle exception
			MessageDialog.openError(null,
					"Event-B Editor",
					"Error creating pages for Event-B Editor");
		}
		catch (Exception e) {
			// TODO Handle exception
			e.printStackTrace();
		}
	}

	/** The <code>EventBEditor</code> implementation of this 
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
	 * The <code>EventBEditor</code> implementation of this 
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
			// TODO Commit the information in the UI to the database
			// clear the dirty state on all the pages
			System.out.println("Save");
			if (this.pages != null) {
				for (int i = 0; i < pages.size(); i++) {
					Object page = pages.get(i);
					if (page instanceof IFormPage) {
						IFormPage fpage = (IFormPage) page;
						if (fpage.isDirty()) {
							System.out.println("Saving " + fpage.toString());
							fpage.doSave(monitor);
						}
					}
				}
			}

			// Save the file from the database to file
			IRodinFile inputFile = this.getRodinInput();
			inputFile.save(monitor, true);
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
			this.setActivePage(VariablesPage.PAGE_ID);
			return;
		}
		if (node.isType(IInvariant.ELEMENT_TYPE)) {
			this.setActivePage(InvariantsPage.PAGE_ID);
			return;
		}
		if (node.isType(ITheorem.ELEMENT_TYPE)) {
			this.setActivePage(TheoremsPage.PAGE_ID);
			return;
		}
		if (node.isType(IEvent.ELEMENT_TYPE)) {
			this.setActivePage(EventsPage.PAGE_ID);
			return;
		}
		if (node.isType(ICarrierSet.ELEMENT_TYPE)) {
			this.setActivePage(CarrierSetsPage.PAGE_ID);
			return;
		}
		if (node.isType(IConstant.ELEMENT_TYPE)) {
			this.setActivePage(ConstantsPage.PAGE_ID);
			return;
		}
		if (node.isType(IAxiom.ELEMENT_TYPE)) {
			this.setActivePage(AxiomsPage.PAGE_ID);
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
			this.setActivePage(CarrierSetsPage.PAGE_ID);
			return;
		}

		if (element instanceof ISees) {
			this.setActivePage(DependenciesPage.PAGE_ID);
			return;
		}
		
		if (element instanceof IAxiom) {
			this.setActivePage(AxiomsPage.PAGE_ID);
		}
		
		else if (element instanceof ITheorem) {
			this.setActivePage(TheoremsPage.PAGE_ID);
		}

		else if (element instanceof ICarrierSet) {
			this.setActivePage(CarrierSetsPage.PAGE_ID);
		}
		
		else if (element instanceof IConstant)
			this.setActivePage(ConstantsPage.PAGE_ID);

		else if (element instanceof IInvariant)
			this.setActivePage(InvariantsPage.PAGE_ID);
		
		else if (element instanceof IEvent)
			this.setActivePage(EventsPage.PAGE_ID);
		
		else if (element instanceof IVariable) {
			if (element.getParent() instanceof IMachine) 
				this.setActivePage(VariablesPage.PAGE_ID);
			else this.setActivePage(EventsPage.PAGE_ID);
		}
		
		else if (element instanceof IGuard) {
			this.setActivePage(EventsPage.PAGE_ID);
		}
		
		else if (element instanceof IAction) {
			this.setActivePage(EventsPage.PAGE_ID);
		}
		
		else {
			System.out.println("Unknown element type");
			return;
		}
		
		// select the element within the page
		EventBFormPage page = (EventBFormPage) this.getActivePageInstance();
		page.setSelection(element);
		
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
	
}
