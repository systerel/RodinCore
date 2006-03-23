package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eventb.core.IAxiom;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class AxiomEditableTableViewer extends EventBEditableTableViewer {

	public AxiomEditableTableViewer(Composite parent, int style, IRodinFile rodinFile) {
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
			case 1:  // Commit name
				try {
					UIUtils.debug("Commit content: " + ((IInternalElement) itemData).getContents() + " to be : " + text);
					if (!((IInternalElement) itemData).getContents().equals(text)) {
						((IInternalElement) itemData).setContents(text);
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
			int counter = rodinFile.getChildrenOfType(IAxiom.ELEMENT_TYPE).length;
			IInternalElement element = rodinFile.createInternalElement(IAxiom.ELEMENT_TYPE, "axm"+(counter+1), null, null);
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
		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");
		nameColumn.setResizable(true);
		nameColumn.setWidth(100);

		
		TableColumn predicateColumn = new TableColumn(table, SWT.LEFT);
		predicateColumn.setText("Predicate");
		predicateColumn.setResizable(true);
		predicateColumn.setWidth(250);
		
		table.setHeaderVisible(true);
	}

}
