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


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;
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
public class ProverUI extends FormEditor {

	/**
	 * The plug-in identifier of the Prover UI (value
	 * <code>"org.eventb.internal.ui"</code>).
	 */
	public static final String EDITOR_ID = EventBUIPlugin.PLUGIN_ID + ".editors.ProverUI";

	/** The outline page */
	private ProofTreeUI fOutlinePage;
	
	//private IRodinFile rodinFile = null;
	
	//private ProofTree root;
	
	//private Object selection = null;
	
	private String [] obligations = {
			"1=1 ;; 2=2 |- 1=1 ∧2=2 ∧2=2",
			"1=1 ;; 2=2 |- 1=1 ∧(3=3 ⇒ 2=2 ∧3=3 ∧(∀x·x=1))",
			"x=1 ∨x=2 |- x < 3 ",
			"1=1 |-  ∃x·x=1"
		};

	private List<String> remaining;
	
	private int counter;
	
	private IProverSequent genSeq(String s){
		String[] hypsStr = (s.split("[|]-")[0]).split(";;");
		String goalStr = s.split("[|]-")[1];
		
		FormulaFactory ff = Lib.ff;
			//ProofManager.formulaFactory();
		
		// Parsing
		Predicate[] hyps = new Predicate[hypsStr.length];
		Predicate goal = ff.parsePredicate(goalStr).getParsedPredicate();
		for (int i=0;i<hypsStr.length;i++){
			hyps[i] = ff.parsePredicate(hypsStr[i]).getParsedPredicate();
		}
		
		// Type check
		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		for (int i=0;i<hyps.length;i++){
			ITypeCheckResult tcResult =  hyps[i].typeCheck(typeEnvironment);
			assert tcResult.isSuccess();
			typeEnvironment.addAll(tcResult.getInferredEnvironment());
			// boolean typed = (hyps[i].isCorrectlyTyped(typeEnvironment,ff)).isCorrectlyTyped();
			// assert typed;
		}
		{
			ITypeCheckResult tcResult =  goal.typeCheck(typeEnvironment);
			assert tcResult.isSuccess();
			typeEnvironment.addAll(tcResult.getInferredEnvironment());
			
			// boolean typed = (goal.isCorrectlyTyped(typeEnvironment,ff)).isCorrectlyTyped();
			// assert typed;
		}
		
		// constructing sequent
		Set<Hypothesis> Hyps = Hypothesis.Hypotheses(hyps);
			// new HashSet<Predicate>(Arrays.asList(hyps));
		
		// return new SimpleProverSequent(typeEnvironment,Hyps,goal);
		IProverSequent Seq = new SimpleProverSequent(typeEnvironment,Hyps,goal);
//		System.out.println("The Sequence " + Seq.toString());
		return Seq.selectHypotheses(Hyps);
		//return null;
	}
	
	/**
	 * Default constructor.
	 */
	public ProverUI() {
		super();
		counter = 0;
		remaining = new ArrayList<String>();
		for (int i = 0; i < obligations.length; i++) {
			remaining.add(obligations[i]);
		}
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void addPages() {
		IEditorInput input = getEditorInput();
		
		String fileName = input.getName();
		
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			
			try {
				if (ext.equalsIgnoreCase("bum")) {
					addPage(new ProofsPage(this));
//					addPage(new VariablesPage(this));
//					addPage(new InvariantsPage(this));
//					addPage(new TheoremsPage(this));
//					addPage(new InitialisationPage(this));
//					addPage(new EventsPage(this));
				}
				else if (ext.equalsIgnoreCase("buc")) {
//					addPage(new CarrierSetsPage(this));
//					addPage(new ConstantsPage(this));
//					addPage(new AxiomsPage(this));
//					addPage(new TheoremsPage(this));
				}
			}
			catch (PartInitException e) {
				MessageDialog.openError(null,
						"Event-B Editor",
						"Error creating pages for Event-B Editor");
				//TODO Handle exception
			}
			this.setPartName(input.getName());
		}
	}

	
	/** The <code>EventBEditor</code> implementation of this 
	 * <code>AbstractTextEditor</code> method performs any extra 
	 * disposal actions required by the event-B editor.
	 */
	public void dispose() {
//		if (fOutlinePage != null)
//			fOutlinePage.setInput(null);
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
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null) {
				fOutlinePage= new ProofTreeUI(this);
				if (getEditorInput() != null)
					fOutlinePage.setInput(getCurrentPO());
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

	private IProverSequent getCurrentPO() {
		return genSeq(obligations[counter]);
	}
	
	public void nextPO() {
		counter = (counter + 1) % obligations.length;
		fOutlinePage.setInput(getCurrentPO());
		fOutlinePage.selectNextPendingSubgoal();
		return;
	}
	
	public void prevPO() {
		counter--;
		if (counter < 0) counter = counter + obligations.length;
		counter = counter % obligations.length;
		fOutlinePage.setInput(getCurrentPO());
		fOutlinePage.selectNextPendingSubgoal();
		return;
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
	//public ProofTreeUI getContentOutlinePage() {return fOutlinePage;}
	
	public void setSelection(Object obj) {
		
//		if (!obj.equals(selection)) {
//			selection = obj;
			this.setActivePage(ProofsPage.PAGE_ID);
		
			// select the element within the page
			ProofsPage page = (ProofsPage) this.getActivePageInstance();
			page.setSelection(obj);
//		}
		return;
	}
	
	protected ProofTreeUI getContentOutline() {return fOutlinePage;}

	protected List<String> getRemainingObligations() {
		return remaining;
	}
	
	public void setFocus() {
		System.out.println("Focus: Need to update corresponding views");
		IWorkbenchPage wp = EventBUIPlugin.getActivePage();
		if (wp != null) {
			ProofControl pc = (ProofControl) wp.findView(ProofControl.VIEW_ID);
			if (pc != null) {
				pc.setInput(this.getContentOutline().getSelection());
			}
		}
	}
}
