package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;

public abstract class AbstractHyperlinkComposite {

	Composite composite;

	EditPage page;

	IInternalParent parent;

	boolean initialised;
	
	IInternalElementType<?> type;

	ImageHyperlink upHyperlink;
	
	ImageHyperlink downHyperlink;
	
	public AbstractHyperlinkComposite(EditPage page, IInternalParent parent,
			IInternalElementType<?> type,
			FormToolkit toolkit, Composite compParent) {
		this.page = page;
		this.parent = parent;
		this.type = type;
		initialised = false;
		createComposite(toolkit, compParent);
	}

	private void createComposite(FormToolkit toolkit, Composite compParent) {
		composite = toolkit.createComposite(compParent);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(composite.getDisplay()
					.getSystemColor(SWT.COLOR_DARK_GRAY));
		}
	}

	public boolean isInitialised() {
		return initialised;
	}
	
	public void setHeightHint(int heightHint) {
		GridData gridData = (GridData) composite.getLayoutData();
		gridData.heightHint = heightHint;
	}

	public void createHyperlinks(FormToolkit toolkit, int level) {
		initialised = true;
		upHyperlink = toolkit.createImageHyperlink(
				composite, SWT.TOP);
		upHyperlink.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_UP));
		upHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				page.move(true);
			}

		});
		upHyperlink.setLayoutData(new GridData());
		
		downHyperlink = toolkit.createImageHyperlink(
				composite, SWT.TOP);
		downHyperlink.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_DOWN));
		downHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				page.move(false);
			}

		});
		downHyperlink.setLayoutData(new GridData());
	}

}
