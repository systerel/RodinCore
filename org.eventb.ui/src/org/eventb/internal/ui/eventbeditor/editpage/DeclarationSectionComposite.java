package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.EventBPlugin;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.eventbeditor.ISectionComposite;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;

public class DeclarationSectionComposite implements ISectionComposite {

	public ISectionComposite create(EditPage page, FormToolkit toolkit,
			ScrolledForm form, Composite parent) {
		final Composite comp = toolkit.createComposite(parent);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		comp.setLayout(gridLayout);
		IEventBEditor editor = (IEventBEditor) page.getEditor();
		FormText widget = toolkit.createFormText(comp, true);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		widget.setLayoutData(gd);
		new EventBFormText(widget);
		String text = "<form><li style=\"text\" bindent = \"-20\"><b>MACHINE</b> "
				+ EventBPlugin.getComponentName(editor.getRodinInput()
						.getElementName()) + "</li></form>";
		widget.setText(text, true, true);

		toolkit.paintBordersFor(comp);
		return this;
	}

	public void elementChanged(ElementChangedEvent event) {
		// Do nothing
	}

	public List<IInternalElement> getSelectedElements() {
		return new ArrayList<IInternalElement>();
	}

}
