package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.eventbeditor.ISectionComposite;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

public class EditPage extends EventBEditorPage {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = EventBUIPlugin.PLUGIN_ID + ".edit"; //$NON-NLS-1$

	public static final String PAGE_TITLE = Messages.editorPage_edit_title;

	public static final String PAGE_TAB_TITLE = Messages.editorPage_edit_tabTitle;

	// The scrolled form
	ScrolledForm form;

	/**
	 * Constructor: This default constructor will be used to create the page
	 */
	public EditPage() {
		super(PAGE_ID, PAGE_TAB_TITLE, PAGE_TITLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
	 */
	@Override
	public void initialize(FormEditor editor) {
		super.initialize(editor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		form = managedForm.getForm();
		Composite body = form.getBody();
		GridLayout gLayout = new GridLayout();
		gLayout.horizontalSpacing = 5;
		body.setLayout(gLayout);

		// TODO: Create different section here (extensible)
		createSections(body);
	}

	public void createSections(final Composite parent) {
		FormToolkit toolkit = this.getManagedForm().getToolkit();
		EventBEditor editor = (EventBEditor) this
				.getEditor();

		EditSectionRegistry editSectionRegistry = EditSectionRegistry
				.getDefault();

		IRodinFile rodinInput = editor.getRodinInput();

		ISectionComposite[] sectionComps = editSectionRegistry.createSections(editor, 
				toolkit, form, parent,
				rodinInput);
		
		for (ISectionComposite sectionComp : sectionComps) {
			editor.addElementChangedListener(sectionComp);
		}
	}

	public static Map<IRodinElement, Collection<IEditComposite>> addToMap(
			Map<IRodinElement, Collection<IEditComposite>> map,
			IRodinElement element, IEditComposite editComposite) {
		Collection<IEditComposite> editComposites = map.get(element);
		if (editComposites == null) {
			editComposites = new ArrayList<IEditComposite>();
			map.put(element, editComposites);
		}
		editComposites.add(editComposite);
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		// createSections(form.getBody());
	}

}
