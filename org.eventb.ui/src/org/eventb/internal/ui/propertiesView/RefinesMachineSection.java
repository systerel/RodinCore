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
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class RefinesMachineSection extends AbstractPropertySection implements
		IElementChangedListener {

	CCombo machineCombo;

	IInternalElement element;

	IEventBEditor editor;

	public RefinesMachineSection() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;

		machineCombo = getWidgetFactory().createCCombo(composite, SWT.BORDER);

		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		machineCombo.setLayoutData(data);

		machineCombo.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				String text = machineCombo.getText();
				setContext(text);
			}

		});

		CLabel labelLabel = getWidgetFactory().createCLabel(composite,
				"Context:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(machineCombo,
				ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(machineCombo, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}

	public void setContext(String text) {
		assert element instanceof IRefinesMachine;
		IRefinesMachine sElement = (IRefinesMachine) element;
		try {
			if (!sElement.getAbstractMachineName().equals(text)) {
				sElement.setAbstractMachineName(text,
						new NullProgressMonitor());
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void refresh() {
		initMachineCombo();
		super.refresh();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(part instanceof IEventBEditor);
		editor = (IEventBEditor) part;
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof IInternalElement);
		this.element = (IInternalElement) input;
	}

	public void elementChanged(ElementChangedEvent event) {
		// TODO Filter out the delta first
		if (machineCombo.isDisposed())
			return;
		Display display = machineCombo.getDisplay();
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

	final void initMachineCombo() {
		machineCombo.removeAll();
		final IRodinProject project = editor.getRodinInput().getRodinProject();
		final IMachineFile[] machines;
		try {
			machines = project.getChildrenOfType(IMachineFile.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			UIUtils.log(e, "when listing the contexts of " + project);
			return;
		}
		for (IMachineFile context : machines) {
			final String bareName = context.getComponentName();
			machineCombo.add(bareName);
		}
		setInitialInput();
	}

	public void setInitialInput() {
		assert element instanceof IRefinesMachine;
		IRefinesMachine rElement = (IRefinesMachine) element;
		try {
			machineCombo.setText(rElement.getAbstractMachineName());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
