/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import static org.eventb.internal.ui.EventBUtils.isReadOnly;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eventb.internal.ui.eventbeditor.elementdesc.TwoStateControl;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;

public abstract class ToggleSection extends AbstractPropertySection implements
		IElementChangedListener {

	private TwoStateControl control;

	private IInternalElement element;

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		final TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		final Composite composite = factory.createFlatFormComposite(parent);

		control = new TwoStateControl(getManipulation());
		control.setElement(element);

		control.createCheckBox(composite);

		setButtonFormData();
		setLabel(composite);

	}

	private void setButtonFormData() {
		assert control != null;

		final Button button = control.getButton();
		final FormData data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		// hide the text of button.
		data.width = 20;
		button.setLayoutData(data);
	}

	private void setLabel(Composite composite) {
		final Button button = control.getButton();
		final CLabel label = getWidgetFactory().createCLabel(composite,
				getLabel() + ":");
		final FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(button, ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(button, 0, SWT.CENTER);
		label.setLayoutData(data);
	}

	@Override
	public void refresh() {
		if (control.getButton().isDisposed())
			return;

		control.setElement(element);
		control.refresh();
		if(element != null) {
			control.getButton().setEnabled(!isReadOnly(element));
		}
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		if (selection instanceof IStructuredSelection) {
			final Object input = ((IStructuredSelection) selection)
					.getFirstElement();
			if (input instanceof IInternalElement) {
				this.element = (IInternalElement) input;
			}
		}
		refresh();
	}

	@Override
	public void aboutToBeHidden() {
		RodinCore.removeElementChangedListener(this);
		super.aboutToBeHidden();
	}

	@Override
	public void aboutToBeShown() {
		RodinCore.addElementChangedListener(this);
		super.aboutToBeShown();
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		final Button button = control.getButton();
		if (button.isDisposed())
			return;
		final Display display = button.getDisplay();
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				refresh();
			}

		});
	}

	protected abstract String getLabel();

	@Override
	public void dispose() {
		control.getButton().dispose();
		super.dispose();
	}

	protected abstract AbstractBooleanManipulation getManipulation();
}
