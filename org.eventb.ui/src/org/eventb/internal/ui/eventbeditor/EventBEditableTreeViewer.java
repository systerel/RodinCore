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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.IEvent;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IUnnamedInternalElement;

/**
 * @author htson
 * <p>
 * An abstract class contains a tree part with buttons
 * for displaying Rodin elements (used as master section in Master-Detail block).
 */
public abstract class EventBEditableTreeViewer
	extends TreeViewer
{
	
	private TreeEditor editor;

	// The Rodin File where the information belongs to.
	protected IRodinFile rodinFile;
	
//	abstract protected void commit(int row, int col, String text);
//	protected abstract void newElement(Tree tree, TreeItem item, int column);
	
	protected abstract void createTreeColumns(Tree tree);
	
	protected abstract void commit(IInternalElement elemnt, int col, String text);
	
	/**
	 * Constructor.
	 * <p>
	 * @param managedForm The form used to create the part
	 * @param parent The composite parent
	 * @param toolkit The Form Toolkit used to create the part
	 * @param style The style used to creat the part
	 * @param block The master-detail block contains this part
	 */
	public EventBEditableTreeViewer(Composite parent, int style, IRodinFile rodinFile) {
		super(parent, style);
		this.rodinFile = rodinFile;
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		Tree tree = this.getTree();
		tree.setLayoutData(gd);
		
		createTreeColumns(tree);
		
		
		editor = new TreeEditor(tree);
		editor.grabHorizontal = true;
		tree.addMouseListener(new MouseAdapter() {

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
				Tree tree = EventBEditableTreeViewer.this.getTree();
				Control old = editor.getEditor();
		        if (old != null) old.dispose();

		        // Determine where the mouse was clicked
		        Point pt = new Point(e.x, e.y);

		        TreeItem item = tree.getItem(pt);
                // Determine which column was selected
	        	if (item != null) {
			        int column = -1;
		        	for (int i = 0, n = tree.getColumnCount(); i < n; i++) {
		        		Rectangle rect = item.getBounds(i);
		        		if (rect.contains(pt)) {
		        			// This is the selected column
		        			column = i;
		        			break;
		        		}
		        	}
					selectItem(item, column);
	        	}
			}
		});
	}
	
//	public void selectRow(Point pt, int column) {
//		TreeItem item = this.getTree().getItem(pt);
//		if (item != null) selectItem(item, pt, column);
//	}
	
	public void selectItem(TreeItem item, int column) {
		Tree tree = EventBEditableTreeViewer.this.getTree();
		
        UIUtils.debug("Item " + item);
        
        // Set the selection of the viewer
        IStructuredSelection ssel = (IStructuredSelection) this.getSelection();
        if (!ssel.toList().contains(item.getData())) 
        	this.setSelection(new StructuredSelection(item.getData()));

        select(tree, editor, item, column);
	}
	
	protected void select(final Tree tree, final TreeEditor editor, final TreeItem item, final int column) {
		final Color black = tree.getDisplay().getSystemColor (SWT.COLOR_BLACK);
        if (column < 1) return; // The object column is not editable
//        UIUtils.debug("Item: " + item.getData() + " of class: " + item.getData().getClass());
        final Object itemData = item.getData();
        if (itemData instanceof IUnnamedInternalElement && column == 1) return;
        if (column == 2) {
        	if (itemData instanceof IVariable) return;
        	if (itemData instanceof IEvent) return;
        }
        
		boolean isCarbon = SWT.getPlatform ().equals ("carbon");
		final Composite composite = new Composite (tree, SWT.NONE);
		if (!isCarbon) composite.setBackground (black);
		final Text text = new Text (composite, SWT.NONE);
		new EventBMath(text);
		new TimerText(text) {

			/* (non-Javadoc)
			 * @see org.eventb.internal.ui.eventbeditor.TimerText#commit()
			 */
			@Override
			public void commit() {
				EventBEditableTreeViewer.this.commit((IInternalElement) itemData, column, text.getText());
			}
			
		};
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
						commit((IInternalElement) itemData, column, text.getText());
						item.setText (column, text.getText());
						composite.dispose ();
						break;
					case SWT.Verify:
//						UIUtils.debug("Verify");
						editor.horizontalAlignment = SWT.LEFT;
						editor.layout();
						break;
					case SWT.Traverse:
						switch (e.detail) {
							case SWT.TRAVERSE_RETURN:
								UIUtils.debug("TraverseReturn");
								commit((IInternalElement) itemData, column, text.getText ());
								composite.dispose();
								e.doit = false;
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
