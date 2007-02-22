package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eventb.core.IEvent;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class ConvergenceSection extends AbstractPropertySection implements
		IElementChangedListener {

	CCombo convergenceCombo;

	IEvent element;

	IEventBEditor editor;

	private static final String ORDINARY = "ORDINARY";

	private static final String CONVERGENT = "CONVERGENT";

	private static final String ANTICIPATED = "ANTICIPATED";

	public ConvergenceSection() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;

		convergenceCombo = getWidgetFactory().createCCombo(composite,
				SWT.DEFAULT);

		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		convergenceCombo.setLayoutData(data);
		convergenceCombo.add(ORDINARY);
		convergenceCombo.add(CONVERGENT);
		convergenceCombo.add(ANTICIPATED);

		convergenceCombo.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				String text = convergenceCombo.getText();
				try {
					if (text.equals(ORDINARY)
							&& element.getConvergence() != Convergence.ORDINARY) {
						element.setConvergence(Convergence.ORDINARY,
								new NullProgressMonitor());
					} else if (text.equals(CONVERGENT)
							&& element.getConvergence() != Convergence.CONVERGENT) {
						element.setConvergence(Convergence.CONVERGENT,
								new NullProgressMonitor());
					} else if (text.equals(ANTICIPATED)
							&& element.getConvergence() != Convergence.ANTICIPATED) {
						element.setConvergence(Convergence.ANTICIPATED,
								new NullProgressMonitor());
					}
				} catch (RodinDBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		CLabel labelLabel = getWidgetFactory().createCLabel(composite,
				"Convergence:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(convergenceCombo,
				ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(convergenceCombo, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}

	@Override
	public void refresh() {
		try {
			Convergence convergence = element.getConvergence();
			if (convergence.equals(Convergence.ORDINARY)) {
				convergenceCombo.setText(ORDINARY);
			} else if (convergence.equals(Convergence.CONVERGENT)) {
				convergenceCombo.setText(CONVERGENT);
			} else {
				convergenceCombo.setText(ANTICIPATED);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			convergenceCombo.setText("false");
		}
		super.refresh();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(part instanceof IEventBEditor);
		editor = (IEventBEditor) part;
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof IEvent);
		this.element = (IEvent) input;
	}

	public void elementChanged(ElementChangedEvent event) {
		// TODO Filter out the delta first
		if (convergenceCombo.isDisposed())
			return;
		Display display = convergenceCombo.getDisplay();
		display.asyncExec(new Runnable() {

			public void run() {
				refresh();
			}

		});
	}

	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
		RodinCore.addElementChangedListener(this);
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		RodinCore.removeElementChangedListener(this);
	}

}
