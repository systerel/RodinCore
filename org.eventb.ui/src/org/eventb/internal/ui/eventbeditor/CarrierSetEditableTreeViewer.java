/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IContext;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This sub-class Event-B Editable table viewer for editing carrier set
 *         elements.
 */
public class CarrierSetEditableTreeViewer extends EventBEditableTreeViewer {


	/**
	 * The content provider class. 
	 */
	class CarrierSetContentProvider
	implements IStructuredContentProvider, ITreeContentProvider
	{
		private IContext invisibleRoot = null;
		
		public Object getParent(Object child) {
			if (child instanceof IRodinElement) return ((IRodinElement) child).getParent();
			return null;
		}
		
		public Object[] getChildren(Object parent) {
//			UIUtils.debug("Get Children: " + parent);
			if (parent instanceof IContext) {
//				ArrayList<Node> list = new ArrayList<Node>();
//				try {
//					IRodinElement [] sets =   ((IContext) parent).getChildrenOfType(ICarrierSet.ELEMENT_TYPE);
//					for (IRodinElement set : sets) {
////						UIUtils.debug("Event: " + event.getElementName());
//						Node node = new Node(set);
//						elementsMap.put(set, node);
//						list.add(node);
//					}
//				}
//				catch (RodinDBException e) {
//					// TODO Exception handle
//					e.printStackTrace();
//				}
//				return list.toArray();
				try {
					return ((IContext) parent).getChildrenOfType(ICarrierSet.ELEMENT_TYPE);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (parent instanceof IParent) {
				try {
					return ((IParent) parent).getChildren();
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				Node node = (Node) parent;
//				node.removeAllChildren();
//				try {
//					IRodinElement element = node.getElement();
//					
//					if (element instanceof IParent) {
//						IRodinElement [] children = ((IParent) element).getChildren();
//						for (IRodinElement child : children) {
//							Leaf leaf;
//							if (child instanceof IParent) leaf = new Node(child);
//							else leaf = new Leaf(child);
//							elementsMap.put(child, leaf);
//							node.addChildren(leaf);
//						}
//					}
//				}
//				catch (RodinDBException e) {
//					e.printStackTrace();
//				}
//				return node.getChildren();
			}
			
			return new Object[0];
		}
		
		public boolean hasChildren(Object parent) {
			return getChildren(parent).length > 0;
		}
		
		public Object[] getElements(Object parent) {
			if (parent instanceof IRodinFile) {
				if (invisibleRoot == null) {
					invisibleRoot = (IContext) parent;
					return getChildren(invisibleRoot);
				}
			}
			return getChildren(parent);
		}
		
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			invisibleRoot = null;
//			elementsMap = new HashMap<IRodinElement, Leaf>();
		}
	}
	
	public CarrierSetEditableTreeViewer(EventBEditor editor, Composite parent, int style) {
		super(editor, parent, style);
		this.setContentProvider(new CarrierSetContentProvider());
		this.setLabelProvider(new EventBTreeLabelProvider(editor));
		this.setSorter(new RodinElementSorter());
	}

	public void commit(IRodinElement element, int col, String text) {
				
		switch (col) {
		case 0:  // Commit name
			try {
				UIUtils.debug("Commit : " + element.getElementName() + " to be : " + text);
				if (!element.getElementName().equals(text)) {
					((IInternalElement) element).rename(text, false, null);
				}
			}
			catch (RodinDBException e) {
				e.printStackTrace();
			}
				
			break;

//		case 1:  // Commit content
//			try {
//				UIUtils.debug("Commit content: " + ((IInternalElement) element).getContents() + " to be : " + text);
//				if (!((IInternalElement) element).getContents().equals(text)) {
//					((IInternalElement) element).setContents(text);
//				}
//			}
//			catch (RodinDBException e) {
//				e.printStackTrace();
//			}
//			break;
		}
	}
	

	protected void createTreeColumns() {
		numColumn = 1;

		Tree tree = this.getTree();
		TreeColumn elementColumn = new TreeColumn(tree, SWT.LEFT);
		elementColumn.setText("Name");
		elementColumn.setResizable(true);
		elementColumn.setWidth(200);
//
//		TreeColumn predicateColumn = new TreeColumn(tree, SWT.LEFT);
//		predicateColumn.setText("Predicates");
//		predicateColumn.setResizable(true);
//		predicateColumn.setWidth(250);
//		
		tree.setHeaderVisible(true);
	}
	
	@Override
	protected boolean isNotSelectable(Object object, int column) {
		if (!(object instanceof IRodinElement)) return false;
		if (column == 0) {
			if (!editor.isNewElement((IRodinElement) object)) return true;
		}
		return false;
	}
	
	protected void edit(IRodinElement element) {
		this.reveal(element);
		TreeItem item  = TreeSupports.findItem(this.getTree(), element);
		selectItem(item, 0);
	}
}
