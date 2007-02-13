package org.eventb.ui.eventbeditor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinFile;

public interface ISectionComposite extends IElementChangedListener {

	ISectionComposite create(
			IEventBEditor editor, FormToolkit toolkit, ScrolledForm form,
			Composite parent, IRodinFile rInput);

}
