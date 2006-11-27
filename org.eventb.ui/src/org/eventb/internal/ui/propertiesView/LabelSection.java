package org.eventb.internal.ui.propertiesView;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eventb.internal.ui.ElementUIRegistry;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class LabelSection extends AbstractPropertySection implements
		IElementChangedListener {

	Text labelText;

	IInternalElement element;

	public LabelSection() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;

		labelText = getWidgetFactory().createText(composite, "");

		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		labelText.setLayoutData(data);

		new TimerText(labelText, 1000) {

			@Override
			protected void response() {
				try {
					ElementUIRegistry.getDefault().modify(element, "name",
							labelText.getText());
				} catch (RodinDBException e) {
					UIUtils.log(e, "Error modifiying element "
							+ element.getElementName());
					if (UIUtils.DEBUG)
						e.printStackTrace();
				}
			}

		};

		CLabel labelLabel = getWidgetFactory()
				.createCLabel(composite, "Label:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(labelText,
				ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(labelText, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}

	@Override
	public void refresh() {
		labelText.setText(ElementUIRegistry.getDefault().getLabel(element));
		super.refresh();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof IInternalElement);
		this.element = (IInternalElement) input;
	}

	public void elementChanged(ElementChangedEvent event) {
		// TODO Filter out the delta first
		if (labelText.isDisposed()) return;
		Display display = labelText.getDisplay();
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
