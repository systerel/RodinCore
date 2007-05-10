package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.eventbeditor.IEventBEditor;
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
	
	Composite afterComposite;

	Composite afterHyperlinkComposite;
	
	ImageHyperlink addAfterHyperlink;

	Composite beforeComposite;

	Composite beforeHyperlinkComposite;

	ImageHyperlink addBeforeHyperlink;

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
		isExpanded = true;
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
						.getImage(IEventBSharedImages.IMG_EXPANDED));
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
				if (isExpanded()) {
					folding.setImage(EventBImage
							.getImage(IEventBSharedImages.IMG_EXPANDED));
				} else {
					folding.setImage(EventBImage
							.getImage(IEventBSharedImages.IMG_COLLAPSED));
				}
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
		this.isExpanded = isExpanded;
		if (isExpanded) {
			GridData gridData = (GridData) beforeComposite.getLayoutData();
			gridData.heightHint = SWT.DEFAULT;
			gridData = (GridData) afterComposite.getLayoutData();
			gridData.heightHint = SWT.DEFAULT;
			gridData = (GridData) elementComposite.getLayoutData();
			gridData.heightHint = SWT.DEFAULT;
			createElementComposites();
		}
		else {
			for (IElementComposite elementComp : elementComps) {
				elementComp.dispose();
			}
			elementComps.clear();
			if (beforeHyperlinkComposite != null) 
				beforeHyperlinkComposite.dispose();
			afterHyperlinkComposite.dispose();
			GridData gridData = (GridData) beforeComposite.getLayoutData();
			gridData.heightHint = 0;
			gridData = (GridData) afterComposite.getLayoutData();
			gridData.heightHint = 0;
			gridData = (GridData) elementComposite.getLayoutData();
			gridData.heightHint = 0;
		}

		form.getBody().pack(true);
		form.reflow(true);
	}

	public void createContents() {
		composite = toolkit.createComposite(compParent);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout());
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

		beforeComposite = toolkit.createComposite(composite);
		beforeComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		beforeComposite.setLayout(new GridLayout());
		if (EventBEditorUtils.DEBUG) {
			beforeComposite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_DARK_GRAY));
		}
		
		elementComposite = toolkit.createComposite(composite);
		elementComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		elementComposite.setLayout(new GridLayout());
		if (EventBEditorUtils.DEBUG) {
			elementComposite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_GREEN));
		}

		afterComposite = toolkit.createComposite(composite);
		afterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		afterComposite.setLayout(new GridLayout());
		if (EventBEditorUtils.DEBUG) {
			afterComposite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_DARK_GRAY));
		}

		createElementComposites();

		String postfix = editSectionRegistry.getPostfix(
				parent.getElementType(), type);

		if (postfix != null)
			createPostfixLabel(postfix);

		return;
	}

	private void createElementComposites() {

		try {
			IRodinElement[] children = parent.getChildrenOfType(type);
			if (children.length != 0)
				createBeforeHyperlinks();
			elementComps = new LinkedList<IElementComposite>();
			for (IRodinElement child : children) {
				elementComps.add(new ElementComposite(page, toolkit, form,
						elementComposite, child, level));
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createAfterHyperlinks();
	}

	private void createBeforeHyperlinks() {
		beforeHyperlinkComposite = toolkit.createComposite(beforeComposite);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		beforeHyperlinkComposite.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		beforeHyperlinkComposite.setLayout(gridLayout);

		Composite tmp = toolkit.createComposite(beforeHyperlinkComposite);
		gridData = new GridData();
		// -12 for alignment with the icon of children elements
		gridData.widthHint = (level + 1) * 40 - 12; 
		gridData.heightHint = 0;
		tmp.setLayoutData(gridData);

		addBeforeHyperlink = toolkit.createImageHyperlink(
				beforeHyperlinkComposite, SWT.TOP);
		addBeforeHyperlink.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_ADD));
		addBeforeHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				IEventBEditor editor = (IEventBEditor) page.getEditor();
				try {
					IInternalElement[] children = parent.getChildrenOfType(type);
					assert (children.length != 0);
					IInternalElement first = children[0];
					
					String newName = UIUtils.getFreeChildName(editor, parent,
							type);
					IInternalElement newElement = parent
							.getInternalElement(
									(IInternalElementType<? extends IRodinElement>) type,
									newName);
					newElement.create(first, new NullProgressMonitor());
				} catch (RodinDBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		addBeforeHyperlink.setLayoutData(new GridData());
	}

	private void createAfterHyperlinks() {
		afterHyperlinkComposite = toolkit.createComposite(afterComposite);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		afterHyperlinkComposite.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		afterHyperlinkComposite.setLayout(gridLayout);

		Composite tmp = toolkit.createComposite(afterHyperlinkComposite);
		gridData = new GridData();
		// -12 for alignment with the icon of children elements
		gridData.widthHint = (level + 1) * 40 - 12; 
		gridData.heightHint = 0;
		tmp.setLayoutData(gridData);

		addAfterHyperlink = toolkit.createImageHyperlink(
				afterHyperlinkComposite, SWT.TOP);
		addAfterHyperlink.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_ADD));
		addAfterHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				IEventBEditor editor = (IEventBEditor) page.getEditor();
				try {
					String newName = UIUtils.getFreeChildName(editor, parent,
							type);
					IInternalElement newElement = parent
							.getInternalElement(
									(IInternalElementType<? extends IRodinElement>) type,
									newName);
					newElement.create(null, new NullProgressMonitor());
				} catch (RodinDBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		addAfterHyperlink.setLayoutData(new GridData());
	}

	public void dispose() {
		composite.dispose();
	}

	public void refresh(IRodinElement element) {
		for (IElementComposite elementComp : elementComps) {
			elementComp.refresh(element);
		}
	}

	public IElementType getElementType() {
		return type;
	}

	public void elementRemoved(IRodinElement element) {
		Collection<IElementComposite> toBeRemoved = new ArrayList<IElementComposite>();
		for (IElementComposite elementComp : elementComps) {
			IRodinElement rElement = elementComp.getElement();

			if (rElement.equals(element)) {
				elementComp.dispose();
				toBeRemoved.add(elementComp);
			} else
				elementComp.elementRemoved(element);
		}

		elementComps.removeAll(toBeRemoved);
		updateHyperlink();
	}

	private void updateHyperlink() {
		if (elementComps.size() == 0 && beforeHyperlinkComposite != null) {
			beforeHyperlinkComposite.dispose();
			GridData gridData = (GridData) beforeComposite.getLayoutData();
			gridData.heightHint = 0;
			beforeHyperlinkComposite = null;
			return;
		}
		if (elementComps.size() != 0 && beforeHyperlinkComposite == null) {
			GridData gridData = (GridData) beforeComposite.getLayoutData();
			gridData.heightHint = SWT.DEFAULT;
			createBeforeHyperlinks();
		}
	}

	public void elementAdded(IRodinElement element) {
		if (element.getParent().equals(parent)
				&& element.getElementType() == type) {
			// Create a new Element composite added to the end of the list
			elementComps.add(new ElementComposite(page, toolkit, form,
					elementComposite, element, level));
		} else {
			for (IElementComposite elementComp : elementComps) {
				elementComp.elementAdded(element);
			}
		}
		updateHyperlink();
	}

	public void childrenChanged(IRodinElement element, IElementType childrenType) {
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

		for (IElementComposite elementComp : elementComps) {
			elementComp.childrenChanged(element, childrenType);
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
		if (parent.isAncestorOf(element)) {
			for (IElementComposite elementComp : elementComps) {
				elementComp.select(element, select);
			}
		}
	}

}
