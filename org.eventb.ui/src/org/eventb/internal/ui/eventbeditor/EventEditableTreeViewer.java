package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class EventEditableTreeViewer extends EventBEditableTreeViewer {

	public EventEditableTreeViewer(Composite parent, int style, IRodinFile rodinFile) {
		super(parent, style, rodinFile);
	}
	
	public void commit(IRodinElement element, int col, String text) {
		// Determine which row was selected
//		IInternalElement rodinElement = (IInternalElement) leaf.getElement();
//        TreeItem item = this.getTree().getItem(pt);
//        if (item == null) return; 
//        Object itemData = item.getData();
//		if (itemData instanceof IInternalElement) {
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
//		}
	}

//	protected void newElement(Tree tree, TreeItem item, int column) {
//		try {
//			int counter = rodinFile.getChildrenOfType(IInvariant.ELEMENT_TYPE).length;
//			IInternalElement element = rodinFile.createInternalElement(IInvariant.ELEMENT_TYPE, "inv"+(counter+1), null, null);
//			refresh();
//			reveal(element);
//			int row = tree.indexOf(item);
//			selectRow(row + 1, column);
//		}
//		catch (RodinDBException exception) {
//			exception.printStackTrace();
//		}
//	}
	
	protected void createTreeColumns(Tree tree) {
		TreeColumn elementColumn = new TreeColumn(tree, SWT.LEFT);
		elementColumn.setText("Elements");
		elementColumn.setResizable(true);
		elementColumn.setWidth(200);

//		TreeColumn nameColumn = new TreeColumn(tree, SWT.LEFT);
//		nameColumn.setText("Name");
//		nameColumn.setResizable(true);
//		nameColumn.setWidth(150);
		
		TreeColumn predicateColumn = new TreeColumn(tree, SWT.LEFT);
		predicateColumn.setText("Contents");
		predicateColumn.setResizable(true);
		predicateColumn.setWidth(250);
		
		tree.setHeaderVisible(true);
	}

}
