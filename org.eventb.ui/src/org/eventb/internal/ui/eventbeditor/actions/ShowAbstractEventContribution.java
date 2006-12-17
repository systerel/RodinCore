package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eventb.core.IEvent;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class ShowAbstractEventContribution extends ContributionItem {

	private IRodinFile file;

	private IEvent event;

	public ShowAbstractEventContribution(IRodinFile file, IEvent event) {
		this.file = file;
		this.event = event;
	}

	@Override
	public void fill(Menu menu, int index) {

		try {
			IRodinFile abstractFile = EventBEditorUtils.getAbstractFile(file);
			IEvent abs_evt = event;
			while (abstractFile != null && abstractFile.exists()
					&& abs_evt != null) {
				abs_evt = EventBEditorUtils.getAbstractEvent(abstractFile,
						abs_evt);
				if (abs_evt != null)
					createMenuItem(menu, abstractFile, abs_evt);
				else
					break;
				abstractFile = EventBEditorUtils.getAbstractFile(abstractFile);
			}

		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createMenuItem(Menu menu, final IRodinFile abstractFile,
			final IEvent abs_evt) {
		final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText(abstractFile.getBareName());
		menuItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_REFINES));

		Listener listener = new Listener() {
			public void handleEvent(Event ev) {
				switch (ev.type) {
				case SWT.Selection:
					UIUtils.linkToEventBEditor(abs_evt);
					break;
				}
			}
		};
		menuItem.addListener(SWT.Selection, listener);
	}

}
