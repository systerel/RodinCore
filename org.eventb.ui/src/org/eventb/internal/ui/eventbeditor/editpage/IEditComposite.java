package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.rodinp.core.IAttributedElement;

public interface IEditComposite {

	public abstract void refresh();

	public abstract void setElement(IAttributedElement element);

	abstract public void createComposite(FormToolkit toolkit, Composite parent);

	public abstract void setFillHorizontal(boolean fill);

	public abstract void setForm(ScrolledForm form);

	public abstract void setSelected(boolean selection);

	public abstract void setPrefix(String prefix);

	public abstract void setPostfix(String postfix);

	public abstract void setUndefinedValue();
	
}