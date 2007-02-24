package org.eventb.internal.ui.propertiesView;

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
import org.eventb.core.IContextFile;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public abstract class AbstractContextSection<F extends IRodinFile> extends AbstractPropertySection implements
		IElementChangedListener {

	CCombo contextCombo;

	IInternalElement element;

	IEventBEditor<F> editor;

	public AbstractContextSection() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;

		contextCombo = getWidgetFactory().createCCombo(composite, SWT.BORDER);

		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		contextCombo.setLayoutData(data);

		contextCombo.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				String text = contextCombo.getText();
				setContext(text);
			}

		});

		CLabel labelLabel = getWidgetFactory().createCLabel(composite,
				"Context:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(contextCombo,
				ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(contextCombo, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}

	public abstract void setContext(String text);

	@Override
	public void refresh() {
		initContextCombo();
		super.refresh();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(part instanceof IEventBEditor);
		editor = (IEventBEditor<F>) part;
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof IInternalElement);
		this.element = (IInternalElement) input;
	}

	public void elementChanged(ElementChangedEvent event) {
		// TODO Filter out the delta first
		if (contextCombo.isDisposed())
			return;
		Display display = contextCombo.getDisplay();
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

	final void initContextCombo() {
		contextCombo.removeAll();
		final IRodinProject project = editor.getRodinInput().getRodinProject();
		final IContextFile[] contexts;
		try {
			contexts = project.getChildrenOfType(IContextFile.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			UIUtils.log(e, "when listing the contexts of " + project);
			return;
		}
		for (IContextFile context : contexts) {
			final String bareName = context.getComponentName();
				contextCombo.add(bareName);
		}
		setInitialInput();
	}

	public abstract void setInitialInput();

}
