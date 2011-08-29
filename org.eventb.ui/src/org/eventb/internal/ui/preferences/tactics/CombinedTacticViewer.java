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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;

/**
 * @author Nicolas Beauger
 * 
 */
public class CombinedTacticViewer extends AbstractTacticViewer<ICombinedTacticDescriptor>{

	private TreeViewer treeViewer;

	private final class TacticLabelProvider extends LabelProvider {
		public TacticLabelProvider() {
			// avoid synthetic access
		}

		@Override
		public String getText(Object element) {
			if (element instanceof ITacticDescriptor) {
				return ((ITacticDescriptor) element).getTacticName();
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
			if (!(inputElement instanceof CombInput)) {
				return null;
			}
			return new Object[] { ((CombInput) inputElement).getD() };
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (!(parentElement instanceof ICombinedTacticDescriptor)) {
				return null;
			}
			final ICombinedTacticDescriptor combDesc = (ICombinedTacticDescriptor) parentElement;
			final List<ITacticDescriptor> combinedTactics = combDesc
					.getCombinedTactics();

			return combinedTactics
					.toArray(new ITacticDescriptor[combinedTactics.size()]);
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return element instanceof ICombinedTacticDescriptor
					|| element instanceof CombInput;
		}
	}

	// required to display the root element
	private static class CombInput {
		private final ICombinedTacticDescriptor d;

		public CombInput(ICombinedTacticDescriptor d) {
			super();
			this.d = d;
		}

		public ICombinedTacticDescriptor getD() {
			return d;
		}
	}

	@Override
	public void createContents(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.FILL 
				| SWT.H_SCROLL | SWT.V_SCROLL);
		final Tree tree = treeViewer.getTree();
		tree.setLayout(parent.getLayout());
		tree.setLayoutData(parent.getLayoutData());
		treeViewer.setContentProvider(new TacticContentProvider());
		treeViewer.setLabelProvider(new TacticLabelProvider());
	}

	@Override
	public void setInput(ICombinedTacticDescriptor desc) {
		if (treeViewer == null) {
			return;
		}
		treeViewer.setInput(new CombInput(desc));
		treeViewer.expandAll();
		treeViewer.getTree().pack();
	}

	@Override
	protected Control getControl() {
		return treeViewer.getTree();
	}
}
