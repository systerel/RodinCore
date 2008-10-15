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


package fr.systerel.explorer.masterDetails.Statistics;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelProject;
import fr.systerel.explorer.navigator.IElementNode;

/**
 * This is the content provider for the overview of the statistics.
 *
 */
public class StatisticsContentProvider implements IStructuredContentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Object[]) {
			Object[] elements = (Object[]) inputElement;
			ArrayList<IStatistics> result = new ArrayList<IStatistics>();
			IStatistics stats;
			//get the statistics for all elements
			for (Object element : elements) {
				stats = getStatistics(element);
				if (stats != null) {
					result.add(stats);
				}
			
			}
			if ( result.size() ==1) {
				return result.toArray();
			}
			// create an AggregateStatistics if there is more than one result
			if ( result.size() >1) {
				Object[] res=  new Object[1];
				res[0] = new AggregateStatistics(result.toArray(new IStatistics[result.size()]));
				return res;
			}
		}
		return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// do nothing

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}
	
	/**
	 * Indicates whether elements of the given type can possible have proof obligations 
	 * associated with them.
	 * @param type The type of the elements
	 * @return true, if they can have proof obligations, false otherwise.
	 */
	public boolean canHavePOs(IInternalElementType<?> type) {
		if (type == IInvariant.ELEMENT_TYPE) {
			return true;
		}
		if (type == IAxiom.ELEMENT_TYPE) {
			return true;
		}
		if (type == IEvent.ELEMENT_TYPE) {
			return true;
		}
		if (type == ITheorem.ELEMENT_TYPE) {
			return true;
		}
		if (type == IPSStatus.ELEMENT_TYPE) {
			return true;
		}
		return false;
	}

	
	
	public IStatistics getStatistics(Object inputElement) {
		Statistics stats = null;
		if (inputElement instanceof IMachineFile) {
			stats = new Statistics(ModelController.getMachine((IMachineFile) inputElement));
		}
		if (inputElement instanceof IContextFile) {
			stats = new Statistics(ModelController.getContext((IContextFile) inputElement));
		}
		if (inputElement instanceof IInvariant) {
			stats = new Statistics(ModelController.getInvariant((IInvariant) inputElement));
		}
		if (inputElement instanceof IAxiom) {
			stats = new Statistics(ModelController.getAxiom((IAxiom) inputElement));
		}
		if (inputElement instanceof ITheorem) {
			stats = new Statistics(ModelController.getTheorem((ITheorem) inputElement));
		}
		if (inputElement instanceof IEvent) {
			stats = new Statistics(ModelController.getEvent((IEvent) inputElement));
		}
		if (inputElement instanceof IElementNode) {
			IElementNode node = (IElementNode) inputElement;
			if (canHavePOs(node.getChildrenType())) {
				stats = new Statistics(inputElement);
			}
		}
		if (inputElement instanceof IProject) {
			IProject project = (IProject) inputElement;
			try {
				if (project.isAccessible() && project.hasNature(RodinCore.NATURE_ID)) {
					IRodinProject proj = (RodinCore.getRodinDB().getRodinProject(project.getName()));
					if (proj != null) {
						ModelProject modelproject = ModelController.getProject(proj);
						if (modelproject !=  null) {
							stats = new Statistics(modelproject);
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
		return stats;
	}
}
