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
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPSStatus;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelProject;
import fr.systerel.explorer.navigator.IElementNode;

/**
 * @author Administrator
 *
 */
public class StatisticsDetailsContentProvider extends StatisticsContentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		ArrayList<Object> children = new ArrayList<Object>();
		if (inputElement instanceof Object[]) {
			for (Object object : (Object[])inputElement) {
				
		
				try {
					if (object instanceof IMachineFile){
						children.addAll(Arrays.asList(((IMachineFile) object).getChildren()));
					}
					if (object instanceof IContextFile) {
						children.addAll(Arrays.asList(((IContextFile) object).getChildren()));
					}
					if (object instanceof IElementNode) {
						IElementNode node = (IElementNode) object;
						if (canHavePOs(node.getChildrenType())) {
							//if this is a PO node show details of children of the machine/context.
							if (node.getChildrenType() == IPSStatus.ELEMENT_TYPE) {
								children.addAll(Arrays.asList( node.getParent().getChildren()));
							} else {
								children.addAll(Arrays.asList(node.getParent().getChildrenOfType(node.getChildrenType())));
							}
						}
					}
					if (object instanceof IProject) {
						IProject project = (IProject) object;
							if (project.isAccessible() && project.hasNature(RodinCore.NATURE_ID)) {
								IRodinProject proj = (RodinCore.getRodinDB().getRodinProject(project.getName()));
								if (proj != null) {
									ModelProject modelproject = ModelController.getProject(proj);
									if (modelproject !=  null) {
										children.addAll(Arrays.asList(proj.getChildren()));
									}
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
		}
		ArrayList<IStatistics> result = new ArrayList<IStatistics>();
		IStatistics stats;
		for (Object child : children) {
			stats = getStatistics(child);
			if (stats != null) {
				result.add(stats);
			}
		}
		return result.toArray();
	}

	
	
}
