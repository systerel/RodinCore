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

package fr.systerel.internal.explorer.statistics;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelProject;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * This is a content provider for the statistics details viewer.
 *
 */
public class StatisticsDetailsContentProvider extends StatisticsContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		ArrayList<Object> children = new ArrayList<Object>();
		if (inputElement instanceof Object[]) {
			//just one input element: show details for children
			if (((Object[]) inputElement).length == 1) {
				Object object = ((Object[]) inputElement)[0];
				children.addAll(Arrays.asList(getChildren(object)));
			} else {
				//several input elements: show details for each.
				children.addAll(Arrays.asList((Object[]) inputElement));
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

	protected Object[] getChildren(Object object) {
		try {
			if (object instanceof IMachineRoot) {
				return ((IMachineRoot) object).getChildren();
			} else if (object instanceof IContextRoot) {
				return ((IContextRoot) object).getChildren();
			} else if (object instanceof IElementNode) {
				IElementNode node = (IElementNode) object;
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
				IRodinProject proj = RodinCore.valueOf((IProject) object);
				if (proj.exists()) {
					ModelProject modelproject = ModelController
							.getProject(proj);
					if (modelproject != null) {
						ArrayList<IEventBRoot> result = new ArrayList<IEventBRoot>();
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

}
