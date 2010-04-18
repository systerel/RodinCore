/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.wizards;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;

/**
 * This is a control for selecting working sets.
 * It is used when creating new projects.
 *
 */
public class WorkingSetControl {
	  ComboViewer workingsetComboViewer;

	  final Shell shell;
	  
	  ArrayList<WorkingSetSelection> workingSetSelections = new ArrayList<WorkingSetSelection>();
	  

	  public WorkingSetControl(Composite container, Shell shell) {
		    this.shell = shell;
		    
		    createControl(container);
	  }

	  private void createControl(Composite container) {
		  final Button addToWorkingSetCheckBox = new Button(container, SWT.CHECK);
		  GridData gd_addToWorkingSetButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		  gd_addToWorkingSetButton.verticalIndent = 12;
		  addToWorkingSetCheckBox.setLayoutData(gd_addToWorkingSetButton);
		  addToWorkingSetCheckBox.setSelection(true);
		  addToWorkingSetCheckBox.setData("name", "addToWorkingSetButton");
		  addToWorkingSetCheckBox.setText("&Add project to working sets");
		  addToWorkingSetCheckBox.setSelection(false);
	    
		  final Label workingsetLabel = new Label(container, SWT.NONE);
		  GridData gd_workingsetLabel = new GridData();
		  gd_workingsetLabel.horizontalIndent = 10;
		  workingsetLabel.setLayoutData(gd_workingsetLabel);
		  workingsetLabel.setEnabled(false);
		  workingsetLabel.setData("name", "workingsetLabel");
		  workingsetLabel.setText("Wo&rking set:");
	    
		  Combo workingsetCombo = new Combo(container, SWT.READ_ONLY);
		  workingsetCombo.setData("name", "workingsetCombo");
		  workingsetCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		  workingsetComboViewer = new ComboViewer(workingsetCombo);
		  workingsetComboViewer.setContentProvider(new IStructuredContentProvider() {
		      public Object[] getElements(Object input) {
		    	  if(input instanceof WorkingSetSelection[]) {
		    		  return (WorkingSetSelection[]) input;
		    	  }
		    	  return new WorkingSetSelection[0];
		      }
	      
	      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	    	  //do nothing
	      }

	      public void dispose() {
	    	  //do nothing
	      }
		  });
	    
		  workingsetComboViewer.setLabelProvider(new LabelProvider() {
	        
			  @Override
			  public String getText(Object element) {
				  if(element instanceof WorkingSetSelection) {
					  return ((WorkingSetSelection) element).toString();
				  }        
				  return super.getText(element);
			  }
	      });
	    
		  workingsetComboViewer.setInput(workingSetSelections.toArray(new WorkingSetSelection[workingSetSelections.size()]));
		  workingsetCombo.setEnabled(false);
	    
		  final Button selectWorkingSetButton = new Button(container, SWT.NONE);
		  selectWorkingSetButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		  selectWorkingSetButton.setData("name", "selectButton");
		  selectWorkingSetButton.setText("&Select");
		  selectWorkingSetButton.setEnabled(false);
		  selectWorkingSetButton.addSelectionListener(new SelectionAdapter() {
	      @Override
	      public void widgetSelected(final SelectionEvent e) {
	    	  IWorkingSetManager manager = PlatformUI.getWorkbench().getWorkingSetManager();
	    	  IWorkingSetSelectionDialog dialog = manager.createWorkingSetSelectionDialog(shell, true);
	    	  if (!workingsetComboViewer.getSelection().isEmpty() ){
	    		  WorkingSetSelection select =(WorkingSetSelection) ((IStructuredSelection) workingsetComboViewer.getSelection()).getFirstElement();
	    		  dialog.setSelection(select.getWorkingSets());
	    	  }
	    	  if (dialog.open()== Window.OK) {
	    		  IWorkingSet[] selection = dialog.getSelection();
	    		  if (selection.length > 0) {
	    			  workingsetComboViewer.getCombo().setEnabled(true);
	    			  WorkingSetSelection wsSelection = new WorkingSetSelection(selection);
	    			  //add it to the selections
	    			  wsSelection = addWorkingSetSelection(wsSelection);
			          workingsetComboViewer.setInput(workingSetSelections.toArray(new WorkingSetSelection[workingSetSelections.size()]));
			          workingsetComboViewer.refresh();
			          workingsetComboViewer.setSelection(new StructuredSelection(wsSelection));
	    		  } else {
	    			  addToWorkingSetCheckBox.setSelection(false);
				      workingsetLabel.setEnabled(false);
				      workingsetComboViewer.getCombo().setEnabled(false);
				      selectWorkingSetButton.setEnabled(false);
			          workingsetComboViewer.setSelection(null);
	    		  }
		  				
	    	  }
	      }
	    });

	    addToWorkingSetCheckBox.addSelectionListener(new SelectionAdapter() {
	        @Override
			public void widgetSelected(SelectionEvent e) {
	          boolean addToWorkingingSet = addToWorkingSetCheckBox.getSelection();
	          workingsetLabel.setEnabled(addToWorkingingSet);
	          workingsetComboViewer.getCombo().setEnabled(addToWorkingingSet &&
	        		  ((Object[]) workingsetComboViewer.getInput()).length > 0);
	          selectWorkingSetButton.setEnabled(addToWorkingingSet);
	          if(!addToWorkingingSet) {
	            // configuration.setWorkingSet(null);
	            workingsetComboViewer.setSelection(null);
	          }
	        }
	      });
	  }
	  
	  /**
	   * 
	   * @return The currently selected Working Sets
	   */
	  public IWorkingSet[] getSelection(){
		  if (workingsetComboViewer.getSelection().isEmpty()) {
			  return new IWorkingSet[0];
		  } else {
			  IStructuredSelection selection =((IStructuredSelection) workingsetComboViewer.getSelection());
			  
			  return ((WorkingSetSelection) selection.getFirstElement()).getWorkingSets();
		  }
	  }

	  /**
	   * Adds a WorkingSetSelection to workingSetSelections, 
	   * if it doesn't contain an equal one yet.
	   * otherwise it returns that equal WorkingSetSelection
	   * @param selection
	   * @return the WorkingSetSelection that equals selection and is contained in workingSetSelections
	   */
	  public WorkingSetSelection addWorkingSetSelection(WorkingSetSelection selection){
		  for (WorkingSetSelection sel : workingSetSelections) {
			  if (sel.equals(selection)) {
				  return sel;
			  }
		  }
		  workingSetSelections.add(selection);
		  return selection;
	  }
	  
	  public void dispose() {
		    workingsetComboViewer.getLabelProvider().dispose();
	  }
}
