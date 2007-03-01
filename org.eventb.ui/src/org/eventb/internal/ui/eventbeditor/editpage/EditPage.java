package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.eventbeditor.ISectionComposite;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

public class EditPage extends EventBEditorPage implements ISelectionProvider {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = EventBUIPlugin.PLUGIN_ID + ".edit"; //$NON-NLS-1$

	public static final String PAGE_TITLE = Messages.editorPage_edit_title;

	public static final String PAGE_TAB_TITLE = Messages.editorPage_edit_tabTitle;

	ISectionComposite [] sectionComps;
	
	// The scrolled form
	ScrolledForm form;

	/**
	 * Constructor: This default constructor will be used to create the page
	 */
	public EditPage() {
		super(PAGE_ID, PAGE_TAB_TITLE, PAGE_TITLE);
		listenerList = new ListenerList();
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

		sectionComps = editSectionRegistry.createSections(this, 
				toolkit, form, parent);
		
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

	private ListenerList listenerList;
	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listenerList.add(listener);
	}

	ISelection globalSelection;
	
	public ISelection getSelection() {
		return globalSelection;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listenerList.remove(listener);
	}

	public void setSelection(ISelection selection) {
		this.globalSelection = selection;
		fireSelectionChanged(new SelectionChangedEvent(this,
				globalSelection));
		IEventBEditor editor = (IEventBEditor) this.getEditor();
		ISelectionProvider selectionProvider = editor.getSite().getSelectionProvider();
		selectionProvider.setSelection(selection);
	}

	/**
	 * Notifies all registered selection changed listeners that the editor's
	 * selection has changed. Only listeners registered at the time this
	 * method is called are notified.
	 * 
	 * @param event
	 *            the selection changed event
	 */
	public void fireSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = this.listenerList.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunner.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}
	
	public void selectionChanges() {
		List<IInternalElement> elements = new ArrayList<IInternalElement>(); 
		for (ISectionComposite sectionComp : sectionComps) {
			List<IInternalElement> sel = sectionComp.getSelectedElements();
			elements.addAll(sel);
		}
		setSelection(new StructuredSelection(elements));
	}
}
