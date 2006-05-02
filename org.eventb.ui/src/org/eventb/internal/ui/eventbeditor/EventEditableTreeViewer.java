package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eventb.core.IEvent;
import org.eventb.core.IMachine;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinDBException;

public class EventEditableTreeViewer extends EventBEditableTreeViewer {

	/**
	 * The content provider class. 
	 */
	class EventContentProvider
	implements IStructuredContentProvider, ITreeContentProvider
	{
		private IMachine invisibleRoot = null;
		
		public Object getParent(Object child) {
			if (child instanceof IRodinElement) return ((IRodinElement) child).getParent();
			return null;
		}
		
		public Object[] getChildren(Object parent) {
//			UIUtils.debug("Get Children: " + parent);
			if (parent instanceof IMachine) {
				ArrayList<Node> list = new ArrayList<Node>();
				try {
					IRodinElement [] events =   ((IMachine) parent).getChildrenOfType(IEvent.ELEMENT_TYPE);
					for (IRodinElement event : events) {
//						UIUtils.debug("Event: " + event.getElementName());
						Node node = new Node(event);
						elementsMap.put(event, node);
						list.add(node);
					}
				}
				catch (RodinDBException e) {
					// TODO Exception handle
					e.printStackTrace();
				}
				return list.toArray();
			}
			
			if (parent instanceof Node) {
				Node node = (Node) parent;
				node.removeAllChildren();
				try {
					IRodinElement element = node.getElement();
					
					if (element instanceof IParent) {
						IRodinElement [] children = ((IParent) element).getChildren();
						for (IRodinElement child : children) {
							Leaf leaf;
							if (child instanceof IParent) leaf = new Node(child);
							else leaf = new Leaf(child);
							elementsMap.put(child, leaf);
							node.addChildren(leaf);
						}
					}
				}
				catch (RodinDBException e) {
					e.printStackTrace();
				}
				return node.getChildren();
			}
			
			return new Object[0];
		}
		
		public boolean hasChildren(Object parent) {
			return getChildren(parent).length > 0;
		}
		
		public Object[] getElements(Object parent) {
			if (parent instanceof IRodinFile) {
				if (invisibleRoot == null) {
					invisibleRoot = (IMachine) parent;
					return getChildren(invisibleRoot);
				}
			}
			return getChildren(parent);
		}
		
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			invisibleRoot = null;
			elementsMap = new HashMap<IRodinElement, Leaf>();
		}
	}
	
	public EventEditableTreeViewer(EventBEditor editor, Composite parent, int style) {
		super(editor, parent, style);
		this.setContentProvider(new EventContentProvider());
		this.setLabelProvider(new EventBTreeLabelProvider(editor));
		this.setSorter(new RodinElementSorter());
	}
	
	public void commit(Leaf leaf, int col, String text) {
		// Determine which row was selected
		IInternalElement element = (IInternalElement) leaf.getElement();
		
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

		case 1:  // Commit content
			try {
				UIUtils.debug("Commit content: " + ((IInternalElement) element).getContents() + " to be : " + text);
				if (!((IInternalElement) element).getContents().equals(text)) {
					((IInternalElement) element).setContents(text);
				}
			}
			catch (RodinDBException e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
	protected void createTreeColumns() {
		Tree tree = this.getTree();
		TreeColumn elementColumn = new TreeColumn(tree, SWT.LEFT);
		elementColumn.setText("Elements");
		elementColumn.setResizable(true);
		elementColumn.setWidth(200);

		TreeColumn predicateColumn = new TreeColumn(tree, SWT.LEFT);
		predicateColumn.setText("Contents");
		predicateColumn.setResizable(true);
		predicateColumn.setWidth(250);
		
		tree.setHeaderVisible(true);
	}

	@Override
	protected boolean isNotSelectable(Object object, int column) {
		if (!(object instanceof Leaf)) return false;
		object = ((Leaf) object ).getElement();
		if (column == 0) {
			if (!editor.isNewElement((IRodinElement) object)) return true;
		}
		
		//        if (column < 1) return; // The object column is not editable
//		UIUtils.debug("Item: " + object.toString() + " of class: " + object.getClass());
		if (column == 0) {
			if (object instanceof IUnnamedInternalElement) return true;
		}
		else if (column == 1) {
			if (object instanceof IVariable) return true;
			if (object instanceof IEvent) return true;
		}
		return false;
	}
}
