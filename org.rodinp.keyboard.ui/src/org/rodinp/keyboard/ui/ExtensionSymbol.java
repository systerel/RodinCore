/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard.ui;

/**
 * Class that maps to {@link org.rodinp.keyboard.ui.core.ExtensionSymbol} for
 * backward compatibility.
 * 
 * @deprecated This class shall not be used. Users shall use
 *             {@link org.rodinp.keyboard.ui.core.ExtensionSymbol} instead.
 * @author Thomas Muller
 * @since 1.1
 * 
 */
public class ExtensionSymbol extends org.rodinp.keyboard.core.ExtensionSymbol{

	public ExtensionSymbol(String id, String name, String combo,
			String translation) {
		super(id, name, combo, translation);
	}

}
