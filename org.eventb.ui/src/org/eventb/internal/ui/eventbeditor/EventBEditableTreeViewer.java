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

	protected EventBEditor editor;
	
	protected int numColumn;
	
	/* Abstract methods */
	protected abstract void createTreeColumns();
	protected abstract boolean isNotSelectable(Object object, int column);
	protected abstract void commit(IRodinElement element, int col, String text);
	
	private Collection<IElementMovedListener> elementMovedListeners;
	
	public void addElementMovedListener(IElementMovedListener listener) {
		elementMovedListeners.add(listener);
	}
	
	public void removeElementMovedListener(IElementMovedListener listener) {
		elementMovedListeners.remove(listener);
	}
	
	public void notifyElementMovedListener(HashMap<IRodinElement, IRodinElement> moved) {
		for (IElementMovedListener listener : elementMovedListeners) {
			listener.elementMoved(moved);
		}
	}
	
//	private class ObjectComparer implements IElementComparer {
//		public boolean equals(Object a, Object b) {
//			return a == b;
//		}
//
//		public int hashCode(Object element) {
//			return 0;
//		}
//	}
//	
//	private class LeafComparer implements IElementComparer {
//		
//		public boolean equals(Object a, Object b) {
//			if (a instanceof Leaf && b instanceof Leaf) {
//				return ((Leaf) a).getElement() == ((Leaf) b).getElement();
//			}
//			return a == b;
//		}
//
//		public int hashCode(Object element) {
//			return 0;
//		}
//	}

	/**
	 * Constructor.
	 * <p>
	 * @param managedForm The form used to create the part
	 * @param parent The composite parent
	 * @param toolkit The Form Toolkit used to create the part
	 * @param style The style used to creat the part
	 * @param block The master-detail block contains this part
	 */
	public EventBEditableTreeViewer(EventBEditor editor, Composite parent, int style) {
		super(parent, style);
		this.editor = editor;
		elementMovedListeners = new HashSet<IElementMovedListener>();

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		Tree tree = this.getTree();
		tree.setLayoutData(gd);
		
		createTreeColumns();
//		setComparer(new LeafComparer());
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

		new ElementText(this, text, treeEditor, item, tree, (IRodinElement) itemData, column) {
			/* (non-Javadoc)
			 * @see org.eventb.internal.ui.eventbeditor.ElementText#commit(org.rodinp.core.IRodinElement, int, java.lang.String)
			 */
			@Override
			public void commit(IRodinElement element, int column, String contents) {
				EventBEditableTreeViewer.this.commit(element, column, contents);
			}
			
			public void nextEditableCell() {
				selectNextEditableCell(item, column);
			}
			
			private void selectNextEditableCell(TreeItem item, int column) {
				Rectangle rec = item.getBounds();
//				UIUtils.debug("Bound: " + rec);
				
				if (column == numColumn - 1) {
					TreeItem next = tree.getItem(new Point(rec.x + rec.width/2, rec.y + rec.height/2 + tree.getItemHeight()));
					if (next != null) {
//						UIUtils.debug("Found item: " + next);
						if (isNotSelectable(next.getData(), 0)) selectNextEditableCell(next, 0);
						else selectItem(next, 0);
					}
					else return;
				}
				else {
					if (isNotSelectable(item.getData(), column + 1)) selectNextEditableCell(item, column + 1); 
					else selectItem(item, column + 1);
				}
				
			}
			public void prevEditableCell() {
				selectPrevEditableCell(item, column);
			}
			
			private void selectPrevEditableCell(TreeItem item, int column) {
				Rectangle rec = item.getBounds();
//				UIUtils.debug("Bound: " + rec);
				
				if (column == 0) {
					TreeItem next = tree.getItem(new Point(rec.x + rec.width/2, rec.y + rec.height/2 - tree.getItemHeight()));
					if (next != null) {
//						UIUtils.debug("Found item: " + next);
						if (isNotSelectable(next.getData(), numColumn - 1)) selectPrevEditableCell(next, numColumn - 1);
						else selectItem(next, numColumn - 1);
					}
					else return;
				}
				else {
					if (isNotSelectable(item.getData(), column - 1)) selectPrevEditableCell(item, column - 1); 
					else selectItem(item, column - 1);
				}
				
			}

		};
		new EventBMath(text);
//		new TimerText(this, text, (IRodinElement) itemData) {
//
//			/* (non-Javadoc)
//			 * @see org.eventb.internal.ui.eventbeditor.TimerText#commit()
//			 */
//			@Override
//			public void commit() {
//				EventBEditableTreeViewer.this.commit(element, column, text.getText());
//			}
//			
//		};
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
//	protected HashMap<IRodinElement, Leaf> elementsMap = new HashMap<IRodinElement, Leaf>();

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
		moved = new HashMap<IRodinElement, IRodinElement>();
//		UIUtils.debug("Table: " + this);
//		if (this instanceof EventEditableTreeViewer) 
//			UIUtils.debug("Delta: " + event.getDelta());
		processDelta(event.getDelta());
		notifyElementMovedListener(moved);
		postRefresh(toRefresh, true);
	}
		
	private HashMap<IRodinElement, IRodinElement> moved;
	
	private void processMoveRecursive(IRodinElement oldElement, IRodinElement newElement) {
		try {
			UIUtils.debug("from: " + oldElement.getElementName() + " expanded " + this.getExpandedState(oldElement)); 
			UIUtils.debug("to: " + newElement.getElementName() + " content: " + ((IInternalElement) newElement).getContents());
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
		IStructuredSelection ssel = (IStructuredSelection) this.getSelection();
		boolean selected = ssel.toList().contains(oldElement);
		
//		newStatus.add(new StatusObject(newElement, oldElement, this.getExpandedState(leaf), selected));
		moved.put(oldElement, newElement);
		newStatus.add(new StatusObject(newElement, oldElement, this.getExpandedState(oldElement), selected));
		
		if (newElement instanceof IInternalElement) {
			IRodinElement[] elements;
			try {
				elements = ((IInternalElement) newElement).getChildren();
				for (IRodinElement element : elements) {
//					UIUtils.debug("Leaf: " + l);
//					if (l == null) continue;
//					IRodinElement element = l.getElement();
					IRodinElement oldChild = ((IInternalElement) oldElement).getInternalElement(element.getElementType(), element.getElementName(), ((IInternalElement) element).getOccurrenceCount());
					processMoveRecursive(oldChild, element);
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	
//	private void processMove(IRodinElementDelta delta) {
//		int kind= delta.getKind();
//		IRodinElement element= delta.getElement();
//		if (kind == IRodinElementDelta.ADDED) {
//			// Handle move operation
//			if ((delta.getFlags() & IRodinElementDelta.F_MOVED_FROM) != 0) {
//				UIUtils.debug("Moved: " + element.getElementName() + " from: " + delta.getMovedFromElement());
//				IRodinElement oldElement = delta.getMovedFromElement();
//				// Recursively process the children
//				UIUtils.debug("--- Process Move ---");
//				processMoveRecursive(oldElement, element);				
//			}
//			else {
//				UIUtils.debug("Added: " + element.getElementName());
//				Object parent = element.getParent();
//			}
//			return;
//		}
//		
//		if (kind == IRodinElementDelta.REMOVED) {
//			// Ignore the move operation
//			if ((delta.getFlags() & IRodinElementDelta.F_MOVED_TO) == 0) {
//				UIUtils.debug("Removed: " + element.getElementName());			
//				Object parent = element.getParent();
//			}
//			return;
//		}
//		
//		if (kind == IRodinElementDelta.CHANGED) {
//			int flags = delta.getFlags();
//			UIUtils.debug("Changed: " + element.getElementName());
//			
//			if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
//				UIUtils.debug("CHILDREN");
//				IRodinElementDelta [] deltas = delta.getAffectedChildren();
//				for (int i = 0; i < deltas.length; i++) {
//					processMove(deltas[i]);
//				}
//				return;
//			}
//			
//			if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
//				UIUtils.debug("REORDERED");
////				toRefresh.add(element.getParent());
//				return;
//			}
//			
//			if ((flags & IRodinElementDelta.F_CONTENT) != 0) {
//				UIUtils.debug("CONTENT");
//
////				if (!(element instanceof IRodinFile)) toRefresh.add(element);
//				return;
//			}
//		}
//
//	}

//	private void processMove(Leaf leaf, IRodinElement newElement) {
//		if (leaf == null) return; // The element tree table has not been create yet
//		IRodinElement oldElement = leaf.getElement();
////		try {
////			UIUtils.debug("from: " + oldElement.getElementName() + " content: "); 
////			UIUtils.debug("to: " + newElement.getElementName() + " content: " + ((IInternalElement) newElement).getContents());
////		}
////		catch (RodinDBException e) {
////			e.printStackTrace();
////		}
//		IStructuredSelection ssel = (IStructuredSelection) this.getSelection();
//		boolean selected = ssel.toList().contains(leaf);
//
//		newStatus.add(new StatusObject(newElement, oldElement, this.getExpandedState(leaf), selected));
//
//		if (leaf instanceof Node) {
//			Leaf [] leaves = ((Node) leaf).getChildren();
//			
//			for (Leaf l : leaves) {
////				UIUtils.debug("Leaf: " + l);
//				if (l == null) continue;
//				IRodinElement element = l.getElement();
//				IRodinElement newChild = ((IInternalElement) newElement).getInternalElement(element.getElementType(), element.getElementName(), ((IInternalElement) element).getOccurrenceCount());
//				processMove(l, newChild);
//			}
//		}
//	}
		
	private void processDelta(IRodinElementDelta delta) {
		int kind= delta.getKind();
		IRodinElement element= delta.getElement();
		if (kind == IRodinElementDelta.ADDED) {
			// Handle move operation
			if ((delta.getFlags() & IRodinElementDelta.F_MOVED_FROM) != 0) {
				UIUtils.debug("Moved: " + element.getElementName() + " from: " + delta.getMovedFromElement());
				IRodinElement oldElement = delta.getMovedFromElement();
//				Leaf leaf = elementsMap.get(oldElement); 
//				TreeItem item = TreeSupports.findItem(this.getTree(), oldElement);
//				UIUtils.debug("Item found: " + item);
				UIUtils.debug("--- Process Move ---");
				processMoveRecursive(oldElement, element);
			}
//			else {
//				UIUtils.debug("Added: " + element.getElementName());
//				
//			}
			Object parent = element.getParent();
			toRefresh.add(parent);
			return;
		}
		
		if (kind == IRodinElementDelta.REMOVED) {
			// Ignore the move operation
//			if ((delta.getFlags() & IRodinElementDelta.F_MOVED_TO) == 0) {
				UIUtils.debug("Removed: " + element.getElementName());			
				Object parent = element.getParent();
				toRefresh.add(parent);
//			}
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
//					Object [] objects = viewer.getExpandedElements();
					for (Iterator iter = toRefresh.iterator(); iter.hasNext();) {
						IRodinElement element = (IRodinElement) iter.next();
						UIUtils.debug("Refresh element " + element.getElementName());
//						Leaf leaf = elementsMap.get(element);
						viewer.refresh(element, updateLabels);
					}

//					ISelection sel = viewer.getSelection();
//					viewer.setExpandedElements(objects);
					
//					EventBEditableTreeViewer.this.setComparer(new ObjectComparer());
					for (Iterator iter = newStatus.iterator(); iter.hasNext();) {
						StatusObject state = (StatusObject) iter.next();
						Object newElement = state.getObject();
						UIUtils.debug("Object: " + newElement + " expanded: " + state.getExpandedStatus());
						try {
//							UIUtils.debug("from: " + oldElement.getElementName() + " content: "); 
							UIUtils.debug("Details: " + ((IInternalElement) newElement).getElementName() + " content: " + ((IInternalElement) newElement).getContents());
						}
						catch (RodinDBException e) {
							e.printStackTrace();
						}
						Object oldElement = state.getMoveFrom();
//						Leaf leaf = elementsMap.get(oldElement);
//						leaf.setElement((IRodinElement) newElement);
//						elementsMap.remove(oldElement);
//						elementsMap.put((IRodinElement) newElement, leaf);
						viewer.setExpandedState(newElement, state.getExpandedStatus());
						viewer.update(newElement, null);
												
						if (state.getSelectedStatus()) {
							IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
							ArrayList<Object> list = new ArrayList<Object>(ssel.size() + 1);
							for (Iterator it = ssel.iterator(); it.hasNext();) {
								list.add(it.next());
							}
							list.add(newElement);
							viewer.setSelection(new StructuredSelection(list));
						}
						
						if (EventBEditableTreeViewer.this.editor.isNewElement((IRodinElement) oldElement)) {
							EventBEditableTreeViewer.this.editor.addNewElement((IRodinElement) newElement);
						}
						
					}
//					EventBEditableTreeViewer.this.setComparer(new LeafComparer());
					
//					viewer.setSelection(sel);

				}
			}
		}, this.getControl());
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.IStatusChangedListener#statusChanged(java.util.Collection)
	 */
	public void statusChanged(final IRodinElement element) {
		UIUtils.debug("Change status " + element);
		final TreeViewer viewer = this;
		UIUtils.syncPostRunnable(new Runnable() {
			public void run() {
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
//					Leaf leaf = elementsMap.get(element);
					viewer.refresh(element);
				}
			}
		}, this.getControl());
	}
	
	protected abstract void edit(IRodinElement element);
}
