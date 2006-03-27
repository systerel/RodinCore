package org.eventb.internal.ui.eventbeditor;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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
import org.eventb.internal.ui.EventBMath;

public class ElementNameContentInputDialog extends Dialog {
	private String defaultName;
	private Collection<String> names;
	private Collection<String> contents;
	private Collection<Text> nameTexts;
	private Collection<Text> contentTexts;
	private ScrolledForm scrolledForm;
	private String title;
	private String message;
	private FormToolkit toolkit;
	private int counter;
	
	public ElementNameContentInputDialog(Shell parentShell, String title, String message, String defaultName, int counter) {
		super(parentShell);
		this.title = title;
		this.message = message;
		this.defaultName = defaultName;
		this.counter = counter;
		names = new ArrayList<String>();
		nameTexts = new ArrayList<Text>();
		contents = new ArrayList<String>();
		contentTexts = new ArrayList<Text>();
		setShellStyle(getShellStyle() | SWT.RESIZE);
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
		toolkit = new FormToolkit(parent.getDisplay());
		toolkit.setBackground(parent.getBackground());
		toolkit.setBorderStyle(SWT.BORDER);
		
		scrolledForm = toolkit.createScrolledForm(composite);
		Composite body = scrolledForm.getBody();
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		body.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);
		
		Label label = toolkit.createLabel(body, message);
		label.setLayoutData(new GridData());
		
		Text text = toolkit.createText(body, defaultName + (counter++));
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		text.setLayoutData(gd);
		nameTexts.add(text);
		
		EventBMath textMath = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		textMath.getTextWidget().setLayoutData(gd);
		contentTexts.add(textMath.getTextWidget());
		textMath.getTextWidget().setFocus();
		
		label = toolkit.createLabel(body, message);
//		label.setLayoutData(new GridData());
		
		text = text = toolkit.createText(body, defaultName + (counter++));
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		text.setLayoutData(gd);
		nameTexts.add(text);
		
		textMath = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		textMath.getTextWidget().setLayoutData(gd);
		contentTexts.add(textMath.getTextWidget());
		
		label = toolkit.createLabel(body, message);
		label.setText(message);
//		label.setLayoutData(new GridData());
		
		text = text = toolkit.createText(body, defaultName + (counter++));
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		text.setLayoutData(gd);
		nameTexts.add(text);
		
		textMath = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		textMath.getTextWidget().setLayoutData(gd);
		contentTexts.add(textMath.getTextWidget());
		
		composite.pack();
		
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
			contents = new HashSet<String>();
        }
		else if (buttonId == IDialogConstants.YES_ID) {
			Label label = toolkit.createLabel(scrolledForm.getBody(), message);
			label.setLayoutData(new GridData());
			
			Text text = toolkit.createText(scrolledForm.getBody(), defaultName + (counter++));
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			text.setLayoutData(gd);
			nameTexts.add(text);
			
			text = toolkit.createText(scrolledForm.getBody(), "");
			text.setLayoutData(gd);
			contentTexts.add(text);
			
			toolkit.paintBordersFor(scrolledForm.getBody());
			scrolledForm.reflow(true);
		}
		else if (buttonId == IDialogConstants.OK_ID) {
			names = new ArrayList<String>();
			contents = new ArrayList<String>();
			Object [] namesList = nameTexts.toArray();
			Object [] contentsList = contentTexts.toArray();
			for (int i = 0; i < namesList.length; i++) {
				Text contentText = (Text) contentsList[i];
				if (!contentText.getText().equals("")) {
					Text nameText = (Text) namesList[i];
					names.add(nameText.getText());
					contents.add(contentText.getText());
				}
			}
		}
		super.buttonPressed(buttonId);
	}
	
	public String [] getNewNames() {
		return (String []) names.toArray(new String[names.size()]);
	}
	
	public String [] getNewContents() {
		return (String []) contents.toArray(new String[contents.size()]);
	}
	
}
