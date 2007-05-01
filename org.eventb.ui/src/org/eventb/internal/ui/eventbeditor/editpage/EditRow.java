package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class EditRow {

	Composite composite;

	Composite compParent;

	Composite nextComposite;

	FormToolkit toolkit;

	ScrolledForm form;

	IRodinElement element;

	IEditComposite[] editComposites;

	ImageHyperlink selectHyperlink;

	ImageHyperlink foldingHyperlink;
	
	ImageHyperlink removeHyperlink; 
	
	IElementComposite elementComp;
	
	Composite buttonComp;
	
	int level;
	
	char keyHold;

	public EditRow(IElementComposite elementComp, ScrolledForm form,
			FormToolkit toolkit, Composite compParent, Composite nextComposite,
			IRodinElement element, int level) {
		assert (element != null);
		this.elementComp = elementComp;
		this.form = form;
		this.toolkit = toolkit;
		this.compParent = compParent;
		this.nextComposite = nextComposite;
		this.element = element;
		this.level = level;

		createContents();
	}

	private void createContents() {
		composite = toolkit.createComposite(compParent);
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_RED));
		}
		if (nextComposite != null) {
			assert nextComposite.getParent() == compParent;
			composite.moveAbove(nextComposite);
		}
		IElementType<? extends IRodinElement> type = element.getElementType();
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		EditSectionRegistry sectionRegistry = EditSectionRegistry.getDefault();
		int numColumns = 1 + 3 * sectionRegistry.getNumAttributes(type);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns + 1;
		composite.setLayout(gridLayout);

		createButtons(type);
		editComposites = sectionRegistry.createAttributeComposites(form,
				toolkit, composite, element);
		toolkit.paintBordersFor(composite);
	}

	public void createButtons(IElementType type) {
		buttonComp = toolkit.createComposite(composite);
		buttonComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		if (EventBEditorUtils.DEBUG) {
			buttonComp.setBackground(buttonComp.getDisplay().getSystemColor(
					SWT.COLOR_CYAN));
		}
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		buttonComp.setLayout(gridLayout);

		Composite tmp = toolkit.createComposite(buttonComp);
		GridData gridData = new GridData();
		gridData.widthHint = level * 40;
		gridData.heightHint = 0;
		tmp.setLayoutData(gridData);

		foldingHyperlink = toolkit.createImageHyperlink(buttonComp, SWT.TOP);
		foldingHyperlink
				.setImage(EventBImage
						.getImage(IEventBSharedImages.IMG_EXPANDED));
		foldingHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				elementComp.folding();
			}

		});

		foldingHyperlink.addMouseTrackListener(new MouseTrackListener() {

			public void mouseEnter(MouseEvent e) {
				if (elementComp.isExpanded()) {
					foldingHyperlink.setImage(EventBImage
							.getImage(IEventBSharedImages.IMG_EXPANDED_HOVER));
				} else {
					foldingHyperlink.setImage(EventBImage
							.getImage(IEventBSharedImages.IMG_COLLAPSED_HOVER));
				}
			}

			public void mouseExit(MouseEvent e) {
				if (elementComp.isExpanded()) {
					foldingHyperlink.setImage(EventBImage
							.getImage(IEventBSharedImages.IMG_EXPANDED));
				} else {
					foldingHyperlink.setImage(EventBImage
							.getImage(IEventBSharedImages.IMG_COLLAPSED));
				}
			}

			public void mouseHover(MouseEvent e) {
				// Do nothing
			}

		});

		selectHyperlink = toolkit.createImageHyperlink(buttonComp, SWT.TOP);
		selectHyperlink.setImage(EventBImage.getRodinImage(element));
		selectHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				elementComp.getPage().selectionChanges(element);
			}

		});

		removeHyperlink = toolkit.createImageHyperlink(buttonComp,
				SWT.TOP);
		removeHyperlink.setImage(EventBImage.getImage(IEventBSharedImages.IMG_REMOVE));
		removeHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				try {
					((IInternalElement) element).delete(true,
							new NullProgressMonitor());
				} catch (RodinDBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		updateLinks();
	}

	void updateLinks() {
		EditSectionRegistry editSectionRegistry = EditSectionRegistry
				.getDefault();
		if (editSectionRegistry.getChildrenTypes(element.getElementType()).length != 0) {
			foldingHyperlink.setVisible(true);
		} else
			foldingHyperlink.setVisible(false);
	}

	public void refresh() {
		for (IEditComposite editComposite : editComposites) {
			editComposite.setElement(element);
			editComposite.refresh();
		}
		updateLinks();
	}

	public boolean isSelected() {
		return selectHyperlink.getSelection();
	}

	public IRodinElement getElement() {
		return element;
	}

	public void setElement(IInternalElement element) {
		assert element.getElementType() == this.element.getElementType();
		this.element = element;
		refresh();
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
			composite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
			buttonComp.setBackground(buttonComp.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		}
		else {
			if (EventBEditorUtils.DEBUG) {
				composite.setBackground(composite.getDisplay().getSystemColor(
						SWT.COLOR_RED));
				buttonComp.setBackground(buttonComp.getDisplay().getSystemColor(
						SWT.COLOR_CYAN));
			}
			else {
				composite.setBackground(composite.getDisplay().getSystemColor(
						SWT.COLOR_WHITE));				
				buttonComp.setBackground(composite.getDisplay().getSystemColor(
						SWT.COLOR_WHITE));				
			}
		}
	}

}
