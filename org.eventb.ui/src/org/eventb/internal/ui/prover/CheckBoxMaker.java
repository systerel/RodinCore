/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Class able to create checkboxes.
 * 
 * @author "Thomas Muller"
 */
public class CheckBoxMaker extends ControlMaker {

	public CheckBoxMaker(Composite parent) {
		super(parent);
	}

	@Override
	public Control makeControl(ControlHolder holder) {
		return new Button(getParent(), SWT.CHECK);
	}

}
