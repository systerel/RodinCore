package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

public class AfterHyperlinkComposite extends AbstractHyperlinkComposite {

	ImageHyperlink addAfterHyperlink;

	public AfterHyperlinkComposite(EditPage page, IInternalParent parent,
			IInternalElementType<? extends IInternalElement> type,
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

		addAfterHyperlink = toolkit.createImageHyperlink(composite,
				SWT.TOP);
		addAfterHyperlink.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_ADD));
		addAfterHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				IEventBEditor editor = (IEventBEditor) page.getEditor();
				try {
					IRodinElement element = EditSectionRegistry.getDefault()
							.createElement(editor, parent, type, null);
					page.recursiveExpand(element);
				} catch (CoreException coreException) {
					EventBUIExceptionHandler
							.handleCreateElementException(coreException);
				}
			}

		});
		addAfterHyperlink.setLayoutData(new GridData());
		super.createHyperlinks(toolkit, level);
	}
}
