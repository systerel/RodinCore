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

public class NewEventInputDialog extends Dialog {
	private String defaultName;
	private String name;
	private Collection<String> varNames;
	private Collection<String> grdNames;
	private Collection<String> grdPredicates;
	private Collection<String> actions;
	private Text nameText;
	private Collection<Text> varNameTexts;
	private Collection<Text> grdNameTexts;
	private Collection<Text> grdPredicateTexts;
	private Collection<Text> actionTexts;
	private ScrolledForm scrolledForm;
	private String title;
	private FormToolkit toolkit;
	
	public NewEventInputDialog(Shell parentShell, FormToolkit toolkit, String title, String defaultName) {
		super(parentShell);
		this.toolkit = toolkit;
		this.title = title;
		this.defaultName = defaultName;
		varNames = new HashSet<String>();
		grdNames = new HashSet<String>();
		grdPredicates = new HashSet<String>();
		actions = new HashSet<String>();
		varNameTexts = new ArrayList<Text>();
		grdNameTexts = new ArrayList<Text>();
		grdPredicateTexts = new ArrayList<Text>();
		actionTexts = new ArrayList<Text>();
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
		Composite composite = (Composite) super.createDialogArea(parent);
//		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
//		gd.heightHint = 320;
//		gd.widthHint = 500;
//		composite.setLayoutData(gd);
		
		scrolledForm = toolkit.createScrolledForm(composite);
		Composite body = scrolledForm.getBody();
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		layout.verticalSpacing = 5;
		body.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);
		
		Label label = toolkit.createLabel(body, "Name", SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		label.setLayoutData(gd);
		
		Composite separator = toolkit.createComposite(body);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);
		
		label = toolkit.createLabel(body, "Variable name(s)", SWT.CENTER);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);
		
		nameText = toolkit.createText(body, defaultName);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		nameText.setLayoutData(gd);

		separator = toolkit.createComposite(body);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);
		
		for (int i = 0; i < 3; i++) {
			Text text = toolkit.createText(body, "");
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			text.setLayoutData(gd);
			varNameTexts.add(text);
		}
		
		separator = toolkit.createCompositeSeparator(body);
		GridData separatorGD = new GridData();
		separatorGD.heightHint = 5;
		separatorGD.horizontalSpan = 5;
		separator.setLayoutData(separatorGD);
		
		label = toolkit.createLabel(body, "Guard name(s)", SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		label.setLayoutData(gd);
		
		separator = toolkit.createComposite(body);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);
		
		label = toolkit.createLabel(body, "Guard predicate(s)", SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		for (int i = 1; i <= 3; i++) {
			Text text = toolkit.createText(body, "grd"+i);
			gd = new GridData(SWT.FILL, SWT.NONE, false, false);
			text.setLayoutData(gd);
			grdNameTexts.add(text);
			
			separator = toolkit.createComposite(body);
			gd = new GridData(SWT.NONE, SWT.NONE, false, false);
			gd.widthHint = 30;
			gd.heightHint = 20;
			separator.setLayoutData(gd);
			
			text = toolkit.createText(body, "");
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.horizontalSpan = 3;
			gd.widthHint = 200;
			text.setLayoutData(gd);
			grdPredicateTexts.add(text);
			new EventBMath(text);
		}
		
		
		separator = toolkit.createCompositeSeparator(body);
		separator.setLayoutData(separatorGD);
		
		label = toolkit.createLabel(body, "Action(s)", SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 5;
		label.setLayoutData(gd);

		
		for (int i = 0; i < 3; i++) {
			Text text = toolkit.createText(body, "");
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.horizontalSpan = 5;
			gd.widthHint = 250;
			text.setLayoutData(gd);
			actionTexts.add(text);
			new EventBMath(text);
		}
		

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
			name = null;
			varNames = new HashSet<String>();
			grdNames = new HashSet<String>();
			grdPredicates = new HashSet<String>();
			actions = new HashSet<String>();
        }
		else if (buttonId == IDialogConstants.OK_ID) {
			name = nameText.getText();
			
			varNames = new ArrayList<String>();
			Object [] varNameList = varNameTexts.toArray();
			for (int i = 0; i < varNameList.length; i++) {
				Text nameText = (Text) varNameList[i];
				if (!nameText.getText().equals("")) {
					varNames.add(nameText.getText());
				}
			}
			
			grdNames = new ArrayList<String>();
			grdPredicates = new ArrayList<String>();
			Object [] grdNameList = grdNameTexts.toArray();
			Object [] grdPredicateList = grdPredicateTexts.toArray();
			for (int i = 0; i < grdNameList.length; i++) {
				Text predicateText = (Text) grdPredicateList[i];
				if (!predicateText.getText().equals("")) {
					Text nameText = (Text) grdNameList[i];
					grdNames.add(nameText.getText());
					grdPredicates.add(predicateText.getText());
				}
			}
			
			actions = new ArrayList<String>();
			Object [] actionList = actionTexts.toArray();
			for (int i = 0; i < actionList.length; i++) {
				Text actionText = (Text) actionList[i];
				if (!actionText.getText().equals("")) {
					actions.add(actionText.getText());
				}
			}
		}
		super.buttonPressed(buttonId);
	}
	
	public String getName() {
		return name;
	}
	
	public String [] getVariables() {
		return (String []) varNames.toArray(new String[varNames.size()]);
	}
	
	public String [] getGrdNames() {
		return (String []) grdNames.toArray(new String[grdNames.size()]);
	}
	
	public String [] getGrdPredicates() {
		return (String []) grdPredicates.toArray(new String[grdPredicates.size()]);
	}
	
	public String [] getActions() {
		return (String []) actions.toArray(new String[actions.size()]);
	}
}
