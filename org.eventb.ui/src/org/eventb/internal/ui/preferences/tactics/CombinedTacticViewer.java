/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;

/**
 * @author Nicolas Beauger
 * 
 */
public class CombinedTacticViewer extends AbstractTacticViewer<ITacticDescriptor>{

	static final Object[] NO_OBJECT = new Object[0];
	
	private TreeViewer treeViewer;

	private final class TacticLabelProvider extends LabelProvider {
		public TacticLabelProvider() {
			// avoid synthetic access
		}

		@Override
		public String getText(Object element) {
			if (element instanceof CombNode) {
				final CombNode combNode = (CombNode) element;
				return combNode.getDesc().getTacticName();
			}
			return null;
		}
	}

	private static final class TacticContentProvider implements
			ITreeContentProvider {
		
		public TacticContentProvider() {
			// avoid synthetic access
		}

		@Override
		public void dispose() {
			// do nothing
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// do nothing
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (!(inputElement instanceof ITacticDescriptor)) {
				return NO_OBJECT;
			}
			ITacticDescriptor desc = (ITacticDescriptor) inputElement;
			final CombNode combNode = new CombNode(desc);
			return new Object[] { combNode };
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (!(parentElement instanceof CombNode)) {
				return NO_OBJECT;
			}
			final CombNode combParent = (CombNode) parentElement;
			final ITacticDescriptor desc = combParent.getDesc();
			if (!(desc instanceof ICombinedTacticDescriptor)) {
				return NO_OBJECT;
			}
			final ICombinedTacticDescriptor combDesc = (ICombinedTacticDescriptor) desc;
			final List<ITacticDescriptor> combinedTactics = combDesc
					.getCombinedTactics();
			final CombNode[] combChildren = new CombNode[combinedTactics.size()];
			for (int i = 0; i < combChildren.length; i++) {
				final ITacticDescriptor childDesc = combinedTactics.get(i);
				combChildren[i] = new  CombNode(childDesc);
			}
			return combChildren;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return element instanceof CombNode;
		}
	}

	// required to display the root element
	private static class CombNode {
		private final ITacticDescriptor desc;

		public CombNode(ITacticDescriptor desc) {
			super();
			this.desc = desc;
		}

		public ITacticDescriptor getDesc() {
			return desc;
		}
	}

	@Override
	public void createContents(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.FILL 
				| SWT.H_SCROLL | SWT.V_SCROLL);
		final Tree tree = treeViewer.getTree();
		tree.setLayout(new GridLayout());
		tree.setLayoutData(new GridData());
		treeViewer.setContentProvider(new TacticContentProvider());
		treeViewer.setLabelProvider(new TacticLabelProvider());
	}

	@Override
	public void setInput(ITacticDescriptor desc) {
		if (treeViewer == null) {
			return;
		}
		treeViewer.setInput(desc);
		treeViewer.expandAll();
		treeViewer.getTree().pack();
	}

	@Override
	public Control getControl() {
		if (treeViewer == null) {
			return null;
		}
		return treeViewer.getTree();
	}

	@Override
	public ITacticDescriptor getEditResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITacticDescriptor getInput() {
		final Object input = treeViewer.getInput();
		if (!(input instanceof ITacticDescriptor)) {
			return null;
		}
		return (ITacticDescriptor) input;
	}
}
