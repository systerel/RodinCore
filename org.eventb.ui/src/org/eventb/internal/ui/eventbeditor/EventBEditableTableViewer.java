/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 * <p>
 * An abstract class contains a table part with buttons
 * for displaying Rodin elements (used as master section in Master-Detail block).
 */
public abstract class EventBEditableTableViewer
	extends TableViewer
{

	private TableEditor editor;

	// The Rodin File where the information belongs to.
	protected IRodinFile rodinFile;
	
	abstract protected void commit(int row, int col, String text);
	abstract protected void newElement(Table table, TableItem item, int column);

	/**
	 * Constructor.
	 * <p>
	 * @param managedForm The form used to create the part
	 * @param parent The composite parent
	 * @param toolkit The Form Toolkit used to create the part
	 * @param style The style used to creat the part
	 * @param block The master-detail block contains this part
	 */
	public EventBEditableTableViewer(Composite parent, int style, IRodinFile rodinFile) {
		super(parent, style);
		this.rodinFile = rodinFile;
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		Table table = this.getTable();
		table.setLayoutData(gd);
		createTableColumns(table);
		
		
		editor = new TableEditor(table);
		editor.grabHorizontal = true;
		table.addMouseListener(new MouseAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
			 */
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
			 */
			public void mouseDown(MouseEvent e) {
				Table table = EventBEditableTableViewer.this.getTable();
				Control old = editor.getEditor();
		        if (old != null) old.dispose();

		        // Determine where the mouse was clicked
		        Point pt = new Point(e.x, e.y);

		        TableItem item = table.getItem(pt);
		        
                // Determine which column was selected
	        	if (item != null) {
	        		int row = table.indexOf(item);
			        int column = -1;
		        	for (int i = 0, n = table.getColumnCount(); i < n; i++) {
		        		Rectangle rect = item.getBounds(i);
		        		if (rect.contains(pt)) {
		        			// This is the selected column
		        			column = i;
		        			break;
		        		}
		        	}
					selectItem(item, row, column);
	        	}
			}
		});
	}
	
	protected abstract void createTableColumns(Table table);
	
	protected void selectRow(int row, int column) {
		TableItem item = this.getTable().getItem(row);
		if (item != null) selectItem(item, row, column);
	}
	
	private void selectItem(TableItem item, int row, int column) {
		Table table = EventBEditableTableViewer.this.getTable();
		
        UIUtils.debug("Item " + item);
        
        // Set the selection of the viewer
        IStructuredSelection ssel = (IStructuredSelection) this.getSelection();
        if (!ssel.toList().contains(item.getData())) 
        	this.setSelection(new StructuredSelection(item.getData()));

        select(table, editor, item, column, row);
	}
	
	protected void select(final Table table, final TableEditor editor, final TableItem item, final int column, final int row) {
		final Color black = table.getDisplay().getSystemColor (SWT.COLOR_BLACK);

		boolean isCarbon = SWT.getPlatform ().equals ("carbon");
		final Composite composite = new Composite (table, SWT.NONE);
		if (!isCarbon) composite.setBackground (black);
		final Text text = new Text (composite, SWT.NONE);
		new EventBMath(text);
		final int inset = isCarbon ? 0 : 1;
		composite.addListener (SWT.Resize, new Listener () {
			public void handleEvent (Event e) {
				UIUtils.debug("Event: " + e.toString());
				Rectangle rect = composite.getClientArea ();
				UIUtils.debug("Rectangle: " + rect.toString());
				text.setBounds (rect.x + inset, rect.y + inset, rect.width - inset * 2, rect.height - inset * 2);
			}
		});
		Listener textListener = new Listener () {
			public void handleEvent (final Event e) {
				switch (e.type) {
					case SWT.FocusOut:
						UIUtils.debug("FocusOut");
						commit(row, column, text.getText());
						item.setText (column, text.getText());
						composite.dispose ();
						break;
					case SWT.Verify:
						UIUtils.debug("Verify");
						editor.horizontalAlignment = SWT.LEFT;
						editor.layout();
						break;
					case SWT.Traverse:
						switch (e.detail) {
							case SWT.TRAVERSE_RETURN:
								UIUtils.debug("TraverseReturn");
								commit(row, column, text.getText ());
								composite.dispose();
								e.doit = false;
								break;
							case SWT.TRAVERSE_TAB_NEXT:
								UIUtils.debug("Traverse Tab Next");
								commit(row, column, text.getText ());
								composite.dispose ();
								e.doit = false;
								if (table.getItem(table.getItemCount() - 1).equals(item)) {
									newElement(table, item, column);
								}
								else {
									int row = table.indexOf(item);
									selectRow(row + 1, column);
								}
								break;
							case SWT.TRAVERSE_TAB_PREVIOUS:
								UIUtils.debug("Traverse Tab Previous");
								int row = table.indexOf(item);
								commit(row, column, text.getText ());
								composite.dispose ();
								e.doit = false;
								if (row != 0) selectRow(row - 1, column);
								break;
							case SWT.TRAVERSE_ESCAPE:
								composite.dispose ();
								e.doit = false;
						}
						break;
				}
			}
				};
		text.addListener (SWT.FocusOut, textListener);
		text.addListener (SWT.Traverse, textListener);
		text.addListener (SWT.Verify, textListener);
		editor.setEditor(composite, item, column);
		text.setText (item.getText(column));
		text.selectAll ();
		text.setFocus ();
    }

}
