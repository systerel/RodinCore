package org.eventb.internal.ui.preferences;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Widget;

public abstract class TwoListSelectionEditor extends FieldEditor {

    /**
	 * The right table viewer: selected objects; can be <code>null</code>
	 * (before creation or after disposal).
	 */
    List selected;

    /**
     * The left list widget: available objects; can be <code>null</code>
     * (before creation or after disposal).
     */
    List available;
	
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

    private ArrayList<Object> selectedElements;
    
    private ArrayList<Object> availableElements;
       
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
		selectedElements = new ArrayList<Object>();
		availableElements = new ArrayList<Object>();
        init(name, labelText);
        createControl(parent);
    }

    /**
     * Notifies that the Add button has been pressed.
     */
    void addPressed() {
        setPresentsDefaultValue(false);
        move(available, selected, availableElements, selectedElements);
    }

    /**
     * Notifies that the Remove button has been pressed.
     */
    void removePressed() {
        setPresentsDefaultValue(false);
        move(selected, available, selectedElements, availableElements);
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
            selected.getParent().layout(true);
        }    	
    }
    
    protected abstract String getLabel(Object object);

    /**
     * Creates the Add, Remove, Up, and Down button in the given button box.
     *
     * @param box the box for the buttons
     */
    private void createButtons(Composite box) {
        addButton = createPushButton(box, ">>");//$NON-NLS-1$
        removeButton = createPushButton(box, "<<");//$NON-NLS-1$
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
				} else if (widget == selected) {
					selectionChanged();
				} else if (widget == available) {
					selectionChanged();
				}
			}
		};
    }

    /**
     * Notifies that the list selection has changed.
     */
    void selectionChanged() {
        int selectedIndex = selected.getSelectionIndex();
        int availableIndex = available.getSelectionIndex();
        int selectedSize = selectedElements.size();
        
        addButton.setEnabled(availableIndex >= 0);
        removeButton.setEnabled(selectedIndex >= 0);
        upButton.setEnabled(selectedSize > 1 && selectedIndex > 0);
		downButton.setEnabled(selectedSize > 1 && selectedIndex >= 0
				&& selectedIndex < selectedSize - 1);
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
        int index = selected.getSelectionIndex();
        int target = up ? index - 1 : index + 1;

        if (index >= 0) {
            String[] selection = selected.getSelection();
            Assert.isTrue(selection.length == 1);
            selected.remove(index);
            selected.add(selection[0], target);
            Object object = selectedElements.get(index);
            selectedElements.remove(index);
            selectedElements.add(target, object);
            
            selected.setSelection(target);
        }
        selectionChanged();
    }

	@Override
	protected void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData) selected.getLayoutData()).horizontalSpan = 1;
        ((GridData) buttonBox.getLayoutData()).horizontalSpan = numColumns-2;
        ((GridData) available.getLayoutData()).horizontalSpan = 1;
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

        Label availableLabel = new Label(parent, SWT.LEFT);
        availableLabel.setText("Available:");
        gd = new GridData(GridData.FILL_HORIZONTAL);
        availableLabel.setLayoutData(gd);

        Label tmpLabel = new Label(parent, SWT.CENTER);
        gd = new GridData();
        gd.verticalAlignment = GridData.CENTER;
        tmpLabel.setLayoutData(gd);
        
        Label selectedLabel = new Label(parent, SWT.LEFT);
        selectedLabel.setText("Selected:");
        gd = new GridData(GridData.FILL_HORIZONTAL);
        selectedLabel.setLayoutData(gd);

        available = getListControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = 1;
        gd.minimumWidth = minWidth;
        gd.grabExcessHorizontalSpace = true;
        available.setLayoutData(gd);

        buttonBox = getButtonBoxControl(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.CENTER;
        buttonBox.setLayoutData(gd);

        selected = getListControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = 1;
        gd.minimumWidth = minWidth;
        gd.grabExcessHorizontalSpace = true;
        selected.setLayoutData(gd);

        selectionChanged();
	}
	
	@Override
	protected void doLoad() {
		if (selected != null) {
			if (getPreferenceStore().contains(getPreferenceName())) {
				String s = getPreferenceStore().getString(getPreferenceName());
				setPreference(s);
			}
			else {
				doLoadDefault();
			}
        }
	}

	private void setPreference(String preference) {
		selected.removeAll();
		selectedElements = parseString(preference);
		for (Object object : selectedElements) {
			selected.add(getLabel(object));
		}

		Collection<Object> declaredElements = getDeclaredObjects();
		availableElements.clear();
		available.removeAll();
		for (Object object : declaredElements) {
			if (!selectedElements.contains(object)) {
				availableElements.add(object);
				available.add(getLabel(object));
			}
		}		
	}
	protected abstract Collection<Object> getDeclaredObjects();
	
	@Override
	protected void doLoadDefault() {
		if (selected != null) {
            String s = getPreferenceStore().getDefaultString(getPreferenceName());
            setPreference(s);
        }
	}

	@Override
	protected void doStore() {
		String s = createList(selectedElements);
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
		return selectedElements;
	}

}
