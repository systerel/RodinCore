package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.ITheorem;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.eventbeditor.ISectionComposite;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class TheoremSectionComposite implements ISectionComposite {

	Map<IRodinElement, Collection<IEditComposite>> map;

	public ISectionComposite create(IEventBEditor editor, FormToolkit toolkit,
			ScrolledForm form, Composite parent, IRodinFile rInput) {

		map = new HashMap<IRodinElement, Collection<IEditComposite>>();

		EditSectionRegistry sectionRegistry = EditSectionRegistry.getDefault();
		int numColumns = sectionRegistry.getNumColumns(ITheorem.ELEMENT_TYPE) + 1;

		final Composite comp = toolkit.createComposite(parent);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns;
		comp.setLayout(gridLayout);
		Label label = toolkit.createLabel(comp, "THEOREMS");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		label.setLayoutData(gd);

		ITheorem[] theorems;
		try {
			theorems = rInput.getChildrenOfType(ITheorem.ELEMENT_TYPE);
		} catch (RodinDBException e1) {
			// TODO Auto-generated catch block
			return this;
		}

		for (ITheorem theorem : theorems) {
			createButtons(toolkit, comp);

			map = sectionRegistry.createColumns(form, toolkit, comp, theorem,
					map);
		}
		toolkit.paintBordersFor(comp);

		return this;
	}

	private void createButtons(FormToolkit toolkit, Composite parent) {
		Button button = toolkit.createButton(parent, "+", SWT.PUSH);
		GridData gd = new GridData();
		button.setLayoutData(gd);
	}

	public void elementChanged(ElementChangedEvent event) {
		// TODO Auto-generated method stub

	}

}
