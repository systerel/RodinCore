package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.EventBPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.eventbeditor.ISectionComposite;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinFile;

public class DeclarationSectionComposite implements ISectionComposite {

	public ISectionComposite create(IEventBEditor editor, FormToolkit toolkit,
			ScrolledForm form, Composite parent, IRodinFile rInput) {
		final Composite section1 = toolkit.createComposite(parent);
		section1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		section1.setLayout(gridLayout);
		toolkit.createLabel(section1, "MACHINE");
		toolkit.createLabel(section1, EventBPlugin.getComponentName(rInput
				.getElementName()));
		toolkit.paintBordersFor(section1);
		return this;
	}

	public void elementChanged(ElementChangedEvent event) {
		// TODO Auto-generated method stub

	}

}
