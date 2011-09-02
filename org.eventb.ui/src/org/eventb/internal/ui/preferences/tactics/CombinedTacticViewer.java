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

import java.util.ArrayList;
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
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.SequentProver;

/**
 * @author Nicolas Beauger
 * 
 */
// TODO deal with fonts
public class CombinedTacticViewer extends AbstractTacticViewer<ITacticDescriptor>{

	static final ITacticNode[] NO_NODE = new ITacticNode[0];
	
	private TreeViewer treeViewer;

	public static final class TacticNodeLabelProvider extends LabelProvider {
		
		public TacticNodeLabelProvider() {
			// avoid synthetic access
		}

		@Override
		public String getText(Object element) {
			if (element instanceof ITacticNode) {
				final ITacticNode node = (ITacticNode) element;
				return node.getText();
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
			final ITacticNode combNode = makeTacticNode(inputElement);
			if (combNode == null) {
				return NO_NODE;
			}
			return new Object[] { combNode };
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (!(parentElement instanceof ITacticNode)) {
				return NO_NODE;
			}
			final ITacticNode combParent = (ITacticNode) parentElement;
			return combParent.getChildren();
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return element instanceof ITacticNode;
		}
	}

	public static interface ITacticNode {
		String getText();
		ITacticDescriptor getResultDesc();
		boolean hasChildren();
		ITacticNode[] getChildren();
		boolean isValid();
	}
	
	public static ITacticNode makeTacticNode(Object descriptor) {
		if (descriptor instanceof ITacticDescriptor) {
			if (descriptor instanceof ICombinedTacticDescriptor) {
				return new CombinatorNode(
						(ICombinedTacticDescriptor) descriptor);
			} else {
				// simple or ref (no param here)
				return new SimpleNode((ITacticDescriptor) descriptor);
			}
		}
		if (descriptor instanceof ICombinatorDescriptor) {
			return new CombinatorNode((ICombinatorDescriptor) descriptor);
		}
		return null;
	}

	public static class SimpleNode implements ITacticNode {

		private final ITacticDescriptor desc;
		
		public SimpleNode(ITacticDescriptor desc) {
			this.desc = desc;
		}
		
		@Override
		public ITacticDescriptor getResultDesc() {
			return desc;
		}

		@Override
		public boolean hasChildren() {
			return false;
		}

		@Override
		public ITacticNode[] getChildren() {
			return NO_NODE;
		}

		@Override
		public boolean isValid() {
			return true;
		}

		@Override
		public String getText() {
			return desc.getTacticName();
		}
		
	}
	
	public static class CombinatorNode implements ITacticNode {
		private final ICombinatorDescriptor combinator;
		private final List<ITacticNode> children;

		public CombinatorNode(ICombinatorDescriptor combinator) {
			this.combinator = combinator;
			this.children = new ArrayList<ITacticNode>();
		}

		public CombinatorNode(ICombinedTacticDescriptor desc) {
			final String combinatorId = desc.getCombinatorId();
			final IAutoTacticRegistry reg = SequentProver
					.getAutoTacticRegistry();
			this.combinator = reg.getCombinatorDescriptor(combinatorId);
			if (combinator == null) {
				throw new IllegalArgumentException("invalid combinator: "
						+ combinatorId);
			}
			final List<ITacticDescriptor> combined = desc.getCombinedTactics();
			this.children = computeNodes(combined);
		}
		
		private static List<ITacticNode> computeNodes(
				List<ITacticDescriptor> descs) {
			final List<ITacticNode> combChildren = new ArrayList<ITacticNode>(
					descs.size());
			for (ITacticDescriptor desc : descs) {
				combChildren.add(makeTacticNode(desc));
			}
			return combChildren;
		}
		
		@Override
		public ITacticDescriptor getResultDesc() {
			if (!isValid()) {
				throw new IllegalStateException(
						"computing an invalid descriptor from "
								+ combinator.getTacticDescriptor()
										.getTacticID());
			}
			final List<ITacticDescriptor> childDescs = new ArrayList<ITacticDescriptor>(
					children.size());
			for (ITacticNode child : children) {
				final ITacticDescriptor childDesc = child.getResultDesc();
				childDescs.add(childDesc);
			}
			final String combinedId = combinator.getTacticDescriptor()
					.getTacticID() + ".combined";
			return combinator.combine(childDescs, combinedId);
		}
		
		@Override
		public boolean hasChildren() {
			return true;
		}
		
		@Override
		public ITacticNode[] getChildren() {
			return children.toArray(new ITacticNode[children.size()]);
		}

		@Override
		public boolean isValid() {
			for (ITacticNode child : children) {
				if (!child.isValid()) {
					return false;
				}
			}
			
			final int arity = children.size();
			
			final int minArity = combinator.getMinArity();
			if (combinator.isArityBound()) {
				return arity == minArity;
			}
			return arity >= minArity;
		}

		@Override
		public String getText() {
			return combinator.getTacticDescriptor().getTacticName();
		}
	}

	public static class ProfileNode implements ITacticNode {

		private final IPrefMapEntry<ITacticDescriptor> entry;
		
		public ProfileNode(IPrefMapEntry<ITacticDescriptor> entry) {
			this.entry = entry;
		}

		@Override
		public String getText() {
			return entry.getKey();
		}

		@Override
		public ITacticDescriptor getResultDesc() {
			return entry.getReference();
		}

		@Override
		public boolean hasChildren() {
			return false;
		}

		@Override
		public ITacticNode[] getChildren() {
			return NO_NODE;
		}

		@Override
		public boolean isValid() {
			return entry.getValue() != null;
		}
		
	}
	
	@Override
	public void createContents(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.FILL 
				| SWT.H_SCROLL | SWT.V_SCROLL);
		final Tree tree = treeViewer.getTree();
		tree.setLayout(new GridLayout());
		final GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumWidth = 200;
		tree.setLayoutData(layoutData);
		treeViewer.setContentProvider(new TacticContentProvider());
		treeViewer.setLabelProvider(new TacticNodeLabelProvider());
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

	private ITacticNode getTopNode() {
		final TreeItem topItem = treeViewer.getTree().getTopItem();
		if (topItem == null) {
			return null;
		}
		final Object data = topItem.getData();
		if (!(data instanceof ITacticNode)) {
			return null;
		}
		return (ITacticNode) data;
	}

	@Override
	public ITacticDescriptor getEditResult() {
		final ITacticNode topNode = getTopNode();
		if (topNode == null) {
			return null;
		}
		return topNode.getResultDesc();
	}

	public boolean isResultValid() {
		final ITacticNode topNode = getTopNode();
		if (topNode == null) {
			return false;
		}
		return topNode.isValid();
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
