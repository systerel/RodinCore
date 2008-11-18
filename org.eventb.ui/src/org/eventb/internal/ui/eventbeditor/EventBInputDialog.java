/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - add getNameInputText and getContentInputText to factor several methods
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;

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

	private final int MAX_WIDTH = 800;

	private final int MAX_HEIGHT = 500;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
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
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
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
			Text text = (Text) e.widget;
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Modified: " + text.getText());
			if (text.getText().equals("")) {
				dirtyTexts.remove(text);
				text.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_WHITE));
			} else if (text.isFocusControl()) {
				dirtyTexts.add(text);
				text.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_YELLOW));
			}
		}
	}

	protected void clearDirtyTexts() {
		for (Text text : dirtyTexts) {
			text.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_WHITE));
		}
		dirtyTexts.clear();
	}

	protected void updateSize() {
		Composite parent = this.getContents().getParent();
		Point curr = parent.getSize();
		Point pt = parent.computeSize(SWT.DEFAULT,
				SWT.DEFAULT);

		if (curr.x < pt.x || curr.y < pt.y) {
			int x = curr.x < pt.x ? pt.x : curr.x;
			int y = curr.y < pt.y ? pt.y : curr.y;
			if (x <= MAX_WIDTH && y <= MAX_HEIGHT)
				parent.setSize(x, y);
		}
		else { // Bug: resize to force refresh
			parent.setSize(curr.x + 1, curr.y);
		}
		scrolledForm.reflow(true);
	}

	protected IEventBInputText getNameInputText(FormToolkit tk, Composite composite, String text) {
		final IEventBInputText inputText = new EventBText(tk.createText(composite, text));
		final GridData gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		inputText.getTextWidget().setLayoutData(gd);
		inputText.getTextWidget().addModifyListener(new DirtyStateListener());
		return inputText;
	}

	protected EventBMath getContentInputText(FormToolkit tk, Composite composite) {
		final EventBMath textMath = new EventBMath(tk.createText(composite,
				""));
		final GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		textMath.getTextWidget().setLayoutData(gd);
		textMath.getTextWidget().addModifyListener(new DirtyStateListener());
		return textMath;
	}
}
