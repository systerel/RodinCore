package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.IMachine;

public class SyntheticViewPage
	extends FormPage 
{
	// Title, tab title and ID of the page.
	public static final String PAGE_ID = "Synthetic View"; //$NON-NLS-1$
	public static final String PAGE_TITLE = "Synthetic View";
	public static final String PAGE_TAB_TITLE = "Synthetic";
	EventBTreePartWithButtons part;
	
	/**
	 * Constructor.
	 * @param editor The form editor that holds the page 
	 */
	public SyntheticViewPage(FormEditor editor) {
		super(editor, PAGE_ID, PAGE_TAB_TITLE);  //$NON-NLS-1$
	}

	/**
	 * Creating the content of the page
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		form.setText(PAGE_TITLE); //$NON-NLS-1$
		Composite body = form.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);

		if (((EventBEditor) this.getEditor()).getRodinInput() instanceof IMachine) 
			part = new SyntheticMachineViewSection(managedForm, body, managedForm.getToolkit(), Section.NO_TITLE, (EventBEditor) this.getEditor());
		else
			part = new SyntheticContextViewSection(managedForm, body, managedForm.getToolkit(), Section.NO_TITLE, (EventBEditor) this.getEditor());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		gd.minimumHeight = 150;
		gd.widthHint = 150;
		part.getSection().setLayoutData(gd);
		managedForm.addPart(part);
		this.getSite().setSelectionProvider(part.getViewer());
	}
	
}
