/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 *         <p>
 *         An abstract class contains of an Editable (Table) Tree Viewer.
 */
public abstract class EventBEditableTreeViewer extends TreeViewer {
	TreeEditor treeEditor;

	protected IEventBEditor editor;

	// Number of columns for this Tree Viewer. Should be consistance with the
	// abstract method createTreeColumns().
	protected int numColumn;

	// Listener to the changes for element moving.
	// private Collection<IElementMovedListener> elementMovedListeners;

	// List of elements need to be refresh (when processing Delta of changes).
	private Collection<IRodinElement> toRefresh;

	CommentToolTip handler;

	private KeyListener keyListener;

	// private Collection<StatusObject> newStatus;

	/**
	 * @author htson
	 *         <p>
	 *         This class is used to keep the status of the new object (when
	 *         process element moving)
	 */
	// private class StatusObject {
	// Object object;
	//
	// Object moveFrom;
	//
	// boolean expanded;
	//
	// boolean selected;
	//
	// StatusObject(Object object, Object moveFrom, boolean expanded,
	// boolean selected) {
	// this.object = object;
	// this.moveFrom = moveFrom;
	// this.expanded = expanded;
	// this.selected = selected;
	// }
	//
	// Object getObject() {
	// return object;
	// }
	//
	// Object getMoveFrom() {
	// return moveFrom;
	// }
	//
	// boolean getExpandedStatus() {
	// return expanded;
	// }
	//
	// boolean getSelectedStatus() {
	// return selected;
	// }
	// }
	/**
	 * Create the tree column for this tree viewer.
	 */
	protected abstract void createTreeColumns();

	/**
	 * Specify if the object is editable at the column
	 * <p>
	 * 
	 * @param object
	 *            The current object
	 * @param column
	 *            The current column
	 * @return <code>true</code> if the object is editable at the current
	 *         column
	 */
	protected abstract boolean isNotSelectable(Object object, int column);

	/**
	 * Commit the change for an element at the column with the new information
	 * "text".
	 * <p>
	 * 
	 * @param element
	 *            The Rodin Element
	 * @param col
	 *            The current column
	 * @param text
	 *            The new information
	 */
	protected abstract void commit(IRodinElement element, int col, String text);

	/**
	 * Select and edit the element in this Viewer.
	 * <p>
	 * 
	 * @param element
	 *            a Rodin element
	 */
	protected abstract void edit(IRodinElement element);

	/**
	 * Add new listener to element moving.
	 * <p>
	 * 
	 * @param listener
	 *            an element moved listener
	 */
	// public void addElementMovedListener(IElementMovedListener listener) {
	// elementMovedListeners.add(listener);
	// }
	/**
	 * Remove listener for element moving.
	 * <p>
	 * 
	 * @param listener
	 *            an element moved listner
	 */
	// public void removeElementMovedListener(IElementMovedListener listener) {
	// elementMovedListeners.remove(listener);
	// }
	/**
	 * Notified the element moved listeners with the map of moving elements.
	 * <p>
	 * 
	 * @param moved
	 *            a set of mapping from old elements to new elements.
	 */
	// public void notifyElementMovedListener(
	// HashMap<IRodinElement, IRodinElement> moved) {
	// for (IElementMovedListener listener : elementMovedListeners) {
	// listener.elementMoved(moved);
	// }
	// }
	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            The Event-B Editor
	 * @param parent
	 *            The composite parent
	 * @param style
	 *            The style used to creat the part
	 */
	public EventBEditableTreeViewer(IEventBEditor editor, Composite parent,
			int style) {
		super(parent, style);
		this.editor = editor;
		// elementMovedListeners = new HashSet<IElementMovedListener>();

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		final Tree tree = this.getTree();
		tree.setLayoutData(gd);

		createTreeColumns();
		treeEditor = new TreeEditor(tree);
		treeEditor.grabHorizontal = true;
		tree.addMouseListener(new MouseAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
			 */
			@Override
			public void mouseDown(MouseEvent e) {
				Control old = treeEditor.getEditor();
				if (old != null)
					old.dispose();

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

		handler = new CommentToolTip(this.getControl().getShell());
		handler.activateHoverHelp(this.getControl());

		keyListener = new KeyListener() {

			public void keyPressed(KeyEvent e) {
				// Do nothing
			}

			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.F2) {
					handler.openEditing();
				}
			}

		};

		this.getControl().addKeyListener(keyListener);

	}

	public void selectItem(TreeItem item, int column) {
		Tree tree = EventBEditableTreeViewer.this.getTree();
		if (EventBEditorUtils.DEBUG)
			EventBEditorUtils.debug("Item " + item);

		// Set the selection of the viewer
		IStructuredSelection ssel = (IStructuredSelection) this.getSelection();
		if (!ssel.toList().contains(item.getData()))
			this.setSelection(new StructuredSelection(item.getData()));

		select(tree, treeEditor, item, column);
	}

