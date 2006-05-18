package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
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
//				ArrayList<Node> list = new ArrayList<Node>();
//				try {
//					IRodinElement [] events =   ((IMachine) parent).getChildrenOfType(IEvent.ELEMENT_TYPE);
//					for (IRodinElement event : events) {
////						UIUtils.debug("Event: " + event.getElementName());
//						Node node = new Node(event);
//						elementsMap.put(event, node);
//						list.add(node);
//					}
//				}
//				catch (RodinDBException e) {
//					// TODO Exception handle
//					e.printStackTrace();
//				}
//				return list.toArray();
				try {
					return ((IMachine) parent).getChildrenOfType(IEvent.ELEMENT_TYPE);
				} catch (RodinDBException e) {
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
			}
//			if (parent instanceof Node) {
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
//			}
			
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
//			elementsMap = new HashMap<IRodinElement, Leaf>();
		}
	}
	
	public EventEditableTreeViewer(EventBEditor editor, Composite parent, int style) {
		super(editor, parent, style);
		this.setContentProvider(new EventContentProvider());
		this.setLabelProvider(new EventBTreeLabelProvider(editor));
		this.setSorter(new RodinElementSorter());
	}
	
	public void commit(IRodinElement element, int col, String text) {
//		IInternalElement element = (IInternalElement) leaf.getElement();
		
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
		numColumn = 2;

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
		if (!(object instanceof IRodinElement)) return false;
		IRodinElement element = (IRodinElement) object;
		if (column == 0) {
			if (!editor.isNewElement(element)) return true;
		}
		if (column == 0) {
			if (element instanceof IUnnamedInternalElement) return true;
		}
		else if (column == 1) {
			if (element instanceof IVariable) return true;
			if (element instanceof IEvent) return true;
		}
		return false;
	}
	
	protected void edit(IRodinElement element) {
		this.reveal(element);
		TreeItem item  = TreeSupports.findItem(this.getTree(), element);
		if (element instanceof IUnnamedInternalElement) selectItem(item, 1);
		else if (element instanceof IVariable) selectItem(item, 0);
		else if (element instanceof IEvent) selectItem(item, 0);
		else selectItem(item, 1);
	}
}
