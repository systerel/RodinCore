package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
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
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class SectionComposite implements ISectionComposite {

	FormToolkit toolkit;

	ScrolledForm form;

	Composite compParent;

	IInternalParent parent;

	IInternalElementType<? extends IInternalElement> type;

	int level;

	Composite composite;

	EditPage page;

	ImageHyperlink folding;

	LinkedList<IElementComposite> elementComps;

	Composite elementComposite;
	
	AbstractHyperlinkComposite beforeHyperlinkComposite;

	AbstractHyperlinkComposite afterHyperlinkComposite;

	boolean isExpanded;

	public SectionComposite(EditPage page, FormToolkit toolkit,
			ScrolledForm form, Composite compParent, IInternalParent parent,
			IInternalElementType<? extends IInternalElement> type, int level) {
		this.page = page;
		this.toolkit = toolkit;
		this.form = form;
		this.compParent = compParent;
		this.parent = parent;
		this.type = type;
		this.level = level;
		createContents();
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

		FormText widget = toolkit.createFormText(comp, true);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		widget.setLayoutData(gd);
		new EventBFormText(widget);
		int space = -20;
		String text = "<form><li style=\"text\" bindent = \"" + space
				+ "\"><b>" + str + "</b></li></form>";
		widget.setText(text, true, true);
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

	public void createContents() {
		composite = toolkit.createComposite(compParent);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_DARK_CYAN));
		}
		
		EditSectionRegistry editSectionRegistry = EditSectionRegistry
				.getDefault();

		String prefix = editSectionRegistry.getPrefix(parent.getElementType(),
				type);
		if (prefix != null)
			createPrefixLabel(prefix);

		gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		beforeHyperlinkComposite = new BeforeHyperlinkComposite(page, parent,
				type, toolkit, composite);
		
		gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		elementComposite = toolkit.createComposite(composite);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		elementComposite.setLayoutData(gridData);
		elementComposite.setLayout(gridLayout);
		if (EventBEditorUtils.DEBUG) {
			elementComposite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_GREEN));
		}
		
		afterHyperlinkComposite = new AfterHyperlinkComposite(page, parent, type,
				toolkit, composite);

		String postfix = editSectionRegistry.getPostfix(
				parent.getElementType(), type);

		if (postfix != null)
			createPostfixLabel(postfix);

		setExpand(false);
		return;
	}

	private void createElementComposites() {

		try {
			IRodinElement[] children = parent.getChildrenOfType(type);
			if (!beforeHyperlinkComposite.isInitialised())
				beforeHyperlinkComposite.createHyperlinks(toolkit, level);
			
			if (children.length != 0)
				beforeHyperlinkComposite.setHeightHint(SWT.DEFAULT);
			else
				beforeHyperlinkComposite.setHeightHint(0);
			
			if (elementComps == null) {
				elementComps = new LinkedList<IElementComposite>();
				for (IRodinElement child : children) {
					elementComps.add(new ElementComposite(page, toolkit, form,
							elementComposite, child, level));
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

	public IElementType getElementType() {
		return type;
	}

	public void elementRemoved(IRodinElement element) {
		Collection<IElementComposite> toBeRemoved = new ArrayList<IElementComposite>();
		if (elementComps != null) {
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
				&& element.getElementType() == type) {
			if (elementComps != null) {
				// Create a new Element composite added to the end of the list
				elementComps.add(new ElementComposite(page, toolkit, form,
						elementComposite, element, level));
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

	public void childrenChanged(IRodinElement element, IElementType childrenType) {
		if (elementComps == null)
			return;

		if (parent.equals(element) && childrenType == type) {
			// Sorting the section
			try {
				Rectangle bounds = elementComposite.getBounds();
				IRodinElement[] children = parent.getChildrenOfType(type);
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

	public void select(IRodinElement element, boolean select) {
		if (parent.isAncestorOf(element) && elementComps != null) {
			for (IElementComposite elementComp : elementComps) {
				elementComp.select(element, select);
			}
		}
	}

	public void recursiveExpand(IRodinElement element) {
		if (parent.equals(element)
				|| (parent.isAncestorOf(element) && type.equals(element
						.getElementType())) || element.isAncestorOf(parent)) {
			setExpand(true);
			for (IElementComposite elementComp : elementComps) {
				elementComp.recursiveExpand(element);
			}
		}
	}

}
