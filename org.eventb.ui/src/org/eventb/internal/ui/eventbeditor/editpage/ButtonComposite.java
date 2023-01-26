/*******************************************************************************
 * Copyright (c) 2007, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - added history support
 *     Systerel - used ElementDescRegistry
 *     Systerel - introduced read only elements
 *     Systerel - fixed Hyperlink.setImage() calls
 *     Systerel - used eclipse decorator mechanism
 *     Systerel - now remove label listener when disposed
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import static org.eventb.internal.ui.EventBUtils.setHyperlinkImage;
import static org.eventb.internal.ui.eventbeditor.EventBEditorUtils.checkAndShowReadOnly;

import java.util.Arrays;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

public class ButtonComposite implements ILabelProviderListener, DisposeListener {

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
		composite
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(EventBSharedColor.getSystemColor(
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
		gridData.widthHint = level * EditPage.LEVEL_INDENT + EditPage.LEVEL_INDENT/2;
		gridData.heightHint = 0;
		tmp.setLayoutData(gridData);

		foldingHyperlink = toolkit.createImageHyperlink(composite, SWT.TOP);
		foldingHyperlink.setImage(EventBImage.getImage(IEventBSharedImages.IMG_COLLAPSED));
		foldingHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				elementComp.folding();
			}

		});

		foldingHyperlink.addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseEnter(MouseEvent e) {
				if (elementComp.isExpanded()) {
					foldingHyperlink.setImage(EventBImage.getImage(IEventBSharedImages.IMG_EXPANDED_HOVER));
				} else {
					foldingHyperlink.setImage(EventBImage.getImage(IEventBSharedImages.IMG_COLLAPSED_HOVER));
				}
			}

			@Override
			public void mouseExit(MouseEvent e) {
				updateExpandStatus();
			}

			@Override
			public void mouseHover(MouseEvent e) {
				// Do nothing
			}

		});

		final IRodinElement element = elementComp.getElement();
		selectHyperlink = toolkit.createImageHyperlink(composite, SWT.TOP);
		updateSelectHyperlink();
		selectHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				int stateMask = e.getStateMask();
				elementComp.getPage().selectionChanges(element,
						(stateMask & SWT.SHIFT) != 0);
			}

		});
		selectHyperlink.setData(element);
		ImageHyperlinkMarkerToolTip handler = new ImageHyperlinkMarkerToolTip(
				(IEventBEditor<?>) elementComp.getPage().getEditor(),
				selectHyperlink.getShell());
		handler.activateHoverHelp(selectHyperlink);

		removeHyperlink = toolkit.createImageHyperlink(composite, SWT.TOP);
		setHyperlinkImage(removeHyperlink, EventBImage
				.getImage(IEventBSharedImages.IMG_REMOVE));
		removeHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				if(checkAndShowReadOnly(element)) {
					return;
				}
				AtomicOperation operation = OperationFactory
						.deleteElement((IInternalElement) element);
				History.getInstance().addOperation(operation);
			}
		});

		updateLinks();
		PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()
				.addListener(this);
		composite.addDisposeListener(this);
	}

	public void updateLinks() {
		final ElementDescRegistry registry = ElementDescRegistry.getInstance();
		IRodinElement element = elementComp.getElement();

		if (registry.getChildTypes(element.getElementType()).length != 0) {
			foldingHyperlink.setVisible(true);
		} else
			foldingHyperlink.setVisible(false);
		updateSelectHyperlink();
		selectHyperlink.redraw();
	}

	private static Image getDecoratedImage(IRodinElement element) {
		final Image image = EventBImage.getRodinImage(element);
		final Image decorated = PlatformUI.getWorkbench().getDecoratorManager()
				.getLabelDecorator().decorateImage(image, element);
		if (decorated != null) {
			return decorated;
		}
		return image;
	}

	public boolean isSelected() {
		return selectHyperlink.getSelection();
	}

	public void setSelected(boolean select) {
		if (select) {
			composite.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_GRAY));
		} else {
			if (EventBEditorUtils.DEBUG) {
				composite.setBackground(EventBSharedColor.getSystemColor(
						SWT.COLOR_CYAN));
			} else {
				composite.setBackground(EventBSharedColor.getSystemColor(
						SWT.COLOR_WHITE));
			}
		}
	}

	public void updateExpandStatus() {
		if (elementComp.isExpanded()) {
			foldingHyperlink.setImage(EventBImage.getImage(IEventBSharedImages.IMG_EXPANDED));
		} else {
			foldingHyperlink.setImage(EventBImage.getImage(IEventBSharedImages.IMG_COLLAPSED));
		}
	}

	// Note: the standard decorator mechanism works with ILabelProvider
	// which we do not have here
	// FIXME: not updated when proof status changes
	// TODO: listener should be placed upper, in the edit page
	// but it is inefficient without appropriate refresh methods
	// (currently we would use the postRefresh that would refresh
	// the same composite several times and globally)
	// Postponed until new editor implementation
	@Override
	public void labelProviderChanged(LabelProviderChangedEvent event) {
		final IRodinElement element = elementComp.getElement();
		final Object[] elements = event.getElements();
		if (elements == null) {
			// null means refresh all elements
			updateSelectHyperlink();
		} else if (Arrays.asList(elements).contains(element)) {
			updateSelectHyperlink();
		}
	}

	private void updateSelectHyperlink() {
		if (selectHyperlink.isDisposed()) {
			return;
		}
		final IRodinElement element = elementComp.getElement();
		setHyperlinkImage(selectHyperlink, getDecoratedImage(element));
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()
				.removeListener(this);
	}

}
