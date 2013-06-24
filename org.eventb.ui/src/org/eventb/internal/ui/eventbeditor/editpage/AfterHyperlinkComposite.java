/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
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
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

public class AfterHyperlinkComposite extends AbstractHyperlinkComposite {

	ImageHyperlink addAfterHyperlink;

	public AfterHyperlinkComposite(EditPage page, IInternalElement parent,
			IInternalElementType<? extends IInternalElement> type,
			FormToolkit toolkit, Composite compParent) {
		super(page, parent, type, toolkit, compParent);
	}

	@Override
	public void createContent(FormToolkit toolkit, int level) {
		Composite tmp = toolkit.createComposite(composite);
		GridData gridData = new GridData();
		gridData.widthHint = (level + 1) * 40;
		gridData.heightHint = 0;
		tmp.setLayoutData(gridData);

		addAfterHyperlink = toolkit.createImageHyperlink(composite,
				SWT.TOP);
		setHyperlinkImage(addAfterHyperlink, EventBImage
				.getImage(IEventBSharedImages.IMG_ADD));
		addAfterHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (checkAndShowReadOnly()) {
					return;
				}
				AtomicOperation operation = OperationFactory
						.createElementGeneric(parent, type, null);
				History.getInstance().addOperation(operation);
			}

		});
		addAfterHyperlink.setLayoutData(new GridData());

		setInitialised(true);
	}
}
