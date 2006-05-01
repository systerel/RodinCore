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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
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
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An abstract class contains a tree part with buttons
 * for displaying Rodin elements (used as master section in Master-Detail block).
 */
public abstract class EventBEditableTreeViewer
	extends TreeViewer
{
	
	private TreeEditor treeEditor;
	
	/* Abstract methods */
	protected abstract void createTreeColumns();
	protected abstract boolean isNotSelectable(Object object, int column);
	protected abstract void commit(Leaf leaf, int col, String text);
	
	/**
	 * Constructor.
	 * <p>
	 * @param managedForm The form used to create the part
	 * @param parent The composite parent
	 * @param toolkit The Form Toolkit used to create the part
	 * @param style The style used to creat the part
	 * @param block The master-detail block contains this part
	 */
	public EventBEditableTreeViewer(Composite parent, int style) {
		super(parent, style);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		Tree tree = this.getTree();
		tree.setLayoutData(gd);
		
		createTreeColumns();
		
		treeEditor = new TreeEditor(tree);
		treeEditor.grabHorizontal = true;
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
				Control old = treeEditor.getEditor();
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
	
	public void selectItem(TreeItem item, int column) {
		Tree tree = EventBEditableTreeViewer.this.getTree();
		
        UIUtils.debug("Item " + item);
        
        // Set the selection of the viewer
        IStructuredSelection ssel = (IStructuredSelection) this.getSelection();
        if (!ssel.toList().contains(item.getData())) 
        	this.setSelection(new StructuredSelection(item.getData()));

        select(tree, treeEditor, item, column);
	}
	
	
	protected void select(
			final Tree tree, 
			final TreeEditor treeEditor, 
			final TreeItem item, 
			final int column) 
	{
        final Object itemData = item.getData();
		
        /* Check if the cell is editable or not */
        if (isNotSelectable(itemData, column)) return;
		
		final Color black = tree.getDisplay().getSystemColor (SWT.COLOR_BLACK);
		boolean isCarbon = SWT.getPlatform ().equals ("carbon");
		final Composite composite = new Composite (tree, SWT.NONE);
		if (!isCarbon) composite.setBackground (black);
		final Text text = new Text(composite, SWT.NONE); 
		new ElementText(text, treeEditor, item, tree, (Leaf) itemData, column) {
			/* (non-Javadoc)
			 * @see org.eventb.internal.ui.eventbeditor.ElementText#commit(org.rodinp.core.IRodinElement, int, java.lang.String)
			 */
			@Override
			public void commit(Leaf leaf, int column, String contents) {
				EventBEditableTreeViewer.this.commit(leaf, column, contents);
			}
			
		};
		new EventBMath(text);
		new TimerText(text) {

			/* (non-Javadoc)
			 * @see org.eventb.internal.ui.eventbeditor.TimerText#commit()
			 */
			@Override
			public void commit() {
				EventBEditableTreeViewer.this.commit((Leaf) itemData, column, text.getText());
			}
			
		};
		final int inset = isCarbon ? 0 : 1;
		composite.addListener (SWT.Resize, new Listener () {
			public void handleEvent (Event e) {
				Rectangle rect = composite.getClientArea ();
				text.setBounds (rect.x + inset, rect.y + inset, rect.width - inset * 2, rect.height - inset * 2);
			}
		});
		treeEditor.setEditor(composite, item, column);
		text.setText (item.getText(column));
		text.selectAll ();
		text.setFocus ();
    }
	
	
	// List of elements need to be refresh (when processing Delta of changes).
	private Collection<Object> toRefresh;
	private Collection<StatusObject> newStatus;
	protected HashMap<IRodinElement, Leaf> elementsMap = new HashMap<IRodinElement, Leaf>();

    private class StatusObject {
    	Object object;
    	Object moveFrom;
    	boolean expanded;
		boolean selected;
    	
    	StatusObject(Object object, Object moveFrom, boolean expanded, boolean selected) {
    		this.object = object;
    		this.moveFrom = moveFrom;
    		this.expanded = expanded;
    		this.selected = selected;
    	}

    	Object getObject() {return object;}
    	Object getMoveFrom() {return moveFrom;}
    	boolean getExpandedStatus() {return expanded;}
    	boolean getSelectedStatus() {return selected;}
    }
    
    /* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		toRefresh = new HashSet<Object>();
		newStatus = new HashSet<StatusObject>();
		processDelta(event.getDelta());
		postRefresh(toRefresh, true);
	}
		
	private void processMove(TreeItem item, IRodinElement newElement) {
		Leaf leaf = (Leaf) item.getData();
		IRodinElement oldElement = leaf.getElement();
		UIUtils.debug("--- Process Move ---");
		try {
			UIUtils.debug("from: " + oldElement.getElementName() + " content: "); 
			UIUtils.debug("to: " + newElement.getElementName() + " content: " + ((IInternalElement) newElement).getContents());
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
		IStructuredSelection ssel = (IStructuredSelection) this.getSelection();
		boolean selected = ssel.toList().contains(leaf);

		newStatus.add(new StatusObject(newElement, oldElement, this.getExpandedState(leaf), selected));

		TreeItem [] items = item.getItems();
		
		for (TreeItem i : items) {
			UIUtils.debug("Tree Items: " + i);
			Leaf l = (Leaf) i.getData();
			if (l == null) continue;
			IRodinElement element = l.getElement();
			IRodinElement newChild = ((IInternalElement) newElement).getInternalElement(element.getElementType(), element.getElementName(), ((IInternalElement) element).getOccurrenceCount());
			processMove(i, newChild);
		}
	}
	
	private TreeItem findItem(IRodinElement element) {
//		UIUtils.debug("Trying to find " + element.getElementName());
		Tree tree = this.getTree();
		TreeItem [] items = tree.getItems();
		for (TreeItem item : items) {
			TreeItem temp = findItem(item, element);
			if (temp != null) return temp;
		}
		return null;
	}
	
	private TreeItem findItem(TreeItem item, IRodinElement element) {
//		UIUtils.debug("From " + item);
		Leaf leaf = (Leaf) item.getData();
		if (leaf == null) return null;
		if (leaf.getElement().equals(element)) {
//			UIUtils.debug("Found");
			return item;
		}
		else {
//			UIUtils.debug("Recursively ...");
			TreeItem [] items = item.getItems();
			for (TreeItem i : items) {
				TreeItem temp = findItem(i, element);
				if (temp != null) return temp;
			}
		}
//		UIUtils.debug("... Not found");
		return null;
	}
	
	private void processDelta(IRodinElementDelta delta) {
		int kind= delta.getKind();
		IRodinElement element= delta.getElement();
		if (kind == IRodinElementDelta.ADDED) {
			// Handle move operation
			if ((delta.getFlags() & IRodinElementDelta.F_MOVED_FROM) != 0) {
				UIUtils.debug("Moved: " + element.getElementName() + " from: " + delta.getMovedFromElement());
				IRodinElement oldElement = delta.getMovedFromElement();
				// Recursively process the children
				TreeItem item = findItem(oldElement);
				UIUtils.debug("Item found: " + item);
				processMove(item, element);				
			}
			else {
				UIUtils.debug("Added: " + element.getElementName());
				Object parent = element.getParent();
				toRefresh.add(parent);
			}
			return;
		}
		
		if (kind == IRodinElementDelta.REMOVED) {
			// Ignore the move operation
			if ((delta.getFlags() & IRodinElementDelta.F_MOVED_TO) == 0) {
				UIUtils.debug("Removed: " + element.getElementName());			
				Object parent = element.getParent();
				toRefresh.add(parent);
			}
			return;
		}
		
		if (kind == IRodinElementDelta.CHANGED) {
			int flags = delta.getFlags();
			UIUtils.debug("Changed: " + element.getElementName());
			
			if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				UIUtils.debug("CHILDREN");
				IRodinElementDelta [] deltas = delta.getAffectedChildren();
				for (int i = 0; i < deltas.length; i++) {
					processDelta(deltas[i]);
				}
				return;
			}
			
			if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
				UIUtils.debug("REORDERED");
				toRefresh.add(element.getParent());
				return;
			}
			
			if ((flags & IRodinElementDelta.F_CONTENT) != 0) {
				UIUtils.debug("CONTENT");

				if (!(element instanceof IRodinFile)) toRefresh.add(element);
				return;
			}
		}

	}
	
	/**
	 * Refresh the nodes.
	 * <p>
	 * @param toRefresh List of node to refresh
	 * @param updateLabels <code>true</code> if the label need to be updated as well
	 */
	private void postRefresh(final Collection toRefresh, final boolean updateLabels) {
		final TreeViewer viewer = this;
		UIUtils.syncPostRunnable(new Runnable() {
			public void run() {
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					ISelection sel = viewer.getSelection();
					Object [] objects = viewer.getExpandedElements();
					for (Iterator iter = toRefresh.iterator(); iter.hasNext();) {
						IRodinElement element = (IRodinElement) iter.next();
						UIUtils.debug("Refresh element " + element.getElementName());
						Leaf leaf = elementsMap.get(element);
//						boolean expanded = viewer.getExpandedState(leaf);
						viewer.refresh(leaf, updateLabels);
//						viewer.setExpandedState(leaf, expanded);
					}
					viewer.setExpandedElements(objects);
					viewer.setSelection(sel);

					for (Iterator iter = newStatus.iterator(); iter.hasNext();) {
						StatusObject state = (StatusObject) iter.next();
						UIUtils.debug("Object: " + state.getObject() + " expanded: " + state.getExpandedStatus());
						try {
//							UIUtils.debug("from: " + oldElement.getElementName() + " content: "); 
							UIUtils.debug("Details: " + ((IInternalElement) state.getObject()).getElementName() + " content: " + ((IInternalElement) state.getObject()).getContents());
						}
						catch (RodinDBException e) {
							e.printStackTrace();
						}
						Leaf leaf = elementsMap.get(state.getMoveFrom());
						leaf.setElement((IRodinElement) state.getObject());
						elementsMap.remove(state.getMoveFrom());
						elementsMap.put((IRodinElement) state.getObject(), leaf);
						viewer.setExpandedState(leaf, state.getExpandedStatus());
						viewer.update(leaf, null);
												
						if (state.getSelectedStatus()) {
							IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
							ArrayList<Object> list = new ArrayList<Object>(ssel.size() + 1);
							for (Iterator it = ssel.iterator(); it.hasNext();) {
								list.add(elementsMap.get(it.next()));
							}
							list.add(leaf);
							viewer.setSelection(new StructuredSelection(list));
						}
					}
//					if (lastMouseEvent != null) mouseAdapter.mouseDown(lastMouseEvent);
				}
			}
		}, this.getControl());
	}
	
}
