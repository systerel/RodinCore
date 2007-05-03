package org.eventb.internal.ui.preferences;

import java.util.ArrayList;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Widget;

public abstract class TwoListSelectionEditor extends FieldEditor {

    /**
     * The left table viewer; <code>null</code> if none
     * (before creation or after disposal).
     */
    List left;

    /**
     * The right list widget; <code>null</code> if none
     * (before creation or after disposal).
     */
    List right;
	
    /**
     * The button box containing the Add, Remove, Up, and Down buttons;
     * <code>null</code> if none (before creation or after disposal).
     */
    Composite buttonBox;

    /**
     * The Add button.
     */
    Button addButton;

    /**
     * The Remove button.
     */
    Button removeButton;

    /**
     * The Up button.
     */
    Button upButton;

    /**
     * The Down button.
     */
    Button downButton;

    /**
     * The selection listener.
     */
    private SelectionListener selectionListener;

    private ArrayList<Object> leftElements;
    
    private ArrayList<Object> rightElements;
       
    private int minWidth = 200;

    /**
     * Creates a new field editor 
     */
    protected TwoListSelectionEditor() {
    	// Do nothing
    }

    /**
     * Creates a list field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
	protected TwoListSelectionEditor(String name, String labelText,
			Composite parent) {
		leftElements = new ArrayList<Object>();
		rightElements = new ArrayList<Object>();
        init(name, labelText);
        createControl(parent);
    }

    /**
     * Notifies that the Add button has been pressed.
     */
    void addPressed() {
        setPresentsDefaultValue(false);
        move(right, left, rightElements, leftElements);
    }

    /**
     * Notifies that the Remove button has been pressed.
     */
    void removePressed() {
        setPresentsDefaultValue(false);
        move(left, right, leftElements, rightElements);
    }
    
    private void move(List from, List to,
			ArrayList<Object> fromElements, ArrayList<Object> toElements) {
    	int[] indices = from.getSelectionIndices();
        if (indices.length != 0) {
            ArrayList<Object> elements = new ArrayList<Object>();
        	
        	for (int index : indices) {
        		elements.add(fromElements.get(index));
        	}

        	// Remove all selected objects in "from"
        	fromElements.removeAll(elements);
        	from.remove(indices);

        	// Add to the objects to "to" at the correct index
        	int index = to.getSelectionIndex();
        	
        	if (index < 0) index = to.getItemCount();
        	
        	toElements.addAll(index, elements);

            for (Object object : elements) {
            	to.add(getLabel(object), index++);
            }
            selectionChanged();
            left.getParent().layout(true);
        }    	
    }
    
    protected abstract String getLabel(Object object);

    /**
     * Creates the Add, Remove, Up, and Down button in the given button box.
     *
     * @param box the box for the buttons
     */
    private void createButtons(Composite box) {
        addButton = createPushButton(box, "<<");//$NON-NLS-1$
        removeButton = createPushButton(box, ">>");//$NON-NLS-1$
        upButton = createPushButton(box, "ListEditor.up");//$NON-NLS-1$
        downButton = createPushButton(box, "ListEditor.down");//$NON-NLS-1$
    }
    
