package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eventb.core.IPredicateElement;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class PredicateSection extends AbstractPropertySection implements
		IElementChangedListener {

	Text predicateText;

	IEventBInputText inputText;

	IPredicateElement element;

	public PredicateSection() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;

		predicateText = getWidgetFactory().createText(composite, "");

		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		predicateText.setLayoutData(data);

		inputText = new EventBMath(predicateText);

		new TimerText(predicateText, 1000) {

			@Override
			protected void response() {
				try {
					element.setPredicateString(predicateText.getText(),
							new NullProgressMonitor());
				} catch (RodinDBException e) {
					UIUtils.log(e, "Error modifiying element "
							+ element.getElementName());
					if (UIUtils.DEBUG)
						e.printStackTrace();
				}
			}

		};

		CLabel labelLabel = getWidgetFactory().createCLabel(composite,
				"Predicate:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(predicateText,
				ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(predicateText, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}

	@Override
	public void refresh() {
		try {
			predicateText.setText(element.getPredicateString());
		} catch (RodinDBException e) {
			String message = "Cannot get predicate string of "
					+ element.getElementName();
			UIUtils.log(e, message);
			if (UIUtils.DEBUG)
				e.printStackTrace();
		}
		super.refresh();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof IPredicateElement);
		this.element = (IPredicateElement) input;
	}

	public void elementChanged(ElementChangedEvent event) {
		// TODO Filter out the delta first
		if (predicateText.isDisposed())
			return;
		Display display = predicateText.getDisplay();
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
