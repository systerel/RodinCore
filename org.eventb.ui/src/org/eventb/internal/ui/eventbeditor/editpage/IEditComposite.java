package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;

public interface IEditComposite {

	public abstract void refresh();

	public abstract void setElement(IAttributedElement element);

	abstract public void createComposite(IEventBEditor<?> editor,
			FormToolkit toolkit, Composite parent);

	public abstract void setForm(ScrolledForm form);

	public abstract void setSelected(boolean selection);

	public abstract void setUndefinedValue();

}