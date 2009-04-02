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
 *     Systerel - used Map for element composite
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementRelationship;
import org.eventb.internal.ui.markers.MarkerUIRegistry;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class SectionComposite implements ISectionComposite {

	// The Form Toolkit used to create different Widget
	FormToolkit toolkit;

	// The top level scrolled form
	ScrolledForm form;

	// The composite parent for the section
	Composite compParent;

	// The Rodin parent of this section
	IInternalElement parent;

	// The element relationship associated with this section composite
	IElementRelationship rel;

	// The level of this section composite: 0 is the top level
	int level;

	// The Edit Page
	EditPage page;

	// The main composite
	Composite composite;

	// The folding image hyperlink
	ImageHyperlink folding;

	// expanding status
	boolean isExpanded;
	
	FormText prefixFormText;
	
	// Before hyperlink composite
	AbstractHyperlinkComposite beforeHyperlinkComposite;

	// The element composite
	Composite elementComposite;
	
	// List of contained element composite
	LinkedList<IElementComposite> elementComps;

	private Map<IRodinElement, IElementComposite> mapComps;

	private Integer currentSeverity;
	
	private boolean doSeveralRefresh = false;
	
	// After hyperlink composite
	AbstractHyperlinkComposite afterHyperlinkComposite;

	int severity = IMarker.SEVERITY_INFO;
	
	public SectionComposite(EditPage page, FormToolkit toolkit,
			ScrolledForm form, Composite compParent, IInternalElement parent,
			IElementRelationship rel, int level) {
		this.page = page;
		this.toolkit = toolkit;
		this.form = form;
		this.compParent = compParent;
		this.parent = parent;
		this.rel = rel;
		this.level = level;
		createContents();
	}

	private void createContents() {
		composite = toolkit.createComposite(compParent);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_DARK_CYAN));
		}
		
		final ElementDescRegistry registry = ElementDescRegistry.getInstance();
		
		final String prefix = registry.getPrefix(rel.getChildType());
		if (prefix != null || prefix != "")
			createPrefixLabel(prefix);

		gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		beforeHyperlinkComposite = new BeforeHyperlinkComposite(page, parent,
				rel.getChildType(), toolkit, composite);
		
		gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		elementComposite = toolkit.createComposite(composite);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		elementComposite.setLayoutData(gridData);
		elementComposite.setLayout(gridLayout);
		if (EventBEditorUtils.DEBUG) {
			elementComposite.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_GREEN));
		}

		afterHyperlinkComposite = new AfterHyperlinkComposite(page, parent, rel
				.getChildType(), toolkit, composite);

		final String suffix = registry.getChildrenSuffix(rel.getParentType(),
				rel.getChildType());

		if (suffix != null && suffix != "")
			createPostfixLabel(suffix);

		setExpand(false);
		return;
	}

	private void createPostfixLabel(String str) {
		Composite comp = toolkit.createComposite(composite);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		comp.setLayout(gridLayout);

		Composite tmp = toolkit.createComposite(comp);
		GridData gridData = new GridData();
		gridData.heightHint = 0;
		gridData.widthHint = 40 * level;
		tmp.setLayoutData(gridData);

		FormText widget = toolkit.createFormText(comp, true);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		widget.setLayoutData(gd);
		new EventBFormText(widget);
		int space = -5;
		String text = "<form><li style=\"text\" bindent = \"" + space
				+ "\"><b>" + str + "</b></li></form>";
		widget.setText(text, true, true);
	}

	private void createPrefixLabel(String str) {
		Composite comp = toolkit.createComposite(composite);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		comp.setLayout(gridLayout);

		Composite tmp = toolkit.createComposite(comp);
		GridData gridData = new GridData();
		gridData.widthHint = 40 * level;
		gridData.heightHint = 0;
		tmp.setLayoutData(gridData);

		folding = toolkit.createImageHyperlink(comp, SWT.TOP);
		folding
				.setImage(EventBImage
						.getImage(IEventBSharedImages.IMG_COLLAPSED));
		folding.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				folding();
			}

		});

		folding.addMouseTrackListener(new MouseTrackListener() {

			public void mouseEnter(MouseEvent e) {
				if (isExpanded()) {
					folding.setImage(EventBImage
							.getImage(IEventBSharedImages.IMG_EXPANDED_HOVER));
				} else {
					folding.setImage(EventBImage
							.getImage(IEventBSharedImages.IMG_COLLAPSED_HOVER));
				}
			}

			public void mouseExit(MouseEvent e) {
				updateExpandStatus();
			}

			public void mouseHover(MouseEvent e) {
				// Do nothing
			}

		});

		prefixFormText = toolkit.createFormText(comp, true);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		prefixFormText.setLayoutData(gd);
		new EventBFormText(prefixFormText);
		int space = -20;
		String text = "<form><li style=\"text\" bindent = \"" + space
				+ "\"><b>" + str + "</b></li></form>";
		prefixFormText.setText(text, true, true);
		updatePrefixFormText();
	}

	protected boolean isExpanded() {
		return isExpanded;
	}

	protected void folding() {
		setExpand(!isExpanded);
	}

	public void setExpand(boolean isExpanded) {
		long beforeTime = System.currentTimeMillis();
		form.setRedraw(false);	
		this.isExpanded = isExpanded;
		if (isExpanded) {
			createElementComposites();
		}
		else {
			beforeHyperlinkComposite.setHeightHint(0);
			GridData gridData = (GridData) elementComposite.getLayoutData();
			gridData.heightHint = 0;
			afterHyperlinkComposite.setHeightHint(0);
		}
		updateExpandStatus();
		updatePrefixFormText();
		form.reflow(true);
		form.setRedraw(true);
		long afterTime = System.currentTimeMillis();
		if (EventBEditorUtils.DEBUG)
			EventBEditorUtils.debug("Duration: " + (afterTime - beforeTime)
					+ " ms");
		
	}

	void updateExpandStatus() {
		if (isExpanded()) {
			folding.setImage(EventBImage
					.getImage(IEventBSharedImages.IMG_EXPANDED));
		} else {
			folding.setImage(EventBImage
					.getImage(IEventBSharedImages.IMG_COLLAPSED));
		}
	}

	private void createElementComposites() {

		try {
			IRodinElement[] children = parent.getChildrenOfType(rel.getChildType());
			if (!beforeHyperlinkComposite.isInitialised())
				beforeHyperlinkComposite.createHyperlinks(toolkit, level);
			
			if (children.length != 0)
				beforeHyperlinkComposite.setHeightHint(SWT.DEFAULT);
			else
				beforeHyperlinkComposite.setHeightHint(0);
			
			if (elementComps == null) {
				elementComps = new LinkedList<IElementComposite>();
				mapComps = new HashMap<IRodinElement, IElementComposite>();
				for (IRodinElement child : children) {
					final IElementComposite comp = new ElementComposite(page, toolkit, form,
							elementComposite, child, level);
					elementComps.add(comp);
					mapComps.put(child, comp);
				}
			}
			GridData gridData = (GridData) elementComposite.getLayoutData();
			if (elementComps.size() != 0)
				gridData.heightHint = SWT.DEFAULT;
			else
				gridData.heightHint = 0;
			
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!afterHyperlinkComposite.isInitialised())
			afterHyperlinkComposite.createHyperlinks(toolkit, level);
		
		afterHyperlinkComposite.setHeightHint(SWT.DEFAULT);
	}

	public void dispose() {
		composite.dispose();
	}

	public void refresh(IRodinElement element) {
		if (elementComps != null)
			for (IElementComposite elementComp : elementComps) {
				elementComp.refresh(element);
			}
	}

	public IElementType<?> getElementType() {
		return rel.getChildType();
	}

	public void elementRemoved(IRodinElement element) {
		Collection<IElementComposite> toBeRemoved = new ArrayList<IElementComposite>();
		if (elementComps != null) {
			mapComps.remove(element);
			for (IElementComposite elementComp : elementComps) {
				IRodinElement rElement = elementComp.getElement();

				if (rElement.equals(element)) {
					elementComp.dispose();
					toBeRemoved.add(elementComp);
				} else
					elementComp.elementRemoved(element);
			}

			elementComps.removeAll(toBeRemoved);
		}
		if (elementComps != null && elementComps.size() == 0) {
			GridData gridData = (GridData) elementComposite.getLayoutData();
			gridData.heightHint = 0;			
		}
		updateHyperlink();
		form.reflow(true);
	}

	private void updateHyperlink() {
		if (elementComps == null || elementComps.size() == 0 || !isExpanded) {
			beforeHyperlinkComposite.setHeightHint(0);
		}
		else {
			beforeHyperlinkComposite.setHeightHint(SWT.DEFAULT);
		}
	}

	public void elementAdded(IRodinElement element) {
		if (element.getParent().equals(parent)
				&& element.getElementType() == rel.getChildType()) {
			if (elementComps != null) {
				// Create a new Element composite added to the end of the list
				final IElementComposite comp = new ElementComposite(page, toolkit, form,
						elementComposite, element, level);
				elementComps.add(comp);
				mapComps.put(element, comp);
				GridData gridData = (GridData) elementComposite.getLayoutData();
				gridData.heightHint = SWT.DEFAULT;
				updateHyperlink();
				form.reflow(true);
			}
		} else {
			if (elementComps != null) {
				for (IElementComposite elementComp : elementComps) {
					elementComp.elementAdded(element);
				}
			}
		}
		updateHyperlink();
	}

	public void childrenChanged(IRodinElement element, IElementType<?> childrenType) {
		if (elementComps == null)
			return;

		if (parent.equals(element) && childrenType == rel.getChildType()) {
			// Sorting the section
			try {
				Rectangle bounds = elementComposite.getBounds();
				IRodinElement[] children = parent.getChildrenOfType(rel.getChildType());
				assert children.length == elementComps.size();
				for (int i = 0; i < children.length; ++i) {
					IRodinElement child = children[i];
					// find elementComp corresponding to child
					int index = indexOf(child);
					if (index == i)
						continue;
					IElementComposite elementComp = elementComps.get(index);
					assert (elementComp != null);

					elementComps.remove(elementComp);
					elementComps.add(i, elementComp);

					Composite comp = elementComp.getComposite();
					if (i == 0) {
						comp.moveAbove(null);
					} else {
						IElementComposite prevElementComposite = elementComps
								.get(i - 1);
						Composite prevComp = prevElementComposite.getComposite();
						comp.moveBelow(prevComp);
						comp.redraw();
						comp.pack();
						prevComp.redraw();
						prevComp.pack();
					}
				}
				elementComposite.layout();
				elementComposite.setBounds(bounds);
			} catch (RodinDBException e) {
				e.printStackTrace();
			}

			return;
		}

		if (elementComps != null) {
			for (IElementComposite elementComp : elementComps) {
				elementComp.childrenChanged(element, childrenType);
			}
		}
	}

	private int indexOf(IRodinElement child) {
		int i = 0;
		for (IElementComposite elementComp : elementComps) {
			if (elementComp.getElement().equals(child))
				return i;
			++i;
		}
		return -1;
	}

	public boolean select(IRodinElement element, boolean selected) {
		if (contain(element)) {
			if (selected)
				setExpand(true);
			if (elementComps != null)
				for (IElementComposite elementComp : elementComps) {
					if (elementComp.select(element, selected))
						return true;
				}
		}
		return false;
	}

	private boolean contain(IRodinElement element) {
		try {
			IRodinElement[] children = parent.getChildrenOfType(rel
					.getChildType());
			for (IRodinElement child : children) {
				if (child.equals(element) || child.isAncestorOf(element))
					return true;
			}
		} catch (RodinDBException e) {
			// Do nothing, return false
			return false;
		}
		return false;
	}

	public void recursiveExpand(IRodinElement element) {
		if (parent.equals(element)
				|| (parent.isAncestorOf(element) && rel.equals(element
						.getElementType())) || element.isAncestorOf(parent)) {
			setExpand(true);
			for (IElementComposite elementComp : elementComps) {
				elementComp.recursiveExpand(element);
			}
		}
	}

	public void edit(IInternalElement element, IAttributeType attributeType,
			int charStart, int charEnd) {
		if (parent.isAncestorOf(element)) {
			if (!isExpanded())
				setExpand(true);
			assert elementComps != null;
			for (IElementComposite elementComp : elementComps) {
				elementComp.edit(element, attributeType, charStart, charEnd);
			}
		}
	}

	public void refresh(IRodinElement element, Set<IAttributeType> set) {
		if (parent.equals(element) || parent.isAncestorOf(element)) {
			updatePrefixFormText();
			if (elementComps!= null) {
				// get the first ancestor after parent or element
				final IRodinElement ancestor = EventBEditorUtils
						.getChildTowards(parent, element);
				final IElementComposite comp = mapComps.get(ancestor);
				if (comp != null) {
					comp.refresh(element, set);
				}
			}
		}
	}

	private void updatePrefixFormText() {
		Color RED = EventBSharedColor.getSystemColor(SWT.COLOR_RED);
		Color YELLOW = EventBSharedColor.getSystemColor(SWT.COLOR_YELLOW);
		Color WHITE = EventBSharedColor.getSystemColor(SWT.COLOR_WHITE);
		Color BLACK = EventBSharedColor.getSystemColor(SWT.COLOR_BLACK);
		try {
			if(!doSeveralRefresh){
				computeCurrentSeverity();
			}

			if (currentSeverity == severity)
				return;
			severity = currentSeverity; 
			if (severity == IMarker.SEVERITY_ERROR) {
				prefixFormText.setBackground(RED);
				prefixFormText.setForeground(YELLOW);
			}
			else if (severity == IMarker.SEVERITY_WARNING) {
				prefixFormText.setBackground(YELLOW);
				prefixFormText.setForeground(RED);
			}
			else {
				prefixFormText.setBackground(WHITE);
				prefixFormText.setForeground(BLACK);
			} 
		} catch (CoreException e) {
			return;
		}
	}

	private void computeCurrentSeverity() throws CoreException {
		currentSeverity = MarkerUIRegistry.getDefault().getMaxMarkerSeverity(
				parent, rel.getChildType());
	}
	
	public void recursiveExpand() {
		setExpand(true);
		assert elementComps != null;
		for (IElementComposite elementComp : elementComps) {
			elementComp.setExpand(true);
		}
	}

	public void recursiveCollapse() {
		if (elementComps != null) {
			for (IElementComposite elementComp : elementComps) {
				elementComp.setExpand(false);
			}
		}
		setExpand(false);
	}
	
	public void disableMarkerRefresh() {
		doSeveralRefresh = false;
	}
	
	public void enableMarkerRefresh() {
		doSeveralRefresh = true;
		try {
			computeCurrentSeverity();
		} catch (CoreException e) {
			e.printStackTrace();
			doSeveralRefresh = false;
		}
	}
}
