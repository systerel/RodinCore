/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;

public abstract class AbstractEditComposite implements IEditComposite {

	IEventBEditor<?> fEditor;
	
	ScrolledForm form;

	IAttributedElement element;

	Composite composite;
	
	Label prefixLabel;
	
	Label postfixLabel;

	private FormToolkit toolkit;
	
	protected IAttributeUISpec uiSpec;
	
	public AbstractEditComposite(IAttributeUISpec uiSpec) {
		this.uiSpec = uiSpec;
	}
	
	public void setForm(ScrolledForm form) {
		this.form = form;
	}

	protected FormToolkit getFormToolkit() {
		return toolkit;
	}
	
	public void refresh() {
		initialise();
		internalPack();
	}

	public void setElement(IAttributedElement element) {
		this.element = element;
	}

	public void createComposite(IEventBEditor<?> editor, FormToolkit tk, Composite parent) {
		this.fEditor = editor;
		this.toolkit = tk;
		
		String prefix = uiSpec.getPrefix();
		if (prefix == null)
			prefix = "";
		prefixLabel = toolkit.createLabel(parent, prefix);
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.CENTER;
		if (prefix == null)
			gridData.widthHint = 0;
		
		prefixLabel.setLayoutData(gridData);
		if (EventBEditorUtils.DEBUG)
			prefixLabel.setBackground(prefixLabel.getDisplay().getSystemColor(
					SWT.COLOR_CYAN));

		composite = toolkit.createComposite(parent);
		
		gridData = new GridData(SWT.FILL, SWT.CENTER, uiSpec.isFillHorizontal(), false);
		gridData.minimumWidth = 200;
		composite.setLayoutData(gridData);
		composite.setLayout(new FillLayout());
		
		initialise();
		String postfix = uiSpec.getPostfix();
		postfixLabel = toolkit.createLabel(parent, " " + postfix + " ");
		gridData = new GridData();
		gridData.verticalAlignment = SWT.CENTER;
		if (postfix == null)
			gridData.widthHint = 0;
		postfixLabel.setLayoutData(gridData);
		if (EventBEditorUtils.DEBUG)
			postfixLabel.setBackground(postfixLabel.getDisplay()
					.getSystemColor(SWT.COLOR_CYAN));
	}

	public abstract void initialise();

	void internalPack() {
		internalPack(composite);
	}

	public void setSelected(boolean selection) {
		if (selection) {
			postfixLabel.setBackground(postfixLabel.getDisplay()
					.getSystemColor(SWT.COLOR_GRAY));
			composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_GRAY));
			prefixLabel.setBackground(prefixLabel.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		} else {
			if (EventBEditorUtils.DEBUG) {
				postfixLabel.setBackground(postfixLabel.getDisplay()
						.getSystemColor(SWT.COLOR_CYAN));
				composite.setBackground(composite.getDisplay().getSystemColor(
						SWT.COLOR_CYAN));
				prefixLabel.setBackground(prefixLabel.getDisplay()
						.getSystemColor(SWT.COLOR_CYAN));
			}
			else {
				postfixLabel.setBackground(postfixLabel.getDisplay()
						.getSystemColor(SWT.COLOR_WHITE));
				composite.setBackground(composite.getDisplay().getSystemColor(
						SWT.COLOR_WHITE));
				prefixLabel.setBackground(prefixLabel.getDisplay()
						.getSystemColor(SWT.COLOR_WHITE));
			}
		}

	}

	private void internalPack(Control c) {
		if (c.equals(form.getBody())) {
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Full resize");
			form.reflow(true);			
		}
		Rectangle bounds = c.getBounds();
		Point preferredSize = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		if (preferredSize.x > bounds.width || preferredSize.y > bounds.height) {
			internalPack(c.getParent());
		} else {
			((Composite) c).layout(true);
			c.setBounds(bounds);
		}
	}

}
