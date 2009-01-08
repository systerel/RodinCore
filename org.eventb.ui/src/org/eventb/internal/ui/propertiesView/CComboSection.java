/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - used IAttributeFactory
 *     Systerel - removed MouseWheel Listener of CCombo
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public abstract class CComboSection<E extends IAttributedElement> extends
		AbstractPropertySection implements IElementChangedListener {

	CCombo comboWidget;

	private IAttributeFactory<E> factory = null;

	E element;

	public CComboSection() {
		// Do nothing
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;

		comboWidget = getWidgetFactory().createCCombo(composite, SWT.DEFAULT);

		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		comboWidget.setLayoutData(data);
		
		// to fix bug 2417413
		UIUtils.removeTextListener(comboWidget);
		
		comboWidget.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				try {
					setText(comboWidget.getText(), new NullProgressMonitor());
				} catch (RodinDBException exception) {
					EventBUIExceptionHandler
							.handleSetAttributeException(exception);
				}
			}

		});

		CLabel labelLabel = getWidgetFactory().createCLabel(composite,
				getLabel() + ":");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(comboWidget,
				ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(comboWidget, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}

	abstract String getLabel();

	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element, getFactory(), text, null);
	}

	void setData() {
		for (String value : getFactory().getPossibleValues(element, null))
			comboWidget.add(value);
	}

	String getText() throws RodinDBException {
		return getFactory().getValue(element, null);
	}

	@Override
	public void refresh() {
		if (comboWidget.isDisposed())
			return;

		try {
			comboWidget.removeAll();
			setData();
			comboWidget.setText(getText());
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		super.refresh();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		if (selection instanceof IStructuredSelection) {
			Object input = ((IStructuredSelection) selection).getFirstElement();
			if (input instanceof IInternalElement) {
				// TODO should check compatibility from the factory.
				this.element = (E) input;
			}
		}
		refresh();
	}

	public void elementChanged(ElementChangedEvent event) {
		// TODO Filter out the delta first
		if (comboWidget.isDisposed())
			return;
		Display display = comboWidget.getDisplay();
		display.asyncExec(new Runnable() {

			public void run() {
				refresh();
			}

		});
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

	private IAttributeFactory<E> getFactory() {
		if (factory == null)
			factory = createFactory();
		return factory;
	}

	abstract protected IAttributeFactory<E> createFactory();

}
