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


package fr.systerel.explorer.masterDetails;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelPOContainer;
import fr.systerel.explorer.model.ModelProject;
import fr.systerel.explorer.navigator.contentProviders.RodinLabelProvider;

/**
 * The Content Provider for Statistics.
 *
 */
public class StatisticsTab implements INavigatorDetailsTab, ISelectionChangedListener {
	private ListViewer viewer = null;

	public TabItem getTabItem(TabFolder tabFolder) {
		TabItem item = new TabItem (tabFolder, SWT.NONE);
		viewer = new ListViewer(tabFolder);
		viewer.setContentProvider(statisticsContentProvider);
		viewer.setLabelProvider(new RodinLabelProvider());
		item.setControl(viewer.getControl());
		item.setText ("Statistics ");
		return item;
	}
	
	private static final IStructuredContentProvider statisticsContentProvider =
		new IStructuredContentProvider() {
		
		public Object[] getElements(Object inputElement) {
			ModelPOContainer container = null;
			boolean valid = false;
			int total = 0;
			int undischarged = 0;
			int reviewed = 0;
			int manual = 0;
			if (inputElement instanceof IMachineFile) {
				container = ModelController.getMachine((IMachineFile) inputElement);
			}
			if (inputElement instanceof IContextFile) {
				container = ModelController.getContext((IContextFile) inputElement);
			}
			if (inputElement instanceof IInvariant) {
				container = ModelController.getInvariant((IInvariant) inputElement);
			}
			if (inputElement instanceof IAxiom) {
				container = ModelController.getAxiom((IAxiom) inputElement);
			}
			if (inputElement instanceof ITheorem) {
				container = ModelController.getTheorem((ITheorem) inputElement);
			}
			if (inputElement instanceof IEvent) {
				container = ModelController.getEvent((IEvent) inputElement);
			}
			if (inputElement instanceof IProject) {
				IProject project = (IProject) inputElement;
				try {
					if (project.isAccessible() && project.hasNature(RodinCore.NATURE_ID)) {
						IRodinProject proj = (RodinCore.getRodinDB().getRodinProject(project.getName()));
						if (proj != null) {
							ModelProject modelproject = ModelController.getProject(proj);
							if (modelproject !=  null) {
								total = modelproject.getPOcount();
								undischarged = modelproject.getUndischargedPOcount();
								reviewed = modelproject.getReviewedPOcount();
								manual =  modelproject.getManuallyDischargedPOcount();
								valid = true;
							}
						}
					}
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			if (container != null) {
				total = container.getPOcount();
				undischarged = +container.getUndischargedPOcount();
				reviewed = container.getReviewedPOcount();
				manual = container.getManuallyDischargedPOcount();
				valid = true;
			} 
			if (valid) {
				String[] result= new String[5];
				result[0] = "Total number of POs: " +total;
				result[1] = "Auto. discharged: " +(total -undischarged -manual);
				result[2] = "Manual. discharged: " +manual;
				result[3] = "Reviewed: " +reviewed;
				result[4] = "Undischarged: " +(undischarged -reviewed);
 
				return result;
			} 
			String[] noresult = new String[1];
			noresult[0] = "No statistics available";
			return noresult;
		}

		public void dispose() {
			// Do nothing
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
			
			
			// Do nothing
		}
	};

	public void registerAsListener(ISelectionProvider selectionProvider) {
		selectionProvider.addSelectionChangedListener(this);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection.isEmpty())
			return;

		if (selection instanceof ITreeSelection) {
			final Object selectedNode = ((ITreeSelection) selection).getPaths()[0]
					.getLastSegment();
			if (selectedNode != null) {
				viewer.setInput(selectedNode);
			}
		}
		
	}

	

}
