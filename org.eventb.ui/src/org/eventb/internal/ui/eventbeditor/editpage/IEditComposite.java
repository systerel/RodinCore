package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.rodinp.core.IRodinElement;

public interface IEditComposite {

	public abstract void setElement(IRodinElement element);

	abstract public void createComposite(FormToolkit toolkit, Composite parent);

	abstract public void setValue();

	abstract public String getValue();

	public abstract void refresh();

	public abstract void setFillHorizontal(boolean fill);

	public abstract void setControl(Control control);

	public abstract void setForm(ScrolledForm form);

}