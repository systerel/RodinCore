package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

public class ElementComposite implements IElementComposite {

	FormToolkit toolkit;

	ScrolledForm form;

	Composite compParent;

	IRodinElement rElement;

	EditRow row;

	Composite composite;

	ArrayList<ISectionComposite> sectionComps;

	EditPage page;

	int level;

	boolean isExpanded;

	public ElementComposite(EditPage page, FormToolkit toolkit,
			ScrolledForm form, Composite compParent, IRodinElement element,
			int level) {
		this.page = page;
		this.toolkit = toolkit;
		this.form = form;
		this.compParent = compParent;
		this.rElement = element;
		this.level = level;
		createContents();
	}

	private void createContents() {
		composite = toolkit.createComposite(compParent);
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		}
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		composite.setLayout(gridLayout);

		row = new EditRow(this, form, toolkit);
		row.createContents(toolkit, composite, null, level);
		setExpand(false);
	}

	public void folding() {
		setExpand(!isExpanded);
	}

	public void setExpand(boolean isExpanded) {
		this.isExpanded = isExpanded;
		if (isExpanded)
			createSectionComposites();
		else {
			if (sectionComps != null) {
				for (ISectionComposite sectionComp : sectionComps) {
					sectionComp.dispose();
				}
				sectionComps.clear();
			}
		}

		form.getBody().pack(true);
		form.reflow(true);
	}

	protected void createSectionComposites() {
		EditSectionRegistry editSectionRegistry = EditSectionRegistry
				.getDefault();

		IInternalElementType<? extends IInternalElement>[] types = editSectionRegistry
				.getChildrenTypes(rElement.getElementType());
		sectionComps = new ArrayList<ISectionComposite>(types.length);
		for (IInternalElementType<? extends IInternalElement> type : types) {

			// Create the section composite
			sectionComps.add(new SectionComposite(page, toolkit, form,
					composite, (IInternalParent) rElement, type, level + 1));

		}
	}

	public EditPage getPage() {
		return page;
	}

	public void refresh(IRodinElement element) {
		if (!rElement.exists())
			return;
		if (element == rElement) {
			row.refresh();
			
			// Refresh sub section composite as well?
			EditSectionRegistry editSectionRegistry = EditSectionRegistry
					.getDefault();
			IElementType<? extends IRodinElement>[] childrenTypes = editSectionRegistry
					.getChildrenTypes(element.getElementType());
			
			boolean recreate = false;
			if (childrenTypes.length != sectionComps.size()) {
				recreate = true;
			}
			else {
				for (int i = 0; i < childrenTypes.length; ++i) {
					if (sectionComps.get(i).getElementType() != childrenTypes[i]) {
						recreate = true;
						break;
					}
				}
			}

			if (recreate) {
				for (ISectionComposite sectionComp : sectionComps) {
					sectionComp.dispose();
				}
				createSectionComposites();
			}
		} else {
			if (rElement.isAncestorOf(element)) {
				for (ISectionComposite sectionComp : sectionComps) {
					sectionComp.refresh(element);
				}
			}
		}
	}

	public void elementRemoved(IRodinElement element) {
		if (!rElement.exists())
			return;
		assert (!rElement.equals(element));
		if (rElement.isAncestorOf(element)) {
			for (ISectionComposite sectionComp : sectionComps) {
				sectionComp.elementRemoved(element);
			}
		}
	}

	public void elementAdded(IRodinElement element) {
		if (!rElement.exists())
			return;
		if (rElement.isAncestorOf(element)) {
			for (ISectionComposite sectionComp : sectionComps) {
				sectionComp.elementAdded(element);
			}
		}
	}

	public void dispose() {
		composite.dispose();
	}

	public IRodinElement getElement() {
		return rElement;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void childrenChanged(IRodinElement element, IElementType childrenType) {
		if (!rElement.exists())
			return;

		if (rElement.equals(element)) {
			for (ISectionComposite sectionComp : sectionComps) {
				if (sectionComp.getElementType().equals(childrenType)) {
					sectionComp.childrenChanged(element, childrenType);
				}
			}

			row.updateLinks();
		}

		if (rElement.isAncestorOf(element)) {
			for (ISectionComposite sectionComp : sectionComps) {
				sectionComp.childrenChanged(element, childrenType);
			}
		}
	}

	public Composite getComposite() {
		return composite;
	}

	public void select(IRodinElement element, boolean select) {
		if (!rElement.exists())
			return;
		
		if (rElement.equals(element)) {
			row.setSelected(select);
		}

		if (rElement.isAncestorOf(element)) {
			for (ISectionComposite sectionComp : sectionComps) {
				sectionComp.select(element, select);
			}
		}
	}

}
