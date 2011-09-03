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

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.dnd.TreeDragSourceEffect;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
public class CombinedTacticViewer extends AbstractTacticViewer<ITacticDescriptor>{

	static final ITacticNode[] NO_NODE = new ITacticNode[0];

	private static class TacticDrop extends ViewerDropAdapter {
		private final CombinedTacticViewer tacticViewer;

		public TacticDrop(TreeViewer treeViewer, CombinedTacticViewer tacticViewer) {
			super(treeViewer);
			this.tacticViewer = tacticViewer;
			
		}

		@Override
		public boolean validateDrop(Object target, int operation,
				TransferData transferType) {
			
			if (target == null) {
				// OK if tree is empty
				final Tree tree = tacticViewer.getControl();
				return tree.getItems().length == 0;
			}
			if (!(target instanceof ITacticNode)) {
				return false;
			}
			final ITacticNode targetNode = (ITacticNode) target;
			
			return targetNode.canAcceptDrop();
			// FIXME do not accept drop in descendants
		}

		@Override
		public boolean performDrop(Object data) {
			// FIXME valid only when dragging inside tree viewer
			// use selection service instead
			final Object selected = getSelectedObject();
			if (!(selected instanceof ITacticNode)) {
				return false;
			}
			final ITacticNode droppedNode = (ITacticNode) selected;
			final Object target = getCurrentTarget();
			if (!(target instanceof ITacticNode)) {
				return false;
			}
			ITacticNode targetNode = (ITacticNode) target;
			targetNode.drop(droppedNode);
			tacticViewer.refresh(targetNode);

			return true;
		}
	}

	private static class TacticDragEffect extends TreeDragSourceEffect {

		private final CombinedTacticViewer viewer;

		public TacticDragEffect(CombinedTacticViewer viewer) {
			super(viewer.getControl());
			this.viewer = viewer;
		}

