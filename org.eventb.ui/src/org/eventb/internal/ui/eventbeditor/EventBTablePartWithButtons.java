package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.rodinp.core.IRodinElement;

public abstract class EventBTablePartWithButtons
	extends EventBPartWithButtons
{
	public EventBTablePartWithButtons(final IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBEditor editor, String [] buttonLabels, String title, String description) {
		super(managedForm, parent, toolkit, style, editor, buttonLabels, title, description);
	}
	
	@Override
	protected Viewer createViewer(IManagedForm managedForm, FormToolkit toolkit, Composite parent) {
		return createTableViewer(managedForm, toolkit, parent);
	}

	abstract protected EventBEditableTableViewer createTableViewer(IManagedForm managedForm, FormToolkit toolkit, Composite parent);

	/**
	 * Set the selection in the table viewer.
	 * <p>
	 * @param element A Rodin element
	 */
	public void setSelection(IRodinElement element) {
		StructuredViewer viewer = (StructuredViewer) this.getViewer();
		viewer.setSelection(new StructuredSelection(element));
		edit(element);
	}
	
	protected void selectRow(int row, int column) {
		((EventBEditableTableViewer) getViewer()).selectRow(row, column);
	}

}
