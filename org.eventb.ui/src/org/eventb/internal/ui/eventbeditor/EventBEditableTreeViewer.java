/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - used ElementDescRegistry
 *     Systerel - introduced read only elements
 *     Systerel - managed text insertion command
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import static org.eventb.internal.ui.EventBUtils.isReadOnly;
import static org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry.Column.LABEL;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.ui.swt.IFocusService;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.itemdescription.IAttributeDesc;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 *         <p>
 *         An abstract class contains of an Editable (Table) Tree Viewer.
 */
public abstract class EventBEditableTreeViewer extends TreeViewer implements
		ISelectionChangedListener {
	TreeEditor treeEditor;

	protected IEventBEditor<?> editor;

	// Number of columns for this Tree Viewer. Should be consistance with the
	// abstract method createTreeColumns().
	protected int numColumn;

	// List of elements need to be refresh (when processing Delta of changes).
	private Collection<IRodinElement> toRefresh;

	CommentToolTip handler;

	private KeyListener keyListener;

	private static final int DEFAULT_COLUMN = 0;

	// a unique ID for all text controls provided as variable to core
	// expressions for the various services; must NOT be used for comparison
	private static final String TEXT_CONTROL_ID = EventBUIPlugin.PLUGIN_ID
			+ ".controls.text";

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
	protected final boolean isSelectable(Object object, int column) {
		final ElementDesc desc = getElementDesc(object);
		if(desc == null)
			return false;
		return desc.isSelectable(column);
	}

	/**
	 * If the given object is an internal element, the method return an element
	 * description. Else the method return <code>null</code>
	 */
	private ElementDesc getElementDesc(Object o) {
		if (!(o instanceof IRodinElement))
			return null;
		final IRodinElement element = (IRodinElement) o;
		return ElementDescRegistry.getInstance().getElementDesc(element);
	}
	
	private IAttributeDesc getAttributeDesc(IRodinElement element, int col) {
		final ElementDesc desc = getElementDesc(element);
		if (desc == null)
			return null;
		final IAttributeDesc attrDesc = desc.atColumn(col);
		return attrDesc;
	}
	
	private void trace(String msg) {
		if (EventBEditorUtils.DEBUG) {
			System.out.println(msg);
		}
	}
	
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
	protected final void commit(IRodinElement element, int col, String text,
			IProgressMonitor monitor) {
		final IAttributeDesc attrDesc = getAttributeDesc(element, col);
		if (!(element instanceof IInternalElement) || attrDesc == null){
			trace("Error when committing change on " + element);
			return;
		}
		UIUtils.setStringAttribute((IInternalElement) element, attrDesc
				.getManipulation(), text, monitor);
	}

	/**
	 * Select and edit the element in this Viewer.
	 * <p>
	 * 
	 * @param element
	 *            a Rodin element
	 */
	protected final void edit(IRodinElement element) {
		this.reveal(element);
		final ElementDesc desc = getElementDesc(element);
		if (desc == null)
			return;
 		final int column = desc.getDefaultColumn();
		final TreeItem item = TreeSupports.findItem(this.getTree(), element);
		selectItem(item, column);
	}

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
	public EventBEditableTreeViewer(IEventBEditor<?> editor, Composite parent,
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

			@Override
			public void mouseDown(MouseEvent e) {
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

			@Override
			public void keyPressed(KeyEvent e) {
				// Do nothing
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.F2) {
					handler.openEditing();
				}
			}

		};

		this.getControl().addKeyListener(keyListener);

		this.addSelectionChangedListener(this);
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
		disposeOpenedEditor();
		final Object itemData = item.getData();

		/* Check if the cell is editable or not */
		if (!isSelectable(itemData, column))
			return;

		final Color black = EventBSharedColor.getSystemColor(SWT.COLOR_BLACK);
		boolean isCarbon = SWT.getPlatform().equals("carbon");
		final Composite composite = new Composite(tree, SWT.NONE);
		if (!isCarbon)
			composite.setBackground(black);
		final Text text = new Text(composite, SWT.NONE);
		text.addKeyListener(keyListener);

		new ElementText(new EventBMath(text), treeEditor1, tree, item,
				(IRodinElement) itemData, column, 1000) {

			@Override
			public void commit(IRodinElement elm, int col, String contents) {
				EventBEditableTreeViewer.this.commit(elm, col, contents,
						new NullProgressMonitor());
			}

			@Override
			public void nextEditableCell() {
				selectNextEditableCell(item, column);
			}

			private void selectNextEditableCell(TreeItem item1, int col) {
				Rectangle rec = item1.getBounds();

				if (col == numColumn - 1) {
					TreeItem next = tree
							.getItem(new Point(rec.x + rec.width / 2, rec.y
									+ rec.height / 2 + tree.getItemHeight()));
					if (next != null) {
						// UIUtils.debug("Found item: " + next);
						if (isSelectable(next.getData(), LABEL.getId()))
							selectItem(next, LABEL.getId());
						else
							selectNextEditableCell(next, 0);

					} else
						return;
				} else {
					if (isSelectable(item1.getData(), col + 1))
						selectItem(item1, col + 1);
					else
						selectNextEditableCell(item1, col + 1);
				}

			}

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
						if (isSelectable(next.getData(), numColumn - 1))
							selectItem(next, numColumn - 1);
						else
							selectPrevEditableCell(next, numColumn - 1);
					} else
						return;
				} else {
					if (isSelectable(item1.getData(), col - 1))
						selectItem(item1, col - 1);
					else
						selectPrevEditableCell(item1, col - 1);
				}
			}

		};

		final int inset = isCarbon ? 0 : 1;
		composite.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event e) {
				Rectangle rect = composite.getClientArea();
				text.setBounds(rect.x + inset, rect.y + inset, rect.width
						- inset * 2, rect.height - inset * 2);
			}
		});
		if ((itemData instanceof IInternalElement)
				&& isReadOnly((IInternalElement) itemData)) {
			text.setEditable(false);
		} else {
			final IFocusService service = (IFocusService) editor.getSite()
					.getService(IFocusService.class);
			service.addFocusTracker(text, TEXT_CONTROL_ID);
		}
		
		treeEditor1.setEditor(composite, item, column);
		text.setText(item.getText(column));
		text.selectAll();
		text.setFocus();
	}

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

	private void processDelta(IRodinElementDelta delta) {
		int kind = delta.getKind();
		IRodinElement element = delta.getElement();
		if (kind == IRodinElementDelta.ADDED) {
			toRefresh.add(element.getParent());
			return;
		}

		if (kind == IRodinElementDelta.REMOVED) {
			toRefresh.add(element.getParent());
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
			@Override
			public void run() {
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					ctrl.setRedraw(false);
					for (IRodinElement element : toRefresh1) {
						viewer.refresh(element);
					}
					ctrl.setRedraw(true);
				}
			}

		}, viewer.getControl());
	}

	public void statusChanged(final IRodinElement element) {
		final TreeViewer viewer = this;
		UIUtils.syncPostRunnable(new Runnable() {
			@Override
			public void run() {
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					viewer.refresh(element);
				}
			}
		}, this.getControl());
	}

	protected String getColumnID(int columnIndex) {
		// TODO Should be implemented dynamically
		if (columnIndex == 1)
			return "content";
		else if (columnIndex == 0)
			return "name";
		else
			return "";
	}

	protected int getColumnNumber(String defaultColumn) {
		if (defaultColumn == null)
			return DEFAULT_COLUMN;
		else if (defaultColumn.equals("name"))
			return 0;
		else if (defaultColumn.equals("content"))
			return 1;
		return DEFAULT_COLUMN;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		((EventBEditor<?>) editor).pageSelectionChanged(event);
		if (event.getSelection().isEmpty()) {
			disposeOpenedEditor();
		}
	}

	// Disposes the active editor if any
	void disposeOpenedEditor() {
		final Control old = treeEditor.getEditor();
		if (old != null && !old.isDisposed()) {
			old.dispose();
		}
	}

}