		@Override
		public void dragFinished(DragSourceEvent event) {
			final Tree tree = (Tree) getControl();
			final TreeItem[] selection = tree.getSelection();
			for (TreeItem treeItem : selection) {
				final Object data = treeItem.getData();
				if (!(data instanceof ITacticNode)) {
					continue;
				}
				final ITacticNode node = (ITacticNode) data;
				// FIXME not if drop was not performed
				// TODO make a 'trash' composite
				node.delete();
				viewer.refresh(node);
			}
			super.dragFinished(event);
		}
	}

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
			final ITacticNode combNode = makeTacticNode(null, inputElement);
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
			if (!(element instanceof ITacticNode)) {
				return null;
			}
			final ITacticNode comb = (ITacticNode) element;
			return comb.getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof ITacticNode) {
				final ITacticNode node = (ITacticNode) element;
				return node.hasChildren();
			}
			return false;
		}
	}

	public static interface ITacticNode {
		String getText();
		ITacticDescriptor getResultDesc();
		ITacticNode getParent();
		boolean hasChildren();
		ITacticNode[] getChildren();
		boolean isValid();
		void drop(ITacticNode droppedNode);
		void addChild(ITacticNode droppedNode, LeafNode nextSibling);
		boolean canAcceptDrop();
		void delete();
		void deleteChild(ITacticNode child);
	}

	public static ITacticNode makeTacticNode(ITacticNode parent, Object descriptor) {
		if (descriptor instanceof ITacticDescriptor) {
			if (descriptor instanceof ICombinedTacticDescriptor) {
				return new CombinatorNode(parent,
						(ICombinedTacticDescriptor) descriptor);
			} else {
				// simple or ref (no param here)
				return new SimpleNode(parent, (ITacticDescriptor) descriptor);
			}
		}
		if (descriptor instanceof ICombinatorDescriptor) {
			return new CombinatorNode(parent,
					(ICombinatorDescriptor) descriptor);
		}
		throw new IllegalArgumentException("illegal descriptor : " + descriptor);
	}

	public static abstract class AbstractTacticNode implements ITacticNode {
		protected final ITacticNode parent;

		public AbstractTacticNode(ITacticNode parent) {
			super();
			this.parent = parent;
		}
		
		@Override
		public ITacticNode getParent() {
			return parent;
		}
		
		@Override
		public void delete() {
			if (parent == null) {
				// FIXME delete root
				return;
			}
			parent.deleteChild(this);
		}
		
		@Override
		public String toString() {
			return getText();
		}

	}
	
	public static abstract class LeafNode extends AbstractTacticNode {
		
		public LeafNode() {
			this(null);
		}
		
		public LeafNode(ITacticNode parent) {
			super(parent);
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
		public boolean canAcceptDrop() {
			if (parent == null) {
			return false;
			}
			return parent.canAcceptDrop();
		}
		
		@Override
		public void drop(ITacticNode droppedNode) {
			if (parent == null) {
				return;
			}
			parent.addChild(droppedNode, this);
		}
		
		@Override
		public void deleteChild(ITacticNode child) {
			// nothing to do
		}
		
		@Override
		public void addChild(ITacticNode droppedNode, LeafNode leafNode) {
			throw new UnsupportedOperationException("a leaf has no child !");
		}
	}
	
	public static class SimpleNode extends LeafNode {

		private final ITacticDescriptor desc;
		
		public SimpleNode(ITacticDescriptor desc) {
			this.desc = desc;
		}
		
		public SimpleNode(ITacticNode parent, ITacticDescriptor desc) {
			super(parent);
			this.desc = desc;
		}
		
		@Override
		public ITacticDescriptor getResultDesc() {
			return desc;
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
	
	public static class CombinatorNode extends AbstractTacticNode {
		private final ICombinatorDescriptor combinator;
		private final List<ITacticNode> children;

		public CombinatorNode(ITacticNode parent, ICombinatorDescriptor combinator) {
			super(parent);
			this.combinator = combinator;
			this.children = new ArrayList<ITacticNode>();
		}

		public CombinatorNode(ITacticNode parent, ICombinedTacticDescriptor desc) {
			super(parent);
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
		
		private List<ITacticNode> computeNodes(
				List<ITacticDescriptor> descs) {
			final List<ITacticNode> combChildren = new ArrayList<ITacticNode>(
					descs.size());
			for (ITacticDescriptor desc : descs) {
				combChildren.add(makeTacticNode(this, desc));
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
			return !children.isEmpty();
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

		@Override
		public void drop(ITacticNode droppedNode) {
			addChild(droppedNode, null);
		}

		@Override
		public void addChild(ITacticNode droppedNode, LeafNode nextSibling) {
			final ITacticNode newChild = makeNewChild(droppedNode);
			final int index;
			if (nextSibling == null) {
				// insert at the end
				index = children.size();
			} else {
				index = children.indexOf(nextSibling);
				if (index < 0) {
					throw new IllegalArgumentException("no such child: "
							+ droppedNode.getText() + " in " + getText());
				}
			}
			children.add(index, newChild);
		}
		
		// make a new instance to distinguish dragged from dropped
		private ITacticNode makeNewChild(ITacticNode node) {
			if(node instanceof CombinatorNode) {
				// avoid computing result desc as it may be invalid
				final CombinatorNode combNode = (CombinatorNode) node;
				final CombinatorNode newChild = new CombinatorNode(this, combNode.combinator);
				newChild.children.addAll(combNode.children);
				return newChild;
			}
			return makeTacticNode(this, node.getResultDesc());
		}
		
		@Override
		public String toString() {
			return getText();
		}

		@Override
		public boolean canAcceptDrop() {
			// avoid adding too many children
			if (!combinator.isArityBound()) {
				return true;
			}
			// bound => min = max
			final int maxArity = combinator.getMinArity();
			
			return children.size() < maxArity;
		}

		@Override
		public void deleteChild(ITacticNode child) {
			final boolean removed = children.remove(child);
			if (!removed) {
				throw new IllegalArgumentException("no such child: " + child);
			}
		}
	}

	public static class ProfileNode extends LeafNode {

		private final IPrefMapEntry<ITacticDescriptor> entry;
		
		public ProfileNode(IPrefMapEntry<ITacticDescriptor> entry) {
			this.entry = entry;
		}
		
		public ProfileNode(ITacticNode parent, IPrefMapEntry<ITacticDescriptor> entry) {
			super(parent);
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
		public boolean isValid() {
			return entry.getValue() != null;
		}
		
	}
	
	private TreeViewer treeViewer;
	
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
	public Tree getControl() {
		if (treeViewer == null) {
			return null;
		}
		return treeViewer.getTree();
	}

	void refresh(ITacticNode changedNode) {
		final ITacticNode parentNode = changedNode.getParent();
		if (parentNode == null) {
			treeViewer.refresh(changedNode);
		} else {
			treeViewer.refresh(parentNode);
		}
	}
	
	public void addDragAndDropSupport() {
		final Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer
				.getTransfer() };
		
		final TreeDragSourceEffect drag = new TacticDragEffect(this);

		final ViewerDropAdapter drop = new TacticDrop(treeViewer, this);
		drop.setScrollExpandEnabled(true);
		
		treeViewer.addDragSupport(DND.DROP_MOVE, transferTypes, drag);
		treeViewer.addDropSupport(DND.DROP_MOVE, transferTypes, drop);
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
