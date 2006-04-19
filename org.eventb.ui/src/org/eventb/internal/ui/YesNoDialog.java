/**
 * 
 */
package org.eventb.internal.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author htson
 *
 */
public class YesNoDialog extends Dialog {
//	private FormToolkit toolkit;
	private String title;
	private String message;
	private boolean answer;
	
	public YesNoDialog(Shell parentShell, String title, String message) {
		super(parentShell);
//		this.toolkit = new FormToolkit(parentShell.getDisplay());
		this.title = title;
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		Image icon = Display.getDefault().getSystemImage(SWT.ICON_QUESTION);
		Label iconLabel = new Label(composite, SWT.NONE);
		iconLabel.setImage(icon);
		Label label = new Label(composite, SWT.NONE);
		label.setText(message);
		return composite;
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.YES_ID,
                IDialogConstants.YES_LABEL, true);

        createButton(parent, IDialogConstants.NO_ID,
                IDialogConstants.NO_LABEL, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.YES_ID) {
			answer = true;
		}
		else if (buttonId == IDialogConstants.NO_ID) {
			answer = false;
		}
		this.close();
	}

	public boolean getAnswer() {return answer;}
	
}
