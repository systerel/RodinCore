/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.rodinp.internal.keyboard.translators;

import org.eclipse.swt.widgets.Widget;

/**
 * @author htson
 *         <p>
 *         The interface for Event-B Keyboard translator
 */
public interface IRodinKeyboardTranslator {

	/**
	 * This method translates the content of the input Text widget into Event-B
	 * mathematical language.
	 * <p>
	 * 
	 * @param widget
	 *            a widget
	 */
	public void translate(Widget widget);

}
