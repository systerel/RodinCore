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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
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
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of Section Part for displaying and editting Sees clause.
 */
public class SyntheticViewSection
	extends SectionPart
{

	// Title and description of the section.
//	private static final String SECTION_TITLE = "Synthetic View";
//	private static final String SECTION_DESCRIPTION = "The synthetic view of the component";	
	
	// The Form editor contains this section.
    private FormEditor editor;
    private TreeViewer viewer;
    private MouseAdapter mouseAdapter;
    private Tree tree;
    
    private class StatusPair {
    	Object object;
    	boolean status;
    	
    	StatusPair(Object first, boolean second) {
    		this.object = first;
    		this.status = second;
    	}

    	Object getObject() {return object;}
    	boolean getStatus() {return status;}
    }
    
	/**
	 * @author htson
	 * <p>
	 * This is the content provider class for the tree display in 
	 * the outline page.
	 */
	class ViewContentProvider
		implements ITreeContentProvider, IElementChangedListener
	{
		// List of elements need to be refresh (when processing Delta of changes).
		private Collection<Object> toRefresh;
		
		private Collection<StatusPair> newStatus;
		
		/**
		 * This response for the delta changes from the Rodin Database
		 * <p>
		 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
		 */
		public void elementChanged(ElementChangedEvent event) {
			toRefresh = new HashSet<Object>();
			newStatus = new HashSet<StatusPair>();
			processDelta(event.getDelta());
			postRefresh(toRefresh, true);
		}
		
		
		/*
		 * Process the delta recursively and depend on the kind of the delta.
		 * <p> 
		 * @param delta The Delta from the Rodin Database
		 */
		private void processDelta(IRodinElementDelta delta) {
			int kind= delta.getKind();
			IRodinElement element= delta.getElement();
			if (kind == IRodinElementDelta.ADDED) {
				// Handle move operation
				if ((delta.getFlags() & IRodinElementDelta.F_MOVED_FROM) != 0) {
					IRodinElement oldElement = delta.getMovedFromElement();
					newStatus.add(new StatusPair(element, viewer.getExpandedState(oldElement)));
				}
				Object parent = element.getParent();
				toRefresh.add(parent);
				return;
			}
			
			if (kind == IRodinElementDelta.REMOVED) {
				// Ignore the move operation
				
				Object parent = element.getParent();
				toRefresh.add(parent);
				return;
			}
			
			if (kind == IRodinElementDelta.CHANGED) {
				int flags = delta.getFlags();
				
				if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
					IRodinElementDelta [] deltas = delta.getAffectedChildren();
					for (int i = 0; i < deltas.length; i++) {
						processDelta(deltas[i]);
					}
					return;
				}
				
				if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
					toRefresh.add(element.getParent());
					return;
				}
				
				if ((flags & IRodinElementDelta.F_CONTENT) != 0) {
					toRefresh.add(element);
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
			postRunnable(new Runnable() {
				public void run() {
					Control ctrl= viewer.getControl();
					if (ctrl != null && !ctrl.isDisposed()) {
						
						Object [] objects = viewer.getExpandedElements();
						for (Iterator iter = toRefresh.iterator(); iter.hasNext();) {
							IRodinElement element = (IRodinElement) iter.next();
							UIUtils.debug("Refresh element " + element.getElementName());
							viewer.refresh(element, updateLabels);
						}
						viewer.setExpandedElements(objects);
						for (Iterator iter = newStatus.iterator(); iter.hasNext();) {
							StatusPair state = (StatusPair) iter.next();
							UIUtils.debug("Object: " + state.getObject() + " expanded: " + state.getStatus());
							viewer.setExpandedState(state.getObject(), state.getStatus());
						}
					}
				}
			});
		}
		
		private void postRunnable(final Runnable r) {
			Control ctrl= viewer.getControl();
			final Runnable trackedRunnable= new Runnable() {
				public void run() {
					try {
						r.run();
					} finally {
						//removePendingChange();
						//if (UIUtils.DEBUG) System.out.println("Runned");
					}
				}
			};
			if (ctrl != null && !ctrl.isDisposed()) {
				try {
					ctrl.getDisplay().asyncExec(trackedRunnable); 
				} catch (RuntimeException e) {
					throw e;
				} catch (Error e) {
					throw e; 
				}
			}
		}

		// The invisible root of the tree (should be the current editting file).
		private IRodinFile invisibleRoot = null;
		
		
		// When the input is change, reset the invisible root to null.
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (oldInput == null && newInput != null)
				((EventBEditor) editor).addElementChangedListener(this);
			else if (oldInput != null && newInput == null)
				((EventBEditor) editor).removeElementChangedListener(this);
			invisibleRoot = null;
			return;
		}
		
		
		// When the tree is dispose, do nothing.
		public void dispose() {
		}
		
		
		// Getting the list of elements, setting the invisible root if neccesary.
		public Object[] getElements(Object parent) {
//			UIUtils.debug("Get element " + parent.toString());
			if (parent instanceof IRodinFile) {
				if (invisibleRoot == null) 
					invisibleRoot = (IRodinFile) parent;
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}
		
		
		// Getting the parent of the an element.
		public Object getParent(Object child) {
			if (child instanceof IRodinElement) {
				return ((IRodinElement) child).getParent();
			}
			return null;
		}
		
		
		// Getting the list of children.
		public Object [] getChildren(Object parent) {
			if (parent instanceof IParent) {
				try {
					return ((IParent) parent).getChildren();
				}
				catch (RodinDBException e) {
					e.printStackTrace();
					EventBMachineEditorContributor.sampleAction.refreshAll();
					MessageDialog.openError(null, "Error",
							"Cannot get children of " + parent);
				}
			}
			return new Object[0];
		}
		
		
		// Check if the element has children.
		public boolean hasChildren(Object parent) {
			if (parent instanceof IParent)
				try {
					return ((IParent) parent).hasChildren();
				}
				catch (RodinDBException e) {
					MessageDialog.openError(null, "Error",
							"Cannot check hasChildren of " + parent);
					e.printStackTrace();
				}
			return false;
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
//			IRodinElement rodinElement = ((Leaf) element).getElement();
			if (columnIndex != 0) return null;
			return UIUtils.getImage(element);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
//			IRodinElement element = ((Leaf) element).getElement();
			
			if (columnIndex == 0) {
				if (element instanceof IUnnamedInternalElement) return "";
				if (element instanceof IInternalElement) return ((IInternalElement) element).getElementName();
				return element.toString();
			}
			
			if (columnIndex == 1) {
				try {
					if (element instanceof IInternalElement) return ((IInternalElement) element).getContents();
				}
				catch (RodinDBException e) {
					e.printStackTrace();
				}
				return element.toString();
			}
			
//			if (columnIndex == 0) {
//				try {
//					if (element instanceof IUnnamedInternalElement) return ((IUnnamedInternalElement) element).getContents();
//				}
//				catch (RodinDBException e) {
//					e.printStackTrace();
//				}
//				if (element instanceof IInternalElement) return ((IInternalElement) element).getElementName();
//				else return element.toString();
//			}
			return element.toString();

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
             return display.getSystemColor(SWT.COLOR_WHITE);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
		 */
		public Color getForeground(Object element, int columnIndex) {
			Display display = Display.getCurrent();
            return display.getSystemColor(SWT.COLOR_BLACK);
       }

//		public String getText(Object obj) {
//			if (obj instanceof IAction) {
//				try {
//					return ((IAction) obj).getContents();
//				}
//				catch (RodinDBException e) {
//					// TODO Handle Exception
//					e.printStackTrace();
//					return "";
//				}
//			}
//			if (obj instanceof IInternalElement) return ((IInternalElement) obj).getElementName();
//			return obj.toString();
//		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object, int)
		 */
		public Font getFont(Object element, int columnIndex) {
//			UIUtils.debug("Get fonts");
			return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		}
		
		
//		public Image getImage(Object obj) {
//			return UIUtils.getImage(obj);
//		}
	
	
	
	}

	/**
	 * @author htson
	 * <p>
	 * This class sorts the RODIN elements by types. 
	 */
	private class ElementsSorter extends ViewerSorter {
		
		public int compare(Viewer viewer, Object e1, Object e2) {
	        int cat1 = category(e1);
	        int cat2 = category(e2);
	        return cat1 - cat2;
		}
		
		public int category(Object element) {
			if (element instanceof IVariable) return 1;
			if (element instanceof IInvariant) return 2;
			if (element instanceof ITheorem) return 4;
			if (element instanceof IEvent) return 5;
			if (element instanceof ICarrierSet) return 1;
			if (element instanceof IConstant) return 2;
			if (element instanceof IAxiom) return 3;
			if (element instanceof IGuard) return 2;
			if (element instanceof IAction) return 3;
			
			return 0;
		}
	}
//	
//	class MasterLabelProvider 
//		implements  ITableLabelProvider, ITableFontProvider, ITableColorProvider {
//		
//		/* (non-Javadoc)
//		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
//		 */
//		public Image getColumnImage(Object element, int columnIndex) {
//			if (columnIndex != 0) return null;
//			return UIUtils.getImage(element);
//		}
//	
//		/* (non-Javadoc)
//		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
//		 */
//		public String getColumnText(Object element, int columnIndex) {
//			
//			if (columnIndex == 1) {
//				if (element instanceof IUnnamedInternalElement) return "";
//				if (element instanceof IInternalElement) return ((IInternalElement) element).getElementName();
//				return element.toString();
//			}
//			
//			if (columnIndex == 2) {
//				try {
//					if (element instanceof IInternalElement) return ((IInternalElement) element).getContents();
//				}
//				catch (RodinDBException e) {
//					e.printStackTrace();
//				}
//				return element.toString();
//			}
//			
//			if (columnIndex == 0) {
//				try {
//					if (element instanceof IUnnamedInternalElement) return ((IUnnamedInternalElement) element).getContents();
//				}
//				catch (RodinDBException e) {
//					e.printStackTrace();
//				}
//				if (element instanceof IInternalElement) return ((IInternalElement) element).getElementName();
//				else return element.toString();
//			}
//			return element.toString();
//		}
//	
//		/* (non-Javadoc)
//		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
//		 */
//		public void addListener(ILabelProviderListener listener) {
//			// TODO Auto-generated method stub
//			
//		}
//	
//		/* (non-Javadoc)
//		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
//		 */
//		public void dispose() {
//			// TODO Auto-generated method stub
//			
//		}
//	
//		/* (non-Javadoc)
//		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
//		 */
//		public boolean isLabelProperty(Object element, String property) {
//			// TODO Auto-generated method stub
//			return false;
//		}
//	
//		/* (non-Javadoc)
//		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
//		 */
//		public void removeListener(ILabelProviderListener listener) {
//			// TODO Auto-generated method stub
//			
//		}
//	
//		/* (non-Javadoc)
//		 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
//		 */
//		public Color getBackground(Object element, int columnIndex) {
//			 Display display = Display.getCurrent();
//	         return display.getSystemColor(SWT.COLOR_WHITE);
//		}
//		
//		/* (non-Javadoc)
//		 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
//		 */
//		public Color getForeground(Object element, int columnIndex) {
//			Display display = Display.getCurrent();
//	        return display.getSystemColor(SWT.COLOR_BLACK);
//	   }
//	
//		/* (non-Javadoc)
//		 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object, int)
//		 */
//		public Font getFont(Object element, int columnIndex) {
//			return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
//		}
//	
//	}
	
	/**
     * Constructor.
     * <p>
     * @param editor The Form editor contains this section
     * @param page The Dependencies page contains this section
     * @param parent The composite parent
     */
	public SyntheticViewSection(FormEditor editor, FormToolkit toolkit, Composite parent) {
//		super(parent, toolkit, ExpandableComposite.TITLE_BAR);
		super(parent, toolkit, SWT.NONE);
		this.editor = editor;
		createClient(getSection(), toolkit);
	}


	/**
	 * Creat the content of the section.
	 */
	public void createClient(Section section, FormToolkit toolkit) {
//        section.setText(SECTION_TITLE);
//        section.setDescription(SECTION_DESCRIPTION);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 400;
		gd.minimumHeight = 300;
		gd.widthHint = 300;
		section.setLayoutData(gd);
//		scrolledForm = toolkit.createScrolledForm(section);
//		scrolledForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//        GridLayout layout = new GridLayout();
//        layout.numColumns = 1;
//        layout.marginHeight = 2;
//        layout.makeColumnsEqualWidth = true;
//        Composite body = scrolledForm.getBody();
//		body.setLayout(layout);

//		viewer = new TreeViewer(body, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer = new SyntheticEditableTreeViewer(section, SWT.MULTI | SWT.FULL_SELECTION, ((EventBEditor) editor).getRodinInput());
//		viewer = new TreeViewer(section, SWT.MULTI | SWT.FULL_SELECTION);
		
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new SyntheticLabelProvider());
		viewer.setSorter(new ElementsSorter());
		tree = viewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.setHeaderVisible(true);
		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setText("Elements");
		column.setResizable(true);
		column.setWidth(200);
//		TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
//		column2.setText("Name");
//		column2.setResizable(true);
//		column2.setWidth(150);
		TreeColumn column3 = new TreeColumn(tree, SWT.LEFT);
		column3.setText("Contents");
		column3.setResizable(true);
		column3.setWidth(300);
		viewer.setInput(((EventBEditor) editor).getRodinInput());
		viewer.refresh();
		final TreeEditor editor = new TreeEditor(tree);
		mouseAdapter = 
		 
		new MouseAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
			 */
//			public void mouseDoubleClick(MouseEvent e) {
//				// TODO Auto-generated method stub
//				
//			}

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
			 */
			public void mouseDown(MouseEvent e) {
				final Color black = tree.getDisplay().getSystemColor (SWT.COLOR_BLACK);
				Control old = editor.getEditor();
		        if (old != null) old.dispose();

		        // Determine where the mouse was clicked
		        final Point pt = new Point(e.x, e.y);

		        // Determine which row was selected
		        final TreeItem item = tree.getItem(pt);
		        if (item != null) {
		        	// Determine which column was selected
		        	int column = -1;
		        	for (int i = 0, n = tree.getColumnCount(); i < n; i++) {
		        		Rectangle rect = item.getBounds(i);
		        		if (rect.contains(pt)) {
		        			// This is the selected column
		        			column = i;
		        			break;
		        		}
		        	}
//			        UIUtils.debug("Column: " + column);
			        final int col = column;
			        if (col < 1) return; // The object column is not editable
//			        UIUtils.debug("Item: " + item.getData() + " of class: " + item.getData().getClass());
			        final Object itemData = item.getData();
//			        if (itemData instanceof IUnnamedInternalElement && col == 1) return;
			        if (col == 1) {
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
							SyntheticViewSection.this.commit((IRodinElement) itemData, col, text.getText());
						}
						
					};
					final int inset = isCarbon ? 0 : 1;
					composite.addListener (SWT.Resize, new Listener () {
						public void handleEvent (Event e) {
							Rectangle rect = composite.getClientArea ();
							text.setBounds (rect.x + inset, rect.y + inset, rect.width - inset * 2, rect.height - inset * 2);
						}
					});
					Listener textListener = new Listener () {
						public void handleEvent (final Event e) {
							
//							UIUtils.debug("Not disposed");
							switch (e.type) {
								case SWT.FocusOut:
									UIUtils.debug("FocusOut");
									if (item.isDisposed()) return;
									commit((IRodinElement) itemData, col, text.getText());
									item.setText (col, text.getText());
									composite.dispose ();
									break;
								case SWT.Verify:
									String newText = text.getText();
									String leftText = newText.substring (0, e.start);
									String rightText = newText.substring (e.end, newText.length ());
									GC gc = new GC (text);
									Point size = gc.textExtent (leftText + e.text + rightText);
									gc.dispose ();
									size = text.computeSize (size.x, SWT.DEFAULT);
									editor.horizontalAlignment = SWT.LEFT;
									Rectangle itemRect = item.getBounds (), rect = tree.getClientArea ();
									editor.minimumWidth = Math.max (size.x, itemRect.width) + inset * 2;
									int left = itemRect.x, right = rect.x + rect.width;
									editor.minimumWidth = Math.min (editor.minimumWidth, right - left);
									editor.minimumHeight = size.y + inset * 2;
									editor.layout();
									break;
								case SWT.Traverse:
									switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											UIUtils.debug("TraverseReturn");
											if (item.isDisposed()) return;
											item.setText (col, text.getText ());
											commit((IRodinElement) itemData, col, text.getText());
											//FALL THROUGH
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
					editor.setEditor (composite, item, column);
					text.setText (item.getText(column));
					text.selectAll ();
					text.setFocus ();
		        }
		        
			}

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
			 */
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		tree.addMouseListener(mouseAdapter);
//		scrolledForm.reflow(true);
		section.setClient(viewer.getControl());
		
	}	

//	private void commit(Point pt, int col, String text) {
//        // Determine which row was selected
//        TreeItem item = tree.getItem(pt);
//        Object itemData = item.getData();
//		if (itemData instanceof IInternalElement) {
//			switch (col) {
//			case 1:  // Commit name
//				try {
//					if (((IInternalElement) itemData).getElementName().equals(text)) return;
//					UIUtils.debug("Rename " + ((IInternalElement) itemData).getElementName() + " to " + text);
//					((IInternalElement) itemData).rename(text, false, null);
////					markDirty();
//				}
//				catch (RodinDBException e) {
//					e.printStackTrace();
//				}
//				
//				break;
//			case 2:  // Commit content
//				try {
//					((IInternalElement) itemData).setContents(text);
////					markDirty();
//				}
//				catch (RodinDBException e) {
//					e.printStackTrace();
//				}
//				break;
//			}
//		}
//	}

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
}