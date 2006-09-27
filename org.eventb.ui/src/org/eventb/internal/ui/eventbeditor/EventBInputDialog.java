/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         new event with some local varialbes, guards and actSubstitutions.
 */
public abstract class EventBInputDialog extends Dialog {
	protected Collection<Text> dirtyTexts;

	protected FormToolkit toolkit;

	protected ScrolledForm scrolledForm;

	private String title;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 * @param defaultName
	 *            the default name for the event
	 */
	public EventBInputDialog(Shell parentShell, String title) {
		super(parentShell);
		this.title = title;
		dirtyTexts = new HashSet<Text>();
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		toolkit = new FormToolkit(parent.getDisplay());
		toolkit.setBackground(parent.getBackground());
		toolkit.setBorderStyle(SWT.BORDER);

		scrolledForm = toolkit.createScrolledForm(composite);
		Composite body = scrolledForm.getBody();

		createContents();

		composite.pack();

		toolkit.paintBordersFor(body);
		applyDialogFont(body);
		return body;
	}

	protected abstract void createContents();

	protected class GuardListener implements ModifyListener {

		Text grdText;

		public GuardListener(Text grdText) {
			this.grdText = grdText;
		}

		public void modifyText(ModifyEvent e) {
			Text varText = (Text) e.widget;
			if (!dirtyTexts.contains(grdText)) {
				String text = varText.getText();
				if (text.equals(""))
					grdText.setText("");
				else
					grdText.setText(text + " \u2208 ");
			}
		}

	}

	protected class ActionListener implements ModifyListener {

		Text actText;

		public ActionListener(Text actText) {
			this.actText = actText;
		}

		public void modifyText(ModifyEvent e) {
			Text varText = (Text) e.widget;
			if (!dirtyTexts.contains(actText)) {
				String text = varText.getText();
				if (text.equals(""))
					actText.setText("");
				else
					actText.setText(text + " \u2254 ");
			}
		}

	}

	protected class DirtyStateListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			Display display = Display.getCurrent();
			Text text = (Text) e.widget;
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Modified: " + text.getText());
			if (text.getText().equals("")) {
				dirtyTexts.remove(text);
				text.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
			} else if (text.isFocusControl()) {
				dirtyTexts.add(text);
				text.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
			}

		}
	}

	protected void updateSize() {
		Point curr = this.getContents().getParent().getSize();
		Point pt = this.getContents().getParent().computeSize(SWT.DEFAULT,
				SWT.DEFAULT);

		if (curr.x < pt.x || curr.y < pt.y) {
			int x = curr.x < pt.x ? pt.x : curr.x;
			int y = curr.y < pt.y ? pt.y : curr.y;
			this.getContents().getParent().setSize(x, y);
		} else {
			scrolledForm.reflow(true);
		}
	}

}
