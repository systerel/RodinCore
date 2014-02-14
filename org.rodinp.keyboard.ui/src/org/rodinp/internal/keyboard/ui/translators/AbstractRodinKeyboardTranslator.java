/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - supported contribution through extension and at runtime
 *     Systerel - removed duplicated code
 *******************************************************************************/
package org.rodinp.internal.keyboard.ui.translators;

import static org.rodinp.keyboard.core.KeyboardUtils.debug;

import java.util.Collection;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.rodinp.keyboard.core.ISymbol;

/**
 * @author htson
 *         <p>
 *         Abstract class for symbol translation.
 *         </p>
 */
public abstract class AbstractRodinKeyboardTranslator implements IRodinKeyboardTranslator {

	/**
	 * Boolean value telling whether the DEBUG mode is activated or not
	 */
	protected final boolean debug;

	/**
	 * Boolean value telling whether the current translator is a text translator
	 * or not.
	 */
	protected final boolean textTranslator;

	/**
	 * The object which is responsible of computing and resolving the
	 * appropriate symbols depending on whether the translator is a text
	 * translator or a mathematical translator.
	 */
	protected final SymbolComputer symbolComputer;

	public AbstractRodinKeyboardTranslator(boolean textTranslator,
			boolean debug, SymbolComputer symbolComputer) {
		this.debug = debug;
		this.symbolComputer = symbolComputer;
		this.textTranslator = textTranslator;
	}
	
	@Override
	public void translate(Widget widget) {
		if (widget instanceof Text) {
			translate(new TextWrapper((Text) widget));
		}
		if (widget instanceof StyledText) {
			translate(new StyledTextWrapper((StyledText) widget));
		}
	}
	
	protected void translate(AbstractWidgetWrapper widget) {
		final String text = widget.getText();
		final int beginIndex = 0;
		final int endIndex = text.length();
		translate(widget, beginIndex, endIndex);
	}

	/**
	 * FIXME 
	 * WARNING THIS IMPLEMENTATION DOES NOT WORK AT ALL
	 * 
	 * THIS IMPLEMENTATION IS OVERRIDDEN BY THE LEGACY CODE
	 * @see LegacyRodinKeyboardTranslator
	 */
	protected void translate(AbstractWidgetWrapper widget, int beginIndex,
			int subStringEndIndex) {
		if (debug) {
			debug("***************************************");
			debug("Begin: " + beginIndex);
			debug("End: " + subStringEndIndex);
		}
		if (beginIndex == subStringEndIndex) {
			return;
		}
		int i = 0;
		final SingleSymbolTranslator singleTranslator = new SingleSymbolTranslator(
				widget, textTranslator, debug);
		for (i = symbolComputer.getMaxSymbolSize(); i > 0; i--) {
			final Collection<ISymbol> collection = symbolComputer.getSymbols(i);
			if (collection == null) {
				continue;
			}
			for (ISymbol symbol : collection) {
				singleTranslator.setSymbol(symbol);
				singleTranslator.translateAllOccurrences();

			}
		}
	}

}