    /**
     * Helper method to create a push button.
     * 
     * @param parent the parent control
     * @param key the resource name used to supply the button's label text
     * @return Button
     */
    private Button createPushButton(Composite parent, String key) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(JFaceResources.getString(key));
        button.setFont(parent.getFont());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
//        int widthHint = convertHorizontalDLUsToPixels(button,
//                IDialogConstants.BUTTON_WIDTH);
//        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
//                SWT.DEFAULT, true).x);
        button.setLayoutData(data);
        button.addSelectionListener(getSelectionListener());
        return button;
    }
    
    /**
     * Returns this field editor's selection listener.
     * The listener is created if nessessary.
     *
     * @return the selection listener
     */
    private SelectionListener getSelectionListener() {
        if (selectionListener == null) {
			createSelectionListener();
		}
        return selectionListener;
    }
    
    /**
     * Creates a selection listener.
     */
    private void createSelectionListener() {
        selectionListener = new SelectionAdapter() {

        	/* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
			public void widgetSelected(SelectionEvent event) {
                Widget widget = event.widget;
                if (widget == addButton) {
                    addPressed();
                } else if (widget == removeButton) {
                    removePressed();
                } else if (widget == upButton) {
                    upPressed();
                } else if (widget == downButton) {
                    downPressed();
				} else if (widget == left) {
					selectionChanged();
				} else if (widget == right) {
					selectionChanged();
				}
			}
		};
    }

    /**
     * Notifies that the list selection has changed.
     */
    void selectionChanged() {
        int leftIndex = left.getSelectionIndex();
        int rightIndex = right.getSelectionIndex();
        int leftSize = leftElements.size();
        
        addButton.setEnabled(rightIndex >= 0);
        removeButton.setEnabled(leftIndex >= 0);
        upButton.setEnabled(leftSize > 1 && leftIndex > 0);
		downButton.setEnabled(leftSize > 1 && leftIndex >= 0
				&& leftIndex < leftSize - 1);
    }

    /**
	 * Notifies that the Up button has been pressed.
	 */
    void upPressed() {
        swap(true);
    }


    /**
     * Notifies that the Down button has been pressed.
     */
    void downPressed() {
        swap(false);
    }

    /**
     * Moves the currently selected item up or down.
     *
     * @param up <code>true</code> if the item should move up,
     *  and <code>false</code> if it should move down
     */
    private void swap(boolean up) {
        setPresentsDefaultValue(false);
        int index = left.getSelectionIndex();
        int target = up ? index - 1 : index + 1;

        if (index >= 0) {
            String[] selection = left.getSelection();
            Assert.isTrue(selection.length == 1);
            left.remove(index);
            left.add(selection[0], target);
            Object object = leftElements.get(index);
            leftElements.remove(index);
            leftElements.add(target, object);
            
            left.setSelection(target);
        }
        selectionChanged();
    }

	@Override
	protected void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData) left.getLayoutData()).horizontalSpan = 1;
        ((GridData) buttonBox.getLayoutData()).horizontalSpan = numColumns-2;
        ((GridData) right.getLayoutData()).horizontalSpan = 1;
	}

    /**
     * Combines the given list of objects into a single string.
     * This method is the converse of <code>parseString</code>. 
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @param objects the list of items
     * @return the combined string
     * @see #parseString
     */
    protected abstract String createList(ArrayList<Object> objects);

    /**
     * Splits the given string into a list of objects.
     * This method is the converse of <code>createList</code>. 
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @param stringList the string
     * @return an array of <code>String</code>
     * @see #createList
     */
    protected abstract ArrayList<Object> parseString(String stringList);
    
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
        Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        left = getListControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = 1;
        gd.minimumWidth = minWidth;
        gd.grabExcessHorizontalSpace = true;
        left.setLayoutData(gd);

        buttonBox = getButtonBoxControl(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.CENTER;
        buttonBox.setLayoutData(gd);

        right = getListControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = 1;
        gd.minimumWidth = minWidth;
        gd.grabExcessHorizontalSpace = true;
        right.setLayoutData(gd);

        selectionChanged();
	}
	
	@Override
	protected void doLoad() {
		if (left != null) {
			if (getPreferenceStore().contains(getPreferenceName())) {
				String s = getPreferenceStore().getString(getPreferenceName());
				left.removeAll();
				leftElements = parseString(s);
				for (Object object : leftElements) {
					left.add(getLabel(object));
				}

				s = getPreferenceStore().getDefaultString(getPreferenceName());
				ArrayList<Object> defaultElements = parseString(s);
				rightElements.clear();
				right.removeAll();
				for (Object object : defaultElements) {
					if (!leftElements.contains(object)) {
						rightElements.add(object);
						right.add(getLabel(object));
					}
				}
			}
			else {
				doLoadDefault();
			}
        }
	}

	@Override
	protected void doLoadDefault() {
		if (left != null) {
            String s = getPreferenceStore().getDefaultString(getPreferenceName());
            left.removeAll();
            leftElements = parseString(s);
            for (Object object : leftElements) {
                left.add(getLabel(object));
            }
            right.removeAll();
            rightElements.clear();
        }
	}

	@Override
	protected void doStore() {
		String s = createList(leftElements);
        if (s != null) {
			getPreferenceStore().setValue(getPreferenceName(), s);
		}
	}

	@Override
	public int getNumberOfControls() {
		return 3;
	}

	/**
     * Returns this field editor's list control.
     *
     * @param parent the parent control
     * @return the list control
     */
    public List getListControl(Composite parent) {
    	List list;
		list = new List(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL);
		list.setFont(parent.getFont());
		list.addSelectionListener(getSelectionListener());
        return list;
    }


    /**
	 * Returns this field editor's button box containing the Add, Remove, Up,
	 * and Down button.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the button box
	 */
	public Composite getButtonBoxControl(Composite parent) {
		buttonBox = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		buttonBox.setLayout(layout);
		createButtons(buttonBox);
		buttonBox.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				addButton = null;
				removeButton = null;
				upButton = null;
				downButton = null;
				buttonBox = null;
			}
		});
        return buttonBox;
    }

	public ArrayList<Object> getSelectedObjects() {
		return leftElements;
	}

}
