/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prover;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eventb.internal.ui.EventBImage;
import org.eventb.ui.EventBUIPlugin;
import org.osgi.framework.Bundle;

/**
 * @author htson
 *         <p>
 *         This is the dialog to show a set of dancing penguins when the a proof
 *         finishes.
 */
public class PenguinDanceDialog extends Dialog {

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 */
	public PenguinDanceDialog(Shell parentShell) {
		super(parentShell);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		Button ok = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		ok.setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		// composite.setLayoutData(gd);
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		Image image = registry.get(EventBImage.IMG_PENGUIN);

		Browser browser = new Browser(composite, Window.getDefaultOrientation());

		// if the bundle is not ready then there is no image
		Bundle bundle = Platform.getBundle(EventBUIPlugin.PLUGIN_ID);
		if ((bundle != null)
				&& (bundle.getState() & (Bundle.RESOLVED | Bundle.STARTING
						| Bundle.ACTIVE | Bundle.STOPPING)) != 0)
			return composite;

		// look for the image (this will check both the plugin and fragment
		// folders
		URL fullPathString = FileLocator.find(bundle, new Path(
				"icons/penguins-dancing.gif"), null);
		if (fullPathString == null) {
			try {
				fullPathString = new URL("icons/penguins-dancing.gif");
			} catch (MalformedURLException e) {
				return composite;
			}
		}

		try {
			browser.setText("<html><body><img align=\"center\" src=\""
					+ FileLocator.toFileURL(fullPathString).getFile()
					+ "\" alt=\"Penguin tumbler\"></body></html>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = image.getBounds().width + 20;
		gd.heightHint = image.getBounds().height + 20;
		browser.setLayoutData(gd);

		applyDialogFont(composite);
		return composite;
	}

}