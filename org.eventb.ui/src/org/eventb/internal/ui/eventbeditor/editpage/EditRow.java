package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

public class EditRow {

	Composite composite;

	Composite compParent;

	Composite nextComposite;

	FormToolkit toolkit;

	ScrolledForm form;

	IRodinElement element;

	IEditComposite[] editComposites;

	IElementComposite elementComp;
	
	ButtonComposite buttonComp;
	
	int level;
	
	char keyHold;

	public EditRow(IElementComposite elementComp, ScrolledForm form,
			FormToolkit toolkit, Composite compParent, Composite nextComposite,
			IRodinElement element, int level) {
		assert (element != null);
		this.elementComp = elementComp;
		this.form = form;
		this.toolkit = toolkit;
		this.compParent = compParent;
		this.nextComposite = nextComposite;
		this.element = element;
		this.level = level;

		createContents();
	}

	private void createContents() {
		composite = toolkit.createComposite(compParent);
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_RED));
		}
		if (nextComposite != null) {
			assert nextComposite.getParent() == compParent;
			composite.moveAbove(nextComposite);
		}
		IElementType<? extends IRodinElement> type = element.getElementType();
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		EditSectionRegistry sectionRegistry = EditSectionRegistry.getDefault();
		int numColumns = 1 + 3 * sectionRegistry.getNumAttributes(type);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns + 1;
		composite.setLayout(gridLayout);

		createButtons();
		editComposites = sectionRegistry.createAttributeComposites(form,
				toolkit, composite, element);
		toolkit.paintBordersFor(composite);
	}

	public void createButtons() {
		buttonComp = new ButtonComposite(elementComp);
		buttonComp.createContents(toolkit, composite, level);
	}

	public void refresh() {
		for (IEditComposite editComposite : editComposites) {
			editComposite.setElement(element);
			editComposite.refresh();
		}
		buttonComp.updateLinks();
	}

	public boolean isSelected() {
		return buttonComp.isSelected();
	}

	public IRodinElement getElement() {
		return element;
	}

	public void setElement(IInternalElement element) {
		assert element.getElementType() == this.element.getElementType();
		this.element = element;
		refresh();
	}

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
