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
 *     Systerel - separation of file and root element
 *     Systerel - used ElementDescRegistry
 *     Systerel - used Map for section composite
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRelationship;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

public class ElementComposite implements IElementComposite {

	FormToolkit toolkit;

	ScrolledForm form;

	Composite compParent;

	IRodinElement rElement;

	EditRow row;

	Composite composite;

	Composite mainSectionComposite;

	ArrayList<ISectionComposite> sectionComps;

	Map<IElementType<?>, ISectionComposite> mapComps;

	EditPage page;

	int level;

	boolean isExpanded;

	public ElementComposite(EditPage page, FormToolkit toolkit,
			ScrolledForm form, Composite compParent, IRodinElement element,
			int level) {
		this.page = page;
		this.toolkit = toolkit;
		this.form = form;
		this.compParent = compParent;
		this.rElement = element;
		this.level = level;
		createContents();
	}

	private void createContents() {
		composite = toolkit.createComposite(compParent);
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_GRAY));
		}
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		composite.setLayout(gridLayout);

		row = new EditRow(this, form, toolkit);
		row.createContents((IEventBEditor<?>) page.getEditor(), toolkit,
				composite, null, level);

		mainSectionComposite = toolkit.createComposite(composite);
		mainSectionComposite.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		mainSectionComposite.setLayout(gridLayout);

		setExpand(false);
	}

	public void folding() {
		setExpand(!isExpanded);
	}

	public void setExpand(boolean isExpanded) {
		long beforeTime = 0;
		if (EventBEditorUtils.DEBUG)
			beforeTime = System.currentTimeMillis();
		form.setRedraw(false);
		this.isExpanded = isExpanded;
		if (isExpanded) {
			if (sectionComps == null) {
				createSectionComposites();
				recursivelyExpandSectionComposites();
			}
			GridData gridData = (GridData) mainSectionComposite.getLayoutData();
			if (sectionComps.size() == 0) {
				gridData.heightHint = 0;
			} else {
				gridData.heightHint = SWT.DEFAULT;
			}
		} else {
			GridData gridData = (GridData) mainSectionComposite.getLayoutData();
			gridData.heightHint = 0;
		}
		row.updateExpandStatus();
		form.reflow(true);
		form.setRedraw(true);
		if (EventBEditorUtils.DEBUG) {
			long afterTime = System.currentTimeMillis();
			EventBEditorUtils.debug("Duration: " + (afterTime - beforeTime)
					+ " ms");
		}
	}

	private void recursivelyExpandSectionComposites() {
		assert sectionComps != null;
		for (ISectionComposite sectionComp : sectionComps) {
			sectionComp.recursiveExpand();
		}
	}

	protected void createSectionComposites() {
		final ElementDescRegistry registry = ElementDescRegistry.getInstance();

		final IElementType<?>[] rels = registry.getChildTypes(rElement
				.getElementType());
		sectionComps = new ArrayList<ISectionComposite>(rels.length);
		mapComps = new HashMap<IElementType<?>, ISectionComposite>();
		 for (IElementType<?> type : rels) {
			// Create the section composite
			 final ElementDescRelationship rel = new ElementDescRelationship(
					rElement.getElementType(), (IInternalElementType<?>) type);
			 final ISectionComposite comp = new SectionComposite(page, toolkit,
					form, mainSectionComposite, (IInternalElement) rElement,
					rel, level + 1);
			sectionComps.add(comp);
			mapComps.put(type, comp);
		}
	}

	public EditPage getPage() {
		return page;
	}

	public void refresh(IRodinElement element) {
		if (!rElement.exists())
			return;
		if (rElement.equals(element)) {
			row.refresh();
			if (sectionComps == null)
				return;

			// Refresh sub section composite as well?
			final ElementDescRegistry registry = ElementDescRegistry
					.getInstance();
			final IElementType<?>[] rels = registry.getChildTypes(element
					.getElementType());

			boolean recreate = false;
			if (rels.length != sectionComps.size()) {
				recreate = true;
			} else {
				for (int i = 0; i < rels.length; ++i) {
					if (sectionComps.get(i).getElementType() != rels[i]) {
						recreate = true;
						break;
					}
				}
			}

			if (recreate) {
				for (ISectionComposite sectionComp : sectionComps) {
					sectionComp.dispose();
				}
				createSectionComposites();
			}
		} else if (rElement.isAncestorOf(element)) {
			if (sectionComps != null) {
				for (ISectionComposite sectionComp : sectionComps) {
					sectionComp.refresh(element);
				}
			}
		}
	}

	public void elementRemoved(IRodinElement element) {
		if (!rElement.exists())
			return;
		assert (!rElement.equals(element));
		if (rElement.isAncestorOf(element)) {
			if (sectionComps != null) {
				for (ISectionComposite sectionComp : sectionComps) {
					sectionComp.elementRemoved(element);
				}
			}
		}
	}

	public void elementAdded(IRodinElement element) {
		if (!rElement.exists())
			return;
		if (rElement.isAncestorOf(element)) {
			if (sectionComps == null)
				return;
			for (ISectionComposite sectionComp : sectionComps) {
				sectionComp.elementAdded(element);
			}
		}
	}

	public void dispose() {
		composite.dispose();
	}

	public IRodinElement getElement() {
		return rElement;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void childrenChanged(IRodinElement element,
			IElementType<?> childrenType) {
		if (!rElement.exists())
			return;

		// Only continue if the children section composites already exists
		if (sectionComps == null)
			return;
		
		if (rElement.equals(element)) {
			for (ISectionComposite sectionComp : sectionComps) {
				if (sectionComp.getElementType().equals(childrenType)) {
					sectionComp.childrenChanged(element, childrenType);
				}
			}

			row.updateLinks();
		}

		if (rElement.isAncestorOf(element)) {
			for (ISectionComposite sectionComp : sectionComps) {
				sectionComp.childrenChanged(element, childrenType);
			}
		}
	}

	public Composite getComposite() {
		return composite;
	}

	public boolean select(IRodinElement element, boolean selected) {
		if (!rElement.exists())
			return false;

		if (rElement.equals(element)) {
			row.setSelected(selected);
			return true;
		}

		if (rElement.isAncestorOf(element)) {
			if (selected)
				setExpand(true);
			for (ISectionComposite sectionComp : sectionComps) {
				if (sectionComp.select(element, selected))
					return true;
			}
		}
		
		return false;
	}

	public void recursiveExpand(IRodinElement element) {
		if (!rElement.exists())
			return;

		if (rElement.equals(element) || rElement.isAncestorOf(element)
				|| element.isAncestorOf(rElement)) {
			setExpand(true);
			for (ISectionComposite sectionComp : sectionComps) {
				sectionComp.recursiveExpand(element);
			}
		}

	}

	public void edit(IInternalElement element, IAttributeType attributeType,
			int charStart, int charEnd) {
		if (!rElement.exists())
			return;

		if (rElement.equals(element)) {
			row.edit(attributeType, charStart, charEnd);
		}

		if (rElement.isAncestorOf(element)) {
			if (!isExpanded())
				setExpand(true);
			assert sectionComps != null;
			for (ISectionComposite sectionComp : sectionComps) {
				sectionComp.edit(element, attributeType, charStart, charEnd);
			}
		}
		
	}

	public void refresh(IRodinElement element, Set<IAttributeType> set) {
		if (!rElement.exists())
			return;
		if (element.equals(rElement)) {
			row.refresh(set);
			if (sectionComps == null)
				return;

			// Refresh sub section composite as well?
			final ElementDescRegistry registry = ElementDescRegistry.getInstance();
			final IElementType<?>[] rels = registry.getChildTypes(element
					.getElementType());

			boolean recreate = false;
			if (rels.length != sectionComps.size()) {
				recreate = true;
			} else {
				for (int i = 0; i < rels.length; ++i) {
					if (sectionComps.get(i).getElementType() != rels[i]) {
						recreate = true;
						break;
					}
				}
			}

			if (recreate) {
				for (ISectionComposite sectionComp : sectionComps) {
					sectionComp.dispose();
				}
				createSectionComposites();
			}
		} else {
			row.updateLinks();
			if (rElement.isAncestorOf(element)) {
				if (sectionComps != null) {
					final IElementType<?> type = EventBEditorUtils
							.getChildTowards(rElement, element).getElementType();
					final ISectionComposite comp = mapComps.get(type);
					if (comp != null)
						comp.refresh(element, set);
				}
			}
		}
	}

}
