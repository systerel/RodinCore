/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - changed double click behavior
 *     Systerel - separation of file and root element
 *     Systerel - introduced read only elements
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import static org.eventb.internal.ui.EventBUtils.isReadOnly;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public abstract class TextSection extends AbstractPropertySection implements
		IElementChangedListener {

	Text textWidget;

	IEventBInputText inputText;
	
	IInternalElement element; // This can be null

	int style;

	boolean math;
	
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
		if (math) {
			inputText = new EventBMath(textWidget);
		}
		else {
			inputText = new EventBText(textWidget);
		}
		
		new TimerText(textWidget, 1000) {

			@Override
			protected void response() {
				try {
					setText(text.getText(), new NullProgressMonitor());
				} catch (RodinDBException e) {
					EventBUIExceptionHandler.handleSetAttributeException(e);
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

	void setStyle() {
		style = SWT.SINGLE;
		math = false;
	}

	@Override
	public void dispose() {
		inputText.dispose();
		super.dispose();
	}

	abstract String getLabel();

	void setText(String text, IProgressMonitor monitor)
			throws RodinDBException {
		UIUtils.setStringAttribute(element, getFactory(), text, monitor);
	}

	private String getText() throws RodinDBException {
		final IAttributeManipulation factory = getFactory();
		if (!factory.hasValue(element, null))
			return "";
		return factory.getValue(element, null);
	}

	protected abstract IAttributeManipulation getFactory();

	@Override
	public void refresh() {
		if (textWidget.isDisposed())
			return;
		
		try {
			String text = getText();
			if (text != null && !textWidget.getText().equals(text))
				textWidget.setText(text);
			
			if(element != null) {
				textWidget.setEditable(!isReadOnly(element));
			}
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetAttributeException(e,
					EventBUIExceptionHandler.UserAwareness.IGNORE);		
		}
		super.refresh();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		if (selection instanceof IStructuredSelection) {
			Object input = ((IStructuredSelection) selection).getFirstElement();
			if (input instanceof IInternalElement) {
				this.element = (IInternalElement) input;
			}
		}
		refresh();
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		// TODO Filter out the delta first
		if (textWidget.isDisposed())
			return;
		Display display = textWidget.getDisplay();
		display.asyncExec(new Runnable() {

			@Override
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

}
