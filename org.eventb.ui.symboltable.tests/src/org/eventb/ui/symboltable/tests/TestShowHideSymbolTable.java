/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.symboltable.tests;

import static org.eventb.ui.symboltable.ISymbolTableConstants.SYMBOL_TABLE_VIEW_ID;
import static org.junit.Assert.fail;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eventb.ui.symboltable.internal.SymbolViewPart;
import org.junit.Test;

public class TestShowHideSymbolTable {

	/**
	 * Tests if the symbolTableView can be hidden and shown again.
	 */
	@Test
	public void hideShowSymbolTable() {

		final IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(SYMBOL_TABLE_VIEW_ID);
		try {
			if (view == null) {
				view = page.showView(SYMBOL_TABLE_VIEW_ID);
			}
			checkSymbolTableView(view);

			page.hideView(view);

			view = page.findView(SYMBOL_TABLE_VIEW_ID);
			if (view == null) {
				view = page.showView(SYMBOL_TABLE_VIEW_ID);
			}
			checkSymbolTableView(view);
		} catch (PartInitException e) {
			fail("Symbol table view could not be initialized.\n"
					+ e.getMessage());
		}
	}

	private static void checkSymbolTableView(IViewPart view) {
		if (!(view instanceof SymbolViewPart)) {
			fail("Symbol table view could not be properly shown.");
		}
	}
}
