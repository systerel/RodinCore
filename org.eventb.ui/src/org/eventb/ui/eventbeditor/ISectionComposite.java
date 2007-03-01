package org.eventb.ui.eventbeditor;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.eventbeditor.editpage.EditPage;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;

public interface ISectionComposite extends IElementChangedListener {

	ISectionComposite create(EditPage page, FormToolkit toolkit,
			ScrolledForm form, Composite parent);

	List<IInternalElement> getSelectedElements();

}
