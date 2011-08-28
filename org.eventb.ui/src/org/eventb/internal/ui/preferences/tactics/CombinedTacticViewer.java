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
import org.eclipse.swt.widgets.Composite;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

/**
 * @author Nicolas Beauger
 * 
 */
public class CombinedTacticViewer {

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

	public void createContents(Composite parent) {
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new TacticContentProvider());
		treeViewer.setLabelProvider(new TacticLabelProvider());

	}

	public void setInput(ICombinedTacticDescriptor desc) {
		if (treeViewer != null) {
			treeViewer.setInput(new CombInput(desc));
		}
	}

	public void dispose() {
		if (treeViewer != null) {
			treeViewer.getTree().dispose();
		}
	}

	public void expandAll() {
		treeViewer.expandAll();
	}
}