	protected void select(final Tree tree, final TreeEditor treeEditor1,
			final TreeItem item, final int column) {
		final Object itemData = item.getData();

		/* Check if the cell is editable or not */
		if (isNotSelectable(itemData, column))
			return;

		final Color black = tree.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		boolean isCarbon = SWT.getPlatform().equals("carbon");
		final Composite composite = new Composite(tree, SWT.NONE);
		if (!isCarbon)
			composite.setBackground(black);
		final Text text = new Text(composite, SWT.NONE);
		text.addKeyListener(keyListener);

		new ElementText(new EventBMath(text), treeEditor1, tree, item,
				(IRodinElement) itemData, column, 1000) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eventb.internal.ui.eventbeditor.ElementText#commit(org.rodinp.core.IRodinElement,
			 *      int, java.lang.String)
			 */
			@Override
			public void commit(IRodinElement element, int col,
					String contents) {
				EventBEditableTreeViewer.this.commit(element, col, contents);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eventb.internal.ui.eventbeditor.ElementText#nextEditableCell()
			 */
			@Override
			public void nextEditableCell() {
				selectNextEditableCell(item, column);
			}

			private void selectNextEditableCell(TreeItem item1, int col) {
				Rectangle rec = item1.getBounds();
				// UIUtils.debug("Bound: " + rec);

				if (col == numColumn - 1) {
					TreeItem next = tree
							.getItem(new Point(rec.x + rec.width / 2, rec.y
									+ rec.height / 2 + tree.getItemHeight()));
					if (next != null) {
						// UIUtils.debug("Found item: " + next);
						if (isNotSelectable(next.getData(), 0))
							selectNextEditableCell(next, 0);
						else
							selectItem(next, 0);
					} else
						return;
				} else {
					if (isNotSelectable(item1.getData(), col + 1))
						selectNextEditableCell(item1, col + 1);
					else
						selectItem(item1, col + 1);
				}

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eventb.internal.ui.eventbeditor.ElementText#prevEditableCell()
			 */
			@Override
			public void prevEditableCell() {
				selectPrevEditableCell(item, column);
			}

			private void selectPrevEditableCell(TreeItem item1, int col) {
				Rectangle rec = item1.getBounds();
				// UIUtils.debug("Bound: " + rec);

				if (col == 0) {
					TreeItem next = tree
							.getItem(new Point(rec.x + rec.width / 2, rec.y
									+ rec.height / 2 - tree.getItemHeight()));
					if (next != null) {
						// UIUtils.debug("Found item: " + next);
						if (isNotSelectable(next.getData(), numColumn - 1))
							selectPrevEditableCell(next, numColumn - 1);
						else
							selectItem(next, numColumn - 1);
					} else
						return;
				} else {
					if (isNotSelectable(item1.getData(), col - 1))
						selectPrevEditableCell(item1, col - 1);
					else
						selectItem(item1, col - 1);
				}

			}

		};

		final int inset = isCarbon ? 0 : 1;
		composite.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				Rectangle rect = composite.getClientArea();
				text.setBounds(rect.x + inset, rect.y + inset, rect.width
						- inset * 2, rect.height - inset * 2);
			}
		});
		treeEditor1.setEditor(composite, item, column);
		text.setText(item.getText(column));
		text.selectAll();
		text.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		toRefresh = new HashSet<IRodinElement>();
		// newStatus = new HashSet<StatusObject>();
		// moved = new HashMap<IRodinElement, IRodinElement>();
		if (EventBEditorUtils.DEBUG) {
			EventBEditorUtils.debug("--- Table: " + this + "---");
			EventBEditorUtils.debug(event.getDelta().toString());
			EventBEditorUtils
					.debug("------------------------------------------");
		}
		// if (this instanceof EventEditableTreeViewer)
		// UIUtils.debug("Delta: " + event.getDelta());
		processDelta(event.getDelta());
		// notifyElementMovedListener(moved);
		postRefresh(toRefresh, true);
	}

	// private HashMap<IRodinElement, IRodinElement> moved;

	// private void processMoveRecursive(IRodinElement oldElement,
	// IRodinElement newElement) {
	// UIUtils.debugEventBEditor("from: " + oldElement.getElementName()
	// + " expanded " + this.getExpandedState(oldElement) + "\n to: "
	// + newElement.getElementName() + "\n Expanded elements "
	// + this.getExpandedElements());
	//
	// IStructuredSelection ssel = (IStructuredSelection) this.getSelection();
	// boolean selected = ssel.toList().contains(oldElement);
	//
	// // moved.put(oldElement, newElement);
	// newStatus.add(new StatusObject(newElement, oldElement, this
	// .getExpandedState(oldElement), selected));
	//
	// if (newElement instanceof IInternalElement) {
	// IRodinElement[] elements;
	// try {
	// elements = ((IInternalElement) newElement).getChildren();
	// for (IRodinElement element : elements) {
	// IRodinElement oldChild = ((IInternalElement) oldElement)
	// .getInternalElement(element.getElementType(),
	// element.getElementName(),
	// ((IInternalElement) element)
	// .getOccurrenceCount());
	// processMoveRecursive(oldChild, element);
	// }
	// } catch (RodinDBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }

	private void processDelta(IRodinElementDelta delta) {
		int kind = delta.getKind();
		IRodinElement element = delta.getElement();
		if (kind == IRodinElementDelta.ADDED) {
			// Handle move operation
			// if ((delta.getFlags() & IRodinElementDelta.F_MOVED_FROM) != 0) {
			// UIUtils.debugEventBEditor("Moved: " + element.getElementName()
			// + " from: " + delta.getMovedFromElement());
			// IRodinElement oldElement = delta.getMovedFromElement();
			// UIUtils.debugEventBEditor("--- Process Move ---");
			// processMoveRecursive(oldElement, element);
			// } else {
			// UIUtils.debugEventBEditor("Added: " + element.getElementName());
			// }
			// IRodinElement parent = element.getParent();
			toRefresh.add(element.getParent());
			return;
		}

		if (kind == IRodinElementDelta.REMOVED) {
			// Ignore the move operation
			// if ((delta.getFlags() & IRodinElementDelta.F_MOVED_TO) == 0) {
			// UIUtils.debugEventBEditor("Removed: " +
			// element.getElementName());
			// IRodinElement parent = element.getParent();
			toRefresh.add(element.getParent());
			// }
			return;
		}

		if (kind == IRodinElementDelta.CHANGED) {
			int flags = delta.getFlags();
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Changed: " + element.getElementName());

			if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("CHILDREN");
				IRodinElementDelta[] deltas = delta.getAffectedChildren();
				for (int i = 0; i < deltas.length; i++) {
					processDelta(deltas[i]);
				}
				return;
			}

			if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("REORDERED");
				toRefresh.add(element.getParent());
				return;
			}

			if ((flags & IRodinElementDelta.F_CONTENT) != 0) {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("CONTENT");

				if (!(element instanceof IRodinFile))
					toRefresh.add(element);
				return;
			}

			if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("ATTRIBUTE");
				toRefresh.add(element);
				return;
			}
		}

	}

	/**
	 * Refresh the nodes.
	 * <p>
	 * 
	 * @param toRefresh1
	 *            List of node to refresh
	 * @param updateLabels
	 *            <code>true</code> if the label need to be updated as well
	 */
	private void postRefresh(final Collection<IRodinElement> toRefresh1,
			final boolean updateLabels) {
		final TreeViewer viewer = this;
		UIUtils.syncPostRunnable(new Runnable() {
			public void run() {
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					ctrl.setRedraw(false);
					for (IRodinElement element : toRefresh1) {
						viewer.refresh(element);
					}
					// refreshViewer(toRefresh);
					ctrl.setRedraw(true);
					// for (Iterator iter = toRefresh.iterator();
					// iter.hasNext();) {
					// IRodinElement element = (IRodinElement) iter.next();
					// UIUtils.debugEventBEditor("Refresh element " + element);
					//						
					// viewer.refresh(element, updateLabels);
					// }

					// // Processing the Moved elements
					// for (Iterator iter = newStatus.iterator();
					// iter.hasNext();) {
					// StatusObject state = (StatusObject) iter.next();
					// Object newElement = state.getObject();
					// UIUtils.debugEventBEditor("Object: " + newElement
					// + " expanded: " + state.getExpandedStatus());
					// viewer.setExpandedState(newElement, state
					// .getExpandedStatus());
					// viewer.update(newElement, null);
					//
					// if (state.getSelectedStatus()) {
					// IStructuredSelection ssel = (IStructuredSelection) viewer
					// .getSelection();
					// ArrayList<Object> list = new ArrayList<Object>(ssel
					// .size() + 1);
					// for (Iterator it = ssel.iterator(); it.hasNext();) {
					// list.add(it.next());
					// }
					// list.add(newElement);
					// viewer.setSelection(new StructuredSelection(list));
					// }

					// Object oldElement = state.getMoveFrom();
					// if (EventBEditableTreeViewer.this.editor
					// .isNewElement((IRodinElement) oldElement)) {
					// EventBEditableTreeViewer.this.editor
					// .addNewElement((IRodinElement) newElement);
					// }

					// }

				}
			}

		}, viewer.getControl());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IStatusChangedListener#statusChanged(java.util.Collection)
	 */
	public void statusChanged(final IRodinElement element) {
		final TreeViewer viewer = this;
		UIUtils.syncPostRunnable(new Runnable() {
			public void run() {
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					viewer.refresh(element);
				}
			}
		}, this.getControl());
	}

	public String getColumnID(int columnIndex) {
		// TODO Should be implemented dynamically
		if (columnIndex == 1) return "content";
		else return "name";
	}

}
