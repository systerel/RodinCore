package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinDBException;

public class SyntheticEditableTreeViewer 
	extends EventBEditableTreeViewer
{

	private EventBEditor editor;
	
	/**
	 * The content provider class. 
	 */
	class SyntheticContentProvider
	implements IStructuredContentProvider, ITreeContentProvider
	{
		private IRodinFile invisibleRoot = null;
		
		public Object getParent(Object child) {
			if (child instanceof IRodinElement) return ((IRodinElement) child).getParent();
			return null;
		}
		
		public Object[] getChildren(Object parent) {
			if (parent instanceof IRodinFile) {
				ArrayList<Leaf> list = new ArrayList<Leaf>();
				try {
					IRodinElement [] elements = ((IRodinFile) parent).getChildren();
					for (IRodinElement element : elements) {
						Leaf leaf;
//						UIUtils.debug("Element: " + element.getElementName());
						if (element instanceof IParent) {
							leaf = new Node(element);
						}
						else leaf = new Leaf(element);
						elementsMap.put(element, leaf);
						list.add(leaf);
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
					invisibleRoot = (IRodinFile) parent;
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

	/**
	 * @author htson
	 * This class provides the label for different elements in the tree.
	 */
	class SyntheticLabelProvider 
		implements  ITableLabelProvider, ITableFontProvider, ITableColorProvider {
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			IRodinElement rodinElement = ((Leaf) element).getElement();
			if (columnIndex != 0) return null;
			return UIUtils.getImage(rodinElement);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			IRodinElement rodinElement = ((Leaf) element).getElement();
			
			if (columnIndex == 0) {
				if (rodinElement instanceof IUnnamedInternalElement) return "";
				if (rodinElement instanceof IInternalElement) return ((IInternalElement) rodinElement).getElementName();
				return rodinElement.toString();
			}
			
			if (columnIndex == 1) {
				try {
					if (rodinElement instanceof IInternalElement) return ((IInternalElement) rodinElement).getContents();
				}
				catch (RodinDBException e) {
					e.printStackTrace();
				}
				return rodinElement.toString();
			}
			
			return rodinElement.toString();

		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
		 */
		public Color getBackground(Object element, int columnIndex) {
			 Display display = Display.getCurrent();
			 
			 if (editor.isNewElement(((Leaf) element).getElement()))
				 return display.getSystemColor(SWT.COLOR_YELLOW);
             return display.getSystemColor(SWT.COLOR_WHITE);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
		 */
		public Color getForeground(Object element, int columnIndex) {
			Display display = Display.getCurrent();
			 if (editor.isNewElement(((Leaf) element).getElement()))
				 return display.getSystemColor(SWT.COLOR_DARK_MAGENTA);
            return display.getSystemColor(SWT.COLOR_BLACK);
       }
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object, int)
		 */
		public Font getFont(Object element, int columnIndex) {
//			UIUtils.debug("Get fonts");
			return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		}
			
	}

	/**
	 * @author htson
	 * This class sorts the elements by types.
	 */
	private class SyntheticElementsSorter extends ViewerSorter {
		
		public int compare(Viewer viewer, Object e1, Object e2) {
	        int cat1 = category(e1);
	        int cat2 = category(e2);
	        return cat1 - cat2;
		}
		
		public int category(Object obj) {
			IRodinElement rodinElement = ((Leaf) obj).getElement();
			if (rodinElement instanceof IVariable) return 1;
			if (rodinElement instanceof IInvariant) return 2;
			if (rodinElement instanceof ITheorem) return 4;
			if (rodinElement instanceof IEvent) return 5;
			if (rodinElement instanceof IGuard) return 2;
			if (rodinElement instanceof IAction) return 3;
			if (rodinElement instanceof ICarrierSet) return 1;
			if (rodinElement instanceof IConstant) return 2;
			if (rodinElement instanceof IAxiom) return 3;
			return 0;
		}
	}
	
	public SyntheticEditableTreeViewer(EventBEditor editor, Composite parent, int style) {
		super(parent, style);
		this.editor = editor;
		this.setContentProvider(new SyntheticContentProvider());
		this.setLabelProvider(new SyntheticLabelProvider());
		this.setSorter(new SyntheticElementsSorter());
	}

	@Override
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
	protected void commit(Leaf leaf, int col, String text) {
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

	@Override
	protected boolean isNotSelectable(Object object, int column) {
		if (!(object instanceof Leaf)) return false;
		object = ((Leaf) object ).getElement();
		
		if (column == 0) {
			if (!editor.isNewElement((IRodinElement) object)) return true;
		}
		//        if (column < 1) return; // The object column is not editable
//		UIUtils.debug("Item: " + object + " of class: " + object.getClass());
		if (column == 0) {
			if (object instanceof IUnnamedInternalElement) return true;
		}
		else if (column == 1) {
			if (object instanceof IVariable) return true;
			if (object instanceof IEvent) return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.IStatusChangedListener#statusChanged(java.util.Collection)
	 */
	public void statusChanged(final IRodinElement element) {
		UIUtils.debug("Change status " + element.getElementName());
		final TreeViewer viewer = this;
		UIUtils.syncPostRunnable(new Runnable() {
			public void run() {
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					Leaf leaf = elementsMap.get(element);
					viewer.refresh(leaf);
				}
			}
		}, this.getControl());
	}
}
