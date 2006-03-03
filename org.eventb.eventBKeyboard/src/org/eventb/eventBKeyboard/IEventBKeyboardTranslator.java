/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.eventBKeyboard;

import org.eclipse.swt.widgets.Text;

/**
 * @author htson
 * The interface for Event-B Keyboard translator
 */
public interface IEventBKeyboardTranslator {
	public void translate(Text widget);
}
