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

package org.eventb.internal.ui.prover;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eventb.core.pm.UserSupport;
import org.eventb.internal.ui.EventBUIPlugin;

/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class ProverUI
	extends FormEditor 
{

	/**
	 * The plug-in identifier of the Prover UI (value
	 * <code>"org.eventb.internal.ui"</code>).
	 */
	public static final String EDITOR_ID = EventBUIPlugin.PLUGIN_ID + ".editors.ProverUI";

	/** The outline page */
	private ProofTreeUIPage fProofTreeUI;
	
	private ProofControlPage fProofControlPage;
	
	private ProofInformationPage fProofInformationPage;
	
	private UserSupport userSupport;
	
	/**
	 * Default constructor.
	 */
	public ProverUI() {
		super();
		this.userSupport = new UserSupport();
	}

	public UserSupport getUserSupport() {return userSupport;}
	
	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void addPages() {
		try {
			addPage(new ProofsPage(this));
		}
		catch (PartInitException e) {
			MessageDialog.openError(null,
					"Prover UI Editor",
					"Error creating pages for Prover UI Editor");
			//TODO Handle exception
		}
	}

	
	/** The <code>EventBEditor</code> implementation of this 
	 * <code>AbstractTextEditor</code> method performs any extra 
	 * disposal actions required by the event-B editor.
	 */
	public void dispose() {
		if (fProofTreeUI != null)
			fProofTreeUI.setInput(null);
		super.dispose();
	}	
			
	/**
	 * The <code>ProverUI</code> implementation of this 
	 * method performs gets the content outline page if request
	 * is for a an outline page.
	 * <p> 
	 * @param required the required type
	 * <p>
	 * @return an adapter for the required type or <code>null</code>
	 */ 
	public Object getAdapter(Class required) {
		if (IProofTreeUIPage.class.equals(required)) {
			if (fProofTreeUI == null) {
				fProofTreeUI = new ProofTreeUIPage(userSupport);
				if (getEditorInput() != null)
					fProofTreeUI.setInput(userSupport.getCurrentPO().getProofTree());
			}
			return fProofTreeUI;
		}
		if (IProofControlPage.class.equals(required)) {
			if (fProofControlPage == null) {
				fProofControlPage = new ProofControlPage(this);
				//if (getEditorInput() != null)
					// fRemainingGoalsPage.setInput(userSupport.getCurrentPO());
			}
			return fProofControlPage;
		}

		if (IProofInformationPage.class.equals(required)) {
			if (fProofInformationPage == null) {
				fProofInformationPage = new ProofInformationPage(this);
				//if (getEditorInput() != null)
					// fRemainingGoalsPage.setInput(userSupport.getCurrentPO());
			}
			return fProofInformationPage;
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
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() {
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
		//try {
			// TODO Commit the information in the UI to the database
			// clear the dirty state on all the pages
			if (this.pages != null) {
				for (int i = 0; i < pages.size(); i++) {
					Object page = pages.get(i);
					//System.out.println("Trying to save page " + i + " : " + page);
					if (page instanceof IFormPage) {
						//System.out.println("Saving");
						IFormPage fpage = (IFormPage) page;
						
						fpage.doSave(monitor);
						//System.out.println("Dirty? " + i + " " + fpage.isDirty());
					}
				}
			}

			// Save the file from the database to disk
			//IRodinFile inputFile = this.getRodinInput();
			//inputFile.save(monitor, true);
		//}
		//catch (RodinDBException e) {
//			e.printStackTrace();
		//}

		editorDirtyStateChanged(); // Refresh the dirty state of the editor
	}

	
	/**
	 * Getting the outline page associated with this editor
	 * @return the outline page
	 */
	protected ProofTreeUIPage getProofTreeUI() {return fProofTreeUI;}
	
//	public void setSelection(Object obj) {
//		
//		this.setActivePage(ProofsPage.PAGE_ID);
//		
//		// select the element within the page
//		ProofsPage page = (ProofsPage) this.getActivePageInstance();
//		page.setSelection(obj);
//		return;
//	}
	
}
