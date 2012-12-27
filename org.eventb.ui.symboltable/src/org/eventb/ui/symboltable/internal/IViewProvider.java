/*******************************************************************************
 * Copyright (c) 2009, 2012 Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Universitaet Duesseldorf - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.symboltable.internal;

import org.eclipse.swt.widgets.Composite;

public interface IViewProvider {

	public void createPartControl(final Composite parent);

	public void setFocus();

	public void dispose();

	public void setEnabled(boolean enabled);
}
