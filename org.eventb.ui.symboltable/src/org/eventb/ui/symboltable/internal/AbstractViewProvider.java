/*******************************************************************************
 * Copyright (c) 2009, 2012 Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Universitaet Duesseldorf - initial API and implementation
 *     Systerel - Removed erroneous calls to dispose()
 *******************************************************************************/
package org.eventb.ui.symboltable.internal;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eventb.ui.EventBUIPlugin;

public abstract class AbstractViewProvider {
	protected static final String TOOLTIP_PATTERN = "{0} (ASCII: {1})";

	protected final Display display;
	protected Color white;
	protected Color gray;
	protected Color black;
	protected Font symbolFont;

	protected final SymbolProvider contentProvider;
	protected final ClickListener clickListener;

	protected AbstractViewProvider(final Display display,
			final SymbolProvider contentProvider,
			final ClickListener clickListener) {
		this.display = display;
		this.contentProvider = contentProvider;
		this.clickListener = clickListener;
	}

	protected void init() {
		white = display.getSystemColor(SWT.COLOR_WHITE);
		gray = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		black = display.getSystemColor(SWT.COLOR_BLACK);

		// make sure Event-B UI plug-in is loaded and font is registered
		EventBUIPlugin.getDefault();
		// final FontData[] fontList = display.getFontList(null, true);
		symbolFont = new Font(display, "Brave Sans Mono", 12, SWT.NORMAL);
	}

	protected String createTooltip(final Symbol symbol) {
		// escape & character for correct display in tooltip
		final String asciiCombo = symbol.asciiCombo.replace("&", "&&");
		return MessageFormat.format(TOOLTIP_PATTERN, symbol.description,
				asciiCombo);
	}

	protected void dispose() {
		symbolFont.dispose();
	}
}
