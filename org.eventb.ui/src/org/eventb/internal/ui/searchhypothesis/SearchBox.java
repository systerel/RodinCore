/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/
package org.eventb.internal.ui.searchhypothesis;

import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eventb.internal.ui.EventBMath;

/**
 * Implementation of a text box for entering a pattern to be used for searching
 * hypotheses in the current sequent of the current proof.
 * 
 * @author Thomas Muller
 */
public final class SearchBox extends ToolBarContributionItem {

	protected Text text;

	protected SearchHypothesisPage searchHypothesis;

	public SearchBox(SearchHypothesisPage searchHyp) {
		super();
		searchHypothesis = searchHyp;
	}

	protected Composite createComposite(ToolBar parent) {
		final Composite composite = new Composite(parent, SWT.FLAT);
		composite.setLayout(new FillLayout());
		text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		final EventBMath math = new EventBMath(text);
		final SelectionListener listener = new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				math.translate();
				search();
			}
			
		};
		text.addSelectionListener(listener);
		return composite;
	}

	@Override
	public void fill(ToolBar toolbar, int index) {
		final Composite composite = createComposite(toolbar);
		final ToolItem ti = new ToolItem(toolbar, SWT.SEPARATOR, index);
		ti.setControl(composite);
		ti.setWidth(200);
	}

	public void setFocus() {
		text.setFocus();
	}

	public void search() {
		searchHypothesis.setPattern(text.getText());
		searchHypothesis.updatePage();
		setFocus();
	}
	
	public String getSearchedText() {
		return text.getText();
	}

	public void setSearchedText(String searched) {
		text.setText(searched);
	}

}