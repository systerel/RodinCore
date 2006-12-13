package org.eventb.ui.eventbeditor;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public abstract class EventBEditorPage extends FormPage {

	private String pageTitle;
	
	public EventBEditorPage(String id, String tabTitle,
			String pageTitle) {
		super(id, tabTitle);
		this.pageTitle = pageTitle;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();

		form.setText(pageTitle);
	}

	/**
	 * Returns the parent event-B editor.
	 * 
	 * @return parent editor instance
	 */
	protected IEventBEditor getEventBEditor() {
		return (IEventBEditor) this.getEditor();
	}
	
}
