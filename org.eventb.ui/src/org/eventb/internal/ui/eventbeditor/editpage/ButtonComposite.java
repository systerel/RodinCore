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
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ButtonComposite {

	Composite composite;

	IElementComposite elementComp;

	ImageHyperlink selectHyperlink;

	ImageHyperlink foldingHyperlink;

	ImageHyperlink removeHyperlink;

	public ButtonComposite(IElementComposite elementComp) {
		this.elementComp = elementComp;
	}

	public void createContents(FormToolkit toolkit, Composite parent, int level) {
		composite = toolkit.createComposite(parent);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_CYAN));
		}

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		composite.setLayout(gridLayout);

		Composite tmp = toolkit.createComposite(composite);
		GridData gridData = new GridData();
		gridData.widthHint = level * 40 + 20;
		gridData.heightHint = 0;
		tmp.setLayoutData(gridData);

		foldingHyperlink = toolkit.createImageHyperlink(composite, SWT.TOP);
		foldingHyperlink.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_COLLAPSED));
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
				updateExpandStatus();
			}

			public void mouseHover(MouseEvent e) {
				// Do nothing
			}

		});

		final IRodinElement element = elementComp.getElement();
		selectHyperlink = toolkit.createImageHyperlink(composite, SWT.TOP);
		selectHyperlink.setImage(EventBImage.getRodinImage(element));
		selectHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				int stateMask = e.getStateMask();
				elementComp.getPage().selectionChanges(element, (stateMask & SWT.SHIFT) != 0);
			}

		});

		removeHyperlink = toolkit.createImageHyperlink(composite, SWT.TOP);
		removeHyperlink.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_REMOVE));
		removeHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				try {
					((IInternalElement) element).delete(true,
							new NullProgressMonitor());
				} catch (RodinDBException exception) {
					EventBUIExceptionHandler
							.handleDeleteElementException(exception);
				}
			}

		});

		updateLinks();
	}

	public void updateLinks() {
		EditSectionRegistry editSectionRegistry = EditSectionRegistry
				.getDefault();
		IRodinElement element = elementComp.getElement();

		if (editSectionRegistry.getChildrenTypes(element.getElementType()).length != 0) {
			foldingHyperlink.setVisible(true);
		} else
			foldingHyperlink.setVisible(false);
	}

	public boolean isSelected() {
		return selectHyperlink.getSelection();
	}

	public void setSelected(boolean select) {
		if (select) {
			composite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		} else {
			if (EventBEditorUtils.DEBUG) {
				composite.setBackground(composite.getDisplay().getSystemColor(
						SWT.COLOR_CYAN));
			} else {
				composite.setBackground(composite.getDisplay().getSystemColor(
						SWT.COLOR_WHITE));
			}
		}
	}

	public void updateExpandStatus() {
		if (elementComp.isExpanded()) {
			foldingHyperlink.setImage(EventBImage
					.getImage(IEventBSharedImages.IMG_EXPANDED));
		} else {
			foldingHyperlink.setImage(EventBImage
					.getImage(IEventBSharedImages.IMG_COLLAPSED));
		}
	}

}
