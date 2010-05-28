/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - removed StatisticsContentProvider inheritance
 *******************************************************************************/

package fr.systerel.internal.explorer.statistics;

import static fr.systerel.internal.explorer.statistics.StatisticsUtil.canHavePOs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelProject;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * This is a content provider for the statistics details viewer.
 *
 */
public class StatisticsDetailsContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		final List<Object> children = new ArrayList<Object>();
		if (inputElement instanceof Object[]) {
			//just one input element: show details for children
			if (((Object[]) inputElement).length == 1) {
				final Object object = ((Object[]) inputElement)[0];
				children.addAll(Arrays.asList(getChildren(object)));
			} else {
				//several input elements: show details for each.
				children.addAll(Arrays.asList((Object[]) inputElement));
			}
		}
		final List<IStatistics> temp = getStatistics(children);
		final IStatistics overviewStats = extractOverviewStat(inputElement);
		if (overviewStats != null) {
			temp.add(0, overviewStats);
		}
		return temp.toArray();
	}
	
	private IStatistics extractOverviewStat(Object input) {
		final Object[] elements = (Object[]) input;
		final List<IStatistics> overview = getStatistics(Arrays
				.asList(elements));
		if (overview.size() > 0) {
			final IStatistics[] statArray = new IStatistics[overview.size()];
			return new AggregateStatistics(overview.toArray(statArray));
		}
		return null;
	}
	
	private List<IStatistics> getStatistics(List<Object> elements){
		final List<IStatistics> result = new ArrayList<IStatistics>();
		for (Object element : elements) {
			final IStatistics stat = getStatistics(element);
			if (stat != null){
				result.add(getStatistics(element));				
			}
		}
		return result;
	}

	protected Object[] getChildren(Object object) {
		try {
			if (object instanceof IMachineRoot) {
				return ((IMachineRoot) object).getChildren();
			} else if (object instanceof IContextRoot) {
				return ((IContextRoot) object).getChildren();
			} else if (object instanceof IElementNode) {
				final IElementNode node = (IElementNode) object;
				if (canHavePOs(node.getChildrenType())) {
					// if this is a PO node show details of children of the
					// machine/context.
					if (node.getChildrenType() == IPSStatus.ELEMENT_TYPE) {
						return node.getParent().getChildren();
					} else {
						return node.getParent().getChildrenOfType(
								node.getChildrenType());
					}
				}
			} else if (object instanceof IProject) {
				final IRodinProject proj = RodinCore.valueOf((IProject) object);
				if (proj.exists()) {
					final ModelProject modelproject = ModelController
							.getProject(proj);
					if (modelproject != null) {
						final List<IEventBRoot> result = new ArrayList<IEventBRoot>();
						result.addAll(Arrays.asList(ExplorerUtils
								.getContextRootChildren(proj)));
						result.addAll(Arrays.asList(ExplorerUtils
								.getMachineRootChildren(proj)));
						return result.toArray();
					}
				}
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "when getting statistics children for " +object);
		}
		return new Object[0];

	}
	
	private IStatistics getStatistics(Object inputElement) {
		if (inputElement instanceof IProject) {
			final IRodinProject rodinProject = RodinCore
					.valueOf((IProject) inputElement);
			if (rodinProject.exists()) {
				inputElement = rodinProject;
			}
		}
		final IModelElement element = ModelController
				.getModelElement(inputElement);
		if (element != null) {
			return new Statistics(element);
		}
		return null;
	}

	public void dispose() {
		// do nothing
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
		
	}
}
