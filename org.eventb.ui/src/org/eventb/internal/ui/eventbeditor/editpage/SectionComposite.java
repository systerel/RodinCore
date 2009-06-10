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
 *     Systerel - optimized tree traversal
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

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

	// The next two variables maintain a link to the elements embedded in this
	// section. The list gives the order of the sections, while the map allows
	// direct access to the composite of a Rodin element.
	LinkedList<IElementComposite> elementComps;
	private Map<IRodinElement, IElementComposite> mapComps;

	// After hyperlink composite
	AbstractHyperlinkComposite afterHyperlinkComposite;

	int displayedSeverity = IMarker.SEVERITY_INFO;

	public SectionComposite(final EditPage page, final FormToolkit toolkit,
			final ScrolledForm form, final Composite compParent,
			final IInternalElement parent, final IElementRelationship rel,
			final int level) {
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
			composite.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_DARK_CYAN));
		}

		final ElementDescRegistry registry = ElementDescRegistry.getInstance();

		final String prefix = registry.getPrefix(rel.getChildType());
		if (notVoid(prefix)) {
			createPrefixLabel(prefix);
		}

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
			elementComposite.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_GREEN));
		}

		afterHyperlinkComposite = new AfterHyperlinkComposite(page, parent, rel
				.getChildType(), toolkit, composite);

		final String suffix = registry.getChildrenSuffix(rel.getParentType(),
				rel.getChildType());

		if (notVoid(suffix)) {
			createPostfixLabel(suffix);
		}

		setExpand(false);
	}

	private boolean notVoid(final String prefix) {
		return prefix != null && prefix.length() != 0;
	}

	private void createPostfixLabel(final String str) {
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

	private void createPrefixLabel(final String str) {
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
		folding.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_COLLAPSED));
		folding.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(final HyperlinkEvent e) {
				folding();
			}

		});

		folding.addMouseTrackListener(new MouseTrackListener() {

			public void mouseEnter(final MouseEvent e) {
				if (isExpanded()) {
					folding.setImage(EventBImage
							.getImage(IEventBSharedImages.IMG_EXPANDED_HOVER));
				} else {
					folding.setImage(EventBImage
							.getImage(IEventBSharedImages.IMG_COLLAPSED_HOVER));
				}
			}

			public void mouseExit(final MouseEvent e) {
				updateExpandStatus();
			}

			public void mouseHover(final MouseEvent e) {
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
		refreshPrefixMarker();
	}

	protected boolean isExpanded() {
		return isExpanded;
	}

	protected void folding() {
		setExpand(!isExpanded);
	}

	public void setExpand(final boolean isExpanded) {
		long beforeTime = System.currentTimeMillis();
		form.setRedraw(false);
		this.isExpanded = isExpanded;
		if (isExpanded) {
			createElementComposites();
		} else {
			beforeHyperlinkComposite.setHeightHint(0);
			GridData gridData = (GridData) elementComposite.getLayoutData();
			gridData.heightHint = 0;
			afterHyperlinkComposite.setHeightHint(0);
		}
		updateExpandStatus();
		refreshPrefixMarker();
		form.reflow(true);
		form.setRedraw(true);
		long afterTime = System.currentTimeMillis();
		if (EventBEditorUtils.DEBUG) {
			EventBEditorUtils.debug("Duration: " + (afterTime - beforeTime)
					+ " ms");
		}

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
			IRodinElement[] children = parent.getChildrenOfType(rel
					.getChildType());
			if (!beforeHyperlinkComposite.isInitialised()) {
				beforeHyperlinkComposite.createHyperlinks(toolkit, level);
			}

			if (children.length != 0) {
				beforeHyperlinkComposite.setHeightHint(SWT.DEFAULT);
			} else {
				beforeHyperlinkComposite.setHeightHint(0);
			}

			if (elementComps == null) {
				elementComps = new LinkedList<IElementComposite>();
				mapComps = new HashMap<IRodinElement, IElementComposite>();
				for (IRodinElement child : children) {
					final IElementComposite comp = new ElementComposite(page,
							toolkit, form, elementComposite, child, level);
					elementComps.add(comp);
					mapComps.put(child, comp);
				}
			}
			GridData gridData = (GridData) elementComposite.getLayoutData();
			if (elementComps.size() != 0) {
				gridData.heightHint = SWT.DEFAULT;
			} else {
				gridData.heightHint = 0;
			}

		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!afterHyperlinkComposite.isInitialised()) {
			afterHyperlinkComposite.createHyperlinks(toolkit, level);
		}

		afterHyperlinkComposite.setHeightHint(SWT.DEFAULT);
	}

	public void dispose() {
		composite.dispose();
	}

	public void refresh(final IRodinElement element) {
		final IElementComposite comp = getCompositeTowards(element);
		if (comp != null) {
			comp.refresh(element);
		}
	}

	public IElementType<?> getElementType() {
		return rel.getChildType();
	}

	public void elementRemoved(final IRodinElement element) {
		if (elementComps == null) {
			return;
		}

		final IElementComposite comp = getCompositeTowards(element);
		if (comp != null) {
			final IRodinElement rElement = comp.getElement();
			if (rElement.equals(element)) {
				comp.dispose();
				mapComps.remove(element);
				elementComps.remove(comp);
			} else {
				comp.elementRemoved(element);
			}
		}

		if (elementComps.size() == 0) {
			GridData gridData = (GridData) elementComposite.getLayoutData();
			gridData.heightHint = 0;
		}
		updateHyperlink();
		form.reflow(true);
	}

	private void updateHyperlink() {
		if (elementComps == null || elementComps.size() == 0 || !isExpanded) {
			beforeHyperlinkComposite.setHeightHint(0);
		} else {
			beforeHyperlinkComposite.setHeightHint(SWT.DEFAULT);
		}
	}

	public void elementAdded(final IRodinElement element) {
		if (elementComps == null) {
			return;
		}

		if (element.getParent().equals(parent)
				&& element.getElementType() == rel.getChildType()) {
			// Create a new Element composite added to the end of the list
			final IElementComposite comp = new ElementComposite(page, toolkit,
					form, elementComposite, element, level);
			elementComps.add(comp);
			mapComps.put(element, comp);
			GridData gridData = (GridData) elementComposite.getLayoutData();
			gridData.heightHint = SWT.DEFAULT;
			updateHyperlink();
			form.reflow(true);
		} else {
			final IElementComposite comp = getCompositeTowards(element);
			if (comp != null) {
				comp.elementAdded(element);
			}
		}
		updateHyperlink();
	}

	public void childrenChanged(final IRodinElement element,
			final IElementType<?> childrenType) {
		if (elementComps == null) {
			return;
		}

		if (parent.equals(element) && childrenType == rel.getChildType()) {
			// Sorting the section
			try {
				Rectangle bounds = elementComposite.getBounds();
				IRodinElement[] children = parent.getChildrenOfType(rel
						.getChildType());
				assert children.length == elementComps.size();
				for (int i = 0; i < children.length; ++i) {
					IRodinElement child = children[i];
					// find elementComp corresponding to child
					int index = indexOf(child);
					if (index == i) {
						continue;
					}
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
						Composite prevComp = prevElementComposite
								.getComposite();
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
		} else {
			final IElementComposite comp = getCompositeTowards(element);
			if (comp != null) {
				comp.childrenChanged(element, childrenType);
			}
		}
	}

	private int indexOf(final IRodinElement child) {
		int i = 0;
		for (IElementComposite elementComp : elementComps) {
			if (elementComp.getElement().equals(child)) {
				return i;
			}
			++i;
		}
		return -1;
	}

	public boolean select(final IRodinElement element, final boolean selected) {
		final IRodinElement child = getChildTowards(element);
		if (child == null) {
			return false;
		}

		if (selected) {
			setExpand(true);
		}
		final IElementComposite comp = getComposite(child);
		if (comp == null) {
			return false;
		}
		return comp.select(element, selected);
	}

	public void recursiveExpand(final IRodinElement element) {
		if (parent.equals(element) || element.isAncestorOf(parent)) {
			setExpand(true);
			for (IElementComposite elementComp : elementComps) {
				elementComp.recursiveExpand(element);
			}
		} else {
			final IRodinElement child = getChildTowards(element);
			if (child == null) {
				return;
			}
			setExpand(true);
			final IElementComposite comp = getComposite(child);
			if (comp != null) {
				comp.recursiveExpand(element);
			}
		}
	}

	public void edit(final IInternalElement element,
			final IAttributeType attributeType, final int charStart,
			final int charEnd) {
		final IRodinElement child = getChildTowards(element);
		if (child == null) {
			return;
		}
		if (!isExpanded()) {
			setExpand(true);
		}

		final IElementComposite comp = getComposite(child);
		if (comp != null) {
			comp.edit(element, attributeType, charStart, charEnd);
		}
	}

	public void refresh(final IRodinElement element,
			final Set<IAttributeType> set) {
		final IElementComposite comp = getCompositeTowards(element);
		if (comp != null) {
			comp.refresh(element, set);
		}
	}

	// refresh only if necessary
	public void refreshPrefixMarker() {
		Color RED = EventBSharedColor.getSystemColor(SWT.COLOR_RED);
		Color YELLOW = EventBSharedColor.getSystemColor(SWT.COLOR_YELLOW);
		Color WHITE = EventBSharedColor.getSystemColor(SWT.COLOR_WHITE);
		Color BLACK = EventBSharedColor.getSystemColor(SWT.COLOR_BLACK);
		try {
			final int severity = MarkerUIRegistry.getDefault()
					.getMaxMarkerSeverity(parent, rel.getChildType());

			if (severity == displayedSeverity) {
				return;
			}
			displayedSeverity = severity;

			if (severity == IMarker.SEVERITY_ERROR) {
				prefixFormText.setBackground(RED);
				prefixFormText.setForeground(YELLOW);
			} else if (severity == IMarker.SEVERITY_WARNING) {
				prefixFormText.setBackground(YELLOW);
				prefixFormText.setForeground(RED);
			} else {
				prefixFormText.setBackground(WHITE);
				prefixFormText.setForeground(BLACK);
			}
		} catch (CoreException e) {
			return;
		}
	}

	public void recursiveExpand() {
		setExpand(true);
		if (elementComps == null) {
			return;
		}

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

	protected IElementComposite getComposite(final IRodinElement element) {
		if (element == null || mapComps == null) {
			return null;
		}
		return mapComps.get(element);
	}

	protected IElementComposite getCompositeTowards(final IRodinElement element) {
		final IRodinElement child = getChildTowards(element);
		return getComposite(child);
	}

	protected IRodinElement getChildTowards(final IRodinElement element) {
		return EventBEditorUtils.getChildTowards(parent, element);
	}
}
