/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *     Systerel - introduced read only elements
 *     Systerel - fixed Hyperlink.setImage() calls
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import static org.eventb.internal.ui.EventBUtils.setHyperlinkImage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

public class BeforeHyperlinkComposite extends AbstractHyperlinkComposite {

	ImageHyperlink addBeforeHyperlink;
	
	public BeforeHyperlinkComposite(EditPage page, IInternalElement parent,
			IInternalElementType<?> type,
			FormToolkit toolkit, Composite compParent) {
		super(page, parent, type, toolkit, compParent);
	}

	@Override
	public void createHyperlinks(FormToolkit toolkit, int level) {
		Composite tmp = toolkit.createComposite(composite);
		GridData gridData = new GridData();
		gridData.widthHint = (level + 1) * 40; 
		gridData.heightHint = 0;
		tmp.setLayoutData(gridData);

		addBeforeHyperlink = toolkit.createImageHyperlink(
				composite, SWT.TOP);
		setHyperlinkImage(addBeforeHyperlink, EventBImage
				.getImage(IEventBSharedImages.IMG_ADD));
		addBeforeHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				try {
					if (checkAndShowReadOnly()) {
						return;
					}
					IInternalElement[] children = parent.getChildrenOfType(type);
					assert (children.length != 0);
					IInternalElement first = children[0];
					AtomicOperation operation = OperationFactory
							.createElementGeneric(parent, type, first);
					History.getInstance().addOperation(operation);

				} catch (RodinDBException exception) {
					EventBUIExceptionHandler
							.handleCreateElementException(exception);
				}
			}

		});
		addBeforeHyperlink.setLayoutData(new GridData());
		super.createHyperlinks(toolkit, level);
	}

}
