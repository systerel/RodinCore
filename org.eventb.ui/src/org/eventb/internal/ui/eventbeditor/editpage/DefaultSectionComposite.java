package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.Collection;
import java.util.Map;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.eventbeditor.ISectionComposite;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public abstract class DefaultSectionComposite implements ISectionComposite {

	Map<IRodinElement, Collection<IEditComposite>> map;

	Composite fComp;

	FormToolkit fToolkit;
	
	ScrolledForm fForm;
	
	IRodinFile fInput;
	
	IEventBEditor fEditor;
	
	public ISectionComposite create(IEventBEditor editor, FormToolkit toolkit, ScrolledForm form,
			Composite parent, IRodinFile rInput) {
		this.fEditor = editor;
		this.fInput = rInput;
		this.fToolkit = toolkit;
		this.fForm = form;
		this.fInput = rInput;
		
		fComp = toolkit.createComposite(parent);
		fComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		try {
			createContents();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}

	abstract void createContents() throws RodinDBException;

	void refresh() {
		for (Control control : fComp.getChildren()) {
			control.dispose();
		}
		try {
			createContents();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fComp.getParent().pack();
		fForm.reflow(true);
	}

}
