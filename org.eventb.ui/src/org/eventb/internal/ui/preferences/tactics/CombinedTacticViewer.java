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

import static org.eventb.internal.ui.preferences.tactics.TacticPreferenceUtils.packAll;
import static org.eventb.internal.ui.utils.Messages.tacticviewer_combined_action_delete;
import static org.eventb.internal.ui.utils.Messages.tacticviewer_combined_unboundarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEffect;
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

		public TacticDrop(CombinedTacticViewer tacticViewer) {
			super(tacticViewer);
			
		}

		@Override
		public boolean validateDrop(Object target, int operation,
				TransferData transferType) {
			if (target == null) {
				// OK if tree is empty
				return getViewer().getInput() == null;
			}
			if (!(target instanceof ITacticNode)) {
				return false;
			}
			final ITacticNode targetNode = (ITacticNode) target;

			final ITacticNode parent = targetNode.getParent();

			final List<ITacticNode> selectedNodes = getSelectedNodes();
			
			switch (getCurrentLocation()) {
			case LOCATION_NONE:
				// should not happen if target != null
				return false;
			case LOCATION_ON:
				if (!(targetNode instanceof LeafNode)) {
					return targetNode.canDrop(selectedNodes);
				}
				// no break on purpose: drop on leaf
			case LOCATION_BEFORE:
			case LOCATION_AFTER:
				return parent != null && parent.canDrop(selectedNodes);
			default:
				return false;
			}

		}

		@Override
		public boolean performDrop(Object data) {
			final List<ITacticNode> selectedNodes = getSelectedNodes();
			if (selectedNodes == null) {
				return false;
			}
			
			final Object target = getCurrentTarget();
			if (!(target instanceof ITacticNode) && !isTreeEmpty()) {
				// pointing empty space on non empty tree
				return false;
			}
			final ITacticNode targetNode = (ITacticNode) target;
			
			final ITacticNode parent;
			final ITacticNode nextSibling;
			
			switch (getCurrentLocation()) {
			case LOCATION_NONE:
				// empty tree, checked above (target == null)
				parent = null;
				nextSibling = null;
				break;
			case LOCATION_ON:
				if (!(targetNode instanceof LeafNode)) {
					parent = targetNode;
					nextSibling = null;
					break;
				}
				// no break on purpose: drop on leaf
			case LOCATION_BEFORE:
				parent = targetNode.getParent();
				nextSibling = targetNode;
				break;
			case LOCATION_AFTER:
				parent = targetNode.getParent();
				final ITacticNode[] children = parent.getChildren();
				final List<ITacticNode> childList = Arrays.asList(children);
				final int targetIndex = childList.indexOf(targetNode);
				if (targetIndex <0) {
					return false;
				}
				if (targetIndex == childList.size() - 1) {
					// drop after last
					nextSibling = null;
				} else {
					nextSibling = childList.get(targetIndex + 1);
				}
				break;
			default:
				return false;
			}
			drop(selectedNodes, parent, nextSibling);
			return true;
		}
		
		private boolean isTreeEmpty() {
			return getViewer().getInput() == null;
		}
		
		private boolean drop(List<ITacticNode> nodes, ITacticNode parent,
				ITacticNode nextSibling) {
			final CombinedTacticViewer viewer = (CombinedTacticViewer) getViewer();
			if (parent == null) {
				if (nodes.size() == 1) {
					viewer.setInput(nodes.get(0));
					viewer.refresh();
					return true;
				}
				return false;
			}
			for (ITacticNode node : nodes) {
				parent.addChild(node, nextSibling);
			}
			viewer.refresh(parent);
			return true;
		}
		
		private static List<ITacticNode> getSelectedNodes() {
			final ISelection selection = LocalSelectionTransfer.getTransfer()
					.getSelection();
			if (!(selection instanceof IStructuredSelection)) {
				return null;
			}
			final IStructuredSelection sel = (IStructuredSelection) selection;
			final List<ITacticNode> selNodes = new ArrayList<ITacticNode>();
			for (Object selected : sel.toList()) {
				if (selected instanceof ITacticNode) {
					selNodes.add((ITacticNode) selected);
				} else {
					return null;
				}
			}
			return selNodes;
		}
	}

	public static class ViewerSelectionDragEffect extends DragSourceEffect {

		private final Viewer viewer;
		
		public ViewerSelectionDragEffect(Viewer viewer) {
			super(viewer.getControl());
			this.viewer = viewer;
		}
		
		@Override
		public void dragStart(DragSourceEvent event) {
			super.dragStart(event);
			final ISelection selection = viewer.getSelection();
			if (!selection.isEmpty()) {
				LocalSelectionTransfer.getTransfer().setSelection(selection);
			}
		}
		
		@Override
		public void dragSetData(DragSourceEvent event) {
			final ISelection selection = LocalSelectionTransfer.getTransfer()
					.getSelection();

			if (LocalSelectionTransfer.getTransfer()
					.isSupportedType(event.dataType)) {
				event.data = selection;
			} else {
				event.doit = false;
			}
		}

	}
	
	private static class TacticViewerDragEffect extends TreeDragSourceEffect {
		// TODO factorize duplicate code with ViewerSelectionDragEffect
		private final CombinedTacticViewer viewer;

		public TacticViewerDragEffect(CombinedTacticViewer viewer) {
			super(viewer.getControl());
			this.viewer = viewer;
		}

		@Override
		public void dragStart(DragSourceEvent event) {
			super.dragStart(event);
			final ISelection selection = viewer.getSelection();
			if (!selection.isEmpty()) {
				LocalSelectionTransfer.getTransfer().setSelection(selection);
			}
		}
		
		@Override
		public void dragSetData(DragSourceEvent event) {
			final ISelection selection = LocalSelectionTransfer.getTransfer()
					.getSelection();

			if (LocalSelectionTransfer.getTransfer()
					.isSupportedType(event.dataType)) {
				event.data = selection;
			} else {
				event.doit = false;
			}
		}

		@Override
		public void dragFinished(DragSourceEvent event) {
			if (!event.doit) {
				return;
			}
			final ISelection selection = LocalSelectionTransfer.getTransfer()
					.getSelection();
			if (!(selection instanceof ITreeSelection)) {
				return;
			}
			deleteDraggedNodes((ITreeSelection) selection);
			LocalSelectionTransfer.getTransfer().setSelection(null);
			super.dragFinished(event);
		}

		private void deleteDraggedNodes(ITreeSelection sel) {
			for (Object selected : sel.toList()) {
				if (!(selected instanceof ITacticNode)) {
					continue;
				}
				final ITacticNode node = (ITacticNode) selected;
				node.delete();
				final ITacticNode parent = node.getParent();
				if (parent != null) {
					viewer.refresh(parent);
				}
			}
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
		void addChild(ITacticNode node, ITacticNode nextSibling);
		boolean canDrop(List<ITacticNode> nodes);
		void delete();
		void deleteChild(ITacticNode child);
		ITacticNode copyWithParent(ITacticNode newParent);
		String getDescription();
	}

	public static ITacticNode makeTacticNode(ITacticNode parent, Object descOrNode) {
		if (descOrNode instanceof ITacticDescriptor) {
			if (descOrNode instanceof ICombinedTacticDescriptor) {
				return new CombinatorNode(parent,
						(ICombinedTacticDescriptor) descOrNode);
			} else {
				// simple or ref (no param here)
				return new SimpleNode(parent, (ITacticDescriptor) descOrNode);
			}
		}
		if (descOrNode instanceof ICombinatorDescriptor) {
			return new CombinatorNode(parent,
					(ICombinatorDescriptor) descOrNode);
		}
		if (descOrNode instanceof ITacticNode) {
			final ITacticNode node = (ITacticNode) descOrNode;
			return node.copyWithParent(parent);
		}
		throw new IllegalArgumentException("illegal descriptor : " + descOrNode); //$NON-NLS-1$
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
		public boolean canDrop(List<ITacticNode> nodes) {
			return false;
		}
		
		@Override
		public void deleteChild(ITacticNode child) {
			// nothing to do
		}
		
		@Override
		public void addChild(ITacticNode node, ITacticNode nextSibling) {
			throw new UnsupportedOperationException("a leaf has no child !"); //$NON-NLS-1$
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

		@Override
		public ITacticNode copyWithParent(ITacticNode newParent) {
			return new SimpleNode(newParent, desc);
		}

		@Override
		public String getDescription() {
			return desc.getTacticDescription();
		}
		
	}
	
	public static class CombinatorNode extends AbstractTacticNode {
		private final ICombinatorDescriptor combinator;
		private final List<ITacticNode> children;
		private final String text;

		public CombinatorNode(ITacticNode parent, ICombinatorDescriptor combinator) {
			super(parent);
			this.combinator = combinator;
			this.children = new ArrayList<ITacticNode>();
			this.text = computeText(combinator);
		}

		public CombinatorNode(ITacticNode parent, ICombinedTacticDescriptor desc) {
			super(parent);
			final String combinatorId = desc.getCombinatorId();
			final IAutoTacticRegistry reg = SequentProver
					.getAutoTacticRegistry();
			this.combinator = reg.getCombinatorDescriptor(combinatorId);
			if (combinator == null) {
				throw new IllegalArgumentException("invalid combinator: " //$NON-NLS-1$
						+ combinatorId);
			}
			final List<ITacticDescriptor> combined = desc.getCombinedTactics();
			this.children = computeNodes(combined);
			this.text = computeText(combinator);
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
		
		private static String computeText(ICombinatorDescriptor combinator) {
			final String tacticName = combinator.getTacticDescriptor().getTacticName();
			final StringBuilder sb = new StringBuilder();
			sb.append(tacticName);
			sb.append(" [");
			final int minArity = combinator.getMinArity();
			sb.append(minArity);
			if (!combinator.isArityBound()) {
				sb.append(tacticviewer_combined_unboundarity);
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public ITacticDescriptor getResultDesc() {
			if (!isValid()) {
				throw new IllegalStateException(
						"computing an invalid descriptor from " //$NON-NLS-1$
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
					.getTacticID() + ".combined"; //$NON-NLS-1$
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
			return text;
		}

		@Override
		public void addChild(ITacticNode node, ITacticNode nextSibling) {
			final ITacticNode newChild = makeNewChild(this, node);
			final int index;
			if (nextSibling == null) {
				// insert at the end
				index = children.size();
			} else {
				index = children.indexOf(nextSibling);
				if (index < 0) {
					throw new IllegalArgumentException("no such child: " //$NON-NLS-1$
							+ node.getText() + " in " + getText()); //$NON-NLS-1$
				}
			}
			children.add(index, newChild);
		}
		
		// make a new instance to distinguish dragged from dropped
		private static ITacticNode makeNewChild(ITacticNode parent, ITacticNode node) {
			if(node instanceof CombinatorNode) {
				// avoid computing result desc as it may be invalid
				final CombinatorNode combNode = (CombinatorNode) node;
				final CombinatorNode newChild = new CombinatorNode(parent, combNode.combinator);
				for(ITacticNode combChild: combNode.children) {
					final ITacticNode newChildChild = makeNewChild(newChild, combChild);
					newChild.children.add(newChildChild);
				}
				return newChild;
			}
			return makeTacticNode(parent, node);
		}
		
		@Override
		public String toString() {
			return getText();
		}

		@Override
		public boolean canDrop(List<ITacticNode> nodes) {
			final List<ITacticNode> hierarchy = new ArrayList<ITacticNode>();
			addAncestors(this, hierarchy);
			
			hierarchy.retainAll(nodes);
			if (!hierarchy.isEmpty()) {
				// trying to drop this or an ancestor
				return false;
			}
			
			// avoid adding too many children
			if (!combinator.isArityBound()) {
				return true;
			}

			int sizeAfterDrop = children.size();
			for (ITacticNode node : nodes) {
				if (!children.contains(node)) {
					sizeAfterDrop++;
				}
			}
			
			// bound => min = max
			final int maxArity = combinator.getMinArity();
			
			return sizeAfterDrop <= maxArity;
		}
		
		private static void addAncestors(ITacticNode node, List<ITacticNode> nodes) {
			nodes.add(node);
			final ITacticNode parent = node.getParent();
			if (parent != null) {
				addAncestors(parent, nodes);
			}
		}

		@Override
		public void deleteChild(ITacticNode child) {
			final boolean removed = children.remove(child);
			if (!removed) {
				throw new IllegalArgumentException("no such child: " + child); //$NON-NLS-1$
			}
		}

		@Override
		public ITacticNode copyWithParent(ITacticNode newParent) {
			return makeNewChild(newParent, this);
		}

		@Override
		public String getDescription() {
			return combinator.getTacticDescriptor().getTacticDescription();
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

		@Override
		public ITacticNode copyWithParent(ITacticNode newParent) {
			return new ProfileNode(newParent, entry);
		}

		@Override
		public String getDescription() {
			return getText();
		}
		
	}
	
	public static class DeleteNodeAction extends Action {
		private final CombinedTacticViewer viewer;

		public DeleteNodeAction(CombinedTacticViewer viewer) {
			this.viewer = viewer;
			setActionDefinitionId("org.eclipse.ui.edit.delete"); //$NON-NLS-1$
			setText(tacticviewer_combined_action_delete);
		}
		
		@Override
		public void run() {
			final ISelection selection = viewer.getSelection();
			if (!(selection instanceof ITreeSelection)) {
				return;
			}
			final ITreeSelection treeSel = (ITreeSelection) selection;
			final TreePath[] paths = treeSel.getPaths();
			if (paths.length == 0) {
				return;
			}
			// TODO filter child path when parent path is present
			for (TreePath path : paths) {
				final int segmentCount = path.getSegmentCount();
				if (segmentCount == 1) {
					// deleting root
					viewer.setInput(null);
					viewer.refresh();
					return;
				}
				final Object element = path.getLastSegment();
				if (!(element instanceof ITacticNode)) {
					continue;
				}
				final ITacticNode node = (ITacticNode) element;
				final ITacticNode parent = node.getParent();
				node.delete();
				if (parent != null) {
					viewer.refresh(parent);
				}
			}
		}
	}
	
	public static interface ITacticRefreshListener {
		void tacticRefreshed();
	}
	
	private TreeViewer treeViewer;
	private final List<ITacticRefreshListener> listeners = new ArrayList<ITacticRefreshListener>();
	
	@Override
	public void createContents(Composite parent) {
		treeViewer = new TreeViewer(parent);
		final Tree tree = treeViewer.getTree();
		tree.setLayout(new GridLayout());
		final GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumWidth = 200;
		layoutData.minimumHeight = 200;
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
	}

	@Override
	public Tree getControl() {
		if (treeViewer == null) {
			return null;
		}
		return treeViewer.getTree();
	}

	public void addTacticRefreshListener(ITacticRefreshListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removedTacticRefreshListener(ITacticRefreshListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners() {
		for(ITacticRefreshListener listener:listeners) {
			listener.tacticRefreshed();
		}
	}
	
	public void refresh(ITacticNode node) {
		treeViewer.refresh(node);
		treeViewer.expandToLevel(node, 1);
		internalPack(treeViewer.getTree());
		notifyListeners();
	}

	@Override
	public void refresh() {
		treeViewer.refresh();
		internalPack(treeViewer.getTree());
		notifyListeners();
	}

	private static void internalPack(Composite c) {
		packAll(c, 8);
		
//		final Point size = c.getSize();
//		final Point preferredSize = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);
//		preferredSize.x = Math.max(preferredSize.x, size.x);
//		preferredSize.y = Math.max(preferredSize.y, size.y);
//		if (!preferredSize.equals(size)) {
//			c.setSize(preferredSize);
//			c.redraw();
//			final Composite parent = c.getParent();
//			if (parent != null) {
//				internalPack(parent);
//			}
//		}

	}
	
	public void addEditSupport() {
		addDND();
		addPopupMenu();
	}

	private void addDND() {
		final Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer
				.getTransfer() };
		
		final TreeDragSourceEffect drag = new TacticViewerDragEffect(this);

		final ViewerDropAdapter drop = new TacticDrop(this);
		drop.setScrollExpandEnabled(true);
		
		treeViewer.addDragSupport(DND.DROP_MOVE, transferTypes, drag);
		treeViewer.addDropSupport(DND.DROP_MOVE, transferTypes, drop);
	}
	
	private void addPopupMenu() {
		final MenuManager mgr = new MenuManager();
		mgr.add(new DeleteNodeAction(this));
		final Tree tree = treeViewer.getTree();
		tree.setMenu(mgr.createContextMenu(tree));
	}

	private ITacticNode getTopNode() {
		final Tree tree = treeViewer.getTree();
		if (tree.isDisposed()) {
			return null;
		}
		// tree.getTopItem() is not reliable
		final TreeItem[] items = tree.getItems();
		if (items.length == 0) {
			return null;
		}
		if (items.length > 1) {
			return null;
		}
		final TreeItem topItem = items[0];
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
	public Object getInput() {
		return treeViewer.getInput();
	}

	@Override
	public ISelection getSelection() {
		return treeViewer.getSelection();
	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		treeViewer.setSelection(selection, reveal);
	}
	
	@Override
	public IContentProvider getContentProvider() {
		return treeViewer.getContentProvider();
	}

	@Override
	public IBaseLabelProvider getLabelProvider() {
		return treeViewer.getLabelProvider();
	}

	@Override
	public void setInput(Object input) {
		treeViewer.setInput(input);
	}
	
	@Override
	public void setSelection(ISelection selection) {
		treeViewer.setSelection(selection);
	}

	@Override
	public void dispose() {
		listeners.clear();
		super.dispose();
	}
	
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		treeViewer.addSelectionChangedListener(listener);
	}
	
	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		treeViewer.removeSelectionChangedListener(listener);
	}
}
