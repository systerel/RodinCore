/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
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
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

public class EditRow {

	Composite composite;

	ScrolledForm form;

	IEditComposite[] editComposites;

	IElementComposite elementComp;

	ButtonComposite buttonComp;

	public EditRow(IElementComposite elementComp, ScrolledForm form,
			FormToolkit toolkit) {
		this.elementComp = elementComp;
		this.form = form;
	}

	public void createContents(IEventBEditor<?> editor, FormToolkit toolkit,
			Composite parent, Composite sibling, int level) {
		composite = toolkit.createComposite(parent);
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_RED));
		}
		if (sibling != null) {
			assert sibling.getParent() == parent;
			composite.moveAbove(sibling);
		}
		IRodinElement element = elementComp.getElement();
		IElementType<? extends IRodinElement> type = element.getElementType();
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final int numberOfAttributes = ElementDescRegistry.getInstance()
				.getElementDesc(type).getAttributeDescription().length;
		int numColumns = 1 + 3 * numberOfAttributes;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns + 1;
		composite.setLayout(gridLayout);

		createButtons(toolkit, level);

		final List<IEditComposite> list = new ArrayList<IEditComposite>();
		DescRegistryReader.createAttributeComposites(form, (IInternalElement) element, composite,
				editor, toolkit, list);

		editComposites = list.toArray(new IEditComposite[list.size()]);
		toolkit.paintBordersFor(composite);
	}

	private void createButtons(FormToolkit toolkit, int level) {
		buttonComp = new ButtonComposite(elementComp);
		buttonComp.createContents(toolkit, composite, level);
	}

	public void refresh() {
		IRodinElement element = elementComp.getElement();
		for (IEditComposite editComposite : editComposites) {
			editComposite.setElement((IInternalElement) element);
			editComposite.refresh(false);
		}
		buttonComp.updateLinks();
	}

	public boolean isSelected() {
		return buttonComp.isSelected();
	}

	public void dispose() {
		composite.dispose();
	}

	public Composite getComposite() {
		return composite;
	}

	public void setSelected(boolean select) {
		for (IEditComposite editComposite : editComposites) {
			editComposite.setSelected(select);
		}
		if (select) {
			composite.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_GRAY));
			FormToolkit.ensureVisible(composite);
		} else {
			if (EventBEditorUtils.DEBUG) {
				composite.setBackground(EventBSharedColor.getSystemColor(
						SWT.COLOR_RED));
			} else {
				composite.setBackground(EventBSharedColor.getSystemColor(
						SWT.COLOR_WHITE));
			}
		}
		buttonComp.setSelected(select);
	}

	public void updateLinks() {
		buttonComp.updateLinks();
	}

	public void updateExpandStatus() {
		buttonComp.updateExpandStatus();
	}

	public void edit(IAttributeType attributeType, int charStart, int charEnd) {
		if (attributeType == null) {
			this.setSelected(true);
			return;
		}
		for (IEditComposite editComposite : editComposites) {
			IAttributeType type = editComposite.getAttributeType();
			if (attributeType.equals(type)) {
				editComposite.edit(charStart, charEnd);
			}
		}

	}

	public void refresh(Set<IAttributeType> set) {
		IRodinElement element = elementComp.getElement();
		for (IEditComposite editComposite : editComposites) {
			editComposite.setElement((IInternalElement) element);
			editComposite.refresh(set);
		}
		buttonComp.updateLinks();		
	}

}
