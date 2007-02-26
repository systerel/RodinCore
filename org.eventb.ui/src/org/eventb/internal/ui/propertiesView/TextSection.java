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
import org.eventb.internal.ui.TimerText;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public abstract class TextSection extends AbstractPropertySection implements
		IElementChangedListener {

	Text textWidget;

	IInternalElement element;

	IEventBEditor editor;

	int style = SWT.SINGLE;

	public TextSection() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;
		setStyle();
		textWidget = getWidgetFactory().createText(composite, "", style);

		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		if ((style & SWT.MULTI) != 0)
			data.height = textWidget.getLineHeight() * 3;
		textWidget.setLayoutData(data);

		new TimerText(textWidget, 1000) {

			@Override
			protected void response() {
				try {
					setText(text.getText());
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};

		CLabel labelLabel = getWidgetFactory().createCLabel(composite,
				getLabel() + ":");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(textWidget,
				ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(textWidget, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}

	abstract void setStyle();

	abstract String getLabel();

	abstract void setText(String text) throws RodinDBException;

	abstract String getText() throws RodinDBException;

	@Override
	public void refresh() {
		try {
			textWidget.setText(getText());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		Assert.isTrue(input instanceof IInternalElement);
		this.element = (IInternalElement) input;
		refresh();
	}

	public void elementChanged(ElementChangedEvent event) {
		// TODO Filter out the delta first
		if (textWidget.isDisposed())
			return;
		Display display = textWidget.getDisplay();
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
