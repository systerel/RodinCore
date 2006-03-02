package org.eventb.internal.ui.eventbeditor;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class ElementInputDialog extends Dialog {
	private String defaultName;
	private Collection<String> names;
	private Collection<Text> texts;
	private ScrolledForm scrolledForm;
	private String title;
	private String message;
	private FormToolkit toolkit;
	
	public ElementInputDialog(Shell parentShell, FormToolkit toolkit, String title, String message, String defaultName) {
		super(parentShell);
		this.toolkit = toolkit;
		this.title = title;
		this.message = message;
		this.defaultName = defaultName;
		texts = new ArrayList<Text>();
		names = new ArrayList<String>();
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
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.YES_ID, "&Add", false);
		
		createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);

        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite = (Composite) super.createDialogArea(parent);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		gd.widthHint = 300;
		composite.setLayoutData(gd);
		
		scrolledForm = toolkit.createScrolledForm(composite);
		Composite body = scrolledForm.getBody();
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		body.setLayout(layout);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);
		
		Label label = toolkit.createLabel(body, message);
		label.setLayoutData(new GridData());
		
		Text text = toolkit.createText(body, defaultName);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 100;
		text.setLayoutData(gd);
		texts.add(text);
		
		label = toolkit.createLabel(body, message);
		label.setLayoutData(new GridData());
		
		text = text = toolkit.createText(body, "");
		text.setLayoutData(gd);
		texts.add(text);
		
		label = toolkit.createLabel(body, message);
		label.setText(message);
		label.setLayoutData(new GridData());
		
		text = text = toolkit.createText(body, "");
		text.setLayoutData(gd);
		texts.add(text);
		
		toolkit.paintBordersFor(body);
		applyDialogFont(body);
		return body;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			names = new HashSet<String>();
        }
		else if (buttonId == IDialogConstants.YES_ID) {
			Label label = toolkit.createLabel(scrolledForm.getBody(), message);
			label.setLayoutData(new GridData());
			
			Text text = new Text(scrolledForm.getBody(), SWT.SINGLE);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			text.setLayoutData(gd);
			texts.add(text);
			
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			toolkit.paintBordersFor(scrolledForm.getBody());
			scrolledForm.reflow(true);
		}
		else if (buttonId == IDialogConstants.OK_ID) {
			names = new ArrayList<String>();
			for (Iterator<Text> it = texts.iterator(); it.hasNext();) {
				Text text = it.next();
				if (!text.getText().equals("")) names.add(text.getText());
			}
		}
		super.buttonPressed(buttonId);
	}
	
	public Collection<String> getNames() {
		return names;
	}
	
}
