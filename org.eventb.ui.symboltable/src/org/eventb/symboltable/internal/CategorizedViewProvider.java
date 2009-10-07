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

package org.eventb.symboltable.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eventb.symboltable.internal.Symbol.Category;

public class CategorizedViewProvider extends AbstractViewProvider implements
		IViewProvider {

	private ExpandBar expandBar;
	private final List<Label> labels = new ArrayList<Label>();

	private final Map<Category, ExpandItem> expItems = new HashMap<Category, ExpandItem>();

	private ScrolledComposite scrollComposite;
	private int oldScrollWidth;

	public CategorizedViewProvider(final Display display,
			final SymbolProvider contentProvider,
			final ClickListener clickListener) {
		super(display, contentProvider, clickListener);
	}

	public void createPartControl(final Composite parent) {
		init();

		scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL);

		expandBar = new ExpandBar(scrollComposite, SWT.NONE);
		// expandBar.setSpacing(8);
		final Listener listener = new Listener() {
			public void handleEvent(final Event event) {
				adjustExpandItems();
			}
		};
		expandBar.addListener(SWT.Expand, listener);
		expandBar.addListener(SWT.Collapse, listener);

		createExpandItems();
		addSymbolLabels();

		scrollComposite.setContent(expandBar);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				final int newWidth = scrollComposite.getSize().x;

				if (newWidth != oldScrollWidth) {
					adjustExpandItems();
				}

				oldScrollWidth = newWidth;
			}
		});
	}

	private boolean adjustExpandItems() {
		boolean changed = false;

		final int expBarWidth = scrollComposite.getClientArea().width;
		final int itemWidth = expBarWidth - expandBar.getBorderWidth() - 2
				* expandBar.getSpacing();

		for (final Category cat : Category.values()) {
			final ExpandItem item = expItems.get(cat);
			final int oldHeight = item.getHeight();

			final Control composite = item.getControl();
			final int prefHight = composite.computeSize(itemWidth
					- composite.getBorderWidth(), SWT.DEFAULT).y;

			if (prefHight != oldHeight) {
				item.setHeight(prefHight);
				changed = true;
			}
		}

		if (changed) {
			final Point newBarSize = expandBar.computeSize(expBarWidth,
					SWT.DEFAULT);
			// expandBar.setSize(newBarSize);

			scrollComposite.setMinHeight(newBarSize.y);
			scrollComposite.pack(true);
			// scrollComposite.setSize(scrollComposite.computeSize(SWT.DEFAULT,
			// SWT.DEFAULT, true));
		}

		return changed;
	}

	private void createExpandItems() {
		for (final Category cat : Category.values()) {
			final ExpandItem item = new ExpandItem(expandBar, SWT.NONE);
			item.setText(cat.toString());
			item.setControl(createSymbolsBackground());
			item.setExpanded(true);
			expItems.put(cat, item);
		}
	}

	private Control createSymbolsBackground() {
		final Composite background = new Composite(expandBar, SWT.NONE);
		background.setToolTipText(Messages.view_tooltip);
		final RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.wrap = true;
		rowLayout.pack = false;
		rowLayout.justify = false;
		background.setLayout(rowLayout);

		return background;
	}

	private void addSymbolLabels() {
		final List<Symbol> symbols = contentProvider.getSymbols();
		for (final Symbol symbol : symbols) {
			final ExpandItem expandItem = expItems.get(symbol.category);
			createSymbolLabel((Composite) expandItem.getControl(), symbol);
		}
	}

	private void createSymbolLabel(final Composite background,
			final Symbol symbol) {
		final Composite labelFrame = new Composite(background, SWT.NONE);
		final GridLayout layout = new GridLayout(1, true);
		labelFrame.setLayout(layout);

		final Label label = new Label(labelFrame, SWT.CENTER);
		label.setData(symbol);
		label.setText(symbol.text);
		label.setToolTipText(createTooltip(symbol));
		label.setBackground(white);
		label.setForeground(black);
		label.setFont(symbolFont);

		final int size = 24;
		final GridData layoutData = new GridData();
		layoutData.minimumHeight = size;
		layoutData.minimumWidth = size;
		layoutData.heightHint = size;
		layoutData.widthHint = size;
		layoutData.horizontalAlignment = SWT.FILL | SWT.CENTER;
		layoutData.verticalAlignment = SWT.FILL | SWT.CENTER;
		label.setLayoutData(layoutData);

		label.addMouseListener(clickListener);
		labels.add(label);
	}

	public void setFocus() {
		expandBar.setFocus();
	}

	@Override
	public void dispose() {
		labels.clear();
		expandBar = null;

		super.dispose();
	}

	public void setEnabled(final boolean enabled) {
		for (final Label label : labels) {
			label.setForeground(enabled ? black : gray);
		}
	}
}
