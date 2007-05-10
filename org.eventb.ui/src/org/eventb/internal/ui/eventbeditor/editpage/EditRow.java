package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;

public class EditRow {

	Composite composite;

	ScrolledForm form;

	IEditComposite[] editComposites;

	IElementComposite elementComp;
	
	ButtonComposite buttonComp;
	
	public EditRow(IElementComposite elementComp, ScrolledForm form,
			FormToolkit toolkit) {
		this.elementComp = elementComp;
		this.form = form;
	}

	public void createContents(FormToolkit toolkit, Composite parent,
			Composite sibling, int level) {
		composite = toolkit.createComposite(parent);
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_RED));
		}
		if (sibling != null) {
			assert sibling.getParent() == parent;
			composite.moveAbove(sibling);
		}
		IRodinElement element = elementComp.getElement(); 
		IElementType<? extends IRodinElement> type = element.getElementType();
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		EditSectionRegistry sectionRegistry = EditSectionRegistry.getDefault();
		int numColumns = 1 + 3 * sectionRegistry.getNumAttributes(type);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns + 1;
		composite.setLayout(gridLayout);

		createButtons(toolkit, level);
		editComposites = sectionRegistry.createAttributeComposites(element.getElementType());
		for (IEditComposite editComposite : editComposites) {
			editComposite.setForm(form);
			editComposite.setElement(element);
			editComposite.createComposite(toolkit, composite);
		}
		toolkit.paintBordersFor(composite);
	}

	private void createButtons(FormToolkit toolkit, int level) {
		buttonComp = new ButtonComposite(elementComp);
		buttonComp.createContents(toolkit, composite, level);
	}

	public void refresh() {
		IRodinElement element = elementComp.getElement(); 
		for (IEditComposite editComposite : editComposites) {
			editComposite.setElement(element);
			editComposite.refresh();
		}
		buttonComp.updateLinks();
	}

	public boolean isSelected() {
		return buttonComp.isSelected();
	}

//	public IRodinElement getElement() {
//		return element;
//	}

//	public void setElement(IInternalElement element) {
//		assert element.getElementType() == this.element.getElementType();
//		this.element = element;
//		refresh();
//	}

	public void dispose() {
		composite.dispose();
	}

	public Composite getComposite() {
		return composite;
	}

	public void setSelected(boolean select) {
		for (IEditComposite editComposite : editComposites) {
			editComposite.setSelected(select);
		}
		if (select) {
			composite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		}
		else {
			if (EventBEditorUtils.DEBUG) {
				composite.setBackground(composite.getDisplay().getSystemColor(
						SWT.COLOR_RED));
			}
			else {
				composite.setBackground(composite.getDisplay().getSystemColor(
						SWT.COLOR_WHITE));				
			}
		}
		buttonComp.setSelected(select);
	}

	public void updateLinks() {
		buttonComp.updateLinks();
	}

}
