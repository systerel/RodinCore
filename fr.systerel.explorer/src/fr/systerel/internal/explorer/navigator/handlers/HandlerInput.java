/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.handlers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPSStatus;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.navigator.ExplorerUtils;

public class HandlerInput {

	private static class SchedulingRuleComputer {

		private final Set<IRodinElement> elems;

		private final Set<IEventBRoot> roots;

		public SchedulingRuleComputer() {
			this.elems = new HashSet<IRodinElement>();
			this.roots = new HashSet<IEventBRoot>();
		}

		public void add(Object obj) {
			if (obj instanceof IResource) {
				add(RodinCore.valueOf((IResource) obj));
			} else if (obj instanceof IRodinElement) {
				add((IRodinElement) obj);
			} else if (obj instanceof IElementNode) {
				add((IElementNode) obj);
			}
		}

		private void add(IRodinElement elem) {
			if (elem instanceof IRodinFile) {
				addRoot(((IRodinFile) elem).getRoot());
			} else if (elem instanceof IInternalElement) {
				addRoot(((IInternalElement) elem).getRoot());
			} else {
				elems.add(elem);
			}
		}

		private void add(IElementNode node) {
			IModelElement elem = ModelController.getModelElement(node);
			while (elem != null) {
				final IRodinElement ie = elem.getInternalElement();
				if (ie != null) {
					add(ie);
					break;
				}
				elem = elem.getModelParent();
			}
		}

		private void addRoot(IInternalElement root) {
			if (root instanceof IEventBRoot)
				roots.add((IEventBRoot) root);
		}

		public ISchedulingRule getSchedulingRule() {
			final Set<ISchedulingRule> rules = new HashSet<ISchedulingRule>();
			for (IRodinElement elem : elems) {
				rules.add(elem.getSchedulingRule());
			}
			for (IEventBRoot root : roots) {
				rules.add(root.getPORoot().getSchedulingRule());
				rules.add(root.getPRRoot().getSchedulingRule());
				rules.add(root.getPSRoot().getSchedulingRule());
			}
			final ISchedulingRule[] array = new ISchedulingRule[rules.size()];
			rules.toArray(array);
			return MultiRule.combine(array);
		}

	}

	private final IStructuredSelection selection;

	public HandlerInput(IStructuredSelection selection) {
		this.selection = selection;
	}

	public ISchedulingRule getSchedulingRule() {
		final SchedulingRuleComputer comp = new SchedulingRuleComputer();
		final Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			comp.add(iter.next());
		}
		return comp.getSchedulingRule();
	}

	public Set<IPSStatus> getStatuses(boolean pendingOnly, SubMonitor monitor)
			throws InterruptedException {
		// TODO expand code of ExplorerUtils.getStatuses() here
		return ExplorerUtils.getStatuses(selection.toArray(), pendingOnly,
				monitor);
	}

}
