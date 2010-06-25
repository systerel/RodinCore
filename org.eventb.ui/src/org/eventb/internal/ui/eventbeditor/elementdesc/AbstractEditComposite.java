/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - made IAttributeFactory generic
 *     Systerel - separation of file and root element
 *     Systerel - used ElementDescRegistry
 *     Systerel - added dispose listener to row composite
 *     Systerel - introduced read only elements
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import static org.eventb.internal.ui.EventBUtils.isReadOnly;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
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
import org.eventb.internal.ui.eventbeditor.editpage.IEditComposite;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;

/**
 * @author htson
 *         <p>
 *         Abstract implementation for edit composite which is used to edit an
 *         attribute of a particular element.
 *         </p>
 */
public abstract class AbstractEditComposite implements IEditComposite {

	protected static final Color WHITE = EventBSharedColor
			.getSystemColor(SWT.COLOR_WHITE);
	protected static final Color BLACK = EventBSharedColor
			.getSystemColor(SWT.COLOR_BLACK);
	protected static final Color RED = EventBSharedColor
			.getSystemColor(SWT.COLOR_RED);
	protected static final Color YELLOW = EventBSharedColor
			.getSystemColor(SWT.COLOR_YELLOW);

	// The Event-B Editor.
	IEventBEditor<?> fEditor;
	
	// The attributed element to edit.
	protected IInternalElement element;

	// The UI information on how this attribute is displayed and/or edited.
	final protected AttributeDesc attrDesc;
	final protected IAttributeManipulation manipulation;
	
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
	 * create the widgets themselves.
	 * 
	 * @param attrDesc
	 *            a UI description for an attribute.
	 */
	public AbstractEditComposite(AttributeDesc attrDesc) {
		this.attrDesc = attrDesc;
		this.manipulation = attrDesc.getManipulation();
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
		setReadOnly(isReadOnly(element)); 
		internalPack();
	}

	public void setElement(IInternalElement element) {
		this.element = element;
	}

	public void createComposite(IEventBEditor<?> editor, FormToolkit tk,
			Composite parent) {
		this.fEditor = editor;
		this.toolkit = tk;
		
		String prefix = attrDesc.getPrefix();
		if (prefix == null)
			prefix = "";
		prefixLabel = toolkit.createLabel(parent, prefix);
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.CENTER;
		if (prefix == "")
			gridData.widthHint = 0;
		
		prefixLabel.setLayoutData(gridData);
		if (EventBEditorUtils.DEBUG)
			prefixLabel.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_CYAN));

		composite = toolkit.createComposite(parent);
		
		gridData = new GridData(SWT.FILL, SWT.CENTER, attrDesc.isHorizontalExpand(), false);
		gridData.minimumWidth = 200;
		composite.setLayoutData(gridData);
		composite.setLayout(new FillLayout());
		
		initialise(true);
		String postfix = attrDesc.getSuffix();
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
		setReadOnly(isReadOnly(element)); 
		EventBEditorUtils.changeFocusWhenDispose(composite, form);
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

	public IAttributeType getAttributeType() {
		return attrDesc.getAttributeType();
	}

	public void refresh(Set<IAttributeType> set) {
		if (set.size() == 0
				|| set.contains(attrDesc.getAttributeType())) {
			refresh(true);
		}
	}

	/**
	 * Sets the read only state of this composite.
	 * <p>
	 * After it has been called with <code>true</code> argument, the user is no
	 * more able to edit the composite contents.
	 * </p>
	 * <p>
	 * Conversely, after it has been called with <code>false</code> argument,
	 * the user is able to edit the composite contents.
	 * </p>
	 * 
	 * @param readOnly
	 *            <code>true</code> to make the composite read only,
	 *            <code>false</code> otherwise
	 */
	public abstract void setReadOnly(boolean readOnly);

}
