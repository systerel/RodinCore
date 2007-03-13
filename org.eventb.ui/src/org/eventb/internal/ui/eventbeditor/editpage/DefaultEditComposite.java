package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IRodinElement;

public abstract class DefaultEditComposite implements IEditComposite {

	ScrolledForm form;

	IRodinElement element;

	Control control;

	public void setForm(ScrolledForm form) {
		this.form = form;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setElement(org.rodinp.core.IRodinElement)
	 */
	public void setElement(IRodinElement element) {
		this.element = element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#createComposite(org.eclipse.ui.forms.widgets.FormToolkit,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	abstract public void createComposite(FormToolkit toolkit, Composite parent);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setValue()
	 */
	abstract public void setControlValue();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#getValue()
	 */
	abstract public String getValue();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setValue()
	 */
	abstract public void setValue();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#refresh(org.rodinp.core.IInternalElement)
	 */
	public void refresh() {
		setControlValue();
		internalPack();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setFillHorizontal(boolean)
	 */
	public void setFillHorizontal(boolean fill) {
		GridData gridData;
		gridData = new GridData(SWT.FILL, SWT.TOP, fill, false);

		control.setLayoutData(gridData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setControl(org.eclipse.swt.widgets.Control)
	 */
	public void setControl(Control control) {
		this.control = control;
	}

	void internalPack() {
		Composite parent = control.getParent();
		Rectangle bounds = parent.getBounds();
		Point preferredSize = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		if (preferredSize.x > bounds.width || preferredSize.y > bounds.height) {
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Full resize");
			form.getBody().pack();
			form.reflow(true);
		} else {
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Local resize");
			control.pack();
			parent.pack(true);
			parent.setBounds(bounds);
		}
	}

}
