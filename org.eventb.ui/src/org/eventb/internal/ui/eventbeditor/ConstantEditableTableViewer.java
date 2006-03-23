package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eventb.core.IConstant;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class ConstantEditableTableViewer extends EventBEditableTableViewer {

	public ConstantEditableTableViewer(Composite parent, int style, IRodinFile rodinFile) {
		super(parent, style, rodinFile);
	}
	
	public void commit(int row, int col, String text) {
		// Determine which row was selected
        TableItem item = this.getTable().getItem(row);
        if (item == null) return; 
        Object itemData = item.getData();
		if (itemData instanceof IInternalElement) {
			switch (col) {
			case 0:  // Commit name
				try {
					UIUtils.debug("Commit : " + ((IInternalElement) itemData).getElementName() + " to be : " + text);
					if (!((IInternalElement) itemData).getElementName().equals(text)) {
						((IInternalElement) itemData).rename(text, false, null);
					}
				}
				catch (RodinDBException e) {
					e.printStackTrace();
				}
				
				break;
			}
		}
	}

	protected void newElement(Table table, TableItem item, int column) {
		try {
			int counter = rodinFile.getChildrenOfType(IConstant.ELEMENT_TYPE).length;
			IInternalElement element = rodinFile.createInternalElement(IConstant.ELEMENT_TYPE, "cst"+(counter+1), null, null);
			refresh();
			reveal(element);
			int row = table.indexOf(item);
			selectRow(row + 1, column);
		}
		catch (RodinDBException exception) {
			exception.printStackTrace();
		}
	}

	protected void createTableColumns(Table table) {
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText("Name");
		column.setResizable(true);
		column.setWidth(150);
		table.setHeaderVisible(true);
	}
	
}
