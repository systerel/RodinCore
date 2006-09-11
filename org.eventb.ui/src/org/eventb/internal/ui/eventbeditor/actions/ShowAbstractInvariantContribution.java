package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IInvariant;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class ShowAbstractInvariantContribution extends ContributionItem {

	private IRodinFile file;

	public ShowAbstractInvariantContribution(IRodinFile file) {
		this.file = file;
	}

	@Override
	public void fill(Menu menu, int index) {

		try {

			IRodinFile abstractFile = EventBEditorUtils.getAbstractFile(file);
			while (abstractFile != null && abstractFile.exists()) {
				createMenuItem(menu, abstractFile);
				abstractFile = EventBEditorUtils.getAbstractFile(abstractFile);
			}

		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createMenuItem(Menu menu, final IRodinFile abstractFile) throws RodinDBException {
		final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText(EventBPlugin.getComponentName(abstractFile
				.getElementName()));
		menuItem.setImage(EventBImage.getImage(EventBImage.IMG_REFINES));

		final IRodinElement inv;
		IRodinElement[] invs = abstractFile
				.getChildrenOfType(IInvariant.ELEMENT_TYPE);
		if (invs.length != 0)
			inv = invs[0];
		else
			inv = null;
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					if (inv != null)
						UIUtils.linkToEventBEditor(inv);
					else
						UIUtils.linkToEventBEditor(abstractFile);
					break;
				}
			}
		};
		menuItem.addListener(SWT.Selection, listener);

	}

}
