/*******************************************************************************
 * Copyright (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Heinrich Heine Universitaet Duesseldorf - initial API and implementation
 *******************************************************************************/

package org.eventb.ui.symboltable.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class UncategorizedViewProvider extends AbstractViewProvider implements
		IViewProvider {

	private Composite symbolsComposite;
	private final List<Label> labels = new ArrayList<Label>();

	public UncategorizedViewProvider(final Display display,
			final SymbolProvider contentProvider,
			final ClickListener clickListener) {
		super(display, contentProvider, clickListener);
	}

	public void createPartControl(final Composite parent) {
		init();

		final ScrolledComposite scrollComposite = new ScrolledComposite(parent,
				SWT.V_SCROLL);

		symbolsComposite = new Composite(scrollComposite, SWT.NONE);
		symbolsComposite.setToolTipText(Messages.view_tooltip);
		final RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.wrap = true;
		rowLayout.pack = false;
		rowLayout.justify = false;
		rowLayout.spacing = 4;
		rowLayout.marginHeight = 4;
		rowLayout.marginWidth = 4;
		symbolsComposite.setLayout(rowLayout);
		addSymbolLabels(symbolsComposite);

		scrollComposite.setContent(symbolsComposite);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				final Rectangle r = scrollComposite.getClientArea();
				scrollComposite.setMinSize(symbolsComposite.computeSize(
						r.width, SWT.DEFAULT));
			}
		});
	}

	private void addSymbolLabels(final Composite background) {
		final List<Symbol> symbols = contentProvider.getSymbols();
		for (final Symbol symbol : symbols) {
			final Label label = createSymbolLabel(background, symbol);
			labels.add(label);
		}
	}

	private Label createSymbolLabel(final Composite background,
			final Symbol symbol) {
		final Label label = new Label(background, SWT.CENTER);
		label.setData(symbol);
		label.setText(symbol.text);
		label.setToolTipText(createTooltip(symbol));
		label.setBackground(white);
		label.setForeground(black);
		label.setFont(symbolFont);

		label.addMouseListener(clickListener);
		return label;
	}

	public void setFocus() {
		symbolsComposite.setFocus();
	}

	@Override
	public void dispose() {
		labels.clear();
		symbolsComposite = null;

		super.dispose();
	}

	public void setEnabled(final boolean enabled) {
		for (final Label label : labels) {
			label.setForeground(enabled ? black : gray);
		}
	}
}
