/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - made IAttributeFactory generic
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.Set;

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
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;

/**
 * @author htson
 *         <p>
 *         Abstract implmentation for edit composite which is used to edit an
 *         attribute of a particular element.
 *         </p>
 */
public abstract class AbstractEditComposite<E extends IAttributedElement>
		implements IEditComposite<E> {

	// The Event-B Editor.
	IEventBEditor<?> fEditor;
	
	// The attributed element to edit.
	E element;

	// The UI information on how this attribute is displayed and/or edited.
	protected IAttributeUISpec<E> uiSpec;
	
	// The main scrolled form of the edit page.
	ScrolledForm form;

	// The form toolkit used to create the widgets
	private FormToolkit toolkit;
	
	// The layout of the widgets are as follow (without the separator "|") :
	// prefixLabel | composite | postfixLabel
	
	// The main composite of this edit composite 
	Composite composite;
	
	// The prefix label widget. This is created lazily.
	Label prefixLabel;
	
	// The postfix label widget. This is created lazily.
	Label postfixLabel;

	
	/**
	 * Constructor. No actual widgets are created at the moment. Client has to
	 * call {@link #createComposite(IEventBEditor, FormToolkit, Composite)} to
	 * create the widgets themshelves.
	 * 
	 * @param uiSpec
	 *            a UI Spec for an attribute.
	 */
	public AbstractEditComposite(IAttributeUISpec<E> uiSpec) {
		this.uiSpec = uiSpec;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#setForm(org.eclipse.ui.forms.widgets.ScrolledForm)
	 */
	public void setForm(ScrolledForm form) {
		this.form = form;
	}

	protected FormToolkit getFormToolkit() {
		return toolkit;
	}
	
	public void refresh(boolean refreshMarker) {
		initialise(refreshMarker);
		internalPack();
	}

	public void setElement(E element) {
		this.element = element;
	}

	public void createComposite(IEventBEditor<?> editor, FormToolkit tk,
			Composite parent) {
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
			prefixLabel.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_CYAN));

		composite = toolkit.createComposite(parent);
		
		gridData = new GridData(SWT.FILL, SWT.CENTER, uiSpec.isFillHorizontal(), false);
		gridData.minimumWidth = 200;
		composite.setLayoutData(gridData);
		composite.setLayout(new FillLayout());
		
		initialise(true);
		String postfix = uiSpec.getPostfix();
		postfixLabel = toolkit.createLabel(parent, " " + postfix + " ");
		gridData = new GridData();
		gridData.verticalAlignment = SWT.CENTER;
		if (postfix == null)
			gridData.widthHint = 0;
		postfixLabel.setLayoutData(gridData);
		if (EventBEditorUtils.DEBUG) {
			postfixLabel.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_CYAN));
		}
	}

	public abstract void initialise(boolean refreshMarker);

	void internalPack() {
		internalPack(composite);
	}

	public void setSelected(boolean selection) {
		if (selection) {
			postfixLabel.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_GRAY));
			composite.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_GRAY));
			prefixLabel.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_GRAY));
		} else {
			if (EventBEditorUtils.DEBUG) {
				postfixLabel.setBackground(EventBSharedColor
						.getSystemColor(SWT.COLOR_CYAN));
				composite.setBackground(EventBSharedColor.getSystemColor(
						SWT.COLOR_CYAN));
				prefixLabel.setBackground(EventBSharedColor
						.getSystemColor(SWT.COLOR_CYAN));
			}
			else {
				postfixLabel.setBackground(EventBSharedColor
						.getSystemColor(SWT.COLOR_WHITE));
				composite.setBackground(EventBSharedColor.getSystemColor(
						SWT.COLOR_WHITE));
				prefixLabel.setBackground(EventBSharedColor
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

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IEditComposite#getAttributeType()
	 */
	public IAttributeType getAttributeType() {
		return uiSpec.getAttributeType();
	}

	public void refresh(Set<IAttributeType> set) {
		if (set.size() == 0
				|| set.contains(uiSpec.getAttributeType())) {
			refresh(true);
		}
	}

}
