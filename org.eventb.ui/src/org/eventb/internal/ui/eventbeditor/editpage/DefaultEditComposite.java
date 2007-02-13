package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.rodinp.core.IRodinElement;

public abstract class DefaultEditComposite implements IEditComposite {
	
	ScrolledForm form;
	
	IRodinElement element;

	Control control;
	
	public void setForm(ScrolledForm form) {
		this.form = form;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setElement(org.rodinp.core.IRodinElement)
	 */
	public void setElement(IRodinElement element) {
		this.element = element;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#createComposite(org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.swt.widgets.Composite)
	 */
	abstract public void createComposite(FormToolkit toolkit, Composite parent);

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setValue()
	 */
	abstract public void setValue();
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#getValue()
	 */
	abstract public String getValue();
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#refresh()
	 */
	public void refresh() {
		setValue();
		internalPack();
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setFillHorizontal(boolean)
	 */
	public void setFillHorizontal(boolean fill) {
		if (fill)
			control.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		else
			control.setLayoutData(new GridData());
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setControl(org.eclipse.swt.widgets.Control)
	 */
	public void setControl(Control control) {
		this.control = control;
	}
	
	void internalPack() {
		Composite parent = control.getParent();
		parent.setRedraw(false);
		Rectangle bounds = control.getBounds();
		Point preferredSize = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (preferredSize.x > bounds.width || preferredSize.y > bounds.height) {
			parent.pack();
			form.reflow(true);
		}
		parent.setRedraw(true);
	}

}
