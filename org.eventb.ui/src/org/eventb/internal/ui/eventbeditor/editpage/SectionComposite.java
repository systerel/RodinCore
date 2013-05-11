/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
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
 *     Systerel - fixed expanding
 *     Systerel - fixed Hyperlink.setImage() calls
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementRelationship;
import org.eventb.internal.ui.eventbeditor.handlers.CreateElementHandler;
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
	final FormToolkit toolkit;

	// The top level scrolled form
	final ScrolledForm form;

	// The composite parent for the section
	final Composite compParent;

	// The Rodin parent of this section
	final IInternalElement parent;

	// The element relationship associated with this section composite
	final IElementRelationship rel;

	// The level of this section composite: 0 is the top level
	final int level;

	// The Edit Page
	final EditPage page;

	// The main composite
	Composite composite;

	// The folding image hyperlink
	ImageHyperlink folding;

	// expanding status
	boolean isExpanded;

	FormText prefixFormText;


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

	/**
	 *  Create section's contents.
	 *  
	 *  XXX If the prefix label is empty, then the section's header (including folding and add controls) 
	 *  is not created.
	 * 
	 */
	private void createContents() {
		composite = toolkit.createComposite(compParent);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
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
			createHeader(prefix);
		}


		elementComposite = toolkit.createComposite(composite);
		elementComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		elementComposite.setLayout(gridLayout);
		
		if (EventBEditorUtils.DEBUG) {
			elementComposite.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_GREEN));
		}

		// XXX - to remove
		afterHyperlinkComposite = new AfterHyperlinkComposite(page, parent, rel
				.getChildType(), toolkit, composite);

		final String suffix = registry.getChildrenSuffix(rel.getParentType(),
				rel.getChildType());

		if (notVoid(suffix)) {
			createFooter(suffix);
		}

		setExpandNoReflow(false, false);
	}

	private boolean notVoid(final String prefix) {
		return prefix != null && prefix.length() != 0;
	}

	private void createFooter(final String str) {
		final Composite comp = toolkit.createComposite(composite);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		comp.setLayout(gridLayout);

		final Composite tmp = toolkit.createComposite(comp);
		final GridData gridData = new GridData();
		gridData.heightHint = 0;
		gridData.widthHint = EditPage.LEVEL_INDENT * level;
		tmp.setLayoutData(gridData);

		final FormText widget = toolkit.createFormText(comp, true);

		widget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new EventBFormText(widget);
		widget.setText(getPrefixFormText(str, -5), true, true);
	}

	/**
	 * Create the header of the section.
	 * <p>
	 * This consists of:
	 * <li> A spacer composite for indentation according to the level
	 * <li> A twistie control which controls section folding.
	 * <li> Text for the section prefix label (e.g. 'EVENTS')
	 * <li> An 'add' button - to add new elements into this section
	 * 
	 * @param str
	 * 
	 * 
	 */
	private void createHeader(final String str) {
		final Composite headerComp = toolkit.createComposite(composite);
		headerComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		headerComp.setLayout(gridLayout);

		final Composite spacer = toolkit.createComposite(headerComp);
		final GridData gridData = new GridData();
		gridData.widthHint = EditPage.LEVEL_INDENT * level;
		gridData.heightHint = 0;
		spacer.setLayoutData(gridData);

		folding = toolkit.createImageHyperlink(headerComp, SWT.TOP);
		EventBUtils.setHyperlinkImage(folding, EventBImage.getImage(IEventBSharedImages.IMG_COLLAPSED));
		
		folding.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(final HyperlinkEvent e) {
				folding();
			}
		});

		folding.addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseEnter(final MouseEvent e) {
				if (isExpanded()) {
					EventBUtils.setHyperlinkImage(folding, EventBImage
							.getImage(IEventBSharedImages.IMG_EXPANDED_HOVER));
				} else {
					EventBUtils.setHyperlinkImage(folding, EventBImage
							.getImage(IEventBSharedImages.IMG_COLLAPSED_HOVER));
				}
			}

			@Override
			public void mouseExit(final MouseEvent e) {
				updateExpandStatus();
			}

			@Override
			public void mouseHover(final MouseEvent e) {
				// Do nothing
			}

		});

		prefixFormText = toolkit.createFormText(headerComp, true);

		new EventBFormText(prefixFormText);
		prefixFormText.setText(getPrefixFormText(str, -20), true, true);
		refreshPrefixMarker();
		
		
		// Add button
		final ImageHyperlink addButton = toolkit.createImageHyperlink(headerComp, SWT.TOP);
		EventBUtils.setHyperlinkImage(addButton, EventBImage.getImage(IEventBSharedImages.IMG_ADD));
		
		addButton.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				
				// adding an element will show contents anyway 
				//  - force expanding
				if(!isExpanded) {
					setExpand(true, false);
				}
				
				final IInternalElement selectedEl = CreateElementHandler.insertionPointForSelection(
						new StructuredSelection(page.getCurrentSelection()));

				// insert at selected element, if it is in the current section
				final IInternalElement insertionPoint =
						selectedEl.getParent().equals(parent)
								&& selectedEl.getElementType().equals(rel.getChildType()) ?
						selectedEl
						: null;
				
				try {
					CreateElementHandler.doExecute(parent, rel.getChildType(), insertionPoint);
				} catch (RodinDBException e1) {
					UIUtils.log(e1, "o.e.i.u.eventbeditor.editpage");
				}
			}

		});
	}
	
	private static String getPrefixFormText(String sectionName, int spaceBefore) {
		return "<form><li style=\"text\" bindent = \"" + spaceBefore
		+ "\"><b>" + sectionName + "</b></li></form>";
	}

	protected boolean isExpanded() {
		return isExpanded;
	}

	protected void folding() {
		setExpand(!isExpanded, false);
	}

	@Override
	public void setExpand(final boolean isExpanded, boolean recursive) {
		
		long beforeTime=0;
		if (EventBEditorUtils.DEBUG) {
			beforeTime = System.currentTimeMillis();
		}
		
		form.setRedraw(false);
		setExpandNoReflow(isExpanded, recursive);
		form.reflow(true);
		form.setRedraw(true);
		
		if (EventBEditorUtils.DEBUG) {
			final long afterTime = System.currentTimeMillis();
			EventBEditorUtils.debug("Duration: " + (afterTime - beforeTime)
					+ " ms");
		}
	}
	
	@Override
	public void setExpandNoReflow(final boolean isExpanded, boolean recursive) {
		this.isExpanded = isExpanded;
		if (isExpanded) {
			createElementComposites();
			if (recursive) {
				recursiveSetExpand();
			}
		} else {
			final GridData gridData = (GridData) elementComposite.getLayoutData();
			gridData.heightHint = 0;
			afterHyperlinkComposite.setHeightHint(0);
			// collapse is always recursive
			recursiveSetExpand();
		}
		updateExpandStatus();
		refreshPrefixMarker();
	}

	void updateExpandStatus() {
		if (isExpanded()) {
			EventBUtils.setHyperlinkImage(folding, EventBImage
					.getImage(IEventBSharedImages.IMG_EXPANDED));
		} else {
			EventBUtils.setHyperlinkImage(folding, EventBImage
					.getImage(IEventBSharedImages.IMG_COLLAPSED));
		}
	}

	private void createElementComposites() {

		try {
			final IRodinElement[] children = parent.getChildrenOfType(rel
					.getChildType());
			if (!afterHyperlinkComposite.isInitialised()) {
				afterHyperlinkComposite.createContent(toolkit, level);
			}

			if (children.length == 0) {
				afterHyperlinkComposite.setHeightHint(SWT.DEFAULT);
			} else {
				afterHyperlinkComposite.setHeightHint(0);
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
			final GridData gridData = (GridData) elementComposite.getLayoutData();
			if (elementComps.size() != 0) {
				gridData.heightHint = SWT.DEFAULT;
			} else {
				gridData.heightHint = 0;
			}

		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void dispose() {
		composite.dispose();
	}

	@Override
	public void refresh(final IRodinElement element) {
		final IElementComposite comp = getCompositeTowards(element);
		if (comp != null) {
			comp.refresh(element);
		}
	}

	@Override
	public IElementType<?> getElementType() {
		return rel.getChildType();
	}

	@Override
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
			final GridData gridData = (GridData) elementComposite.getLayoutData();
			gridData.heightHint = 0;
		}
		updateHyperlink();
		form.reflow(true);
	}

	private void updateHyperlink() {
		
		final boolean show = 
				isExpanded && (elementComps == null || elementComps.size() == 0);
		
		afterHyperlinkComposite.setHeightHint(show ? SWT.DEFAULT : 0);
	}

	@Override
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
			final GridData gridData = (GridData) elementComposite.getLayoutData();
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

	@Override
	public void childrenChanged(final IRodinElement element,
			final IElementType<?> childrenType) {
		if (elementComps == null) {
			return;
		}

		if (parent.equals(element) && childrenType == rel.getChildType()) {
			// Sorting the section
			try {
				final Rectangle bounds = elementComposite.getBounds();
				final IRodinElement[] children = parent.getChildrenOfType(rel
						.getChildType());
				assert children.length == elementComps.size();
				for (int i = 0; i < children.length; ++i) {
					final IRodinElement child = children[i];
					// find elementComp corresponding to child
					int index = indexOf(child);
					if (index == i) {
						continue;
					}
					final IElementComposite elementComp = elementComps.get(index);
					assert (elementComp != null);

					elementComps.remove(elementComp);
					elementComps.add(i, elementComp);

					final Composite comp = elementComp.getComposite();
					if (i == 0) {
						comp.moveAbove(null);
					} else {
						final IElementComposite prevElementComposite = elementComps
								.get(i - 1);
						final Composite prevComp = prevElementComposite
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
		for (final IElementComposite elementComp : elementComps) {
			if (elementComp.getElement().equals(child)) {
				return i;
			}
			++i;
		}
		return -1;
	}

	@Override
	public boolean select(final IRodinElement element, final boolean selected) {
		final IRodinElement child = getChildTowards(element);
		if (child == null) {
			return false;
		}

		if (selected) {
			setExpand(true, false);
		}
		final IElementComposite comp = getComposite(child);
		if (comp == null) {
			return false;
		}
		return comp.select(element, selected);
	}

	@Override
	public void recursiveExpand(final IRodinElement element) {
		recursiveExpandNoReflow(element);
		form.reflow(true);
	}

	@Override
	public void recursiveExpandNoReflow(final IRodinElement element) {
		if (parent.equals(element)) {
			setExpandNoReflow(true, true);
			for (final IElementComposite elementComp : elementComps) {
				elementComp.recursiveExpand(element);
			}
		} else {
			final IRodinElement child = getChildTowards(element);
			if (child == null) {
				return;
			}
			setExpandNoReflow(true, false);
			final IElementComposite comp = getComposite(child);
			if (comp != null) {
				comp.recursiveExpand(element);
			}
		}
	}

	@Override
	public void edit(final IInternalElement element,
			final IAttributeType attributeType, final int charStart,
			final int charEnd) {
		final IRodinElement child = getChildTowards(element);
		if (child == null) {
			return;
		}
		if (!isExpanded()) {
			setExpand(true, false);
		}

		final IElementComposite comp = getComposite(child);
		if (comp != null) {
			comp.edit(element, attributeType, charStart, charEnd);
		}
	}

	@Override
	public void refresh(final IRodinElement element,
			final Set<IAttributeType> set) {
		final IElementComposite comp = getCompositeTowards(element);
		if (comp != null) {
			comp.refresh(element, set);
		}
	}

	// refresh only if necessary
	@Override
	public void refreshPrefixMarker() {
		final Color RED = EventBSharedColor.getSystemColor(SWT.COLOR_RED);
		final Color YELLOW = EventBSharedColor.getSystemColor(SWT.COLOR_YELLOW);
		final Color WHITE = EventBSharedColor.getSystemColor(SWT.COLOR_WHITE);
		final Color BLACK = EventBSharedColor.getSystemColor(SWT.COLOR_BLACK);
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

	private void recursiveSetExpand() {
		if (elementComps == null) {
			return;
		}

		for (IElementComposite elementComp : elementComps) {
			elementComp.setExpand(isExpanded, true);
		}
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
